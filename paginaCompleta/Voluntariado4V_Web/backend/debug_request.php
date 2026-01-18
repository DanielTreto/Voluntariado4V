<?php

use App\Kernel;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Dotenv\Dotenv;

require_once dirname(__FILE__) . '/vendor/autoload.php';

(new Dotenv())->bootEnv(dirname(__FILE__) . '/.env');

$kernel = new Kernel('dev', true);
$request = Request::create('/api/volunteers', 'GET');

try {
    $response = $kernel->handle($request);
    $response->send();
} catch (\Throwable $e) {
    echo "EXCEPTION CAUGHT:\n";
    echo $e->getMessage() . "\n";
    echo $e->getTraceAsString() . "\n";
} finally {
    $kernel->terminate($request, $response ?? null);
}
