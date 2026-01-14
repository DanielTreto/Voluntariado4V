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
        $token = $data['token'] ?? '';

        if (!$token) {
            return new JsonResponse(['error' => 'Firebase token is required'], 400);
        }

        // TODO: Verify Firebase token using Admin SDK or similar
        // For now, we simulate we got a UID from the token
        // In a real implementation: $verifiedIdToken = $auth->verifyIdToken($token); $uid = $verifiedIdToken->claims()->get('sub');
        $uid = $token; // SIMULATION: We assume the token IS the uid for testing purposes during preparation phase

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
