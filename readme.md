# AegisVault Password Manager

AegisVault is a secure Java-based password manager that leverages advanced encryption and steganography techniques to protect your sensitive data.

## Features

- **Triple-Layer AES Encryption:**  
    - Encrypts passwords using CryptoX AES at three levels:  
        - **AES Encryption (Base64)**
        - **Salted Hashing (Base64)**
        - **Encrypted Password (Base64)**
- **Base64 to Binary Conversion:**  
    - Converts encrypted Base64 strings to binary bits for added security.
- **Steganography:**  
    - Embeds binary data into images using steganography.
- **Image Encoding:**  
    - Encoded images store your encrypted credentials, making data extraction extremely difficult.

## How It Works

1. **Password Input:**  
     User enters a password to be stored.
2. **Encryption:**  
     Password is encrypted using AES, salted, and the result is Base64 encoded.
3. **Binary Conversion:**  
     The Base64 string is converted to binary bits.
4. **Steganography:**  
     Binary bits are hidden inside an image file.
5. **Image Encoding:**  
     The image is further encoded and stored securely.

## Technologies Used

- **Java 17+**
- **CryptoX AES Library**
- **Steganography Algorithms**
- **Base64 Encoding/Decoding**

## Getting Started

1. **Clone the repository:**
     ```bash
     git clone https://github.com/yourusername/AegisVault.git
     ```
2. **Build the project:**
     ```bash
     ./gradlew build
     ```
3. **Run the application:**
     ```bash
     java -jar build/libs/AegisVault.jar
     ```

## Usage

- Add, retrieve, and manage passwords securely.
- Export and import encrypted password images.

## Security

- All sensitive data is encrypted before storage.
- Multi-layer encryption and steganography make brute-force and extraction attacks highly impractical.

---

> **AegisVault**: Your passwords, protected by cryptography and hidden in plain sight.
