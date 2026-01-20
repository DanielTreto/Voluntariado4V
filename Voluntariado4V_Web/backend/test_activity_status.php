<?php
/**
 * Test: PATCH /activities/{id}/status - Actualizar estado de actividad
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

// Obtener actividades existentes
echo "Getting existing activities...\n";
$res = testRequest('GET', "$baseUrl/activities");
$activities = json_decode($res['body'], true);

if (empty($activities)) {
    echo "No activities found. Please run test_activity_create.php first.\n";
    exit(1);
}

$activityId = $activities[0]['id'];
echo "Using Activity ID: $activityId\n";
echo "Current Status: " . $activities[0]['status'] . "\n";

// Test: Actualizar estado a EN_PROGRESO
echo "\nTesting Update Activity Status to EN_PROGRESO...\n";
$res = testRequest('PATCH', "$baseUrl/activities/$activityId/status", ['status' => 'EN_PROGRESO']);
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";

// Test: Actualizar estado a FINALIZADA
echo "\nTesting Update Activity Status to FINALIZADA...\n";
$res = testRequest('PATCH', "$baseUrl/activities/$activityId/status", ['status' => 'FINALIZADA']);
echo "Status: " . $res['code'] . "\n";
echo "Body: " . $res['body'] . "\n";

// Test: Actualizar con estado inválido
echo "\nTesting Update Activity Status with INVALID status...\n";
$res = testRequest('PATCH', "$baseUrl/activities/$activityId/status", ['status' => 'INVALID_STATUS']);
echo "Status: " . $res['code'] . " (expected 400)\n";
echo "Body: " . $res['body'] . "\n";
