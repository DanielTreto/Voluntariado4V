<?php

namespace App\EventSubscriber;

use Symfony\Component\EventDispatcher\EventSubscriberInterface;
use Symfony\Component\HttpKernel\Event\RequestEvent;
use Symfony\Component\HttpKernel\KernelEvents;
use Symfony\Component\HttpFoundation\JsonResponse;
use App\Repository\VolunteerRepository;
use App\Repository\OrganizationRepository;
use App\Repository\AdministratorRepository;

class ApiSecuritySubscriber implements EventSubscriberInterface
{
    private $volunteerRepository;
    private $organizationRepository;
    private $adminRepository;

    public function __construct(VolunteerRepository $volunteerRepository, OrganizationRepository $organizationRepository, AdministratorRepository $adminRepository)
    {
        $this->volunteerRepository = $volunteerRepository;
        $this->organizationRepository = $organizationRepository;
        $this->adminRepository = $adminRepository;
    }

    public static function getSubscribedEvents(): array
    {
        return [
            KernelEvents::REQUEST => 'onKernelRequest',
        ];
    }

    public function onKernelRequest(RequestEvent $event): void
    {
        $request = $event->getRequest();
        $path = $request->getPathInfo();

        // Only secure /api routes
        if (!str_starts_with($path, '/api')) {
            return;
        }

        // Whitelist public routes
        // Adjust these paths based on actual usage
        $publicRoutes = [
            '/api/login',
            '/api/register',
            '/api/register/organization', // If exists
            '/api/doc', // If swagger is added later
        ];

        if (in_array($path, $publicRoutes)) {
            return;
        }
        
        // Also allow OPTIONS requests for CORS (handled by Nelmio, but good to be safe)
        if ($request->getMethod() === 'OPTIONS') {
            return;
        }

        // Check Authorization Header
        if (!$request->headers->has('Authorization')) {
            $event->setResponse(new JsonResponse(['error' => 'Authentication required'], 401));
            return;
        }

        $authHeader = $request->headers->get('Authorization');
        if (!str_starts_with($authHeader, 'Bearer ')) {
            $event->setResponse(new JsonResponse(['error' => 'Invalid token format'], 401));
            return;
        }

        $token = substr($authHeader, 7);

        // Validate Token (Manual JWT Decode)
        try {
            $tokenParts = explode('.', $token);
            if (count($tokenParts) < 2) {
                throw new \Exception('Invalid token structure');
            }

            $payload = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $tokenParts[1])), true);

            if (!$payload) {
                throw new \Exception('Invalid token payload');
            }

            // Expiry check
            if (isset($payload['exp']) && $payload['exp'] < time()) {
                throw new \Exception('Token expired');
            }

            $uid = $payload['sub'] ?? ($payload['user_id'] ?? null);
            if (!$uid) {
                throw new \Exception('Token missing UID');
            }

            // Optional: User existence check
            // For now, if valid firebase UID format, we accept. 
            // Better security: check if user exists in DB.
            
            $userExists = false;
            if ($this->volunteerRepository->findOneBy(['firebaseUid' => $uid])) $userExists = true;
            elseif ($this->organizationRepository->findOneBy(['firebaseUid' => $uid])) $userExists = true;
            elseif ($this->adminRepository->findOneBy(['firebaseUid' => $uid])) $userExists = true;

            if (!$userExists) {
                // If checking DB, uncomment below:
                // throw new \Exception('User not found');
                
                // For now, allowing if token is valid structure to avoid blocking valid firebase users not yet synced? 
                // No, security requirement implies we should check.
                // But wait, Register endpoint creates the user. Subsequent requests have token.
                // So user SHOULD exist.
                
                // If it's a new user trying to register, they hit /api/register (public).
                // So valid users hitting other endpoints MUST exist.
                 $event->setResponse(new JsonResponse(['error' => 'User not found or invalid token'], 401));
                 return;
            }

            // Attach user info to request if needed (e.g. $request->attributes->set('user', $user))

        } catch (\Exception $e) {
            $event->setResponse(new JsonResponse(['error' => 'Unauthorized: ' . $e->getMessage()], 401));
        }
    }
}
