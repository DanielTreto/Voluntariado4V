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
                $firebaseUid = (method_exists($volunteer, 'getFirebaseUid') ? $volunteer->getFirebaseUid() : null) ?? 'vol-' . $volunteer->getCODVOL();
                $generatedToken = $this->generateToken([
                    'sub' => $firebaseUid, 
                    'user_id' => $firebaseUid,
                    'email' => $volunteer->getCORREO(),
                    'role' => 'volunteer'
                ]);
                
                return new JsonResponse([
                    'success' => true,
                    'role' => 'volunteer',
                    'token' => $generatedToken,
                    'id' => $volunteer->getCODVOL(),
                    'name' => trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? '')),
                    'email' => $volunteer->getCORREO(),
                    'firebaseUid' => $firebaseUid,
                    'avatar' => $volunteer->getAVATAR(),
                    'status' => $volunteer->getESTADO()
                ]);
            }

            if ($org) {
                $firebaseUid = (method_exists($org, 'getFirebaseUid') ? $org->getFirebaseUid() : null) ?? 'org-' . $org->getCODORG();
                $generatedToken = $this->generateToken([
                    'sub' => $firebaseUid,
                    'user_id' => $firebaseUid,
                    'email' => $org->getCORREO(),
                    'role' => 'organization'
                ]);

                return new JsonResponse([
                    'success' => true,
                    'role' => 'organization',
                    'token' => $generatedToken,
                    'id' => $org->getCODORG(),
                    'name' => $org->getNOMBRE(),
                    'email' => $org->getCORREO(),
                    'firebaseUid' => $firebaseUid,
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

                        $firebaseUid = (method_exists($admin, 'getFirebaseUid') ? $admin->getFirebaseUid() : null) ?? 'admin-' . $admin->getId();
                        $generatedToken = $this->generateToken([
                            'sub' => $firebaseUid,
                            'user_id' => $firebaseUid,
                            'email' => $admin->getCorreo(),
                            'role' => 'admin'
                        ]);
                        
                        return new JsonResponse([
                            'success' => true,
                            'role' => 'admin',
                            'token' => $generatedToken,
                            'id' => $admin->getId(),
                            'name' => trim($name),
                            'email' => $admin->getCorreo(),
                            'firebaseUid' => $firebaseUid,
                            'avatar' => method_exists($admin, 'getAVATAR') ? $admin->getAVATAR() : null
                        ]);
                    } else {
                         // Fallback Admin
                         $generatedToken = $this->generateToken([
                            'sub' => 'admin-uid',
                            'user_id' => 'admin-uid',
                            'email' => $cred->getCorreo(),
                            'role' => 'admin'
                        ]);

                         return new JsonResponse([
                            'success' => true,
                            'role' => 'admin',
                            'token' => $generatedToken,
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
                    $firebaseUid = (method_exists($volunteer, 'getFirebaseUid') ? $volunteer->getFirebaseUid() : null) ?? 'vol-' . $volunteer->getCODVOL();
                    $generatedToken = $this->generateToken([
                        'sub' => $firebaseUid,
                        'user_id' => $firebaseUid,
                        'email' => $volunteer->getCORREO(),
                        'role' => 'volunteer'
                    ]);

                    return new JsonResponse([
                        'success' => true,
                        'role' => 'volunteer',
                        'token' => $generatedToken,
                        'id' => $volunteer->getCODVOL(),
                        'name' => trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? '')),
                        'email' => $volunteer->getCORREO(),
                        'firebaseUid' => $firebaseUid,
                        'avatar' => $volunteer->getAVATAR(),
                        'status' => $volunteer->getESTADO()
                    ]);
                }
                
                $org = $cred->getOrganizacion();
                if ($org) {
                    $firebaseUid = (method_exists($org, 'getFirebaseUid') ? $org->getFirebaseUid() : null) ?? 'org-' . $org->getCODORG();
                    $generatedToken = $this->generateToken([
                        'sub' => $firebaseUid,
                        'user_id' => $firebaseUid,
                        'email' => $org->getCORREO(),
                        'role' => 'organization'
                    ]);

                    return new JsonResponse([
                        'success' => true,
                        'role' => 'organization',
                        'token' => $generatedToken,
                        'id' => $org->getCODORG(),
                        'name' => $org->getNOMBRE(),
                        'email' => $org->getCORREO(),
                        'firebaseUid' => $firebaseUid,
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

    private function generateToken(array $payload): string
    {
        // Add basic JWT claims
        $payload['iat'] = time();
        $payload['exp'] = time() + (60 * 60 * 24); // 24 hours
        
        // Simple base64 encoding without real crypto signature for this dev environment
        // The ApiSecuritySubscriber only checks structure and expiry, not signature validity.
        $header = base64_encode(json_encode(['typ' => 'JWT', 'alg' => 'HS256']));
        $payloadEncoded = base64_encode(json_encode($payload));
        $signature = base64_encode('dummy_signature'); // Not verified by current Subscriber
        
        return "$header.$payloadEncoded.$signature";
    }
}
