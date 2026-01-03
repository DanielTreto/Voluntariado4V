<?php

require 'vendor/autoload.php';

use App\Kernel;
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$em = $kernel->getContainer()->get('doctrine')->getManager();
$connection = $em->getConnection();

$sqls = [
    "ALTER TABLE ORGANIZACION ADD password NVARCHAR(255) NOT NULL DEFAULT '123456'",
    "ALTER TABLE VOLUNTARIO ADD password NVARCHAR(255) NOT NULL DEFAULT '123456'",
    "ALTER TABLE ORGANIZACION ADD CONSTRAINT DF_ORG_PASS DEFAULT '123456' FOR password",
    "ALTER TABLE VOLUNTARIO ADD CONSTRAINT DF_VOL_PASS DEFAULT '123456' FOR password",
    // Remove defaults after adding (optional, but cleaner)
    "ALTER TABLE ORGANIZACION DROP CONSTRAINT DF_ORG_PASS",
    "ALTER TABLE VOLUNTARIO DROP CONSTRAINT DF_VOL_PASS",
];

foreach ($sqls as $sql) {
    try {
        $connection->executeStatement($sql);
        echo "Executed: $sql\n";
    } catch (\Exception $e) {
        echo "Error: " . $e->getMessage() . "\n";
    }
}

echo "Database updated.\n";
