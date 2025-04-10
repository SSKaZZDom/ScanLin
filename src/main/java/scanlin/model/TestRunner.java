package scanlin.model;

import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.ObjectLin;
import scanlin.model.parserLin.StateLin;
import scanlin.model.parserLin.TestLin;

import java.util.HashMap;
import java.util.List;

public class TestRunner {

    private final DebianVersionComparator debianComparator = new DebianVersionComparator();

    public boolean checkTest(TestLin test, DataStorageLin storage) {
        ObjectLin object = storage.findObject(test.getObject());
        List<String> stateRefs = test.getStates();

        for (String stateRef : stateRefs) {
            StateLin state = storage.findState(stateRef);
            if (checkStateAgainstObject(state, object)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkStateAgainstObject(StateLin state, ObjectLin object) {
        for (HashMap<String, String> stateValue : state.getValues()) {
            String tag = stateValue.get("tag");
            String expectedValue = stateValue.get("value");
            String operation = stateValue.get("operation");
            String datatype = stateValue.get("datatype");

            HashMap<String, String> objectValue = object.getValue(tag);
            if (objectValue == null) {
                return false;
            }

            String actualValue = objectValue.get("name");

            if (datatype.equals("debian_evr_string")) {
                if (!compareDebianVersion(actualValue, expectedValue, operation)) {
                    return false;
                }
            } else {
                if (!compareStrings(actualValue, expectedValue, operation)) {
                    return false;
                }
            }
        }

        return true;
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
}
