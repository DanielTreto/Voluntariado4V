<?php

namespace App\Controller;

use App\Entity\Volunteer;
use App\Repository\VolunteerRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use App\Entity\Credenciales;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use App\Entity\Ciclo;
use App\Repository\CicloRepository;

#[Route('/api')]
class VolunteerController extends AbstractController
{
    #[Route('/volunteers', name: 'api_volunteers_index', methods: ['GET'])]
    public function index(VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteers = $volunteerRepository->findAll();
        $data = [];

        foreach ($volunteers as $v) {
            $data[] = [
                'id' => $v->getCODVOL(),
                'name' => $v->getNOMBRE(),
                'surname1' => $v->getAPELLIDO1(),
                'surname2' => $v->getAPELLIDO2(),
                'email' => $v->getCORREO(),
                'phone' => $v->getTELEFONO(),
                'dni' => $v->getDNI(),
                'dateOfBirth' => $v->getFECHA_NACIMIENTO()?->format('Y-m-d'),
                'description' => $v->getDESCRIPCION(),
                'course' => $v->getCODCICLO(),
                'status' => $v->getESTADO(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers/{id}', name: 'api_volunteers_show', methods: ['GET'])]
    public function show(string $id, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $v = $volunteerRepository->find($id);

        if (!$v) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $data = [
            'id' => $v->getCODVOL(),
            'name' => $v->getNOMBRE(),
            'surname1' => $v->getAPELLIDO1(),
            'surname2' => $v->getAPELLIDO2(),
            'email' => $v->getCORREO(),
            'phone' => $v->getTELEFONO(),
            'dni' => $v->getDNI(),
            'dateOfBirth' => $v->getFECHA_NACIMIENTO()?->format('Y-m-d'),
            'description' => $v->getDESCRIPCION(),
            'course' => $v->getCODCICLO(),
            'status' => $v->getESTADO(),
        ];

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers', name: 'api_volunteers_create', methods: ['POST'])]
    public function create(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator, CicloRepository $cicloRepository, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $data = json_decode($request->getContent(), true);

        if (!$data) {
            return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        $volunteer = new Volunteer();
        $volunteer->setNOMBRE($data['name'] ?? '');
        $volunteer->setAPELLIDO1($data['surname1'] ?? '');
        $volunteer->setAPELLIDO2($data['surname2'] ?? null);
        $volunteer->setCORREO($data['email'] ?? '');
        $volunteer->setTELEFONO($data['phone'] ?? '');
        $volunteer->setDNI($data['dni'] ?? '');

        if (isset($data['dateOfBirth'])) {
            try {
                $volunteer->setFECHA_NACIMIENTO(new \DateTime($data['dateOfBirth']));
            } catch (\Exception $e) {
                // Handle invalid date format if necessary, though validation will catch blank
            }
        }

        $volunteer->setDESCRIPCION($data['description'] ?? null);
        if (isset($data['course']) && !empty($data['course'])) {
            $ciclo = $cicloRepository->find($data['course']);
            if ($ciclo) {
                $volunteer->setCiclo($ciclo);
            }
        }



        $volunteer->setESTADO('PENDIENTE');
        
        // Generate Custom ID
        $newId = $volunteerRepository->findNextId();
        $volunteer->setCODVOL($newId);

        // Create Credentials
        $credenciales = new Credenciales();
        $credenciales->setVoluntario($volunteer); // Link directly to object
        $credenciales->setUserType('VOLUNTARIO');
        $credenciales->setCorreo($data['email'] ?? '');
        $credenciales->setPassword($data['password'] ?? '');
        $entityManager->persist($credenciales);

        // Validation
        $errors = $validator->validate($volunteer);
        if (count($errors) > 0) {
            // ... validation logic ...
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        $entityManager->persist($volunteer);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Volunteer created', 'id' => $volunteer->getCODVOL()], 201);
        // CORS headers for development
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');

        return $response;
    }

    // Simple OPTIONS handler for CORS preflight
    #[Route('/volunteers', name: 'api_volunteers_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, DELETE');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/volunteers/{id}', name: 'api_volunteers_delete', methods: ['DELETE'])]
    public function delete(string $id, EntityManagerInterface $entityManager, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);

        if (!$volunteer) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $entityManager->remove($volunteer);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Volunteer deleted'], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers/{id}', name: 'api_volunteers_item_options', methods: ['OPTIONS'])]
    public function itemOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, PUT, DELETE, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
    #[Route('/volunteers/{id}/status', name: 'api_volunteers_update_status', methods: ['PATCH'])]
    public function updateStatus(string $id, Request $request, EntityManagerInterface $entityManager, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);

        if (!$volunteer) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        $newStatus = $data['status'] ?? null;

        $validStatuses = ['ACTIVO', 'SUSPENDIDO', 'PENDIENTE'];

        if (!$newStatus || !in_array($newStatus, $validStatuses)) {
            return new JsonResponse(['error' => 'Invalid status. Allowed values: ' . implode(', ', $validStatuses)], 400);
        }

        $volunteer->setESTADO($newStatus);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Volunteer status updated', 'newStatus' => $newStatus], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }
    #[Route('/volunteers/{id}/status', name: 'api_volunteers_update_status_options', methods: ['OPTIONS'])]
    public function updateStatusOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'PATCH, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/volunteers/{id}', name: 'api_volunteers_update', methods: ['PUT'])]
    public function update(string $id, Request $request, EntityManagerInterface $entityManager, VolunteerRepository $volunteerRepository, ValidatorInterface $validator, CicloRepository $cicloRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);

        if (!$volunteer) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (!$data) {
             return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        // Update fields if provided
        if (isset($data['name'])) $volunteer->setNOMBRE($data['name']);
        if (isset($data['surname1'])) $volunteer->setAPELLIDO1($data['surname1']);
        if (isset($data['surname2'])) $volunteer->setAPELLIDO2($data['surname2']);
        if (isset($data['email'])) $volunteer->setCORREO($data['email']);
        if (isset($data['phone'])) $volunteer->setTELEFONO($data['phone']);
        if (isset($data['dni'])) $volunteer->setDNI($data['dni']);
        if (isset($data['description'])) $volunteer->setDESCRIPCION($data['description']);
        if (isset($data['course'])) {
             $ciclo = $cicloRepository->find($data['course']);
             if ($ciclo) {
                 $volunteer->setCiclo($ciclo);
             }
        }
        if (isset($data['dateOfBirth'])) {
            try {
                $volunteer->setFECHA_NACIMIENTO(new \DateTime($data['dateOfBirth']));
            } catch (\Exception $e) {
                // Ignore invalid date
            }
        }

        // Validate
        $errors = $validator->validate($volunteer);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Volunteer updated'], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers/{id}/activities', name: 'api_volunteers_activities', methods: ['GET'])]
    public function myActivities(int $id, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);
        if (!$volunteer) {
             return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $activities = $volunteer->getActividades();
        $data = [];

        foreach ($activities as $act) {
            $data[] = [
                'id' => $act->getCODACT(),
                'title' => $act->getNOMBRE(),
                'description' => $act->getDESCRIPCION(),
                'date' => $act->getFECHA_INICIO()->format('Y-m-d'),
                'status' => $act->getESTADO(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers/{id}/activities', name: 'api_volunteers_activities_options', methods: ['OPTIONS'])]
    public function myActivitiesOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
