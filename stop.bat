@echo off
TITLE Stopping Voluntariado4V
ECHO ======================================================
ECHO      STOPPING VOLUNTARIADO4V SERVICES
ECHO ======================================================

ECHO.
ECHO [1/2] Stopping Frontend (Node/Angular)...
:: Kill the window named "Voluntariado4V Frontend" and all child processes
taskkill /FI "WINDOWTITLE eq Voluntariado4V Frontend" /T /F >nul 2>&1
IF %ERRORLEVEL% EQ 0 (
    ECHO    [OK] Frontend stopped.
) ELSE (
    ECHO    [INFO] Frontend window not found (maybe already closed).
)

ECHO.
ECHO [2/2] Stopping Backend (Symfony)...
:: Try to stop the symfony server gracefully
cd Voluntariado4V_Web\backend
call symfony server:stop >nul 2>&1
cd ..\..

:: Kill the window named "Voluntariado4V Backend" and all child processes
taskkill /FI "WINDOWTITLE eq Voluntariado4V Backend" /T /F >nul 2>&1
IF %ERRORLEVEL% EQ 0 (
    ECHO    [OK] Backend window closed.
) ELSE (
    ECHO    [INFO] Backend window not found (maybe already closed).
)

ECHO.
ECHO All services stopped.
PAUSE
