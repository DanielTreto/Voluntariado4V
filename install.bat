@echo off
TITLE Installing Voluntariado4V Environment
ECHO ======================================================
ECHO      VOLUNTARIADO4V - AUTOMATED INSTALLATION
ECHO ======================================================
ECHO.

:: Check for PHP
where php >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] PHP is not installed. Attempting to install via Winget...
    winget install -e --id PHP.PHP
    IF %ERRORLEVEL% NEQ 0 (
        ECHO [ERROR] Failed to install PHP. Please install it manually.
        PAUSE
        EXIT /B 1
    )
    :: Refresh env vars
    call refreshenv 2>nul
)

:: Check for Composer
where composer >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Composer is not installed. Attempting to install via Winget...
    winget install -e --id Composer.Composer
    IF %ERRORLEVEL% NEQ 0 (
        ECHO [ERROR] Failed to install Composer. Please install it manually.
        PAUSE
        EXIT /B 1
    )
)

:: Check for Node/NPM
where node >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Node.js is not installed. Attempting to install via Winget...
    winget install -e --id OpenJS.NodeJS
    IF %ERRORLEVEL% NEQ 0 (
        ECHO [ERROR] Failed to install Node.js. Please install it manually.
        PAUSE
        EXIT /B 1
    )
)

:: Check for Symfony CLI
where symfony >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [WARNING] Symfony CLI is not installed. Attempting to install via Winget...
    winget install -e --id Symfony.SymfonyCLI
    IF %ERRORLEVEL% NEQ 0 (
        ECHO [INFO] Failed to install Symfony CLI via Winget. Continuing without it (using PHP directly).
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
call npm install
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