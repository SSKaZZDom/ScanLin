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
        for (VulnerabilityLin vul : vuls) {
            if (CriteriaCheck(vul.getCriteria(), storage)) {
                result.add(vul);
            }
        }
        return result;
    }

    private boolean CriteriaCheck (CriteriaLin criteriaLin, DataStorageLin storage) {
        boolean result = false;
        TestRunnerLin runner = new TestRunnerLin();
        if (criteriaLin.getOperator().equals("OR")) {
            for (String test : criteriaLin.getTests()) {
                if (runner.checkTest(storage.findTest(test), storage)) {
                    result = true;
                }
            }
            for (CriteriaLin subCriteria : criteriaLin.getCriteria()) {
                if (CriteriaCheck(subCriteria, storage)) {
                    result = true;
                }
            }
            for (String inventory : criteriaLin.getDefinitions()) {
                if (CriteriaCheck(storage.findInventory(inventory).getCriteria(), storage)) {
                    result = true;
                }
            }
        } else {
            result = true;
            for (String test : criteriaLin.getTests()) {
                if (!runner.checkTest(storage.findTest(test), storage)) {
                    result = false;
                }
            }
            for (CriteriaLin subCriteria : criteriaLin.getCriteria()) {
                if (!CriteriaCheck(subCriteria, storage)) {
                    result = false;
                }
            }
            for (String inventory : criteriaLin.getDefinitions()) {
                if (!CriteriaCheck(storage.findInventory(inventory).getCriteria(), storage)) {
                    result = false;
                }
            }
        }
        return result;
    }
}
