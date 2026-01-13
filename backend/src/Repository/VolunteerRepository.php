<?php

namespace App\Repository;

use App\Entity\Volunteer;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Volunteer>
 */
class VolunteerRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Volunteer::class);
    }

    public function save(Volunteer $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findNextId(): string
    {
        // Get max ID
        $qb = $this->createQueryBuilder('v');
        $qb->select('v.CODVOL')
           ->orderBy('v.CODVOL', 'DESC')
           ->setMaxResults(1);
        
        $result = $qb->getQuery()->getOneOrNullResult();
        
        if (!$result) {
            return 'vol001';
        }

        $maxId = $result['CODVOL'];
        // extract number
        $num = (int) substr($maxId, 3);
        $nextNum = $num + 1;
        
        // Pad with zeros (3 digits)
        return 'vol' . str_pad((string)$nextNum, 3, '0', STR_PAD_LEFT);
    }
}
