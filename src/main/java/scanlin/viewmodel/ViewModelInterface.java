package scanlin.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

public interface ViewModelInterface {
    public void togglePause();
    public BooleanProperty pausedProperty();
    public DoubleProperty progressProperty();
    public StringProperty statusProperty();
    public void stop();
    public void start();
    public void togglePauseScan();
    public BooleanProperty pausedPropertyScan();
    public DoubleProperty progressPropertyScan();
    public StringProperty statusPropertyScan();
    public void stopScan();
    public void startScan();
}
