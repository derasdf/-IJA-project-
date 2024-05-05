/*
 * RoomWindow.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Burylov Volodymyr xburyl00
 */
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import common.Environment;
import javafx.stage.StageStyle;
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
import java.io.BufferedReader;
import java.io.FileReader;
public class RoomWindow extends Application {
    private Canvas canvas;
    private Stage primaryStage;
    private final int  CELL_SIZE = 600;
    private int collected = 0;
    private int collectedExists = 0;
    private boolean mouseControlActive = false;
    private int timeLeft = 60;
    Label dustLabel = new Label("");
    Label TimerLabel = new Label("");
    Label endScreenLabel = new Label("YOU");
    private final Map<ControlledRobot, Timeline> robotTimelines = new HashMap<>();
    private boolean keyboardControlActive = false;
    private ControlledRobot activeRobot = null;
    private final Timeline[] timeline = {null}; // Initialize timeline as an array to access it inside the lambda
    private boolean action = false;
    Environment room = Room.create(CELL_SIZE, CELL_SIZE);
    GraphicsContext gc;
    ListView<ControlledRobot> robotList;
    ListView<Obstacle> obstacleList;
    ListView<Collectable> collectableList;
    ControlledRobot selectedRobot;
    Obstacle selectedObstacle;
    Collectable selectedCollectable;
    Image basicObstacleImage = new Image("/images/obstacle-empty.png");
    Image selectedObstacleImage = new Image("/images/obstacle-color.png");
    Image Obstacleimage;
    Image basicRobotImage = new Image("/images/robot-vacuum-basic.png");
    Image selectedRobotImage = new Image("/images/robot-vacuum-color.png");
    Image robotImage;
    boolean setBut = false;
    private char[][] map;
    public void setMap(char[][] map) {
        this.map = map;
    }
    @Override
    public void start(Stage primaryStage) {
        if (map == null) {
            return;
        }
        this.primaryStage = primaryStage;

        // Button to go back to SplashScreen
        Image homeIconImage = new Image(getClass().getResourceAsStream("/images/home-icon.png"));
        ImageView homeIconView = new ImageView(homeIconImage);
        homeIconView.setFitWidth(30);
        homeIconView.setFitHeight(30);
        Button homeButton = new Button();
        homeButton.setPrefSize(60, 60);
        homeButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        homeButton.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        homeButton.setGraphic(homeIconView);
        homeButton.setStyle("-fx-background-color: transparent;");
        homeButton.setOnAction(e -> {
            SplashScreen splashScreen = new SplashScreen();
            Stage newStage = new Stage();
            splashScreen.start(newStage);
            primaryStage.close();
        });

        // Play button
        Button playButton = new Button("Play");
        playButton.setPrefSize(150, 50);
        playButton.setOnAction(e -> {
            setBut = true;
            drawAllObjects();
            action = true;
            updateDustLabel();
            updateTimeLeft();
            startLogging();
            playButton.setVisible(false);
        });

        // Canvas setup
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

        // List views setup
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

        // Creating room
        createRoomFromMap(map);
        int buttonWidth = 250;
        // Buttons on the left
        Button btnCreateRobot = new Button("Create Robot");
        btnCreateRobot.setPrefSize(buttonWidth, 50);
        btnCreateRobot.setOnAction(e -> openRobotDialog());

        Button btnCreateObstacle = new Button("Create Obstacle");
        btnCreateObstacle.setPrefSize(buttonWidth, 50);
        btnCreateObstacle.setOnAction(e -> openObstacleDialog());

        Button btnChange = new Button("Change");
        btnChange.setPrefSize(buttonWidth, 50);
        btnChange.setOnAction(e -> handleChange());

        Button btnDelete = new Button("Delete");
        btnDelete.setPrefSize(buttonWidth, 50);
        btnDelete.setOnAction(e -> handleDelete());

        VBox leftButtons = new VBox(10, btnCreateRobot, btnCreateObstacle, btnChange, btnDelete);
        leftButtons.setAlignment(Pos.CENTER);

        // Buttons on the right
        Button btnStartAut = new Button("Automatic");
        btnStartAut.setPrefSize(buttonWidth, 50);
        btnStartAut.setOnAction(e -> startAutomatic(gc));

        Button btnKeyboardMovement = new Button("Keyboard");
        btnKeyboardMovement.setPrefSize(buttonWidth, 50);
        btnKeyboardMovement.setOnAction(event -> toggleKeyboardControl());

        Button btnMouseMovement = new Button("Mouse");
        btnMouseMovement.setPrefSize(buttonWidth, 50);
        btnMouseMovement.setOnAction(e -> {
            if (selectedRobot != null) {
                if (mouseControlActive) {
                    stopMouseMovement();
                } else {
                    startMouseMovement(selectedRobot);
                }
            }
        });

        Button btnClear = new Button("Clear");
        btnClear.setPrefSize(220, 50);
        btnClear.setPrefSize(buttonWidth, 50);
        btnClear.setOnAction(e -> clearCanvas(gc));

        VBox rightButtons = new VBox(10, btnStartAut, btnKeyboardMovement, btnMouseMovement, btnClear);
        rightButtons.setAlignment(Pos.CENTER);

        // Left and right panels
        VBox leftPanel = new VBox(10, new Label("Robots"), robotList, leftButtons);
        VBox rightPanel = new VBox(10, new Label("Obstacles"), obstacleList, rightButtons);

        // Layout setup
        VBox topPanel = new VBox(10, homeButton);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setSpacing(10);
        topPanel.setPadding(new Insets(10));
        VBox topPanel2 = new VBox(20, playButton,dustLabel,TimerLabel);
        topPanel2.setAlignment(Pos.CENTER);
        topPanel2.setSpacing(10);
        topPanel2.setPadding(new Insets(20));
        HBox mainPanel = new HBox(40, leftPanel, canvas, rightPanel);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(20));

