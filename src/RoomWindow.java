import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import common.Obstacle;


public class RoomWindow extends Application {
    private Canvas canvas;
    private int CELL_SIZE = 60;
    private int OBSTACLE_SIZE = 10;
    private int ROBOT_SIZE = 30;

    Environment room;
    GraphicsContext gc;
    ListView<ControlledRobot> robotList;
    ListView<Obstacle> obstacleList;
    ControlledRobot selectedRobot;
    Obstacle selectedObstacle;

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
        room = Room.create(600, 600);

        robotList = new ListView<>();
        obstacleList = new ListView<>();
        robotList.setItems(FXCollections.observableArrayList(room.robots()));
        obstacleList.setItems(FXCollections.observableArrayList(room.myObstacleslist()));

        robotList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                obstacleList.getSelectionModel().clearSelection();
                selectedObstacle = null;
                selectedRobot = newVal;
                highlightObjectOnCanvas(newVal.getPosition(), true);
            }
        });

        obstacleList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                robotList.getSelectionModel().clearSelection();
                selectedRobot = null;
                selectedObstacle = newVal;
                highlightObjectOnCanvas(newVal.getPosition(), false);
            }
        });
        createRoomFromMap(map);

        Button btnCreateRobot = new Button("Create Robot");
        btnCreateRobot.setPrefSize(200, 50);
        btnCreateRobot.setOnAction(e -> openRobotDialog());

        Button btnCreateObstacle = new Button("Create Obstacle");
        btnCreateObstacle.setPrefSize(200, 50);
        btnCreateObstacle.setOnAction(e -> openObstacleDialog());

        Button btnClear = new Button("Clear");
        btnClear.setPrefSize(200, 50);
        btnClear.setOnAction(e -> clearCanvas(gc));

        Button btnStartAut = new Button("Start automatic");
        btnStartAut.setPrefSize(200, 50);
        btnStartAut.setOnAction(e -> startAutomatic(gc));

        Button btnChange = new Button("Change");
        btnChange.setPrefSize(200, 50);
        btnChange.setOnAction(e -> handleChange());
        Button btnDelete = new Button("Delete");
        btnDelete.setPrefSize(200, 50);
        btnDelete.setOnAction(e -> handleDelete());

        HBox hboxButtons = new HBox(10, btnCreateRobot, btnCreateObstacle, btnClear, btnStartAut, btnChange, btnDelete);
        hboxButtons.setAlignment(Pos.CENTER);
        VBox leftPanel = new VBox(10, new Label("Robots"), robotList);
        VBox rightPanel = new VBox(10, new Label("Obstacles"), obstacleList);
        VBox vbox = new VBox(10, hboxButtons, new HBox(10,leftPanel,  canvas,  rightPanel ) );
        vbox.setAlignment(Pos.CENTER);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());
        scene.setOnMouseClicked(e -> {
            if (robotList.isFocused() || obstacleList.isFocused()) {
                System.out.println("Mouse clicked");
                robotList.getSelectionModel().clearSelection();
                obstacleList.getSelectionModel().clearSelection();
                selectedRobot = null;
                selectedObstacle = null;
                drawAllObjects();
            }
        });
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
                        placeObject(x,y,60);
                        break;
                    case 'R': // Add robot
                        System.out.println("Place robot " + x + " " + y);
                        placeRobot(x,y,40,25,30);
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
        Spinner<Integer> spinnerSpeeed = new Spinner<>(20, 100, 0);
        Spinner<Integer> spinnerTurnAngle = new Spinner<>(0, 360, 0);
        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            placeRobot(spinnerX.getValue(),spinnerY.getValue() , spinnerSpeeed.getValue(),spinnerTurnAngle.getValue(),30);
            dialog.close();
        });

        dialogVbox.getChildren().addAll(new Label("X Coordinate:"), spinnerX, new Label("Y Coordinate:"), spinnerY, new Label("Speed:"), spinnerSpeeed, new Label("Turn angle:"), spinnerTurnAngle ,  btnCreate);

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
            placeObject(spinnerX.getValue(),spinnerY.getValue(),spinnerSize.getValue());
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
    private void placeRobot(double x, double y,int speed,int angle, int size) {
        Position pos = new Position(x - 15, y - 15);
        ControlledRobot robot = ControlledRobot.create(room, pos, size,speed, angle);
        if(robot == null)
        {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            robotList.getItems().add(robot);
            System.out.println("Place robot " + pos.getWidth() + " " + pos.getHeight() + " " + size + " " + speed + " " + angle);
            drawRobot(pos.getWidth(), pos.getHeight(), robot);
        }
    }
    private void placeObject(double x, double y, int size) {
        Obstacle newObstacle = Obstacle.create(room, new Position(x - size/2 , y - size/2), size);
        double b = x- size/2;
        double c = y- size/2;
        System.out.println("Obstacle created " + b + " " + c + " " + size);
        if(newObstacle == null)
        {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            obstacleList.getItems().add(newObstacle);
            drawObstacle(newObstacle.getPosition().getWidth() , newObstacle.getPosition().getHeight() , size, newObstacle);
        }
    }
    private void drawRobot(double x, double y, ControlledRobot robot) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, 30, 30);
        if (robot.equals(selectedRobot)) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(2);
            gc.strokeOval(x, y, 30, 30);
        }
    }

    private void drawObstacle(double x, double y, int size, Obstacle obstacle) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(x, y, size, size);
        if (obstacle.equals(selectedObstacle)) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, size, size);
        }
    }
    private void startAutomatic(GraphicsContext gc) {
        double timeStep = 0.1;

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(timeStep), e -> {
            for (ControlledRobot robot : room.robots()) {

                double oldX = robot.getPosition().getWidth();
                double oldY = robot.getPosition().getHeight();
                double angleInRadians = Math.toRadians(robot.angle());
                int speed = robot.getSpeed();
                double newX = (oldX + Math.cos(angleInRadians) * speed * timeStep);
                double newY = (oldY + Math.sin(angleInRadians) * speed * timeStep);
                Position newPosition = new Position(newX, newY);
                System.out.println("NewX = " + newX + " NewY= " + newY + " Size = " + robot.getSize() +  " Math.cos(angleInRadians) =  " + Math.cos(angleInRadians) + " Math.sin(angleInRadians) =  " + Math.sin(angleInRadians) + " speed = " + speed + " timeStep = " + timeStep + " angle = " + robot.angle());
                System.out.println(" " + room.obstacleAt(newPosition, robot.getSize(), null) + " " + room.robotAt(newPosition, robot.getSize(), robot) + " " + room.containsPosition(newPosition, robot.getSize()));

                if (!room.obstacleAt(newPosition, robot.getSize(), null) && !room.robotAt(newPosition, robot.getSize(), robot) && room.containsPosition(newPosition, robot.getSize())) {
                    clearRobotAt(gc, oldX, oldY, robot.getSize());
                    robot.setPosition(newPosition);
                    drawRobot(newX, newY, robot);
                } else {

                    robot.turn(robot.getTurnAngle());
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void clearRobotAt(GraphicsContext gc, double x, double y, int size) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x - 1 , y - 1  , 32,  32);
    }

    private void drawAllObjects() {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        System.out.println("drawAllObjects" + room.robots().size() + " " + room.myObstacleslist().size());
        for (ControlledRobot robot : room.robots()) {
            drawRobot(robot.getPosition().getWidth(), robot.getPosition().getHeight(), robot);
        }
        for (Obstacle obstacle : room.myObstacleslist()) {
            drawObstacle(obstacle.getPosition().getWidth(), obstacle.getPosition().getHeight(), obstacle.getSize(), obstacle);
        }
    }
    private void highlightObjectOnCanvas(Position pos, boolean isRobot) {
        drawAllObjects();
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        if (isRobot) {
            gc.strokeOval(pos.getWidth(), pos.getHeight(), 30, 30); // Выделение робота
        } else {
            int size = selectedObstacle.getSize();
            gc.strokeRect(pos.getWidth(), pos.getHeight(), size, size); // Выделение препятствия
        }
    }

    private void handleChange() {
        if (selectedRobot != null) {
        } else if (selectedObstacle != null) {
        }
    }

    private void handleDelete() {
        if (selectedRobot != null) {
            room.removeRobot(selectedRobot);
            robotList.getItems().remove(selectedRobot);
            selectedRobot = null;
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawAllObjects();
        }
        else if (selectedObstacle != null) {
            room.removeObstacle(selectedObstacle);
            obstacleList.getItems().remove(selectedObstacle);
            selectedObstacle = null;
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawAllObjects();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}