<?php

namespace App\Controller;

use App\Repository\CicloRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/api')]
class CicloController extends AbstractController
{
    #[Route('/ciclos', name: 'api_ciclos_index', methods: ['GET'])]
    public function index(CicloRepository $cicloRepository): JsonResponse
    {
        $ciclos = $cicloRepository->findAll();
        $data = [];

        foreach ($ciclos as $ciclo) {
            $data[] = [
                'id' => $ciclo->getCODCICLO(),
                'name' => $ciclo->getNOMBRE(),
                'family' => $ciclo->getFAMILIA(), // Assuming this field exists, checking entity next
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/ciclos', name: 'api_ciclos_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
