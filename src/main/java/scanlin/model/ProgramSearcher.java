package scanlin.model;

import java.io.*;
import java.util.*;
public class ProgramSearcher {
    public List<Map<String, String>> getProgramList() {
        List<Map<String, String>> programs = new ArrayList<>();
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File fullOutputFile = new File("data/FullOutput.txt");
        try (
                BufferedWriter fullOutputWriter = new BufferedWriter(new FileWriter(fullOutputFile))
        ) {
            List<String> command = Arrays.asList(
                    "reg", "query",
                    "HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
                    "/s"
            );
            ProcessBuilder builder = new ProcessBuilder(command);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            String name = null, version = null;
            int flag = 1;
            while ((line = reader.readLine()) != null) {
                fullOutputWriter.write(line);
                fullOutputWriter.newLine();
                if (line.contains("HKEY_LOCAL_MACHINE")) {
                    if (flag == 1) {
                        flag = 0;
                    } else {
                        System.out.println(name + "  " + version);
                        name = null;
                        version = null;
                    }
                }
                if (line.contains("DisplayName")) {
                    name = line.split("    ")[line.split("    ").length - 1];
                } else if (line.contains("DisplayVersion")) {
                    version = line.split("    ")[line.split("    ").length - 1];
                }
                if (name != null && version != null) {
                    Map<String, String> program = new HashMap<>();
                    program.put("name", name);
                    program.put("version", version);
                    programs.add(program);
                    name = null;
                    version = null;
                    flag = 1;
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return programs;
    }
}
