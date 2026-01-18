<?php
$url = 'http://localhost:8000/api/volunteers/1/status';
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
$headers = $http_response_header;

echo "Response Headers:\n";
print_r($headers);
echo "\nResponse Body:\n";
echo $result;
