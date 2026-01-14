<?php

namespace App\Entity;

use App\Repository\CicloRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: CicloRepository::class)]
#[ORM\Table(name: 'CICLO')]
class Ciclo
{
    #[ORM\Id]
    #[ORM\Column(name: 'CODCICLO', type: 'string', length: 10)]
    private ?string $CODCICLO = null;

    #[ORM\Column(length: 70)]
    #[Assert\NotBlank]
    private ?string $NOMBRE = null;

    #[ORM\Column]
    #[Assert\NotBlank]
    private ?int $CURSO = null;

    public function getCODCICLO(): ?string
    {
        return $this->CODCICLO;
    }

    public function setCODCICLO(string $CODCICLO): static
    {
        $this->CODCICLO = $CODCICLO;
        return $this;
    }

    public function getNOMBRE(): ?string
    {
        return $this->NOMBRE;
    }

    public function setNOMBRE(string $NOMBRE): static
    {
        $this->NOMBRE = $NOMBRE;
        return $this;
    }

    public function getCURSO(): ?int
    {
        return $this->CURSO;
    }

    public function setCURSO(int $CURSO): static
    {
        $this->CURSO = $CURSO;
        return $this;
    }
}
