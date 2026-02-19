<?php

namespace App\Dto;

use App\Entity\Solicitud;

class RequestDto
{
    public int $id;
    public string $status;
    public ?string $message = null;
    public ?string $date = null;
    public array $volunteer = [];
    public array $activity = [];

    public static function fromEntity(Solicitud $solicitud): self
    {
        $dto = new self();
        $dto->id = $solicitud->getId();
        $dto->status = $solicitud->getStatus();
        $dto->message = $solicitud->getMensaje();
        $dto->date = $solicitud->getFechaSolicitud() ? $solicitud->getFechaSolicitud()->format(\DateTimeInterface::ATOM) : null;
        
        $volunteer = $solicitud->getVolunteer();
        if ($volunteer) {
            $dto->volunteer = [
                'id' => $volunteer->getCODVOL(),
                'name' => $volunteer->getNOMBRE(),
                'fullName' => trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? '')),
                'email' => $volunteer->getCORREO(),
                'avatar' => $volunteer->getAVATAR()
            ];
        }

        $activity = $solicitud->getActividad();
        if ($activity) {
            $dto->activity = [
                'id' => $activity->getCODACT(),
                'title' => $activity->getNOMBRE(),
                'date' => $activity->getFECHA_INICIO() ? $activity->getFECHA_INICIO()->format(\DateTimeInterface::ATOM) : null
            ];
        }

        return $dto;
    }
}
