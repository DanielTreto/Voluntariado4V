<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260113124815 extends AbstractMigration
{
    public function getDescription(): string
    {
        return 'Restores relationships safely using T-SQL checks';
    }

    public function up(Schema $schema): void
    {
        // 1. Actividad -> Organizacion
        $this->addSql("
            IF OBJECT_ID('FK_ACTIVIDAD_ORGANIZACION', 'F') IS NULL
            BEGIN
                ALTER TABLE ACTIVIDAD ADD CONSTRAINT FK_ACTIVIDAD_ORGANIZACION FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG);
            END
        ");

        // 2. Volunteer -> Ciclo
        $this->addSql("
            IF OBJECT_ID('FK_VOLUNTARIO_CICLO', 'F') IS NULL
            BEGIN
                ALTER TABLE VOLUNTARIO ADD CONSTRAINT FK_VOLUNTARIO_CICLO FOREIGN KEY (CODCICLO) REFERENCES CICLO (CODCICLO);
            END
        ");

        // 3. Volunteer <-> Actividad (VOL_PARTICIPA_ACT)
        $this->addSql("
            IF OBJECT_ID('FK_VOL_PART_ACT', 'F') IS NULL
            BEGIN
                ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_VOL_PART_ACT FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
            END
        ");
        
        $this->addSql("
            IF OBJECT_ID('FK_VOL_PART_VOL', 'F') IS NULL
            BEGIN
                ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_VOL_PART_VOL FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
            END
        ");
    }

    public function down(Schema $schema): void
    {
        // Revert (Blind drop, or check existence)
        $this->addSql("ALTER TABLE ACTIVIDAD DROP CONSTRAINT IF EXISTS FK_ACTIVIDAD_ORGANIZACION");
        $this->addSql("ALTER TABLE VOLUNTARIO DROP CONSTRAINT IF EXISTS FK_VOLUNTARIO_CICLO");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT DROP CONSTRAINT IF EXISTS FK_VOL_PART_ACT");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT DROP CONSTRAINT IF EXISTS FK_VOL_PART_VOL");
    }
}
