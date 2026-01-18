<?php
/**
 * Test: GET /organizations - Lista todas las organizaciones
 */

$url = 'http://localhost:8000/api/organizations';
$options = [
    'http' => [
        'method'  => 'GET',
        'header'  => "Accept: application/json\r\n",
        'ignore_errors' => true
    ]
];
$context = stream_context_create($options);
$result = file_get_contents($url, false, $context);

echo "GET /organizations\n";
echo "Response Code: " . explode(' ', $http_response_header[0])[1] . "\n";
echo "Response Body:\n" . $result . "\n";
