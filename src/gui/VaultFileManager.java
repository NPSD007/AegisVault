package gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * VaultFileManager - Handles file system storage for AegisVault
 * Manages JSON metadata, vault configuration, and encrypted image files
 * Uses simple JSON string manipulation instead of external JSON library
 */
public class VaultFileManager {
    
    // Vault directory structure
    private static final String VAULT_BASE_DIR = "vault_data";
    private static final String VAULT_IMAGES_DIR = VAULT_BASE_DIR + "/vault_images";
    private static final String VAULT_METADATA_FILE = VAULT_BASE_DIR + "/vault_metadata.json";
    private static final String VAULT_CONFIG_FILE = VAULT_BASE_DIR + "/vault_config.json";
    
    private final Path vaultBasePath;
    private final Path vaultImagesPath;
    private final Path metadataPath;
    private final Path configPath;
    
    public VaultFileManager() {
        this.vaultBasePath = Paths.get(VAULT_BASE_DIR);
        this.vaultImagesPath = Paths.get(VAULT_IMAGES_DIR);
        this.metadataPath = Paths.get(VAULT_METADATA_FILE);
        this.configPath = Paths.get(VAULT_CONFIG_FILE);
        
        initializeVaultDirectories();
    }
    
    /**
     * Initialize vault directory structure if it doesn't exist
     */
    private void initializeVaultDirectories() {
        try {
            // Create directories if they don't exist
            Files.createDirectories(vaultBasePath);
            Files.createDirectories(vaultImagesPath);
            
            // Create empty metadata file if it doesn't exist
            if (!Files.exists(metadataPath)) {
                saveEmptyMetadata();
            }
            
            // Create vault config if it doesn't exist
            if (!Files.exists(configPath)) {
                generateAndSaveVaultConfig();
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize vault directories", e);
        }
    }
    
    /**
     * Save empty metadata file
     */
    private void saveEmptyMetadata() throws IOException {
        String emptyJson = "{\"entries\":[]}";
        Files.write(metadataPath, emptyJson.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Generate initial vault configuration with master key salt
     */
    private void generateAndSaveVaultConfig() throws IOException {
        // Generate cryptographically secure salt
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        byte[] saltBytes = new byte[32]; // 256-bit salt
        secureRandom.nextBytes(saltBytes);
        String saltBase64 = Base64.getEncoder().encodeToString(saltBytes);
        
        String configJson = String.format(
            "{\"masterKeySalt\":\"%s\",\"version\":\"v1\",\"created\":\"%s\"}",
            saltBase64,
            java.time.LocalDateTime.now().toString()
        );
        
        Files.write(configPath, configJson.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Load vault configuration
     */
    public VaultConfig loadVaultConfig() {
        try {
            String configJson = Files.readString(configPath, StandardCharsets.UTF_8);
            return parseVaultConfig(configJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vault configuration", e);
        }
    }
    
    /**
     * Parse vault config from JSON string
     */
    private VaultConfig parseVaultConfig(String json) {
        VaultConfig config = new VaultConfig();
        
        // Extract masterKeySalt
        int saltStart = json.indexOf("\"masterKeySalt\":\"") + 17;
        int saltEnd = json.indexOf("\"", saltStart);
        config.masterKeySalt = json.substring(saltStart, saltEnd);
        
        // Extract version
        int versionStart = json.indexOf("\"version\":\"") + 11;
        int versionEnd = json.indexOf("\"", versionStart);
        config.version = json.substring(versionStart, versionEnd);
        
        // Extract created
        int createdStart = json.indexOf("\"created\":\"") + 11;
        int createdEnd = json.indexOf("\"", createdStart);
        config.created = json.substring(createdStart, createdEnd);
        
        return config;
    }
    
    /**
     * Load all password entries from metadata
     */
    public List<PasswordEntry> loadAllEntries() {
        try {
            String metadataJson = Files.readString(metadataPath, StandardCharsets.UTF_8);
            return parsePasswordEntries(metadataJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load password entries", e);
        }
    }
    
    /**
     * Parse password entries from JSON string
     */
    private List<PasswordEntry> parsePasswordEntries(String json) {
        List<PasswordEntry> entries = new ArrayList<>();
        
        // Find the entries array
        int entriesStart = json.indexOf("\"entries\":[") + 11;
        int entriesEnd = json.lastIndexOf("]");
        
        if (entriesStart == 10 || entriesEnd == -1) { // entries array not found or empty
            return entries;
        }
        
        String entriesJson = json.substring(entriesStart, entriesEnd);
        
        // Split entries by },{ pattern
        String[] entryStrings = entriesJson.split("\\},\\{");
        
        for (String entryStr : entryStrings) {
            if (entryStr.trim().isEmpty()) continue;
            
            // Clean up the entry string
            entryStr = entryStr.replace("{", "").replace("}", "");
            
            PasswordEntry entry = parsePasswordEntry(entryStr);
            if (entry != null) {
                entries.add(entry);
            }
        }
        
        return entries;
    }
    
    /**
     * Parse individual password entry
     */
    private PasswordEntry parsePasswordEntry(String entryStr) {
        try {
            PasswordEntry entry = new PasswordEntry();
            
            // Extract id
            int idStart = entryStr.indexOf("\"id\":\"") + 6;
            int idEnd = entryStr.indexOf("\"", idStart);
            entry.id = entryStr.substring(idStart, idEnd);
            
            // Extract site
            int siteStart = entryStr.indexOf("\"site\":\"") + 8;
            int siteEnd = entryStr.indexOf("\"", siteStart);
            entry.site = entryStr.substring(siteStart, siteEnd);
            
            // Extract username
            int usernameStart = entryStr.indexOf("\"username\":\"") + 12;
            int usernameEnd = entryStr.indexOf("\"", usernameStart);
            entry.username = entryStr.substring(usernameStart, usernameEnd);
            
            // Extract category
            int categoryStart = entryStr.indexOf("\"category\":\"") + 12;
            int categoryEnd = entryStr.indexOf("\"", categoryStart);
            entry.category = entryStr.substring(categoryStart, categoryEnd);
            
            // Extract lastModified
            int modifiedStart = entryStr.indexOf("\"lastModified\":\"") + 16;
            int modifiedEnd = entryStr.indexOf("\"", modifiedStart);
            entry.lastModified = entryStr.substring(modifiedStart, modifiedEnd);
            
            // Extract imagePath
            int pathStart = entryStr.indexOf("\"imagePath\":\"") + 13;
            int pathEnd = entryStr.indexOf("\"", pathStart);
            entry.imagePath = entryStr.substring(pathStart, pathEnd);
            
            return entry;
        } catch (Exception e) {
            System.err.println("Failed to parse password entry: " + entryStr);
            return null;
        }
    }
    
    /**
     * Save password entry with encrypted image
     */
    public String savePasswordEntry(String site, String username, String category, byte[] encryptedImageBytes) {
        try {
            // Generate unique ID for this entry
            String entryId = UUID.randomUUID().toString();
            String imagePath = VAULT_IMAGES_DIR + "/" + entryId + ".png";
            
            // Save encrypted image to file
            Files.write(Paths.get(imagePath), encryptedImageBytes);
            
            // Create password entry metadata
            PasswordEntry entry = new PasswordEntry();
            entry.id = entryId;
            entry.site = site;
            entry.username = username;
            entry.category = category;
            entry.lastModified = java.time.LocalDate.now().toString();
            entry.imagePath = imagePath;
            
            // Load existing entries and add new one
            List<PasswordEntry> entries = loadAllEntries();
            entries.add(entry);
            
            // Save updated metadata
            saveAllEntries(entries);
            
            return entryId;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to save password entry", e);
        }
    }
    
    /**
     * Save all entries to metadata file
     */
    private void saveAllEntries(List<PasswordEntry> entries) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\"entries\":[");
        
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) json.append(",");
            PasswordEntry entry = entries.get(i);
            json.append(String.format(
                "{\"id\":\"%s\",\"site\":\"%s\",\"username\":\"%s\",\"category\":\"%s\",\"lastModified\":\"%s\",\"imagePath\":\"%s\"}",
                entry.id, entry.site, entry.username, entry.category, entry.lastModified, entry.imagePath
            ));
        }
        
        json.append("]}");
        
        Files.write(metadataPath, json.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Load encrypted image bytes for a password entry
     */
    public byte[] loadEncryptedImage(String imagePath) throws IOException {
        return Files.readAllBytes(Paths.get(imagePath));
    }
    
    /**
     * Update an existing password entry
     */
    public void updatePasswordEntry(String entryId, String site, String username, String category, byte[] newEncryptedImageBytes) {
        try {
            List<PasswordEntry> entries = loadAllEntries();
            
            // Find and update the entry
            for (PasswordEntry entry : entries) {
                if (entry.id.equals(entryId)) {
                    entry.site = site;
                    entry.username = username;
                    entry.category = category;
                    entry.lastModified = java.time.LocalDate.now().toString();
                    
                    // Update the encrypted image if provided
                    if (newEncryptedImageBytes != null) {
                        Files.write(Paths.get(entry.imagePath), newEncryptedImageBytes);
                    }
                    break;
                }
            }
            
            // Save updated metadata
            saveAllEntries(entries);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to update password entry", e);
        }
    }
    
    /**
     * Delete a password entry and its associated image
     */
    public void deletePasswordEntry(String entryId) {
        try {
            List<PasswordEntry> entries = loadAllEntries();
            
            // Find and remove the entry
            entries.removeIf(entry -> {
                if (entry.id.equals(entryId)) {
                    // Delete the associated image file
                    try {
                        Files.deleteIfExists(Paths.get(entry.imagePath));
                    } catch (IOException e) {
                        System.err.println("Warning: Could not delete image file: " + entry.imagePath);
                    }
                    return true;
                }
                return false;
            });
            
            // Save updated metadata
            saveAllEntries(entries);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete password entry", e);
        }
    }
    
    /**
     * Check if an image file exists and is readable
     */
    public boolean isImageFileValid(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            return Files.exists(path) && Files.isReadable(path) && Files.size(path) > 0;
        } catch (IOException e) {
            return false;
        }
    }
    
    // ======================== Data Classes ========================
    
    /**
     * Password entry metadata
     */
    public static class PasswordEntry {
        public String id;
        public String site;
        public String username;
        public String category;
        public String lastModified;
        public String imagePath;
        
        // Default constructor
        public PasswordEntry() {}
        
        // Constructor for easy creation
        public PasswordEntry(String id, String site, String username, String category, String lastModified, String imagePath) {
            this.id = id;
            this.site = site;
            this.username = username;
            this.category = category;
            this.lastModified = lastModified;
            this.imagePath = imagePath;
        }
    }
    
    /**
     * Vault configuration
     */
    public static class VaultConfig {
        public String masterKeySalt;
        public String version;
        public String created;
    }
}