package scanlin.view;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scanlin.model.ModelInterface;
import scanlin.viewmodel.ViewModelInterface;

public class View implements ViewInterface{
    ViewModelInterface viewModel;
    public View() {

    }

    public void initUI(Stage stage) {
        // Установка начальной сцены
        MainMenu menu = new MainMenu(viewModel, stage);
        Scene scene = new Scene(menu.getRoot(), 900, 600);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Главное меню");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public void setViewModel(ViewModelInterface viewModel) {
        this.viewModel = viewModel;
    }
}
