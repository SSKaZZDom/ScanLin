package scanlin.model;

import scanlin.model.parserLin.*;

import java.util.HashMap;
import java.util.List;

public class TestRunner {
    private final DataStorageLin storage;

    public TestRunner(DataStorageLin storage) {
        this.storage = storage;
    }

    public void runAllTests() {
        for (TestLin test : storage.getTests()) {
            System.out.println("Проверка теста: " + test.getId());

            ObjectLin object = storage.findObject(test.getObject());
            if (object == null) {
                System.out.println("   ❌ Объект не найден: " + test.getObject());
                continue;
            }

            boolean passed = true;

            for (String stateRef : test.getStates()) {
                StateLin state = storage.findState(stateRef);
                if (state == null) {
                    System.out.println("   ❌ Состояние не найдено: " + stateRef);
                    passed = false;
                    continue;
                }

                if (!checkStateMatchesObject(state, object)) {
                    System.out.println("   ❌ Объект не соответствует состоянию: " + state.getId());
                    passed = false;
                } else {
                    System.out.println("   ✅ Объект соответствует состоянию: " + state.getId());
                }
            }

            if (passed) {
                System.out.println("✅ Тест " + test.getId() + " пройден полностью\n");
            } else {
                System.out.println("❌ Тест " + test.getId() + " не пройден\n");
            }
        }
    }

    private boolean checkStateMatchesObject(StateLin state, ObjectLin object) {
        List<HashMap<String, String>> stateValues = state.getValues();

        for (HashMap<String, String> stateVal : stateValues) {
            String stateTag = stateVal.get("tag");
            String stateExpected = stateVal.get("value");

            HashMap<String, String> objectValue = object.getValue(stateTag);

            if (objectValue == null) {
                System.out.println("      ➤ Тег не найден в объекте: " + stateTag);
                return false;
            }

            String objectActual = objectValue.get("value");

            if (!stateExpected.equals(objectActual)) {
                System.out.println("      ➤ Несовпадение значения: ожидалось '" + stateExpected + "', найдено '" + objectActual + "'");
                return false;
            }
        }

        return true;
    }
}
