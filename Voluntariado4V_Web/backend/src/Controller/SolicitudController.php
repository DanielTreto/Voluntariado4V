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

#[Route('/api/requests')]
class SolicitudController extends AbstractController
{
    #[Route('', name: 'api_requests_list', methods: ['GET'])]
    public function list(Request $request, SolicitudRepository $solicitudRepository): JsonResponse
    {
        $organizationId = $request->query->get('organizationId');
        $status = $request->query->get('status');
        
        $criteria = [];
        if ($status) {
            $criteria['status'] = $status;
        }

        // If organization ID is provided, we need to filter requests by activities belonging to that org
        // Since Solicitud is linked to Actividad, and Actividad is linked to Organizacion.
        // SolicitudRepository default findBy operates on direct properties.
        // We might need a custom query in repository for this, but let's try to build query builder or filter manually for MVP.
        
        $qb = $solicitudRepository->createQueryBuilder('s')
            ->join('s.actividad', 'a')
            ->join('a.organizacion', 'o')
            ->addSelect('a')
            ->addSelect('o');

        if ($organizationId) {
            $qb->andWhere('o.CODORG = :orgId')
               ->setParameter('orgId', $organizationId);
        }

        if ($status) {
            $qb->andWhere('s.status = :status')
               ->setParameter('status', $status);
        }

        $qb->orderBy('s.fechaSolicitud', 'DESC');

        $requests = $qb->getQuery()->getResult();

        $data = [];
        foreach ($requests as $req) {
            $volunteer = $req->getVolunteer();
            $activity = $req->getActividad();

            // Skip if critical relations are missing (should not happen with strict schema, but safety first)
            if (!$volunteer || !$activity) {
                continue;
            }

            $fechaSolicitud = $req->getFechaSolicitud();
            $fechaInicio = $activity->getFECHA_INICIO();

            $data[] = [
                'id' => $req->getId(),
                'status' => $req->getStatus(),
                'message' => $req->getMensaje(),
                'date' => $fechaSolicitud ? $fechaSolicitud->format('Y-m-d H:i:s') : null,
                'volunteer' => [
                    'id' => $volunteer->getCODVOL(),
                    'name' => $volunteer->getNOMBRE(),
                    'fullName' => trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? '')),
                    'email' => $volunteer->getCORREO(),
                    'avatar' => null
                ],
                'activity' => [
                    'id' => $activity->getCODACT(),
                    'title' => $activity->getNOMBRE(),
                    'date' => $fechaInicio ? $fechaInicio->format('Y-m-d') : null
                ]
            ];
        }

        $response = new JsonResponse($data);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
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

        $response = new JsonResponse(['id' => $solicitud->getId(), 'status' => $solicitud->getStatus()]);
        $response->headers->set('Access-Control-Allow-Origin', '*');
        return $response;
    }
}
