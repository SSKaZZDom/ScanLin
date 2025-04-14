package scanlin.model.parserLin;

import scanlin.model.Storage;
import scanlin.model.parserLin.*;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataStorageLin {
    List<VulnerabilityLin> vulnerabilities;
    List<InventoryLin> inventories;
    List<TestLin> tests;
    List<ObjectLin> objects;
    List<StateLin> states;
    List<VariableLin> variables;

    public DataStorageLin(List<VulnerabilityLin> listA, List<InventoryLin> listB, List<TestLin> listC,
                          List<ObjectLin> listD, List<StateLin> listE, List<VariableLin> listF) {
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
    public List<VulnerabilityLin> getVulnerabilities() {
        return vulnerabilities;
    }

    public List<InventoryLin> getInventories() {
        return inventories;
    }

    public List<TestLin> getTests() {
        return tests;
    }

    public List<ObjectLin> getObjects() {
        return objects;
    }

    public List<StateLin> getStates() {
        return states;
    }
    public List<VariableLin> getVariables() { return variables; }

    private void listSort (List<? extends Storage> list) {
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

    public VulnerabilityLin findVulnerability(String id) {
        return binarySearch(vulnerabilities, id);
    }

    public InventoryLin findInventory(String id) {
        return binarySearch(inventories, id);
    }

    public TestLin findTest(String id) {
        return binarySearch(tests, id);
    }

    public ObjectLin findObject(String id) {
        return binarySearch(objects, id);
    }

    public StateLin findState(String id) {
        return binarySearch(states, id);
    }

    public VariableLin findVariable(String id) {
        return binarySearch(variables, id);
    }
}
