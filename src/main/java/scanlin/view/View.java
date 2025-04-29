package scanlin.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scanlin.model.ModelInterface;
import scanlin.viewmodel.ViewModelInterface;

public class View implements ViewInterface{
    ViewModelInterface viewModel;
    public View(ViewModelInterface viewModel) {
        this.viewModel = viewModel;
    }

    public void initUI(Stage stage) {
        // Установка начальной сцены
        MainMenu menu = new MainMenu(viewModel);
        Scene scene = new Scene(menu.getRoot());
        stage.setScene(scene);
        stage.setTitle("Главное меню");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
