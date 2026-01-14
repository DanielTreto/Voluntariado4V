<?php

namespace App\Entity;

use App\Repository\SolicitudRepository;
use Doctrine\ORM\Mapping as ORM;
use App\Entity\Volunteer;
use App\Entity\Actividad;

#[ORM\Entity(repositoryClass: SolicitudRepository::class)]
#[ORM\Table(name: 'SOLICITUD')]
class Solicitud
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne(targetEntity: Volunteer::class)]
    #[ORM\JoinColumn(name: 'CODVOL', referencedColumnName: 'CODVOL', nullable: false)]
    private ?Volunteer $volunteer = null;

    #[ORM\ManyToOne(targetEntity: Actividad::class)]
    #[ORM\JoinColumn(name: 'CODACT', referencedColumnName: 'CODACT', nullable: false)]
    private ?Actividad $actividad = null;

    #[ORM\Column(length: 20)]
    private ?string $status = 'PENDIENTE';

    #[ORM\Column(type: 'datetime')]
    private ?\DateTimeInterface $fechaSolicitud = null;

    #[ORM\Column(type: 'text', nullable: true)]
    private ?string $mensaje = null;

    public function getVolunteer(): ?Volunteer
    {
        return $this->volunteer;
    }

    public function setVolunteer(?Volunteer $volunteer): static
    {
        $this->volunteer = $volunteer;
        return $this;
    }

    public function getActividad(): ?Actividad
    {
        return $this->actividad;
    }

    public function setActividad(?Actividad $actividad): static
    {
        $this->actividad = $actividad;
        return $this;
    }

    public function getStatus(): ?string
    {
        return $this->status;
    }

    public function setStatus(string $status): static
    {
        $this->status = $status;
        return $this;
    }

    public function getFechaSolicitud(): ?\DateTimeInterface
    {
        return $this->fechaSolicitud;
    }

    public function setFechaSolicitud(\DateTimeInterface $fechaSolicitud): static
    {
        $this->fechaSolicitud = $fechaSolicitud;
        return $this;
    }

    public function getMensaje(): ?string
    {
        return $this->mensaje;
    }

    public function setMensaje(?string $mensaje): static
    {
        $this->mensaje = $mensaje;
        return $this;
    }
}
