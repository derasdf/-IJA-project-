/*
 * SplashScreen.java
 * @author Aleksandrov Vladimir xaleks03
 * @author Burylov Volodymyr xburyl00
 */
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Rectangle2D;

public class SplashScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label customTitleLabel = new Label("Vacuum Cleaner Simulator");
        customTitleLabel.getStyleClass().add("custom-title-label");

        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("custom-close-button");
        closeButton.setOnAction(e -> primaryStage.close());

        // Перетаскиваем окно с помощью заголовка
        final Delta dragDelta = new Delta();
        HBox titleBar = new HBox(10, customTitleLabel, new Region(), closeButton);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: #4A90E2;");
        titleBar.setPrefHeight(40);
        titleBar.setMaxWidth(Double.MAX_VALUE);
        titleBar.setOnMousePressed(mouseEvent -> {
            dragDelta.x = primaryStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = primaryStage.getY() - mouseEvent.getScreenY();
        });
        titleBar.setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            primaryStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        Label titleLabel = new Label("Vacuum cleaner simulator");

        titleLabel.getStyleClass().add("title-label");

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 30px;");
        startButton.setPrefSize(300, 120);
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
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 30px;");
        exitButton.setPrefSize(300, 120);
        exitButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        exitButton.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        exitButton.setOnAction(e -> {
            System.out.println("Приложение закрыто!");
            primaryStage.close();
        });

        VBox vbox = new VBox(20, titleLabel, startButton, exitButton);
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.getChildren().add(vbox);
        root.setAlignment(Pos.CENTER);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Стартовое окно");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private static class Delta {
        double x, y;
    }
    public static void main(String[] args) {
        launch(args);
    }
}