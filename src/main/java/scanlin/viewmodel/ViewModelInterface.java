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
}
