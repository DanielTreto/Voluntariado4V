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

        return new JsonResponse($data);
    }


}
