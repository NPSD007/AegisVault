package aes;

/**
 * AESOptions - Configuration for AES encryption operations.
 * Similar structure to StegoOptions for consistency.
 */
public class AESOptions {

    /**
     * Enable/disable automatic key derivation from password.
     */
    public boolean usePasswordDerivation = true;

    /**
     * Number of PBKDF2 iterations (higher = more secure but slower).
     */
    public int pbkdf2Iterations = 100000;

    /**
     * Salt length in bytes.
     */
    public int saltLength = 32;

    /**
     * Enable/disable Base64 encoding of output.
     */
    public boolean base64Output = true;

    /**
     * Metadata version for compatibility tracking.
     */
    public String version = "v1";

    public AESOptions() {}

    /**
     * Create default AES options matching OWASP recommendations.
     */
    public static AESOptions defaultOptions() {
        AESOptions opts = new AESOptions();
        opts.usePasswordDerivation = true;
        opts.pbkdf2Iterations = 100000;
        opts.saltLength = 32;
        opts.base64Output = true;
        opts.version = "v1";
        return opts;
    }

    /**
     * Create high-security options with increased iterations.
     */
    public static AESOptions highSecurityOptions() {
        AESOptions opts = new AESOptions();
        opts.usePasswordDerivation = true;
        opts.pbkdf2Iterations = 310000; // OWASP 2023 recommendation
        opts.saltLength = 32;
        opts.base64Output = true;
        opts.version = "v1-high";
        return opts;
    }
}
