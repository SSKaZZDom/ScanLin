package scanlin.model;

import scanlin.model.parserWin.DataStorageWin;
import scanlin.model.parserWin.StateWin;
import scanlin.model.parserWin.TestWin;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public boolean runTest(TestWin test, DataStorageWin dataStorage) {
        Object object = dataStorage.findObject(test.getObject());
        List<StateWin> states = new ArrayList<>();
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
