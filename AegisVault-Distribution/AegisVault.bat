@echo off
title AegisVault Password Manager - Beta v1.0.0
echo.
echo  ====================================================
echo  ^|              AegisVault Beta v1.0.0              ^|
echo  ^|           Password Manager - Portable            ^|
echo  ====================================================
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

echo  Loading AegisVault...
echo.

REM Get current directory for portable JavaFX libs
set CURRENT_DIR=%~dp0
set JAVAFX_PATH=%CURRENT_DIR%lib

REM Launch AegisVault with bundled JavaFX
java -Dprism.order=sw,d3d -cp "AegisVault.jar;lib\*" --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics gui.Main

echo.
echo AegisVault has closed.
echo Thank you for using AegisVault Password Manager!
echo.
pause