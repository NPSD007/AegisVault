@echo off
title Building AegisVault Distribution
echo.
echo ====================================================
echo ^|          Building AegisVault Distribution        ^|
echo ^|              Compiling and Packaging             ^|
echo ====================================================
echo.

REM Clean any existing compiled files
echo Cleaning previous build...
if exist "gui" rmdir /s /q "gui"
if exist "aes" rmdir /s /q "aes"
if exist "steganography" rmdir /s /q "steganography"

REM Compile all source files
echo Compiling source files...
javac -cp "AegisVault-Distribution\lib\*" --module-path "AegisVault-Distribution\lib" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -d . src\gui\*.java src\gui\controllers\*.java src\aes\*.java src\steganography\*.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

REM Copy resources
echo Copying resources...
xcopy "src\gui\fxml" "gui\fxml\" /E /Y /Q >nul 2>&1
xcopy "src\gui\images" "gui\images\" /E /Y /Q >nul 2>&1
copy "src\gui\application.css" "gui\" /Y >nul 2>&1
xcopy "src\gui\config" "gui\config\" /E /Y /Q >nul 2>&1

REM Create new JAR file
echo Creating new JAR file...
jar -cf AegisVault-NEW.jar gui aes steganography

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: JAR creation failed!
    pause
    exit /b 1
)

REM Replace the old JAR in distribution
echo Updating distribution...
copy "AegisVault-NEW.jar" "AegisVault-Distribution\AegisVault.jar" /Y

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to update distribution JAR!
    pause
    exit /b 1
)

REM Clean up temporary files
echo Cleaning up...
del "AegisVault-NEW.jar"
rmdir /s /q "gui"
rmdir /s /q "aes"
rmdir /s /q "steganography"

echo.
echo ====================================================
echo ^|        AegisVault Distribution Updated!          ^|
echo ^|          Fixed edit functionality included       ^|
echo ====================================================
echo.
pause