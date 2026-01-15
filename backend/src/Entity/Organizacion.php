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
    #[ORM\Column(name: 'CODORG', type: 'string', length: 20)]
    private ?string $CODORG = null;



    #[ORM\OneToMany(mappedBy: 'organizacion', targetEntity: Actividad::class)]
    private $actividades;

    public function __construct()
    {
        $this->actividades = new \Doctrine\Common\Collections\ArrayCollection();
    }

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

    #[ORM\Column(length: 9, unique: true, columnDefinition: 'CHAR(9)')]
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

    #[ORM\Column(length: 128, unique: true, nullable: true)]
    #[Assert\NotBlank]
    private ?string $firebaseUid = null;

    #[ORM\Column(length: 100, nullable: true)]
    private ?string $PERSONA_CONTACTO = null;

    #[ORM\Column(length: 500, nullable: true)]
    #[Assert\Length(max: 500)]
    private ?string $DESCRIPCION = null;

    #[ORM\Column(length: 20)]
    #[Assert\Choice(choices: ['ACTIVO', 'SUSPENDIDO', 'PENDIENTE'])]
    private ?string $ESTADO = 'PENDIENTE';

    public function getCODORG(): ?string
    {
        return $this->CODORG;
    }

    public function setCODORG(string $CODORG): static
    {
        $this->CODORG = $CODORG;
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
        // Sanitize: remove all non-numeric characters
        $this->TELEFONO = preg_replace('/\D/', '', $TELEFONO);
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

    public function getFirebaseUid(): ?string
    {
        return $this->firebaseUid;
    }

    public function setFirebaseUid(string $firebaseUid): static
    {
        $this->firebaseUid = $firebaseUid;
        return $this;
    }

    public function getPERSONA_CONTACTO(): ?string
    {
        return $this->PERSONA_CONTACTO;
    }

    public function setPERSONA_CONTACTO(?string $PERSONA_CONTACTO): static
    {
        $this->PERSONA_CONTACTO = $PERSONA_CONTACTO;
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
