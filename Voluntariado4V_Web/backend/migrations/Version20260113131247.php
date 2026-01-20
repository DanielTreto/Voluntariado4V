<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260113131247 extends AbstractMigration
{
    public function getDescription(): string
    {
        return 'Refactors User IDs to Strings (vol001) and creates Credentials table, performing data migration.';
    }

    public function up(Schema $schema): void
    {
        // 1. Drop FK Constraints (Explicit or Dynamic if needed, but explicit is safer for known schema)
        $this->addSql("ALTER TABLE ACTIVIDAD DROP CONSTRAINT IF EXISTS FK_ACTIVIDAD_ORGANIZACION");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT DROP CONSTRAINT IF EXISTS FK_VOL_PART_VOL");
        $this->addSql("ALTER TABLE DISPONIBILIDAD DROP CONSTRAINT IF EXISTS FK_DISPONIBILIDAD_VOL");
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT DROP CONSTRAINT IF EXISTS FK_VOL_PREF_VOL");

        // Helper string to drop indices on a column
        $dropIndicesSql = function($table, $col) {
            return "
                DECLARE @sql NVARCHAR(MAX) = N'';
                SELECT @sql += N'DROP INDEX ' + QUOTENAME(i.name) + ' ON ' + QUOTENAME(OBJECT_NAME(i.object_id)) + '; '
                FROM sys.indexes i
                JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                WHERE i.object_id = OBJECT_ID('$table')
                AND ic.column_id = COLUMNPROPERTY(OBJECT_ID('$table'), '$col', 'ColumnId')
                AND i.is_primary_key = 0; -- PKs handled by DROP CONSTRAINT
                EXEC sp_executesql @sql;
            ";
        };

        // --- ORGANIZACION ---
        $this->addSql("
            DECLARE @pkName NVARCHAR(255);
            SELECT @pkName = name FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('ORGANIZACION') AND type = 'PK';
            IF @pkName IS NOT NULL EXEC('ALTER TABLE ORGANIZACION DROP CONSTRAINT ' + @pkName);
        ");
        $this->addSql($dropIndicesSql('ORGANIZACION', 'CODORG'));
        $this->addSql("ALTER TABLE ORGANIZACION ADD CODORG_NEW NVARCHAR(20)");
        $this->addSql("UPDATE ORGANIZACION SET CODORG_NEW = 'org' + RIGHT('000' + CAST(CODORG AS VARCHAR(10)), 3)");
        $this->addSql("ALTER TABLE ORGANIZACION DROP COLUMN CODORG");
        $this->addSql("EXEC sp_rename 'ORGANIZACION.CODORG_NEW', 'CODORG', 'COLUMN'");
        $this->addSql("ALTER TABLE ORGANIZACION ALTER COLUMN CODORG NVARCHAR(20) NOT NULL");
        $this->addSql("ALTER TABLE ORGANIZACION ADD PRIMARY KEY (CODORG)");

        // --- VOLUNTARIO ---
        $this->addSql("
            DECLARE @pkNameV NVARCHAR(255);
            SELECT @pkNameV = name FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('VOLUNTARIO') AND type = 'PK';
            IF @pkNameV IS NOT NULL EXEC('ALTER TABLE VOLUNTARIO DROP CONSTRAINT ' + @pkNameV);
        ");
        $this->addSql($dropIndicesSql('VOLUNTARIO', 'CODVOL'));
        $this->addSql("ALTER TABLE VOLUNTARIO ADD CODVOL_NEW NVARCHAR(20)");
        $this->addSql("UPDATE VOLUNTARIO SET CODVOL_NEW = 'vol' + RIGHT('000' + CAST(CODVOL AS VARCHAR(10)), 3)");
        $this->addSql("ALTER TABLE VOLUNTARIO DROP COLUMN CODVOL");
        $this->addSql("EXEC sp_rename 'VOLUNTARIO.CODVOL_NEW', 'CODVOL', 'COLUMN'");
        $this->addSql("ALTER TABLE VOLUNTARIO ALTER COLUMN CODVOL NVARCHAR(20) NOT NULL");
        $this->addSql("ALTER TABLE VOLUNTARIO ADD PRIMARY KEY (CODVOL)");

        // --- ADMINISTRADOR ---
        $this->addSql("
            DECLARE @pkNameA NVARCHAR(255);
            SELECT @pkNameA = name FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('ADMINISTRADOR') AND type = 'PK';
            IF @pkNameA IS NOT NULL EXEC('ALTER TABLE ADMINISTRADOR DROP CONSTRAINT ' + @pkNameA);
        ");
        $this->addSql($dropIndicesSql('ADMINISTRADOR', 'id'));
        $this->addSql("ALTER TABLE ADMINISTRADOR ADD id_new NVARCHAR(20)");
        $this->addSql("UPDATE ADMINISTRADOR SET id_new = 'adm' + RIGHT('000' + CAST(id AS VARCHAR(10)), 3)");
        $this->addSql("ALTER TABLE ADMINISTRADOR DROP COLUMN id");
        $this->addSql("EXEC sp_rename 'ADMINISTRADOR.id_new', 'id', 'COLUMN'");
        $this->addSql("ALTER TABLE ADMINISTRADOR ALTER COLUMN id NVARCHAR(20) NOT NULL");
        $this->addSql("ALTER TABLE ADMINISTRADOR ADD PRIMARY KEY (id)");

        // --- ACTIVIDAD (CODORG) ---
        $this->addSql($dropIndicesSql('ACTIVIDAD', 'CODORG'));
        $this->addSql("ALTER TABLE ACTIVIDAD ADD CODORG_NEW NVARCHAR(20)");
        $this->addSql("UPDATE ACTIVIDAD SET CODORG_NEW = 'org' + RIGHT('000' + CAST(CODORG AS VARCHAR(10)), 3) WHERE CODORG IS NOT NULL");
        $this->addSql("ALTER TABLE ACTIVIDAD DROP COLUMN CODORG");
        $this->addSql("EXEC sp_rename 'ACTIVIDAD.CODORG_NEW', 'CODORG', 'COLUMN'");
        $this->addSql("ALTER TABLE ACTIVIDAD ALTER COLUMN CODORG NVARCHAR(20) NOT NULL");

        // --- VOL_PARTICIPA_ACT (CODVOL) ---
        // PK Drop
        $this->addSql("
            DECLARE @pkNameVPA NVARCHAR(255);
            SELECT @pkNameVPA = name FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('VOL_PARTICIPA_ACT') AND type = 'PK';
            IF @pkNameVPA IS NOT NULL EXEC('ALTER TABLE VOL_PARTICIPA_ACT DROP CONSTRAINT ' + @pkNameVPA);
        ");
        $this->addSql($dropIndicesSql('VOL_PARTICIPA_ACT', 'CODVOL'));
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT ADD CODVOL_NEW NVARCHAR(20)");
        $this->addSql("UPDATE VOL_PARTICIPA_ACT SET CODVOL_NEW = 'vol' + RIGHT('000' + CAST(CODVOL AS VARCHAR(10)), 3)");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT DROP COLUMN CODVOL");
        $this->addSql("EXEC sp_rename 'VOL_PARTICIPA_ACT.CODVOL_NEW', 'CODVOL', 'COLUMN'");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT ALTER COLUMN CODVOL NVARCHAR(20) NOT NULL");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT ADD PRIMARY KEY (CODACT, CODVOL)");

        // --- DISPONIBILIDAD (CODVOL) ---
        $this->addSql("
            DECLARE @pkNameDisp NVARCHAR(255);
            SELECT @pkNameDisp = name FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('DISPONIBILIDAD') AND type = 'PK';
            IF @pkNameDisp IS NOT NULL EXEC('ALTER TABLE DISPONIBILIDAD DROP CONSTRAINT ' + @pkNameDisp);
        ");
        $this->addSql($dropIndicesSql('DISPONIBILIDAD', 'CODVOL'));
        $this->addSql("ALTER TABLE DISPONIBILIDAD ADD CODVOL_NEW NVARCHAR(20)");
        $this->addSql("UPDATE DISPONIBILIDAD SET CODVOL_NEW = 'vol' + RIGHT('000' + CAST(CODVOL AS VARCHAR(10)), 3)");
        $this->addSql("ALTER TABLE DISPONIBILIDAD DROP COLUMN CODVOL");
        $this->addSql("EXEC sp_rename 'DISPONIBILIDAD.CODVOL_NEW', 'CODVOL', 'COLUMN'");
        $this->addSql("ALTER TABLE DISPONIBILIDAD ALTER COLUMN CODVOL NVARCHAR(20) NOT NULL");
        // Restore PK (assuming DIA, HORA, CODVOL from previous schema knowledge)
        $this->addSql("ALTER TABLE DISPONIBILIDAD ADD PRIMARY KEY (CODVOL, DIA, HORA)");

        // --- VOL_PREFIERE_TACT (CODVOL) ---
        $this->addSql("
            DECLARE @pkNameVPT NVARCHAR(255);
            SELECT @pkNameVPT = name FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('VOL_PREFIERE_TACT') AND type = 'PK';
            IF @pkNameVPT IS NOT NULL EXEC('ALTER TABLE VOL_PREFIERE_TACT DROP CONSTRAINT ' + @pkNameVPT);
        ");
        $this->addSql($dropIndicesSql('VOL_PREFIERE_TACT', 'CODVOL'));
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT ADD CODVOL_NEW NVARCHAR(20)");
        $this->addSql("UPDATE VOL_PREFIERE_TACT SET CODVOL_NEW = 'vol' + RIGHT('000' + CAST(CODVOL AS VARCHAR(10)), 3)");
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT DROP COLUMN CODVOL");
        $this->addSql("EXEC sp_rename 'VOL_PREFIERE_TACT.CODVOL_NEW', 'CODVOL', 'COLUMN'");
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT ALTER COLUMN CODVOL NVARCHAR(20) NOT NULL");
        // Restore PK (CODVOL, CODTIPO)
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT ADD PRIMARY KEY (CODVOL, CODTIPO)");


        // RE-ADD FOREIGN KEYS
        $this->addSql("ALTER TABLE ACTIVIDAD ADD CONSTRAINT FK_ACTIVIDAD_ORGANIZACION FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG)");
        $this->addSql("ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_VOL_PART_VOL FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)");
        $this->addSql("ALTER TABLE DISPONIBILIDAD ADD CONSTRAINT FK_DISPONIBILIDAD_VOL FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)");
        $this->addSql("ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_VOL_PREF_VOL FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)");

        // 3. Create CREDENCIALES
        $this->addSql('CREATE TABLE CREDENCIALES (id INT IDENTITY NOT NULL, user_id NVARCHAR(20) NOT NULL, user_type NVARCHAR(20) NOT NULL, correo NVARCHAR(180) NOT NULL, password NVARCHAR(255) NOT NULL, PRIMARY KEY (id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_6B3529C077040BC9 ON CREDENCIALES (correo) WHERE correo IS NOT NULL');
        $this->addSql('CREATE INDEX IDX_CRED_USERID ON CREDENCIALES (user_id)');

        // 4. Backfill CREDENCIALES
        $this->addSql("
            INSERT INTO CREDENCIALES (user_id, user_type, correo, password)
            SELECT CODVOL, 'VOLUNTARIO', CORREO, ISNULL(PASSWORD, 'temp1234') FROM VOLUNTARIO WHERE CORREO IS NOT NULL
        ");
        $this->addSql("
            INSERT INTO CREDENCIALES (user_id, user_type, correo, password)
            SELECT CODORG, 'ORGANIZACION', CORREO, ISNULL(PASSWORD, 'temp1234') FROM ORGANIZACION WHERE CORREO IS NOT NULL
        ");
        $this->addSql("
            INSERT INTO CREDENCIALES (user_id, user_type, correo, password)
            SELECT id, 'ADMINISTRADOR', correo, ISNULL(password, 'temp1234') FROM ADMINISTRADOR WHERE correo IS NOT NULL
        ");
    }

    public function down(Schema $schema): void
    {
        // Reverting this is very complex (String -> Int Identity), likely lossy or hard.
        // We throw exception for safety.
        $this->throwIrreversibleMigrationException('Converting Custom String IDs back to Auto-Increment Integers is not automatically supported.');
    }
}
