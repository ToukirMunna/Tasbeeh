@echo off
title Wireless ADB Connector
color 0B
cd /d "%~dp0\.."

echo ========================================================
echo   Wireless ADB Connection Assistant
echo ========================================================
echo.

where adb >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
        set "PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"
    )
)

set "DEFAULT_IP="
if exist ".phone_ip" (
    set /p DEFAULT_IP=<.phone_ip
)

if defined DEFAULT_IP (
    set /p PHONE_IP="Enter Phone IP Address [Default: %DEFAULT_IP%]: "
) else (
    set /p PHONE_IP="Enter Phone IP Address (e.g. 192.168.1.100): "
)

if "%PHONE_IP%"=="" set "PHONE_IP=%DEFAULT_IP%"
if "%PHONE_IP%"=="" (
    color 0C
    echo [ERROR] No IP address provided.
    pause
    exit /b 1
)

echo %PHONE_IP%>.phone_ip

set /p PHONE_PORT="Enter Port [Default: 5555]: "
if "%PHONE_PORT%"=="" set "PHONE_PORT=5555"

echo.
echo Connecting to %PHONE_IP%:%PHONE_PORT%...
adb connect %PHONE_IP%:%PHONE_PORT%

echo.
echo Checking Connected Devices:
adb devices

echo.
pause
