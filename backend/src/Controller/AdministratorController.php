<?php

namespace App\Controller;

use App\Entity\Administrator;
use App\Repository\AdministratorRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use App\Entity\Volunteer;
use App\Entity\Organizacion;

#[Route('/api')]
class AdministratorController extends AbstractController
{
    #[Route('/admin/{id}', name: 'api_admin_show', methods: ['GET'])]
    public function show(int $id, AdministratorRepository $repo): JsonResponse
    {
        $admin = $repo->find($id);

        if (!$admin) {
            return new JsonResponse(['error' => 'Administrator not found'], 404);
        }

        $data = [
            'id' => $admin->getId(),
            'name' => $admin->getNombre(),
            'apellidos' => $admin->getApellidos(),
            'email' => $admin->getCorreo(),
            'phone' => $admin->getTelefono(),
            'photoUrl' => $admin->getFoto()
        ];

        return $this->json($data);
    }

    #[Route('/admin/{id}', name: 'api_admin_update', methods: ['PUT'])]
    public function update(int $id, Request $request, EntityManagerInterface $em, AdministratorRepository $repo, ValidatorInterface $validator): JsonResponse
    {
        $admin = $repo->find($id);

        if (!$admin) {
            return new JsonResponse(['error' => 'Administrator not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (!$data) {
             return new JsonResponse(['error' => 'Invalid JSON'], 400);
        }

        if (isset($data['name'])) $admin->setNombre($data['name']);
        if (isset($data['apellidos'])) $admin->setApellidos($data['apellidos']);
        if (isset($data['email'])) $admin->setCorreo($data['email']);
        if (isset($data['phone'])) $admin->setTelefono($data['phone']);
        if (isset($data['photoUrl'])) $admin->setFoto($data['photoUrl']);

        // Basic validation
        $errors = $validator->validate($admin);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(['errors' => $errorMessages], 400);
        }

        $em->flush();

        return new JsonResponse(['status' => 'Administrator updated']);
    }
    
    // Explicit Options method for CORS
    #[Route('/admin/{id}', name: 'api_admin_options', methods: ['OPTIONS'])]
    public function options(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, PUT, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
