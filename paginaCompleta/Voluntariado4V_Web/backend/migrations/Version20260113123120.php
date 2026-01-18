<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260113123120 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE ADMINISTRADOR (id INT IDENTITY NOT NULL, nombre NVARCHAR(50) NOT NULL, apellidos NVARCHAR(100) NOT NULL, correo NVARCHAR(100) NOT NULL, telefono NVARCHAR(20) NOT NULL, password NVARCHAR(255) NOT NULL, foto NVARCHAR(255), PRIMARY KEY (id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_62B5A7C477040BC9 ON ADMINISTRADOR (correo) WHERE correo IS NOT NULL');
        // Seed default admin
        $this->addSql("INSERT INTO ADMINISTRADOR (nombre, apellidos, correo, telefono, password) VALUES ('Admin', 'Sistema', 'admin@example.com', '600000000', 'admin')");
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('DROP TABLE ADMINISTRADOR');
    }
}
