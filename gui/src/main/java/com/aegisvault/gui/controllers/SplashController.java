package com.aegisvault.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SplashController implements Initializable {
    
    @FXML
    private Text titleText;
    
    @FXML
    private Text subtitleText;
    
    @FXML
    private ImageView medusaView;
    
    // NEW FXML fields
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label loadingLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load custom font
        Font.loadFont(getClass().getResourceAsStream("/com/aegisvault/gui/fonts/Montserrat.ttf"), 64);
        Font.loadFont(getClass().getResourceAsStream("/com/aegisvault/gui/fonts/TaraType.ttf"), 64);

        // Configure title text (retains custom gradient/shadow)
        configureTitleText();
        
        // Configure subtitle text (retains custom gradient/shadow)
        configureSubtitleText();
        
        // Configure medusa image
        configureMedusaImage();
        
        // NEW: Configure progress bar and loading text
        if (progressBar != null) {
            // Set to indeterminate progress for the initial load
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); 
        }
        if (loadingLabel != null) {
            loadingLabel.setText("Initializing components...");
            // NOTE: Custom styling for the label must be in application.css
        }
        
        // The transition logic is kept in Main.java, so no PauseTransition is needed here.
    }
    
    private void configureTitleText() {
        titleText.setFont(Font.font("TaraType", FontWeight.BOLD, 272));
        titleText.setFill(
            new LinearGradient(
                0, 0, 0, 1, // vertical gradient: top to bottom
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
        shadow.setOffsetX(4);
        shadow.setOffsetY(2);
        shadow.setRadius(0);
        shadow.setSpread(0);
        shadow.setColor(Color.rgb(0, 0, 0, 1.0));
        titleText.setEffect(shadow);
    }
    
    private void configureSubtitleText() {
        subtitleText.setFont(Font.font("Montserrat", 64));
        subtitleText.setFill(
        	    new LinearGradient(
        	        0, 0, 0, 1,   // vertical gradient: top to bottom
        	        true, CycleMethod.NO_CYCLE,
        	        new Stop(0.0, Color.web("#70C8B7")),  // top color (light turquoise)
        	        new Stop(1.0, Color.web("#29917A"))   // bottom color (darker turquoise)
        	    )
        	);
        
        DropShadow subtitleShadow = new DropShadow();
        subtitleShadow.setOffsetX(2);
        subtitleShadow.setOffsetY(1);
        subtitleShadow.setRadius(0);
        subtitleShadow.setSpread(0);
        subtitleShadow.setColor(Color.rgb(0, 0, 0, 1.0));
        subtitleText.setEffect(subtitleShadow);
    }
    
    private void configureMedusaImage() {
        try {
            // Updated to medusa.png (from the new FXML)
            Image medusaImage = new Image(getClass().getResourceAsStream("/com/aegisvault/gui/images/medusa.png"));
            medusaView.setImage(medusaImage);
            medusaView.setFitWidth(400);
            medusaView.setFitHeight(400);
            medusaView.setPreserveRatio(true);
            medusaView.setTranslateY(-30); 

        } catch (Exception e) {
            System.err.println("Could not load medusa.png: " + e.getMessage());
        }
    }
}