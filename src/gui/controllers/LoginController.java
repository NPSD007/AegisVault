package gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML
    private Text welcomeText;
    
    @FXML
    private Text titleText;
    
    @FXML
    private ImageView medusaView;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Text blankField;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load custom fonts 
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/Montserrat.ttf"), 64);
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/TaraType.ttf"), 106);
        
        // Welcome text
        welcomeText.setFont(Font.font("Montserrat", 32));
        welcomeText.setFill(Color.web("#70C8B7")); // Teal color
        
        //configure blank fields text
        blankField.setFont(Font.font("Montserrat", 21));
        blankField.setFill(Color.web("#FF0000"));
        
        // Configure title text 
        titleText.setFont(Font.font("TaraType", FontWeight.BOLD, 106));
        titleText.setFill(
            new LinearGradient(
                0, 0, 0, 1, // vertical gradient top to bottom
                true, CycleMethod.NO_CYCLE,
                new Stop(0.10, Color.web("#C29226")),
                new Stop(0.25, Color.web("#B27D0F")),
                new Stop(0.39, Color.web("#D9B14A")),
                new Stop(0.53, Color.web("#FADE7B")),
                new Stop(0.60, Color.web("#DAB74F")),
                new Stop(0.76, Color.web("#C7972B"))
            )
        );
        
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setOffsetY(1);
        shadow.setRadius(0);
        shadow.setSpread(0);
        shadow.setColor(Color.rgb(0, 0, 0, 1.0));
        titleText.setEffect(shadow);
        
        // Load Medusa image and set size
        try {
            Image medusaImage = new Image(getClass().getResourceAsStream("/gui/images/medusa.png"));
            medusaView.setImage(medusaImage);
            medusaView.setFitWidth(390);
            medusaView.setFitHeight(390);
            medusaView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load medusa.png: " + e.getMessage());
        }
        
        // Style the input fields 
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(260);
        usernameField.setPrefHeight(55);
        usernameField.setStyle(
        		"-fx-background-color: #3F340B; " +
        			    "-fx-text-fill: #FFF; " +
        			    "-fx-prompt-text-fill: rgba(255,255,255,0.55); " +
        			    "-fx-font-family: 'Montserrat'; " +
        			    "-fx-font-size: 18px; " +
        			    "-fx-border-radius: 10px; " +
        			    "-fx-background-radius: 10px; " +
        			    "-fx-padding: 10px;"
        );
        
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(260);
        passwordField.setPrefHeight(55);
        passwordField.setStyle(
        		"-fx-background-color: #3F340B; " +
        			    "-fx-text-fill: #FFF; " +
        			    "-fx-prompt-text-fill: rgba(255,255,255,0.55); " +
        			    "-fx-font-family: 'Montserrat'; " +
        			    "-fx-font-size: 18px; " +
        			    "-fx-border-radius: 10px; " +
        			    "-fx-background-radius: 10px; " +
        			    "-fx-padding: 10px;"
        );
        //style the login button
        loginButton.setPrefWidth(120);
        loginButton.setPrefHeight(50);
        loginButton.setStyle(
        		"-fx-text-fill: #FFF;" +
        		"-fx-font-family: 'Montserrat';" +
        		"-fx-font-size: 22px;" +
        		"-fx-font-weight: 650;" +
        		"-fx-alignment: center;" +  
        		"-fx-letter-spacing: -0.83px;" +
            	"-fx-background-color: #3F340B;" +
            	"-fx-border-radius: 40px;" +
            	"-fx-background-radius: 40px;" +
            	"-fx-border-width: 2.5px;" +
            	"-fx-border-color: #D27E00;" +
            	"-fx-padding: 7px 22px 7px 22px;" +
            	"-fx-cursor: hand;"
        );

        //login button micro-animation
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
            		"-fx-text-fill: #FFF;" +
                    		"-fx-font-family: 'Montserrat';" +
                    		"-fx-font-size: 22px;" +
                    		"-fx-font-weight: 650;" +
                    		"-fx-alignment: center;" +  
                    		"-fx-letter-spacing: -0.83px;" +
                        	"-fx-background-color: #856900;" +
                        	"-fx-border-radius: 40px;" +
                        	"-fx-background-radius: 40px;" +
                        	"-fx-border-width: 2.5px;" +
                        	"-fx-border-color: #D27E00;" +
                        	"-fx-padding: 7px 22px 7px 22px;" +
                        	"-fx-cursor: hand;"
            );
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
            		"-fx-text-fill: #FFF;" +
                    		"-fx-font-family: 'Montserrat';" +
                    		"-fx-font-size: 22px;" +
                    		"-fx-font-weight: 650;" +
                    		"-fx-alignment: center;" +  
                    		"-fx-letter-spacing: -0.83px;" +
                        	"-fx-background-color: #3F340B;" +
                        	"-fx-border-radius: 40px;" +
                        	"-fx-background-radius: 40px;" +
                        	"-fx-border-width: 2.5px;" +
                        	"-fx-border-color: #D27E00;" +
                        	"-fx-padding: 7px 22px 7px 22px;" +
                        	"-fx-cursor: hand;"
            );
        });
     // Hide error message 
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            blankField.setVisible(false);
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            blankField.setVisible(false);
        });
    }
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            blankField.setVisible(true);
            return;
        }
        blankField.setVisible(false);
        System.out.println("Login attempt - Username: " + username);
        usernameField.clear();
        passwordField.clear();
    }
}
