package scanlin;

import javafx.application.Application;
import javafx.stage.Stage;
import scanlin.model.Model;
import scanlin.model.ModelInterface;
import scanlin.view.View;
import scanlin.view.ViewInterface;
import scanlin.viewmodel.ViewModel;
import scanlin.viewmodel.ViewModelInterface;

public class MainApp extends Application {
    private Model model;
    private ViewModel viewModel;
    private View view;

    @Override
    public void start(Stage primaryStage) {
        ModelInterface model = new Model();
        ViewModelInterface viewmodel = new ViewModel(model);
        ViewInterface view = new View(viewmodel);

        view.initUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
