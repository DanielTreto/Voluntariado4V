<?php

require __DIR__ . '/vendor/autoload.php';

use App\Kernel;
use Symfony\Component\Dotenv\Dotenv;

// Load environment variables
(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$container = $kernel->getContainer();
$entityManager = $container->get('doctrine.orm.entity_manager');
$connection = $entityManager->getConnection();

$sqlFile = __DIR__ . '/populate_database.sql';

if (!file_exists($sqlFile)) {
    echo "Files not found: $sqlFile\n";
    exit(1);
}

echo "Loading SQL from $sqlFile...\n";

$sql = file_get_contents($sqlFile);

try {
    $queries = explode(';', $sql);
    foreach ($queries as $query) {
        $query = trim($query);
        if (!empty($query)) {
            $connection->executeStatement($query);
        }
    }
    echo "Database populated successfully!\n";
} catch (\Exception $e) {
    echo "Error populating database: " . $e->getMessage() . "\n";
    exit(1);
}
