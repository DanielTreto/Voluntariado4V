<?php

namespace App\Dto;

use App\Entity\Organizacion;

class OrganizationDto
{
    public string $id;
    public string $name;
    public ?string $type = null;
    public ?string $sector = null;
    public ?string $scope = null;
    public ?string $description = null;
    public ?string $address = null;
    public ?string $web = null;
    public ?string $contactPerson = null;
    public ?string $avatar = null;
    public string $email;
    public ?string $phone = null;
    public ?string $status = null;

    public static function fromEntity(Organizacion $org): self
    {
        $dto = new self();
        $dto->id = $org->getCODORG();
        $dto->name = $org->getNOMBRE();
        $dto->type = $org->getTIPO_ORG();
        $dto->sector = $org->getSECTOR();
        $dto->scope = $org->getAMBITO();
        $dto->description = $org->getDESCRIPCION();
        $dto->address = $org->getDIRECCION();
        $dto->web = $org->getWEB();
        $dto->contactPerson = $org->getPERSONA_CONTACTO();
        $dto->avatar = $org->getAVATAR();
        $dto->email = $org->getCORREO();
        $dto->phone = $org->getTELEFONO();
        $dto->status = $org->getESTADO();

        return $dto;
    }
}

