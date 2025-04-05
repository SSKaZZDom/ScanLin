package scanlin.model;

import scanlin.model.parser.DataStorage;
import scanlin.model.parser.OvalParser;
import scanlin.model.parser.Vulnerability;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private DataStorage dataStorage;
    public DataStorage getDataStorage() {
        OvalParser ovalParser = new OvalParser();
        this.dataStorage = ovalParser.ovalParsing();
        return this.dataStorage;
    }
    public static void updateDB() {
        File file = new File("data/scanoval.xml");
        if (file.exists()) {
            file.delete();
            System.out.println("Старая версия файла удалена.");
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://bdu.fstec.ru/files/scanoval.xml").openConnection();
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
                     FileOutputStream out = new FileOutputStream("data/scanoval.xml")) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Файл успешно скачан в " + "data/scanoval.xml");
                }
            } else {
                System.out.println("Ошибка: сервер вернул код " + connection.getResponseCode());
            }

        } catch (IOException e) {
            System.out.println("Ошибка при скачивании файла: " + e.getMessage());
        }
    }

    public List<String> vulnerabilitySearch(String id) {
        List<String> res = new ArrayList<>();
        for (Vulnerability vul : dataStorage.getVulnerabilities()) {
            if (vul.getFstec_id().equals(id)) {
                res.add(vul.getFstec_url());
                res.add(vul.getSeverity());
                return res;
            }
        }
        return null;
    }
}
