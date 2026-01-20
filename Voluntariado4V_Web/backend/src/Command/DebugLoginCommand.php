<?php

namespace App\Command;

use App\Entity\Credenciales;
use App\Entity\Administrator;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

#[AsCommand(name: 'app:debug-login')]
class DebugLoginCommand extends Command
{
    private $em;

    public function __construct(EntityManagerInterface $em)
    {
        $this->em = $em;
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $output->writeln("--- Debugging Login ---");
        $email = 'admin2@example.com';
        
        // 1. Check Credenciales
        $credRepo = $this->em->getRepository(Credenciales::class);
        $cred = $credRepo->findOneBy(['correo' => $email]);

        if ($cred) {
            $output->writeln("Credenciales FOUND:");
            $output->writeln("  - ID: " . $cred->getId());
            $output->writeln("  - UserType: " . $cred->getUserType());
            // $output->writeln("  - Password: " . $cred->getPassword());
            $output->writeln("  - Correo: " . $cred->getCorreo());
        } else {
            $output->writeln("Credenciales NOT FOUND for $email");
            
            // Check raw SQL just in case
            $conn = $this->em->getConnection();
            $sql = "SELECT * FROM CREDENCIALES WHERE correo = :email";
            $raw = $conn->executeQuery($sql, ['email' => $email])->fetchAssociative();
            
            if ($raw) {
                $output->writeln("  (But found in Raw SQL! Doctrine mapping might be wrong)");
                foreach ($raw as $key => $value) {
                    $output->writeln("   $key: $value");
                }
            } else {
                $output->writeln("  (NOT found in Raw SQL either)");
            }
        }

        // 2. Check Administrator
        $adminRepo = $this->em->getRepository(Administrator::class);
        $admin = $adminRepo->findOneBy(['correo' => $email]);

        if ($admin) {
            $output->writeln("Administrator FOUND:");
            $output->writeln("  - ID: " . $admin->getId());
            $output->writeln("  - Nombre: " . $admin->getNombre());
        } else {
            $output->writeln("Administrator NOT FOUND for $email");
        }
        
        $output->writeln("--- End Debug ---");

        return Command::SUCCESS;
    }
}
