@echo off
TITLE Installing Voluntariado4V Environment
ECHO ======================================================
ECHO      VOLUNTARIADO4V - AUTOMATED INSTALLATION
ECHO ======================================================
ECHO.

:: Check for PHP
php -v >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] PHP is not installed or not in PATH.
    PAUSE
    EXIT /B 1
)

:: Check for Composer
call composer -V >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Composer is not installed or not in PATH.
    PAUSE
    EXIT /B 1
)

:: Check for Node/NPM
call npm -v >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Node.js/NPM is not installed or not in PATH.
    PAUSE
    EXIT /B 1
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

ECHO [1/4] Setting up BACKEND (Symfony)...
cd Voluntariado4V_Web\backend
call composer install
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Composer install failed.
    PAUSE
    EXIT /B 1
)

:: Setup .env.local if not exists
IF NOT EXIST .env.local (
    ECHO Creating .env.local from .env...
    copy .env .env.local
    ECHO.
    ECHO [IMPORTANT] Please check .env.local and configure your DATABASE_URL.
    ECHO Press any key when you are ready to continue...
    PAUSE
)

ECHO.
ECHO [2/4] Configuring DATABASE...
ECHO Creating database if not exists...
call php bin/console doctrine:database:create --if-not-exists
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Database creation failed. Allow it if it already exists.
)

ECHO Running Migrations...
call php bin/console doctrine:migrations:migrate --no-interaction --allow-no-migration
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Migrations failed.
    PAUSE
    EXIT /B 1
)

ECHO Populating initial data (from src/BDD/full_database_setup.sql)...
call php load_sql.php
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Data population failed.
    PAUSE
    EXIT /B 1
)

ECHO [3/4] Installing FRONTEND (Angular)...
cd ..\frontend
call npm install --legacy-peer-deps
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] NPM install failed.
    PAUSE
    EXIT /B 1
)

ECHO.
ECHO.
powershell -Command "Write-Host '=============================================' -ForegroundColor Green"
powershell -Command "Write-Host '           INSTALACIÓN COMPLETADA            ' -ForegroundColor Green"
powershell -Command "Write-Host '=============================================' -ForegroundColor Green"
ECHO.
ECHO You can now run 'start.bat' to launch the application.
cd ..\..
PAUSE