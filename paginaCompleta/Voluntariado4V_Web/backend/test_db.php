<?php
echo "Testing connection...\n";

$serverName = "BICHARRACO\SQLEXPRESS";
$connectionOptions = array(
    "Database" => "VOLUNTARIADOBD",
    "TrustServerCertificate" => true,
    //"UID" => "usuario", // Usando Windows Auth no hace falta
    //"PWD" => "contraseña"
);

// Intentar conexión con SQLSRV driver
echo "Attempting sqlsrv_connect...\n";
if (function_exists('sqlsrv_connect')) {
    $conn = sqlsrv_connect($serverName, $connectionOptions);
    if ($conn) {
        echo "SQLSRV Connected successfully.\n";
        sqlsrv_close($conn);
    } else {
        echo "SQLSRV Connection failed.\n";
        if (function_exists('sqlsrv_errors')) {
            print_r(sqlsrv_errors());
        } else {
            echo "Cannot retrieve errors: sqlsrv_errors function undefined.\n";
        }
    }
} else {
    echo "Function sqlsrv_connect does not exist.\n";
}

// Intentar conexión con PDO
echo "\nAttempting PDO connection...\n";
try {
    $conn = new PDO("sqlsrv:server=$serverName;Database=VOLUNTARIADOBD;TrustServerCertificate=true");
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "PDO Connected successfully.\n";

    $sql = "SELECT * FROM VOLUNTARIO";
    $stmt = $conn->query($sql);
    $volunteers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo "Found " . count($volunteers) . " volunteers.\n";
    print_r($volunteers);
} catch (PDOException $e) {
    echo "PDO Connection failed: " . $e->getMessage() . "\n";
}
