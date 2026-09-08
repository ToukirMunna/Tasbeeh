@echo off
title Android App Initializer
color 0B
cd /d "%~dp0\.."

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0init-app.ps1"

echo.
pause
