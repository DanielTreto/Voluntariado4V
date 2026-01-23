<?php

$url = 'http://127.0.0.1:8000/api/organizations';
$data = [
    'name' => 'DebugOrg',
    'type' => 'ONG',
    'email' => 'debug_' . time() . '@test.com', // Unique email
    'phone' => '666 000 000',
    'sector' => 'SOCIAL',
    'scope' => 'LOCAL',
    'description' => 'Debug',
    'password' => 'pass'
];

$options = [
    'http' => [
        'header'  => "Content-type: application/json\r\n",
        'method'  => 'POST',
        'content' => json_encode($data),
        'ignore_errors' => true // Fetch content even on 400/500
    ]
];

$context  = stream_context_create($options);
$result = file_get_contents($url, false, $context);
$headers = $http_response_header;

echo "Response Headers:\n";
print_r($headers);
echo "\nResponse Body:\n";
echo $result;
