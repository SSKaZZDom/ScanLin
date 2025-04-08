package scanlin.model;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class OSAnalyzer {
    private String currentOSName = "Unknown";
    private boolean isLinux = false;
    private boolean isWindows = false;

    public OSAnalyzer() {
        detectOS();
    }

    // Метод возвращает имя ОС
    public String getOSName() {
        return currentOSName;
    }

    // Возвращает true, если ОС семейства Linux
    public boolean isLinux() {
        return isLinux;
    }

    // Возвращает true, если Windows
    public boolean isWindows() {
        return isWindows;
    }

    // Детектим ОС
    private void detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        currentOSName = osName;

        if (osName.contains("win")) {
            isWindows = true;
            currentOSName = "Windows";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            isLinux = true;
            currentOSName = getLinuxDistroName();
        }
    }

    // Только если Linux — пробуем уточнить дистрибутив
    private String getLinuxDistroName() {
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "lsb_release -d -s");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            process.waitFor();
            return output.toString().trim();
        } catch (IOException | InterruptedException e) {
            return "Linux (unknown distro)";
        }
    }

    // Отладочная печать
    public void printOSInfo() {
        System.out.println("Detected OS: " + currentOSName);
        System.out.println("Is Linux? " + isLinux);
        System.out.println("Is Windows? " + isWindows);
    }
}
