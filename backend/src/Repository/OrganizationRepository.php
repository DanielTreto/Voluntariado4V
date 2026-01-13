<?php

namespace App\Repository;

use App\Entity\Organizacion;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Organizacion>
 */
class OrganizationRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Organizacion::class);
    }

    public function save(Organizacion $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findNextId(): string
    {
        $qb = $this->createQueryBuilder('o');
        $qb->select('o.CODORG')
           ->orderBy('o.CODORG', 'DESC')
           ->setMaxResults(1);
        
        $result = $qb->getQuery()->getOneOrNullResult();
        
        if (!$result) {
            return 'org001';
        }

        $maxId = $result['CODORG'];
        $num = (int) substr($maxId, 3);
        $nextNum = $num + 1;
        
        return 'org' . str_pad((string)$nextNum, 3, '0', STR_PAD_LEFT);
    }
}
