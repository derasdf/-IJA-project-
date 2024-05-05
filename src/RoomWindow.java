import javafx.animation.Animation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import common.Environment;
import tool.common.Position;
import room.ControlledRobot;
import room.Room;
import common.Collectable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javax.swing.JOptionPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import common.Obstacle;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.input.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.scene.image.Image;
import java.io.File;
public class RoomWindow extends Application {
    private Canvas canvas;
    private int CELL_SIZE = 600;
    private int OBSTACLE_SIZE = 10;
    private int ROBOT_SIZE = 30;
    private int collected = 0;
    private int collectedExists = 0;
    private int timeLeft = 60;
    Label dustLabel = new Label("Dust collected: ");
    Label TimerLabel = new Label("Time left: ");
    Label endScreenLabel = new Label("YOU");
    private Map<ControlledRobot, Timeline> robotTimelines = new HashMap<>();
    private boolean keyboardControlActive = false;
    private ControlledRobot activeRobot = null;
    private Timeline[] timeline = {null}; // Initialize timeline as an array to access it inside the lambda

    Environment room;
    GraphicsContext gc;
    ListView<ControlledRobot> robotList;
    ListView<Obstacle> obstacleList;
    ListView<Collectable> collectableList;
    ControlledRobot selectedRobot;
    Obstacle selectedObstacle;
    Collectable selectedCollectable;


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
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY && selectedRobot != null) {
                activeRobot = selectedRobot;
                moveTowards(activeRobot, e.getX(), e.getY());
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY && activeRobot != null) {
                moveTowards(activeRobot, e.getX(), e.getY());
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && activeRobot != null) {
                stopContinuousMovement(activeRobot);
                activeRobot = null;
            }
        });
        robotList = new ListView<>();
        obstacleList = new ListView<>();
        collectableList = new ListView<>();

        robotList.setItems(FXCollections.observableArrayList(room.robots()));
        obstacleList.setItems(FXCollections.observableArrayList(room.myObstacleslist()));
        collectableList.setItems(FXCollections.observableArrayList(room.myCollectableslist()));

        robotList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                obstacleList.getSelectionModel().clearSelection();
                collectableList.getSelectionModel().clearSelection();
                selectedObstacle = null;
                selectedCollectable = null;
                selectedRobot = newVal;
                highlightObjectOnCanvas(newVal.getPosition(), true);
            }
        });

        obstacleList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                robotList.getSelectionModel().clearSelection();
                collectableList.getSelectionModel().clearSelection();
                selectedRobot = null;
                selectedCollectable = null;
                selectedObstacle = newVal;
                highlightObjectOnCanvas(newVal.getPosition(), false);
            }
        });

        collectableList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                robotList.getSelectionModel().clearSelection();
                obstacleList.getSelectionModel().clearSelection();
                selectedRobot = null;
                selectedObstacle = null;
                selectedCollectable = newVal;
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
        Button btnKeyboardMovement = new Button("Keyboard Movement");
        btnKeyboardMovement.setPrefSize(200, 50);
        btnKeyboardMovement.setOnAction(event -> toggleKeyboardControl());

        VBox vbox = new VBox(10);
        updateDustLabel();
        updateTimeLeft();
        vbox.getChildren().add(0, dustLabel);
        vbox.getChildren().add(1, TimerLabel);
        HBox hboxButtons = new HBox(10, btnCreateRobot, btnCreateObstacle, btnClear, btnStartAut, btnChange, btnDelete,btnKeyboardMovement);
        hboxButtons.setAlignment(Pos.CENTER);
        VBox leftPanel = new VBox(10, new Label("Robots"), robotList);
        VBox rightPanel = new VBox(10, new Label("Obstacles"), obstacleList);
        VBox rightPanel2 = new VBox(10, new Label("Collectables"), collectableList);

        VBox vbox2 = new VBox(10,vbox, hboxButtons, new HBox(10,leftPanel,  canvas,  rightPanel ,rightPanel2) );
        vbox2.setAlignment(Pos.CENTER);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(vbox2, screenBounds.getWidth(), screenBounds.getHeight());
        scene.setOnMouseClicked(e -> {
            if (robotList.isFocused() || obstacleList.isFocused()) {
                System.out.println("Mouse clicked");
                robotList.getSelectionModel().clearSelection();
                obstacleList.getSelectionModel().clearSelection();
                collectableList.getSelectionModel().clearSelection();
                selectedRobot = null;
                selectedObstacle = null;
                selectedCollectable = null;
                drawAllObjects();
            }
        });
        startLogging();
        scene.setOnKeyPressed(this::handleKeyPress);
        primaryStage.setTitle("Room Grid Window");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    private void createRoomFromMap(char[][] map) {
        room = Room.create(CELL_SIZE, CELL_SIZE);
        // Iterate over the map and add objects accordingly
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                char symbol = map[row][col];
                int x = col * CELL_SIZE /map.length  + CELL_SIZE / (2 * map.length); // Adjust according to your needs
                int y = row * CELL_SIZE / map[row].length + CELL_SIZE / (2 * map[row].length); // Adjust according to your needs

                switch (symbol) {
                    case 'X': // Add obstacle
                        placeObject(x,y,CELL_SIZE /map.length);
                        break;
                    case 'R': // Add robot
                        System.out.println("Place robot " + x + " " + y);
                        placeRobot(x,y,40,25,CELL_SIZE /map.length, 10);
                        break;
                    case 'C': // Add robot
                        placeCollectable(x,y,CELL_SIZE /map.length);
                        break;
                }
            }
        }
    }
    private void logMapStateToFile() {
        timeLeft--;
        updateTimeLeft();
        try (PrintWriter writer = new PrintWriter(new FileWriter("map_log.txt", true))) {
            writer.println( map.length + " " + map[0].length);
            for (int row = 0; row < map.length; row++) {
                for (int col = 0; col < map[row].length; col++) {
                    writer.print(map[row][col]);
                }
                writer.println(); // New line for each row
            }
            writer.println();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }
    }
    private void startLogging() {
        int[] seconds = {0}; // Variable to track elapsed time
        File logFile = new File("map_log.txt");
        if (logFile.exists()) {
            logFile.delete();
        }
        // Create a Timeline to trigger logging every second
        timeline[0] = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    updateMap();
                    logMapStateToFile();
                    seconds[0]++;
                    if (seconds[0] >= 60) {
                        timeline[0].stop();
                    }
                })
        );
        timeline[0].setCycleCount(Timeline.INDEFINITE);
        timeline[0].play();
    }
    private void updateMap() {
        int scale = (CELL_SIZE /map.length);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Position PosCheck = new Position( scale * i + scale / 2, scale * j + scale / 2);
                if (room.obstacleAt(PosCheck, 1, null)) {
                    map[j][i] = 'X';
                } else if (room.robotAt(PosCheck, 1, null)) {
                    map[j][i] = 'R';
                }else if (room.collectableAt(PosCheck, 1, null)) {
                    map[j][i] = 'C';
                }else {
                    map[j][i] = '.';
                }
            }
        }
    }
    private void clearCanvas(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        room.clearRobots();
        room.clearObstacles();
        room.clearCollectables();
    }

    private void openRobotDialog() {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), 0);
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), 0);
        Spinner<Integer> spinnerSpeed = new Spinner<>(20, 100, 50);
        Spinner<Integer> spinnerTurnAngle = new Spinner<>(0, 360, 45);
        Spinner<Integer> spinnerDetectionRange = new Spinner<>(1, 100, 10);  // Новый спиннер для дистанции обнаружения

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> {
            placeRobot(spinnerX.getValue(), spinnerY.getValue(), spinnerSpeed.getValue(), spinnerTurnAngle.getValue(), 30, spinnerDetectionRange.getValue());
            dialog.close();
        });

        dialogVbox.getChildren().addAll(
                new Label("X Coordinate:"), spinnerX,
                new Label("Y Coordinate:"), spinnerY,
                new Label("Speed:"), spinnerSpeed,
                new Label("Turn angle:"), spinnerTurnAngle,
                new Label("Detection Range:"), spinnerDetectionRange,  // Добавляем на форму
                btnCreate
        );

        Scene dialogScene = new Scene(dialogVbox, 300, 300);
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
    private void placeRobot(double x, double y,int speed,int angle, int size, int detectionRange) {
        Position pos = new Position(x - size/2 , y - size/2);
        ControlledRobot robot = ControlledRobot.create(room, pos, size,speed, angle, detectionRange);
        if(robot == null)
        {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            robotList.getItems().add(robot);
            System.out.println("Place robot " + pos.getWidth() + " " + pos.getHeight() + " " + size + " " + speed + " " + angle);
            drawRobot(pos.getWidth(), pos.getHeight(), robot,size);
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
    private void placeCollectable(double x, double y, int size) {
        Collectable newCollectable = Collectable.create(room, new Position(x - size/2 , y - size/2), size);
        double b = x- size/2;
        double c = y- size/2;
        if(newCollectable == null)
        {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            collectedExists++;
            collectableList.getItems().add(newCollectable);
            drawCollectable(newCollectable.getPosition().getWidth() , newCollectable.getPosition().getHeight() , size, newCollectable);
        }
    }
    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double x, double y, int size) {
        clearRobotAt(gc, x, y, angle, size);
        gc.save(); // Сохраняем текущее состояние графического контекста
        gc.translate(x + size / 2, y + size / 2); // Перемещаем контекст в центр изображения
        gc.rotate(angle); // Поворачиваем контекст
        gc.translate(-size / 2, -size / 2); // Сдвигаем обратно на половину размера
        gc.drawImage(image, 0, 0, size, size); // Рисуем изображение
        gc.restore(); // Восстанавливаем графический контекст
    }
    private void drawRobot(double x, double y, ControlledRobot robot, int size) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image basicImage = new Image("/images/robot-vacuum-basic.png");
        Image selectedImage = new Image("/images/robot-vacuum-color.png");
        Image robotImage = robot.equals(selectedRobot) ? selectedImage : basicImage;
        drawRotatedImage(gc, robotImage, robot.angle(), x, y, size);

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
    private void drawCollectable(double x, double y, int size, Collectable collectable) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y, size, size);
        if (collectable.equals(selectedCollectable)) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, size, size);
        }
    }
    private void startAutomatic(GraphicsContext gc) {
        if (selectedRobot == null) {
            return;
        }
        ControlledRobot robot = selectedRobot;
        Timeline timeline = robotTimelines.get(selectedRobot);
        System.out.println("startAutomatic " + timeline);
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> moveRobot(robot)));
            timeline.setCycleCount(Timeline.INDEFINITE);
            robotTimelines.put(selectedRobot, timeline);
            timeline.play();
        } else {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
            } else {
                timeline.play();
            }
        }
    }

    private void moveRobot(ControlledRobot robot) {
        double oldX = robot.getPosition().getWidth() ;
        double oldY = robot.getPosition().getHeight() ;
        Position oldPos =  new Position(oldX , oldY);
        double angleInRadians = Math.toRadians(robot.angle());
        int speed = robot.getSpeed();
        double newX = (oldX + Math.cos(angleInRadians) * speed * 0.1);
        double newY = (oldY + Math.sin(angleInRadians) * speed * 0.1);
        Position newPosition = new Position(newX , newY);
        Position PosCheck = new Position(newX - robot.getDetectionRange(), newY - robot.getDetectionRange());
        if (!room.obstacleAt(PosCheck, robot.getSize() + 2 * robot.getDetectionRange(), null) && !room.robotAt(PosCheck, robot.getSize() + 2 * robot.getDetectionRange(), robot) && room.containsPosition(PosCheck, robot.getSize() + 2 * robot.getDetectionRange() )) {
            //clearRobotAt(gc, oldX, oldY,robot.getAngle(), robot.getSize());
            robot.setPosition(newPosition);
            //drawRobot(newX, newY, robot,robot.getSize());
            drawAllObjects();
        } else {
            robot.turn(robot.getTurnAngle());
        }
        if (room.collectableAt(oldPos, robot.getSize(), null)) {
            Collectable collectable = room.getCollectableAt(oldPos,robot.getSize());
            if (collectable != null){
                collected++;
                updateDustLabel();
                room.removeCollectable(collectable);
                collectableList.getItems().remove(collectable);

            }
        }
    }
    private void updateDustLabel() {
        dustLabel.setText("Dust collected: " + collected + "/" + collectedExists);
    }
    private void updateTimeLeft() {
        TimerLabel.setText("Time left: "+timeLeft);
        if (timeLeft == 0){
            endScreenLabel.setText("YOU LOST");
            endScreen();
        }
        if (collectableList.getItems().isEmpty()){
            endScreenLabel.setText("YOU WON");
            endScreen();
        }
    }
    private void clearRobotAt(GraphicsContext gc, double x, double y, int size) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x - 1, y - 1, size + 2, size + 2);
    }
    private void clearRotatedImageAt(GraphicsContext gc, double x, double y, double angle, int size, Color backgroundColor) {
        // Сохраняем текущее состояние графического контекста
        gc.save();

        // Перемещаем контекст в центр изображения и поворачиваем
        gc.translate(x + size / 2, y + size / 2);
        gc.rotate(angle);

        // Очищаем область изображения
        gc.setFill(backgroundColor);
        gc.fillRect(-size / 2 , -size / 2 , size + 4, size + 4);

        // Восстанавливаем графический контекст
        gc.restore();
    }

    private void clearRobotAt(GraphicsContext gc, double x, double y, double angle, int size) {
        clearRotatedImageAt(gc, x, y, angle, size, Color.LIGHTGRAY);
    }

    private void drawAllObjects() {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        System.out.println("drawAllObjects" + room.robots().size() + " " + room.myObstacleslist().size());
        for (ControlledRobot robot : room.robots()) {
            drawRobot(robot.getPosition().getWidth(), robot.getPosition().getHeight(), robot,robot.getSize());
        }
        for (Obstacle obstacle : room.myObstacleslist()) {
            drawObstacle(obstacle.getPosition().getWidth(), obstacle.getPosition().getHeight(), obstacle.getSize(), obstacle);
        }
        for (Collectable collectable : room.myCollectableslist()) {
            drawCollectable(collectable.getPosition().getWidth(), collectable.getPosition().getHeight(), collectable.getSize(), collectable);
        }
    }
    private void highlightObjectOnCanvas(Position pos, boolean isRobot) {
        drawAllObjects();
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        int size = (CELL_SIZE /map.length);
        if (!isRobot){
        gc.strokeRect(pos.getWidth(), pos.getHeight(), size, size); }// Выделение препятствия
    }

    private void handleChange() {
        if (selectedRobot != null) {
            openRobotChangeDialog(selectedRobot);
        } else if (selectedObstacle != null) {
            openObstacleChangeDialog(selectedObstacle);
        }
    }

    private void openRobotChangeDialog(ControlledRobot robot) {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        // Создание спиннеров для установки различных параметров робота
        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), (int) robot.getPosition().getWidth());
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), (int) robot.getPosition().getHeight());
        Spinner<Integer> spinnerSpeed = new Spinner<>(1, 100, robot.getSpeed());
        Spinner<Integer> spinnerOrientationAngle = new Spinner<>(0, 360, robot.getAngle());  // Угол ориентации робота
        Spinner<Integer> spinnerTurnAngle = new Spinner<>(0, 360, robot.getTurnAngle());  // Угол поворота робота
        Spinner<Integer> spinnerDetectionRange = new Spinner<>(1, 100, robot.getDetectionRange());

        Button btnUpdate = new Button("Update");
        btnUpdate.setOnAction(e -> {
            Position newPos = new Position(spinnerX.getValue() - robot.getDetectionRange(), spinnerY.getValue()- robot.getDetectionRange());
            if(room.robotAt(newPos, robot.getSize() + 2*robot.getDetectionRange(), robot) || room.obstacleAt(newPos, robot.getSize()+ 2*robot.getDetectionRange(), null) || !room.containsPosition(newPos, robot.getSize()+ 2*robot.getDetectionRange()))
            {
                JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
            {
                robot.setPosition(new Position(spinnerX.getValue(), spinnerY.getValue()));
            }
            robot.setSpeed(spinnerSpeed.getValue());
            robot.setAngle(spinnerOrientationAngle.getValue());
            robot.setTurnAngle(spinnerTurnAngle.getValue());
            Position PosCheck = new Position(spinnerX.getValue() - spinnerDetectionRange.getValue(), spinnerY.getValue() - spinnerDetectionRange.getValue());
            if (!room.obstacleAt(PosCheck, robot.getSize() + 2 * spinnerDetectionRange.getValue(), null) && !room.robotAt(PosCheck, robot.getSize() + 2 * spinnerDetectionRange.getValue(), robot) && room.containsPosition(PosCheck, robot.getSize() + 2 * spinnerDetectionRange.getValue() )){
                robot.setDetectionRange(spinnerDetectionRange.getValue());
            }
            else {
                JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            drawAllObjects();
            dialog.close();
        });

        dialogVbox.getChildren().addAll(
                new Label("X Coordinate:"), spinnerX,
                new Label("Y Coordinate:"), spinnerY,
                new Label("Speed:"), spinnerSpeed,
                new Label("Orientation Angle:"), spinnerOrientationAngle,
                new Label("Turn Angle:"), spinnerTurnAngle,
                new Label("Detection Range:"), spinnerDetectionRange,
                btnUpdate
        );

        Scene dialogScene = new Scene(dialogVbox, 300, 350);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void openObstacleChangeDialog(Obstacle obstacle) {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), (int) obstacle.getPosition().getWidth());
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), (int) obstacle.getPosition().getHeight());
        Spinner<Integer> spinnerSize = new Spinner<>(1, 100, obstacle.getSize());
        Position pos = new Position(spinnerX.getValue(), spinnerY.getValue());
        if (!room.containsPosition(pos, spinnerSize.getValue()) || room.obstacleAt(pos, spinnerSize.getValue(), null) || room.robotAt(pos, spinnerSize.getValue(), null)) {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Button btnUpdate = new Button("Update");
        btnUpdate.setOnAction(e -> {
            obstacle.setPosition(new Position(spinnerX.getValue(), spinnerY.getValue()));
            obstacle.setSize(spinnerSize.getValue());
            drawAllObjects();
            dialog.close();
        });

        dialogVbox.getChildren().addAll(
                new Label("X Coordinate:"), spinnerX,
                new Label("Y Coordinate:"), spinnerY,
                new Label("Size:"), spinnerSize,
                btnUpdate
        );

        Scene dialogScene = new Scene(dialogVbox, 300, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void handleDelete() {
        if (selectedRobot != null) {
            room.removeRobot(selectedRobot);
            robotList.getItems().remove(selectedRobot);
            robotList.getSelectionModel().clearSelection(); // Сброс выбора
            selectedRobot = null;
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawAllObjects();
        }
        else if (selectedObstacle != null) {
            room.removeObstacle(selectedObstacle);
            obstacleList.getItems().remove(selectedObstacle);
            obstacleList.getSelectionModel().clearSelection(); // Сброс выбора
            selectedObstacle = null;
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawAllObjects();
        }
    }
    private void toggleKeyboardControl() {
        if (selectedRobot != null) {
            if (keyboardControlActive && activeRobot == selectedRobot) {
                keyboardControlActive = false;
                activeRobot = null;
            } else {
                if (activeRobot != null) {
                    keyboardControlActive = false;
                    activeRobot = null;
                }
                keyboardControlActive = true;
                activeRobot = selectedRobot;
                stopAutomaticMovement(activeRobot);
            }
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (keyboardControlActive && activeRobot != null) {
            switch (event.getCode()) {
                case W:
                    moveRobotForward(activeRobot);
                    break;
                case PAGE_UP:
                    moveRobotForward(activeRobot);
                    break;
                case D:
                    activeRobot.turn(activeRobot.getTurnAngle());
                    drawAllObjects();
                    break;
                case A:
                    activeRobot.turn(-1 * activeRobot.getTurnAngle());
                    drawAllObjects();
                    break;
                default:
                    break;
            }
        }
    }
    private void stopAutomaticMovement(ControlledRobot robot) {
        Timeline timeline = robotTimelines.get(robot);
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }
    }

    private void moveRobotForward(ControlledRobot robot) {
        double oldX = robot.getPosition().getWidth() ;
        double oldY = robot.getPosition().getHeight() ;
        Position oldPos =  new Position(oldX , oldY);
        double angleInRadians = Math.toRadians(robot.angle());
        int speed = robot.getSpeed();
        double newX = (oldX + Math.cos(angleInRadians) * speed * 0.1);
        double newY = (oldY + Math.sin(angleInRadians) * speed * 0.1);
        Position newPosition = new Position(newX , newY);
        Position PosCheck = new Position(newX - robot.getDetectionRange(), newY - robot.getDetectionRange());
        if (!room.obstacleAt(PosCheck, robot.getSize() + 2 * robot.getDetectionRange(), null) && !room.robotAt(PosCheck, robot.getSize() + 2 * robot.getDetectionRange(), robot) && room.containsPosition(PosCheck, robot.getSize() + 2 * robot.getDetectionRange() )) {
            //clearRobotAt(gc, oldX, oldY,robot.getAngle(), robot.getSize());
            robot.setPosition(newPosition);
            drawAllObjects();
            //drawRobot(newX, newY, robot,robot.getSize());
        }
        if (room.collectableAt(oldPos, robot.getSize(), null)) {
            Collectable collectable = room.getCollectableAt(oldPos, robot.getSize());
            if (collectable != null) {
                collected++;
                updateDustLabel();
                room.removeCollectable(collectable);
                collectableList.getItems().remove(collectable);
            }
        }
    }

    //private void startContinuousMovement(ControlledRobot robot, double targetX, double targetY) {
     //   double angle = Math.toDegrees(Math.atan2(targetY - robot.getPosition().getHeight(), targetX - robot.getPosition().getWidth()));
     //   robot.setAngle((int) angle);
//
     //   Timeline movementTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> moveTowards(robot, targetX, targetY)));
     //   movementTimeline.setCycleCount(Timeline.INDEFINITE);
     //   robotTimelines.put(robot, movementTimeline);
      //  movementTimeline.play();
    //}

    private void stopContinuousMovement(ControlledRobot robot) {
        Timeline timeline = robotTimelines.get(robot);
        if (timeline != null) {
            timeline.stop();
            robotTimelines.remove(robot);
            drawAllObjects();
        }
    }
    private double calculateAngleToCursor(ControlledRobot robot, double cursorX, double cursorY) {
        double dx = cursorX - robot.getPosition().getWidth();
        double dy = cursorY - robot.getPosition().getHeight();
        return Math.toDegrees(Math.atan2(dy, dx));
    }
    private void moveTowards(ControlledRobot robot, double targetX, double targetY) {
        final Timeline[] timelineHolder = new Timeline[1];
        timelineHolder[0] = robotTimelines.get(robot);
        if (timelineHolder[0] != null) {
            timelineHolder[0].stop();
        }

        timelineHolder[0] = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            double angleToCursor = calculateAngleToCursor(robot, targetX, targetY);
            robot.setAngle((int) angleToCursor);
            double currentX = robot.getPosition().getWidth();
            double currentY = robot.getPosition().getHeight();
            double dx = targetX - currentX;
            double dy = targetY - currentY;
            double distanceToMove = Math.min(robot.getSpeed() * 0.1, Math.sqrt(dx * dx + dy * dy));

            if (distanceToMove < 1) {
                timelineHolder[0].stop();
            } else {
                double angleInRadians = Math.atan2(dy, dx);
                double newX = currentX + Math.cos(angleInRadians) * distanceToMove;
                double newY = currentY + Math.sin(angleInRadians) * distanceToMove;
                Position newPosition = new Position(newX , newY);
                Position PosCheck = new Position(newX - robot.getDetectionRange(), newY - robot.getDetectionRange());
                if (!room.obstacleAt(PosCheck, robot.getSize() + 2 * robot.getDetectionRange(), null) && !room.robotAt(PosCheck, robot.getSize() + 2 * robot.getDetectionRange(), robot) && room.containsPosition(PosCheck, robot.getSize() + 2 * robot.getDetectionRange() )) {
                    //clearRobotAt(gc, currentX, currentY, robot.getAngle(), robot.getSize());
                    robot.setPosition(newPosition);
                    drawAllObjects();
                    //drawRobot(newX, newY, robot,robot.getSize());
                }
            }
        }));
        timelineHolder[0].setCycleCount(Animation.INDEFINITE);
        robotTimelines.put(robot, timelineHolder[0]);
        timelineHolder[0].play();
    }
    private void endScreen() {
        timeline[0].stop();
        clearCanvas(gc);
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        Button btnMenu = new Button("Menu");


        Button btnReplay = new Button("Show Replay");

        dialogVbox.getChildren().addAll(
                endScreenLabel,
                btnMenu,
                btnReplay
        );

        Scene dialogScene = new Scene(dialogVbox, 500, 500);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}