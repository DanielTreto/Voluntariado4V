<?php

namespace App\Repository;

use App\Entity\Solicitud;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Solicitud>
 */
class SolicitudRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Solicitud::class);
    }

    /**
     * @return Solicitud[]
     */
    public function findByFilters(?string $organizationId = null, ?string $status = null): array
    {
        $qb = $this->createQueryBuilder('s')
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

        return $qb->orderBy('s.fechaSolicitud', 'DESC')
            ->getQuery()
            ->getResult();
    }

//    /**
//     * @return Solicitud[] Returns an array of Solicitud objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('s')
//            ->andWhere('s.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('s.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Solicitud
//    {
//        return $this->createQueryBuilder('s')
//            ->andWhere('s.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
