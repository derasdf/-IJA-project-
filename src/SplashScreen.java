import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SplashScreen extends Application{
    @Override
    public void start(Stage primaryStage) {

        Button startButton = new Button("Start");
        startButton.setPrefSize(300, 100);

        startButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);


        startButton.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        startButton.setOnAction(e -> {
            System.out.println("Стартовое действие запущено!");
            Application app = new RoomCreationWindow();
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


        Scene scene = new Scene(root, 800, 800);

        scene.getStylesheets().add("style.css");
        primaryStage.setTitle("Стартовое окно");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
