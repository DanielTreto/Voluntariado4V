<?php

use App\Kernel;
use App\Entity\Volunteer;
use App\Entity\Credenciales;
use Symfony\Component\HttpFoundation\Request;

require __DIR__.'/vendor/autoload.php';

$kernel = new Kernel('dev', true);
$kernel->boot();
$container = $kernel->getContainer();
// For Symfony 5/6, services are private, but 'doctrine' implies 'doctrine.orm.entity_manager' typically or via registry
$registry = $container->get('doctrine');
$em = $registry->getManager();
$validator = $container->get('validator');
$volRepo = $em->getRepository(Volunteer::class);

echo "Starting registration debug...\n";

try {
    $volunteer = new Volunteer();
    $volunteer->setNOMBRE('Debug');
    $volunteer->setAPELLIDO1('User');
    // Unique email to avoid dup
    $volunteer->setCORREO('debug_' . time() . '@example.com');
    // Unique PHONE/DNI - using random
    $vPhone = '6' . rand(10000000, 99999999);
    $volunteer->setTELEFONO($vPhone);
    // DNI format: 8 digits + Letter
    $vDni = rand(10000000, 99999999) . 'Z';
    $volunteer->setDNI($vDni);
    $volunteer->setFECHA_NACIMIENTO(new \DateTime('1990-01-01'));
    $volunteer->setESTADO('PENDIENTE');

    $newId = $volRepo->findNextId();
    echo "Generated ID: $newId\n";
    $volunteer->setCODVOL($newId);

    $cred = new Credenciales();
    $cred->setVoluntario($volunteer);
    $cred->setUserType('VOLUNTARIO');
    $cred->setCorreo($volunteer->getCORREO());
    $cred->setPassword('password123');

    // Trying persist order
    $em->persist($cred);
    
    $errors = $validator->validate($volunteer);
    if (count($errors) > 0) {
        echo "Validation Errors:\n";
        foreach ($errors as $e) {
            echo $e->getPropertyPath() . ": " . $e->getMessage() . "\n";
        }
    } else {
        echo "Validation OK\n";
    }

    $em->persist($volunteer);
    
    echo "Flushing...\n";
    $em->flush();
    echo "Success! Database inserted volunteer $newId\n";

} catch (\Throwable $e) {
    echo "Exception: " . $e->getMessage() . "\n";
    echo "Trace:\n" . $e->getTraceAsString() . "\n";
}
