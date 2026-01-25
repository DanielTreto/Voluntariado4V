<?php

namespace App\Controller;

use App\Repository\VolunteerRepository;
use App\Repository\OrganizationRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use App\Repository\AdministratorRepository;
use App\Repository\CredencialesRepository;
use Doctrine\ORM\EntityManagerInterface;

#[Route('/api')]
class AuthController extends AbstractController
{
    #[Route('/login', name: 'api_login', methods: ['POST'])]
    public function login(
        Request $request, 
        VolunteerRepository $volRepo, 
        OrganizationRepository $orgRepo, 
        AdministratorRepository $adminRepo,
        CredencialesRepository $credRepo, 
        EntityManagerInterface $entityManager
    ): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $token = $data['token'] ?? '';
        $email = $data['email'] ?? '';
        $password = $data['password'] ?? '';

        // 1. Firebase Token Login
        if ($token) {
            $tokenParts = explode('.', $token);
            $uid = $token; 
            $payload = [];
            
            if (count($tokenParts) >= 2) {
                $payload = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $tokenParts[1])), true);
                if (isset($payload['sub'])) {
                    $uid = $payload['sub'];
                } elseif (isset($payload['user_id'])) {
                    $uid = $payload['user_id'];
                }
            }
            
            $tokenEmail = $data['email'] ?? ($payload['email'] ?? ''); 

            // 1. Check Volunteer by UID
            $volunteer = $volRepo->findOneBy(['firebaseUid' => $uid]);
            
            // 2. Check Organization by UID
            $org = $orgRepo->findOneBy(['firebaseUid' => $uid]);

            // 3. Email Fallback & Account Linking
            if (!$volunteer && !$org && $tokenEmail) {
                $volunteerByEmail = $volRepo->findOneBy(['CORREO' => $tokenEmail]);
                if ($volunteerByEmail) {
                    $volunteerByEmail->setFirebaseUid($uid);
                    $entityManager->persist($volunteerByEmail);
                    $entityManager->flush();
                    $volunteer = $volunteerByEmail;
                } else {
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
                return new JsonResponse([
                    'success' => true,
                    'role' => 'volunteer',
                    'id' => $volunteer->getCODVOL(),
                    'name' => trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? '')),
                    'email' => $volunteer->getCORREO(),
                    'firebaseUid' => $volunteer->getFirebaseUid(),
                    'avatar' => $volunteer->getAVATAR(),
                    'status' => $volunteer->getESTADO()
                ]);
            }

            if ($org) {
                return new JsonResponse([
                    'success' => true,
                    'role' => 'organization',
                    'id' => $org->getCODORG(),
                    'name' => $org->getNOMBRE(),
                    'email' => $org->getCORREO(),
                    'firebaseUid' => $org->getFirebaseUid(),
                    'avatar' => $org->getAVATAR(),
                    'status' => $org->getESTADO()
                ]);
            }
        }
        // 2. SQL Email/Password Login
        elseif ($email && $password) {
            $cred = $credRepo->findOneBy(['correo' => $email]);
            
            if ($cred && $cred->getPassword() === $password) {
                if (in_array(strtoupper($cred->getUserType()), ['ADMIN', 'ADMINISTRADOR'])) {
                    $admin = $adminRepo->findOneBy(['correo' => $email]);
                    if (!$admin) {
                        $admin = $cred->getAdministrator(); 
                    }

                    if ($admin) {
                        $name = method_exists($admin, 'getNombre') ? $admin->getNombre() : 'Admin';
                        if (method_exists($admin, 'getApellidos')) {
                            $name .= ' ' . $admin->getApellidos();
                        }
                        
                        return new JsonResponse([
                            'success' => true,
                            'role' => 'admin',
                            'id' => $admin->getId(),
                            'name' => trim($name),
                            'email' => $admin->getCorreo(),
                            'firebaseUid' => method_exists($admin, 'getFirebaseUid') ? $admin->getFirebaseUid() : 'admin-uid',
                            'avatar' => method_exists($admin, 'getAVATAR') ? $admin->getAVATAR() : null
                        ]);
                    } else {
                         return new JsonResponse([
                            'success' => true,
                            'role' => 'admin',
                            'id' => 'adm001', 
                            'name' => 'Administrador System',
                            'email' => $cred->getCorreo(),
                            'firebaseUid' => 'admin-uid',
                            'avatar' => null
                        ]);
                    }
                }

                $volunteer = $cred->getVoluntario();
                if ($volunteer) {
                    return new JsonResponse([
                        'success' => true,
                        'role' => 'volunteer',
                        'id' => $volunteer->getCODVOL(),
                        'name' => trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? '')),
                        'email' => $volunteer->getCORREO(),
                        'firebaseUid' => $volunteer->getFirebaseUid(),
                        'avatar' => $volunteer->getAVATAR(),
                        'status' => $volunteer->getESTADO()
                    ]);
                }
                
                $org = $cred->getOrganizacion();
                if ($org) {
                    return new JsonResponse([
                        'success' => true,
                        'role' => 'organization',
                        'id' => $org->getCODORG(),
                        'name' => $org->getNOMBRE(),
                        'email' => $org->getCORREO(),
                        'firebaseUid' => $org->getFirebaseUid(),
                        'avatar' => $org->getAVATAR(),
                        'status' => $org->getESTADO()
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
        $response = new JsonResponse(null, 200);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'POST, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type, WebSocket-Protocol, Authorization'); 
        return $response;
    }
}
