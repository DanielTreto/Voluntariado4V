<?php
// cleanup_activities.php
require 'vendor/autoload.php';

use App\Kernel;
use App\Entity\Actividad;
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();
$container = $kernel->getContainer();
$em = $container->get('doctrine')->getManager();
$repo = $em->getRepository(Actividad::class);

// IDs to delete: 6-10 (duplicates) and 14 (Test)
$idsToDelete = [6, 7, 8, 9, 10, 14];
$count = 0;

foreach ($idsToDelete as $id) {
    $act = $repo->find($id);
    if ($act) {
        $em->remove($act);
        echo "Deleted Activity ID: $id (" . $act->getNOMBRE() . ")\n";
        $count++;
    } else {
        echo "Activity ID $id not found.\n";
    }
}

$em->flush();
echo "Total deleted: $count\n";
