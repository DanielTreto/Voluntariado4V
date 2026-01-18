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
    public function show(string $id, AdministratorRepository $repo): JsonResponse
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
            'phone' => $admin->getTelefono(),
            'avatar' => $admin->getAVATAR()
        ];

        return $this->json($data);
    }

    #[Route('/admin/{id}', name: 'api_admin_update', methods: ['PUT'])]
    public function update(string $id, Request $request, EntityManagerInterface $em, AdministratorRepository $repo, ValidatorInterface $validator): JsonResponse
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
        if (isset($data['phone'])) $admin->setTelefono($data['phone']);
        // photoUrl is handled by uploadAvatar endpoint, avoid manual set unless strictly needed
        // if (isset($data['photoUrl'])) $admin->setAVATAR($data['photoUrl']);

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
    #[Route('/admin/{id}/avatar', name: 'api_admin_upload_avatar', methods: ['POST'])]
    public function uploadAvatar(string $id, Request $request, EntityManagerInterface $entityManager, AdministratorRepository $adminRepository): JsonResponse
    {
        $admin = $adminRepository->find($id);
        if (!$admin) {
            return new JsonResponse(['error' => 'Administrator not found'], 404);
        }

        $file = $request->files->get('avatar');
        if (!$file) {
            return new JsonResponse(['error' => 'No file uploaded'], 400);
        }

        $allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif'];
        if (!in_array($file->getMimeType(), $allowedMimeTypes)) {
            return new JsonResponse(['error' => 'Invalid file type. Only JPG, PNG and GIF are allowed.'], 400);
        }

        $fileName = 'avatar-admin-' . $id . '-' . uniqid() . '.' . $file->guessExtension();
        $uploadDir = $this->getParameter('kernel.project_dir') . '/public/uploads/avatars';

        try {
            $file->move($uploadDir, $fileName);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Could not save file'], 500);
        }

        // Save path in DB
        $admin->setAVATAR('/uploads/avatars/' . $fileName);
        $entityManager->flush();

        $response = new JsonResponse(['status' => 'Avatar uploaded', 'url' => $admin->getAVATAR()], 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/admin/{id}/avatar', name: 'api_admin_avatar_options', methods: ['OPTIONS'])]
    public function avatarOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
