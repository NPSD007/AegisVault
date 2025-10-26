package aes;

/**
 * Custom exception for AES encryption/decryption operations.
 */
public class AESException extends Exception {

    public AESException(String message) {
        super(message);
    }

    public AESException(String message, Throwable cause) {
        super(message, cause);
    }
}
