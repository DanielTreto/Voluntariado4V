<?php

function testRequest($method, $url, $data = [])
{
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
    if (!empty($data)) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    return ['code' => $httpCode, 'body' => $response];
}

$baseUrl = 'http://127.0.0.1:8000/api';

echo "Testing Volunteer Registration...\n";
$volData = [
    'name' => 'John',
    'surname1' => 'Doe',
    'email' => 'john.doe@example.com', // random email to avoid collision?
    'phone' => '600000001',
    'dni' => '12345678B',
    'dateOfBirth' => '1990-01-01',
    'course' => '2ASIR', // Valid from DB
    'password' => 'secret123'
];
// Append random to email to avoid Unique constraint
$volData['email'] = 'john' . rand(1000, 9999) . '@example.com';
$volData['dni'] = rand(10000000, 99999999) . 'X';
$volData['phone'] = '6' . rand(10000000, 99999999);

$res = testRequest('POST', "$baseUrl/volunteers", $volData);
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";

if ($res['code'] !== 201) {
    echo "Volunteer Registration Failed!\n";
} else {
    echo "Volunteer Registration Success!\n";

    echo "Testing Volunteer Login...\n";
    $loginData = ['email' => $volData['email'], 'password' => 'secret123'];
    $resLogin = testRequest('POST', "$baseUrl/login", $loginData);
    echo "Login Status: " . $resLogin['code'] . "\n";
    echo "Login Body: " . $resLogin['body'] . "\n";
}

echo "Testing Organization Registration...\n";
$orgData = [
    'name' => 'Test Org',
    'type' => 'ONG',
    'email' => 'org' . rand(1000, 9999) . '@example.com',
    'phone' => '9' . rand(10000000, 99999999), // Org phones usually start with 9? Validation regex was ^[0-9]{9}$
    'sector' => 'SOCIAL',
    'scope' => 'LOCAL',
    'description' => 'Test Desc',
    'password' => 'orgpass123'
];

$res = testRequest('POST', "$baseUrl/organizations", $orgData);
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";

if ($res['code'] !== 201) {
    echo "Org Registration Failed!\n";
} else {
    echo "Org Registration Success!\n";

    echo "Testing Org Login...\n";
    $loginData = ['email' => $orgData['email'], 'password' => 'orgpass123'];
    $resLogin = testRequest('POST', "$baseUrl/login", $loginData);
    echo "Login Status: " . $resLogin['code'] . "\n";
    echo "Login Body: " . $resLogin['body'] . "\n";
}
