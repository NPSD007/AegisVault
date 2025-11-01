@echo off
title AegisVault Password Manager

:: Check for Java
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java is required but not found. Please install Java 11+ and try again.
    pause
    exit /b 1
)

:: Launch AegisVault silently
start "AegisVault" /MIN cmd /c "java -Djava.library.path=lib -cp AegisVault.jar;lib/* --module-path lib --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics gui.Main"

:: Close this launcher
timeout /t 1 /nobreak >nul
exit