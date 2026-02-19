<?php

namespace App\Repository;

use App\Entity\Actividad;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Actividad>
 */
class ActivityRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Actividad::class);
    }

    public function getMonthlyStats(string $year): array
    {
        $startDate = new \DateTime("$year-01-01 00:00:00");
        $endDate = new \DateTime("$year-12-31 23:59:59");

        $qb = $this->createQueryBuilder('a');
        $qb->select('a')
           ->where('a.FECHA_INICIO >= :start')
           ->andWhere('a.FECHA_INICIO <= :end')
           // Filter by relevant statuses for "Activity Reports"
           // Assuming we want to show valid activities (Ongoing, Finished, or Active)
           ->andWhere("a.ESTADO IN ('EN_PROGRESO', 'FINALIZADA', 'ACTIVE', 'ACTIVO', 'PENDIENTE')") 
           ->setParameter('start', $startDate)
           ->setParameter('end', $endDate);

        $results = $qb->getQuery()->getResult();
        
        $monthlyData = array_fill(0, 12, 0);
        
        foreach ($results as $act) {
            $date = $act->getFECHA_INICIO();
            if ($date) {
                // Month is 1-indexed (1=Jan, 12=Dec)
                $month = (int)$date->format('n'); 
                if ($month >= 1 && $month <= 12) {
                    $monthlyData[$month - 1]++;
                }
            }
        }

        return $monthlyData;
    }

    public function getStatusDistribution(): array
    {
        $qb = $this->createQueryBuilder('a');
        $qb->select('a.ESTADO as status, COUNT(a.CODACT) as count')
           ->groupBy('a.ESTADO');
        
        $results = $qb->getQuery()->getResult();
        
        $statusDistribution = [];
        foreach($results as $row) {
            $statusDistribution[$row['status']] = $row['count'];
        }

        return $statusDistribution;
    }

    public function save(Actividad $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
    public function findWithFilters(?string $organizationId = null): array
    {
        $qb = $this->createQueryBuilder('a')
            ->leftJoin('a.organizacion', 'o')->addSelect('o')
            ->leftJoin('a.voluntarios', 'v')->addSelect('v')
            ->leftJoin('a.tiposActividad', 't')->addSelect('t')
            ->leftJoin('a.ods', 'od')->addSelect('od');

        if ($organizationId) {
            $qb->andWhere('a.organizacion = :orgId')
               ->setParameter('orgId', $organizationId);
        }

        return $qb->getQuery()->getResult();
    }
}

