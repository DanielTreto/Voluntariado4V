<?php

namespace App\Controller;

use App\Repository\VolunteerRepository;
use App\Repository\OrganizationRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/api')]
class AuthController extends AbstractController
{
    #[Route('/login', name: 'api_login', methods: ['POST'])]
    public function login(Request $request, VolunteerRepository $volRepo, OrganizationRepository $orgRepo): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $email = $data['email'] ?? '';
        $password = $data['password'] ?? '';

        if (!$email || !$password) {
            return new JsonResponse(['error' => 'Email and password are required'], 400);
        }

        // Check Volunteer
        $volunteer = $volRepo->findOneBy(['CORREO' => $email]);
        if ($volunteer && $volunteer->getPASSWORD() === $password) {
            return new JsonResponse([
                'success' => true,
                'role' => 'volunteer',
                'id' => $volunteer->getCODVOL(),
                'name' => $volunteer->getNOMBRE(),
                'email' => $volunteer->getCORREO()
            ]);
        }

        // Check Organization
        $org = $orgRepo->findOneBy(['CORREO' => $email]);
        if ($org && $org->getPASSWORD() === $password) {
            return new JsonResponse([
                'success' => true,
                'role' => 'organization',
                'id' => $org->getCODORG(),
                'name' => $org->getNOMBRE(),
                'email' => $org->getCORREO()
            ]);
        }

        return new JsonResponse(['error' => 'Invalid credentials'], 401);
    }

    #[Route('/login', name: 'api_login_options', methods: ['OPTIONS'])]
    public function loginOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
