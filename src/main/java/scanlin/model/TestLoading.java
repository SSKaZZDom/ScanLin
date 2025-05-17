package scanlin.model;

import javafx.application.Platform;
import java.util.function.Consumer;

public class TestLoading {
    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private final Object pauseLock = new Object();

    public void runLongOperation(Consumer<Double> onProgress, Consumer<String> onStatus) {
        new Thread(() -> {
            for (int i = 1; i <= 60 && !stopped; i++) {
                synchronized (pauseLock) {
                    while (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            Platform.runLater(() -> onStatus.accept("Операция прервана"));
                            return;
                        }
                    }
                }

                double progress = i / 60.0;

                Platform.runLater(() -> {
                    onProgress.accept(progress);
                    onStatus.accept((int)(progress * 100) + "%");
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Platform.runLater(() -> onStatus.accept("Операция прервана"));
                    return;
                }
            }

            if (!stopped) {
                Platform.runLater(() -> onStatus.accept("Завершено!"));
            }
        }).start();
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
        resume(); // если вдруг в паузе — выйдет
    }

    public boolean isPaused() {
        return paused;
    }
}
