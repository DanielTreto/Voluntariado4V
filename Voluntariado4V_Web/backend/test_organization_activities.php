<?php
/**
 * Test: GET /organizations/{id}/activities - Ver actividades de una organización
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

// Obtener organizaciones existentes
echo "Getting existing organizations...\n";
$res = testRequest('GET', "$baseUrl/organizations");
$orgs = json_decode($res['body'], true);

if (empty($orgs)) {
    echo "No organizations found. Please run test_api.php first to create one.\n";
    exit(1);
}

$orgId = $orgs[0]['id'];
echo "Using Organization ID: $orgId\n";

// Test: Ver actividades de la organización
echo "\nTesting Get Organization Activities...\n";
$res = testRequest('GET', "$baseUrl/organizations/$orgId/activities");
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";
