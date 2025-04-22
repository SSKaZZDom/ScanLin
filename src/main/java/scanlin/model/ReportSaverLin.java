package scanlin.model;

import scanlin.model.parserLin.VulnerabilityLin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportSaverLin {
    public static void exportToCSV(List<VulnerabilityLin> vulnerabilities, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("ID,Title,Description,Severity,Product,CWE,CVSS 2.0,BDU URL,Remediation\n");

            for (VulnerabilityLin v : vulnerabilities) {
                writer.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        v.getFstec_id(),
                        v.getTitle(),
                        v.getDescription(),
                        v.getSeverity(),
                        v.getProduct(),
                        v.getCWE(),
                        v.getCvss2(),
                        v.getFstec_url(),
                        v.getRemediation()));
            }

            System.out.println("Файл успешно создан: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
