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
    public function login(Request $request, VolunteerRepository $volRepo, OrganizationRepository $orgRepo, \App\Repository\CredencialesRepository $credRepo, \Doctrine\ORM\EntityManagerInterface $entityManager): JsonResponse
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
            $tokenEmail = $data['email'] ?? ''; // Expect email from frontend when using Google Login

            // 1. Check Volunteer by UID
            $volunteer = $volRepo->findOneBy(['firebaseUid' => $uid]);
            
            // 2. Check Organization by UID
            $org = $orgRepo->findOneBy(['firebaseUid' => $uid]);

            // 3. Email Fallback & Account Linking
            if (!$volunteer && !$org && $tokenEmail) {
                // Check Volunteer by Email
                $volunteerByEmail = $volRepo->findOneBy(['CORREO' => $tokenEmail]);
                if ($volunteerByEmail) {
                    // Update UID to link account
                    $volunteerByEmail->setFirebaseUid($uid);
                    $entityManager->persist($volunteerByEmail);
                    $entityManager->flush();
                    $volunteer = $volunteerByEmail;
                } else {
                    // Check Organization by Email
                    $orgByEmail = $orgRepo->findOneBy(['CORREO' => $tokenEmail]); 
                    if ($orgByEmail) {
                        $orgByEmail->setFirebaseUid($uid);
                        $entityManager->persist($orgByEmail);
                        $entityManager->flush();
                        $org = $orgByEmail;
                    }
                }
            }

            if ($volunteer) {
                // If we just linked, we need to persist. 
                // Since user didn't inject EM, let's do it properly now by modifying signature.
                return new JsonResponse([
                    'success' => true,
                    'role' => 'volunteer',
                    'id' => $volunteer->getCODVOL(),
                    'name' => $volunteer->getNOMBRE(),
                    'email' => $volunteer->getCORREO(),
                    'firebaseUid' => $volunteer->getFirebaseUid(),
                    'avatar' => $volunteer->getAVATAR()
                ]);
            }

            if ($org) {
                 // If we just linked, we need to persist.
                return new JsonResponse([
                    'success' => true,
                    'role' => 'organization',
                    'id' => $org->getCODORG(),
                    'name' => $org->getNOMBRE(),
                    'email' => $org->getCORREO(),
                    'firebaseUid' => $org->getFirebaseUid(),
                    'avatar' => $org->getAVATAR()
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
                        'firebaseUid' => $volunteer->getFirebaseUid(),
                        'avatar' => $volunteer->getAVATAR()
                    ]);
                }
                
                // Handle organization login
                $org = $cred->getOrganizacion();
                if ($org) {
                    return new JsonResponse([
                        'success' => true,
                        'role' => 'organization',
                        'id' => $org->getCODORG(),
                        'name' => $org->getNOMBRE(),
                        'email' => $org->getCORREO(),
                        'firebaseUid' => $org->getFirebaseUid(),
                        'avatar' => $org->getAVATAR()
                    ]);
                }
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
        $response = new JsonResponse(null, 200); // Return 200 instead of 204 just to be safe
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type, WebSocket-Protocol, Authorization'); // Added typical headers
        return $response;
    }
}
