package scanlin.viewmodel;

import javafx.beans.property.*;
import scanlin.model.ModelInterface;
import scanlin.view.ViewInterface;

import java.io.IOException;

public class ViewModel implements ViewModelInterface {
    ModelInterface model;
    ViewInterface view;
    ScanController scanController;
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final StringProperty status = new SimpleStringProperty("Инициализация...");
    public ViewModel(ModelInterface model, ViewInterface view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void togglePauseScan(){
        scanController.togglePause();
    }

    @Override
    public StringProperty statusPropertyScan(){
        return scanController.statusProperty();
    }

    @Override
    public DoubleProperty progressPropertyScan(){
        return scanController.progressProperty();
    }

    @Override
    public BooleanProperty pausedPropertyScan(){
        return scanController.pausedProperty();
    }

    @Override
    public void stopScan(){ scanController.stop();}

    @Override
    public void startScan() {
        scanController = new ScanController(model);
    }

    @Override
    public double calcFstec(double cvss, int type, int count, int network) {
        return model.calc(cvss, type, count, network);
    }
    @Override
    public void startDownload(){
        try {
            model.updateDataBase(progress::set,
                    status::set);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopDownload() {
        model.stopDownload();
        StringProperty status = new SimpleStringProperty("Инициализация...");
    }

    @Override
    public StringProperty statusPropertyDownload(){
        return status;
    }

    @Override
    public DoubleProperty progressPropertyDownload(){
        return progress;
    }
}
