<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260113130340 extends AbstractMigration
{
    public function getDescription(): string
    {
        return 'Forces creation of missing FK constraints for ODS and TipoActividad, and drops legacy table';
    }

    public function up(Schema $schema): void
    {
        // 1. ACT_PRACTICA_ODS Relations (Fix missing ODS relation)
        $this->addSql("
            IF OBJECT_ID('FK_ACT_PRAC_ODS', 'F') IS NULL
            BEGIN
                ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_ACT_PRAC_ODS FOREIGN KEY (NUMODS) REFERENCES ODS (NUMODS);
            END
        ");
        $this->addSql("
            IF OBJECT_ID('FK_ACT_PRAC_ACT', 'F') IS NULL
            BEGIN
                ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_ACT_PRAC_ACT FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
            END
        ");

        // 2. ACT_ASOCIADO_TACT Relations (Fix missing Tipo relations)
        $this->addSql("
            IF OBJECT_ID('FK_ACT_ASOC_TIPO', 'F') IS NULL
            BEGIN
                ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_ACT_ASOC_TIPO FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO);
            END
        ");
        $this->addSql("
            IF OBJECT_ID('FK_ACT_ASOC_ACT', 'F') IS NULL
            BEGIN
                ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_ACT_ASOC_ACT FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
            END
        ");
        
        // 3. Drop Legacy Duplicate Table
        $this->addSql("
            IF OBJECT_ID('VOLUNTARIO_ACTIVIDAD', 'U') IS NOT NULL
            BEGIN
                DROP TABLE VOLUNTARIO_ACTIVIDAD;
            END
        ");
    }

    public function down(Schema $schema): void
    {
        $this->addSql("ALTER TABLE ACT_PRACTICA_ODS DROP CONSTRAINT IF EXISTS FK_ACT_PRAC_ODS");
        $this->addSql("ALTER TABLE ACT_PRACTICA_ODS DROP CONSTRAINT IF EXISTS FK_ACT_PRAC_ACT");
        $this->addSql("ALTER TABLE ACT_ASOCIADO_TACT DROP CONSTRAINT IF EXISTS FK_ACT_ASOC_TIPO");
        $this->addSql("ALTER TABLE ACT_ASOCIADO_TACT DROP CONSTRAINT IF EXISTS FK_ACT_ASOC_ACT");
        // Cannot restore dropped table without schema definition
    }
}
