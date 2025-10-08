package steganography;

import java.awt.image.BufferedImage;

public final class StegoDecoder {

    private StegoDecoder() {}

    /**
     * Extract payload bytes from a BufferedImage (square B/W image created by encoder).
     */
    public static byte[] decodeImage(BufferedImage img, StegoOptions opts) throws Exception {
        if (opts == null) opts = StegoOptions.defaultOptions();
        int w = img.getWidth();
        int h = img.getHeight();
        if (w != h) {
            // we accept non-square but algorithm assumes row-major reading up to w*h bits
        }
        int side = Math.max(w, h);
        int capacityBits = w * h;
        boolean[] bits = new boolean[capacityBits];
        int idx = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y) & 0xFFFFFF;
                // treat nonzero as white (1)
                bits[idx++] = (rgb != 0);
            }
        }

        // Read 4-byte header from first 32 bits (MSB-first)
        if (capacityBits < 32) throw new IllegalArgumentException("Image too small to contain header");
        int len = 0;
        for (int i = 0; i < 32; i++) {
            len = (len << 1) | (bits[i] ? 1 : 0);
        }
        if (len < 0) throw new IllegalArgumentException("Invalid length in header: " + len);

        long totalPayloadBits = ((long) len) * 8L;
        long requiredBits = 32L + totalPayloadBits;
        if (requiredBits > capacityBits) {
            throw new IllegalArgumentException("Image does not contain enough bits for declared payload length");
        }

        byte[] out = new byte[4 + len];
        // reconstruct header + payload bytes
        for (int byteIndex = 0; byteIndex < out.length; byteIndex++) {
            int v = 0;
            for (int b = 0; b < 8; b++) {
                int bitPos = byteIndex * 8 + b;
                boolean bit = bits[bitPos];
                v = (v << 1) | (bit ? 1 : 0);
            }
            out[byteIndex] = (byte) v;
        }

        // return only payload (remove header)
        byte[] payload = new byte[len];
        System.arraycopy(out, 4, payload, 0, len);
        return payload;
    }
}
