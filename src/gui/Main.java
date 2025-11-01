package gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

            // --- IMPLEMENTATION: Load application.css for the splash scene ---
            String cssPath = getClass().getResource("/gui/application.css").toExternalForm();
            splashScene.getStylesheets().add(cssPath);

            // Keep window decorations for standard window controls
            primaryStage.setScene(splashScene);
            primaryStage.setTitle("AegisVault");
            
            // Set application icon
            try {
                // Load multiple icon sizes for better display quality
                Image icon32 = new Image(getClass().getResourceAsStream("/gui/images/medusa.png"));
                Image icon16 = new Image(getClass().getResourceAsStream("/gui/images/medusa20.png"));
                
                primaryStage.getIcons().addAll(icon32, icon16);
            } catch (Exception iconEx) {
                System.err.println("Could not load application icon: " + iconEx.getMessage());
            }

            // Set optimal window size for desktop application
            primaryStage.setWidth(1280);
            primaryStage.setHeight(720);
            primaryStage.setMinWidth(1280);
            primaryStage.setMinHeight(720);
            primaryStage.setResizable(false);
            
            // Center the window on screen
            primaryStage.centerOnScreen();
            
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
                    
                    // --- IMPLEMENTATION: Load application.css for the login scene ---
                    loginScene.getStylesheets().add(cssPath); // Reuse the path variable
                    
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