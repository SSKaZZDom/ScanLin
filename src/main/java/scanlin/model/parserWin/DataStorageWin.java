package scanlin.model.parserWin;

import scanlin.model.Storage;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataStorageWin {
    List<VulnerabilityWin> vulnerabilities;
    List<InventoryWin> inventories;
    List<TestWin> tests;
    List<ObjectWin> objects;
    List<StateWin> states;
    List<VariableWin> variables;

    public DataStorageWin(List<VulnerabilityWin> listA, List<InventoryWin> listB, List<TestWin> listC,
                          List<ObjectWin> listD, List<StateWin> listE, List<VariableWin> listF) {
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
    public List<VulnerabilityWin> getVulnerabilities() {
        return vulnerabilities;
    }

    public List<InventoryWin> getInventories() {
        return inventories;
    }

    public List<TestWin> getTests() {
        return tests;
    }

    public List<ObjectWin> getObjects() {
        return objects;
    }

    public List<StateWin> getStates() {
        return states;
    }

    public List<VariableWin> getVariables() {
        return variables;
    }

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

    public VulnerabilityWin findVulnerability(String id) {
        return binarySearch(vulnerabilities, id);
    }

    public InventoryWin findInventory(String id) {
        return binarySearch(inventories, id);
    }

    public TestWin findTest(String id) {
        return binarySearch(tests, id);
    }

    public ObjectWin findObject(String id) {
        return binarySearch(objects, id);
    }

    public StateWin findState(String id) {
        return binarySearch(states, id);
    }

    public VariableWin findVariable(String id) {
        return binarySearch(variables, id);
    }
}
