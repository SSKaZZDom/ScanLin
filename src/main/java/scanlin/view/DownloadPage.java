package scanlin.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scanlin.viewmodel.ViewModelInterface;

public class DownloadPage extends BasePage{
    private final ViewModelInterface viewModel;

    public DownloadPage(Stage stage, ViewModelInterface viewModel) {
        super(stage);
        this.viewModel = viewModel;
        initializeContent();
    }

    private void initializeContent() {
        viewModel.startDownload();
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(40));

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: black; -fx-font-size: 20px;");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefHeight(20); // толщина
        progressBar.setPrefWidth(400); // ширина по желанию

        progressBar.setStyle(
                "-fx-accent: #4CAF50;" +  // зелёный цвет прогресса
                        "-fx-control-inner-background: transparent;" + // убираем фон
                        "-fx-background-color: transparent;" +         // убираем фон у самой полоски
                        "-fx-border-color: transparent;"               // убираем рамку
        );


        Button cancelButton = createStyledButton("Прервать");
        cancelButton.setOnAction(e -> {
            viewModel.stopDownload(); // Останавливаем процесс

            // Переход на главное меню
            MainMenu mainMenu = new MainMenu(viewModel, stage);
            stage.setScene(mainMenu.createScene());
        });

        Button finishButton = createStyledButton("Завершить");

        finishButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu(viewModel, stage);
            stage.setScene(mainMenu.createScene());
        });

        HBox scanningButtonsBox = new HBox(20, cancelButton);
        scanningButtonsBox.setAlignment(Pos.CENTER);

        HBox finishedButtonsBox = new HBox(20, finishButton);
        finishedButtonsBox.setAlignment(Pos.CENTER);
        finishedButtonsBox.setVisible(false);
        finishedButtonsBox.setManaged(false);

        viewModel.statusPropertyDownload().addListener((obs, oldStatus, newStatus) -> {
            if ("Завершено!".equals(newStatus)) {
                scanningButtonsBox.setVisible(false);
                scanningButtonsBox.setManaged(false);

                finishedButtonsBox.setVisible(true);
                finishedButtonsBox.setManaged(true);
            }
        });


        // Привязка к ViewModel
        progressBar.progressProperty().bind(viewModel.progressPropertyDownload());
        statusLabel.textProperty().bind(viewModel.statusPropertyDownload());

        contentBox.getChildren().addAll(
                statusLabel,
                progressBar,
                scanningButtonsBox,
                finishedButtonsBox
        );
        root.setCenter(contentBox);
    }
    @Override
    public Scene createScene() {
        return super.createScene();
    }
}