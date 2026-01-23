<?php

namespace App\Entity;

use App\Repository\OdsRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: OdsRepository::class)]
#[ORM\Table(name: 'ODS')]
class Ods
{
    #[ORM\Id]
    #[ORM\Column(name: 'NUMODS')]
    private ?int $NUMODS = null;

    #[ORM\Column(length: 70)]
    #[Assert\NotBlank]
    private ?string $DESCRIPCION = null;

    public function getNUMODS(): ?int
    {
        return $this->NUMODS;
    }

    public function setNUMODS(int $NUMODS): static
    {
        $this->NUMODS = $NUMODS;
        return $this;
    }

    public function getDESCRIPCION(): ?string
    {
        return $this->DESCRIPCION;
    }

    public function setDESCRIPCION(string $DESCRIPCION): static
    {
        $this->DESCRIPCION = $DESCRIPCION;
        return $this;
    }
}
