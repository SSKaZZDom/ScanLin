package scanlin.model.parser;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataStorage {
    List<Vulnerability> vulnerabilities;
    List<Inventory> inventories;
    List<Test> tests;
    List<SystemObject> objects;
    List<State> states;
    List<Variable> variables;

    public DataStorage(List<Vulnerability> listA, List<Inventory> listB, List<Test> listC,
                       List<SystemObject> listD, List<State> listE, List<Variable> listF) {
        this.vulnerabilities = listA;
        this.inventories = listB;
        this.tests = listC;
        this.objects = listD;
        this.states = listE;
        this.variables = listF;
        listSort(this.vulnerabilities);
        listSort(this.inventories);
        listSort(this.tests);
        listSort(this.objects);
        listSort(this.states);
        listSort(this.variables);
    }

    // Геттеры, если нужно
    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public List<Test> getTests() {
        return tests;
    }

    public List<SystemObject> getObjects() {
        return objects;
    }

    public List<State> getStates() {
        return states;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    private void listSort (List<? extends  Storage> list) {
        list.sort(Comparator.comparingInt(s -> extractNumber(s.getId())));
    }

    private static int extractNumber(String id) {
        Pattern pattern = Pattern.compile("\\d+$"); // Ищем число в конце строки
        Matcher matcher = pattern.matcher(id);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return Integer.MAX_VALUE; // Если вдруг числа нет (неожиданное поведение)
    }

    private <T extends Storage> T binarySearch(List<T> list, String targetId) {
        int targetNumber = extractNumber(targetId);
        int left = 0, right = list.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midNumber = extractNumber(list.get(mid).getId());
            if (midNumber == targetNumber) {
                return list.get(mid);
            } else if (midNumber < targetNumber) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    public Vulnerability findVulnerability(String id) {
        return binarySearch(vulnerabilities, id);
    }

    public Inventory findInventory(String id) {
        return binarySearch(inventories, id);
    }

    public Test findTest(String id) {
        return binarySearch(tests, id);
    }

    public SystemObject findObject(String id) {
        return binarySearch(objects, id);
    }

    public State findState(String id) {
        return binarySearch(states, id);
    }

    public Variable findVariable(String id) {
        return binarySearch(variables, id);
    }
}
