<?php

require 'vendor/autoload.php';

use App\Kernel;
use App\Entity\Organizacion;
use Symfony\Component\Dotenv\Dotenv;
use Symfony\Component\Validator\Validation;

// 1. Boot simple environment or just use Validator directly if creating new kernel is too heavy
// But we need the doctrine annotations reader usually.
// Let's try booting Kernel minimal style or just standard boot.

(new Dotenv())->bootEnv(__DIR__ . '/.env');
$validator = Validation::createValidatorBuilder()
    ->enableAttributeMapping()
    ->getValidator();

echo "--- Testing Organizacion Telephone Validation ---\n";

$org = new Organizacion();
$org->setNOMBRE("Test Org");
$org->setTIPO_ORG("ONG");
$org->setCORREO("valid@email.com");
$org->setSECTOR("SOCIAL");
$org->setAMBITO("LOCAL");
$org->setPASSWORD("password");
// $org->setTELEFONO(...) // Testing this

$testCases = [
    "666111222" => "Valid 9 digits",
    "666 111 222" => "9 digits with spaces",
    "666-111-222" => "9 digits with dashes",
    "123456789" => "9 digits starting with 1",
    "12345" => "Too short",
    "1234567890" => "Too long"
];

foreach ($testCases as $phone => $desc) {
    echo "\nTesting: '$phone' ($desc)\n";
    $org->setTELEFONO($phone);
    echo "  -> After setter: '" . $org->getTELEFONO() . "'\n";

    $errors = $validator->validate($org);

    if (count($errors) > 0) {
        echo "  -> FAIL: Validation Errors:\n";
        foreach ($errors as $error) {
            if ($error->getPropertyPath() === 'TELEFONO') {
                echo "     - " . $error->getMessage() . "\n";
            }
        }
    } else {
        echo "  -> PASS\n";
    }
}
