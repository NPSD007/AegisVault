package aes;

import steganography.StegoEngine;
import steganography.StegoOptions;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * IntegratedCryptoEngine - Facade combining AES encryption with steganography.
 * This is the main entry point for vault operations requiring both encryption
 * and steganographic hiding.
 *
 * Usage pattern:
 *   IntegratedCryptoEngine engine = new IntegratedCryptoEngine();
 *
 *   // Encrypt and hide
 *   byte[] stegoPNG = engine.secureEmbed(password, masterKey);
 *
 *   // Extract and decrypt
 *   String password = engine.secureExtract(stegoPNG, masterKey);
 */
public final class IntegratedCryptoEngine {

    private final AESEngine aesEngine;

    public IntegratedCryptoEngine() {
        this.aesEngine = new AESEngine();
    }

    // ========================================================================
    // HIGH-LEVEL INTEGRATED OPERATIONS
    // ========================================================================

    /**
     * Securely embed a password: Encrypt with AES, then hide in image.
     *
     * @param password The password to protect
     * @param key The master encryption key
     * @return PNG image bytes containing encrypted password
     */
    public byte[] secureEmbedPassword(String password, SecretKey key)
            throws AESException {
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        return aesEngine.encryptAndEmbed(passwordBytes, key, null);
    }

    /**
     * Securely extract a password: Extract from image, then decrypt.
     *
     * @param pngBytes PNG image containing encrypted password
     * @param key The master encryption key
     * @return The original password
     */
    public String secureExtractPassword(byte[] pngBytes, SecretKey key)
            throws AESException {
        byte[] decryptedBytes = aesEngine.extractAndDecrypt(pngBytes, key, null);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Securely embed arbitrary data with custom options.
     *
     * @param data Raw data to encrypt and hide
     * @param key Encryption key
     * @param aesOpts AES options (null for defaults)
     * @param stegoOpts Steganography options (null for defaults)
     * @return PNG image bytes
     */
    public byte[] secureEmbed(byte[] data, SecretKey key,
                              AESOptions aesOpts, StegoOptions stegoOpts)
            throws AESException {
        if (stegoOpts == null) {
            stegoOpts = StegoOptions.defaultOptions();
        }
        return aesEngine.encryptAndEmbed(data, key, stegoOpts);
    }

    /**
     * Securely extract arbitrary data with custom options.
     *
     * @param pngBytes PNG image containing encrypted data
     * @param key Decryption key
     * @param aesOpts AES options (null for defaults)
     * @param stegoOpts Steganography options (null for defaults)
     * @return Decrypted data
     */
    public byte[] secureExtract(byte[] pngBytes, SecretKey key,
                                AESOptions aesOpts, StegoOptions stegoOpts)
            throws AESException {
        if (stegoOpts == null) {
            stegoOpts = StegoOptions.defaultOptions();
        }
        return aesEngine.extractAndDecrypt(pngBytes, key, stegoOpts);
    }

    // ========================================================================
    // KEY MANAGEMENT DELEGATION
    // ========================================================================

    /**
     * Generate a new master key.
     */
    public SecretKey generateMasterKey() throws AESException {
        return aesEngine.generateKey();
    }

    /**
     * Derive master key from user's master password.
     */
    public SecretKey deriveMasterKeyFromPassword(char[] password, byte[] salt)
            throws AESException {
        return aesEngine.deriveKeyFromPassword(password, salt);
    }

    /**
     * Generate a salt for password-based key derivation.
     */
    public byte[] generateSalt() {
        return aesEngine.generateSalt();
    }

    /**
     * Convert key to storable string format.
     */
    public String masterKeyToString(SecretKey key) {
        return aesEngine.keyToString(key);
    }

    /**
     * Restore key from stored string format.
     */
    public SecretKey stringToMasterKey(String keyString) {
        return aesEngine.stringToKey(keyString);
    }

    // ========================================================================
    // UTILITY METHODS
    // ========================================================================

    /**
     * Calculate estimated PNG size for given data length.
     * Useful for UI progress indicators.
     */
    public int estimateStegoPNGSize(int dataLength) {
        // Encrypted size = data + IV(12) + tag(16)
        int encryptedSize = dataLength + 12 + 16;
        // Base64 expansion ~1.33x
        int base64Size = (int) Math.ceil(encryptedSize * 1.37);
        // Add 4-byte header for stego
        int totalBytes = base64Size + 4;
        // Bits needed
        int bitsNeeded = totalBytes * 8;
        // Square side
        int side = (int) Math.ceil(Math.sqrt(bitsNeeded));
        // PNG overhead approximation (IHDR, IDAT, etc.) ~200 bytes minimum
        return (side * side / 8) + 500; // Conservative estimate
    }

    /**
     * Validate data size against steganography constraints.
     */
    public boolean validateDataSize(int dataLength, StegoOptions opts) {
        if (opts == null) opts = StegoOptions.defaultOptions();
        int encryptedSize = dataLength + 28; // IV + tag
        int base64Size = (int) Math.ceil(encryptedSize * 1.37);
        int totalBits = (base64Size + 4) * 8;
        int requiredSide = (int) Math.ceil(Math.sqrt(totalBits));
        return requiredSide <= opts.maxSide;
    }

    /**
     * Securely wipe sensitive data from memory.
     */
    public void secureWipe(byte[] data) {
        aesEngine.secureWipe(data);
    }
}
