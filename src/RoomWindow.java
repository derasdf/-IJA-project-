import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import common.Environment;
import tool.common.Position;
import room.ControlledRobot;
import room.Room;
import common.Robot;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javax.swing.JOptionPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


public class RoomWindow extends Application {
    private Canvas canvas;
    private int CELL_SIZE = 60;
    private int OBSTACLE_SIZE = 10;
    private int ROBOT_SIZE = 20;

    Environment room;
    GraphicsContext gc;
    private char[][] map;
    public void setMap(char[][] map) {
        this.map = map;
    }
    @Override
    public void start(Stage primaryStage) {
        if (map == null) {
            return;
        }
        canvas = new Canvas(600, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        createRoomFromMap(map);
        Button btnCreateRobot = new Button("Create Robot");
        btnCreateRobot.setPrefSize(200, 50);
        btnCreateRobot.setOnAction(e -> openRobotDialog());

        Button btnCreateObstacle = new Button("Create Obstacle");
        btnCreateObstacle.setPrefSize(200, 50);
        btnCreateObstacle.setOnAction(e -> openObstacleDialog());

        Button btnClear = new Button("Clear");
        btnClear.setPrefSize(200, 50); // Установка предпочтительного размера
        btnClear.setOnAction(e -> clearCanvas(gc)); // Действие на очистку холста

        Button btnStartAut = new Button("Start automatic");
        btnStartAut.setPrefSize(200, 50); // Установка предпочтительного размера
        btnStartAut.setOnAction(e -> startAutomatic(gc)); // Действие на очистку холста

        HBox hboxButtons = new HBox(10, btnCreateRobot, btnCreateObstacle, btnClear, btnStartAut);
        hboxButtons.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(10, hboxButtons, canvas);
        vbox.setAlignment(Pos.CENTER);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setTitle("Room Grid Window");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    private void createRoomFromMap(char[][] map) {
        int roomWidth = map[0].length * CELL_SIZE; // Adjust CELL_SIZE according to your needs
        int roomHeight = map.length * CELL_SIZE; // Adjust CELL_SIZE according to your needs
        room = Room.create(roomWidth, roomHeight);

        // Iterate over the map and add objects accordingly
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                char symbol = map[row][col];
                int x = col * CELL_SIZE + CELL_SIZE / 2; // Adjust according to your needs
                int y = row * CELL_SIZE + CELL_SIZE / 2; // Adjust according to your needs

                switch (symbol) {
                    case 'X': // Add obstacle
                        createObstacle(60,x, y);
                        break;
                    case 'R': // Add robot
                        Position pos = new Position(x, y);
                        ControlledRobot robot = ControlledRobot.create(room, pos, ROBOT_SIZE); // Adjust ROBOT_SIZE according to your needs
                        // You may also need to set additional properties of the robot
                        if(robot == null)
                        {
                            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            drawRobot(pos.getWidth(), pos.getHeight());
                        }
                        break;
                    // Add cases for other symbols as needed
                }
            }
        }
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        room.clearRobots();
        room.clearObstacles();
    }

    private void openRobotDialog() {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), 0);
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), 0);

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            Position pos = new Position(spinnerX.getValue() - 15, spinnerY.getValue() - 15);
            System.out.println("" + room.obstacleAt(pos, 30) + " " + room.robotAt(pos, 30));
            ControlledRobot robot = ControlledRobot.create(room, pos, 30);
            if(robot == null)
            {
                JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                drawRobot(pos.getWidth(), pos.getHeight());
            }
            dialog.close();
        });

        dialogVbox.getChildren().addAll(new Label("X Coordinate:"), spinnerX, new Label("Y Coordinate:"), spinnerY, btnCreate);

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void openObstacleDialog() {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), 0);
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), 0);
        Spinner<Integer> spinnerSize = new Spinner<>(1, 100, 10);

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            createObstacle(spinnerSize.getValue(),spinnerX.getValue(),spinnerY.getValue());
            dialog.close();
        });

        dialogVbox.getChildren().addAll(
                new Label("X Coordinate:"), spinnerX,
                new Label("Y Coordinate:"), spinnerY,
                new Label("Size:"), spinnerSize,
                btnCreate);

        Scene dialogScene = new Scene(dialogVbox, 300, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    private void createObstacle(int size,int valueX,int valueY) {
        if (!room.createObstacleAt(valueX - size / 2, valueY - size / 2, size)) {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            drawObstacle(valueX, valueY, size);
        }
    }
    private void drawRobot(int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y , 30, 30);
    }

    private void drawObstacle(int x, int y, int size) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(x - size/2, y - size/2, size, size);
    }
    private void startAutomatic(GraphicsContext gc) {
        double timeStep = 0.1; // Время в секундах между обновлениями

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(timeStep), e -> {
            for (ControlledRobot robot : room.robots()) {
                // Сохраняем старые координаты
                int oldX = robot.getPosition().getWidth();
                int oldY = robot.getPosition().getHeight();

                // Рассчитываем новое положение на основе скорости и направления
                double angleInRadians = Math.toRadians(robot.angle());
                int speed = robot.getSpeed(); // предполагается, что скорость определена в robot
                int newX = (int) (oldX + Math.cos(angleInRadians) * speed * timeStep);
                int newY = (int) (oldY + Math.sin(angleInRadians) * speed * timeStep);
                Position newPosition = new Position(newX, newY);
                System.out.println("NewX = " + newX + " NewY= " + newY + " Size = " + robot.getSize() +  " Math.cos(angleInRadians) =  " + Math.cos(angleInRadians) + " Math.sin(angleInRadians) =  " + Math.sin(angleInRadians) + " speed = " + speed + " timeStep = " + timeStep);
                // Проверка на столкновение
                if (!room.obstacleAt(newPosition, robot.getSize()) && !room.robotAt(newPosition, robot.getSize()) && room.containsPosition(newPosition, robot.getSize())) {
                    // Стираем робота на старой позиции
                    clearRobotAt(gc, oldX, oldY, robot.getSize());

                    // Обновляем позицию робота и рисуем его на новом месте
                    robot.setPosition(newPosition);
                    drawRobot(newX, newY);
                } else {
                    // Поворот на заданный угол при обнаружении препятствия
                    robot.turn(robot.getTurnAngle()); // предполагается метод getTurnAngle
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void clearRobotAt(GraphicsContext gc, int x, int y, int size) {
        gc.setFill(Color.LIGHTGRAY); // Цвет фона
        gc.fillRect(x , y , 30, 30);
    }

    public static void main(String[] args) {
        launch(args);
    }
}