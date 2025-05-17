package scanlin.viewmodel;

import javafx.beans.property.*;
import scanlin.model.ModelInterface;

public class LoadingController {
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final StringProperty status = new SimpleStringProperty("Инициализация...");
    private final BooleanProperty paused = new SimpleBooleanProperty(false);
    ModelInterface model;
    public LoadingController(ModelInterface model) {
        this.model = model;
        startLoading();
    }

    private void startLoading() {
        model.runTestThread(
                progress::set,
                status::set
        );
    }

    public void togglePause() {
        if (paused.get()) {
            model.resumeTest();
            paused.set(false);
        } else {
            model.pauseTest();
            paused.set(true);
        }
    }

    public void stop() {
        if (paused.get()) {
            model.stopTest();
        }
    }

    public BooleanProperty pausedProperty() {
        return paused;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public StringProperty statusProperty() {
        return status;
    }
}
