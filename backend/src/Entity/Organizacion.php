<?php

namespace App\Entity;

use App\Repository\OrganizationRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: OrganizationRepository::class)]
#[ORM\Table(name: 'ORGANIZACION')]
class Organizacion
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $CODORG = null;

    #[ORM\Column(length: 50)]
    #[Assert\NotBlank]
    #[Assert\Length(max: 50)]
    private ?string $NOMBRE = null;

    #[ORM\Column(length: 25)]
    #[Assert\NotBlank]
    #[Assert\Choice(choices: ['ONG', 'FUNDACIÓN', 'ASOCIACIÓN', 'ENTIDAD PÚBLICA', 'OTRA'])]
    private ?string $TIPO_ORG = null;

    #[ORM\Column(length: 50, unique: true)]
    #[Assert\NotBlank]
    #[Assert\Email]
    private ?string $CORREO = null;

    #[ORM\Column(length: 9, unique: true)]
    #[Assert\NotBlank]
    #[Assert\Regex(pattern: '/^[0-9]{9}$/', message: 'Invalid phone number')]
    private ?string $TELEFONO = null;

    #[ORM\Column(length: 20)]
    #[Assert\NotBlank]
    #[Assert\Choice(choices: ['SOCIAL', 'SALUD', 'EDUCATIVO', 'AMBIENTAL', 'CULTURAL', 'DEPORTIVO', 'TECNOLÓGICO', 'OTRO'])]
    private ?string $SECTOR = null;

    #[ORM\Column(length: 20)]
    #[Assert\NotBlank]
    #[Assert\Choice(choices: ['LOCAL', 'REGIONAL', 'NACIONAL', 'INTERNACIONAL'])]
    private ?string $AMBITO = null;

    #[ORM\Column(length: 500, nullable: true)]
    #[Assert\Length(max: 500)]
    private ?string $DESCRIPCION = null;

    public function getCODORG(): ?int
    {
        return $this->CODORG;
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

    public function getTIPO_ORG(): ?string
    {
        return $this->TIPO_ORG;
    }

    public function setTIPO_ORG(string $TIPO_ORG): static
    {
        $this->TIPO_ORG = $TIPO_ORG;
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

    public function getSECTOR(): ?string
    {
        return $this->SECTOR;
    }

    public function setSECTOR(string $SECTOR): static
    {
        $this->SECTOR = $SECTOR;
        return $this;
    }

    public function getAMBITO(): ?string
    {
        return $this->AMBITO;
    }

    public function setAMBITO(string $AMBITO): static
    {
        $this->AMBITO = $AMBITO;
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
}
