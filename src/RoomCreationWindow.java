/*
 * RoomCreationWindow.java
 * @author Aleksandrov Vladimir xaleks03
 *
 */
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class RoomCreationWindow extends Application {
    private char[][] map;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Room");

        // Добавляем иконку "Home"
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
        HBox homeButtonContainer = new HBox(homeButton);
        homeButtonContainer.setAlignment(Pos.TOP_LEFT);
        homeButtonContainer.setStyle("-fx-padding: 20px;");

        Label chooseMapLabel = new Label("Choose the map");
        chooseMapLabel.getStyleClass().add("title-label");

        Button map1 = new Button("Map 1");
        map1.setStyle("-fx-font-size: 30px;");
        map1.setPrefSize(300, 120);
        map1.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        map1.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        map1.setOnAction(e -> {
            loadMap(System.getProperty("user.dir") + "/src/room_maps/room1.txt"); // Load the map
            launchRoomWindow();
            primaryStage.close();
        });

        Button map2 = new Button("Map 2");
        map2.setStyle("-fx-font-size: 30px;");
        map2.setPrefSize(300, 120);
        map2.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        map2.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        map2.setOnAction(e -> {
            loadMap(System.getProperty("user.dir") + "/src/room_maps/room2.txt"); // Load the map
            launchRoomWindow();
            primaryStage.close();
        });

        HBox hboxButtons = new HBox(10, map1, map2);
        hboxButtons.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(20, chooseMapLabel, hboxButtons);
        vbox.setAlignment(Pos.CENTER);

        StackPane mainContent = new StackPane(vbox);
        mainContent.setAlignment(Pos.CENTER);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(homeButtonContainer);
        mainLayout.setCenter(mainContent);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(mainLayout, screenBounds.getWidth(), screenBounds.getHeight());
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void loadMap(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String[] dimensions = br.readLine().split(" ");
            int rows = Integer.parseInt(dimensions[0]);
            int columns = Integer.parseInt(dimensions[1]);
            map = new char[rows][columns];
            for (int i = 0; i < rows; i++) {
                String line = br.readLine();
                map[i] = line.toCharArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchRoomWindow() {
        RoomWindow roomWindow = new RoomWindow();
        roomWindow.setMap(map); // Pass the loaded map to RoomWindow
        Stage stage = new Stage();
        roomWindow.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}