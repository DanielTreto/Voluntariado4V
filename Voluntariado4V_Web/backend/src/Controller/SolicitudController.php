<?php

namespace App\Controller;

use App\Entity\Solicitud;
use App\Entity\Organizacion;
use App\Repository\SolicitudRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Serializer\SerializerInterface;
use App\Dto\RequestDto;
use OpenApi\Attributes as OA;
use Nelmio\ApiDocBundle\Attribute\Model;


#[Route('/api/requests')]
class SolicitudController extends AbstractController
{
    #[Route('', name: 'api_requests_list', methods: ['GET'])]
    #[OA\Response(
        response: 200,
        description: 'Returns the list of volunteer requests',
        content: new OA\JsonContent(
            type: 'array',
            items: new OA\Items(ref: new Model(type: RequestDto::class))
        )
    )]
    #[OA\Tag(name: 'Requests')]
    public function list(Request $request, SolicitudRepository $solicitudRepository): JsonResponse
    {
        $organizationId = $request->query->get('organizationId');
        $status = $request->query->get('status');
        
        $requests = $solicitudRepository->findByFilters($organizationId, $status);
        $data = array_map(fn($req) => RequestDto::fromEntity($req), $requests);

        return new JsonResponse($data);
    }


    #[Route('/{id}/status', name: 'api_requests_update_status', methods: ['PATCH'])]
    public function updateStatus(int $id, Request $request, SolicitudRepository $solicitudRepository, EntityManagerInterface $em): JsonResponse
    {
        $solicitud = $solicitudRepository->find($id);

        if (!$solicitud) {
            return new JsonResponse(['error' => 'Request not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        $newStatus = $data['status'] ?? null;

        if (!in_array($newStatus, ['ACEPTADA', 'DENEGADA', 'PENDIENTE'])) {
            return new JsonResponse(['error' => 'Invalid status. Allowed: ACEPTADA, DENEGADA, PENDIENTE'], 400);
        }

        // Logic for accepting
        if ($newStatus === 'ACEPTADA' && $solicitud->getStatus() !== 'ACEPTADA') {
            $activity = $solicitud->getActividad();
            $volunteer = $solicitud->getVolunteer();

            // Check if activity is full
            if ($activity->getVoluntarios()->count() >= $activity->getN_MAX_VOLUNTARIOS()) {
                return new JsonResponse(['error' => 'Activity is full'], 400); 
            }

            // Add volunteer to activity
            if (!$activity->getVoluntarios()->contains($volunteer)) {
                $activity->addVoluntario($volunteer);
                $em->persist($activity);
            }
        }
        
        // Logic for denying (or reverting acceptance?) 
        // If we move from ACEPTADA to DENEGADA/PENDIENTE, should we remove the volunteer?
        // Let's assume Yes for consistency.
        if ($newStatus !== 'ACEPTADA' && $solicitud->getStatus() === 'ACEPTADA') {
             $activity = $solicitud->getActividad();
             $volunteer = $solicitud->getVolunteer();
             
             if ($activity->getVoluntarios()->contains($volunteer)) {
                 $activity->removeVoluntario($volunteer);
                 $em->persist($activity);
             }
        }

        $solicitud->setStatus($newStatus);
        $em->flush();

        return new JsonResponse(['id' => $solicitud->getId(), 'status' => $solicitud->getStatus()]);
    }
}
