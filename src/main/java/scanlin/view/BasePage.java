package scanlin.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public abstract class BasePage {

    protected final BorderPane root = new BorderPane();
    protected Stage stage;
    protected double xOffset = 0;
    protected double yOffset = 0;

    public BasePage(Stage stage) {
        this.stage = stage;
        initializeBaseLayout();
    }

    private void initializeBaseLayout() {
        root.setPrefSize(600, 400);
        root.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-background-radius: 9 9 8 8;" +
                        "-fx-border-radius: 9 9 8 8;"
        );

        // Устанавливаем верхнюю панель
        root.setTop(createTopBar());
    }

    /**
     * Создаёт верхнюю панель с логотипом и кнопками управления
     */
    protected BorderPane createTopBar() {
        BorderPane topBar = new BorderPane();
        topBar.setPrefHeight(40);
        topBar.setStyle("-fx-background-color: black;" +
                "-fx-background-radius: 8 8 0 0;" +
                "-fx-border-radius: 8 8 0 0;");

        // Перетаскивание окна
        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Левая часть — надпись ScanLin
        Label titleLabel = new Label("ScanLin");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font(18));
        BorderPane.setAlignment(titleLabel, Pos.CENTER_LEFT);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 0, 10));
        topBar.setLeft(titleLabel);

        // Кнопки управления
        Button closeButton = createTopBarButton("✕", e -> Platform.exit());
        Button maximizeButton = createTopBarButton("☐", e -> stage.setFullScreen(!stage.isFullScreen()));
        Button minimizeButton = createTopBarButton("─", e -> stage.setIconified(true));

        HBox rightButtons = new HBox(10, minimizeButton, maximizeButton, closeButton);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        rightButtons.setPadding(new Insets(0, 10, 0, 0));
        topBar.setRight(rightButtons);

        return topBar;
    }

    /**
     * Создаёт кнопку на верхней панели
     */
    protected Button createTopBarButton(String symbol, javafx.event.EventHandler<MouseEvent> handler) {
        Button button = new Button(symbol);
        button.setFont(Font.font(14));
        button.setTextFill(Color.WHITE);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseClicked(handler);
        return button;
    }

    /**
     * Стандартная большая кнопка
     */
    protected Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(16));
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setStyle(
                "-fx-background-color: #A9A9A9;" +
                        "-fx-border-color: #808080;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;"
        );
        return button;
    }

    /**
     * Кнопка «Назад в меню»
     */
    protected Button createBackToMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(12));
        button.setPrefWidth(120);
        button.setPrefHeight(30);
        button.setStyle(
                "-fx-background-color: #A9A9A9;" +
                        "-fx-border-color: #808080;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;"
        );
        return button;
    }

    /**
     * Возвращает корневой элемент
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Создаёт сцену с прозрачным фоном
     */
    public Scene createScene() {
        Scene scene = new Scene(getRoot());
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }
}
