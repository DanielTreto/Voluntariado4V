<?php

namespace App\Controller;

use App\Repository\CicloRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;
use OpenApi\Attributes as OA;

#[Route('/api')]
class CicloController extends AbstractController
{
    #[Route('/ciclos', name: 'api_ciclos_index', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of academic cycles',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(
                properties: [
                    new OA\Property(property: 'id', type: 'string'),
                    new OA\Property(property: 'name', type: 'string'),
                    new OA\Property(property: 'curso', type: 'string')
                ]
            )
        )
    )]
    #[OA\Tag(name: 'Metadata')]
    public function index(CicloRepository $cicloRepository): JsonResponse
    {
        $ciclos = $cicloRepository->findAll();
        $data = array_map(fn($ciclo) => [
            'id' => $ciclo->getCODCICLO(),
            'name' => $ciclo->getNOMBRE(),
            'curso' => $ciclo->getCURSO(),
        ], $ciclos);

        return new JsonResponse($data);
    }

}
