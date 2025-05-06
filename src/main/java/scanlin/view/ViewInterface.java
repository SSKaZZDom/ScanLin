package scanlin.view;

import javafx.stage.Stage;
import scanlin.viewmodel.ViewModelInterface;

public interface ViewInterface {
    public void initUI(Stage primaryStage);
    public void setViewModel (ViewModelInterface viewModel);
}
