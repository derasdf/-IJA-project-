import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import common.Environment;
import room.Room;

public class RoomCreationWindow extends Application {
    private int rows = 1;
    private int columns = 1;

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
            Environment room = Room.create(rows, columns);
            Application app = new RoomWindow(room);
            Stage newStage = new Stage();
            try {
                app.start(newStage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
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

        // Настройка сцены и отображение
        Scene scene = new Scene(grid, 800, 800);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}