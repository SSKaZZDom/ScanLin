package scanlin.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

public interface ViewModelInterface {
    public void togglePauseScan();
    public BooleanProperty pausedPropertyScan();
    public DoubleProperty progressPropertyScan();
    public StringProperty statusPropertyScan();
    public void stopScan();
    public void startScan();
    public double calcFstec(double cvss, int type, int count, int network);
}
