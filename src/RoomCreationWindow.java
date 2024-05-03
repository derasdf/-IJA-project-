import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RoomCreationWindow extends Application {
    private char[][] map;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Room");

        Button map1 = new Button("Map 1");
        map1.setOnAction(e -> {
            loadMap(System.getProperty("user.dir")+"/src/room_maps/room1.txt"); // Load the map
            launchRoomWindow();
            primaryStage.close();
        });
        Button map2 = new Button("Map 2");
        map2.setOnAction(e -> {
            loadMap(System.getProperty("user.dir")+"/src/room_maps/room2.txt"); // Load the map
            launchRoomWindow();
            primaryStage.close();
        });
        HBox hboxButtons = new HBox(10,map1,map2);
                GridPane grid = new GridPane();
        hboxButtons.setAlignment(Pos.CENTER);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(50);
        grid.setVgap(140);

        grid.add(map1, 0, 3, 2, 1);
        GridPane.setHalignment(map1, HPos.CENTER);
        grid.add(map2, 0, 3, 2, 1);
        GridPane.setHalignment(map2, HPos.CENTER);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(grid, screenBounds.getWidth(), screenBounds.getHeight());
        scene.getStylesheets().add("style.css");
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
