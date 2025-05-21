package scanlin.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import scanlin.viewmodel.ViewModelInterface;

import java.util.stream.Stream;

public class CalcPage extends BasePage {

    ViewModelInterface viewModel;
    public CalcPage(Stage stage, ViewModelInterface viewModel) {
        super(stage);
        this.viewModel = viewModel;
        initializeContent();
    }

    private void initializeContent() {
        // Корневой макет
        BorderPane rootLayout = new BorderPane();

        // ===== КНОПКА "НАЗАД" =====
        Button backButton = createStyledButton("Назад");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu(viewModel, stage);
            stage.setScene(mainMenu.createScene());
        });

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(10, 0, 0, 10));

        // ===== ВВОД CVSS =====
        Label cvssLabel = new Label("Введите метрику CVSS 3.0:");
        cvssLabel.setStyle("-fx-font-size: 16px;");

        TextField inputField = new TextField();
        inputField.setPromptText("Например: 9.8");
        inputField.setMaxWidth(200);  // Ограничиваем ширину
        inputField.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;"
        );

        VBox topBox = new VBox(10, cvssLabel, inputField);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(60, 0, 0, 0));

        // ===== COMBOBOX =====
        ComboBox<String> combo1 = createStyledComboBox("Тип компонента информационной системы, подверженного уязвимости",
                "Уязвимости подвержены компоненты информационной системы, обеспечивающие реализацию критических процессов, функций, полномочий",
                "Уязвимости подвержены серверы",
                "Уязвимости подвержено телекоммуникационное оборудование, система управления сетью передачи данных",
                "Уязвимости подвержены автоматизированные рабочие места",
                "Уязвимости подвержены другие компоненты");
        ComboBox<String> combo2 = createStyledComboBox("\t\n" +
                        "Количество уязвимых компонентов информационной системы",
                "Более 70% компонентов от общего числа компонентов в информационной системе",
                "50 - 70% компонентов от общего числа компонентов в информационной системе",
                "10 - 50% компонентов от общего числа компонентов в информационной системе",
                "Менее 10% компонентов от общего числа компонентов в информационной системе");
        ComboBox<String> combo3 = createStyledComboBox("Влияние на эффективность защиты периметра системы, сети",
                "Уязвимое программное, программно-аппаратное средство доступно из сети \"Интернет\"",
                "Уязвимое программное, программно-аппаратное средство недоступно из сети \"Интернет\"");


        HBox comboBoxLine = new HBox(50, combo1, combo2, combo3);
        comboBoxLine.setAlignment(Pos.CENTER);

        // ===== КНОПКА "Рассчитать" =====
        Button calcButton = createStyledButton("Рассчитать");
        calcButton.setOnAction(e -> {
            String cvssText = inputField.getText().trim();

            boolean inputValid = true;
            double cvssValue = 0;

            // Попытка парсинга и проверка диапазона
            try {
                cvssValue = Double.parseDouble(cvssText);
                if (cvssValue < 0 || cvssValue > 10) {
                    inputValid = false;
                }
            } catch (NumberFormatException ex) {
                inputValid = false;
            }

            if (!inputValid) {
                inputField.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-border-radius: 5;" +
                                "-fx-border-color: red;" +
                                "-fx-border-width: 2;" +
                                "-fx-background-color: #ffeeee;"
                );
                return;
            } else {
                // Возвращаем нормальный стиль
                inputField.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-border-radius: 5;"
                );
            }

            String av = combo1.getValue();
            String ac = combo2.getValue();
            String ui = combo3.getValue();

            boolean valid = true;

            if (combo1.getSelectionModel().isEmpty()) {
                combo1.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                valid = false;
            } else {
                combo1.setStyle(""); // Сброс стиля, если валиден
            }

            if (combo2.getSelectionModel().isEmpty()) {
                combo2.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                valid = false;
            } else {
                combo2.setStyle("");
            }

            if (combo3.getSelectionModel().isEmpty()) {
                combo3.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                valid = false;
            } else {
                combo3.setStyle("");
            }

            int index1 = combo1.getSelectionModel().getSelectedIndex();
            int index2 = combo2.getSelectionModel().getSelectedIndex();
            int index3 = combo3.getSelectionModel().getSelectedIndex();
            // Подставь сюда нужные параметры вместо 1,2,3 при необходимости
            if (valid) {
                double result = viewModel.calcFstec(cvssValue, index1, index2, index3);
                showCustomResultDialog(result);
            }
        });


        VBox bottomBox = new VBox(20, comboBoxLine, calcButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 40, 0)); // Нижняя половина

        // ===== СБОРКА В РУТ =====
        rootLayout.setTop(backButtonBox);
        rootLayout.setCenter(topBox);
        rootLayout.setBottom(bottomBox);

        root.setCenter(rootLayout); // root унаследован из BasePage
    }


    @Override
    public Scene createScene() {
        return super.createScene();
    }

    private ComboBox<String> createStyledComboBox(String promptText, String... items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.setPromptText(promptText);
        comboBox.setPrefWidth(400);
        comboBox.setPrefHeight(100);

        // Стиль
        comboBox.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #999;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-font-size: 11px;
            -fx-padding: 3 6;
        """);

        // Ячейки выпадающего списка
        comboBox.setCellFactory(listView -> new ListCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setTextFill(javafx.scene.paint.Color.BLACK);
                label.setMaxWidth(380);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        });

        // Ячейка, отображаемая в закрытом состоянии ComboBox
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? promptText : item);
                setStyle("-fx-text-fill: black;");
                setWrapText(true);
            }
        });

        return comboBox;
    }


    private void showCustomResultDialog(double result) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED); // Без стандартной рамки

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: black; -fx-border-width: 1px;");

        // Чёрная полоска сверху
        Label titleBar = new Label("  Результат");
        titleBar.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
        titleBar.setPrefHeight(30);
        titleBar.setMaxWidth(Double.MAX_VALUE);
        titleBar.setAlignment(Pos.CENTER_LEFT);

        String severityLevel;
        String colorHex;
        if (result < 1.5) {
            severityLevel = "Низкий";
            colorHex = "#4CAF50"; // Зелёный
        } else if (result < 4.5) {
            severityLevel = "Средний";
            colorHex = "#FFD700"; // Жёлтый
        } else if (result < 7.0) {
            severityLevel = "Высокий";
            colorHex = "#FF6347"; // Красный
        } else {
            severityLevel = "Критический";
            colorHex = "#800000"; // Бордовый
        }

        // Текст результата
        Label resultLabel = new Label(String.format("Ваша метрика: %.2f%n", result));
        resultLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        resultLabel.setWrapText(true);
        resultLabel.setPadding(new Insets(20));
        resultLabel.setAlignment(Pos.CENTER);

        // Текст уровня опасности
        Label severityLabel = new Label("Уровень опасности: " + severityLevel);
        severityLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");

        Region colorIndicator = new Region();
        colorIndicator.setPrefSize(16, 16);
        colorIndicator.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: black; -fx-border-width: 1;", colorHex));

        // Кнопка закрытия
        Button okButton = new Button("Ок");
        okButton.setStyle("-fx-background-color: #A9A9A9; -fx-text-fill: black; -fx-font-size: 13px;");
        okButton.setOnAction(e -> dialog.close());

        HBox severityBox = new HBox(8, severityLabel, colorIndicator);
        severityBox.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(10, resultLabel, severityBox);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(20, 20, 10, 20));

        VBox content = new VBox(resultBox, okButton);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        root.getChildren().addAll(titleBar, content);

        Scene scene = new Scene(root, 500, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

}
