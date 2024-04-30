import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import common.Environment;
import tool.common.Position;
import room.ControlledRobot;
import room.Room;

public class RoomWindow extends Application {
    private Environment room;

    public RoomWindow(Environment room) {
        this.room = room;
    }

    @Override
    public void start(Stage primaryStage) {
        // Создаем GridPane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // Центрирование содержимого GridPane

        // Создаем VBox для верхнего размещения заголовка
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER); // Выравнивание по верху центра

        // Создаем заголовок
        Label titleLabel = new Label("Room");
        titleLabel.setStyle("-fx-font-size: 25px;"); // Установка размера шрифта
        vbox.getChildren().add(titleLabel); // Добавление заголовка в VBox

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);

        Button btnCreateRobot = new Button("Create Robot");
        btnCreateRobot.setOnAction(e -> openRobotDialog());

        Button btnCreateObstacle = new Button("Create Obstacle");
        btnCreateObstacle.setOnAction(e -> openObstacleDialog());
        // Размер каждой ячейки
        final int size = 400;
        final int SizeCols = size / this.room.cols();
        final int SizeRows = size / this.room.rows();
        grid.setAlignment(Pos.CENTER);
        // Создаем сетку из Rectangle
        for (int row = 0; row < this.room.rows(); row++) {
            for (int col = 0; col < this.room.cols(); col++) {
                Rectangle rect = new Rectangle(SizeCols, SizeRows);
                rect.setStroke(Color.BLACK); // Цвет границы ячейки
                rect.setFill(Color.TRANSPARENT); // Прозрачное заполнение

                grid.add(rect, col, row); // Добавление Rectangle в GridPane
            }
        }


        hbox.getChildren().addAll(btnCreateRobot, grid, btnCreateObstacle);
        // Добавление кнопок и GridPane в VBox
        vbox.getChildren().addAll(hbox);

        // Добавляем VBox на сцену
        Scene scene = new Scene(vbox, 800, 800);
        primaryStage.setTitle("Room Grid Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openRobotDialog() {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);

        Spinner<Integer> spinnerRow = new Spinner<>(1, room.rows(), 1);
        Spinner<Integer> spinnerCol = new Spinner<>(1, room.cols(), 1);
        Spinner<Integer> spinnerAngle = new Spinner<>(0, 360, 0, 45);

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            // Логика создания робота
            dialog.close();
        });

        dialogVbox.getChildren().addAll(new Label("Row ="), spinnerRow, new Label("Column ="), spinnerCol, new Label("Angle ="), spinnerAngle, btnCreate);

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void openObstacleDialog() {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);

        Spinner<Integer> spinnerRow = new Spinner<>(1, room.rows(), 1);
        Spinner<Integer> spinnerCol = new Spinner<>(1, room.cols(), 1);

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            // Логика создания препятствия
            dialog.close();
        });

        dialogVbox.getChildren().addAll(new Label("Row ="), spinnerRow, new Label("Column ="), spinnerCol, btnCreate);

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}