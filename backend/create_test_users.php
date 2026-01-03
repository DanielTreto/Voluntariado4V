<?php

require __DIR__ . '/vendor/autoload.php';

use App\Kernel;
use App\Entity\Volunteer;
use App\Entity\Organizacion;
use App\Entity\Actividad;
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$container = $kernel->getContainer();
$em = $container->get('doctrine')->getManager();

echo "Creating test users...\n";

// 1. Create Volunteer
$volProfile = $em->getRepository(Volunteer::class)->findOneBy(['CORREO' => 'test@volunteer.com']);
if (!$volProfile) {
    $volProfile = new Volunteer();
    $volProfile->setNOMBRE('Test Volunteer');
    $volProfile->setAPELLIDO1('User');
    $volProfile->setCORREO('test@volunteer.com');
    // Using plain text as per current AuthController logic
    $volProfile->setPASSWORD('123456');
    $volProfile->setTELEFONO('600123456');
    $volProfile->setFECHA_NACIMIENTO(new \DateTime('1990-01-01'));
    $volProfile->setDNI('12345678Z');
    $volProfile->setCODCICLO('1DAM');
    $volProfile->setESTADO('ACTIVO');

    $em->persist($volProfile);
    echo "Created volunteer: test@volunteer.com / 123456\n";
} else {
    echo "Volunteer test@volunteer.com already exists.\n";
}

// 2. Create Organization
$orgProfile = $em->getRepository(Organizacion::class)->findOneBy(['CORREO' => 'test@org.com']);
if (!$orgProfile) {
    $orgProfile = new Organizacion();
    $orgProfile->setNOMBRE('Test Org');
    $orgProfile->setTIPO_ORG('ONG');
    $orgProfile->setCORREO('test@org.com');
    $orgProfile->setPASSWORD('123456');
    $orgProfile->setTELEFONO('900123456');
    $orgProfile->setSECTOR('EDUCATIVO');
    $orgProfile->setAMBITO('LOCAL');
    $orgProfile->setDESCRIPCION('Organization for testing purposes');
    $orgProfile->setESTADO('ACTIVO');

    $em->persist($orgProfile);
    echo "Created organization: test@org.com / 123456\n";
} else {
    echo "Organization test@org.com already exists.\n";
}

$em->flush();

// 3. Create Activity for Org
if ($orgProfile) {
    // Check if activity exists
    $existingAct = $em->getRepository(Actividad::class)->findOneBy(['NOMBRE' => 'Test Activity', 'CODORG' => $orgProfile->getCODORG()]);
    if (!$existingAct) {
        $act = new Actividad();
        $act->setNOMBRE('Test Activity');
        $act->setDESCRIPCION('An activity to test signups');
        $act->setDURACION_SESION('02:00');
        $act->setFECHA_INICIO(new \DateTime('+1 week'));
        $act->setFECHA_FIN(new \DateTime('+1 week 2 hours'));
        $act->setN_MAX_VOLUNTARIOS(5);
        $act->setESTADO('PENDIENTE'); // Must be PENDING for signup
        $act->setCODORG($orgProfile->getCODORG());

        $em->persist($act);
        $em->flush();
        echo "Created activity 'Test Activity' for organization.\n";

        // 4. Register Volunteer in Activity
        if ($volProfile) {
            if (!$act->getVoluntarios()->contains($volProfile)) {
                $act->addVoluntario($volProfile);
                $em->persist($act);
                $em->flush();
                echo "Signed up test volunteer for 'Test Activity'.\n";
            } else {
                echo "Volunteer already signed up for 'Test Activity'.\n";
            }
        }
    } else {
        // If activity existed, check association
        if ($volProfile) {
            if (!$existingAct->getVoluntarios()->contains($volProfile)) {
                $existingAct->addVoluntario($volProfile);
                $em->persist($existingAct);
                $em->flush();
                echo "Signed up test volunteer for existing 'Test Activity'.\n";
            } else {
                echo "Volunteer already signed up for 'Test Activity'.\n";
            }
        }
    }
}

echo "Done.\n";
