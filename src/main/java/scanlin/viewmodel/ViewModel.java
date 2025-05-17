package scanlin.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import scanlin.model.ModelInterface;
import scanlin.view.ViewInterface;

public class ViewModel implements ViewModelInterface {
    ModelInterface model;
    ViewInterface view;
    LoadingController testController;
    public ViewModel(ModelInterface model, ViewInterface view) {
        this.model = model;
        this.view = view;
    }
    @Override
    public void togglePause(){
        testController.togglePause();
    }

    @Override
    public StringProperty statusProperty(){
        return testController.statusProperty();
    }

    @Override
    public DoubleProperty progressProperty(){
        return testController.progressProperty();
    }

    @Override
    public BooleanProperty pausedProperty(){
        return testController.pausedProperty();
    }

    @Override
    public void stop(){ testController.stop();}

    @Override
    public void start() {
        testController = new LoadingController(model);
    }
}
