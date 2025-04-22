package scanlin.model;

import scanlin.model.parserLin.CriteriaLin;
import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.InventoryLin;
import scanlin.model.parserLin.VulnerabilityLin;

import java.util.ArrayList;
import java.util.List;

public class CriteriaRunnerLin {
    public CriteriaRunnerLin() {

    }

    public List<VulnerabilityLin> VulnerabilityCheck(DataStorageLin storage) {
        List<VulnerabilityLin> result = new ArrayList<>();
        List<VulnerabilityLin> vuls = storage.getVulnerabilities();
        TestRunnerLin runner = new TestRunnerLin(storage);
        int cnt = 0;
        int size = vuls.size();
        int percents = 0;
        long startTime = System.currentTimeMillis();
        for (VulnerabilityLin vul : vuls) {
            if (CriteriaCheck(vul.getCriteria(), storage, runner)) {
                result.add(vul);
            }
            cnt++;
            if ((cnt * 100) / size > percents) {
                percents = (cnt * 100) / size;
                System.out.println(percents + "% vuls check");
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;

        System.out.println("Время проверки уязвимостей: " + minutes + " мин " + seconds + " сек");

        return result;
    }

    private boolean CriteriaCheck (CriteriaLin criteriaLin, DataStorageLin storage, TestRunnerLin runner) {
        if (criteriaLin.getOperator() != null) {
            if (criteriaLin.getOperator().equals("OR")) {
                for (String test : criteriaLin.getTests()) {
                    if (runner.getTrueTests().contains(test)) {
                        return true;
                    }
                }
                for (CriteriaLin subCriteria : criteriaLin.getCriteria()) {
                    if (CriteriaCheck(subCriteria, storage, runner)) {
                        return true;
                    }
                }
                for (String inventory : criteriaLin.getDefinitions()) {
                    if (CriteriaCheck(storage.findInventory(inventory).getCriteria(), storage, runner)) {
                        return true;
                    }
                }
            }
        } else {
            for (String test : criteriaLin.getTests()) {
                if (!runner.checkTest(storage.findTest(test), storage)) {
                    return false;
                }
            }
            for (CriteriaLin subCriteria : criteriaLin.getCriteria()) {
                if (!CriteriaCheck(subCriteria, storage, runner)) {
                    return false;
                }
            }
            for (String inventory : criteriaLin.getDefinitions()) {
                if (!CriteriaCheck(storage.findInventory(inventory).getCriteria(), storage, runner)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
