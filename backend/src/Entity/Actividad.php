<?php

namespace App\Entity;

use App\Repository\ActivityRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ActivityRepository::class)]
#[ORM\Table(name: 'ACTIVIDAD')]
class Actividad
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(name: 'CODACT')]
    private ?int $CODACT = null;

    #[ORM\Column(length: 70)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 70)]
    private ?string $NOMBRE = null;

    #[ORM\Column(type: 'string', length: 20)]
    #[Assert\NotBlank]
    private ?string $DURACION_SESION = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank]
    private ?\DateTimeInterface $FECHA_INICIO = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank]
    private ?\DateTimeInterface $FECHA_FIN = null;

    #[ORM\Column]
    #[Assert\NotBlank]
    #[Assert\Positive]
    private ?int $N_MAX_VOLUNTARIOS = null;

    // ...

    public function getN_MAX_VOLUNTARIOS(): ?int
    {
        return $this->N_MAX_VOLUNTARIOS;
    }

    public function setN_MAX_VOLUNTARIOS(int $N_MAX_VOLUNTARIOS): static
    {
        $this->N_MAX_VOLUNTARIOS = $N_MAX_VOLUNTARIOS;
        return $this;
    }

    public function getOrganizacion(): ?Organizacion
    {
        return $this->organizacion;
    }

    public function setOrganizacion(?Organizacion $organizacion): static
    {
        $this->organizacion = $organizacion;
        return $this;
    }

    public function getCODORG(): ?string
    {
        return $this->organizacion?->getCODORG();
    }

    public function setCODORG(string $codOrg): static
    {
        // This is tricky without fetching entity. ideally use setOrganizacion.
        // For now, we leave it as is, or better, we might need repository to finding it.
        // Assuming the controller handles setOrganizacion.
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

    public function getESTADO(): ?string
    {
        return $this->ESTADO;
    }

    public function setESTADO(string $ESTADO): static
    {
        $this->ESTADO = $ESTADO;
        return $this;
    }

    /**
     * @return \Doctrine\Common\Collections\Collection<int, Volunteer>
     */
    public function getVoluntarios(): \Doctrine\Common\Collections\Collection
    {
        return $this->voluntarios;
    }

    public function addVoluntario(Volunteer $volunteer): static
    {
        if (!$this->voluntarios->contains($volunteer)) {
            $this->voluntarios->add($volunteer);
        }
        return $this;
    }

    public function removeVoluntario(Volunteer $volunteer): static
    {
        $this->voluntarios->removeElement($volunteer);
        return $this;
    }
}
