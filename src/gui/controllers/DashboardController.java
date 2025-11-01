package gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

// Vault integration imports
import gui.VaultFileManager;
import gui.VaultMasterKeyManager;
import aes.IntegratedCryptoEngine;
import aes.AESException;
import javax.crypto.SecretKey;

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
import javafx.scene.layout.GridPane;
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
    private Button generatorBtn; // Corrected fx:id

    @FXML
    private Button securityBtn; // Corrected fx:id

    @FXML
    private Button settingsBtn;

    // Corrected to Text to match FXML's use
    @FXML
    private Text welcomeText; 

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

    // Vault integration components
    private VaultMasterKeyManager vaultManager;
    private IntegratedCryptoEngine cryptoEngine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Load custom fonts
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/Montserrat.ttf"), 64);
        Font.loadFont(getClass().getResourceAsStream("/gui/fonts/TaraType.ttf"), 106);

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

        // Initialize vault system
        initializeVaultSystem();

        // Load existing password entries from vault
        loadPasswordEntries();

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
        generatorBtn.setOnAction(e -> showFeatureAlert("OLI Generator"));
        securityBtn.setOnAction(e -> showFeatureAlert("Security Check"));
        settingsBtn.setOnAction(e -> showFeatureAlert("Settings"));
    }
    
    private void configureTitleText() {
        titleText.setFont(Font.font("TaraType", 36));
        titleText.setTranslateX(-8);
        titleText.setTranslateY(11);
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
        shadow.setOffsetX(1.5);
        shadow.setOffsetY(0.5);
        shadow.setRadius(0);
        shadow.setSpread(0);
        shadow.setColor(Color.rgb(0, 0, 0, 1.0));
        titleText.setEffect(shadow);
    }

    private void loadMedusaLogo() {
        try {
            Image medusaImage = new Image(getClass().getResourceAsStream("/gui/images/medusa.png"));
            medusaLogoView.setImage(medusaImage);
            medusaLogoView.setFitWidth(50);
            medusaLogoView.setFitHeight(50);
            medusaLogoView.setPreserveRatio(true);
            medusaLogoView.setTranslateX(-8);
            medusaLogoView.setTranslateY(9);
        } catch (Exception e) {
            System.err.println("Could not load medusa.png: " + e.getMessage());
        }
    }

    private void configureWelcomeText() {
        welcomeText.setFont(Font.font("Montserrat", 24));
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
                    System.out.println("VIEW ACTION: User attempting to view password for " + entry.getSite() + " (Username: " + entry.getUsername() + ")");
                    DashboardController.this.promptMasterPassword(entry);
                });

                editBtn.setOnAction(e -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    System.out.println("EDIT ACTION: User attempting to edit password for " + entry.getSite() + " (Username: " + entry.getUsername() + ")");
                    editPassword(entry);
                });

                deleteBtn.setOnAction(e -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    System.out.println("DELETE ACTION: User attempting to delete password for " + entry.getSite() + " (Username: " + entry.getUsername() + ")");
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

    /**
     * Initialize vault system components
     */
    private void initializeVaultSystem() {
        try {
            vaultManager = new VaultMasterKeyManager();
            cryptoEngine = new IntegratedCryptoEngine();
            
            // Initialize vault directories (this happens automatically)
            System.out.println("Vault system initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize vault system: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load existing password entries from vault storage
     */
    private void loadPasswordEntries() {
        try {
            // Load password entries from vault file system
            java.util.List<VaultFileManager.PasswordEntry> vaultEntries = 
                vaultManager.getFileManager().loadAllEntries();
            
            // Convert vault entries to GUI password entries
            passwordData.clear();
            allPasswordData.clear();
            
            for (VaultFileManager.PasswordEntry vaultEntry : vaultEntries) {
                PasswordEntry guiEntry = new PasswordEntry(
                    vaultEntry.site,
                    vaultEntry.username,
                    "[Encrypted]", // Don't show actual password in table
                    vaultEntry.category,
                    vaultEntry.lastModified
                );
                // Store the vault entry ID for later retrieval
                guiEntry.setVaultId(vaultEntry.id);
                
                passwordData.add(guiEntry);
                allPasswordData.add(guiEntry);
            }
            
            passwordTable.setItems(passwordData);
            System.out.println("Loaded " + passwordData.size() + " password entries from vault");
            
        } catch (Exception e) {
            System.err.println("Failed to load password entries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        int totalPasswords = allPasswordData.size();

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
                    LocalDate.now().toString()
                );
            }
            return null;
        });

        Optional<PasswordEntry> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            try {
                // Derive master key from login password
                SecretKey masterKey = vaultManager.deriveMasterKey("admin");
                
                // Encrypt and store the password using vault system
                byte[] encryptedImageBytes = cryptoEngine.secureEmbedPassword(
                    entry.getPassword(), masterKey);
                
                // Save to vault file system
                String vaultId = vaultManager.getFileManager().savePasswordEntry(
                    entry.getSite(),
                    entry.getUsername(), 
                    entry.getCategory(),
                    encryptedImageBytes
                );
                
                // Create GUI entry (without showing actual password)
                PasswordEntry guiEntry = new PasswordEntry(
                    entry.getSite(),
                    entry.getUsername(),
                    "[Encrypted]", // Don't show actual password
                    entry.getCategory(),
                    entry.getLastModified()
                );
                guiEntry.setVaultId(vaultId);
                
                // Add to GUI lists
                passwordData.add(guiEntry);
                allPasswordData.add(guiEntry);
                updateStatistics();
                filterPasswords();
                
                System.out.println("ADD SUCCESS: Password encrypted and stored successfully for " + entry.getSite() + " (Username: " + entry.getUsername() + ")");
                
            } catch (Exception e) {
                System.err.println("Failed to save password: " + e.getMessage());
                e.printStackTrace();
                
                // Show error to user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to save password: " + e.getMessage());
                alert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
                alert.showAndWait();
            }
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
                
                // Applying alert styling defined in application.css
                alert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
                
                alert.showAndWait();
            }
        });
    }

    private void showPasswordDetails(PasswordEntry entry) {
        try {
            // Derive master key from login password
            SecretKey masterKey = vaultManager.deriveMasterKey("admin");
            
            // Load and decrypt the password from vault
            VaultFileManager fileManager = vaultManager.getFileManager();
            
            // Find the vault entry by ID
            java.util.List<VaultFileManager.PasswordEntry> vaultEntries = fileManager.loadAllEntries();
            VaultFileManager.PasswordEntry vaultEntry = null;
            for (VaultFileManager.PasswordEntry ve : vaultEntries) {
                if (ve.id.equals(entry.getVaultId())) {
                    vaultEntry = ve;
                    break;
                }
            }
            
            if (vaultEntry == null) {
                throw new RuntimeException("Vault entry not found for ID: " + entry.getVaultId());
            }
            
            // Check if image file is valid
            if (!fileManager.isImageFileValid(vaultEntry.imagePath)) {
                // Handle corrupted image - prompt user to re-enter password
                handleCorruptedPasswordEntry(entry, vaultEntry);
                return;
            }
            
            // Load encrypted image and decrypt password
            byte[] encryptedImageBytes = fileManager.loadEncryptedImage(vaultEntry.imagePath);
            String decryptedPassword = cryptoEngine.secureExtractPassword(encryptedImageBytes, masterKey);
            
            // Log successful password view
            System.out.println("VIEW SUCCESS: Password for " + entry.getSite() + " (Username: " + entry.getUsername() + ") decrypted and displayed successfully");
            
            // Show password details
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Details");
            alert.setHeaderText(entry.getSite());
            alert.setContentText(
                "Username: " + entry.getUsername() + "\n" +
                "Password: " + decryptedPassword + "\n" +
                "Category: " + entry.getCategory() + "\n" +
                "Last Modified: " + entry.getLastModified()
            );
            
            // Applying alert styling defined in application.css
            alert.getDialogPane().getStyleClass().addAll("alert", "alert-information");
            
            alert.showAndWait();
            
        } catch (Exception e) {
            System.err.println("Failed to decrypt password: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Decryption Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to decrypt password: " + e.getMessage());
            alert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
            alert.showAndWait();
        }
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
                
                // Applying alert styling defined in application.css
                alert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
                
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
                PasswordEntry updatedEntry = new PasswordEntry(
                    siteField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    categoryCombo.getValue(),
                    LocalDate.now().toString()
                );
                // Preserve the original vault ID
                updatedEntry.setVaultId(entry.getVaultId());
                System.out.println("EDIT DEBUG: Original vault ID: " + entry.getVaultId());
                System.out.println("EDIT DEBUG: Updated entry vault ID: " + updatedEntry.getVaultId());
                return updatedEntry;
            }
            return null;
        });

        Optional<PasswordEntry> result = dialog.showAndWait();
        result.ifPresent(updatedEntry -> {
            try {
                // Derive master key from login password
                SecretKey masterKey = vaultManager.deriveMasterKey("admin");
                
                // Get the vault file manager
                VaultFileManager fileManager = vaultManager.getFileManager();
                
                // Update the entry in the vault storage
                byte[] encryptedImageBytes = cryptoEngine.secureEmbedPassword(
                    updatedEntry.getPassword(), masterKey);
                
                fileManager.updatePasswordEntry(
                    entry.getVaultId(), // Use the original entry's vault ID
                    updatedEntry.getSite(),
                    updatedEntry.getUsername(),
                    updatedEntry.getCategory(),
                    encryptedImageBytes
                );
                
                // Vault ID is already set in the dialog converter
                
                // Update the entry in the UI lists
                int index = passwordData.indexOf(entry);
                if (index != -1) {
                    passwordData.set(index, updatedEntry);
                    System.out.println("EDIT DEBUG: Updated passwordData at index " + index + " with vault ID: " + updatedEntry.getVaultId());
                }
                int allIndex = allPasswordData.indexOf(entry);
                if (allIndex != -1) {
                    allPasswordData.set(allIndex, updatedEntry);
                    System.out.println("EDIT DEBUG: Updated allPasswordData at index " + allIndex + " with vault ID: " + updatedEntry.getVaultId());
                }
                
                updateStatistics();
                filterPasswords(); // Refresh the filtered view
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Password updated successfully!");
                successAlert.showAndWait();
                
                System.out.println("Password entry updated successfully for: " + updatedEntry.getSite());
                
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update password: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void deletePassword(PasswordEntry entry) {
        // Prompt for master password first
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Master Password Required");
        dialog.setHeaderText("Enter your master password to delete the password for " + entry.getSite());

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
                // Master password correct, now show confirmation dialog
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Delete Password");
                confirmAlert.setHeaderText("Delete password for " + entry.getSite() + "?");
                confirmAlert.setContentText("This action cannot be undone.");

                // Applying alert styling defined in application.css
                confirmAlert.getDialogPane().getStyleClass().addAll("alert", "alert-confirmation");

                Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    try {
                        // Delete from vault file system
                        vaultManager.getFileManager().deletePasswordEntry(entry.getVaultId());
                        
                        // Remove from GUI lists
                        passwordData.remove(entry);
                        allPasswordData.remove(entry);
                        
                        // Update statistics and refresh view
                        updateStatistics();
                        filterPasswords(); // This will refresh the table view
                        
                        System.out.println("DELETE SUCCESS: Password for " + entry.getSite() + " deleted successfully from vault");
                        
                    } catch (Exception e) {
                        System.err.println("DELETE ERROR: Failed to delete password for " + entry.getSite() + ": " + e.getMessage());
                        e.printStackTrace();
                        
                        // Show error to user
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Delete Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Failed to delete password: " + e.getMessage());
                        errorAlert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
                        errorAlert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Password");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect master password.");
                
                // Applying alert styling defined in application.css
                alert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
                
                alert.showAndWait();
            }
        });
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
        alert.getDialogPane().getStyleClass().addAll("alert", "alert-confirmation");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/fxml/login.fxml")
                );
                Parent root = loader.load();
                
                // Manually load CSS for the new scene since we are not using Main.java for the transition
                String cssPath = getClass().getResource("/gui/application.css").toExternalForm();
                
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
        alert.getDialogPane().getStyleClass().addAll("alert", "alert-information");
        
        alert.showAndWait();
    }
    
    /**
     * Handle corrupted password entry by prompting user to re-enter password
     */
    private void handleCorruptedPasswordEntry(PasswordEntry guiEntry, VaultFileManager.PasswordEntry vaultEntry) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Corrupted Password Entry");
        confirmAlert.setHeaderText("Password entry for " + guiEntry.getSite() + " appears to be corrupted.");
        confirmAlert.setContentText("The encrypted password file cannot be read. Would you like to re-enter the password to restore it?");
        confirmAlert.getDialogPane().getStyleClass().addAll("alert", "alert-confirmation");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Show password re-entry dialog
            showPasswordRecoveryDialog(guiEntry, vaultEntry);
        }
    }
    
    /**
     * Show dialog for password recovery
     */
    private void showPasswordRecoveryDialog(PasswordEntry guiEntry, VaultFileManager.PasswordEntry vaultEntry) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Restore Password");
        dialog.setHeaderText("Re-enter the password for " + guiEntry.getSite());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        
        ButtonType restoreButtonType = new ButtonType("Restore", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(restoreButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        
        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == restoreButtonType) {
                return passwordField.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPassword -> {
            try {
                // Derive master key and re-encrypt password
                SecretKey masterKey = vaultManager.deriveMasterKey("admin");
                byte[] newEncryptedImageBytes = cryptoEngine.secureEmbedPassword(newPassword, masterKey);
                
                // Update vault entry
                vaultManager.getFileManager().updatePasswordEntry(
                    vaultEntry.id,
                    vaultEntry.site,
                    vaultEntry.username,
                    vaultEntry.category,
                    newEncryptedImageBytes
                );
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Password Restored");
                successAlert.setContentText("Password has been successfully restored.");
                successAlert.getDialogPane().getStyleClass().addAll("alert", "alert-information");
                successAlert.showAndWait();
                
            } catch (Exception e) {
                System.err.println("Failed to restore password: " + e.getMessage());
                e.printStackTrace();
                
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Restore Failed");
                errorAlert.setContentText("Failed to restore password: " + e.getMessage());
                errorAlert.getDialogPane().getStyleClass().addAll("alert", "alert-error");
                errorAlert.showAndWait();
            }
        });
    }

    // Inner class for Password Entry
    public static class PasswordEntry {
        private String site;
        private String username;
        private String password;
        private String category;
        private String lastModified;
        private String vaultId; // ID for vault storage integration

        public PasswordEntry(String site, String username, String password, String category, String lastModified) {
            this.site = site;
            this.username = username;
            this.password = password;
            this.category = category;
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

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }
        
        public String getVaultId() { return vaultId; }
        public void setVaultId(String vaultId) { this.vaultId = vaultId; }
    }
}