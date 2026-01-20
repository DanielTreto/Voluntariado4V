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
    #[ORM\Column(name: 'CODVOL', type: 'integer')]
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

    #[ORM\Column(length: 9, columnDefinition: 'CHAR(9)')]
    #[Assert\NotBlank]
    #[Assert\Regex(pattern: '/^[6-9][0-9]{8}$/', message: 'Invalid phone number')]
    private ?string $TELEFONO = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank]
    private ?\DateTimeInterface $FECHA_NACIMIENTO = null;

    #[ORM\Column(length: 500, nullable: true)]
    #[Assert\Length(max: 500)]
    private ?string $DESCRIPCION = null;

    #[ORM\ManyToOne(targetEntity: Ciclo::class)]
    #[ORM\JoinColumn(name: 'CODCICLO', referencedColumnName: 'CODCICLO', nullable: false)]
    #[Assert\NotNull(message: 'El ciclo formativo es obligatorio.')]
    private ?Ciclo $ciclo = null;

    #[ORM\Column(length: 9, unique: true, columnDefinition: 'CHAR(9)')]
    #[Assert\NotBlank]
    #[Assert\Length(min: 9, max: 9)]
    private ?string $DNI = null;

// ... (skipping unchanged parts)

    public function getCiclo(): ?Ciclo
    {
        return $this->ciclo;
    }

    public function setCiclo(?Ciclo $ciclo): static
    {
        $this->ciclo = $ciclo;
        return $this;
    }

    public function getCODCICLO(): ?string
    {
        return $this->ciclo?->getCODCICLO();
    }

    public function setCODCICLO(string $CODCICLO): static
    {
        // Ideally set via setCiclo
        return $this;
    }

    #[ORM\Column(length: 128, unique: true, nullable: true)]
    private ?string $firebaseUid = null;

    #[ORM\Column(length: 10)]
    #[Assert\Choice(choices: ['ACTIVO', 'SUSPENDIDO', 'PENDIENTE'])]
    private ?string $ESTADO = 'PENDIENTE';

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $AVATAR = null;

    #[ORM\OneToMany(mappedBy: 'voluntario', targetEntity: Disponibilidad::class)]
    private $disponibilidades;

    #[ORM\ManyToMany(targetEntity: Actividad::class, mappedBy: 'voluntarios')]
    private $actividades;

    #[ORM\ManyToMany(targetEntity: TipoActividad::class)]
    #[ORM\JoinTable(name: 'VOL_PREFIERE_TACT')]
    #[ORM\JoinColumn(name: 'CODVOL', referencedColumnName: 'CODVOL')]
    #[ORM\InverseJoinColumn(name: 'CODTIPO', referencedColumnName: 'CODTIPO')]
    private $preferencias;

    public function __construct()
    {
        $this->actividades = new \Doctrine\Common\Collections\ArrayCollection();
        $this->disponibilidades = new \Doctrine\Common\Collections\ArrayCollection();
        $this->preferencias = new \Doctrine\Common\Collections\ArrayCollection();
    }

    /**
     * @return \Doctrine\Common\Collections\Collection<int, Disponibilidad>
     */
    public function getDisponibilidades(): \Doctrine\Common\Collections\Collection
    {
        return $this->disponibilidades;
    }

    public function addDisponibilidad(Disponibilidad $disponibilidad): static
    {
        if (!$this->disponibilidades->contains($disponibilidad)) {
            $this->disponibilidades->add($disponibilidad);
            $disponibilidad->setVoluntario($this);
        }
        return $this;
    }

    public function removeDisponibilidad(Disponibilidad $disponibilidad): static
    {
        if ($this->disponibilidades->removeElement($disponibilidad)) {
            // set the owning side to null (unless already changed)
            if ($disponibilidad->getVoluntario() === $this) {
                $disponibilidad->setVoluntario(null);
            }
        }
        return $this;
    }

    public function getCODVOL(): ?int
    {
        return $this->CODVOL;
    }

    public function setCODVOL(int $CODVOL): static
    {
        $this->CODVOL = $CODVOL;
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
        // Sanitize: remove all non-numeric characters
        $this->TELEFONO = preg_replace('/\D/', '', $TELEFONO);
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



    public function getDNI(): ?string
    {
        return $this->DNI;
    }

    public function setDNI(string $DNI): static
    {
        $this->DNI = $DNI;
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
     * @return \Doctrine\Common\Collections\Collection<int, Actividad>
     */
    public function getActividades(): \Doctrine\Common\Collections\Collection
    {
        return $this->actividades;
    }

    public function addActividad(Actividad $actividad): static
    {
        if (!$this->actividades->contains($actividad)) {
            $this->actividades->add($actividad);
            $actividad->addVoluntario($this);
        }
        return $this;
    }

    public function removeActividad(Actividad $actividad): static
    {
        if ($this->actividades->removeElement($actividad)) {
            $actividad->removeVoluntario($this);
        }
        return $this;
    }

    public function getAVATAR(): ?string
    {
        return $this->AVATAR;
    }

    public function setAVATAR(?string $AVATAR): static
    {
        $this->AVATAR = $AVATAR;
        return $this;
    }

    /**
     * @return \Doctrine\Common\Collections\Collection<int, TipoActividad>
     */
    public function getPreferencias(): \Doctrine\Common\Collections\Collection
    {
        return $this->preferencias;
    }

    public function addPreferencia(TipoActividad $preferencia): static
    {
        if (!$this->preferencias->contains($preferencia)) {
            $this->preferencias->add($preferencia);
        }
        return $this;
    }

    public function removePreferencia(TipoActividad $preferencia): static
    {
        $this->preferencias->removeElement($preferencia);
        return $this;
    }
}
