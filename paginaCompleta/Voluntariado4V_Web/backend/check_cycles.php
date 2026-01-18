<?php

require 'vendor/autoload.php';

use App\Kernel;
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$em = $kernel->getContainer()->get('doctrine')->getManager();
$connection = $em->getConnection();

$stmt = $connection->executeQuery("SELECT TOP 5 * FROM CICLO");
$cycles = $stmt->fetchAllAssociative();

print_r($cycles);
