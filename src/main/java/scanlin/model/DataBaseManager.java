package scanlin.model;

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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataBaseManager {
    private DataStorageWin dataStorageWin;
    private DataStorageLin dataStorageLin;
    public DataBaseManager (boolean isLinux) {
        File dbLin = new File("data/AstraSE17VulnsOVAL.xml");
        File dbWin = new File("data/scanoval.xml");
        if (isLinux && !dbLin.exists() || !isLinux && !dbWin.exists()) {
            try {
                updateDB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (isLinux) {
            OvalParserLin parser = new OvalParserLin();
            this.dataStorageLin = parser.ovalParsing();
        } else {
            OvalParserWin parser = new OvalParserWin();
            this.dataStorageWin = parser.ovalParsing();
        }
    }
    public DataStorageWin getDataStorageWin() {
        return this.dataStorageWin;
    }

    public DataStorageLin getDataStorageLin() {
        return this.dataStorageLin;
    }
    public static void updateDB() throws IOException {
        OSAnalyzer osAnalyzer = new OSAnalyzer();
        if (osAnalyzer.isLinux()) {
            downloadDB("data/scanovalcontent_alse17.deb","https://bdu.fstec.ru/files/scanovalcontent_alse17.deb");

            Path debPath = Paths.get("data/scanovalcontent_alse17.deb");
            Path extractedXml = extractXmlFromDeb(debPath, "var/lib/scanoval/data/AstraSE17VulnsOVAL.xml");

            System.out.println("✅ XML-файл извлечён: " + extractedXml.toAbsolutePath());
        } else {
            downloadDB("data/scanoval.xml", "http://bdu.fstec.ru/files/scanoval.xml");
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

    private static void downloadDB(String name, String URL) {
        File file = new File(name);
        if (file.exists()) {
            file.delete();
            System.out.println("Старая версия файла " + name + " удалена.");
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setInstanceFollowRedirects(true);  // Позволяет следовать редиректам
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");  // Имитируем браузер
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String newUrl = connection.getHeaderField("Location"); // Получаем новый URL
                connection = (HttpURLConnection) new URL(newUrl).openConnection(); // Делаем новый запрос
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(name)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Файл успешно скачан в " + name);
                }
            } else {
                System.out.println("Ошибка: сервер вернул код " + connection.getResponseCode());
            }

        } catch (IOException e) {
            System.out.println("Ошибка при скачивании файла: " + e.getMessage());
        }
    }

    public static Path extractXmlFromDeb(Path debFile, String targetFilePathInTar) throws IOException {
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
}
