package steganography;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import javax.imageio.ImageIO;

/**
 * StegoEncoder - encode byte[] -> square black/white image (PNG-ready).
 *
 * Mapping:
 *  - 4-byte big-endian length header (payload length in bytes)
 *  - then payload bytes
 *  - then pad remaining pixels to fill side*side bits with random or zeros
 *
 * Bit order: MSB-first inside each byte.
 * Pixel mapping: 0 -> black (0), 1 -> white (255)
 */
public final class StegoEncoder {

    private static final SecureRandom RNG = new SecureRandom();

    private StegoEncoder() {}

    /**
     * Encode payload bytes into a PNG byte[] using options.
     * Returns PNG byte[].
     */
    public static byte[] encodeToPNGBytes(byte[] payload, StegoOptions opts) throws Exception {
        BufferedImage img = encodeToImage(payload, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    /**
     * Core: build the BufferedImage for the payload.
     */
    public static BufferedImage encodeToImage(byte[] payload, StegoOptions opts) throws Exception {
        if (opts == null) opts = StegoOptions.defaultOptions();

        int totalBytes = payload.length + 4; // header + payload
        long totalBitsNeeded = ((long) totalBytes) * 8L;
        if (totalBitsNeeded <= 0) throw new IllegalArgumentException("Invalid payload size");

        int side;
        if (opts.adaptiveSize) {
            side = (int) Math.ceil(Math.sqrt(totalBitsNeeded));
        } else {
            side = opts.fixedSize;
            // but ensure fixed side is large enough
            long capacityBits = (long) side * (long) side;
            if (capacityBits < totalBitsNeeded) {
                // try to increase to minimum required up to maxSide
                side = (int) Math.ceil(Math.sqrt(totalBitsNeeded));
            }
        }

        if (side > opts.maxSide) {
            throw new IllegalArgumentException("Payload too large for configured maxSide: required side=" + side + " maxSide=" + opts.maxSide);
        }

        long capacityBits = (long) side * (long) side;
        if (capacityBits < totalBitsNeeded) {
            throw new IllegalArgumentException("Computed capacity less than needed: capacityBits=" + capacityBits + " needed=" + totalBitsNeeded);
        }

        // Build buffer: 4-byte big-endian length header followed by payload
        byte[] buffer = new byte[4 + payload.length];
        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN);
        bb.putInt(payload.length);
        bb.put(payload);

        // Convert buffer to bitstream (MSB-first)
        long capacity = capacityBits; // number of bits available
        boolean[] bits = new boolean[(int) capacity]; // safe because capacity <= maxSide^2 <= (128^2)=16384
        int bitIndex = 0;
        for (int i = 0; i < buffer.length; i++) {
            int v = buffer[i] & 0xFF;
            for (int b = 0; b < 8; b++) {
                boolean bit = ((v >> (7 - b)) & 1) == 1;
                bits[bitIndex++] = bit;
            }
        }

        // pad remaining bits
        if (opts.paddingMode == StegoOptions.PaddingMode.RANDOM) {
            while (bitIndex < capacity) {
                bits[bitIndex++] = RNG.nextBoolean();
            }
        } else {
            while (bitIndex < capacity) bits[bitIndex++] = false;
        }

        // Create a black & white image (TYPE_BYTE_BINARY ensures 1-bit palette).
        BufferedImage img = new BufferedImage(side, side, BufferedImage.TYPE_BYTE_BINARY);
        int idx = 0;
        for (int y = 0; y < side; y++) {
            for (int x = 0; x < side; x++) {
                boolean bit = bits[idx++];
                int pixel = bit ? 0xFFFFFF : 0x000000; // white or black
                img.setRGB(x, y, pixel);
            }
        }

        return img;
    }
}
