<?php
/**
 * Test: GET /volunteers/{id}/activities - Ver actividades de un voluntario
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

// Obtener voluntarios existentes
echo "Getting existing volunteers...\n";
$res = testRequest('GET', "$baseUrl/volunteers");
$volunteers = json_decode($res['body'], true);

if (empty($volunteers)) {
    echo "No volunteers found. Please run test_api.php first to create one.\n";
    exit(1);
}

$volunteerId = $volunteers[0]['id'];
echo "Using Volunteer ID: $volunteerId\n";

// Test: Ver actividades del voluntario
echo "\nTesting Get Volunteer Activities...\n";
$res = testRequest('GET', "$baseUrl/volunteers/$volunteerId/activities");
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";
