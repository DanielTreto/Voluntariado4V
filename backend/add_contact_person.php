<?php

require 'vendor/autoload.php';

use App\Kernel;
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$em = $kernel->getContainer()->get('doctrine')->getManager();
$connection = $em->getConnection();

try {
    $sql = "ALTER TABLE ORGANIZACION ADD PERSONA_CONTACTO NVARCHAR(100) NULL";
    $connection->executeQuery($sql);
    echo "Column PERSONA_CONTACTO added successfully.\n";
} catch (\Exception $e) {
    echo "Error adding column: " . $e->getMessage() . "\n";
}
