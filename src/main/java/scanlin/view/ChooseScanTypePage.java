package scanlin.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChooseScanTypePage extends BasePage{

    public ChooseScanTypePage(Stage stage) {
        super(stage);
        initialize();
    }

    private void initialize() {
        // Левая верхняя кнопка под верхней панелью
        Button backButton = createBackToMenuButton("Назад");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu( null, stage); // передай ViewModel, если нужно
            stage.setScene(mainMenu.createScene());
        });

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(10, 0, 0, 10));

        // Центральные кнопки
        Button centerButton1 = createStyledButton("Кнопка A");
        Button centerButton2 = createStyledButton("Кнопка B");

        VBox centerButtons = new VBox(15, centerButton1, centerButton2);
        centerButtons.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(20, backButtonBox, centerButtons);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20, 0, 0, 0));

        root.getChildren().add(contentBox);
    }
}
