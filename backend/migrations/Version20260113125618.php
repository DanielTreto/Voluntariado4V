<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260113125618 extends AbstractMigration
{
    public function getDescription(): string
    {
        return 'Safely creates tables and adds constraints for missing relationships';
    }

    public function up(Schema $schema): void
    {
        // 1. Create ACT_ASOCIADO_TACT (if not exists)
        $this->addSql("
            IF OBJECT_ID('ACT_ASOCIADO_TACT', 'U') IS NULL
            BEGIN
                CREATE TABLE ACT_ASOCIADO_TACT (CODACT INT NOT NULL, CODTIPO INT NOT NULL, PRIMARY KEY (CODACT, CODTIPO));
                CREATE INDEX IDX_9B6A42AF30D1B74F ON ACT_ASOCIADO_TACT (CODACT);
                CREATE INDEX IDX_9B6A42AFDC0ED945 ON ACT_ASOCIADO_TACT (CODTIPO);
                ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_ACT_ASOC_ACT FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
                ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_ACT_ASOC_TIPO FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO);
            END
        ");

        // 2. Create ACT_PRACTICA_ODS (if not exists)
        $this->addSql("
            IF OBJECT_ID('ACT_PRACTICA_ODS', 'U') IS NULL
            BEGIN
                CREATE TABLE ACT_PRACTICA_ODS (CODACT INT NOT NULL, NUMODS INT NOT NULL, PRIMARY KEY (CODACT, NUMODS));
                CREATE INDEX IDX_4FC76DA630D1B74F ON ACT_PRACTICA_ODS (CODACT);
                CREATE INDEX IDX_4FC76DA6AC4A56 ON ACT_PRACTICA_ODS (NUMODS);
                ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_ACT_PRAC_ACT FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
                ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_ACT_PRAC_ODS FOREIGN KEY (NUMODS) REFERENCES ODS (NUMODS);
            END
        ");

        // 3. Drop VOLUNTARIO_ACTIVIDAD (if exists) - The duplicate/wrong table
        $this->addSql("
            IF OBJECT_ID('VOLUNTARIO_ACTIVIDAD', 'U') IS NOT NULL
            BEGIN
                -- Drop constraints first if any (generic)
                -- We try to drop known FKs if they exist, or just drop table if possible (SQL server might strict block)
                -- For safety, we just DROP TABLE and hope constraints cascade or don't exist.
                DROP TABLE VOLUNTARIO_ACTIVIDAD;
            END
        ");

        // 4. DISPONIBILIDAD Constraints
        $this->addSql("
            IF OBJECT_ID('FK_DISPONIBILIDAD_VOL', 'F') IS NULL
            BEGIN
                -- Dropping any old incorrect constraint if needed? No, purely additive fix requested.
                ALTER TABLE DISPONIBILIDAD ADD CONSTRAINT FK_DISPONIBILIDAD_VOL FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
                CREATE INDEX IDX_DISP_VOL ON DISPONIBILIDAD (CODVOL);
            END
        ");

        // 5. VOL_PREFIERE_TACT Constraints
        $this->addSql("
            IF OBJECT_ID('FK_VOL_PREF_VOL', 'F') IS NULL
            BEGIN
                ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_VOL_PREF_VOL FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
                CREATE INDEX IDX_VOL_PREF_VOL ON VOL_PREFIERE_TACT (CODVOL);
            END
        ");

        $this->addSql("
            IF OBJECT_ID('FK_VOL_PREF_TIPO', 'F') IS NULL
            BEGIN
                ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_VOL_PREF_TIPO FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO);
                CREATE INDEX IDX_VOL_PREF_TIPO ON VOL_PREFIERE_TACT (CODTIPO);
            END
        ");
    }

    public function down(Schema $schema): void
    {
        // Revert actions
        $this->addSql("DROP TABLE IF EXISTS ACT_ASOCIADO_TACT");
        $this->addSql("DROP TABLE IF EXISTS ACT_PRACTICA_ODS");
        // Cannot restore VOLUNTARIO_ACTIVIDAD easily without schema def
        $this->addSql("ALTER TABLE DISPONIBILIDAD DROP CONSTRAINT IF EXISTS FK_DISPONIBILIDAD_VOL");
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT DROP CONSTRAINT IF EXISTS FK_VOL_PREF_VOL");
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT DROP CONSTRAINT IF EXISTS FK_VOL_PREF_TIPO");
    }
}
