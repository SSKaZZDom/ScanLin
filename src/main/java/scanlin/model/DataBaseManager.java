package scanlin.model;

import javafx.application.Platform;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.OvalParserLin;
import scanlin.model.parserLin.VulnerabilityLin;
import scanlin.model.parserWin.DataStorageWin;
import scanlin.model.parserWin.OvalParserWin;
import scanlin.model.parserWin.VulnerabilityWin;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DataBaseManager {
    private DataStorageWin dataStorageWin;
    private DataStorageLin dataStorageLin;
    private boolean isLinux;
    private static AtomicBoolean stopFlag = new AtomicBoolean(false);
    private static Thread downloadThread;
    private static volatile InputStream currentInputStream;
    public DataBaseManager (boolean isLinux) {
        this.isLinux = isLinux;
        disableSslVerification();
    }

    public DataStorageWin getDataStorageWin() {
        return this.dataStorageWin;
    }

    public DataStorageLin getDataStorageLin() {
        return this.dataStorageLin;
    }
    public void updateDB(Consumer<Double> onProgress, Consumer<String> onStatus) throws IOException {
        if (isLinux) {
            downloadThread = new Thread(() -> downloadDB("data/scanovalcontent_alse17.deb","http://bdu.fstec.ru/files/scanovalcontent_alse17.deb", onProgress, onStatus));
            downloadThread.start();

            Path debPath = Paths.get("data/scanovalcontent_alse17.deb");
            Path extractedXml = extractXmlFromDeb(debPath, "var/lib/scanoval/data/AstraSE17VulnsOVAL.xml");

            System.out.println("✅ XML-файл извлечён: " + extractedXml.toAbsolutePath());
            OvalParserLin parser = new OvalParserLin();
            this.dataStorageLin = parser.ovalParsing();
        } else {
            downloadThread = new Thread(() -> downloadDB("data/scanoval.xml", "http://bdu.fstec.ru/files/scanoval.xml", onProgress, onStatus));
            downloadThread.start();
            OvalParserWin parser = new OvalParserWin();
            this.dataStorageWin = parser.ovalParsing();
        }
    }

    public List<String> vulnerabilitySearchWin(String id) {
        List<String> res = new ArrayList<>();
        for (VulnerabilityWin vul : dataStorageWin.getVulnerabilities()) {
            if (vul.getFstec_id().equals(id)) {
                res.add(vul.getFstec_url());
                res.add(vul.getSeverity());
                return res;
            }
        }
        return null;
    }

    public List<String> vulnerabilitySearchLin(String id) {
        List<String> res = new ArrayList<>();
        for (VulnerabilityLin vul : dataStorageLin.getVulnerabilities()) {
            if (vul.getFstec_id().equals(id)) {
                res.add(vul.getFstec_url());
                res.add(vul.getSeverity());
                return res;
            }
        }
        return null;
    }
    public void stopDownload(){
        stopFlag.set(true);
        if (downloadThread != null) {
            downloadThread.interrupt(); // Попробует прервать, если блокировка на I/O
        }
        if (currentInputStream != null) {
            try {
                currentInputStream.close(); // Закроет поток и read() завершится с IOException
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void downloadDB(String name, String URL, Consumer<Double> onProgress, Consumer<String> onStatus) {
        stopFlag.set(false);
        File file = new File(name);
        File backupFile = new File(name + ".bak");
        File tempFile = new File(name + ".tmp");

        try {
            // Создание резервной копии
            try {
                copyWithRetries(file.toPath(), backupFile.toPath());
                safeDelete(file.toPath());

                System.out.println("Файл успешно переименован в " + backupFile.getName());
            } catch (IOException e) {
                System.err.println("Ошибка при переименовании в .bak: " + e.getMessage());
                Platform.runLater(() -> onStatus.accept("Ошибка при создании резервной копии: " + e.getMessage()));
            }

            // Скачивание файла
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String newUrl = connection.getHeaderField("Location");
                    connection = (HttpURLConnection) new URL(newUrl).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.connect();
                }

                long totalSize = connection.getContentLengthLong();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK && totalSize > 0) {
                    try (InputStream in = connection.getInputStream();
                         FileOutputStream out = new FileOutputStream(tempFile)) {

                        currentInputStream = in;
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long downloaded = 0;

                        while ((bytesRead = in.read(buffer)) != -1) {
                            if (stopFlag.get()) {
                                System.out.println("Загрузка прервана пользователем");
                                Platform.runLater(() -> onStatus.accept("Загрузка отменена"));

                                try {
                                    in.close();
                                    out.close();
                                } catch (IOException ex) {
                                    System.err.println("Ошибка при закрытии потоков после отмены: " + ex.getMessage());
                                }

                                safeDelete(tempFile.toPath());

                                // Восстановление старой версии
                                if (backupFile.exists()) {
                                    try {
                                        copyWithRetries(backupFile.toPath(), file.toPath());
                                        System.out.println("Старая версия восстановлена.");
                                    } catch (IOException ex) {
                                        System.err.println("Ошибка при восстановлении старой версии: " + ex.getMessage());
                                        Platform.runLater(() -> onStatus.accept("Ошибка при восстановлении резервной копии: " + ex.getMessage()));
                                    }
                                }
                                return;
                            }

                            out.write(buffer, 0, bytesRead);
                            downloaded += bytesRead;

                            double progress = (double) downloaded / totalSize;
                            Platform.runLater(() -> {
                                onProgress.accept(progress);
                                onStatus.accept("Загрузка базы данных: " + (int) (progress * 100) + "%");
                            });
                        }

                        // Замена старого файла новым
                        try {
                            copyWithRetries(tempFile.toPath(), file.toPath());
                            safeDelete(tempFile.toPath());
                            System.out.println("Файл " + file.getName() + " успешно заменен.");
                        } catch (IOException ex) {
                            System.err.println("Ошибка при замене файла: " + ex.getMessage());
                            Platform.runLater(() -> onStatus.accept("Ошибка при замене файла: " + ex.getMessage()));
                        }

                        // Удаление резервной копии
                        if (backupFile.exists()) {
                            safeDelete(backupFile.toPath());
                            System.out.println("Резервная копия " + backupFile.getName() + " удалена.");
                        }

                        Platform.runLater(() -> onStatus.accept("Завершено!"));

                    }
                } else {
                    System.err.println("Ошибка: сервер вернул код " + connection.getResponseCode());
                    Platform.runLater(() -> onStatus.accept("Ошибка при скачивании"));
                }

            } catch (IOException e) {
                System.err.println("Ошибка при скачивании файла: " + e.getMessage());
                Platform.runLater(() -> onStatus.accept("Ошибка при скачивании: " + e.getMessage()));
            }
        } finally {
            if (currentInputStream != null) {
                try {
                    currentInputStream.close();
                } catch (IOException e) {
                    System.err.println("Ошибка при закрытии потока: " + e.getMessage());
                }
            }
        }
    }

    private static boolean safeDelete(Path path) {
        try {
            if (!Files.exists(path)) return true;

            // Переименовываем файл
            Path renamed = path.resolveSibling(path.getFileName() + ".to_delete");
            try {
                Files.move(path, renamed, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException moveEx) {
                System.err.println("Не удалось переименовать перед удалением: " + moveEx.getMessage());
                return false;
            }

            // Пробуем удалить переименованный файл
            for (int i = 0; i < 5; i++) {
                try {
                    Files.deleteIfExists(renamed);
                    return true;
                } catch (IOException ex) {
                    System.err.println("Попытка " + (i + 1) + " удалить " + renamed.getFileName() + " не удалась: " + ex.getMessage());
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {}
                }
            }

            System.err.println("Не удалось удалить файл: " + renamed.getFileName());
            return false;

        } catch (Exception e) {
            System.err.println("Ошибка при безопасном удалении " + path.getFileName() + ": " + e.getMessage());
            return false;
        }
    }

    private static void copyWithRetries(Path source, Path target) throws IOException {
        int attempts = 5;
        for (int i = 0; i < attempts; i++) {
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                return;
            } catch (IOException ex) {
                System.err.println("Попытка " + (i + 1) + " не удалась: " + ex.getMessage());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private static Path extractXmlFromDeb(Path debFile, String targetFilePathInTar) throws IOException {
        // Шаг 1: Найдём data.tar.xz внутри .deb (ar-архива)
        try (InputStream debIn = Files.newInputStream(debFile);
             ArArchiveInputStream arIn = new ArArchiveInputStream(debIn)) {

            ArArchiveEntry entry;
            while ((entry = arIn.getNextArEntry()) != null) {
                if (entry.getName().startsWith("data.tar.xz")) {
                    // Читаем содержимое data.tar.xz
                    Path tempDataTarXz = Files.createTempFile("data", ".tar.xz");

                    if (Files.getFileStore(tempDataTarXz).supportsFileAttributeView("posix")) {
                        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
                        Files.setPosixFilePermissions(tempDataTarXz, perms);
                    }

                    try (OutputStream out = Files.newOutputStream(tempDataTarXz)) {
                        arIn.transferTo(out);
                    }

                    // Шаг 2: Распакуем XML-файл из data.tar.xz
                    return extractFileFromTarXz(tempDataTarXz, targetFilePathInTar);
                }
            }
        }
        throw new FileNotFoundException("data.tar.xz не найден в .deb");
    }

    public static Path extractFileFromTarXz(Path tarXzPath, String targetPathInTar) throws IOException {
        try (InputStream in = Files.newInputStream(tarXzPath);
             XZCompressorInputStream xzIn = new XZCompressorInputStream(in);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(xzIn)) {

            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.getName().equals(targetPathInTar)) {
                    Path outputPath = Paths.get("data/AstraSE17VulnsOVAL.xml");
                    try (OutputStream out = Files.newOutputStream(outputPath)) {
                        tarIn.transferTo(out);
                    }
                    return outputPath;
                }
            }
        }
        throw new FileNotFoundException(targetPathInTar + " не найден в data.tar.xz");
    }

    private static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
