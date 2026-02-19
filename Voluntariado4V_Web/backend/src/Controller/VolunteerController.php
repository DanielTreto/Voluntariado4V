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
use App\Dto\VolunteerDto;
use App\Dto\ActivityDto;
use OpenApi\Attributes as OA;
use Nelmio\ApiDocBundle\Annotation\Model;

#[Route('/api')]
class VolunteerController extends AbstractController
{
    #[Route('/volunteers', name: 'api_volunteers_index', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of volunteers',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(ref: new Model(type: VolunteerDto::class))
        )
    )]
    #[OA\Tag(name: 'Volunteers')]
    public function index(VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteers = $volunteerRepository->findAll();
        $data = array_map(fn($v) => VolunteerDto::fromEntity($v), $volunteers);

        return new JsonResponse($data);
    }


    #[Route('/volunteers/{id}', name: 'api_volunteers_show', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns a volunteer detail',
        content: new Model(type: VolunteerDto::class)
    )]
    #[OA\Response(response: 404, description: 'Volunteer not found')]
    #[OA\Tag(name: 'Volunteers')]
    public function show(string $id, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $v = $volunteerRepository->find($id);

        if (!$v) {
            return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        return new JsonResponse(VolunteerDto::fromEntity($v));
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
                if (isset($avail['day']) && isset($avail['hours']) && (int)$avail['hours'] > 0) {
                    $disponibilidad = new Disponibilidad();
                    $disponibilidad->setVoluntario($volunteer);
                    $disponibilidad->setDIA($avail['day']);
                    $disponibilidad->setNUM_HORAS((int)$avail['hours']);
                    // If Mobile sends time, we could set it too if entity supports it
                    // if (isset($avail['time'])) $disponibilidad->setHORA($avail['time']);
                    $entityManager->persist($disponibilidad);
                    $volunteer->addDisponibilidad($disponibilidad);
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

        return new JsonResponse(['status' => 'Volunteer created', 'id' => $volunteer->getCODVOL()], 201);
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

        return new JsonResponse(['status' => 'Volunteer deleted'], 200);
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

        return new JsonResponse(['status' => 'Volunteer status updated', 'newStatus' => $newStatus], 200);
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

        return new JsonResponse(['status' => 'Volunteer updated'], 200);
    }

    #[Route('/volunteers/{id}/activities', name: 'api_volunteers_activities', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the activities of a volunteer',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(ref: new Model(type: ActivityDto::class))
        )
    )]
    #[OA\Tag(name: 'Volunteers')]
    public function myActivities(string $id, VolunteerRepository $volunteerRepository): JsonResponse
    {
        $volunteer = $volunteerRepository->find($id);
        if (!$volunteer) {
             return new JsonResponse(['error' => 'Volunteer not found'], 404);
        }

        $activities = $volunteer->getActividades();
        $data = array_map(fn($act) => ActivityDto::fromEntity($act), $activities->toArray());
        
        // Filter out SUSPENDIDA as per original logic if needed, 
        // but DTO transformation logic is cleaner. 
        // Original logic filtered SUSPENDIDA:
        $data = array_filter($data, fn($actDto) => $actDto->status !== 'SUSPENDIDA');

        return new JsonResponse(array_values($data));
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

        return new JsonResponse($data);
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

        return new JsonResponse(['status' => 'Avatar uploaded', 'url' => $volunteer->getAVATAR()], 200);
    }


}
