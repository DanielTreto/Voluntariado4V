<?php
// Test OPTIONS
$url = 'http://localhost:8000/api/organizations/1/status';
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

// Test PATCH
$data = json_encode(['status' => 'SUSPENDIDO']);
$options = [
    'http' => [
        'method'  => 'PATCH',
        'header'  => "Content-Type: application/json\r\n" .
            "Accept: application/json\r\n",
        'content' => $data,
        'ignore_errors' => true
    ]
];
$context  = stream_context_create($options);
$result = file_get_contents($url, false, $context);
echo "\nPATCH Response Body:\n";
echo $result;
