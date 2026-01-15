<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260115212757 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE CREDENCIALES ADD CODORG NVARCHAR(20)');
        $this->addSql('ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C0ED28F88B FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG)');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_6B3529C0ED28F88B ON CREDENCIALES (CODORG) WHERE CODORG IS NOT NULL');
        $this->addSql('ALTER TABLE ORGANIZACION ALTER COLUMN telefono CHAR(9)');
        $this->addSql('ALTER TABLE VOLUNTARIO ALTER COLUMN telefono CHAR(9)');
        $this->addSql('ALTER TABLE VOLUNTARIO ALTER COLUMN dni CHAR(9)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SCHEMA db_accessadmin');
        $this->addSql('CREATE SCHEMA db_backupoperator');
        $this->addSql('CREATE SCHEMA db_datareader');
        $this->addSql('CREATE SCHEMA db_datawriter');
        $this->addSql('CREATE SCHEMA db_ddladmin');
        $this->addSql('CREATE SCHEMA db_denydatareader');
        $this->addSql('CREATE SCHEMA db_denydatawriter');
        $this->addSql('CREATE SCHEMA db_owner');
        $this->addSql('CREATE SCHEMA db_securityadmin');
        $this->addSql('ALTER TABLE CREDENCIALES DROP CONSTRAINT FK_6B3529C0ED28F88B');
        $this->addSql('DROP INDEX UNIQ_6B3529C0ED28F88B ON CREDENCIALES');
        $this->addSql('ALTER TABLE CREDENCIALES DROP COLUMN CODORG');
        $this->addSql('ALTER TABLE ORGANIZACION ALTER COLUMN TELEFONO NCHAR(9)');
        $this->addSql('ALTER TABLE VOLUNTARIO ALTER COLUMN TELEFONO NCHAR(9)');
        $this->addSql('ALTER TABLE VOLUNTARIO ALTER COLUMN DNI NCHAR(9)');
    }
}
