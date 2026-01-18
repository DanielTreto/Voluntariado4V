<?php

namespace App\Entity;

use App\Repository\DisponibilidadRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: DisponibilidadRepository::class)]
#[ORM\Table(name: 'DISPONIBILIDAD')]
class Disponibilidad
{
    #[ORM\Id]
    #[ORM\ManyToOne(targetEntity: Volunteer::class, inversedBy: 'disponibilidades')]
    #[ORM\JoinColumn(name: 'CODVOL', referencedColumnName: 'CODVOL', nullable: false)]
    private ?Volunteer $voluntario = null;

    #[ORM\Id]
    #[ORM\Column(length: 10)]
    #[Assert\NotBlank]
    private ?string $DIA = null;

    #[ORM\Id]
    #[ORM\Column(length: 10)]
    #[Assert\NotBlank]
    private ?string $HORA = null;

    public function getVoluntario(): ?Volunteer
    {
        return $this->voluntario;
    }

    public function setVoluntario(?Volunteer $voluntario): static
    {
        $this->voluntario = $voluntario;
        return $this;
    }

    public function getDIA(): ?string
    {
        return $this->DIA;
    }

    public function setDIA(string $DIA): static
    {
        $this->DIA = $DIA;
        return $this;
    }

    public function getHORA(): ?string
    {
        return $this->HORA;
    }

    public function setHORA(string $HORA): static
    {
        $this->HORA = $HORA;
        return $this;
    }
}
