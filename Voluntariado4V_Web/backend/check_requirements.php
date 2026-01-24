<?php

function checkExtension($name) {
    if (extension_loaded($name)) {
        return true;
    }
    return false;
}

function enableExtension($phpIniPath, $extensionName) {
    $content = file_get_contents($phpIniPath);
    $pattern = '/^;extension\s*=\s*' . preg_quote($extensionName, '/') . '/m';
    if (preg_match($pattern, $content)) {
        $newContent = preg_replace($pattern, "extension=$extensionName", $content);
        if (file_put_contents($phpIniPath, $newContent)) {
            echo " [FIX] Uncommented 'extension=$extensionName' in php.ini.\n";
            return true;
        }
    }
    return false;
}

$phpIniPath = php_ini_loaded_file();
echo "Checking environment using php.ini: $phpIniPath\n";

$errors = [];
$warnings = [];

// 1. Check ZIP extension (Required for Composer)
if (!checkExtension('zip')) {
    echo " [!] ZIP extension is missing.\n";
    if ($phpIniPath && enableExtension($phpIniPath, 'zip')) {
        echo " [SUCCESS] ZIP extension enabled! Please restart the terminal/script for it to take effect.\n";
        echo " [INFO] You might need to run the installer again.\n";
        exit(1); // Exit to force restart of PHP process/check
    } else {
        $errors[] = "ZIP extension is disabled. Please edit $phpIniPath and uncomment ';extension=zip'.";
    }
} else {
    echo " [OK] ZIP extension found.\n";
}

// 2. Check SQL Server Drivers
$sqlsrv = checkExtension('sqlsrv');
$pdo_sqlsrv = checkExtension('pdo_sqlsrv');

if (!$sqlsrv || !$pdo_sqlsrv) {
    $errors[] = "SQL Server drivers are missing!";
    
    // Check if DLLs depend on thread safety
    ob_start();
    phpinfo(INFO_GENERAL);
    $info = ob_get_clean();
    $ts = (strpos($info, 'Thread Safety => enabled') !== false) ? 'ts' : 'nts';
    $arch = (PHP_INT_SIZE === 8) ? 'x64' : 'x86';
    $version = PHP_MAJOR_VERSION . '.' . PHP_MINOR_VERSION; // e.g., 8.2

    echo "\n" . str_repeat('=', 50) . "\n";
    echo " MISSING SQL SERVER DRIVERS\n";
    echo str_repeat('=', 50) . "\n";
    echo "Your PHP version: $version ($ts) $arch\n";
    echo "You need to download the Microsoft Drivers for PHP for SQL Server.\n\n";
    
    echo "DOWNLOAD INSTRUCTIONS:\n";
    echo "1. Go to: https://learn.microsoft.com/en-us/sql/connect/php/download-drivers-php-sql-server\n";
    echo "2. Download the drivers for PHP $version.\n";
    echo "3. Copy these files to your extension directory:\n";
    echo "   " . ini_get('extension_dir') . "\n";
    $vClean = str_replace('.', '', $version);
    echo "   - php_sqlsrv_{$vClean}_{$ts}_{$arch}.dll\n";
    echo "   - php_pdo_sqlsrv_{$vClean}_{$ts}_{$arch}.dll\n\n";
    echo "4. Add the following lines to your php.ini ($phpIniPath):\n";
    echo "   extension=php_sqlsrv_{$vClean}_{$ts}_{$arch}.dll\n";
    echo "   extension=php_pdo_sqlsrv_{$vClean}_{$ts}_{$arch}.dll\n";
    echo str_repeat('=', 50) . "\n\n";
} else {
    echo " [OK] SQL Server drivers found.\n";
}

if (!empty($errors)) {
    echo "\n[ERROR] The installation cannot proceed due to missing requirements:\n";
    foreach ($errors as $error) {
        echo " - $error\n";
    }
    exit(1);
}

echo "\n[SUCCESS] All checks passed!\n";
exit(0);
