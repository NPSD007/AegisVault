package aes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * AESTest - Standalone test demonstrating AES encryption functionality.
 * This version does NOT require StegoEngine to be present.
 */
public class AESTest {

    public static void main(String[] args) {
        System.out.println("=== AES Engine Standalone Test ===\n");

        try {
            testBasicEncryption();
            testPasswordDerivation();
            testKeyManagement();
            testBase64Integration();
            testSecureWipe();

            System.out.println("\n=== All Tests Passed ===");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testBasicEncryption() throws AESException {
        System.out.println("Test 1: Basic AES Encryption/Decryption");

        AESEngine aes = new AESEngine();
        SecretKey key = aes.generateKey();

        String original = "MySecurePassword123!";
        String encrypted = aes.encryptString(original, key);
        String decrypted = aes.decryptString(encrypted, key);

        System.out.println("  Original:  " + original);
        System.out.println("  Encrypted: " + encrypted.substring(0, Math.min(40, encrypted.length())) + "...");
        System.out.println("  Decrypted: " + decrypted);
        System.out.println("  Match: " + original.equals(decrypted));

        if (!original.equals(decrypted)) {
            throw new AESException("Encryption/Decryption test failed");
        }
        System.out.println();
    }

    private static void testPasswordDerivation() throws AESException {
        System.out.println("Test 2: Password-Based Key Derivation");

        AESEngine aes = new AESEngine();
        char[] password = "MasterPassword123!".toCharArray();
        byte[] salt = aes.generateSalt();

        SecretKey key1 = aes.deriveKeyFromPassword(password, salt);
        SecretKey key2 = aes.deriveKeyFromPassword(password, salt);

        boolean keysMatch = aes.keyToString(key1).equals(aes.keyToString(key2));
        System.out.println("  Keys match with same password/salt: " + keysMatch);
        System.out.println("  Key valid: " + aes.validateKey(key1));
        System.out.println("  Salt length: " + salt.length + " bytes");

        if (!keysMatch) {
            throw new AESException("Key derivation consistency test failed");
        }
        System.out.println();
    }

    private static void testKeyManagement() throws AESException {
        System.out.println("Test 3: Key Serialization and Restoration");

        AESEngine aes = new AESEngine();
        SecretKey originalKey = aes.generateKey();

        String keyString = aes.keyToString(originalKey);
        SecretKey restoredKey = aes.stringToKey(keyString);

        boolean keysMatch = Arrays.equals(originalKey.getEncoded(), restoredKey.getEncoded());
        System.out.println("  Original and restored keys match: " + keysMatch);
        System.out.println("  Restored key valid: " + aes.validateKey(restoredKey));
        System.out.println("  Key string length: " + keyString.length() + " chars");

        if (!keysMatch) {
            throw new AESException("Key serialization test failed");
        }
        System.out.println();
    }

    private static void testBase64Integration() throws AESException {
        System.out.println("Test 4: Base64 Integration (Steganography Ready)");

        AESEngine aes = new AESEngine();
        SecretKey key = aes.generateKey();

        String password = "VerySecretPassword456!";
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        // This Base64 string can be passed to StegoEngine
        String encryptedBase64 = aes.encryptToBase64(passwordBytes, key);
        System.out.println("  Encrypted Base64 length: " + encryptedBase64.length() + " chars");
        System.out.println("  Encrypted Base64 sample: " +
                encryptedBase64.substring(0, Math.min(50, encryptedBase64.length())) + "...");

        // This is what you'd receive back from StegoEngine
        byte[] decryptedBytes = aes.decryptFromBase64(encryptedBase64, key);
        String recoveredPassword = new String(decryptedBytes, StandardCharsets.UTF_8);

        System.out.println("  Original:  " + password);
        System.out.println("  Recovered: " + recoveredPassword);
        System.out.println("  Match: " + password.equals(recoveredPassword));

        if (!password.equals(recoveredPassword)) {
            throw new AESException("Base64 integration test failed");
        }
        System.out.println();
    }

    private static void testSecureWipe() {
        System.out.println("Test 5: Secure Memory Wiping");

        AESEngine aes = new AESEngine();
        byte[] sensitiveData = "SecretPassword123".getBytes(StandardCharsets.UTF_8);

        System.out.println("  Data before wipe: " + new String(sensitiveData, StandardCharsets.UTF_8));

        aes.secureWipe(sensitiveData);

        boolean allZeros = true;
        for (byte b : sensitiveData) {
            if (b != 0) {
                allZeros = false;
                break;
            }
        }

        System.out.println("  Data wiped (all zeros): " + allZeros);
        System.out.println();
    }
}