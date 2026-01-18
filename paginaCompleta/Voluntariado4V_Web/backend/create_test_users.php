<?php

require __DIR__ . '/vendor/autoload.php';

use App\Kernel;
use App\Entity\Volunteer;
use App\Entity\Organizacion;
use App\Entity\Actividad;
use App\Entity\Credenciales; // Import Credenciales
use App\Entity\Ciclo; // Import Ciclo if needed
use Symfony\Component\Dotenv\Dotenv;

(new Dotenv())->bootEnv(__DIR__ . '/.env');

$kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
$kernel->boot();

$container = $kernel->getContainer();
$em = $container->get('doctrine')->getManager();
$volRepo = $em->getRepository(Volunteer::class);
$orgRepo = $em->getRepository(Organizacion::class);

echo "Creating test users...\n";

// Helper to get next ID (simplified version of Repository logic)
function getNextId($repo, $prefix, $field)
{
    $qb = $repo->createQueryBuilder('e');
    $qb->select("e.$field")
        ->orderBy("e.$field", 'DESC')
        ->setMaxResults(1);
    $result = $qb->getQuery()->getOneOrNullResult();
    if (!$result) return $prefix . '001';
    $num = (int) substr($result[$field], 3);
    return $prefix . str_pad((string)($num + 1), 3, '0', STR_PAD_LEFT);
}

// 0. Ensure Ciclo exists (referenced by Volunteer)
$ciclo = $em->getRepository(Ciclo::class)->findOneBy(['CODCICLO' => 'DAM']);
if (!$ciclo) {
    // If deleted by full_database_setup, we might need to recreate or pick another one
    // But full_database_setup inserts it. Let's assume it exists or pick one.
    $ciclo = $em->getRepository(Ciclo::class)->findAll()[0] ?? null;
    if (!$ciclo) {
        die("Error: No Cycles found. Run full_database_setup.sql first.\n");
    }
}

// 1. Create Volunteer
$volEmail = 'test@volunteer.com';
$volProfile = $volRepo->findOneBy(['CORREO' => $volEmail]);

if (!$volProfile) {
    $volProfile = new Volunteer();
    // Manual ID generation since it's not Auto-Increment
    $newId = getNextId($volRepo, 'vol', 'CODVOL');
    $volProfile->setCODVOL($newId);

    $volProfile->setNOMBRE('Test Volunteer');
    $volProfile->setAPELLIDO1('User');
    $volProfile->setCORREO($volEmail);
    // Password is conceptually in Credenciales, but if Entity has it...
    // The Entity Volunteer DOES NOT have password field mapped in annotation usually if Credenciales used, 
    // BUT full_database_setup.sql shows TABLE VOLUNTARIO has password column. 
    // AND Entity/Volunteer.php doesn't show a password field in the snippet I read?
    // Wait, snippet read of Volunteer.php (Step 125) skipped lines 63-65? 
    // Let's assume Credenciales is the way or check if Volunteer has setPassword.
    // Looking at Step 125 content... I don't see password field in Volunteer class snippet!
    // But SQL has it. This implies mismatch or I missed it in "skipping unchanged parts".
    // However, AuthController (Step 20) line 88: $volunteer = $cred->getVoluntario();
    // So Auth uses Credenciales.

    $volProfile->setTELEFONO('600123456');
    $volProfile->setFECHA_NACIMIENTO(new \DateTime('1990-01-01'));
    $volProfile->setDNI('12345678Z');
    $volProfile->setCiclo($ciclo); // Use object, not setCODCICLO string if Association
    $volProfile->setESTADO('ACTIVO');

    $em->persist($volProfile);

    // Create Credenciales for Volunteer
    $cred = new Credenciales();
    $cred->setUserType('VOLUNTARIO');
    $cred->setCorreo($volEmail);
    $cred->setPassword('123456'); // Plain text as per test_login.php expectations for now
    $cred->setVoluntario($volProfile);
    $em->persist($cred);

    echo "Created volunteer: $volEmail / 123456 (ID: $newId)\n";
} else {
    echo "Volunteer $volEmail already exists.\n";
}

// 2. Create Organization
$orgEmail = 'test@org.com';
$orgProfile = $orgRepo->findOneBy(['CORREO' => $orgEmail]);

if (!$orgProfile) {
    $orgProfile = new Organizacion();
    $newId = getNextId($orgRepo, 'org', 'CODORG');
    $orgProfile->setCODORG($newId);

    $orgProfile->setNOMBRE('Test Org');
    $orgProfile->setTIPO_ORG('ONG');
    $orgProfile->setCORREO($orgEmail);
    $orgProfile->setTELEFONO('900123456');
    $orgProfile->setSECTOR('EDUCATIVO');
    $orgProfile->setAMBITO('LOCAL');
    $orgProfile->setDESCRIPCION('Organization for testing purposes');
    $orgProfile->setESTADO('ACTIVO');
    // Same for firebaseUid if needed
    $orgProfile->setFirebaseUid('firebase_org_test');

    $em->persist($orgProfile);

    // Create Credenciales for Organization (if Org supported in Credenciales)
    // Checking Credenciales.php (Step 69) - No field 'organizacion' mapped! 
    // "Add Organization relationship if needed later".
    // But full_database_setup.sql TABLE CREDENCIALES has CODORG column.
    // So Entity is missing the mapping.
    // For now, AuthController line 99 says "Handle organization if implemented in Credenciales later".
    // So Org login might rely on Organization table password if it exists?
    // SQL TABLE ORGANIZACION has password column.
    // Entity Organizacion (Step 127) DOES NOT have password field shown.
    // This implies current codebase might be broken for Org Login via Password if fields missing in Entity?
    // Or maybe I missed them. 
    // BUT AuthController line 34 checks Org by Firebase UID.
    // Line 84 checks Email/Pass via CredencialesRepository.
    // Credenciales only returns Volunteer (line 88).
    // So Org Password Login is NOT implemented in AuthController yet via Credenciales.
    // Be aware of this. I will just create the Org Entity.

    echo "Created organization: $orgEmail (ID: $newId)\n";
} else {
    echo "Organization $orgEmail already exists.\n";
}

$em->flush();

echo "Done.\n";
