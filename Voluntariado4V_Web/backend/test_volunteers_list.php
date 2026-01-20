<?php
/**
 * Test: GET /volunteers - Lista todos los voluntarios
 */

$url = 'http://localhost:8000/api/volunteers';
$options = [
    'http' => [
        'method'  => 'GET',
        'header'  => "Accept: application/json\r\n",
        'ignore_errors' => true
    ]
];
$context = stream_context_create($options);
$result = file_get_contents($url, false, $context);

echo "GET /volunteers\n";
echo "Response Code: " . explode(' ', $http_response_header[0])[1] . "\n";
echo "Response Body:\n" . $result . "\n";
