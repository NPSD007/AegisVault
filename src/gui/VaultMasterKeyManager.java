package gui;

import aes.AESEngine;
import aes.AESException;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 * VaultMasterKeyManager - Handles master key derivation and management
 * Integrates with VaultFileManager to load salt and derive keys from login password
 */
public class VaultMasterKeyManager {
    
    private final VaultFileManager fileManager;
    private final AESEngine aesEngine;
    
    // Cache for current session
    private SecretKey currentMasterKey = null;
    private String currentLoginPassword = null;
    
    public VaultMasterKeyManager() {
        this.fileManager = new VaultFileManager();
        this.aesEngine = new AESEngine();
    }
    
    /**
     * Derive master key from login password using stored salt
     * @param loginPassword The password used to login (e.g., "admin")
     * @return The derived master key for encryption/decryption
     */
    public SecretKey deriveMasterKey(String loginPassword) throws AESException {
        // Check if we already have the key for this password in current session
        if (currentMasterKey != null && loginPassword.equals(currentLoginPassword)) {
            return currentMasterKey;
        }
        
        // Load vault configuration to get salt
        VaultFileManager.VaultConfig config = fileManager.loadVaultConfig();
        byte[] salt = Base64.getDecoder().decode(config.masterKeySalt);
        
        // Derive key using PBKDF2
        SecretKey masterKey = aesEngine.deriveKeyFromPassword(loginPassword.toCharArray(), salt);
        
        // Cache for current session
        currentMasterKey = masterKey;
        currentLoginPassword = loginPassword;
        
        return masterKey;
    }
    
    /**
     * Get the current cached master key (if available)
     */
    public SecretKey getCurrentMasterKey() {
        return currentMasterKey;
    }
    
    /**
     * Clear cached master key (for security when logging out)
     */
    public void clearMasterKey() {
        currentMasterKey = null;
        currentLoginPassword = null;
    }
    
    /**
     * Check if master key is currently cached
     */
    public boolean isMasterKeyAvailable() {
        return currentMasterKey != null;
    }
    
    /**
     * Get the file manager instance
     */
    public VaultFileManager getFileManager() {
        return fileManager;
    }
    
    /**
     * Get the AES engine instance
     */
    public AESEngine getAESEngine() {
        return aesEngine;
    }
}