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
use App\Repository\ActivityRepository;
use App\Repository\VolunteerRepository;
use App\Repository\OrganizationRepository;

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

    #[Route('/admin/dashboard/stats', name: 'api_admin_stats', methods: ['GET'])]
    public function stats(Request $request, ActivityRepository $activityRepo, VolunteerRepository $volRepo, OrganizationRepository $orgRepo): JsonResponse
    {
        $totalActivities = $activityRepo->count([]);
        $totalVolunteers = $volRepo->count([]);
        
        $totalOrgs = $orgRepo->count([]);
        
        $inProgressActivities = $activityRepo->count(['ESTADO' => 'EN_PROGRESO']);
        
        // Monthly Stats for Current Year

        $currentYear = $request->query->get('year');
        if (!$currentYear) {
             $currentYear = (new \DateTime())->format('Y');
        }

        $qb = $activityRepo->createQueryBuilder('a');
        $qb->select('SUBSTRING(a.FECHA_INICIO, 6, 2) as month, COUNT(a.CODACT) as count')
           ->where('SUBSTRING(a.FECHA_INICIO, 1, 4) = :year')
           ->setParameter('year', $currentYear)
           ->groupBy('month');
           
        try {
             $results = $qb->getQuery()->getResult();
        } catch (\Exception $e) {
             // Fallback or retry logic if SUBSTRING not supported by DB platform (e.g. SQLite uses different func)
             // Assuming MySQL/MariaDB.
             $results = [];
             // Alternative: Fetch all for year and process in PHP
             $allActs = $activityRepo->findAll();
             foreach($allActs as $act) {
                 if ($act->getFECHA_INICIO()->format('Y') === $currentYear) {
                     $m = $act->getFECHA_INICIO()->format('m');
                     $found = false;
                     foreach($results as &$res) {
                         if ($res['month'] === $m) {
                             $res['count']++;
                             $found = true;
                             break;
                         }
                     }
                     if (!$found) {
                         $results[] = ['month' => $m, 'count' => 1];
                     }
                 }
             }
        }
        
        // Normalize to [0..11] array
        $monthlyData = array_fill(0, 12, 0);
        
        // Use PHP processing results if Query failed or simpler approach preferred (Going with PHP approach to be safe across DBs)
        // Resetting $monthlyData just to be clean
        $monthlyData = array_fill(0, 12, 0);
        $allActs = $activityRepo->findAll();
        foreach($allActs as $act) {
             // Filter by Status: Only Active/Finished (Realized)
             $status = strtoupper($act->getESTADO());
             $validStatuses = ['ACTIVE', 'ACTIVO', 'FINALIZADA'];
             
             if ($act->getFECHA_INICIO() && $act->getFECHA_INICIO()->format('Y') === $currentYear && in_array($status, $validStatuses)) {
                 $monthIndex = (int)$act->getFECHA_INICIO()->format('m') - 1; // 0 for Jan
                 if ($monthIndex >= 0 && $monthIndex < 12) {
                     $monthlyData[$monthIndex]++;
                 }
             }
        }



        // Status Distribution
        $qbStatus = $activityRepo->createQueryBuilder('a');
        $qbStatus->select('a.ESTADO as status, COUNT(a.CODACT) as count')
                 ->groupBy('a.ESTADO');
        $statusResults = $qbStatus->getQuery()->getResult();
        
        $statusDistribution = [];
        foreach($statusResults as $row) {
            $statusDistribution[$row['status']] = $row['count'];
        }

        $data = [
            'totalActivities' => $totalActivities,
            'totalVolunteers' => $totalVolunteers,
            'activitiesInProgress' => $inProgressActivities,
            'totalOrganizations' => $totalOrgs,
            'monthlyActivities' => $monthlyData,
            'statusDistribution' => $statusDistribution
        ];

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }

    #[Route('/admin/dashboard/stats', name: 'api_admin_stats_options', methods: ['OPTIONS'])]
    public function statsOptions(): JsonResponse
    {
        $response = new JsonResponse(null, 204);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        $response->headers->set('Access-Control-Allow-Methods', 'GET, OPTIONS');
        $response->headers->set('Access-Control-Allow-Headers', 'Content-Type');
        return $response;
    }
}
