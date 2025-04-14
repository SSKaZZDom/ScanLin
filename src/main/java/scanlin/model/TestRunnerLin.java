package scanlin.model;

import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.ObjectLin;
import scanlin.model.parserLin.StateLin;
import scanlin.model.parserLin.TestLin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;


public class TestRunnerLin {

    private final DebianVersionComparator debianComparator = new DebianVersionComparator();

    public boolean checkTest(TestLin test, DataStorageLin storage) {
        ObjectLin object = new ObjectLin();
        StateLin state = new StateLin();
        if (test.getObject() != null) {
            object = storage.findObject(test.getObject());
        }
        if (test.getState() != null) {
            state = storage.findState(test.getState());
        }

        if (test.getType().equals("dpkginfo")) {
            HashMap<String, String> stateValue = state.getValue();
            String expectedValue = stateValue.get("value");
            String operation = stateValue.get("operation");

            List<HashMap<String, String>> objectValues = object.getValues();
            if (objectValues == null) {
                return false;
            }

            List<String> actualValues = new ArrayList<>();
            if (objectValues.get(0).containsKey("operation")) {
                if (objectValues.get(0).get("operation").equals("pattern match")) {
                    String patternName = objectValues.get(0).get("value");
                    List<String> packageNames = getMatchingInstalledPackages(patternName);
                    if (objectValues.size() == 2) {
                        actualValues = filterVersionsByRegex(packageNames, storage.findState(objectValues.get(1).get("value")).getValue().get("value"));
                    } else {
                        for (String packageName : packageNames) {
                            actualValues.add(getInstalledVersion(packageName));
                        }
                    }
                } else {
                    String packageName = objectValues.get(0).get("value");
                    actualValues.add(getInstalledVersion(packageName));
                }
            }

            for (String actualValue : actualValues) {
                if (compareDebianVersion(actualValue, expectedValue, operation)) {
                    return true;
                }
            }
        } else if (test.getType().equals("rpminfo")) {
            List<HashMap<String, String>> objectValues = object.getValues();
            if (objectValues == null) {
                return false;
            }

            if (objectValues.get(0).containsKey("operation")) {
                if (objectValues.get(0).get("operation").equals("pattern match")) {
                    String patternName = objectValues.get(0).get("value");
                    if (objectValues.size() == 2) {
                        String filter = storage.findState(objectValues.get(1).get("value")).getValue().get("value");
                        return isRpmPackageInstalledRegex(patternName, filter);
                    } else {
                        return isRpmPackageInstalledRegex(patternName);
                    }
                } else {
                    String packageName = objectValues.get(0).get("value");
                    return isRpmPackageInstalled(packageName);
                }
            }
        } else if (test.getType().equals("textfilecontent54")) {
            List<HashMap<String, String>> objectValues = object.getValues();
            List<String> files = new ArrayList<>();

            if (objectValues.get(1).containsKey("operation")){
                if (objectValues.get(1).get("operation").equals("pattern match")) {
                    files = getMatchingFiles(objectValues.get(0).get("value"), objectValues.get(1).get("value"));
                }
            } else {
                files.add(objectValues.get(0).get("value") + '\\' + objectValues.get(1).get("value"));
                System.out.println(files.get(0));
            }

            List<String> lines = new ArrayList<>();
            String patternLine;
            for (String file : files) {
                patternLine = findMatchingLine(file, objectValues.get(2).get("value"));
                if (patternLine != null) {
                    lines.add(patternLine);
                }
            }

            if (test.getState() != null) {
                lines = filterStringsByRegex(lines, state.getValue().get("value"));
            }
            
            if (!lines.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //Следующие 4 метода написаны для проверки dpkginfo_test

    private boolean compareDebianVersion(String actual, String expected, String operation) {
        int cmp = debianComparator.compare(actual, expected);

        switch (operation) {
            case "equals":
                return cmp == 0;
            case "not equal":
                return cmp != 0;
            case "greater than":
                return cmp > 0;
            case "greater than or equal":
                return cmp >= 0;
            case "less than":
                return cmp < 0;
            case "less than or equal":
                return cmp <= 0;
            case "pattern match":
                return RegexMatcher.matches(actual, expected);
            default:
                System.out.println("Unknown debian version operation: " + operation);
                return false;
        }
    }

    private static String getInstalledVersion(String packageName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("dpkg-query", "-W", "-f=${Version}", packageName);
            Process process = processBuilder.start();
            process.waitFor();

            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next().trim() : null;

            if (result == null || result.isEmpty()) {
                return null;
            }

            return result;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<String> getMatchingInstalledPackages(String regex) {
        List<String> matchingPackages = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        try {
            Process process = Runtime.getRuntime().exec("dpkg-query -W -f='${Package}\n'");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll("^'|'$", ""); // Убираем лишние кавычки, если есть
                if (pattern.matcher(line).matches()) {
                    matchingPackages.add(line);
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchingPackages;
    }

    private static List<String> filterVersionsByRegex(List<String> packageNames, String regex) {
        List<String> matchedVersions = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);

        for (String packageName : packageNames) {
            if (pattern.matcher(packageName).matches()) {
                String version = getInstalledVersion(packageName);
                if (version != null && !version.isEmpty()) {
                    matchedVersions.add(version);
                }
            }
        }

        return matchedVersions;
    }

    // Следующие 3 метода написаны для проверки rpminfo_test

    private static boolean isRpmPackageInstalled(String packageName) {
        try {
            Process process = new ProcessBuilder("rpm", "-q", packageName).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isRpmPackageInstalledRegex(String regex) {
        try {
            Process process = new ProcessBuilder("rpm", "-qa").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            Pattern pattern = Pattern.compile(regex);
            String line;
            while ((line = reader.readLine()) != null) {
                if (pattern.matcher(line).matches()) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static boolean isRpmPackageInstalledRegex(String regex, String filter) {
        Pattern namePattern = Pattern.compile(regex);
        Pattern versionPattern = Pattern.compile(filter);

        try {
            Process process = new ProcessBuilder("rpm", "-qa", "--qf", "%{NAME} %{VERSION}-%{RELEASE}\n").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(" ", 2);
                if (parts.length != 2) continue;

                String packageName = parts[0];
                String fullVersion = parts[1]; // already in form version-release

                if (namePattern.matcher(packageName).matches() &&
                        versionPattern.matcher(fullVersion).matches()) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Оставшиеся методы написаны для проверки тестов типа textfilecontent54

    private static List<String> getMatchingFiles(String directoryPath, String regex) {
        List<String> matchedFiles = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        File dir = new File(directoryPath);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                        matchedFiles.add(directoryPath + file.getName());
                        System.out.println(directoryPath + file.getName());
                    }
                }
            }
        }

        return matchedFiles;
    }

    private static String findMatchingLine(String filePath, String regex) {
        Pattern pattern = Pattern.compile(regex);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    return line;
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        return null;
    }

    private static List<String> filterStringsByRegex(List<String> inputStrings, String regex) {
        List<String> matchedStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);

        for (String str : inputStrings) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                matchedStrings.add(str);
            }
        }

        return matchedStrings;
    }
}
