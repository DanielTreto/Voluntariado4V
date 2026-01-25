<?php

use App\Kernel;
use Symfony\Component\Dotenv\Dotenv;

require_once dirname(__DIR__).'/backend/vendor/autoload.php';

(new Dotenv())->bootEnv(dirname(__DIR__).'/backend/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$em = $kernel->getContainer()->get('doctrine')->getManager();
$connection = $em->getConnection();

$sqlFile = __DIR__ . '/src/BBDD/full_database_setup.sql';

if (!file_exists($sqlFile)) {
    echo "SQL file not found: $sqlFile\n";
    exit(1);
}

echo "Loading SQL from: $sqlFile\n";
$sql = file_get_contents($sqlFile);

// Split by GO batch separator (case insensitive, on its own line)
// This is required for SQL Server scripts that use GO
$queries = preg_split('/^GO\s*$/im', $sql);

try {
    foreach ($queries as $index => $query) {
        $query = trim($query);
        if (empty($query)) {
            continue;
        }

        try {
            $connection->executeStatement($query);
            echo "."; 
        } catch (\Exception $e) {
            echo "\n[ERROR] Failed executing query #$index:\n";
            echo "--------------------------------------------------\n";
            echo substr($query, 0, 500) . "...\n";
            echo "--------------------------------------------------\n";
            echo "Message: " . $e->getMessage() . "\n";
            throw $e;
        }
    }
    echo "\nDatabase populated successfully!\n";
} catch (\Exception $e) {
    echo "\nFATAL ERROR: " . $e->getMessage() . "\n";
    exit(1);
}
