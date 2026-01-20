<?php

namespace App\Entity;

use App\Repository\CredencialesRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: CredencialesRepository::class)]
#[ORM\Table(name: 'CREDENCIALES')]
class Credenciales
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 20)]
    #[Assert\NotBlank]
    private ?string $userType = null;

    #[ORM\Column(length: 180)]
    #[Assert\NotBlank]
    #[Assert\Email]
    private ?string $correo = null;

    #[ORM\Column]
    #[Assert\NotBlank]
    private ?string $password = null;

    #[ORM\OneToOne(targetEntity: Volunteer::class, cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: "CODVOL", referencedColumnName: "CODVOL", nullable: true)]
    private ?Volunteer $voluntario = null;
    
    #[ORM\OneToOne(targetEntity: Organizacion::class, cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: "CODORG", referencedColumnName: "CODORG", nullable: true)]
    private ?Organizacion $organizacion = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getUserType(): ?string
    {
        return $this->userType;
    }

    public function setUserType(string $userType): static
    {
        $this->userType = $userType;

        return $this;
    }

    public function getCorreo(): ?string
    {
        return $this->correo;
    }

    public function setCorreo(string $correo): static
    {
        $this->correo = $correo;

        return $this;
    }

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(string $password): static
    {
        $this->password = $password;

        return $this;
    }

    public function getVoluntario(): ?Volunteer
    {
        return $this->voluntario;
    }

    public function setVoluntario(?Volunteer $voluntario): static
    {
        $this->voluntario = $voluntario;

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
}
