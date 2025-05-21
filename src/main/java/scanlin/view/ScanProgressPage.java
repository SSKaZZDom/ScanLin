package scanlin.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scanlin.viewmodel.ViewModelInterface;

public class ScanProgressPage extends BasePage{
    private final ViewModelInterface viewModel;

    public ScanProgressPage(Stage stage, ViewModelInterface viewModel) {
        super(stage);
        this.viewModel = viewModel;
        initializeContent();
    }

    private void initializeContent() {
        viewModel.startScan();
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


        Button pauseButton = createStyledButton("⏸ Пауза");
        pauseButton.setOnAction(e -> {
            viewModel.togglePauseScan();
        });

        Button cancelButton = createStyledButton("Прервать");
        cancelButton.setOnAction(e -> {
            viewModel.stopScan(); // Останавливаем процесс

            // Переход на главное меню
            MainMenu mainMenu = new MainMenu(viewModel, stage);
            stage.setScene(mainMenu.createScene());
        });

        Button finishButton = createStyledButton("Завершить");
        finishButton.setVisible(false); // скрыта по умолчанию

        finishButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu(viewModel, stage);
            stage.setScene(mainMenu.createScene());
        });

        // Обновляем надпись кнопки при смене состояния
        viewModel.pausedPropertyScan().addListener((obs, oldVal, newVal) -> {
            pauseButton.setText(newVal ? "▶ Продолжить" : "⏸ Пауза");
        });

        viewModel.statusPropertyScan().addListener((obs, oldStatus, newStatus) -> {
            statusLabel.setText(newStatus);

            if ("Завершено!".equals(newStatus)) {
                finishButton.setVisible(true);
            }
        });

        // Привязка к ViewModel
        progressBar.progressProperty().bind(viewModel.progressPropertyScan());
        statusLabel.textProperty().bind(viewModel.statusPropertyScan());

        contentBox.getChildren().addAll(statusLabel, progressBar, pauseButton, cancelButton, finishButton);
        root.setCenter(contentBox);
    }
    @Override
    public Scene createScene() {
        Scene scene = new Scene(getRoot(), 600, 400);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return scene;
    }
}
