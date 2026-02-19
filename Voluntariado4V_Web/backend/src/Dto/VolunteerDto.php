<?php

namespace App\Dto;

use App\Entity\Volunteer;

class VolunteerDto
{
    public string $id;
    public string $name;
    public ?string $surname1 = null;
    public ?string $surname2 = null;
    public ?string $avatar = null;
    public string $email;
    public ?string $phone = null;
    public ?string $dni = null;
    public ?string $dateOfBirth = null;
    public ?string $description = null;
    public ?string $course = null;
    public ?string $status = null;
    public ?string $lastActivity = null;
    /** @var string[] */
    public array $preferences = [];
    /** @var array */
    public array $availability = [];

    public static function fromEntity(Volunteer $volunteer): self
    {
        $dto = new self();
        $dto->id = $volunteer->getCODVOL();
        $dto->name = $volunteer->getNOMBRE();
        $dto->surname1 = $volunteer->getAPELLIDO1();
        $dto->surname2 = $volunteer->getAPELLIDO2();
        $dto->avatar = $volunteer->getAVATAR();
        $dto->email = $volunteer->getCORREO();
        $dto->phone = $volunteer->getTELEFONO();
        $dto->dni = $volunteer->getDNI();
        $dto->dateOfBirth = $volunteer->getFECHA_NACIMIENTO()?->format('Y-m-d');
        $dto->description = $volunteer->getDESCRIPCION();
        $dto->course = $volunteer->getCODCICLO();
        $dto->status = $volunteer->getESTADO();

        foreach ($volunteer->getPreferencias() as $pref) {
            $dto->preferences[] = $pref->getCODTIPO();
        }

        foreach ($volunteer->getDisponibilidades() as $disp) {
            $dto->availability[] = [
                'day' => $disp->getDIA(),
                'hours' => $disp->getNUM_HORAS(),
                'time' => method_exists($disp, 'getHORA') ? $disp->getHORA() : null
            ];
        }

        // Calculate last activity based on joined activities
        $lastDate = null;
        foreach ($volunteer->getActividades() as $act) {
             $date = $act->getFECHA_FIN(); // Using End Date as reference
             if ($date && ($lastDate === null || $date > $lastDate)) {
                 $lastDate = $date;
             }
        }
        $dto->lastActivity = $lastDate ? $lastDate->format('Y-m-d') : null;

        return $dto;
    }
}

