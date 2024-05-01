import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class SplashScreen extends Application{
    @Override
    public void start(Stage primaryStage) {

        Button startButton = new Button("Start");
        startButton.setPrefSize(300, 100);

        startButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);


        startButton.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        startButton.setOnAction(e -> {
            System.out.println("Стартовое действие запущено!");
            Application app = new RoomWindow();;
            Stage newStage = new Stage();
            try {
                app.start(newStage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            primaryStage.close();
        });



        StackPane root = new StackPane();
        root.getChildren().addAll( startButton);
        StackPane.setAlignment(startButton, Pos.CENTER);


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        scene.getStylesheets().add("style.css");
        primaryStage.setTitle("Стартовое окно");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
