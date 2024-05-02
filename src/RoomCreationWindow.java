import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RoomCreationWindow extends Application {
    private int rows = 1;
    private int columns = 1;
    private char[][] map;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Room");

        Label titleLabel = new Label("Create room");
        Label rowsLabel = new Label("Rows = ");
        Label columnsLabel = new Label("Columns = ");

        Spinner<Integer> rowsSpinner = new Spinner<>(1, 100, 1);
        Spinner<Integer> columnsSpinner = new Spinner<>(1, 100, 1);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            rows = rowsSpinner.getValue();
            columns = columnsSpinner.getValue();
            System.out.println("Rows: " + rows + ", Columns: " + columns);
            loadMap(System.getProperty("user.dir")+"/src/room_maps/room1.txt"); // Load the map
            launchRoomWindow();
            primaryStage.close();
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(50);
        grid.setVgap(140);

        grid.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);

        grid.add(rowsLabel, 0, 1);
        grid.add(rowsSpinner, 1, 1);
        grid.add(columnsLabel, 0, 2);
        grid.add(columnsSpinner, 1, 2);
        grid.add(createButton, 0, 3, 2, 1);
        GridPane.setHalignment(createButton, HPos.CENTER);

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
