<?php

namespace App\Controller;

use App\Repository\OdsRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;
use OpenApi\Attributes as OA;

#[Route('/api')]
class OdsController extends AbstractController
{
    #[Route('/ods', name: 'api_ods_index', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of ODS',
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
    public function index(OdsRepository $odsRepository): JsonResponse
    {
        $odsList = $odsRepository->findAll();
        $data = array_map(fn($ods) => [
            'id' => $ods->getNUMODS(),
            'description' => $ods->getDESCRIPCION(),
        ], $odsList);

        return new JsonResponse($data);
    }



}
