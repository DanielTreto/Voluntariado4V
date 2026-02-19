<?php

namespace App\Controller;

use App\Entity\Organizacion;
use App\Repository\OrganizationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Doctrine\DBAL\Exception\UniqueConstraintViolationException;
use App\Dto\OrganizationDto;
use App\Dto\ActivityDto;
use App\Entity\Credenciales;
use OpenApi\Attributes as OA;
use Nelmio\ApiDocBundle\Attribute\Model;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;



#[Route('/api')]
class OrganizationController extends AbstractController
{
    #[Route('/organizations', name: 'api_organizations_index', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of organizations',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(ref: new Model(type: OrganizationDto::class))
        )
    )]
    #[OA\Tag(name: 'Organizations')]
    public function index(OrganizationRepository $orgRepository): JsonResponse
    {
        $orgs = $orgRepository->findAll();
        $data = array_map(fn($org) => OrganizationDto::fromEntity($org), $orgs);

        return new JsonResponse($data);
    }


    #[Route('/organizations/{id}', name: 'api_organizations_show', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns an organization detail',
        content: new Model(type: OrganizationDto::class)
    )]
    #[OA\Response(response: 404, description: 'Organization not found')]
    #[OA\Tag(name: 'Organizations')]
    public function show(string $id, OrganizationRepository $orgRepository): JsonResponse
    {
        $org = $orgRepository->find($id);

        if (!$org) {
            return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        return new JsonResponse(OrganizationDto::fromEntity($org));
    }


    #[Route('/organizations', name: 'api_organizations_create', methods: ['POST'])]
    #[OA\Response(
        response: 201,
        description: 'Organization created',
        content: new Model(type: OrganizationDto::class)
    )]
    #[OA\Tag(name: 'Organizations')]
    public function create(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator, OrganizationRepository $orgRepository): JsonResponse
    {
        // ... (existing creation logic)
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
        $org->setESTADO(($data['role'] ?? null) === 'admin' ? 'ACTIVO' : 'PENDIENTE');

        $newId = $orgRepository->findNextId();
        $org->setCODORG($newId);

        $cred = new Credenciales();
        $cred->setOrganizacion($org);
        $cred->setUserType('ORGANIZACION');
        $cred->setCorreo($data['email'] ?? '');
        $cred->setPassword($data['password'] ?? '');
        $entityManager->persist($cred);

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
            $msg = $e->getMessage();
            if (str_contains($msg, 'CORREO')) {
                return new JsonResponse(['errors' => ['CORREO' => 'Ya existe una organización con este correo electrónico.']], 400);
            } elseif (str_contains($msg, 'TELEFONO')) {
                return new JsonResponse(['errors' => ['TELEFONO' => 'Ya existe una organización con este número de teléfono.']], 400);
            }
            return new JsonResponse(['errors' => ['DUPLICADO' => 'Ya existe una organización con estos datos.']], 400);
        }

        return new JsonResponse(OrganizationDto::fromEntity($org), 201);
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

        return new JsonResponse(['status' => 'Organization status updated', 'newStatus' => $newStatus], 200);
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
        if (isset($data['status'])) $org->setESTADO($data['status']);

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

        return new JsonResponse(['status' => 'Organization updated'], 200);
    }

    #[Route('/organizations/{id}/activities', name: 'api_organizations_activities', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the activities of an organization',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(ref: new Model(type: ActivityDto::class))
        )
    )]
    #[OA\Tag(name: 'Organizations')]
    public function organizationActivities(string $id, OrganizationRepository $orgRepository, \App\Repository\ActivityRepository $activityRepository): JsonResponse
    {
        $org = $orgRepository->find($id);
        if (!$org) {
             return new JsonResponse(['error' => 'Organization not found'], 404);
        }

        $activities = $activityRepository->findBy(['organizacion' => $id]);
        $data = array_map(fn($act) => ActivityDto::fromEntity($act), $activities);

        return new JsonResponse($data);
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

        return new JsonResponse(['status' => 'Avatar uploaded', 'url' => $org->getAVATAR()], 200);
    }


}
