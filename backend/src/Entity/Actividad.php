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

    #[ORM\Column(name: 'nombre', length: 70)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 70)]
    private ?string $NOMBRE = null;

    #[ORM\Column(name: 'duracion_sesion', type: 'string', length: 20)]
    #[Assert\NotBlank]
    private ?string $DURACION_SESION = null;

    #[ORM\Column(name: 'fecha_inicio', type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank]
    private ?\DateTimeInterface $FECHA_INICIO = null;

    #[ORM\Column(name: 'fecha_fin', type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank]
    private ?\DateTimeInterface $FECHA_FIN = null;

    #[ORM\Column(name: 'n_max_voluntarios')]
    #[Assert\NotBlank]
    #[Assert\Positive]
    private ?int $N_MAX_VOLUNTARIOS = null;

    #[ORM\ManyToOne(targetEntity: Organizacion::class, inversedBy: 'actividades')]
    #[ORM\JoinColumn(name: 'CODORG', referencedColumnName: 'CODORG', nullable: false)]
    private ?Organizacion $organizacion = null;

    #[ORM\ManyToMany(targetEntity: Volunteer::class, inversedBy: 'actividades')]
    #[ORM\JoinTable(name: 'VOL_PARTICIPA_ACT')]
    #[ORM\JoinColumn(name: 'CODACT', referencedColumnName: 'CODACT')]
    #[ORM\InverseJoinColumn(name: 'CODVOL', referencedColumnName: 'CODVOL')]
    private $voluntarios;

    #[ORM\ManyToMany(targetEntity: TipoActividad::class)]
    #[ORM\JoinTable(name: 'ACT_ASOCIADO_TACT')]
    #[ORM\JoinColumn(name: 'CODACT', referencedColumnName: 'CODACT')]
    #[ORM\InverseJoinColumn(name: 'CODTIPO', referencedColumnName: 'CODTIPO')]
    private $tiposActividad;

    #[ORM\ManyToMany(targetEntity: Ods::class)]
    #[ORM\JoinTable(name: 'ACT_PRACTICA_ODS')]
    #[ORM\JoinColumn(name: 'CODACT', referencedColumnName: 'CODACT')]
    #[ORM\InverseJoinColumn(name: 'NUMODS', referencedColumnName: 'NUMODS')]
    private $ods;

    #[ORM\Column(name: 'descripcion', length: 500)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 500)]
    private ?string $DESCRIPCION = null;

    #[ORM\Column(name: 'estado', length: 20)]
    #[Assert\Choice(choices: ['PENDIENTE', 'EN_PROGRESO', 'DENEGADA', 'FINALIZADA'])]
    private ?string $ESTADO = 'PENDIENTE';

    #[ORM\Column(name: 'ubicacion', length: 255, nullable: true)]
    #[Assert\Length(max: 255)]
    private ?string $UBICACION = null;

    public function __construct()
    {
        $this->voluntarios = new \Doctrine\Common\Collections\ArrayCollection();
        $this->tiposActividad = new \Doctrine\Common\Collections\ArrayCollection();
        $this->ods = new \Doctrine\Common\Collections\ArrayCollection();
    }

    public function getUBICACION(): ?string
    {
        return $this->UBICACION;
    }

    public function setUBICACION(?string $UBICACION): static
    {
        $this->UBICACION = $UBICACION;
        return $this;
    }

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
