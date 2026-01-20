<?php
/**
 * Test: POST /activities - Crear una actividad
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
    echo "No organizations found. Creating one first...\n";
    
    $orgData = [
        'name' => 'Test Org for Activity',
        'type' => 'ONG',
        'email' => 'activitytest' . rand(1000, 9999) . '@example.com',
        'phone' => '9' . rand(10000000, 99999999),
        'sector' => 'SOCIAL',
        'scope' => 'LOCAL',
        'description' => 'Test organization for activity creation',
        'password' => 'orgpass123'
    ];
    
    $res = testRequest('POST', "$baseUrl/organizations", $orgData);
    echo "Create Org Status: " . $res['code'] . "\n";
    
    if ($res['code'] === 201) {
        $orgId = json_decode($res['body'], true)['id'];
    } else {
        echo "Failed to create organization. Cannot test activity creation.\n";
        echo "Body: " . $res['body'] . "\n";
        exit(1);
    }
} else {
    $orgId = $orgs[0]['id'];
    echo "Using existing organization ID: $orgId\n";
}

// Crear actividad
echo "\nTesting Activity Creation...\n";
$activityData = [
    'title' => 'Test Activity ' . rand(1000, 9999),
    'description' => 'Test activity for API testing',
    'date' => date('Y-m-d', strtotime('+1 week')),
    'duration' => '02:00',
    'organizationId' => $orgId
];

$res = testRequest('POST', "$baseUrl/activities", $activityData);
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";

if ($res['code'] === 201) {
    echo "Activity Creation Success!\n";
} else {
    echo "Activity Creation Failed!\n";
}
