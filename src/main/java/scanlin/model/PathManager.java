package scanlin.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
public class PathManager {
    private final Path filePath;

    public PathManager() {
        this.filePath = Paths.get(System.getProperty("user.dir"), "data", "path.txt");

        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile() {
        try {
            if (!Files.exists(filePath)) {
                return null;
            }
            return Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeToFile(String content) {
        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
