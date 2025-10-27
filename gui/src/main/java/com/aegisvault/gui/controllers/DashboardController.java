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
        titleText.setFont(Font.font("TaraType", 48));
        titleText.setTranslateX(-10);
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
            medusaLogoView.setFitWidth(70);
            medusaLogoView.setFitHeight(70);
            medusaLogoView.setPreserveRatio(true);
            medusaLogoView.setTranslateX(-10);
            medusaLogoView.setTranslateY(-5);
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
                    DashboardController.this.promptMasterPassword(entry);
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
        passwordData.add(new PasswordEntry("Google", "john.doe@gmail.com", "password123", "Personal", "Strong", "2024-10-20"));
        passwordData.add(new PasswordEntry("Facebook", "johndoe", "password456", "Social", "Medium", "2024-10-15"));
        passwordData.add(new PasswordEntry("Bank of America", "john_doe_123", "password789", "Finance", "Strong", "2024-10-25"));
        passwordData.add(new PasswordEntry("LinkedIn", "john.doe", "passwordABC", "Work", "Weak", "2024-09-30"));
        passwordData.add(new PasswordEntry("Amazon", "johndoe@email.com", "passwordDEF", "Personal", "Strong", "2024-10-22"));
        passwordData.add(new PasswordEntry("Netflix", "john.doe", "passwordGHI", "Personal", "Medium", "2024-10-18"));
        passwordData.add(new PasswordEntry("GitHub", "johndoe123", "passwordJKL", "Work", "Strong", "2024-10-26"));
    }

    private void updateStatistics() {
        int totalPasswords = allPasswordData.size();

        totalVaultsLabel.setText("5");
        totalPasswordsLabel.setText(String.valueOf(totalPasswords));
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
                    passwordField.getText(),
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

    private void promptMasterPassword(PasswordEntry entry) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Master Password Required");
        dialog.setHeaderText("Enter your master password to view the password for " + entry.getSite());

        // Applying dialog styling defined in application.css
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType unlockButtonType = new ButtonType("Unlock", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(unlockButtonType, ButtonType.CANCEL);

        PasswordField masterPasswordField = new PasswordField();
        masterPasswordField.setPromptText("Master Password");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Master Password:"), 0, 0);
        grid.add(masterPasswordField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == unlockButtonType) {
                return masterPasswordField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(masterPassword -> {
            // For now, assume any non-empty password is correct (in a real app, verify against stored hash)
            if (masterPassword.equals("admin")) {
                showPasswordDetails(entry);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Password");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect master password.");
                alert.showAndWait();
            }
        });
    }

    private void showPasswordDetails(PasswordEntry entry) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Details");
        alert.setHeaderText(entry.getSite());
        alert.setContentText(
            "Username: " + entry.getUsername() + "\n" +
            "Password: " + entry.getPassword() + "\n" +
            "Category: " + entry.getCategory() + "\n" +
            "Strength: " + entry.getStrength() + "\n" +
            "Last Modified: " + entry.getLastModified()
        );
        alert.showAndWait();
    }

    private void editPassword(PasswordEntry entry) {
        // Prompt for master password first
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Master Password Required");
        dialog.setHeaderText("Enter your master password to edit the password for " + entry.getSite());

        // Applying dialog styling defined in application.css
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType unlockButtonType = new ButtonType("Unlock", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(unlockButtonType, ButtonType.CANCEL);

        PasswordField masterPasswordField = new PasswordField();
        masterPasswordField.setPromptText("Master Password");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Master Password:"), 0, 0);
        grid.add(masterPasswordField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == unlockButtonType) {
                return masterPasswordField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(masterPassword -> {
            // For now, assume "admin" is correct (in a real app, verify against stored hash)
            if (masterPassword.equals("admin")) {
                showEditDialog(entry);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Password");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect master password.");
                alert.showAndWait();
            }
        });
    }

    private void showEditDialog(PasswordEntry entry) {
        Dialog<PasswordEntry> dialog = new Dialog<>();
        dialog.setTitle("Edit Password");
        dialog.setHeaderText("Edit password details for " + entry.getSite());

        // Applying dialog styling defined in application.css
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField siteField = new TextField(entry.getSite());
        siteField.setPromptText("Website/App");
        TextField usernameField = new TextField(entry.getUsername());
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setText(entry.getPassword()); // Pre-fill password
        passwordField.setPromptText("Password");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Personal", "Work", "Finance", "Social");
        categoryCombo.setValue(entry.getCategory());

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
                    passwordField.getText(),
                    categoryCombo.getValue(),
                    "Strong", // Default strength, could be recalculated
                    LocalDate.now().toString()
                );
            }
            return null;
        });

        Optional<PasswordEntry> result = dialog.showAndWait();
        result.ifPresent(updatedEntry -> {
            // Update the entry in the lists
            int index = passwordData.indexOf(entry);
            if (index != -1) {
                passwordData.set(index, updatedEntry);
            }
            int allIndex = allPasswordData.indexOf(entry);
            if (allIndex != -1) {
                allPasswordData.set(allIndex, updatedEntry);
            }
            updateStatistics();
            filterPasswords(); // Refresh the filtered view
        });
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
        private String password;
        private String category;
        private String strength;
        private String lastModified;

        public PasswordEntry(String site, String username, String password, String category, String strength, String lastModified) {
            this.site = site;
            this.username = username;
            this.password = password;
            this.category = category;
            this.strength = strength;
            this.lastModified = lastModified;
        }

        public String getSite() { return site; }
        public void setSite(String site) { this.site = site; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getStrength() { return strength; }
        public void setStrength(String strength) { this.strength = strength; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    }
}