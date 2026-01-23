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
use App\Entity\Disponibilidad;
use App\Repository\CicloRepository;
use App\Repository\TipoActividadRepository;

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
                'avatar' => $v->getAVATAR(),
                'preferences' => array_map(fn($t) => $t->getCODTIPO(), $v->getPreferencias()->toArray()),
                'availability' => array_map(fn($d) => [
                    'day' => $d->getDIA(),
                    'hours' => $d->getNUM_HORAS(),
                    'time' => method_exists($d, 'getHORA') ? $d->getHORA() : null // Support both if method exists
                ], $v->getDisponibilidades()->toArray()),
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
            'avatar' => $v->getAVATAR(),
            'preferences' => array_map(fn($t) => $t->getCODTIPO(), $v->getPreferencias()->toArray()),
            'availability' => array_map(fn($d) => [
                'day' => $d->getDIA(),
                'hours' => $d->getNUM_HORAS(),
                'time' => method_exists($d, 'getHORA') ? $d->getHORA() : null
            ], $v->getDisponibilidades()->toArray()),
        ];

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers', name: 'api_volunteers_create', methods: ['POST'])]
    public function create(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator, CicloRepository $cicloRepository, VolunteerRepository $volunteerRepository, TipoActividadRepository $tipoActividadRepository): JsonResponse
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
        
        // Handle Preferences
        if (isset($data['preferences']) && is_array($data['preferences'])) {
            // Needed to inject TipoActividadRepository in create method signature
            // Just fetching from EM or assume passed
        }


        $volunteer->setESTADO('PENDIENTE');
        
        // Generate Custom ID
        $newId = $volunteerRepository->findNextId();
        $volunteer->setCODVOL((string)$newId);

        // Create Credentials
        $credenciales = new Credenciales();
        $credenciales->setVoluntario($volunteer); // Link directly to object
        $credenciales->setUserType('VOLUNTARIO');
        $credenciales->setCorreo($data['email'] ?? '');
        $credenciales->setPassword($data['password'] ?? '');
        $entityManager->persist($credenciales);

        // Process Preferences (Interests) - Preserve Web Logic (IDs)
        if (isset($data['preferences']) && is_array($data['preferences'])) {
            foreach ($data['preferences'] as $typeId) {
                $tipo = $tipoActividadRepository->find($typeId);
                if ($tipo) {
                    $volunteer->addPreferencia($tipo);
                }
            }
        }

        // Process Availability - Preserve Web Logic (Hours)
        if (isset($data['availability']) && is_array($data['availability'])) {
            foreach ($data['availability'] as $avail) {
                if (isset($avail['day']) && isset($avail['hours'])) {
                    $disponibilidad = new Disponibilidad();
                    $disponibilidad->setVoluntario($volunteer);
                    $disponibilidad->setDIA($avail['day']);
                    $disponibilidad->setNUM_HORAS((int)$avail['hours']);
                    // If Mobile sends time, we could set it too if entity supports it
                    // if (isset($avail['time'])) $disponibilidad->setHORA($avail['time']);
                    $entityManager->persist($disponibilidad);
                }
            }
        }
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
    public function update(string $id, Request $request, EntityManagerInterface $entityManager, VolunteerRepository $volunteerRepository, ValidatorInterface $validator, CicloRepository $cicloRepository, TipoActividadRepository $tipoActividadRepository): JsonResponse
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

        if (isset($data['preferences']) && is_array($data['preferences'])) {
            // Clear existing preferences
            $vPrefs = $volunteer->getPreferencias();
            foreach ($vPrefs as $pref) {
                $volunteer->removePreferencia($pref);
            }
            
            // Add new preferences
            foreach ($data['preferences'] as $typeId) {
                $tipo = $tipoActividadRepository->find($typeId);
                if ($tipo) {
                    $volunteer->addPreferencia($tipo);
                }
            }
        }

        if (isset($data['availability']) && is_array($data['availability'])) {
             $existingAvails = $volunteer->getDisponibilidades();
             $map = [];
             foreach ($existingAvails as $av) {
                 $map[$av->getDIA()] = $av;
             }

             $submittedDays = [];
             foreach ($data['availability'] as $avail) {
                 if (isset($avail['day']) && isset($avail['hours'])) {
                     $day = $avail['day'];
                     $hours = (int)$avail['hours'];
                     $submittedDays[] = $day;
                     
                     if (isset($map[$day])) {
                         // Update existing
                         $map[$day]->setNUM_HORAS($hours);
                     } else {
                         // Create new
                         $disponibilidad = new Disponibilidad();
                         $disponibilidad->setVoluntario($volunteer);
                         $disponibilidad->setDIA($day);
                         $disponibilidad->setNUM_HORAS($hours);
                         $entityManager->persist($disponibilidad);
                         $volunteer->addDisponibilidad($disponibilidad);
                     }
                 }
             }

             // Remove availabilities that are no longer present
             foreach ($map as $day => $av) {
                 if (!in_array($day, $submittedDays)) {
                     $volunteer->removeDisponibilidad($av);
                     $entityManager->remove($av);
                 }
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
    public function myActivities(string $id, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);
        if (!$volunteer) {
             return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $activities = $volunteer->getActividades();
        $data = [];

        foreach ($activities as $act) {
            // Filter out SUSPENDIDA activities
            if ($act->getESTADO() === 'SUSPENDIDA') {
                continue;
            }

            $vols = [];
            foreach ($act->getVoluntarios() as $v) {
                $fullName = $v->getNOMBRE() . ' ' . $v->getAPELLIDO1();
                if ($v->getAPELLIDO2()) {
                    $fullName .= ' ' . $v->getAPELLIDO2();
                }
                $vols[] = [
                    'id' => $v->getCODVOL(),
                    'name' => trim($fullName),
                    'avatar' => $v->getAVATAR()
                ];
            }
            $data[] = [
                'id' => $act->getCODACT(),
                'title' => $act->getNOMBRE(),
                'description' => $act->getDESCRIPCION(),
                'date' => $act->getFECHA_INICIO()->format('d/m/y H:i'),
                'endDate' => $act->getFECHA_FIN() ? $act->getFECHA_FIN()->format('Y-m-d') : null,
                'location' => $act->getUBICACION(),
                'duration' => $act->getDURACION_SESION(),
                'status' => $act->getESTADO(),
                'type' => $act->getTiposActividad()->first() ? $act->getTiposActividad()->first()->getDESCRIPCION() : 'General',
                'maxVolunteers' => $act->getN_MAX_VOLUNTARIOS(),
                'imagen' => $act->getIMAGEN(),
                'ods' => array_map(function($ods) {
                    return [
                        'id' => $ods->getNUMODS(),
                        'description' => $ods->getDESCRIPCION()
                    ];
                }, $act->getOds()->toArray()),
                'volunteers' => $vols,
                'organization' => $act->getOrganizacion() ? [
                    'id' => $act->getOrganizacion()->getCODORG(),
                    'name' => $act->getOrganizacion()->getNOMBRE(),
                    'avatar' => $act->getOrganizacion()->getAVATAR()
                ] : null
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
    #[Route('/volunteers/{id}/requests', name: 'api_volunteers_requests', methods: ['GET'])]
    public function myRequests(string $id, VolunteerRepository $volunteerRepository, EntityManagerInterface $entityManager): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);
        if (!$volunteer) {
             return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $requests = $entityManager->getRepository(\App\Entity\Solicitud::class)->findBy(['volunteer' => $volunteer]);
        $data = [];

        foreach ($requests as $req) {
            $data[] = [
                'id' => $req->getId(),
                'activityId' => $req->getActividad()->getCODACT(),
                'status' => $req->getStatus(),
                'date' => $req->getFechaSolicitud()->format('Y-m-d H:i:s'),
                'message' => $req->getMensaje() // Optional
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers/{id}/requests', name: 'api_volunteers_requests_options', methods: ['OPTIONS'])]
    public function myRequestsOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
    #[Route('/volunteers/{id}/avatar', name: 'api_volunteers_upload_avatar', methods: ['POST'])]
    public function uploadAvatar(string $id, Request $request, EntityManagerInterface $entityManager, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);
        if (!$volunteer) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $file = $request->files->get('avatar');
        if (!$file) {
            return new JsonResponse(['error' => 'No file uploaded'], 400);
        }

        $allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif'];
        if (!in_array($file->getMimeType(), $allowedMimeTypes)) {
            return new JsonResponse(['error' => 'Invalid file type. Only JPG, PNG and GIF are allowed.'], 400);
        }

        $fileName = 'avatar-' . $id . '-' . uniqid() . '.' . $file->guessExtension();
        $uploadDir = $this->getParameter('kernel.project_dir') . '/public/uploads/avatars';

        try {
            $file->move($uploadDir, $fileName);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Could not save file'], 500);
        }

        // Save path in DB (relative to public)
        $volunteer->setAVATAR('/uploads/avatars/' . $fileName);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Avatar uploaded', 'url' => $volunteer->getAVATAR()], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/volunteers/{id}/avatar', name: 'api_volunteers_avatar_options', methods: ['OPTIONS'])]
    public function avatarOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
