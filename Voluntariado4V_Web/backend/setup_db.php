<?php
$serverName = "localhost\\SQLEXPRESS";
$connectionOptions = array(
    "Database" => "master",
    "TrustServerCertificate" => true
);

// Connect
$conn = sqlsrv_connect($serverName, $connectionOptions);

if ($conn === false) {
    die(print_r(sqlsrv_errors(), true));
}

<<<<<<< HEAD
// Wipe Database
echo "Wiping database...\n";
$wipeSql = "
    DECLARE @sql NVARCHAR(MAX) = N'';
    SELECT @sql += N'ALTER TABLE ' + QUOTENAME(schema_name(schema_id)) + '.' + QUOTENAME(name) + ' DROP CONSTRAINT ' + QUOTENAME(object_name(object_id)) + ';'
    FROM sys.foreign_keys;
    EXEC sp_executesql @sql;
    
    SET @sql = N'';
    SELECT @sql += N'DROP TABLE ' + QUOTENAME(schema_name(schema_id)) + '.' + QUOTENAME(name) + ';'
    FROM sys.tables;
    EXEC sp_executesql @sql;
";
if (sqlsrv_query($conn, "USE VOLUNTARIADOBD")) {
    sqlsrv_query($conn, $wipeSql);
    echo "Database wiped.\n";
} else {
    echo "Could not use VOLUNTARIADOBD, assuming it doesn't exist yet.\n";
=======
// Drop Database (Force Fresh Start)
echo "Dropping database VOLUNTARIADOBD if exists...\n";
$dropSql = "
    IF EXISTS (SELECT name FROM sys.databases WHERE name = N'VOLUNTARIADOBD')
    BEGIN
        ALTER DATABASE [VOLUNTARIADOBD] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
        DROP DATABASE [VOLUNTARIADOBD];
    END
";

if (sqlsrv_query($conn, $dropSql)) {
    echo "Database VOLUNTARIADOBD dropped (if it existed).\n";
} else {
    echo "Error dropping database:\n";
    print_r(sqlsrv_errors());
>>>>>>> temp_mobile_api
}

$sqlFile = 'full_database_setup.sql';
$sqlContent = file_get_contents($sqlFile);

// Remove comments (simple regex, might be risky for complex strings but usually fine for this SQL)
$sqlContent = preg_replace('/--.*$/m', '', $sqlContent);

// Split by GO
// Handle diverse line endings and spacing
$batches = preg_split('/^GO\s*$/mi', $sqlContent);

foreach ($batches as $batch) {
    $batch = trim($batch);
    if (empty($batch)) continue;

    // Execute
    $stmt = sqlsrv_query($conn, $batch);
    if ($stmt === false) {
        echo "Error executing batch:\n$batch\nErrors:\n";
        print_r(sqlsrv_errors());
        // Continue or die? The script drops tables so errors might occur if they don't exist, but SQL has checks.
    } else {
        echo "Batch executed successfully.\n";
    }
}

sqlsrv_close($conn);
echo "Database setup completed.\n";
