package com.aegisvault.steganography;

/**
 * StegoOptions - configuration for the stego encoder/decoder.
 */
public class StegoOptions {
    /**
     * If true: compute side = ceil(sqrt((payloadBytes+4)*8))
     * If false: use fixedSize.
     */
    public boolean adaptiveSize = false;

    /**
     * Default fixed size (square). User preferred compact default: 27 (fits 88-char Base64).
     */
    public int fixedSize = 27;

    /**
     * Maximum allowed side (safety cap).
     */
    public int maxSide = 128;

    /**
     * Padding mode - RANDOM (cryptographic random) or ZERO (deterministic zeros).
     */
    public PaddingMode paddingMode = PaddingMode.RANDOM;

    /**
     * Encoding label stored as metadata (informational only).
     */
    public String encoding = "base64";

    /**
     * Version string to embed in metadata (if you store it externally).
     */
    public String version = "v1";

    public enum PaddingMode {
        RANDOM, ZERO
    }

    public StegoOptions() {}

    public static StegoOptions defaultOptions() {
        StegoOptions o = new StegoOptions();
        o.adaptiveSize = false;   // default to fixed 27x27 (you asked small images)
        o.fixedSize = 27;
        o.maxSide = 128;
        o.paddingMode = PaddingMode.RANDOM;
        o.encoding = "base64";
        return o;
    }
}
