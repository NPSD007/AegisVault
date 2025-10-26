package aes;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public final class AESEngine {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 100000;
    private static final int SALT_LENGTH = 32;

    private final SecureRandom secureRandom;

    public AESEngine() {
        this.secureRandom = new SecureRandom();
    }


    // KEY GENERATION AND DERIVATION


    /**
     * Generate a new random AES-256 key.
     */
    public SecretKey generateKey() throws AESException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, secureRandom);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new AESException("Failed to generate AES key", e);
        }
    }

    /**
     * Derive an AES-256 key from a password using PBKDF2.
     */
    public SecretKey deriveKeyFromPassword(char[] password, byte[] salt)
            throws AESException {
        try {
            if (salt.length != SALT_LENGTH) {
                throw new AESException("Invalid salt length. Expected: " +
                        SALT_LENGTH + ", Got: " + salt.length);
            }

            PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_SIZE);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            spec.clearPassword();

            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AESException("Failed to derive key from password", e);
        }
    }

    /**
     * Generate a cryptographically secure random salt.
     */
    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Generate a random Initialization Vector for GCM mode.
     */
    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }


    // ENCRYPTION OPERATIONS

    /**
     * Encrypt plaintext data using AES-256-GCM.
     * Format: [IV (12 bytes)][Encrypted Data][Authentication Tag (16 bytes)]
     */
    public byte[] encrypt(byte[] plaintext, SecretKey key) throws AESException {
        try {
            byte[] iv = generateIV();

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] ciphertext = cipher.doFinal(plaintext);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            return byteBuffer.array();
        } catch (Exception e) {
            throw new AESException("Encryption failed", e);
        }
    }

    /**
     * Encrypt a string using AES-256-GCM, returns Base64 encoded result.
     */
    public String encryptString(String plaintext, SecretKey key) throws AESException {
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encrypt(plaintextBytes, key);
        return Base64.getEncoder().encodeToString(encrypted);
    }


    // DECRYPTION OPERATIONS

    /**
     * Decrypt data that was encrypted with AES-256-GCM.
     */
    public byte[] decrypt(byte[] encryptedData, SecretKey key) throws AESException {
        try {
            if (encryptedData.length < GCM_IV_LENGTH) {
                throw new AESException("Invalid encrypted data: too short");
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new AESException("Decryption failed", e);
        }
    }

    /**
     * Decrypt a Base64 encoded encrypted string.
     */
    public String decryptString(String encryptedString, SecretKey key)
            throws AESException {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
        byte[] decrypted = decrypt(encryptedBytes, key);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ========================================================================
    // STEGANOGRAPHY INTEGRATION METHODS
    // ========================================================================

    /**
     * INTEGRATION POINT: Encrypt data and return Base64 string for steganography.
     * This is the primary method to use before calling StegoEngine.
     *
     * Usage with StegoEngine:
     *   AESEngine aes = new AESEngine();
     *   SecretKey key = aes.generateKey();
     *   String encryptedBase64 = aes.encryptToBase64(plainData, key);
     *   byte[] stegoPNG = StegoEngine.createStegoPNGFromBase64String(encryptedBase64, opts);
     */
    public String encryptToBase64(byte[] plaintext, SecretKey key) throws AESException {
        byte[] encrypted = encrypt(plaintext, key);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * INTEGRATION POINT: Decrypt Base64 string extracted from steganography.
     * Use this after extracting Base64 from StegoEngine.
     *
     * Usage with StegoEngine:
     *   String extractedBase64 = StegoEngine.extractBase64StringFromPNGBytes(pngBytes, opts);
     *   AESEngine aes = new AESEngine();
     *   byte[] plainData = aes.decryptFromBase64(extractedBase64, key);
     */
    public byte[] decryptFromBase64(String base64Encrypted, SecretKey key)
            throws AESException {
        byte[] encryptedBytes = Base64.getDecoder().decode(base64Encrypted);
        return decrypt(encryptedBytes, key);
    }

    /**
     * INTEGRATION POINT: Complete encrypt-and-embed workflow.
     * Encrypts data and returns ready-to-store PNG bytes.
     *
     * @param plaintext Raw data to encrypt and hide
     * @param key Encryption key
     * @param stegoOptions Options for steganography (pass null for defaults)
     * @return PNG image bytes containing encrypted data
     */
    public byte[] encryptAndEmbed(byte[] plaintext, SecretKey key,
                                  steganography.StegoOptions stegoOptions)
            throws AESException {
        try {
            // Step 1: Encrypt the data
            String encryptedBase64 = encryptToBase64(plaintext, key);

            // Step 2: Embed into steganographic image
            if (stegoOptions == null) {
                stegoOptions = steganography.StegoOptions.defaultOptions();
            }
            return steganography.StegoEngine.createStegoPNGFromBase64String(
                    encryptedBase64, stegoOptions);
        } catch (Exception e) {
            throw new AESException("Failed to encrypt and embed", e);
        }
    }

    /**
     * INTEGRATION POINT: Complete extract-and-decrypt workflow.
     * Extracts and decrypts data from PNG bytes in one call.
     *
     * @param pngBytes PNG image bytes containing encrypted data
     * @param key Decryption key
     * @param stegoOptions Options for steganography (pass null for defaults)
     * @return Decrypted plaintext data
     */
    public byte[] extractAndDecrypt(byte[] pngBytes, SecretKey key,
                                    steganography.StegoOptions stegoOptions)
            throws AESException {
        try {
            // Step 1: Extract encrypted Base64 from image
            if (stegoOptions == null) {
                stegoOptions = steganography.StegoOptions.defaultOptions();
            }
            String extractedBase64 = steganography.StegoEngine
                    .extractBase64StringFromPNGBytes(pngBytes, stegoOptions);

            // Step 2: Decrypt the data
            return decryptFromBase64(extractedBase64, key);
        } catch (Exception e) {
            throw new AESException("Failed to extract and decrypt", e);
        }
    }

    // ========================================================================
    // UTILITY METHODS
    // ========================================================================

    /**
     * Convert SecretKey to Base64 string for storage.
     * WARNING: Store securely, never in plain text files.
     */
    public String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Convert Base64 string back to SecretKey.
     */
    public SecretKey stringToKey(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * Validate that a key is appropriate for AES-256.
     */
    public boolean validateKey(SecretKey key) {
        return key != null &&
                key.getAlgorithm().equals(ALGORITHM) &&
                key.getEncoded().length == (KEY_SIZE / 8);
    }

    /**
     * Securely wipe sensitive data from memory.
     */
    public void secureWipe(byte[] data) {
        if (data != null) {
            secureRandom.nextBytes(data);
            for (int i = 0; i < data.length; i++) {
                data[i] = 0;
            }
        }
    }
}

// ============================================================================
// File 2: AESException.java
// Location: src/encryption/AESException.java
// ============================================================================


