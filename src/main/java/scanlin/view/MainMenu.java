package scanlin.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scanlin.viewmodel.ViewModelInterface;

public class MainMenu extends BasePage{
    ViewModelInterface viewModel;

    public MainMenu(ViewModelInterface viewModel,Stage stage) {
        super(stage);
        this.viewModel = viewModel;
        initialize();
    }

    private void initialize() {
        VBox buttonsBox = new VBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button btn1 = createStyledButton("Поиск уязвимостей");
        Button btn2 = createStyledButton("Просмотр отчётов");
        Button btn3 = createStyledButton("Загрузка базы данных");
        Button btn4 = createStyledButton("Калькулятор ФСТЭК");

        buttonsBox.getChildren().addAll(btn1, btn2, btn3, btn4);

        btn1.setOnAction(e -> {
            ScanProgressPage scanPage = new ScanProgressPage(stage, viewModel);
            stage.setScene(scanPage.createScene());
        });

        btn3.setOnAction(e -> {
            DownloadPage downloadPage = new DownloadPage(stage, viewModel);
            stage.setScene(downloadPage.createScene());
        });

        btn4.setOnAction((e -> {
            CalcPage calcPage = new CalcPage(stage, viewModel);
            stage.setScene(calcPage.createScene());
        }));



        VBox centerContainer = new VBox(buttonsBox);
        centerContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerContainer, Priority.ALWAYS);

        root.setCenter(centerContainer); // 👈 Правильная установка содержимого в центр
    }

    @Override
    public Scene createScene() {
        return super.createScene();
    }

    /*private double xOffset = 0;
    private double yOffset = 0;

    private final VBox root;
    private final Stage stage;
    ViewModelInterface viewModel;

    public MainMenu(ViewModelInterface viewModel, Stage stage) {
        this.viewModel = viewModel;
        this.stage = stage;
        root = new VBox();
        initialize();
    }

    private void initialize() {
        root.setPrefSize(600, 400);
        root.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;"
        );



        // Верхняя чёрная плашка
        BorderPane topBar = new BorderPane();
        topBar.setPrefHeight(40);
        topBar.setStyle("-fx-background-color: black;");

        //Код, для перетаскивания окна
        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        topBar.setOnMouseDragged(event -> {
            root.getScene().getWindow().setX(event.getScreenX() - xOffset);
            root.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });


        // Левая часть — надпись ScanLin
        Label titleLabel = new Label("ScanLin");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font(18));
        BorderPane.setAlignment(titleLabel, Pos.CENTER_LEFT);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 0, 10));
        topBar.setLeft(titleLabel);

        // Правая часть — кнопка-крестик
        Button closeButton = new Button("✕");
        closeButton.setFont(Font.font(14));
        closeButton.setTextFill(Color.WHITE);
        closeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> Platform.exit());

        //Кнопка полноэкранного режима
        Button maximizeButton = new Button("☐");
        maximizeButton.setFont(Font.font(14));
        maximizeButton.setTextFill(Color.WHITE);
        maximizeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );

        // Кнопка "свернуть"
        Button minimizeButton = new Button("─");
        minimizeButton.setFont(Font.font(14));
        minimizeButton.setTextFill(Color.WHITE);
        minimizeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );
        minimizeButton.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setIconified(true);
        });

        // Обработчик для полноэкранной кнопки
        maximizeButton.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        });

        // Группа кнопок справа
        HBox rightButtons = new HBox(10,minimizeButton, maximizeButton, closeButton);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        rightButtons.setPadding(new Insets(0, 10, 0, 0));
        topBar.setRight(rightButtons);

        // Кнопки главного меню
        Button btn1 = createMenuButton("Поиск уязвимостей");
        Button btn2 = createMenuButton("Просмотр отчётов");
        Button btn3 = createMenuButton("Загрузка базы данных");
        Button btn4 = createMenuButton("Калькулятор ФСТЭК");

        btn1.setOnAction(e -> {
            ChooseScanTypePage scanTypePage = new ChooseScanTypePage(stage);
            stage.setScene(scanTypePage.createScene());
        });

        VBox buttonsBox = new VBox(15, btn1, btn2, btn3, btn4);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox centerArea = new VBox(buttonsBox);
        centerArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        root.getChildren().addAll(topBar, centerArea);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(16));
        button.setPrefWidth(200);
        button.setPrefHeight(40);

        button.setStyle(
                "-fx-background-color: #A9A9A9;" +      // тёмно-серый фон
                        "-fx-border-color: #808080;" +          // серая рамка
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +               // белый текст
                        "-fx-background-radius: 10px;" +        // скругление фона
                        "-fx-border-radius: 10px;"              // скругление рамки
        );

        return button;
    }

    public Parent getRoot() {
        return root;
    }

    public Scene createScene() {
        Scene scene = new Scene(getRoot());
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }*/
}
