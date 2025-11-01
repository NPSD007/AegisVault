@echo off
title AegisVault - Development Mode
echo.
echo ====================================================
echo ^|                   AegisVault                     ^|
echo ^|              Development Mode                    ^|
echo ====================================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java 11 or higher to run AegisVault.
    echo.
    pause
    exit /b 1
)

echo Starting AegisVault from source...
echo.

REM Compile source files first
echo Compiling source files...
javac -cp "AegisVault-Distribution\lib\*" --module-path "AegisVault-Distribution\lib" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -d . src\gui\*.java src\gui\controllers\*.java src\aes\*.java src\steganography\*.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

REM Copy resources
echo Copying resources...
if not exist "gui" mkdir "gui"
xcopy "src\gui\fxml" "gui\fxml\" /E /Y /Q >nul 2>&1
xcopy "src\gui\images" "gui\images\" /E /Y /Q >nul 2>&1
copy "src\gui\application.css" "gui\" /Y >nul 2>&1

echo Compilation successful! Starting AegisVault...
echo.

REM Run from compiled classes with JavaFX modules
java -Djava.library.path="AegisVault-Distribution\lib" -Dprism.order=d3d,sw -cp ".;AegisVault-Distribution\lib\*" --module-path "AegisVault-Distribution\lib" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics gui.Main

echo.
echo AegisVault has closed.
pause