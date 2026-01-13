<?php

namespace App\Entity;

use App\Repository\CredencialesRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;

#[ORM\Entity(repositoryClass: CredencialesRepository::class)]
#[ORM\Table(name: 'CREDENCIALES')]
#[UniqueEntity(fields: ['correo'], message: 'There is already an account with this email')]
#[UniqueEntity(fields: ['correo'], message: 'There is already an account with this email')]
class Credenciales
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\OneToOne(targetEntity: Volunteer::class, inversedBy: 'credenciales', cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: 'CODVOL', referencedColumnName: 'CODVOL', nullable: true)]
    private ?Volunteer $voluntario = null;

    #[ORM\OneToOne(targetEntity: Organizacion::class, inversedBy: 'credenciales', cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: 'CODORG', referencedColumnName: 'CODORG', nullable: true)]
    private ?Organizacion $organizacion = null;

    #[ORM\OneToOne(targetEntity: Administrator::class, inversedBy: 'credenciales', cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: 'admin_id', referencedColumnName: 'id', nullable: true)]
    private ?Administrator $admin = null;

    #[ORM\Column(length: 20)]
    #[Assert\NotBlank]
    #[Assert\Choice(choices: ['VOLUNTARIO', 'ORGANIZACION', 'ADMINISTRADOR'])]
    private ?string $userType = null;

    #[ORM\Column(length: 180, unique: true)]
    #[Assert\NotBlank]
    #[Assert\Email]
    private ?string $correo = null;

    #[ORM\Column]
    #[Assert\NotBlank]
    private ?string $password = null;

    public function getId(): ?int
    {
        return $this->id;
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

    public function getAdmin(): ?Administrator
    {
        return $this->admin;
    }

    public function setAdmin(?Administrator $admin): static
    {
        $this->admin = $admin;
        return $this;
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
}
