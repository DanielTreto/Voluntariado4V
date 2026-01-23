<?php
// Test OPTIONS
$url = 'http://localhost:8000/api/activities';
$options = [
    'http' => [
        'method'  => 'OPTIONS',
        'header'  => "Accept: application/json\r\n",
        'ignore_errors' => true
    ]
];
$context  = stream_context_create($options);
$result = file_get_contents($url, false, $context);
echo "OPTIONS Response Headers:\n";
print_r($http_response_header);

// Test GET (Expected 500 if DB not updated, but confirms route exists)
$options = [
    'http' => [
        'method'  => 'GET',
        'header'  => "Accept: application/json\r\n",
        'ignore_errors' => true
    ]
];
$context  = stream_context_create($options);
$result = file_get_contents($url, false, $context);
echo "\nGET Response Code: " . explode(' ', $http_response_header[0])[1] . "\n";
echo "Response Body:\n" . $result . "\n";
