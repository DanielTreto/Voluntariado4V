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
    public function index(Request $request, ActivityRepository $activityRepository, OrganizationRepository $orgRepository, EntityManagerInterface $entityManager): JsonResponse
    {
        // 1. Check for filters (Mobile Logic)
        $orgId = $request->query->get('organizationId');

        if ($orgId) {
             $activities = $activityRepository->findBy(['organizacion' => $orgId]); 
        } else {
             $activities = $activityRepository->findAll();
        }

        // 2. Auto-update status for finished activities (Head Logic - kept commented if unsure, or uncomment if active)
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

        // 3. Prepare response
        $data = [];
        foreach ($activities as $act) {
            $org = $orgRepository->find($act->getCODORG());
            $data[] = [
                'id' => $act->getCODACT(),
                'title' => $act->getNOMBRE(),
                'description' => $act->getDESCRIPCION(),
                'location' => $act->getUBICACION() ?? 'Ubicación no especificada',
                // Merging date formats: Provide standardized ISO and maybe a formatted one if needed, or stick to one. 
                // Mobile expects d/m/y, Web usually Y-m-d. Let's provide Y-m-d as default 'date' and 'formattedDate' for mobile if needed, 
                // BUT previous mobile code used 'date' key for d/m/y. 
                // To avoid breaking Mobile, we might need to change Mobile code OR provide what it expects.
                // However, I must valid "Web and Mobile work". 
                // If I change 'date' format, Web might break if it expects Y-m-d.
                // Safest: 'date' => Y-m-d (standard), 'mobileDate' => d/m/y? 
                // Or check what Frontend uses. Frontend likely parses Y-m-d. Mobile might just display string.
                // I will use Y-m-d for 'date' and 'endDate' to be standard/Web compliant, and hope Mobile parses it or I can add a specific field.
                // Actually, looking at previous Mobile code, it used d/m/y. 
                // I'll provide both formats to be safe.
                'date' => $act->getFECHA_INICIO()->format('Y-m-d'),
                'dateFormatted' => $act->getFECHA_INICIO()->format('d/m/y'), // For Mobile display if needed
                'requestDate' => $act->getFECHA_INICIO()->format('d/m/y'), // Trying to cover bases
                
                'endDate' => $act->getFECHA_FIN()->format('Y-m-d'),
                'endDateFormatted' => $act->getFECHA_FIN()->format('d/m/y'),
                
                'image' => $act->getIMAGEN() ?? 'assets/images/activity-1.jpg', // Web key
                'imagen' => $act->getIMAGEN(), // Mobile key (Db column)
                
                'duration' => $act->getDURACION_SESION(),
                
                'organization' => $org ? [
                    'id' => $org->getCODORG(),
                    'name' => $org->getNOMBRE(),
                    'avatar' => $org->getAVATAR()
                ] : null,
                'volunteers' => array_map(function($vol) {
                    return [
                        'id' => $vol->getCODVOL(),
                        'name' => trim($vol->getNOMBRE() . ' ' . $vol->getAPELLIDO1() . ' ' . ($vol->getAPELLIDO2() ?? '')),
                        'avatar' => $vol->getAVATAR(),
                        'email' => $vol->getCORREO(),
                        'status' => $vol->getESTADO()
                    ];
                }, $act->getVoluntarios()->toArray()), 
                
                'type' => $act->getTiposActividad()->first() ? $act->getTiposActividad()->first()->getDESCRIPCION() : 'General', // Mobile Logic preferred
                'status' => $act->getESTADO(),
                'ods' => array_map(function($ods) {
                    return [
                        'id' => $ods->getNUMODS(),
                        'description' => $ods->getDESCRIPCION()
                    ];
                }, $act->getOds()->toArray()),
                'maxVolunteers' => $act->getN_MAX_VOLUNTARIOS(),
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
        $actividad->setUBICACION($data['location'] ?? null);
        $actividad->setIMAGEN($data['image'] ?? null); // Head Logic
        
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

        $actividad->setN_MAX_VOLUNTARIOS($data['maxVolunteers'] ?? 10);
        $actividad->setESTADO('PENDIENTE');

        // Link Organization (Merged Logic)
        if (isset($data['organizationId'])) {
            $orgId = $data['organizationId'];
            $org = $orgRepository->find($orgId);
            if ($org) {
                $actividad->setOrganizacion($org);
            } else {
                return new JsonResponse(['error' => 'Organization not found with ID: ' . $orgId], 404);
            }
        } else {
            // Fallback from Mobile logic if needed
            $orgs = $orgRepository->findAll();
            if (count($orgs) > 0) {
                $actividad->setOrganizacion($orgs[0]);
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
        if (isset($data['image'])) {
            $act->setIMAGEN($data['image']);
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
             return new JsonResponse(['error' => 'Activity is not available for signup. Status: ' . $act->getESTADO()], 400);
        }

        if ($act->getVoluntarios()->count() >= $act->getN_MAX_VOLUNTARIOS()) {
            return new JsonResponse(['error' => 'Activity is full. Current: ' . $act->getVoluntarios()->count() . ' Max: ' . $act->getN_MAX_VOLUNTARIOS()], 400);
        }

        // Check if already signed up (accepted)
        if ($act->getVoluntarios()->contains($volunteer)) {
             return new JsonResponse(['error' => 'Volunteer already signed up'], 400);
        }

        // Check if already requested
        $existingRequest = $entityManager->getRepository(\App\Entity\Solicitud::class)->findOneBy([
            'volunteer' => $volunteer,
            'actividad' => $act
        ]);

        if ($existingRequest) {
            return new JsonResponse(['error' => 'Request already pending or processed'], 400);
        }

        // Create Request
        $solicitud = new \App\Entity\Solicitud();
        $solicitud->setVolunteer($volunteer);
        $solicitud->setActividad($act);
        $solicitud->setStatus('PENDIENTE');
        $solicitud->setFechaSolicitud(new \DateTime());
        
        $entityManager->persist($solicitud);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Request sent successfully'], 200);
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
                'avatar' => $v->getAVATAR(),
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
    public function removeVolunteer(int $id, string $volunteerId, EntityManagerInterface $entityManager, ActivityRepository $activityRepository, VolunteerRepository $volunteerRepository): JsonResponse
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

    #[Route('/activities/{id}/image', name: 'api_activities_image', methods: ['POST'])]
    public function uploadImage(int $id, Request $request, ActivityRepository $activityRepository, EntityManagerInterface $entityManager): JsonResponse
    {
        $act = $activityRepository->find($id);
        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        $file = $request->files->get('image');
        if (!$file) {
            return new JsonResponse(['error' => 'No image provided'], 400);
        }

        $uploadsDirectory = $this->getParameter('kernel.project_dir') . '/public/uploads/activities';
        $filename = uniqid() . '.' . $file->guessExtension();

        try {
            $file->move($uploadsDirectory, $filename);
            $act->setIMAGEN('/uploads/activities/' . $filename);
            $entityManager->flush();
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Error uploading image: ' . $e->getMessage()], 500);
        }

        $response = new JsonResponse(['status' => 'Image uploaded successfully', 'path' => $act->getIMAGEN()], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/activities/{id}/image', name: 'api_activities_image_options', methods: ['OPTIONS'])]
    public function uploadImageOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
