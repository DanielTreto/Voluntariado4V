<?php

namespace App\Controller;

use App\Entity\Volunteer;
use App\Repository\VolunteerRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;

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

    #[Route('/volunteers', name: 'api_volunteers_create', methods: ['POST'])]
    public function create(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator): JsonResponse
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
        $volunteer->setCODCICLO($data['course'] ?? '');
        $volunteer->setESTADO('PENDIENTE');

        // Validation
        $errors = $validator->validate($volunteer);
        if (count($errors) > 0) {
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
        $response->headers->set('Access-Control-Allow-Methods', 'POST, GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
