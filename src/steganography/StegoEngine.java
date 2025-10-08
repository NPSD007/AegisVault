package steganography;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
/**
 * StegoEngine - public facade used by VaultManager/Crypto module.
 *
 * Usage:
 *   StegoOptions opts = StegoOptions.defaultOptions();
 *   byte[] png = StegoEncoder.encodeToPNGBytes(base64AsciiBytes, opts);
 *   // store png bytes in DB blob
 *
 *   // read back:
 *   BufferedImage img = ImageIO.read(new ByteArrayInputStream(pngBytes));
 *   byte[] payload = StegoDecoder.decodeImage(img, opts);
 *   String base64 = new String(payload, StandardCharsets.US_ASCII);
 */
public final class StegoEngine {

    public static byte[] createStegoPNGFromBase64String(String base64Str, StegoOptions opts) throws Exception {
        byte[] ascii = base64Str.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        return StegoEncoder.encodeToPNGBytes(ascii, opts);
    }

    public static String extractBase64StringFromPNGBytes(byte[] pngBytes, StegoOptions opts) throws Exception {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(pngBytes));
        byte[] payload = StegoDecoder.decodeImage(img, opts);
        return new String(payload, java.nio.charset.StandardCharsets.US_ASCII);
    }

    // Quick demo: generate random payloads for 64-char and 88-char Base64-like ASCII, embed & extract and show PNG size
    public static void main(String[] args) throws Exception {
        StegoOptions opts = StegoOptions.defaultOptions();
        opts.fixedSize = 27; // default; change to adaptive if you want auto sizing
        opts.adaptiveSize = false;

        // Example 1: 64-char "Base64" (simulate by random Base64 chars from A-Za-z0-9+/)
        String base64_64 = randomBase64LikeString(64);
        byte[] png64 = createStegoPNGFromBase64String(base64_64, opts);
        System.out.println("64-char base64 -> PNG bytes: " + png64.length + " bytes (image side: " + opts.fixedSize + ")");

        String recovered64 = extractBase64StringFromPNGBytes(png64, opts);
        System.out.println("Recovered equals? " + base64_64.equals(recovered64));

        // Example 2: 88-char
        String base64_88 = randomBase64LikeString(88);
        // choose appropriate fixed size for 88-case if you want to embed in default 27 (27*27*1=729 bits -> 91 bytes capacity)
        // Our default 27x27 supports up to 729 bits => 91 bytes => 87 bytes payload? To be safe, we'll set fixedSize based on needed size:
        int neededBits88 = (88 + 4) * 8;
        int side88 = (int) Math.ceil(Math.sqrt(neededBits88));
        opts.fixedSize = side88; // set to computed minimal square
        opts.adaptiveSize = false; // keep fixed mode but auto-adjusted above

        byte[] png88 = createStegoPNGFromBase64String(base64_88, opts);
        System.out.println("88-char base64 -> PNG bytes: " + png88.length + " bytes (image side: " + opts.fixedSize + ")");
        String recovered88 = extractBase64StringFromPNGBytes(png88, opts);
        System.out.println("Recovered equals? " + base64_88.equals(recovered88));
    }

    private static java.util.Random rnd = new java.util.Random();
    private static final char[] BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static String randomBase64LikeString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(BASE64_CHARS[rnd.nextInt(BASE64_CHARS.length)]);
        return sb.toString();
    }
}
