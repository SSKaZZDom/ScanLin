package scanlin.model;

import scanlin.model.parserLin.VulnerabilityLin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReportSaverLin {
    public static void exportToCSV(List<VulnerabilityLin> vulnerabilities, String ip) {
        String path = getOutputDirectoryPath();
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try (FileWriter writer = new FileWriter(path + ip)) {
            writer.append("ID,Title,Description,Severity,Product,CVSS 2.0,BDU URL,Remediation\n");

            for (VulnerabilityLin v : vulnerabilities) {
                writer.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        v.getFstec_id(),
                        v.getTitle(),
                        v.getDescription(),
                        v.getSeverity(),
                        v.getProduct(),
                        v.getCvss2(),
                        v.getFstec_url(),
                        v.getRemediation()));
            }

            System.out.println("Файл успешно создан: " + getOutputDirectoryPath() + ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getOutputDirectoryPath() {
        String pathFile = "data/path.txt";
        try {
            String result = Files.readString(Paths.get(pathFile)).trim(); // убираем возможные пробелы/перенос строки
            if (result.startsWith("~")) {
                String home = System.getProperty("user.home");
                return home + result.substring(1);
            }
            return result;
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла пути: " + e.getMessage());
            return null;
        }
    }
}
