<?php

namespace App\Controller;

use App\Entity\Organizacion;
use App\Repository\OrganizationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;

#[Route('/api')]
class OrganizationController extends AbstractController
{
    #[Route('/organizations', name: 'api_organizations_index', methods: ['GET'])]
    public function index(OrganizationRepository $orgRepository): JsonResponse
    {
        $orgs = $orgRepository->findAll();
        $data = [];

        foreach ($orgs as $org) {
            $data[] = [
                'id' => $org->getCODORG(),
                'name' => $org->getNOMBRE(),
                'type' => $org->getTIPO_ORG(),
                'email' => $org->getCORREO(),
                'phone' => $org->getTELEFONO(),
                'sector' => $org->getSECTOR(),
                'scope' => $org->getAMBITO(),
                'description' => $org->getDESCRIPCION(),
                'status' => $org->getESTADO(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations', name: 'api_organizations_create', methods: ['POST'])]
    public function create(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator): JsonResponse
    {
        $data = json_decode($request->getContent(), true);

        if (!$data) {
            return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        $org = new Organizacion();
        $org->setNOMBRE($data['name'] ?? '');
        $org->setTIPO_ORG($data['type'] ?? '');
        $org->setCORREO($data['email'] ?? '');
        $org->setTELEFONO($data['phone'] ?? '');
        $org->setSECTOR($data['sector'] ?? '');
        $org->setAMBITO($data['scope'] ?? '');
        $org->setPERSONA_CONTACTO($data['contactPerson'] ?? null);
        $org->setDESCRIPCION($data['description'] ?? '');
        $org->setPASSWORD($data['password'] ?? '');
        $org->setESTADO('PENDIENTE');

        // Validation
        $errors = $validator->validate($org);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        $entityManager->persist($org);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Organization created', 'id' => $org->getCODORG()], 201);
        // CORS headers for development
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');

        return $response;
    }

    // Simple OPTIONS handler for CORS preflight
    #[Route('/organizations', name: 'api_organizations_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/organizations/{id}/status', name: 'api_organizations_update_status', methods: ['PATCH'])]
    public function updateStatus(int $id, Request $request, EntityManagerInterface $entityManager, OrganizationRepository $orgRepository): JsonResponse
    {
        $org = $orgRepository->find($id);

        if (!$org) {
            return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        $newStatus = $data['status'] ?? null;

        $validStatuses = ['ACTIVO', 'SUSPENDIDO', 'PENDIENTE'];

        if (!$newStatus || !in_array($newStatus, $validStatuses)) {
            return new JsonResponse(['error' => 'Invalid status. Allowed values: ' . implode(', ', $validStatuses)], 400);
        }

        $org->setESTADO($newStatus);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Organization status updated', 'newStatus' => $newStatus], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations/{id}/status', name: 'api_organizations_update_status_options', methods: ['OPTIONS'])]
    public function updateStatusOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'PATCH, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
