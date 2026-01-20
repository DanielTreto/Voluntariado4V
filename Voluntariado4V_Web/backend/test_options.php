<?php
$url = 'http://localhost:8000/api/volunteers/1/status';

$options = [
    'http' => [
        'method'  => 'OPTIONS',
        'header'  => "Accept: application/json\r\n",
        'ignore_errors' => true
    ]
];

$context  = stream_context_create($options);
$result = file_get_contents($url, false, $context);
$headers = $http_response_header;

echo "Response Headers:\n";
print_r($headers);
