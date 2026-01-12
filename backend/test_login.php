<?php
/**
 * Test: POST /login - Pruebas de login
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

// Test: Login con credenciales inválidas
echo "Testing Login with INVALID credentials...\n";
$res = testRequest('POST', "$baseUrl/login", ['email' => 'nonexistent@test.com', 'password' => 'wrongpass']);
echo "Status: " . $res['code'] . " (expected 401)\n";
echo "Body: " . $res['body'] . "\n";

// Test: Login sin password
echo "\nTesting Login without password...\n";
$res = testRequest('POST', "$baseUrl/login", ['email' => 'test@test.com']);
echo "Status: " . $res['code'] . " (expected 400)\n";
echo "Body: " . $res['body'] . "\n";

// Test: Login sin email
echo "\nTesting Login without email...\n";
$res = testRequest('POST', "$baseUrl/login", ['password' => 'somepass']);
echo "Status: " . $res['code'] . " (expected 400)\n";
echo "Body: " . $res['body'] . "\n";

// Test: Login con JSON vacío
echo "\nTesting Login with empty JSON...\n";
$res = testRequest('POST', "$baseUrl/login", []);
echo "Status: " . $res['code'] . " (expected 400)\n";
echo "Body: " . $res['body'] . "\n";
