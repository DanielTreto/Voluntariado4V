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

    #[ORM\Column(name: 'NUM_HORAS', type: 'integer')]
    #[Assert\NotNull]
    #[Assert\PositiveOrZero]
    private ?int $NUM_HORAS = 0;

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

    public function getNUM_HORAS(): ?int
    {
        return $this->NUM_HORAS;
    }

    public function setNUM_HORAS(int $NUM_HORAS): static
    {
        $this->NUM_HORAS = $NUM_HORAS;
        return $this;
    }
}
