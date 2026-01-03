<?php

namespace App\Entity;

use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity]
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

    #[ORM\Column]
    private ?int $CODORG = null;

    #[ORM\ManyToMany(targetEntity: Volunteer::class, inversedBy: 'actividades')]
    #[ORM\JoinTable(name: 'VOLUNTARIO_ACTIVIDAD')]
    #[ORM\JoinColumn(name: 'CODACT', referencedColumnName: 'CODACT')]
    #[ORM\InverseJoinColumn(name: 'CODVOL', referencedColumnName: 'CODVOL')]
    private $voluntarios;

    public function __construct()
    {
        $this->voluntarios = new \Doctrine\Common\Collections\ArrayCollection();
    }

    #[ORM\Column(length: 500)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 500)]
    private ?string $DESCRIPCION = null;

    #[ORM\Column(length: 20)]
    #[Assert\Choice(choices: ['PENDIENTE', 'EN_PROGRESO', 'DENEGADA', 'FINALIZADA'])]
    private ?string $ESTADO = 'PENDIENTE';

    public function getCODACT(): ?int
    {
        return $this->CODACT;
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

    public function getDURACION_SESION(): ?string
    {
        return $this->DURACION_SESION;
    }

    public function setDURACION_SESION(string $DURACION_SESION): static
    {
        $this->DURACION_SESION = $DURACION_SESION;
        return $this;
    }

    public function getFECHA_INICIO(): ?\DateTimeInterface
    {
        return $this->FECHA_INICIO;
    }

    public function setFECHA_INICIO(\DateTimeInterface $FECHA_INICIO): static
    {
        $this->FECHA_INICIO = $FECHA_INICIO;
        return $this;
    }

    public function getFECHA_FIN(): ?\DateTimeInterface
    {
        return $this->FECHA_FIN;
    }

    public function setFECHA_FIN(\DateTimeInterface $FECHA_FIN): static
    {
        $this->FECHA_FIN = $FECHA_FIN;
        return $this;
    }

    public function getN_MAX_VOLUNTARIOS(): ?int
    {
        return $this->N_MAX_VOLUNTARIOS;
    }

    public function setN_MAX_VOLUNTARIOS(int $N_MAX_VOLUNTARIOS): static
    {
        $this->N_MAX_VOLUNTARIOS = $N_MAX_VOLUNTARIOS;
        return $this;
    }

    public function getCODORG(): ?int
    {
        return $this->CODORG;
    }

    public function setCODORG(int $CODORG): static
    {
        $this->CODORG = $CODORG;
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
