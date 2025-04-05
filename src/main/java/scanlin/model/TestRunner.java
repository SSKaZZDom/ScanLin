package scanlin.model;

import scanlin.model.parser.DataStorage;
import scanlin.model.parser.State;
import scanlin.model.parser.Test;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public boolean runTest(Test test, DataStorage dataStorage) {
        Object object = dataStorage.findObject(test.getObject());
        List<State> states = new ArrayList<>();
        for (String stateId : test.getStates()) {
            states.add(dataStorage.findState(stateId));
        }
        if (states == null) {

        }
        if (test.getType().equals("file")) {

        }
        return true;
    }
}
