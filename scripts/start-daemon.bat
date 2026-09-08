@echo off
title Gradle Daemon Starter
color 0A
cd /d "%~dp0\.."

echo ========================================================
echo   Gradle Daemon: Background Warm-Up
echo ========================================================
echo.

if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
        set "PATH=%JAVA_HOME%\bin;%PATH%"
    )
)

if not exist "gradlew.bat" (
    color 0C
    echo [ERROR] gradlew.bat not found in: %cd%
    echo Please ensure this script is run from the root directory of your project.
    echo.
    pause
    exit /b 1
)

echo [1/2] Initializing Gradle Daemon in RAM...
call gradlew.bat --daemon

echo.
echo [2/2] Checking Active Daemon Status...
call gradlew.bat --status

echo.
echo ========================================================
echo   SUCCESS: Gradle Daemon is active in RAM!
echo ========================================================
echo.
pause
