@echo off
TITLE Installing Voluntariado4V Environment
SETLOCAL EnableDelayedExpansion

:: Check for Administrator privileges
net session >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] This script requires Administrator privileges.
    ECHO Please right-click and "Run as Administrator".
    PAUSE
    EXIT /B 1
)

ECHO ======================================================
ECHO      VOLUNTARIADO4V - AUTOMATED INSTALLATION
ECHO ======================================================
ECHO.

:: Check for Winget
where winget >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Winget is not installed. Attempting to install Microsoft App Installer...
    powershell -Command "Start-Process ms-windows-store://pdp/?ProductId=9nblggh4nns1"
    ECHO [INFO] Please click 'Install' or 'Update' in the Microsoft Store window that just opened.
    ECHO [INFO] Once finished, restart this script.
    PAUSE
    EXIT /B 1
)

:: Alternative automated install (more complex but silent) - currently using Store fallback for reliability
:: powershell -Command "Invoke-WebRequest -Uri https://github.com/microsoft/winget-cli/releases/latest/download/Microsoft.DesktopAppInstaller_8wekyb3d8bbwe.msixbundle -OutFile AppInstaller.msixbundle; Add-AppxPackage AppInstaller.msixbundle; Remove-Item AppInstaller.msixbundle"


:: Check for PHP
where php >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] PHP is not installed. Attempting to install via Winget...
    winget install -e --id PHP.PHP --accept-package-agreements --accept-source-agreements --silent
    IF !ERRORLEVEL! NEQ 0 (
        ECHO [ERROR] Failed to install PHP via Winget.
        ECHO Attempting alternative ID...
        winget install -e --id PHP.PHP.8.2 --accept-package-agreements --accept-source-agreements --silent
    )
    
    :: Final check after attempt
    where php >nul 2>&1
    IF !ERRORLEVEL! NEQ 0 (
        ECHO [CRITICAL] Could not install PHP automatically. 
        ECHO Possible reasons: Internet connection or Winget blocked.
        PAUSE
        EXIT /B 1
    )
    ECHO [SUCCESS] PHP installed successfully.
    :: Refresh PATH for current session
    FOR /F "tokens=*" %%g IN ('powershell -Command "[System.Environment]::GetEnvironmentVariable('Path', 'Machine') + ';' + [System.Environment]::GetEnvironmentVariable('Path', 'User')"') DO SET "PATH=%%g"
)

:: Check for Composer
where composer >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Composer is not installed. Attempting to install via Winget...
    winget install -e --id Composer.Composer --accept-package-agreements --accept-source-agreements --silent
    IF !ERRORLEVEL! NEQ 0 (
        ECHO [ERROR] Failed to install Composer.
        PAUSE
        EXIT /B 1
    )
    :: Refresh PATH for current session
    FOR /F "tokens=*" %%g IN ('powershell -Command "[System.Environment]::GetEnvironmentVariable('Path', 'Machine') + ';' + [System.Environment]::GetEnvironmentVariable('Path', 'User')"') DO SET "PATH=%%g"
)

:: Check for Node/NPM
where node >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Node.js is not installed. Attempting to install via Winget...
    winget install -e --id OpenJS.NodeJS --accept-package-agreements --accept-source-agreements --silent
    IF !ERRORLEVEL! NEQ 0 (
        ECHO [ERROR] Failed to install Node.js.
        PAUSE
        EXIT /B 1
    )
    :: Refresh PATH for current session
    FOR /F "tokens=*" %%g IN ('powershell -Command "[System.Environment]::GetEnvironmentVariable('Path', 'Machine') + ';' + [System.Environment]::GetEnvironmentVariable('Path', 'User')"') DO SET "PATH=%%g"
)

:: Check for Symfony CLI
where symfony >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Symfony CLI is not installed. Attempting to install via Winget...
    winget install -e --id Symfony.SymfonyCLI --accept-package-agreements --accept-source-agreements --silent
    IF !ERRORLEVEL! NEQ 0 (
        ECHO [INFO] Failed to install Symfony CLI via Winget. Continuing without it.
    )
)

:: Check for System Requirements (PHP Extensions)
ECHO [0/4] Checking System Requirements...
cd Voluntariado4V_Web\backend
call php check_requirements.php
IF %ERRORLEVEL% NEQ 0 (
    ECHO.
    ECHO [ERROR] System requirements check failed.
    ECHO Please fix the issues listed above and try again.
    PAUSE
    EXIT /B 1
)
cd ..\..

:: Setup Backend .env.local if not exists
CD Voluntariado4V_Web\backend
IF NOT EXIST .env.local (
    ECHO [INFO] Creating .env.local from .env...
    copy .env .env.local
    ECHO [IMPORTANT] Please edit .env.local with your database credentials if needed.
)
call composer install
ECHO [INFO] Creating database and running migrations...
call php bin/console doctrine:database:create --if-not-exists
call php bin/console doctrine:migrations:migrate --no-interaction
CD ..\..

ECHO.
ECHO [2/4] Setting up Frontend (Angular)...
CD Voluntariado4V_Web\frontend
call npm install --legacy-peer-deps
CD ..\..

ECHO [3/4] populating Initial Data...
CD Voluntariado4V_Web\backend
call php load_sql.php
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Data population might have failed if database was already populated.
)
CD ..\..

ECHO.
ECHO [4/4] Finishing...

ECHO.
ECHO.
powershell -Command "Write-Host '=============================================' -ForegroundColor Green"
powershell -Command "Write-Host '           INSTALACIÓN COMPLETADA            ' -ForegroundColor Green"
powershell -Command "Write-Host '=============================================' -ForegroundColor Green"
ECHO.
ECHO You can now run 'start.bat' to launch the application.
cd ..\..
PAUSE