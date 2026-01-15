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
    public function login(Request $request, VolunteerRepository $volRepo, OrganizationRepository $orgRepo, \App\Repository\CredencialesRepository $credRepo): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $token = $data['token'] ?? '';
        $email = $data['email'] ?? '';
        $password = $data['password'] ?? '';

        // 1. Firebase Token Login
        if ($token) {
            // TODO: Verify Firebase token using Admin SDK or similar
            // For now, we simulate we got a UID from the token
            $uid = $token; 

            // Check Volunteer
            $volunteer = $volRepo->findOneBy(['firebaseUid' => $uid]);
            if ($volunteer) {
                return new JsonResponse([
                    'success' => true,
                    'role' => 'volunteer',
                    'id' => $volunteer->getCODVOL(),
                    'name' => $volunteer->getNOMBRE(),
                    'email' => $volunteer->getCORREO(),
                    'firebaseUid' => $volunteer->getFirebaseUid()
                ]);
            }

            // Check Organization
            $org = $orgRepo->findOneBy(['firebaseUid' => $uid]);
            if ($org) {
                return new JsonResponse([
                    'success' => true,
                    'role' => 'organization',
                    'id' => $org->getCODORG(),
                    'name' => $org->getNOMBRE(),
                    'email' => $org->getCORREO(),
                    'firebaseUid' => $org->getFirebaseUid()
                ]);
            }
        }
        // 2. SQL Email/Password Login
        elseif ($email && $password) {
            $cred = $credRepo->findOneBy(['correo' => $email]);
            
            if ($cred && $cred->getPassword() === $password) { // Plain text for now as per registration
                $volunteer = $cred->getVoluntario();
                if ($volunteer) {
                    return new JsonResponse([
                        'success' => true,
                        'role' => 'volunteer',
                        'id' => $volunteer->getCODVOL(),
                        'name' => $volunteer->getNOMBRE(),
                        'email' => $volunteer->getCORREO(),
                        'firebaseUid' => $volunteer->getFirebaseUid() // Might be null
                    ]);
                }
                // Handle organization if implemented in Credenciales later
            }
        }
        else {
             return new JsonResponse(['error' => 'Token or Email/Password required'], 400);
        }

        return new JsonResponse(['error' => 'User not found or invalid credentials'], 404);
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
