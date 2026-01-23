<?php
// check_status.php
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
$activities = $repo->findAll();

echo "ID | NOMBRE | ESTADO | FECHA_FIN\n";
echo "---|---|---|---\n";
foreach ($activities as $a) {
    echo $a->getCODACT() . " | " . $a->getNOMBRE() . " | " . $a->getESTADO() . " | " . $a->getFECHA_FIN()->format('Y-m-d') . "\n";
}
