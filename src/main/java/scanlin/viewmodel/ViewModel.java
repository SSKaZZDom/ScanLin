package scanlin.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import scanlin.model.ModelInterface;
import scanlin.view.ViewInterface;

public class ViewModel implements ViewModelInterface {
    ModelInterface model;
    ViewInterface view;
    ScanController scanController;
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
}
