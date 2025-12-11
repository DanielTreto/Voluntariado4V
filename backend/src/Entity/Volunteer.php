<?php

namespace App\Entity;

use App\Repository\VolunteerRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: VolunteerRepository::class)]
#[ORM\Table(name: 'VOLUNTARIO')]
class Volunteer
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $CODVOL = null;

    #[ORM\Column(length: 30)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 30)]
    private ?string $NOMBRE = null;

    #[ORM\Column(length: 30)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 30)]
    private ?string $APELLIDO1 = null;

    #[ORM\Column(length: 30, nullable: true)]
    #[Assert\Length(max: 30)]
    private ?string $APELLIDO2 = null;

    #[ORM\Column(length: 50, unique: true)]
    #[Assert\NotBlank]
    #[Assert\Email]
    #[Assert\Length(max: 50)]
    private ?string $CORREO = null;

    #[ORM\Column(length: 9)]
    #[Assert\NotBlank]
    #[Assert\Regex(pattern: '/^[6-9][0-9]{8}$/', message: 'Invalid phone number')]
    private ?string $TELEFONO = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank]
    private ?\DateTimeInterface $FECHA_NACIMIENTO = null;

    #[ORM\Column(length: 500, nullable: true)]
    #[Assert\Length(max: 500)]
    private ?string $DESCRIPCION = null;

    #[ORM\Column(length: 10)]
    #[Assert\NotBlank]
    private ?string $CODCICLO = null;

    #[ORM\Column(length: 9, unique: true)]
    #[Assert\NotBlank]
    #[Assert\Length(min: 9, max: 9)]
    private ?string $DNI = null;

    #[ORM\Column(length: 10)]
    #[Assert\Choice(choices: ['ACTIVO', 'SUSPENDIDO', 'PENDIENTE'])]
    private ?string $ESTADO = 'PENDIENTE';

    public function getCODVOL(): ?int
    {
        return $this->CODVOL;
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

    public function getAPELLIDO1(): ?string
    {
        return $this->APELLIDO1;
    }

    public function setAPELLIDO1(string $APELLIDO1): static
    {
        $this->APELLIDO1 = $APELLIDO1;
        return $this;
    }

    public function getAPELLIDO2(): ?string
    {
        return $this->APELLIDO2;
    }

    public function setAPELLIDO2(?string $APELLIDO2): static
    {
        $this->APELLIDO2 = $APELLIDO2;
        return $this;
    }

    public function getCORREO(): ?string
    {
        return $this->CORREO;
    }

    public function setCORREO(string $CORREO): static
    {
        $this->CORREO = $CORREO;
        return $this;
    }

    public function getTELEFONO(): ?string
    {
        return $this->TELEFONO;
    }

    public function setTELEFONO(string $TELEFONO): static
    {
        $this->TELEFONO = $TELEFONO;
        return $this;
    }

    public function getFECHA_NACIMIENTO(): ?\DateTimeInterface
    {
        return $this->FECHA_NACIMIENTO;
    }

    public function setFECHA_NACIMIENTO(\DateTimeInterface $FECHA_NACIMIENTO): static
    {
        $this->FECHA_NACIMIENTO = $FECHA_NACIMIENTO;
        return $this;
    }

    public function getDESCRIPCION(): ?string
    {
        return $this->DESCRIPCION;
    }

    public function setDESCRIPCION(?string $DESCRIPCION): static
    {
        $this->DESCRIPCION = $DESCRIPCION;
        return $this;
    }

    public function getCODCICLO(): ?string
    {
        return $this->CODCICLO;
    }

    public function setCODCICLO(string $CODCICLO): static
    {
        $this->CODCICLO = $CODCICLO;
        return $this;
    }

    public function getDNI(): ?string
    {
        return $this->DNI;
    }

    public function setDNI(string $DNI): static
    {
        $this->DNI = $DNI;
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
}
