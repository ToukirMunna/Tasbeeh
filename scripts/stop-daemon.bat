@echo off
title Gradle Daemon Stopper
color 0C
cd /d "%~dp0\.."

echo ========================================================
echo   Stopping Gradle Daemons
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
    pause
    exit /b 1
)

echo Stopping Gradle Daemons to free RAM...
call gradlew.bat --stop
echo.
echo Daemons stopped. RAM has been freed.
echo.
pause
