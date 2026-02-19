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
use App\Repository\TipoActividadRepository;
use App\Entity\Volunteer;
use App\Dto\ActivityDto;
use OpenApi\Attributes as OA;
use Nelmio\ApiDocBundle\Attribute\Model;


#[Route('/api')]
class ActivityController extends AbstractController
{
    #[Route('/activities', name: 'api_activities_index', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of activities',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(ref: new Model(type: ActivityDto::class))
        )
    )]
    #[OA\Parameter(
        name: 'organizationId',
        in: 'query',
        description: 'Filter by organization ID',
        schema: new OA\Schema(type: 'string')
    )]
    #[OA\Tag(name: 'Activities')]
    public function index(Request $request, ActivityRepository $activityRepository): JsonResponse
    {
        $orgId = $request->query->get('organizationId');

        $activities = $activityRepository->findWithFilters($orgId);


        $data = array_map(fn($act) => ActivityDto::fromEntity($act), $activities);

        return new JsonResponse($data);
    }


    #[Route('/activities', name: 'api_activities_create', methods: ['POST'])]
    #[OA\RequestBody(
        description: 'Activity data',
        required: true,
        content: new OA\JsonContent(
            properties: [
                new OA\Property(property: 'title', type: 'string'),
                new OA\Property(property: 'description', type: 'string'),
                new OA\Property(property: 'date', type: 'string', format: 'date-time'),
                new OA\Property(property: 'duration', type: 'string', example: '02:00'),
                new OA\Property(property: 'typeId', type: 'integer'),
                new OA\Property(property: 'organizationId', type: 'string'),
                new OA\Property(property: 'ods', type: 'integer'),
                new OA\Property(property: 'maxVolunteers', type: 'integer')
            ]
        )
    )]
    #[OA\Response(
        response: 201,
        description: 'Activity created',
        content: new Model(type: ActivityDto::class)
    )]
    #[OA\Tag(name: 'Activities')]
    public function create(Request $request, EntityManagerInterface $entityManager, OrganizationRepository $orgRepository, TipoActividadRepository $tipoActividadRepository, \App\Repository\OdsRepository $odsRepository, ValidatorInterface $validator): JsonResponse
    {
        $data = json_decode($request->getContent(), true);

        if (!$data) {
            return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        $actividad = new Actividad();
        $actividad->setNOMBRE($data['title'] ?? '');
        $actividad->setDESCRIPCION($data['description'] ?? '');
        $actividad->setUBICACION($data['location'] ?? null);
        $actividad->setIMAGEN($data['image'] ?? null);
        
        try {
            // Standardizing on ISO format (ATOM/ISO8601)
            $dateStr = $data['date'] ?? null;
            if (!$dateStr) {
                 return new JsonResponse(['error' => 'Date is required'], 400);
            }
            $actividad->setFECHA_INICIO(new \DateTime($dateStr));
            
            if (isset($data['duration'])) {
                $duration = $data['duration'];
                if (strlen($duration) === 5) $duration .= ':00'; // H:i -> H:i:s
                $actividad->setDURACION_SESION($duration);
            } else {
                $actividad->setDURACION_SESION('02:00:00');
            }
            
            // For now ends same day, we can refine this
            $actividad->setFECHA_FIN(clone $actividad->getFECHA_INICIO());
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Invalid date format. Use ISO 8601 (e.g. 2024-12-31T10:00:00Z)'], 400);
        }

        // Link Activity Type
        if (isset($data['typeId'])) {
            $tipo = $tipoActividadRepository->find($data['typeId']);
            if ($tipo) $actividad->addTipoActividad($tipo);
        }

        // Link ODS
        if (isset($data['ods'])) {
             $ods = $odsRepository->find($data['ods']);
             if ($ods) $actividad->addOd($ods);
        }

        $actividad->setN_MAX_VOLUNTARIOS($data['maxVolunteers'] ?? 10);
        $actividad->setESTADO(($data['role'] ?? null) === 'admin' ? 'EN_PROGRESO' : 'PENDIENTE');

        // Link Organization
        if (isset($data['organizationId'])) {
            $org = $orgRepository->find($data['organizationId']);
            if ($org) {
                $actividad->setOrganizacion($org);
            } else {
                return new JsonResponse(['error' => 'Organization not found'], 404);
            }
        }

        // Validation
        $errors = $validator->validate($actividad);
        if (count($errors) > 0) {
            $errorList = [];
            foreach ($errors as $error) {
                $errorList[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorList], 400);
        }

        $entityManager->persist($actividad);
        $entityManager->flush();

        return new JsonResponse(ActivityDto::fromEntity($actividad), 201);
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

        return new JsonResponse(['status' => 'Activity status updated', 'newStatus' => $newStatus], 200);

    }

    #[Route('/activities/{id}', name: 'api_activities_update', methods: ['PUT'])]
    public function update(int $id, Request $request, EntityManagerInterface $entityManager, ActivityRepository $activityRepository, TipoActividadRepository $tipoActividadRepository, \App\Repository\OdsRepository $odsRepository, ValidatorInterface $validator): JsonResponse
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
                $newDate = new \DateTime($data['date']);
                // Strict check: Cannot update date to the past
                if ($newDate < new \DateTime('today')) {
                    return new JsonResponse(['error' => 'La fecha de la actividad no puede ser anterior a hoy.'], 400);
                }
                $act->setFECHA_INICIO($newDate);
                $act->setFECHA_FIN($newDate); 
            } catch (\Exception $e) {
                return new JsonResponse(['error' => 'Formato de fecha inválido.'], 400);
            }
        }

        if (isset($data['maxVolunteers'])) {
            $max = (int) $data['maxVolunteers'];
            // Strict check: Cannot set max volunteers below current count
            if ($max < $act->getVoluntarios()->count()) {
                return new JsonResponse(['error' => 'No puedes reducir el cupo por debajo del número de voluntarios ya inscritos (' . $act->getVoluntarios()->count() . ').'], 400);
            }
            $act->setN_MAX_VOLUNTARIOS($max);
        }

        // Validate entire entity constraints
        $errors = $validator->validate($act);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        $entityManager->flush();


        return new JsonResponse(['status' => 'Activity updated'], 200);
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

        // Check if caller is Admin or the Organization owner of this activity
        $authUser = $request->attributes->get('authenticated_user');
        $isAdmin = ($data['role'] ?? null) === 'admin'; // Legacy check
        $isOwner = false;

        if ($authUser instanceof \App\Entity\Administrator) {
            $isAdmin = true;
        } elseif ($authUser instanceof \App\Entity\Organizacion) {
            if ($act->getOrganizacion() && $act->getOrganizacion()->getCODORG() === $authUser->getCODORG()) {
                $isOwner = true;
            }
        }

        if ($existingRequest) {
            if ($isAdmin || $isOwner) {
                $existingRequest->setStatus('ACEPTADA');
                if (!$act->getVoluntarios()->contains($volunteer)) {
                    $act->addVoluntario($volunteer);
                    $entityManager->persist($act);
                }
                $entityManager->flush();
                return new JsonResponse(['status' => 'Request updated and accepted by ' . ($isAdmin ? 'admin' : 'organization')], 200);
            } else {
                return new JsonResponse(['error' => 'Request already pending or processed'], 400);
            }
        }

        // Create Request
        $solicitud = new \App\Entity\Solicitud();
        $solicitud->setVolunteer($volunteer);
        $solicitud->setActividad($act);
        
        if ($isAdmin || $isOwner) {
             $solicitud->setStatus('ACEPTADA');
             $act->addVoluntario($volunteer);
             $entityManager->persist($act);
        } else {
             $solicitud->setStatus('PENDIENTE');
        }
        
        $solicitud->setFechaSolicitud(new \DateTime());
        
        $entityManager->persist($solicitud);
        $entityManager->flush();

        return new JsonResponse(['status' => 'Request sent successfully'], 200);
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

        return new JsonResponse($data);
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

        return new JsonResponse(['status' => 'Volunteer removed successfully'], 200);
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

        return new JsonResponse(['status' => 'Image uploaded successfully', 'path' => $act->getIMAGEN()], 200);
    }

    #[Route('/activities/{id}', name: 'api_activities_delete', methods: ['DELETE'])]
    public function delete(int $id, EntityManagerInterface $entityManager, ActivityRepository $activityRepository): JsonResponse
    {
        $act = $activityRepository->find($id);

        if (!$act) {
            return new JsonResponse(['error' => 'Activity not found'], 404);
        }

        // 1. Manually remove related Solicitud entities (Constraint Fix)
        $solicitudes = $entityManager->getRepository(\App\Entity\Solicitud::class)->findBy(['actividad' => $act]);
        foreach ($solicitudes as $solicitud) {
            $entityManager->remove($solicitud);
        }

        // 2. Remove Activity (Doctrine handles ManyToMany join tables like VOL_PARTICIPA_ACT automatically)
        $entityManager->remove($act);
        $entityManager->flush();

        return new JsonResponse(['status' => 'Activity deleted'], 200);
    }
}
