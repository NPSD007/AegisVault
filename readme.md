# AegisVault - Advanced Password Manager

<div align="center">

![AegisVault Logo](src/gui/images/medusa.png)

**Your passwords, encrypted with AES-256 and hidden in PNG images.**

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17+-green.svg)](https://openjfx.io/)
[![Security](https://img.shields.io/badge/Security-AES--256--GCM-red.svg)](https://en.wikipedia.org/wiki/Galois/Counter_Mode)
[![Release](https://img.shields.io/badge/Release-v1.0.0-brightgreen.svg)](https://github.com/NPSD007/AegisVault/releases)

</div>

---

## Overview

**AegisVault** is a next-generation password manager that merges **AES-256-GCM encryption** with **steganographic concealment**. Unlike traditional password managers that simply encrypt data, AegisVault hides your encrypted credentials inside PNG images, making them visually indistinguishable and resistant to data leaks.

This project showcases strong cryptography, modern UI design, and file-based secure storage in a portable and production-ready form.

---

## Key Features

- **Triple-Layer Security:** AES-256-GCM, PBKDF2 key derivation, and LSB steganography
- **Modern Interface:** Dark-themed JavaFX GUI with animations
- **Complete CRUD Operations:** Add, view, edit, and delete credentials securely
- **Portable:** Runs directly without installation
- **Security Logging:** Console audit trail for all vault operations
- **Master Password:** Required for every sensitive action

---

## Architecture

```text
┌───────────────────────────────────────────────┐
│                 Presentation Layer            │
│  JavaFX GUI (FXML, CSS, Controllers)          │
│  - Dashboard, Login, and Edit Screens         │
├───────────────────────────────────────────────┤
│                 Logic Layer                   │
│  - AES-256-GCM Encryption Engine              │
│  - Steganography (LSB Encoder/Decoder)        │
│  - Vault Manager & File I/O                   │
├───────────────────────────────────────────────┤
│                 Storage Layer                 │
│  - JSON-based vault metadata                  │
│  - Encrypted PNG image storage                │
└───────────────────────────────────────────────┘
```

---

## How It Works

1. **Master Password Derivation:** Uses PBKDF2-HMAC-SHA256 with 100,000+ iterations to derive a 256-bit encryption key.
2. **AES Encryption:** Each password entry is encrypted using AES-256-GCM for authenticated encryption.
3. **Steganography:** Encrypted bytes are embedded in PNG images using the Least Significant Bit (LSB) technique.
4. **Vault Storage:** The PNG images and metadata are organized in a secure JSON-based vault.

---

## Technologies Used

| Component | Technology |
|------------|-------------|
| **Language** | Java 17+ |
| **Framework** | JavaFX 17+ |
| **Encryption** | AES-256-GCM |
| **Key Derivation** | PBKDF2-HMAC-SHA256 |
| **Steganography** | LSB (Least Significant Bit) PNG embedding |
| **Storage** | JSON-based vault with PNG containers |

---

## Installation

### **Portable Version (Recommended)**

1. **Download:** [AegisVault v1.0.0 Portable](https://github.com/NPSD007/AegisVault/releases)
2. **Extract:** Unzip the archive to any directory
3. **Run:** `Launch-AegisVault.bat`
4. **Login:** Use default master password `admin`

> Includes JavaFX runtime and dependencies. No Java installation required.

---

## Development Setup

### **System Requirements**
- Java 17+ (OpenJDK or Oracle JDK)
- JavaFX 17+ SDK

### **Clone & Run**
```bash
git clone https://github.com/NPSD007/AegisVault.git
cd AegisVault

# Set JavaFX path (Windows)
set JAVAFX_PATH=C:\path\to\javafx-sdk-17\lib

# Compile and run
javac -cp "src;%JAVAFX_PATH%/*" src/gui/*.java src/gui/controllers/*.java src/steganography/*.java src/aes/*.java
java -cp "src;%JAVAFX_PATH%/*" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml gui.Main
```

---

## Usage Guide

### **Add Password**
1. Click **Add New Password**
2. Fill in details (website, username, password)
3. Click **Save** – encryption and embedding happen automatically

### **View/Edit/Delete**
- **View:** Enter master password to decrypt and view credentials
- **Edit:** Modify details – vault auto-re-encrypts
- **Delete:** Confirm deletion to permanently remove data

---

## Project Structure

```text
AegisVault/
├── src/
│   ├── gui/               # JavaFX GUI components
│   ├── aes/               # AES-256 encryption engine
│   └── steganography/     # PNG steganography logic
├── bin/                   # Portable build
├── vault_data/            # Vault and encrypted images
└── resources/             # FXML, CSS, and image assets
```

---

## Testing

Run internal tests to verify encryption and steganography:

```bash
java -cp "src" aes.AESTest
java -cp "src" steganography.StegoTest
```

**Expected Results:**
- Encryption/decryption successful
- Images visually unchanged after embedding

---

## Security Validation

 AES-256-GCM encryption verified via authenticated encryption
 PBKDF2 key derivation produces consistent keys
 PNG steganography statistically indistinguishable from original
 Secure memory cleanup implemented for sensitive data

---

## Roadmap

| Version | Features |
|----------|-----------|
| **v1.0.0** | Full CRUD, encryption, steganography, JavaFX UI |
| **v1.1.0** | Password generator, import/export, UI improvements |
| **v2.0.0** | Cross-platform support, cloud sync, 2FA integration |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add AmazingFeature'`
4. Push to your branch: `git push origin feature/AmazingFeature`
5. Open a Pull Request

Please follow existing naming conventions and add unit tests for new code.

---

## Security Disclaimer

AegisVault uses industry-standard cryptography and steganography but is primarily an **educational and proof-of-concept project**. It has not undergone professional security auditing. Use responsibly and at your own discretion.

---

## Acknowledgments

- **JavaFX Team** – for the GUI framework
- **OpenJDK Contributors** – for the modern Java platform
- **Cryptography Researchers** – for AES-GCM and PBKDF2 algorithms

---

<div align="center">

**AegisVault – Where Security Meets Steganography**

*Protecting your digital identity, one encrypted pixel at a time.*

</div>

