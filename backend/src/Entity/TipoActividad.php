<?php

namespace App\Entity;

use App\Repository\TipoActividadRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: TipoActividadRepository::class)]
#[ORM\Table(name: 'TIPO_ACTIVIDAD')]
class TipoActividad
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $CODTIPO = null;

    #[ORM\Column(length: 20)]
    #[Assert\NotBlank]
    private ?string $DESCRIPCION = null;

    public function getCODTIPO(): ?int
    {
        return $this->CODTIPO;
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
