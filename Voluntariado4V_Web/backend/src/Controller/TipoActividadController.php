<?php

namespace App\Controller;

use App\Repository\TipoActividadRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;
use OpenApi\Attributes as OA;

#[Route('/api')]
class TipoActividadController extends AbstractController
{
    #[Route('/activity-types', name: 'api_activity_types_index', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of activity types',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(
                properties: [
                    new OA\Property(property: 'id', type: 'integer'),
                    new OA\Property(property: 'description', type: 'string')
                ]
            )
        )
    )]
    #[OA\Tag(name: 'Metadata')]
    public function index(TipoActividadRepository $repo): JsonResponse
    {
        $types = $repo->findAll();
        $data = array_map(fn($type) => [
            'id' => $type->getCODTIPO(),
            'description' => $type->getDESCRIPCION(),
        ], $types);

        return new JsonResponse($data);
    }



}
