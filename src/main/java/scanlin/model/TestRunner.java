package scanlin.model;

import org.apache.commons.lang3.tuple.Pair;
import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.ObjectLin;
import scanlin.model.parserLin.StateLin;
import scanlin.model.parserLin.TestLin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRunner {

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

        return checkStateAgainstObject(state, object, storage);
    }

    private boolean checkStateAgainstObject(StateLin state, ObjectLin object, DataStorageLin storage) {
        if (object.getType().equals("dpkginfo")) {
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
        }
        return false;
    }

    private boolean compareStrings(String actual, String expected, String operation) {
        if (operation == null) {
            return actual.equals(expected);
        }

        switch (operation) {
            case "equals":
                return actual.equals(expected);
            case "not equal":
                return !actual.equals(expected);
            case "pattern match":
                return RegexMatcher.matches(actual, expected);
            default:
                System.out.println("Unknown string operation: " + operation);
                return false;
        }
    }

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
}
