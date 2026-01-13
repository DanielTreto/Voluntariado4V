<?php

namespace App\Repository;

use App\Entity\Administrator;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Administrator>
 *
 * @method Administrator|null find($id, $lockMode = null, $lockVersion = null)
 * @method Administrator|null findOneBy(array $criteria, array $orderBy = null)
 * @method Administrator[]    findAll()
 * @method Administrator[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class AdministratorRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Administrator::class);
    }

    public function add(Administrator $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findNextId(): string
    {
        $qb = $this->createQueryBuilder('a');
        $qb->select('a.id')
           ->orderBy('a.id', 'DESC')
           ->setMaxResults(1);
        
        $result = $qb->getQuery()->getOneOrNullResult();
        
        if (!$result) {
            return 'adm001';
        }

        $maxId = $result['id'];
        $num = (int) substr($maxId, 3);
        $nextNum = $num + 1;
        
        return 'adm' . str_pad((string)$nextNum, 3, '0', STR_PAD_LEFT);
    }

    public function remove(Administrator $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}
