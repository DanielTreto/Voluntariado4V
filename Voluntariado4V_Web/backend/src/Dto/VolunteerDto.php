<?php

namespace App\Dto;

use App\Entity\Volunteer;

class VolunteerDto
{
    public string $id;
    public string $name;
    public ?string $avatar = null;
    public string $email;
    public ?string $status = null;
    public ?string $phone = null;

    public static function fromEntity(Volunteer $volunteer): self
    {
        $dto = new self();
        $dto->id = $volunteer->getCODVOL();
        $dto->name = trim($volunteer->getNOMBRE() . ' ' . $volunteer->getAPELLIDO1() . ' ' . ($volunteer->getAPELLIDO2() ?? ''));
        $dto->avatar = $volunteer->getAVATAR();
        $dto->email = $volunteer->getCORREO();
        $dto->status = $volunteer->getESTADO();
        $dto->phone = $volunteer->getTELEFONO();

        return $dto;
    }
}
