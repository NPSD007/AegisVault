package gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/Montserrat-Regular.ttf"), 64);
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/Montserrat-Light.ttf"), 64);
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/TaraType.ttf"), 106);
        
        // --- RETAINED ORIGINAL STYLE CONFIGURATION ---
        
        // Welcome text
        welcomeText.setFont(Font.font("Montserrat Light", 24));
        welcomeText.setFill(Color.web("#70C8B7")); // Teal color
        welcomeText.setTranslateY(10);
        
        // Configure blank fields text
        blankField.setFont(Font.font("Montserrat Light", 16));
        blankField.setFill(Color.web("#FF0000"));
        
        // Configure title text 
        titleText.setFont(Font.font("TaraType", FontWeight.BOLD, 80));
        titleText.setTranslateY(10);
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
            medusaView.setFitWidth(200);
            medusaView.setFitHeight(200);
            medusaView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load medusa.png: " + e.getMessage());
        }
        
        // Hide error message on key press
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            blankField.setVisible(false);
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            blankField.setVisible(false);
        });
        
        // Add Enter key functionality for auto-login
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin(null);
            }
        });
        
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin(null);
            }
        });
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            blankField.setText("Please fill in all fields!");
            blankField.setVisible(true);
            return;
        }
        
        blankField.setVisible(false);
        
        // Simple authentication logic (User: admin, Pass: admin)
        if (username.equals("admin") && password.equals("admin")) {
            try {
                // CORRECTED: Load the dashboard screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/dashboard.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root);
                
                // Ensure application.css is applied (although Main.java handles this, 
                // it's safer to load it here for scene transitions too)
                String cssPath = getClass().getResource("/gui/application.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
                
                // Fade transition
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                
                stage.setScene(scene);
                fadeIn.play();
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to load dashboard screen: " + e.getMessage());
            }
        } else {
            blankField.setText("Invalid credentials!");
            blankField.setVisible(true);
        }
    }
}