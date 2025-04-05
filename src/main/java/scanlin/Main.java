package scanlin;

import scanlin.model.Model;
import scanlin.model.ModelInterface;
import scanlin.model.parser.DataStorage;
import scanlin.model.parser.Vulnerability;

import javax.xml.crypto.Data;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ModelInterface model = new Model();

        // Код, проверяющий список программ на компьютере

       /*
        List<Map<String, String>> programs = model.getProgramList();

        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File outputFile = new File("data/programs.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map<String, String> program : programs) {
                writer.write("Name: " + program.get("name") + ", Version: " + program.get("version"));
                writer.newLine();
            }
            System.out.println("Program list saved to data/programs.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // Код, обновляющий базу данных
        //model.updateDataBase();

        // Код, ищущий уровень опасности и url уязвимости
        /*List<String> vul = model.getVulnerabilityURL("BDU:2019-01206");
        System.out.println(vul.get(0) + " " + vul.get(1));
        */

        // Код, который парсит бд в список уязвимостей
        DataStorage dataStorage = model.getDataStorage();
    }
}