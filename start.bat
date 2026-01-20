@echo off
TITLE Voluntariado4V Launcher
ECHO ======================================================
ECHO      STARTING VOLUNTARIADO4V APPLICATION
ECHO ======================================================
ECHO.

:: Start Backend
ECHO [1/2] Starting Backend Server (Symfony)...
cd Voluntariado4V_Web\backend
start "Voluntariado4V Backend" cmd /k "symfony server:start"

:: Start Frontend
ECHO [2/2] Starting Frontend Server (Angular)...
timeout /t 5 >nul
cd ..\frontend
start "Voluntariado4V Frontend" cmd /k "ng serve --open"

ECHO.
ECHO Services are starting...
ECHO Backend will run on 127.0.0.1:8000
ECHO Frontend will run on localhost:4200 (Opening browser...)
ECHO.
ECHO Keep this window open or minimize it. Closing the specific server windows will stop them.
cd ..
PAUSE
