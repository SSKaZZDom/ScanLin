package scanlin.model;

import javafx.application.Platform;
import scanlin.model.parserLin.CriteriaLin;
import scanlin.model.parserLin.DataStorageLin;
import scanlin.model.parserLin.InventoryLin;
import scanlin.model.parserLin.VulnerabilityLin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CriteriaRunnerLin {
    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private final Object pauseLock = new Object();
    private List<VulnerabilityLin> trueVuls;
    public CriteriaRunnerLin() {

    }

    public void VulnerabilityCheck(
            DataStorageLin storage,
            Consumer<Double> onProgress,
            Consumer<String> onStatus
    ) {
        new Thread(() -> {
            List<VulnerabilityLin> result = new ArrayList<>();
            List<VulnerabilityLin> vuls = storage.getVulnerabilities();
            TestRunnerLin runner = new TestRunnerLin(storage);
            runner.checkAllTests(progress -> notifyProgress(onProgress, onStatus, progress));

            int cnt = 0;
            int size = vuls.size();

            for (VulnerabilityLin vul : vuls) {
                if (stopped) break;

                synchronized (pauseLock) {
                    while (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            notifyStatus(onStatus, "Операция прервана");
                            return;
                        }
                    }
                }

                if (CriteriaCheck(vul.getCriteria(), storage, runner)) {
                    result.add(vul);
                }

                cnt++;
                double progress = 0.7 + 0.3 * cnt / size;
                notifyProgress(onProgress, onStatus, progress);
            }
            this.trueVuls = result;
            if (!stopped) {
                notifyStatus(onStatus, "Завершено!");
            }
        }).start();
    }

    private void notifyProgress(Consumer<Double> onProgress, Consumer<String> onStatus, double progress) {
        Platform.runLater(() -> {
            onProgress.accept(progress);
            if (progress < 0.7) {
                onStatus.accept("Проверка тестов: " + (int)(progress * 100) + "%");
            } else {
                onStatus.accept("Проверка уязвимостей: " + (int)(progress * 100) + "%");
            }
        });
    }


    private void notifyStatus(Consumer<String> onStatus, String message) {
        Platform.runLater(() -> onStatus.accept(message));
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void stop() {
        stopped = true;
        resume(); // чтобы не застрял в паузе
    }

    public boolean isPaused() {
        return paused;
    }

    public List<VulnerabilityLin> getTrueVuls(){
        return this.trueVuls;
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