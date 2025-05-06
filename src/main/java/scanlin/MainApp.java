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

    @Override
    public void start(Stage primaryStage) {
        ModelInterface model = new Model();
        ViewInterface view = new View();
        ViewModelInterface viewModel = new ViewModel(model, view);
        view.setViewModel(viewModel);
        model.setViewModel(viewModel);

        view.initUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
