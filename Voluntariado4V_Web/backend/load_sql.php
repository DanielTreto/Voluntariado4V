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

try {
    // Disable foreign key checks to avoid ordering issues
    $connection->executeStatement('SET FOREIGN_KEY_CHECKS=0');
    
    // Execute raw SQL. 
    // Note: Doctrine executeStatement runs single statement usually? 
    // PDO::exec might handle multiple if strictly raw, but depending on driver.
    // Safest is to split by ; if necessary, but big dumps often work directly or needing splitting.
    // Let's try splitting if it fails, or just run it. 
    // dbal typically expects prepared statements. For a dump, strict PDO exec is better.
    
    $pdo = $connection->getNativeConnection();
    $pdo->exec($sql);
    
    $connection->executeStatement('SET FOREIGN_KEY_CHECKS=1');
    echo "Database populated successfully!\n";
} catch (\Exception $e) {
    echo "Error loading SQL: " . $e->getMessage() . "\n";
    exit(1);
}
