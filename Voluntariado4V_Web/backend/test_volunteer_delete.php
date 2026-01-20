<?php
/**
 * Test: DELETE /volunteers/{id} - Eliminar voluntario
 */

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

// Crear un voluntario para eliminar
echo "Creating a volunteer to delete...\n";
$volData = [
    'name' => 'ToDelete',
    'surname1' => 'Test',
    'email' => 'delete' . rand(1000, 9999) . '@example.com',
    'phone' => '6' . rand(10000000, 99999999),
    'dni' => rand(10000000, 99999999) . 'X',
    'dateOfBirth' => '1990-01-01',
    'course' => '2DAM',
    'password' => 'testpass123'
];

$res = testRequest('POST', "$baseUrl/volunteers", $volData);
echo "Create Status: " . $res['code'] . "\n";

if ($res['code'] !== 201) {
    echo "Failed to create volunteer for deletion test.\n";
    echo "Body: " . $res['body'] . "\n";
    exit(1);
}

$volunteerId = json_decode($res['body'], true)['id'];
echo "Created Volunteer ID: $volunteerId\n";

// Test: Eliminar voluntario
echo "\nTesting Delete Volunteer...\n";
$res = testRequest('DELETE', "$baseUrl/volunteers/$volunteerId");
echo "Delete Status: " . $res['code'] . "\n";
echo "Delete Body: " . $res['body'] . "\n";

if ($res['code'] === 200 || $res['code'] === 204) {
    echo "Volunteer Deletion Success!\n";
} else {
    echo "Volunteer Deletion Failed!\n";
}
