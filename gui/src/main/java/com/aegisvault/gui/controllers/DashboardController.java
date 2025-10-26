package com.aegisvault.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    // Corrected to Text to match FXML's use of Text for gradient application
    @FXML
    private Text titleText; 

    @FXML
    private ImageView medusaLogoView;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button vaultsBtn;

    @FXML
    private Button passwordsBtn;

    @FXML
    private Button generatorBtn; // Corrected fx:id

    @FXML
    private Button securityBtn; // Corrected fx:id

    @FXML
    private Button settingsBtn;

    // Corrected to Text to match FXML's use
    @FXML
    private Text welcomeText; 

    @FXML
    private Label totalVaultsLabel;

    @FXML
    private Label totalPasswordsLabel;

    @FXML
    private Label weakPasswordsLabel;

    @FXML
    private Label strongPasswordsLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox; // Corrected fx:id

    @FXML
    private TableView<PasswordEntry> passwordTable;

    @FXML
    private TableColumn<PasswordEntry, String> siteColumn;

    @FXML
    private TableColumn<PasswordEntry, String> usernameColumn;

    @FXML
    private TableColumn<PasswordEntry, String> categoryColumn;

    @FXML
    private TableColumn<PasswordEntry, String> strengthColumn;

    @FXML
    private TableColumn<PasswordEntry, String> lastModifiedColumn;

    @FXML
    private TableColumn<PasswordEntry, Void> actionsColumn;

    @FXML
    private Button addPasswordBtn;

    @FXML
    private Button logoutBtn;

    private ObservableList<PasswordEntry> passwordData = FXCollections.observableArrayList();
    private ObservableList<PasswordEntry> allPasswordData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Load custom fonts
        Font.loadFont(getClass().getResourceAsStream("/com/aegisvault/gui/fonts/Montserrat.ttf"), 64);
        Font.loadFont(getClass().getResourceAsStream("/com/aegisvault/gui/fonts/TaraType.ttf"), 106);

        // Configure title text with gradient
        configureTitleText();

        // Load Medusa logo
        loadMedusaLogo();

        // Configure welcome text
        configureWelcomeText();

        // Set up button actions (Logout and Add New) and navigation handlers
        setupButtonActions();

        // Initialize filter combo box
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Work", "Personal", "Finance", "Social"));
        filterComboBox.setValue("All");

        // Set up table columns
        siteColumn.setCellValueFactory(new PropertyValueFactory<>("site"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        strengthColumn.setCellValueFactory(new PropertyValueFactory<>("strength"));
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));

        // Set up actions column
        setupActionsColumn();

        // Add sample data
        addSampleData();
        allPasswordData.addAll(passwordData);
        passwordTable.setItems(passwordData);

        // Update statistics
        updateStatistics();

        // Set up search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterPasswords());
        
        // Use setOnAction to filter when combobox value changes
        filterComboBox.setOnAction(event -> filterPasswords()); 
        
        // Ensure dashboard button starts active (using CSS class)
        dashboardBtn.getStyleClass().add("menu-btn-active");
    }
    
    // Reworked menu button setup to remove manual Java style overriding CSS
    private void setupButtonActions() {
        // Set action handlers
        logoutBtn.setOnAction(e -> handleLogout());
        addPasswordBtn.setOnAction(e -> handleAddPassword());
        
        // Placeholder handlers for other navigation buttons
        // NOTE: These now use the correct fx:id fields (generatorBtn, securityBtn)
        vaultsBtn.setOnAction(e -> showFeatureAlert("Vaults"));
        passwordsBtn.setOnAction(e -> showFeatureAlert("Passwords"));
        generatorBtn.setOnAction(e -> showFeatureAlert("OLI Generator"));
        securityBtn.setOnAction(e -> showFeatureAlert("Security Check"));
        settingsBtn.setOnAction(e -> showFeatureAlert("Settings"));
    }
    
    private void configureTitleText() {
        titleText.setFont(Font.font("TaraType", 28));
        titleText.setFill(
            new LinearGradient(
                0, 0, 0, 1,
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
    }

    private void loadMedusaLogo() {
        try {
            Image medusaImage = new Image(getClass().getResourceAsStream("/com/aegisvault/gui/images/medusa.png"));
            medusaLogoView.setImage(medusaImage);
            medusaLogoView.setFitWidth(50);
            medusaLogoView.setFitHeight(50);
            medusaLogoView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load medusa.png: " + e.getMessage());
        }
    }

    private void configureWelcomeText() {
        welcomeText.setFont(Font.font("Montserrat", 32));
        welcomeText.setFill(Color.web("#70C8B7"));
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                // Action buttons rely on CSS classes defined in application.css
                viewBtn.getStyleClass().addAll("action-btn", "view-btn");
                editBtn.getStyleClass().addAll("action-btn", "edit-btn");
                deleteBtn.getStyleClass().addAll("action-btn", "delete-btn");

                viewBtn.setOnAction(e -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    showPasswordDetails(entry);
                });

                editBtn.setOnAction(e -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    editPassword(entry);
                });

                deleteBtn.setOnAction(e -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    deletePassword(entry);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void addSampleData() {
        passwordData.add(new PasswordEntry("Google", "john.doe@gmail.com", "Personal", "Strong", "2024-10-20"));
        passwordData.add(new PasswordEntry("Facebook", "johndoe", "Social", "Medium", "2024-10-15"));
        passwordData.add(new PasswordEntry("Bank of America", "john_doe_123", "Finance", "Strong", "2024-10-25"));
        passwordData.add(new PasswordEntry("LinkedIn", "john.doe", "Work", "Weak", "2024-09-30"));
        passwordData.add(new PasswordEntry("Amazon", "johndoe@email.com", "Personal", "Strong", "2024-10-22"));
        passwordData.add(new PasswordEntry("Netflix", "john.doe", "Personal", "Medium", "2024-10-18"));
        passwordData.add(new PasswordEntry("GitHub", "johndoe123", "Work", "Strong", "2024-10-26"));
    }

    private void updateStatistics() {
        int totalPasswords = allPasswordData.size();
        long weakCount = allPasswordData.stream().filter(p -> p.getStrength().equals("Weak")).count();
        long strongCount = allPasswordData.stream().filter(p -> p.getStrength().equals("Strong")).count();

        totalVaultsLabel.setText("5");
        totalPasswordsLabel.setText(String.valueOf(totalPasswords));
        weakPasswordsLabel.setText(String.valueOf(weakCount));
        strongPasswordsLabel.setText(String.valueOf(strongCount));
    }

    @FXML
    private void handleAddPassword() {
        Dialog<PasswordEntry> dialog = new Dialog<>();
        dialog.setTitle("Add New Password");
        dialog.setHeaderText("Enter password details");

        // Applying dialog styling defined in application.css
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField siteField = new TextField();
        siteField.setPromptText("Website/App");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Personal", "Work", "Finance", "Social");
        categoryCombo.setValue("Personal");

        // Labels inside dialog
        grid.add(new Label("Site:"), 0, 0);
        grid.add(siteField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new PasswordEntry(
                    siteField.getText(),
                    usernameField.getText(),
                    categoryCombo.getValue(),
                    "Strong", // Default strength
                    LocalDate.now().toString()
                );
            }
            return null;
        });

        Optional<PasswordEntry> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            passwordData.add(entry);
            allPasswordData.add(entry);
            updateStatistics();
            filterPasswords();
        });
    }

    private void showPasswordDetails(PasswordEntry entry) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Details");
        alert.setHeaderText(entry.getSite());
        alert.setContentText(
            "Username: " + entry.getUsername() + "\n" +
            "Category: " + entry.getCategory() + "\n" +
            "Strength: " + entry.getStrength() + "\n" +
            "Last Modified: " + entry.getLastModified()
        );
        alert.showAndWait();
    }

    private void editPassword(PasswordEntry entry) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit Password");
        alert.setContentText("Edit functionality for " + entry.getSite() + " (Not yet implemented)");
        alert.showAndWait();
    }

    private void deletePassword(PasswordEntry entry) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Password");
        alert.setHeaderText("Delete password for " + entry.getSite() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            passwordData.remove(entry);
            allPasswordData.remove(entry);
            updateStatistics();
        }
    }

    @FXML
    private void filterPasswords() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();

        ObservableList<PasswordEntry> filteredData = FXCollections.observableArrayList();
        
        for (PasswordEntry entry : allPasswordData) {
            boolean matchesSearch = searchText.isEmpty() || 
                entry.getSite().toLowerCase().contains(searchText) ||
                entry.getUsername().toLowerCase().contains(searchText);
            
            boolean matchesFilter = filterValue.equals("All") || 
                entry.getCategory().equals(filterValue);
            
            if (matchesSearch && matchesFilter) {
                filteredData.add(entry);
            }
        }
        
        passwordTable.setItems(filteredData);
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setContentText("Are you sure you want to logout?");
        
        // Applying alert styling defined in application.css
        alert.getDialogPane().getStyleClass().add("alert");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/aegisvault/gui/fxml/login.fxml")
                );
                Parent root = loader.load();
                
                // Manually load CSS for the new scene since we are not using Main.java for the transition
                String cssPath = getClass().getResource("/com/aegisvault/gui/application.css").toExternalForm();
                
                Stage stage = (Stage) passwordTable.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(cssPath);

                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Placeholder method for features not yet implemented.
     */
    private void showFeatureAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Not Available");
        alert.setHeaderText(null);
        alert.setContentText(featureName + " feature is currently under development. Stay tuned!");
        
        // Applying alert styling defined in application.css
        alert.getDialogPane().getStyleClass().add("alert");
        
        alert.showAndWait();
    }

    // Inner class for Password Entry
    public static class PasswordEntry {
        private String site;
        private String username;
        private String category;
        private String strength;
        private String lastModified;

        public PasswordEntry(String site, String username, String category, String strength, String lastModified) {
            this.site = site;
            this.username = username;
            this.category = category;
            this.strength = strength;
            this.lastModified = lastModified;
        }

        public String getSite() { return site; }
        public void setSite(String site) { this.site = site; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getStrength() { return strength; }
        public void setStrength(String strength) { this.strength = strength; }
        
        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    }
}