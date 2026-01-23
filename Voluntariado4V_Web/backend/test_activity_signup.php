<?php
/**
 * Test: POST /activities/{id}/signup - Inscribir voluntario en actividad
 * Test: GET /activities/{id}/volunteers - Ver voluntarios de una actividad
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

// Obtener actividades existentes
echo "\nGetting existing activities...\n";
$res = testRequest('GET', "$baseUrl/activities");
$activities = json_decode($res['body'], true);

if (empty($activities)) {
    echo "No activities found. Please run test_activity_create.php first.\n";
    exit(1);
}
$activityId = $activities[0]['id'];
echo "Using Activity ID: $activityId\n";

// Test: Inscribir voluntario en actividad
echo "\nTesting Volunteer Signup to Activity...\n";
$res = testRequest('POST', "$baseUrl/activities/$activityId/signup", ['volunteerId' => $volunteerId]);
echo "Signup Status: " . $res['code'] . "\n";
echo "Signup Body: " . $res['body'] . "\n";

// Test: Ver voluntarios de la actividad
echo "\nTesting Get Activity Volunteers...\n";
$res = testRequest('GET', "$baseUrl/activities/$activityId/volunteers");
echo "Get Volunteers Status: " . $res['code'] . "\n";
echo "Get Volunteers Body: " . $res['body'] . "\n";
