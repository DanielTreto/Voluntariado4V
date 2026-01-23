<?php

namespace App\Controller;

use App\Entity\Organizacion;
use App\Repository\OrganizationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Credenciales;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use Doctrine\DBAL\Exception\UniqueConstraintViolationException;

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
                'contactPerson' => $org->getPERSONA_CONTACTO(),
                'avatar' => $org->getAVATAR(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations/{id}', name: 'api_organizations_show', methods: ['GET'])]
    public function show(string $id, OrganizationRepository $orgRepository): JsonResponse
    {
        $org = $orgRepository->find($id);

        if (!$org) {
            return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        $data = [
            'id' => $org->getCODORG(),
            'name' => $org->getNOMBRE(),
            'type' => $org->getTIPO_ORG(),
            'email' => $org->getCORREO(),
            'phone' => $org->getTELEFONO(),
            'sector' => $org->getSECTOR(),
            'scope' => $org->getAMBITO(),
            'description' => $org->getDESCRIPCION(),
            'address' => $org->getDIRECCION(),
            'web' => $org->getWEB(),
            'status' => $org->getESTADO(),
            'contactPerson' => $org->getPERSONA_CONTACTO(),
            'avatar' => $org->getAVATAR(),
        ];

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations', name: 'api_organizations_create', methods: ['POST'])]
    public function create(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator, OrganizationRepository $orgRepository): JsonResponse
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
        $org->setDIRECCION($data['address'] ?? '');
        $org->setWEB($data['web'] ?? '');
        $org->setESTADO('PENDIENTE');

        // Generate Custom ID
        $newId = $orgRepository->findNextId();
        $org->setCODORG($newId);

        // Create Credentials
        $cred = new Credenciales();
        $cred->setOrganizacion($org); // Link directly to object
        $cred->setUserType('ORGANIZACION');
        $cred->setCorreo($data['email'] ?? '');
        $cred->setPassword($data['password'] ?? '');
        $entityManager->persist($cred);

        // Validation
        $errors = $validator->validate($org);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        try {
            $entityManager->persist($org);
            $entityManager->flush();
        } catch (UniqueConstraintViolationException $e) {
            return new JsonResponse([
                'errors' => [
                    'DUPLICADO' => 'El teléfono o el correo electrónico ya están registrados por otra organización.'
                ]
            ], 400);
        }

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
    public function updateStatus(string $id, Request $request, EntityManagerInterface $entityManager, OrganizationRepository $orgRepository): JsonResponse
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

    #[Route('/organizations/{id}', name: 'api_organizations_update', methods: ['PUT'])]
    public function update(string $id, Request $request, EntityManagerInterface $entityManager, OrganizationRepository $orgRepository, ValidatorInterface $validator): JsonResponse
    {
        $org = $orgRepository->find($id);

        if (!$org) {
            return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (!$data) {
             return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        // Update fields if provided
        if (isset($data['name'])) $org->setNOMBRE($data['name']);
        if (isset($data['type'])) $org->setTIPO_ORG($data['type']);
        if (isset($data['email'])) $org->setCORREO($data['email']);
        if (isset($data['phone'])) $org->setTELEFONO($data['phone']);
        if (isset($data['sector'])) $org->setSECTOR($data['sector']);
        if (isset($data['scope'])) $org->setAMBITO($data['scope']);
        if (isset($data['contactPerson'])) $org->setPERSONA_CONTACTO($data['contactPerson']);
        if (isset($data['description'])) $org->setDESCRIPCION($data['description']);
        if (isset($data['address'])) $org->setDIRECCION($data['address']);
        if (isset($data['web'])) $org->setWEB($data['web']);

        // Validate
        $errors = $validator->validate($org);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                 $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        try {
            $entityManager->flush();
        } catch (UniqueConstraintViolationException $e) {
            return new JsonResponse([
                'errors' => [
                    'DUPLICADO' => 'El teléfono o el correo electrónico ya están registrados por otra organización.'
                ]
            ], 400);
        }

        $response = new JsonResponse(['status' => 'Organization updated'], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations/{id}/activities', name: 'api_organizations_activities', methods: ['GET'])]
    public function organizationActivities(string $id, OrganizationRepository $orgRepository, \App\Repository\ActivityRepository $activityRepository): JsonResponse
    {
        $org = $orgRepository->find($id);
        if (!$org) {
             return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        $activities = $activityRepository->findBy(['organizacion' => $id]);
        $data = [];

        foreach ($activities as $act) {
            $data[] = [
                'id' => $act->getCODACT(),
                'title' => $act->getNOMBRE(),
                'description' => $act->getDESCRIPCION(),
                'date' => $act->getFECHA_INICIO()->format('Y-m-d'),
                'endDate' => $act->getFECHA_FIN()->format('Y-m-d'),
                'status' => $act->getESTADO(),
                'volunteersCount' => $act->getVoluntarios()->count(),
                'volunteers' => array_map(function($vol) {
                     return [
                         'id' => $vol->getCODVOL(),
                         'name' => $vol->getNOMBRE(),
                         'surname1' => $vol->getAPELLIDO1(),
                         'surname2' => $vol->getAPELLIDO2(),
                         'avatar' => $vol->getAVATAR()
                     ];
                }, $act->getVoluntarios()->toArray()),
                'ods' => array_map(function($ods) {
                    return [
                        'id' => $ods->getNUMODS(),
                        'description' => $ods->getDESCRIPCION()
                    ];
                }, $act->getOds()->toArray()),
                // Add Organization info for Mapper to pick up
                'organization' => [
                     'id' => $org->getCODORG(), // Use the org we already fetched
                     'name' => $org->getNOMBRE(),
                     'avatar' => $org->getAVATAR()
                ]
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations/{id}/activities', name: 'api_organizations_activities_options', methods: ['OPTIONS'])]
    public function organizationActivitiesOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
    #[Route('/organizations/{id}/avatar', name: 'api_organizations_upload_avatar', methods: ['POST'])]
    public function uploadAvatar(string $id, Request $request, EntityManagerInterface $entityManager, OrganizationRepository $orgRepository): JsonResponse
    {
        $org = $orgRepository->find($id);
        if (!$org) {
            return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        $file = $request->files->get('avatar');
        if (!$file) {
            return new JsonResponse(['error' => 'No file uploaded'], 400);
        }

        $allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif'];
        if (!in_array($file->getMimeType(), $allowedMimeTypes)) {
            return new JsonResponse(['error' => 'Invalid file type. Only JPG, PNG and GIF are allowed.'], 400);
        }

        $fileName = 'avatar-org-' . $id . '-' . uniqid() . '.' . $file->guessExtension();
        $uploadDir = $this->getParameter('kernel.project_dir') . '/public/uploads/avatars';

        try {
            $file->move($uploadDir, $fileName);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Could not save file'], 500);
        }

        // Save path in DB
        $org->setAVATAR('/uploads/avatars/' . $fileName);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Avatar uploaded', 'url' => $org->getAVATAR()], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/organizations/{id}/avatar', name: 'api_organizations_avatar_options', methods: ['OPTIONS'])]
    public function avatarOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
