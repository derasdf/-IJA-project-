import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import common.Environment;
import tool.common.Position;
import room.ControlledRobot;
import room.Room;
import common.Robot;

public class RoomWindow extends Application {
    private Environment room;
    private Rectangle[][] gridCells;
    private GridPane grid;
    int SizeCols = 0;
    int SizeRows = 0;

    public RoomWindow(Environment room) {
        this.room = room;
        this.gridCells = new Rectangle[room.rows()][room.cols()];
    }

    @Override
    public void start(Stage primaryStage) {
        // Создаем GridPane
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);


        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        // Создаем заголовок
        Label titleLabel = new Label("Room");
        titleLabel.setStyle("-fx-font-size: 25px;");
        vbox.getChildren().add(titleLabel);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);

        Button btnCreateRobot = new Button("Create Robot");
        btnCreateRobot.setOnAction(e -> openRobotDialog());

        Button btnCreateObstacle = new Button("Create Obstacle");
        btnCreateObstacle.setOnAction(e -> openObstacleDialog());
        // Размер каждой ячейки
        final int size = 400;
        SizeCols = size / this.room.cols();
        SizeRows = size / this.room.rows();
        grid.setAlignment(Pos.CENTER);
        // Создаем сетку из Rectangle
        for (int row = 0; row < this.room.rows(); row++) {
            for (int col = 0; col < this.room.cols(); col++) {
                Rectangle rect = new Rectangle(SizeCols, SizeRows);
                rect.setStroke(Color.BLACK);
                rect.setFill(Color.TRANSPARENT);

                grid.add(rect, col, row);
                gridCells[row][col] = rect;
            }
        }


        hbox.getChildren().addAll(btnCreateRobot, grid, btnCreateObstacle);

        vbox.getChildren().addAll(hbox);


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
        //Spinner<Integer> spinnerAngle = new Spinner<>(0, 360, 0, 45);

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            Position p = new Position(spinnerRow.getValue() - 1,spinnerCol.getValue() - 1);
            Robot robot = ControlledRobot.create(room, p);
            if (robot != null) {
                updateRobotCell(p.getRow(), p.getCol());
            }
            dialog.close();
        });

        dialogVbox.getChildren().addAll(new Label("Row ="), spinnerRow, new Label("Column ="), spinnerCol, btnCreate);

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
            int row = spinnerRow.getValue() - 1;
            int col = spinnerCol.getValue() - 1;
            if (room.createObstacleAt(row, col)) {
                updateObstacleCell(row, col);
            }
            dialog.close();
        });

        dialogVbox.getChildren().addAll(new Label("Row ="), spinnerRow, new Label("Column ="), spinnerCol, btnCreate);

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void updateRobotCell(int row, int col) {
        Circle circle = new Circle(SizeCols / 2, Color.BLUE);
        grid.add(circle, col, row);
    }

    private void updateObstacleCell(int row, int col) {
        gridCells[row][col].setFill(Color.RED);
    }
    public static void main(String[] args) {
        launch(args);
    }
}