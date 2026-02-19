<?php

namespace App\Dto;

use App\Entity\Administrator;

class AdminDto
{
    public string $id;
    public string $name;
    public ?string $surname = null;
    public string $email;
    public ?string $phone = null;
    public ?string $avatar = null;

    public static function fromEntity(Administrator $admin): self
    {
        $dto = new self();
        $dto->id = $admin->getId();
        $dto->name = $admin->getNombre();
        $dto->surname = $admin->getApellidos();
        $dto->email = $admin->getCorreo();
        $dto->phone = $admin->getTelefono();
        $dto->avatar = $admin->getAVATAR();

        return $dto;
    }
}
