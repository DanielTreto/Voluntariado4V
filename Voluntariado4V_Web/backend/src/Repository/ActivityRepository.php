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
        $qb = $this->createQueryBuilder('a');
        $qb->select('SUBSTRING(a.FECHA_INICIO, 6, 2) as month, COUNT(a.CODACT) as count')
           ->where('SUBSTRING(a.FECHA_INICIO, 1, 4) = :year')
           ->andWhere("a.ESTADO IN ('ACTIVE', 'ACTIVO', 'FINALIZADA')")
           ->setParameter('year', $year)
           ->groupBy('month');

        try {
            $results = $qb->getQuery()->getResult();
        } catch (\Exception $e) {
            // Fallback for databases where SUBSTRING usually works but might differ (e.g. SQLite)
            // returning empty to let controller/service handle via PHP if needed, but for now assuming MySQL
            return [];
        }

        $monthlyData = array_fill(0, 12, 0);
        foreach ($results as $row) {
            $monthIndex = (int)$row['month'] - 1;
            if ($monthIndex >= 0 && $monthIndex < 12) {
                $monthlyData[$monthIndex] = (int)$row['count'];
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
        $qb = $this->createQueryBuilder('a');

        if ($organizationId) {
            $qb->andWhere('a.organizacion = :orgId')
               ->setParameter('orgId', $organizationId);
        }

        return $qb->getQuery()->getResult();
    }
}

