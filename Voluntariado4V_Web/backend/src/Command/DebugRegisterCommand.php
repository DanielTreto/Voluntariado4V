<?php

namespace App\Command;

use App\Entity\Volunteer;
use App\Entity\Credenciales;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use App\Repository\VolunteerRepository;

#[AsCommand(
    name: 'debug:register',
    description: 'Debug volunteer registration flow',
)]
class DebugRegisterCommand extends Command
{
    public function __construct(
        private EntityManagerInterface $em,
        private ValidatorInterface $validator,
        private VolunteerRepository $volRepo
    ) {
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $output->writeln("Starting registration debug...");

        try {
            $volunteer = new Volunteer();
            $volunteer->setNOMBRE('Debug');
            $volunteer->setAPELLIDO1('User');
            $volunteer->setCORREO('debug_' . time() . '@example.com');
            $vPhone = '6' . rand(10000000, 99999999);
            $volunteer->setTELEFONO($vPhone);
            $vDni = rand(10000000, 99999999) . 'Z';
            $volunteer->setDNI($vDni);
            $volunteer->setFECHA_NACIMIENTO(new \DateTime('1990-01-01'));
            $volunteer->setESTADO('PENDIENTE');

            $newId = $this->volRepo->findNextId();
            $output->writeln("Generated ID: $newId");
            $volunteer->setCODVOL($newId);

            $cred = new Credenciales();
            $cred->setVoluntario($volunteer);
            $cred->setUserType('VOLUNTARIO');
            $cred->setCorreo($volunteer->getCORREO());
            $cred->setPassword('password123');

            $this->em->persist($cred);
            
            $errors = $this->validator->validate($volunteer);
            if (count($errors) > 0) {
                $output->writeln("Validation Errors:");
                foreach ($errors as $e) {
                    $output->writeln($e->getPropertyPath() . ": " . $e->getMessage());
                }
            } else {
                $output->writeln("Validation OK");
            }

            $this->em->persist($volunteer);
            
            $output->writeln("Flushing...");
            $this->em->flush();
            $output->writeln("Success! Database inserted volunteer $newId");

        } catch (\Throwable $e) {
            $output->writeln("Exception: " . $e->getMessage());
            $output->writeln("Trace:");
            $output->writeln($e->getTraceAsString());
            return Command::FAILURE;
        }

        return Command::SUCCESS;
    }
}
