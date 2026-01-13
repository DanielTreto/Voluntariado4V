<?php

namespace App\Controller;

use App\Entity\Actividad;
use App\Entity\Organizacion;
use App\Repository\ActivityRepository;
use App\Repository\OrganizationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use App\Repository\VolunteerRepository;
use App\Entity\Volunteer;

#[Route('/api')]
class ActivityController extends AbstractController
{
    #[Route('/activities', name: 'api_activities_index', methods: ['GET'])]
    public function index(ActivityRepository $activityRepository, OrganizationRepository $orgRepository, EntityManagerInterface $entityManager): JsonResponse
    {
        // 1. Auto-update status for finished activities
        $activities = $activityRepository->findAll();
        /*
        $now = new \DateTime();
        $updated = false;

        foreach ($activities as $act) {
            if ($act->getESTADO() === 'EN_PROGRESO' && $act->getFECHA_FIN() < $now) {
                $act->setESTADO('FINALIZADA');
                $updated = true;
            }
        }

        if ($updated) {
            $entityManager->flush();
        }
        */

        // 2. Prepare response
        $data = [];
        foreach ($activities as $act) {
            $org = $orgRepository->find($act->getCODORG());
            $data[] = [
                'id' => $act->getCODACT(),
                'title' => $act->getNOMBRE(),
                'description' => $act->getDESCRIPCION(),
                'location' => 'Ubicación Placeholder', // Missing location in BD
                'date' => $act->getFECHA_INICIO()->format('Y-m-d'),
                'endDate' => $act->getFECHA_FIN()->format('Y-m-d'),
                'image' => 'assets/images/activity-1.jpg', // Placeholder
                'organization' => $org ? [
                    'id' => $org->getCODORG(),
                    'name' => $org->getNOMBRE(),
                    'logo' => 'assets/images/org-default.png'
                ] : null,
                'volunteers' => [], // Missing relation logic for now
                'type' => 'Social', // Placeholder, need join with TIPO_ACTIVIDAD
                'status' => $act->getESTADO(),
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activities', name: 'api_activities_create', methods: ['POST'])]
    public function create(Request $request, EntityManagerInterface $entityManager, OrganizationRepository $orgRepository, ValidatorInterface $validator): JsonResponse
    {
        $data = json_decode($request->getContent(), true);

        if (!$data) {
            return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        $actividad = new Actividad();
        $actividad->setNOMBRE($data['title'] ?? '');
        $actividad->setDESCRIPCION($data['description'] ?? '');

        try {
            $actividad->setFECHA_INICIO(new \DateTime($data['date']));
            // Assume 2 hour session if not provided, or logic for end date
            $endDate = new \DateTime($data['date']);
            if (isset($data['duration'])) {
                // Format 'H:i' -> 'H:i:00'
                $actividad->setDURACION_SESION($data['duration'] . ':00');
            } else {
                $actividad->setDURACION_SESION('02:00:00');
            }
            $actividad->setFECHA_FIN($endDate); // Simplification: starts and ends same day
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Invalid date format'], 400);
        }

        $actividad->setN_MAX_VOLUNTARIOS(10); // Default or from request
        $actividad->setESTADO('PENDIENTE');

        // Link Organization
        if (isset($data['organizationId'])) {
            $org = $orgRepository->find($data['organizationId']);
            if ($org) {
                $actividad->setCODORG($org->getCODORG());
            }
        } else {
            // Fallback for testing if no logic provided
            // In real app, authenticated user's org
            $orgs = $orgRepository->findAll();
            if (count($orgs) > 0) {
                $actividad->setCODORG($orgs[0]->getCODORG());
            }
        }

        // Validation
        $errors = $validator->validate($actividad);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        $entityManager->persist($actividad);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Activity created', 'id' => $actividad->getCODACT()], 201);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');

        return $response;
    }

    #[Route('/activities/{id}/status', name: 'api_activities_update_status', methods: ['PATCH'])]
    public function updateStatus(int $id, Request $request, EntityManagerInterface $entityManager, ActivityRepository $activityRepository): JsonResponse
    {
        $act = $activityRepository->find($id);

        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        $newStatus = $data['status'] ?? null;

        $validStatuses = ['PENDIENTE', 'EN_PROGRESO', 'DENEGADA', 'FINALIZADA'];

        if (!$newStatus || !in_array($newStatus, $validStatuses)) {
            return new JsonResponse(['error' => 'Invalid status. Allowed: ' . implode(', ', $validStatuses)], 400);
        }

        $act->setESTADO($newStatus);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Activity status updated', 'newStatus' => $newStatus], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;

    }

    #[Route('/activities/{id}', name: 'api_activities_update', methods: ['PUT'])]
    public function update(int $id, Request $request, EntityManagerInterface $entityManager, ActivityRepository $activityRepository): JsonResponse
    {
        $act = $activityRepository->find($id);

        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        
        if (isset($data['title'])) {
            $act->setNOMBRE($data['title']);
        }
        if (isset($data['description'])) {
            $act->setDESCRIPCION($data['description']);
        }
        if (isset($data['date'])) {
            try {
                $act->setFECHA_INICIO(new \DateTime($data['date']));
                // Keeping end date sync logic simple for now
                $act->setFECHA_FIN(new \DateTime($data['date'])); 
            } catch (\Exception $e) {
                // Ignore invalid date for update or handle error
            }
        }
        
        // Add other fields as needed

        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Activity updated'], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activities/{id}', name: 'api_activities_update_options', methods: ['OPTIONS'])]
    public function updateOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'PUT, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/activities', name: 'api_activities_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/activities/{id}/status', name: 'api_activities_update_status_options', methods: ['OPTIONS'])]
    public function updateStatusOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'PATCH, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/activities/{id}/signup', name: 'api_activities_signup', methods: ['POST'])]
    public function signup(int $id, Request $request, EntityManagerInterface $entityManager, ActivityRepository $activityRepository, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $act = $activityRepository->find($id);
        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        $volunteerId = $data['volunteerId'] ?? null;

        if (!$volunteerId) {
            return new JsonResponse(['error' => 'Volunteer ID is required'], 400);
        }

        $volunteer = $volunteerRepository->find($volunteerId);
        if (!$volunteer) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        // Validations
        if ($act->getESTADO() !== 'PENDIENTE' && $act->getESTADO() !== 'EN_PROGRESO') {
             return new JsonResponse(['error' => 'Activity is not available for signup'], 400);
        }

        if ($act->getVoluntarios()->count() >= $act->getN_MAX_VOLUNTARIOS()) {
            return new JsonResponse(['error' => 'Activity is full'], 400);
        }

        if ($act->getVoluntarios()->contains($volunteer)) {
             return new JsonResponse(['error' => 'Volunteer already signed up'], 400);
        }

        $act->addVoluntario($volunteer);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Signed up successfully'], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activities/{id}/signup', name: 'api_activities_signup_options', methods: ['OPTIONS'])]
    public function signupOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }

    #[Route('/activities/{id}/volunteers', name: 'api_activities_volunteers', methods: ['GET'])]
    public function activityVolunteers(int $id, ActivityRepository $activityRepository): JsonResponse
    {
        $act = $activityRepository->find($id);
        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        $volunteers = $act->getVoluntarios();
        $data = [];

        foreach ($volunteers as $v) {
            $data[] = [
                'id' => $v->getCODVOL(),
                'name' => $v->getNOMBRE() . ' ' . $v->getAPELLIDO1(),
                'email' => $v->getCORREO(),
                'phone' => $v->getTELEFONO()
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activities/{id}/volunteers', name: 'api_activities_volunteers_options', methods: ['OPTIONS'])]
    public function activityVolunteersOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
    #[Route('/activities/{id}/volunteers/{volunteerId}', name: 'api_activities_remove_volunteer', methods: ['DELETE'])]
    public function removeVolunteer(int $id, int $volunteerId, EntityManagerInterface $entityManager, ActivityRepository $activityRepository, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $act = $activityRepository->find($id);
        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        $volunteer = $volunteerRepository->find($volunteerId);
        if (!$volunteer) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        if (!$act->getVoluntarios()->contains($volunteer)) {
             return new JsonResponse(['error' => 'Volunteer is not signed up for this activity'], 400);
        }

        $act->removeVoluntario($volunteer);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Volunteer removed successfully'], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activities/{id}/volunteers/{volunteerId}', name: 'api_activities_remove_volunteer_options', methods: ['OPTIONS'])]
    public function removeVolunteerOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'DELETE, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
