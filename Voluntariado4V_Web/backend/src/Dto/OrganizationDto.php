<?php

namespace App\Dto;

use App\Entity\Organizacion;

class OrganizationDto
{
    public string $id;
    public string $name;
    public ?string $avatar = null;
    public string $email;
    public ?string $status = null;

    public static function fromEntity(Organizacion $org): self
    {
        $dto = new self();
        $dto->id = $org->getCODORG();
        $dto->name = $org->getNOMBRE();
        $dto->avatar = $org->getAVATAR();
        $dto->email = $org->getCORREO();
        $dto->status = $org->getESTADO();

        return $dto;
    }
}