        VBox layout = new VBox(10, topPanel,topPanel2, mainPanel);
        layout.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(new VBox(homeButton, topPanel2));
        root.setCenter(layout);
        BorderPane.setAlignment(homeButton, Pos.TOP_LEFT);
        BorderPane.setMargin(homeButton, new Insets(10));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        scene.setOnMouseClicked(e -> {
            if (robotList.isFocused() || obstacleList.isFocused()) {
                robotList.getSelectionModel().clearSelection();
                obstacleList.getSelectionModel().clearSelection();
                collectableList.getSelectionModel().clearSelection();
                selectedRobot = null;
                selectedObstacle = null;
                selectedCollectable = null;
                drawAllObjects();
            }
        });
        //startLogging();
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Room Grid Window");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    private void createRoomFromMap(char[][] map) {
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

                            placeCollectable(x, y, CELL_SIZE / (map.length * 4));

                        break;
                }
            }
        }
    }
    private void logMapStateToFile() {
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
        Thread loggingThread = new Thread(() -> {
            int[] seconds = {0}; // Variable to track elapsed time
            File logFile = new File("map_log.txt");
            if (logFile.exists()) {
                logFile.delete();
            }
            // Create a Timeline to trigger logging every second
            timeline[0] = new Timeline(
                    new KeyFrame(Duration.seconds(1), e -> {
                        updateMap();
                        timeLeft--;
                        logMapStateToFile();
                        seconds[0]++;
                        if (seconds[0] >= 60) {
                            timeline[0].stop();
                        }
                    })
            );
            timeline[0].setCycleCount(Timeline.INDEFINITE);
            timeline[0].play();
        });

        loggingThread.start();
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
        //gc.setFill(Color.LIGHTGRAY);
        //gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        room.clearRobots();
        room.clearObstacles();
        room.clearCollectables();
        robotList.getItems().clear();
        obstacleList.getItems().clear();
        collectableList.getItems().clear();
    }
    private void startMouseMovement(ControlledRobot robot) {
        stopAutomaticMovement(robot);
        keyboardControlActive = false;
        mouseControlActive = true;
        activeRobot = robot;
        canvas.setOnMousePressed(e -> {
            if (mouseControlActive && e.getButton() == MouseButton.PRIMARY && activeRobot != null) {
                moveTowards(activeRobot, e.getX(), e.getY());
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (mouseControlActive && e.getButton() == MouseButton.PRIMARY && activeRobot != null) {
                moveTowards(activeRobot, e.getX(), e.getY());
            }
        });
        canvas.setOnMouseReleased(e -> {
            if (mouseControlActive && e.getButton() == MouseButton.PRIMARY && activeRobot != null) {
                stopContinuousMovement(activeRobot);
                activeRobot = null;
            }
        });
    }
    private void stopMouseMovement() {
        mouseControlActive = false;
        activeRobot = null;
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        canvas.setOnMouseReleased(null);
    }
    private void openRobotDialog() {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.DECORATED);
        dialog.setTitle(" "); // Оставляем заголовок пустым

        // Создаем спиннеры для полей ввода
        Spinner<Integer> spinnerX = new Spinner<>(0, 600, 0);
        spinnerX.setEditable(true);
        Spinner<Integer> spinnerY = new Spinner<>(0, 600, 0);
        spinnerY.setEditable(true);
        Spinner<Integer> spinnerSpeed = new Spinner<>(20, 100, 50);
        spinnerSpeed.setEditable(true);
        Spinner<Integer> spinnerTurnAngle = new Spinner<>(1, 359, 45);
        spinnerTurnAngle.setEditable(true);
        Spinner<Integer> spinnerDetectionRange = new Spinner<>(1, 100, 10);
        spinnerDetectionRange.setEditable(true);

        // Создаем строки с лейблами и спиннерами
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Label xLabel = new Label("X Coordinate:");
        xLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label yLabel = new Label("Y Coordinate:");
        yLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label speedLabel = new Label("Speed:");
        speedLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label angleLabel = new Label("Turn angle:");
        angleLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label detectionRangeLabel = new Label("Detection Range:");
        detectionRangeLabel.setStyle("-fx-text-fill: #d9b0ff;");

        grid.addRow(0, xLabel, spinnerX);
        grid.addRow(1, yLabel, spinnerY);
        grid.addRow(2, speedLabel, spinnerSpeed);
        grid.addRow(3, angleLabel, spinnerTurnAngle);
        grid.addRow(4, detectionRangeLabel, spinnerDetectionRange);

        // Создаем кнопку "Create"
        Button btnCreate = new Button("Create");
        btnCreate.setStyle("-fx-background-color: #d9b0ff; -fx-text-fill: #643d88;");
        btnCreate.setOnAction(e -> {
            placeRobot(spinnerX.getValue(), spinnerY.getValue(), spinnerSpeed.getValue(), spinnerTurnAngle.getValue(), 30, spinnerDetectionRange.getValue());
            dialog.close();
        });

        // Центрируем кнопку
        HBox createButtonContainer = new HBox(btnCreate);
        createButtonContainer.setAlignment(Pos.CENTER);

        // Основной контейнер с заголовком, формой и кнопкой
        VBox dialogVbox = new VBox(10, grid, createButtonContainer);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setPadding(new Insets(20));
        dialogVbox.setStyle("-fx-background-color: #643d88; ");

        Scene dialogScene = new Scene(dialogVbox, 400, 600);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void openObstacleDialog() {
        Stage dialog = new Stage();


        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), 0);
        spinnerX.setEditable(true);
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), 0);
        spinnerY.setEditable(true);
        Spinner<Integer> spinnerSize = new Spinner<>(10, 100, 10);
        spinnerSize.setEditable(true);

        Button btnCreate = new Button("Create");
        btnCreate.setStyle("-fx-background-color: #d9b0ff; -fx-text-fill: #643d88;");
        btnCreate.setOnAction(e -> {
            placeObject(spinnerX.getValue(),spinnerY.getValue(),spinnerSize.getValue());
            dialog.close();
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Label xLabel = new Label("X Coordinate:");
        xLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label yLabel = new Label("Y Coordinate:");
        yLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label sizeLabel = new Label("Size:");
        sizeLabel.setStyle("-fx-text-fill: #d9b0ff;");
        grid.addRow(0, xLabel, spinnerX);
        grid.addRow(1, yLabel, spinnerY);
        grid.addRow(2, sizeLabel, spinnerSize);
        HBox createButtonContainer = new HBox(btnCreate);
        createButtonContainer.setAlignment(Pos.CENTER);

        // Основной контейнер с заголовком, формой и кнопкой
        VBox dialogVbox = new VBox(10, grid, createButtonContainer);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setPadding(new Insets(20));
        dialogVbox.setStyle("-fx-background-color: #643d88; ");

        Scene dialogScene = new Scene(dialogVbox, 400, 350);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }
    private void placeRobot(double x, double y,int speed,int angle, int size, int detectionRange) {
        Position pos = new Position(x - (size >> 1) , y - (size >> 1));
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
        Obstacle newObstacle = Obstacle.create(room, new Position(x - (size >> 1) , y - (size >> 1)), size);
        double b = x- (size >> 1);
        double c = y- (size >> 1);
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
        Collectable newCollectable = Collectable.create(room, new Position(x - (size >> 1) , y - (size >> 1)), size);
        double b = x- (size >> 1);
        double c = y- (size >> 1);
        if(newCollectable == null)
        {
            JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            collectedExists++;
            collectableList.getItems().add(newCollectable);
            if(setBut) {
                drawCollectable(newCollectable.getPosition().getWidth(), newCollectable.getPosition().getHeight(), size, newCollectable);
            }
        }
    }
    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double x, double y, int size) {
        clearRobotAt(gc, x, y, angle, size);
        gc.save(); // Сохраняем текущее состояние графического контекста
        gc.translate(x + (size >> 1), y + (size >> 1)); // Перемещаем контекст в центр изображения
        gc.rotate(angle); // Поворачиваем контекст
        gc.translate(-(size >> 1), -(size >> 1)); // Сдвигаем обратно на половину размера
        gc.drawImage(image, 0, 0, size, size); // Рисуем изображение
        gc.restore(); // Восстанавливаем графический контекст
    }
    private void drawRobot(double x, double y, ControlledRobot robot, int size) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        robotImage = robot.equals(selectedRobot) ? selectedRobotImage : basicRobotImage;
        drawRotatedImage(gc, robotImage, robot.angle(), x, y, size);

    }

    private void drawObstacle(double x, double y, int size, Obstacle obstacle) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //gc.setFill(Color.RED);
        //gc.fillRect(x, y, size, size);
        //if (obstacle.equals(selectedObstacle)) {
        //    gc.setStroke(Color.YELLOW);
        //    gc.setLineWidth(2);
        //    gc.strokeRect(x, y, size, size);
        //}
        Obstacleimage = obstacle.equals(selectedObstacle) ? selectedObstacleImage : basicObstacleImage;
        gc.drawImage(Obstacleimage, x, y, size, size);
    }
    private void drawCollectable(double x, double y, int size, Collectable collectable) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
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
                stopMouseMovement();

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
        if (action){
            dustLabel.setText("Dust collected: " + collected + "/" + collectedExists);
        }else dustLabel.setText("");
    }
    private void updateTimeLeft() {
        if (action) {
            TimerLabel.setText("Time left: " + timeLeft);
        }else TimerLabel.setText("");
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
        gc.translate(x + (size >> 1), y + (size >> 1));
        gc.rotate(angle);

        // Очищаем область изображения
        gc.setFill(backgroundColor);
        gc.fillRect(-(size >> 1) , -(size >> 1) , size + 4, size + 4);

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
        if(setBut) {
            for (Collectable collectable : room.myCollectableslist()) {
                drawCollectable(collectable.getPosition().getWidth(), collectable.getPosition().getHeight(), collectable.getSize(), collectable);
            }
        }
    }
    private void highlightObjectOnCanvas(Position pos, boolean isRobot) {
        drawAllObjects();
        //gc.setStroke(Color.YELLOW);
        //gc.setLineWidth(2);
        //int size = (CELL_SIZE /map.length);
        //if (!isRobot){
        //gc.strokeRect(pos.getWidth(), pos.getHeight(), size, size); }// Выделение препятствия
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
        // Создание спиннеров для установки различных параметров робота
        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), (int) robot.getPosition().getWidth() + (robot.getSize() >> 1));
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), (int) robot.getPosition().getHeight() + (robot.getSize() >> 1));
        Spinner<Integer> spinnerSpeed = new Spinner<>(1, 100, robot.getSpeed());
        Spinner<Integer> spinnerOrientationAngle = new Spinner<>(0, 360, robot.getAngle());  // Угол ориентации робота
        Spinner<Integer> spinnerTurnAngle = new Spinner<>(1, 359, robot.getTurnAngle());  // Угол поворота робота
        Spinner<Integer> spinnerDetectionRange = new Spinner<>(1, 100, robot.getDetectionRange());
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Label xLabel = new Label("X Coordinate:");
        xLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label yLabel = new Label("Y Coordinate:");
        yLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label speedLabel = new Label("Speed:");
        speedLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label AAngleLabel = new Label("Angle:");
        AAngleLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label angleLabel = new Label("Turn angle:");
        angleLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label detectionRangeLabel = new Label("Detection Range:");
        detectionRangeLabel.setStyle("-fx-text-fill: #d9b0ff;");

        grid.addRow(0, xLabel, spinnerX);
        grid.addRow(1, yLabel, spinnerY);
        grid.addRow(2, speedLabel, spinnerSpeed);
        grid.addRow(3, AAngleLabel, spinnerOrientationAngle);
        grid.addRow(4, angleLabel, spinnerTurnAngle);
        grid.addRow(5, detectionRangeLabel, spinnerDetectionRange);
        Button btnUpdate = new Button("Update");
        btnUpdate.setOnAction(e -> {
            Position newPos = new Position(spinnerX.getValue() - (robot.getSize() >> 1) - robot.getDetectionRange(), spinnerY.getValue() - (robot.getSize() >> 1) - robot.getDetectionRange());
            if(room.robotAt(newPos, robot.getSize() + 2*robot.getDetectionRange(), robot) || room.obstacleAt(newPos, robot.getSize()+ 2*robot.getDetectionRange(), null) || !room.containsPosition(newPos, robot.getSize()+ 2*robot.getDetectionRange()))
            {
                JOptionPane.showMessageDialog(null, "An object already exists at this location", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
            {
                robot.setPosition(new Position(spinnerX.getValue()- (robot.getSize() >> 1), spinnerY.getValue()- (robot.getSize() >> 1)));
            }
            robot.setSpeed(spinnerSpeed.getValue());
            robot.setAngle(spinnerOrientationAngle.getValue());
            robot.setTurnAngle(spinnerTurnAngle.getValue());
            Position PosCheck = new Position(spinnerX.getValue() - spinnerDetectionRange.getValue(), spinnerY.getValue() - spinnerDetectionRange.getValue());
            if (!room.obstacleAt(PosCheck, robot.getSize() + 2 * spinnerDetectionRange.getValue(), null) && !room.robotAt(PosCheck, robot.getSize() + 2 * spinnerDetectionRange.getValue(), robot) && room.containsPosition(PosCheck, robot.getSize() + 2 * spinnerDetectionRange.getValue() )){
                robot.setDetectionRange(spinnerDetectionRange.getValue());
            }
            else {
                JOptionPane.showMessageDialog(null, "You can't make a change with this data", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            drawAllObjects();
            dialog.close();
        });
        HBox createButtonContainer = new HBox(btnUpdate);
        createButtonContainer.setAlignment(Pos.CENTER);

        // Основной контейнер с заголовком, формой и кнопкой
        VBox dialogVbox = new VBox(10, grid, createButtonContainer);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setPadding(new Insets(20));
        dialogVbox.setStyle("-fx-background-color: #643d88; ");



        Scene dialogScene = new Scene(dialogVbox, 400, 450);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void openObstacleChangeDialog(Obstacle obstacle) {
        Stage dialog = new Stage();


        Spinner<Integer> spinnerX = new Spinner<>(0, (int) canvas.getWidth(), (int) obstacle.getPosition().getWidth() + obstacle.getSize()/2);
        spinnerX.setEditable(true);
        Spinner<Integer> spinnerY = new Spinner<>(0, (int) canvas.getHeight(), (int) obstacle.getPosition().getHeight() + obstacle.getSize()/2);
        spinnerY.setEditable(true);
        Spinner<Integer> spinnerSize = new Spinner<>(10, 100, (int) obstacle.getSize());
        spinnerSize.setEditable(true);
        Position pos = new Position(spinnerX.getValue() - (spinnerSize.getValue()>> 1), spinnerY.getValue()- (spinnerSize.getValue()>> 1));
        Button btnUpdate = new Button("Update");
        btnUpdate.setStyle("-fx-background-color: #d9b0ff; -fx-text-fill: #643d88;");
        btnUpdate.setOnAction(e -> {
            if (!room.containsPosition(pos, spinnerSize.getValue()) || room.obstacleAt(pos, spinnerSize.getValue(), obstacle) || room.robotAt(pos, spinnerSize.getValue(), null)) {
                JOptionPane.showMessageDialog(null, "You can't make a change with this data", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else {
                obstacle.setPosition(new Position(spinnerX.getValue() - (spinnerSize.getValue()>> 1) , spinnerY.getValue()- (spinnerSize.getValue()>> 1)));
                obstacle.setSize(spinnerSize.getValue());
                drawAllObjects();
            }
            dialog.close();
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Label xLabel = new Label("X Coordinate:");
        xLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label yLabel = new Label("Y Coordinate:");
        yLabel.setStyle("-fx-text-fill: #d9b0ff;");
        Label sizeLabel = new Label("Size:");
        sizeLabel.setStyle("-fx-text-fill: #d9b0ff;");
        grid.addRow(0, xLabel, spinnerX);
        grid.addRow(1, yLabel, spinnerY);
        grid.addRow(2, sizeLabel, spinnerSize);
        HBox createButtonContainer = new HBox(btnUpdate);
        createButtonContainer.setAlignment(Pos.CENTER);

        // Основной контейнер с заголовком, формой и кнопкой
        VBox dialogVbox = new VBox(10, grid, createButtonContainer);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setPadding(new Insets(20));
        dialogVbox.setStyle("-fx-background-color: #643d88; ");

        Scene dialogScene = new Scene(dialogVbox, 400, 350);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void handleDelete() {
        if (selectedRobot != null) {
            room.removeRobot(selectedRobot);
            robotList.getItems().remove(selectedRobot);
            robotList.getSelectionModel().clearSelection(); // Clear selection
            selectedRobot = null;
        } else if (selectedObstacle != null) {
            room.removeObstacle(selectedObstacle);
            obstacleList.getItems().remove(selectedObstacle);
            obstacleList.getSelectionModel().clearSelection(); // Clear selection
            selectedObstacle = null;
        } else if (selectedCollectable != null) {
            room.removeCollectable(selectedCollectable);
            collectableList.getItems().remove(selectedCollectable);
            collectableList.getSelectionModel().clearSelection(); // Clear selection
            selectedCollectable = null;
        }
        drawAllObjects();
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
                    drawAllObjects();
                    break;
                case PAGE_UP:
                    moveRobotForward(activeRobot);
                    drawAllObjects();
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
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox(10);
        dialogVbox.setAlignment(Pos.CENTER);

        Button btnMenu = new Button("Menu");
        btnMenu.setOnAction(e -> {
            Application app = new RoomCreationWindow();;
            Stage newStage = new Stage();
            try {
                app.start(newStage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            dialog.close();
            primaryStage.close();
            setBut = false;
        });

        Button btnReplay = new Button("Show Replay");
        btnReplay.setOnAction(e -> playReplay());

        dialogVbox.getChildren().addAll(
                endScreenLabel,
                btnMenu,
                btnReplay
        );

        Scene dialogScene = new Scene(dialogVbox, 500, 500);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }
    public void playReplay() {
        clearCanvas(gc);
        Thread fileReaderThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader("map_log.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        int rows = Integer.parseInt(parts[0]);
                        int cols = Integer.parseInt(parts[1]);
                        char[][] map = new char[rows][cols];
                        for (int i = 0; i < rows; i++) {
                            if ((line = reader.readLine()) != null) {
                                map[i] = line.toCharArray(); // Convert string to char array
                            } else {
                                // Handle incomplete map state
                                break;
                            }
                        }
                        // Create room from the map
                        createRoomFromMap(map);
                        drawAllObjects();
                        Thread.sleep(500);
                        clearCanvas(gc);
                    } else {
                        // Handle invalid line format
                        System.err.println("Invalid line format: " + line);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        fileReaderThread.start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}