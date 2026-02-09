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

        return new JsonResponse($data);
    }


}
