<?php

namespace App\Dto;

use App\Entity\Actividad;
use Symfony\Component\Validator\Constraints as Assert;

class ActivityDto
{
    public ?int $id = null;

    #[Assert\NotBlank]
    public string $title;

    #[Assert\NotBlank]
    public string $description;

    public ?string $location = null;

    public ?string $image = null;

    #[Assert\NotBlank]
    #[Assert\DateTime(format: \DateTimeInterface::ATOM)]
    public string $startDate;

    #[Assert\NotBlank]
    #[Assert\DateTime(format: \DateTimeInterface::ATOM)]
    public string $endDate;

    public ?string $duration = null;

    public ?OrganizationDto $organization = null;

    /** @var VolunteerDto[] */
    public array $volunteers = [];

    public ?string $type = null;

    public string $status;

    /** @var array */
    public array $ods = [];

    public int $maxVolunteers;

    public static function fromEntity(Actividad $actividad): self
    {
        $dto = new self();
        $dto->id = $actividad->getCODACT();
        $dto->title = $actividad->getNOMBRE();
        $dto->description = $actividad->getDESCRIPCION();
        $dto->location = $actividad->getUBICACION();
        $dto->image = $actividad->getIMAGEN();
        $dto->startDate = $actividad->getFECHA_INICIO() ? $actividad->getFECHA_INICIO()->format(\DateTimeInterface::ATOM) : '';
        $dto->endDate = $actividad->getFECHA_FIN() ? $actividad->getFECHA_FIN()->format(\DateTimeInterface::ATOM) : '';
        $dto->duration = $actividad->getDURACION_SESION();
        $dto->status = $actividad->getESTADO();
        $dto->maxVolunteers = $actividad->getN_MAX_VOLUNTARIOS() ?? 0;

        $org = $actividad->getOrganizacion();
        if ($org) {
            $dto->organization = OrganizationDto::fromEntity($org);
        }

        foreach ($actividad->getVoluntarios() as $vol) {
            $dto->volunteers[] = VolunteerDto::fromEntity($vol);
        }

        $tipo = $actividad->getTiposActividad()->first();
        if ($tipo) {
            $dto->type = $tipo->getDESCRIPCION();
        }

        foreach ($actividad->getOds() as $ods) {
            $dto->ods[] = [
                'id' => $ods->getNUMODS(),
                'description' => $ods->getDESCRIPCION()
            ];
        }

        return $dto;
    }
}
