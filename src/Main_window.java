import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main_window extends Application {
    @Override
    public void start(Stage stage) {
        StackPane layout = new StackPane();
        Scene scene = new Scene(layout, 800, 800);

        stage.setTitle("Новое окно");
        stage.setScene(scene);
        stage.show();
    }
}
