<?php

namespace App\Controller;

use App\Repository\TipoActividadRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/api')]
class TipoActividadController extends AbstractController
{
    #[Route('/activity-types', name: 'api_activity_types_index', methods: ['GET'])]
    public function index(TipoActividadRepository $repo): JsonResponse
    {
        $types = $repo->findAll();
        $data = [];

        foreach ($types as $type) {
            $data[] = [
                'id' => $type->getCODTIPO(),
                'description' => $type->getDESCRIPCION(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activity-types', name: 'api_activity_types_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
