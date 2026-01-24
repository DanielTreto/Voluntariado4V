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
    // Split by GO batch separator (case insensitive, on its own line)
    $queries = preg_split('/^GO\s*$/im', $sql);
    
    foreach ($queries as $query) {
        $query = trim($query);
        // Skip empty queries
        if (empty($query)) {
            continue;
        }

        try {
            $connection->executeStatement($query);
        } catch (\Exception $e) {
            // Log error but maybe continue or stop? For now let's stop on error to be safe, 
            // but wrap in try-catch to give better context if needed.
            throw $e;
        }
    }
    echo "Database populated successfully!\n";
} catch (\Exception $e) {
    echo "Error populating database: " . $e->getMessage() . "\n";
    exit(1);
}
