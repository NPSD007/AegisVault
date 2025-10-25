package gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load splash screen first
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/gui/fxml/splash.fxml"));
            StackPane splashRoot = splashLoader.load();
            Scene splashScene = new Scene(splashRoot);
            splashScene.setFill(javafx.scene.paint.Color.rgb(3,8,9,1));

            // Remove window decorations
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(splashScene);
            primaryStage.setTitle("AegisVault");
            
            // Set fixed size and center
            primaryStage.setWidth(1920);
            primaryStage.setHeight(1080);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            
            primaryStage.show();

            // Pause splash for 3 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(3));

            // Fade out splash
            FadeTransition fadeOutSplash = new FadeTransition(Duration.millis(600), splashRoot);
            fadeOutSplash.setFromValue(1.0);
            fadeOutSplash.setToValue(0.0);

            SequentialTransition splashSequence = new SequentialTransition(pause, fadeOutSplash);
            splashSequence.setOnFinished(e -> {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/gui/fxml/login.fxml"));
                    StackPane loginRoot = loginLoader.load();
                    Scene loginScene = new Scene(loginRoot);
                    loginScene.setFill(javafx.scene.paint.Color.rgb(3,8,9,1));
                    
                    primaryStage.setScene(loginScene);

                    // Fade in login
                    loginRoot.setOpacity(0.0);
                    FadeTransition fadeInLogin = new FadeTransition(Duration.millis(1000), loginRoot);
                    fadeInLogin.setFromValue(0.0);
                    fadeInLogin.setToValue(1.0);
                    fadeInLogin.play();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            splashSequence.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
