<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;

class ApiController extends AbstractController
{
    #[Route('/api/usuarios', name: 'api_usuarios', methods: ['GET'])]
    public function obtenerUsuarios(): JsonResponse
    {
        // 1. Simulación de datos (aquí harías la consulta a BDD con Doctrine)
        $usuarios = [
            ['id' => 1, 'nombre' => 'Ana'],
            ['id' => 2, 'nombre' => 'Carlos']
        ];

        // 2. Devolver JSON
        $response = new JsonResponse($usuarios);

        // 3. ACTIVAR CORS (Vital para que Angular no de error)
        // En producción se usa un "bundle", pero para probar rápido, añade esto:
        $response->headers->set('Access-Control-Allow-Origin', '*');

        return $response;
    }
}
