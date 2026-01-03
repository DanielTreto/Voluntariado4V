<?php
// fix_org_data.php
require 'vendor/autoload.php';

use App\Kernel;
use App\Entity\Actividad;
use App\Entity\Organizacion;
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();
$container = $kernel->getContainer();
$em = $container->get('doctrine')->getManager();

$orgRepo = $em->getRepository(Organizacion::class);
$actRepo = $em->getRepository(Actividad::class);

// Find the specific Organization ID 12
$org = $orgRepo->find(12);

if (!$org) {
    // Fallback if 12 doesn't exist (though logs say user is 12)
    $org = $orgRepo->findOneBy([]);
    echo "Warning: Org ID 12 not found. Using fallback ID: " . $org->getCODORG() . "\n";
}

echo "Found Organization:\n";
echo "Name: " . $org->getNOMBRE() . "\n";
echo "Email: " . $org->getCORREO() . "\n";

echo "ID: " . $org->getCODORG() . "\n";

$activities = $actRepo->findAll();
$count = 0;

foreach ($activities as $act) {
    $act->setCODORG($org->getCODORG());
    $count++;
}

$em->flush();

echo "Updated $count activities to belong to Organization ID " . $org->getCODORG() . ".\n";
echo "Login with email: " . $org->getCORREO() . "\n";
