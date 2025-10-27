package com.aegisvault.steganography;

import java.nio.charset.StandardCharsets;

public class StegoTest {

    public static void main(String[] args) {
        String inputText = "This is a test string for steganography!";
        String outputImage = "output_image.png";

        try {
            // Convert string to Base64 (required by the API)
            String base64Text = java.util.Base64.getEncoder().encodeToString(
                inputText.getBytes(StandardCharsets.UTF_8)
            );
            
            // Convert Base64 string to image (PNG with embedded data)
            StegoOptions options = StegoOptions.defaultOptions();
            byte[] imageBytes = StegoEngine.createStegoPNGFromBase64String(base64Text, options);

            // Save image to file
            java.nio.file.Files.write(java.nio.file.Paths.get(outputImage), imageBytes);
            System.out.println("Steganographic image saved: " + outputImage);

            // Read image from file
            byte[] loadedImageBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(outputImage));

            // Convert image back to Base64 string
            String extractedBase64 = StegoEngine.extractBase64StringFromPNGBytes(loadedImageBytes, options);
            
            // Decode Base64 back to original text
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(extractedBase64);
            String extractedText = new String(decodedBytes, StandardCharsets.UTF_8);

            System.out.println("Original text: " + inputText);
            System.out.println("Extracted text: " + extractedText);
            System.out.println("Test " + (inputText.equals(extractedText) ? "PASSED" : "FAILED"));
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}