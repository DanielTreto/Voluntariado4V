<?php

namespace App\Controller;

use App\Repository\OdsRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/api')]
class OdsController extends AbstractController
{
    #[Route('/ods', name: 'api_ods_index', methods: ['GET'])]
    public function index(OdsRepository $odsRepository): JsonResponse
    {
        $odsList = $odsRepository->findAll();
        $data = [];

        foreach ($odsList as $ods) {
            $data[] = [
                'id' => $ods->getNUMODS(),
                'description' => $ods->getDESCRIPCION(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/ods', name: 'api_ods_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
