<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260123153655 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE ACTIVIDAD (CODACT INT IDENTITY NOT NULL, nombre NVARCHAR(70) NOT NULL, duracion_sesion NVARCHAR(20) NOT NULL, fecha_inicio DATE NOT NULL, fecha_fin DATE NOT NULL, n_max_voluntarios INT NOT NULL, descripcion NVARCHAR(500) NOT NULL, estado NVARCHAR(20) NOT NULL, ubicacion NVARCHAR(255), imagen NVARCHAR(255), CODORG NVARCHAR(20) NOT NULL, PRIMARY KEY (CODACT))');
        $this->addSql('CREATE INDEX IDX_C930A3E9ED28F88B ON ACTIVIDAD (CODORG)');
        $this->addSql('CREATE TABLE VOL_PARTICIPA_ACT (CODACT INT NOT NULL, CODVOL NVARCHAR(20) NOT NULL, PRIMARY KEY (CODACT, CODVOL))');
        $this->addSql('CREATE INDEX IDX_706140E930D1B74F ON VOL_PARTICIPA_ACT (CODACT)');
        $this->addSql('CREATE INDEX IDX_706140E99661D5E0 ON VOL_PARTICIPA_ACT (CODVOL)');
        $this->addSql('CREATE TABLE ACT_ASOCIADO_TACT (CODACT INT NOT NULL, CODTIPO INT NOT NULL, PRIMARY KEY (CODACT, CODTIPO))');
        $this->addSql('CREATE INDEX IDX_9B6A42AF30D1B74F ON ACT_ASOCIADO_TACT (CODACT)');
        $this->addSql('CREATE INDEX IDX_9B6A42AFDC0ED945 ON ACT_ASOCIADO_TACT (CODTIPO)');
        $this->addSql('CREATE TABLE ACT_PRACTICA_ODS (CODACT INT NOT NULL, NUMODS INT NOT NULL, PRIMARY KEY (CODACT, NUMODS))');
        $this->addSql('CREATE INDEX IDX_4FC76DA630D1B74F ON ACT_PRACTICA_ODS (CODACT)');
        $this->addSql('CREATE INDEX IDX_4FC76DA6AC4A56 ON ACT_PRACTICA_ODS (NUMODS)');
        $this->addSql('CREATE TABLE ADMINISTRADOR (id NVARCHAR(20) NOT NULL, nombre NVARCHAR(50) NOT NULL, apellidos NVARCHAR(100) NOT NULL, correo NVARCHAR(100) NOT NULL, telefono NVARCHAR(20) NOT NULL, firebase_uid NVARCHAR(128), avatar NVARCHAR(255), PRIMARY KEY (id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_62B5A7C477040BC9 ON ADMINISTRADOR (correo) WHERE correo IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_62B5A7C42FB49151 ON ADMINISTRADOR (firebase_uid) WHERE firebase_uid IS NOT NULL');
        $this->addSql('CREATE TABLE CICLO (CODCICLO NVARCHAR(10) NOT NULL, nombre NVARCHAR(70) NOT NULL, curso INT NOT NULL, PRIMARY KEY (CODCICLO))');
        $this->addSql('CREATE TABLE CREDENCIALES (id INT IDENTITY NOT NULL, user_type NVARCHAR(20) NOT NULL, correo NVARCHAR(180) NOT NULL, password NVARCHAR(255) NOT NULL, CODVOL NVARCHAR(20), CODORG NVARCHAR(20), CODADMIN NVARCHAR(20), PRIMARY KEY (id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_6B3529C09661D5E0 ON CREDENCIALES (CODVOL) WHERE CODVOL IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_6B3529C0ED28F88B ON CREDENCIALES (CODORG) WHERE CODORG IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_6B3529C05DC26F56 ON CREDENCIALES (CODADMIN) WHERE CODADMIN IS NOT NULL');
        $this->addSql('CREATE TABLE DISPONIBILIDAD (dia NVARCHAR(10) NOT NULL, NUM_HORAS INT NOT NULL, hora NVARCHAR(10), CODVOL NVARCHAR(20) NOT NULL, PRIMARY KEY (CODVOL, dia))');
        $this->addSql('CREATE INDEX IDX_A042D1129661D5E0 ON DISPONIBILIDAD (CODVOL)');
        $this->addSql('CREATE TABLE ODS (NUMODS INT NOT NULL, descripcion NVARCHAR(70) NOT NULL, PRIMARY KEY (NUMODS))');
        $this->addSql('CREATE TABLE ORGANIZACION (CODORG NVARCHAR(20) NOT NULL, nombre NVARCHAR(50) NOT NULL, tipo_org NVARCHAR(25) NOT NULL, correo NVARCHAR(50) NOT NULL, telefono CHAR(9), sector NVARCHAR(20) NOT NULL, ambito NVARCHAR(20) NOT NULL, firebase_uid NVARCHAR(128), persona_contacto NVARCHAR(100), descripcion NVARCHAR(500), estado NVARCHAR(20) NOT NULL, avatar NVARCHAR(255), direccion NVARCHAR(255), web NVARCHAR(255), PRIMARY KEY (CODORG))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_9912454A77040BC9 ON ORGANIZACION (correo) WHERE correo IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_9912454AC1E70A7F ON ORGANIZACION (telefono) WHERE telefono IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_9912454A2FB49151 ON ORGANIZACION (firebase_uid) WHERE firebase_uid IS NOT NULL');
        $this->addSql('CREATE TABLE SOLICITUD (id INT IDENTITY NOT NULL, status NVARCHAR(20) NOT NULL, fecha_solicitud DATETIME2(6) NOT NULL, mensaje VARCHAR(MAX), CODVOL NVARCHAR(20) NOT NULL, CODACT INT NOT NULL, PRIMARY KEY (id))');
        $this->addSql('CREATE INDEX IDX_D210622F9661D5E0 ON SOLICITUD (CODVOL)');
        $this->addSql('CREATE INDEX IDX_D210622F30D1B74F ON SOLICITUD (CODACT)');
        $this->addSql('CREATE TABLE TIPO_ACTIVIDAD (CODTIPO INT IDENTITY NOT NULL, descripcion NVARCHAR(20) NOT NULL, PRIMARY KEY (CODTIPO))');
        $this->addSql('CREATE TABLE VOLUNTARIO (CODVOL NVARCHAR(20) NOT NULL, nombre NVARCHAR(30) NOT NULL, apellido1 NVARCHAR(30) NOT NULL, apellido2 NVARCHAR(30), correo NVARCHAR(50) NOT NULL, telefono CHAR(9), fecha_nacimiento DATE NOT NULL, descripcion NVARCHAR(500), dni CHAR(9), firebase_uid NVARCHAR(128), estado NVARCHAR(10) NOT NULL, avatar NVARCHAR(255), CODCICLO NVARCHAR(10) NOT NULL, PRIMARY KEY (CODVOL))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_2AFD2CC177040BC9 ON VOLUNTARIO (correo) WHERE correo IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_2AFD2CC17F8F253B ON VOLUNTARIO (dni) WHERE dni IS NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_2AFD2CC12FB49151 ON VOLUNTARIO (firebase_uid) WHERE firebase_uid IS NOT NULL');
        $this->addSql('CREATE INDEX IDX_2AFD2CC1D5860D32 ON VOLUNTARIO (CODCICLO)');
        $this->addSql('CREATE TABLE VOL_PREFIERE_TACT (CODVOL NVARCHAR(20) NOT NULL, CODTIPO INT NOT NULL, PRIMARY KEY (CODVOL, CODTIPO))');
        $this->addSql('CREATE INDEX IDX_570D03019661D5E0 ON VOL_PREFIERE_TACT (CODVOL)');
        $this->addSql('CREATE INDEX IDX_570D0301DC0ED945 ON VOL_PREFIERE_TACT (CODTIPO)');
        $this->addSql('ALTER TABLE ACTIVIDAD ADD CONSTRAINT FK_C930A3E9ED28F88B FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG)');
        $this->addSql('ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_706140E930D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT)');
        $this->addSql('ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_706140E99661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)');
        $this->addSql('ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_9B6A42AF30D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT)');
        $this->addSql('ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_9B6A42AFDC0ED945 FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO)');
        $this->addSql('ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_4FC76DA630D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT)');
        $this->addSql('ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_4FC76DA6AC4A56 FOREIGN KEY (NUMODS) REFERENCES ODS (NUMODS)');
        $this->addSql('ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C09661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)');
        $this->addSql('ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C0ED28F88B FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG)');
        $this->addSql('ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C05DC26F56 FOREIGN KEY (CODADMIN) REFERENCES ADMINISTRADOR (id)');
        $this->addSql('ALTER TABLE DISPONIBILIDAD ADD CONSTRAINT FK_A042D1129661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)');
        $this->addSql('ALTER TABLE SOLICITUD ADD CONSTRAINT FK_D210622F9661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)');
        $this->addSql('ALTER TABLE SOLICITUD ADD CONSTRAINT FK_D210622F30D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT)');
        $this->addSql('ALTER TABLE VOLUNTARIO ADD CONSTRAINT FK_2AFD2CC1D5860D32 FOREIGN KEY (CODCICLO) REFERENCES CICLO (CODCICLO)');
        $this->addSql('ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_570D03019661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL)');
        $this->addSql('ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_570D0301DC0ED945 FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO)');

        // SEEDING DATA
        // ODS
        $this->addSql("INSERT INTO ODS (numods, descripcion) VALUES (1, 'Fin de la Pobreza'), (2, 'Hambre Cero'), (3, 'Salud y Bienestar'), (4, 'Educación de Calidad'), (5, 'Igualdad de Género'), (6, 'Agua Limpia y Saneamiento'), (7, 'Energía Asequible y No Contaminante'), (8, 'Trabajo Decente y Crecimiento Económico'), (9, 'Industria, Innovación e Infraestructura'), (10, 'Reducción de las Desigualdades'), (11, 'Ciudades y Comunidades Sostenibles'), (12, 'Producción y Consumo Responsables'), (13, 'Acción por el Clima'), (14, 'Vida Submarina'), (15, 'Vida de Ecosistemas Terrestres'), (16, 'Paz, Justicia e Instituciones Sólidas'), (17, 'Alianzas para lograr los Objetivos')");

        // TIPO_ACTIVIDAD
        $this->addSql("SET IDENTITY_INSERT TIPO_ACTIVIDAD ON; INSERT INTO TIPO_ACTIVIDAD (CODTIPO, descripcion) VALUES (1, 'Digital'), (2, 'Salud'), (3, 'Educativo'), (4, 'Ambiental'), (5, 'Deportivo'), (6, 'Social'), (7, 'Cultural'), (8, 'Tecnico'); SET IDENTITY_INSERT TIPO_ACTIVIDAD OFF;");

        // CICLO
        $this->addSql("INSERT INTO CICLO (codciclo, nombre, curso) VALUES ('1SC', 'SERVICIOS COMERCIALES', 1), ('2SC', 'SERVICIOS COMERCIALES', 2), ('1AAG', 'AUXILIAR ADMINISTRATIVO GENERAL', 1), ('2AAG', 'AUXILIAR ADMINISTRATIVO GENERAL', 2), ('1GA', 'GESTIÓN ADMINISTRATIVA', 1), ('2GA', 'GESTIÓN ADMINISTRATIVA', 2), ('1AC', 'ACTIVIDADES COMERCIALES', 1), ('2AC', 'ACTIVIDADES COMERCIALES', 2), ('1SMR', 'SISTEMAS MICROINFORMÁTICOS Y REDES', 1), ('2SMR', 'SISTEMAS MICROINFORMÁTICOS Y REDES', 2), ('1AF', 'ADMINISTRACIÓN Y FINANZAS', 1), ('2AF', 'ADMINISTRACIÓN Y FINANZAS', 2), ('1CI', 'COMERCIO INTERNACIONAL', 1), ('2CI', 'COMERCIO INTERNACIONAL', 2), ('1GV', 'GESTIÓN DE VENTAS', 1), ('2GV', 'GESTIÓN DE VENTAS', 2), ('1TL', 'TRANSPORTE Y LOGÍSTICA', 1), ('2TL', 'TRANSPORTE Y LOGÍSTICA', 2), ('1ASIR', 'ADMINISTRACIÓN DE SISTEMAS INFORMÁTICOS Y REDES', 1), ('2ASIR', 'ADMINISTRACIÓN DE SISTEMAS INFORMÁTICOS Y REDES', 2), ('1DAM', 'DESARROLLO DE APLICACIONES MULTIPLATAFORMA', 1), ('2DAM', 'DESARROLLO DE APLICACIONES MULTIPLATAFORMA', 2)");

        // ADMINISTRADOR
        $this->addSql("INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, firebase_uid, avatar) VALUES ('adm001', 'Admin', 'Principal', 'admin@voluntariado.com', '600123456', 'admin-uid', 'https://avatar.iran.liara.run/public/job/police/male'), ('adm002', 'Super', 'Admin', 'super@voluntariado.com', '600999999', 'super-admin-uid', 'https://avatar.iran.liara.run/public/job/police/male')");

        // CREDENCIALES ADMIN
        $this->addSql("INSERT INTO CREDENCIALES (user_type, correo, password, CODADMIN) VALUES ('ADMINISTRADOR', 'admin@voluntariado.com', 'admin123', 'adm001'), ('ADMINISTRADOR', 'super@voluntariado.com', 'admin123', 'adm002')");

        // ORGANIZACION
        $this->addSql("INSERT INTO ORGANIZACION (codorg, nombre, tipo_org, correo, telefono, sector, ambito, persona_contacto, descripcion, estado, avatar) VALUES 
            ('org001', 'Cruz Roja', 'ONG', 'cruzroja@email.com', '948111111', 'Social', 'NACIONAL', 'Maria Garcia', 'Ayuda humanitaria internacional.', 'ACTIVO', 'https://images.unsplash.com/photo-1584515933487-779824d29609'),
            ('org002', 'Banco de Alimentos', 'ONG', 'banco@email.com', '948222222', 'Social', 'REGIONAL', 'Juan Perez', 'Recogida y distribución de alimentos.', 'ACTIVO', 'https://images.unsplash.com/photo-1593113598332-cd288d649433'),
            ('org003', 'Greenpeace', 'ONG', 'greenpeace@email.com', '948333333', 'Ambiental', 'INTERNACIONAL', 'Laura Sanz', 'Defensa del medio ambiente.', 'PENDIENTE', 'https://images.unsplash.com/photo-1500485035595-cbe6f645feb1'),
            ('org004', 'Asoc. San Juan', 'ASOCIACIÓN', 'sanjuan@email.com', '948444444', 'Salud', 'LOCAL', 'Carlos Ruiz', 'Apoyo a pacientes.', 'SUSPENDIDO', 'https://images.unsplash.com/photo-1538108149393-fbbd81895907'),
            ('org005', 'Fundacion Ilusion', 'FUNDACIÓN', 'ilusion@email.com', '948555555', 'Educativo', 'NACIONAL', 'Elena Muro', 'Educación para todos.', 'ACTIVO', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6'),
            ('org006', 'Médicos Sin Fronteras', 'ONG', 'msf@email.com', '948666666', 'Salud', 'INTERNACIONAL', 'Dr. House', 'Asistencia médica en zonas de conflicto.', 'ACTIVO', 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528'),
            ('org007', 'WWF', 'ONG', 'wwf@email.com', '948777777', 'Ambiental', 'INTERNACIONAL', 'Panda Bear', 'Conservación de la naturaleza.', 'ACTIVO', 'https://images.unsplash.com/photo-1535083252457-6080fe29be45'),
            ('org008', 'Cáritas', 'ONG', 'caritas@email.com', '948888888', 'Social', 'NACIONAL', 'Padre Ángel', 'Ayuda a los desfavorecidos.', 'ACTIVO', 'https://images.unsplash.com/photo-1593113598332-cd288d649433'),
            ('org009', 'Asociación de Vecinos', 'ASOCIACIÓN', 'vecinos@email.com', '948999999', 'Social', 'LOCAL', 'Paco El Vecino', 'Mejora del barrio.', 'PENDIENTE', 'https://images.unsplash.com/photo-1511632765486-a01980e01a18'),
            ('org010', 'Club Deportivo', 'ASOCIACIÓN', 'deporte@email.com', '948000000', 'Deportivo', 'LOCAL', 'Entrenador Joe', 'Fomento del deporte base.', 'ACTIVO', 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211'),
            ('org011', 'Protectora Animales', 'ONG', 'animales@email.com', '948121212', 'Ambiental', 'REGIONAL', 'Frank Cuesta', 'Cuidado de animales abandonados.', 'ACTIVO', 'https://images.unsplash.com/photo-1450779209076-0920eb596823'),
            ('org012', 'Fundación Ciencia', 'FUNDACIÓN', 'ciencia@email.com', '948131313', 'Tecnológico', 'NACIONAL', 'Marie Curie', 'Divulgación científica.', 'ACTIVO', 'https://images.unsplash.com/photo-1507413245164-6160d8298b31'),
            ('org013', 'Teatro Amateur', 'ASOCIACIÓN', 'teatro@email.com', '948141414', 'Cultural', 'LOCAL', 'Shakespeare', 'Obras de teatro locales.', 'SUSPENDIDO', 'https://images.unsplash.com/photo-1503095392269-271ff41cd0be'),
            ('org014', 'Cruz Verde', 'ONG', 'cruzverde@email.com', '948151515', 'Salud', 'REGIONAL', 'Luigi', 'Apoyo sanitario rural.', 'PENDIENTE', 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7'),
            ('org015', 'Hackers for Good', 'ASOCIACIÓN', 'hackers@email.com', '948161616', 'Tecnológico', 'INTERNACIONAL', 'Neo', 'Ciberseguridad ética.', 'ACTIVO', 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5')");

        // VOLUNTARIO
        $this->addSql("INSERT INTO VOLUNTARIO (codvol, nombre, apellido1, apellido2, correo, telefono, fecha_nacimiento, descripcion, dni, estado, CODCICLO, avatar) VALUES
            ('vol001', 'Pedro', 'Gomez', 'Sanz', 'pedro@email.com', '600111111', '2000-01-15', 'Estudiante motivado.', '11111111A', 'ACTIVO', '1DAM', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d'),
            ('vol002', 'Lucia', 'Martin', 'Perez', 'lucia@email.com', '600222222', '1999-05-20', 'Me gusta ayudar.', '22222222B', 'ACTIVO', '2DAM', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2'),
            ('vol003', 'Jorge', 'Ruiz', 'Lopez', 'jorge@email.com', '600333333', '2001-12-10', 'Busco prácticas.', '33333333C', 'PENDIENTE', '1SMR', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d'),
            ('vol004', 'Ana', 'Blanco', 'Gil', 'ana@email.com', '600444444', '1998-07-30', NULL, '44444444D', 'SUSPENDIDO', '1AC', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80'),
            ('vol005', 'Miguel', 'Soto', 'Diaz', 'miguel@email.com', '600555555', '2002-03-25', 'Apasionado del deporte.', '55555555E', 'ACTIVO', '1ASIR', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e'),
            ('vol006', 'Sofia', 'López', 'García', 'sofia@email.com', '600666666', '2001-02-02', 'Estudiante de enfermería.', '66666666F', 'ACTIVO', '1AAG', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330'),
            ('vol007', 'David', 'Fernandez', 'Ruiz', 'david@email.com', '600777777', '2000-03-03', 'Amante de la naturaleza.', '77777777G', 'ACTIVO', '2DAM', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e'),
            ('vol008', 'Elena', 'Sanchez', 'Mora', 'elena@email.com', '600888888', '1999-04-04', 'Creativa y social.', '88888888H', 'PENDIENTE', '1GA', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80'),
            ('vol009', 'Pablo', 'Diaz', 'Cano', 'pablo@email.com', '600999000', '1998-05-05', 'Deportista.', '99999999I', 'ACTIVO', '2SMR', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde'),
            ('vol010', 'Carmen', 'Torres', 'Vila', 'carmen@email.com', '600101010', '2002-06-06', 'Voluntariada en sangre.', '00000000J', 'ACTIVO', '1af', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2'),
            ('vol011', 'Luis', 'Ramirez', 'Paz', 'luis@email.com', '600121212', '2001-07-07', NULL, '12121212K', 'SUSPENDIDO', '2AF', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d'),
            ('vol012', 'Raquel', 'Molina', 'Sol', 'raquel@email.com', '600131313', '2000-08-08', 'Me gusta programar.', '13131313L', 'ACTIVO', '1DAM', 'https://images.unsplash.com/photo-1554151228-14d9def656ec'),
            ('vol013', 'Victor', 'Gil', 'Luz', 'victor@email.com', '600141414', '1999-09-09', NULL, '14141414M', 'PENDIENTE', '2DAM', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12'),
            ('vol014', 'Nuria', 'Vazquez', 'Rey', 'nuria@email.com', '600151515', '1998-10-10', 'Administrativa.', '15151515N', 'ACTIVO', '1GA', 'https://images.unsplash.com/photo-1542206395-9feb3edaa68d'),
            ('vol015', 'Alberto', 'Serrano', 'Mar', 'alberto@email.com', '600161616', '2002-11-11', 'Marketing digital.', '16161616O', 'ACTIVO', '1AC', 'https://images.unsplash.com/photo-1531427186611-ecfd6d936c79'),
            ('vol016', 'Sara', 'Crespo', 'Rio', 'sara@email.com', '600171717', '2001-12-12', 'Ventas.', '17171717P', 'ACTIVO', '2GV', 'https://images.unsplash.com/photo-1580489944761-15a19d654956'),
            ('vol017', 'Javier', 'Ibañez', 'Ola', 'javier@email.com', '600181818', '2000-01-01', 'Tecnología.', '18181818Q', 'PENDIENTE', '1ASIR', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d'),
            ('vol018', 'Marina', 'Pascual', 'Flor', 'marina@email.com', '600191919', '1999-02-02', NULL, '19191919R', 'ACTIVO', '2TL', 'https://images.unsplash.com/photo-1599566150163-29194dcaad36'),
            ('vol019', 'Roberto', 'Leon', 'Sol', 'roberto@email.com', '600202020', '1998-03-03', 'Logística.', '20202020S', 'SUSPENDIDO', '1TL', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7'),
            ('vol020', 'Cristina', 'Garrido', 'Luz', 'cristina@email.com', '600212121', '2002-04-04', 'Comercio.', '21212121T', 'ACTIVO', '1CI', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb')");

        // ACTIVIDAD
        $this->addSql("SET IDENTITY_INSERT ACTIVIDAD ON; INSERT INTO ACTIVIDAD (CODACT, nombre, duracion_sesion, fecha_inicio, fecha_fin, n_max_voluntarios, descripcion, estado, CODORG, ubicacion, imagen) VALUES
            (1, 'Recogida Alimentos Navidad', '04:00:00', '2025-12-20', '2025-12-22', 50, 'Campaña anual de navidad.', 'FINALIZADA', 'org002', 'Centro Comercial La Morea', 'https://images.unsplash.com/photo-1512406981147-3844a4962c4a'),
            (2, 'Apoyo Escolar Primaria', '01:30:00', '2026-01-10', '2026-06-20', 10, 'Clases de refuerzo.', 'ACTIVO', 'org005', 'Colegio Publico San Jorge', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b'),
            (3, 'Maraton Solidaria', '05:00:00', '2026-03-15', '2026-03-15', 100, 'Evento deportivo benéfico.', 'PENDIENTE', 'org001', 'Vuelta del Castillo', 'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3'),
            (4, 'Limpieza Rio Arga', '03:00:00', '2026-04-22', '2026-04-22', 30, 'Día de la tierra.', 'EN_PROGRESO', 'org003', 'Paseo del Arga', 'https://images.unsplash.com/photo-1618477461853-5f8dd1219df0'),
            (5, 'Charla Salud Mental', '02:00:00', '2025-11-15', '2025-11-15', 20, 'Charla concienciación.', 'FINALIZADA', 'org004', 'Civivox Iturrama', 'https://images.unsplash.com/photo-1544367563-12123d8965cd'),
            (6, 'Torneo Fútbol Sala', '02:00:00', '2026-05-10', '2026-05-12', 40, 'Torneo inter-escolar.', 'ACTIVO', 'org005', 'Polideportivo Arrosadia', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6'),
            (7, 'Visita Residencia', '01:30:00', '2026-02-14', '2026-02-14', 15, 'Acompañamiento mayores.', 'SUSPENDIDA', 'org001', 'Residencia La Vaguada', 'https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4'),
            (8, 'Campaña Vacunación', '08:00:00', '2026-06-01', '2026-06-15', 20, 'Apoyo logístico.', 'PENDIENTE', 'org006', 'Pamplona', 'https://images.unsplash.com/photo-1632053000632-4d2c478a0448'),
            (9, 'Reforestación Bosque', '05:00:00', '2026-03-21', '2026-03-21', 100, 'Plantar árboles.', 'ACTIVO', 'org007', 'Sierra de Urbasa', 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09'),
            (10, 'Comedor Social Fin de Semana', '03:00:00', '2026-01-01', '2026-12-31', 5, 'Servir comidas.', 'ACTIVO', 'org008', 'Pamplona', 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c'),
            (11, 'Fiesta del Barrio', '06:00:00', '2026-07-10', '2026-07-12', 30, 'Organización eventos.', 'PENDIENTE', 'org009', 'Rochapea', 'https://images.unsplash.com/photo-1530103862676-de3c9da59af7'),
            (12, 'Entrenamiento Inclusivo', '01:30:00', '2026-02-01', '2026-05-30', 4, 'Ayuda entrenadores.', 'ACTIVO', 'org010', 'Polideportivo Arrosadia', 'https://images.unsplash.com/photo-1518611012118-696072aa579a'),
            (13, 'Paseo Perros Refugio', '02:00:00', '2026-01-20', '2026-12-20', 10, 'Pasear perros.', 'ACTIVO', 'org011', 'Mutilva', 'https://images.unsplash.com/photo-1625316708582-7c38734be31d'),
            (14, 'Taller Robótica Niños', '02:00:00', '2025-10-01', '2025-10-30', 8, 'Enseñar robótica básica.', 'FINALIZADA', 'org012', 'Civivox', 'https://images.unsplash.com/photo-1589254065878-42c9da9e2ab2'),
            (15, 'Montaje Escenario', '04:00:00', '2026-04-10', '2026-04-10', 10, 'Montar escenario obra.', 'SUSPENDIDA', 'org013', 'Barañain', 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30'),
            (16, 'Revisión Médica Rural', '05:00:00', '2026-08-05', '2026-08-07', 6, 'Acompañamiento rutas.', 'PENDIENTE', 'org014', 'Zona Norte', 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528'),
            (17, 'Auditoria Seguridad', '03:00:00', '2026-09-01', '2026-09-30', 5, 'Análisis redes ONGs.', 'ACTIVO', 'org015', 'Online', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b'),
            (18, 'Recogida Juguetes', '03:00:00', '2025-12-01', '2025-12-15', 20, 'Navidad para todos.', 'FINALIZADA', 'org001', 'Pamplona', 'https://images.unsplash.com/photo-1512353087810-25dfcd100962'),
            (19, 'Apoyo Digital Mayores', '01:30:00', '2026-01-15', '2026-06-15', 5, 'Enseñar uso móvil.', 'ACTIVO', 'org015', 'Residencia', 'https://images.unsplash.com/photo-1485981133625-f1a0918f3075'),
            (20, 'Torneo Baloncesto', '04:00:00', '2026-05-20', '2026-05-22', 15, 'Arbitraje y mesa.', 'PENDIENTE', 'org010', 'Pamplona', 'https://images.unsplash.com/photo-1505666287802-931dc83948e9'),
            (21, 'Limpieza Playa', '04:00:00', '2026-06-05', '2026-06-05', 50, 'Día océanos.', 'PENDIENTE', 'org003', 'San Sebastián', 'https://images.unsplash.com/photo-1621451537084-482c73073a0f'),
            (22, 'Clases Español', '01:30:00', '2026-02-01', '2026-06-30', 3, 'Para inmigrantes.', 'ACTIVO', 'org001', 'Pamplona', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40'),
            (23, 'Taller Reciclaje Creativo', '02:00:00', '2026-05-15', '2026-05-15', 15, 'Aprende a reutilizar materiales.', 'EN_PROGRESO', 'org003', 'Casa de Juventud', 'https://images.unsplash.com/photo-1530587191326-6f1274efb383'),
            (24, 'Maraton de Programación', '12:00:00', '2026-05-20', '2026-05-20', 50, 'Hackathon social.', 'EN_PROGRESO', 'org015', 'Universidad Publica', 'https://images.unsplash.com/photo-1504384308090-c54be385363d'),
            (25, 'Cocina Solidaria', '04:00:00', '2026-05-22', '2026-05-22', 10, 'Cocinar para los necesitados.', 'EN_PROGRESO', 'org002', 'Comedor Paris 365', 'https://images.unsplash.com/photo-1605333396915-47edfe2067d0'); SET IDENTITY_INSERT ACTIVIDAD OFF;");

        // CREDENCIALES FOR ORG & VOL
        $this->addSql("INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES ('ORGANIZACION', 'cruzroja@email.com', 'admin123', 'org001'), ('ORGANIZACION', 'banco@email.com', 'admin123', 'org002'), ('ORGANIZACION', 'greenpeace@email.com', 'admin123', 'org003'), ('ORGANIZACION', 'sanjuan@email.com', 'admin123', 'org004'), ('ORGANIZACION', 'ilusion@email.com', 'admin123', 'org005'), ('ORGANIZACION', 'msf@email.com', 'admin123', 'org006'), ('ORGANIZACION', 'wwf@email.com', 'admin123', 'org007'), ('ORGANIZACION', 'caritas@email.com', 'admin123', 'org008'), ('ORGANIZACION', 'vecinos@email.com', 'admin123', 'org009'), ('ORGANIZACION', 'deporte@email.com', 'admin123', 'org010'), ('ORGANIZACION', 'animales@email.com', 'admin123', 'org011'), ('ORGANIZACION', 'ciencia@email.com', 'admin123', 'org012'), ('ORGANIZACION', 'teatro@email.com', 'admin123', 'org013'), ('ORGANIZACION', 'cruzverde@email.com', 'admin123', 'org014'), ('ORGANIZACION', 'hackers@email.com', 'admin123', 'org015')");
        $this->addSql("INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES ('VOLUNTARIO', 'pedro@email.com', 'admin123', 'vol001'), ('VOLUNTARIO', 'lucia@email.com', 'admin123', 'vol002'), ('VOLUNTARIO', 'jorge@email.com', 'admin123', 'vol003'), ('VOLUNTARIO', 'ana@email.com', 'admin123', 'vol004'), ('VOLUNTARIO', 'miguel@email.com', 'admin123', 'vol005'), ('VOLUNTARIO', 'sofia@email.com', 'admin123', 'vol006'), ('VOLUNTARIO', 'david@email.com', 'admin123', 'vol007'), ('VOLUNTARIO', 'elena@email.com', 'admin123', 'vol008'), ('VOLUNTARIO', 'pablo@email.com', 'admin123', 'vol009'), ('VOLUNTARIO', 'carmen@email.com', 'admin123', 'vol010'), ('VOLUNTARIO', 'luis@email.com', 'admin123', 'vol011'), ('VOLUNTARIO', 'raquel@email.com', 'admin123', 'vol012'), ('VOLUNTARIO', 'victor@email.com', 'admin123', 'vol013'), ('VOLUNTARIO', 'nuria@email.com', 'admin123', 'vol014'), ('VOLUNTARIO', 'alberto@email.com', 'admin123', 'vol015'), ('VOLUNTARIO', 'sara@email.com', 'admin123', 'vol016'), ('VOLUNTARIO', 'javier@email.com', 'admin123', 'vol017'), ('VOLUNTARIO', 'marina@email.com', 'admin123', 'vol018'), ('VOLUNTARIO', 'roberto@email.com', 'admin123', 'vol019'), ('VOLUNTARIO', 'cristina@email.com', 'admin123', 'vol020')");

        // RELATIONS (Participation, Preferences, Availability, ODS)
        $this->addSql("INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (1, 'vol001'), (1, 'vol002'), (2, 'vol005'), (1, 'vol003'), (1, 'vol004'), (1, 'vol005'), (1, 'vol008'), (3, 'vol001'), (3, 'vol002'), (3, 'vol006'), (3, 'vol009'), (3, 'vol015'), (4, 'vol003'), (4, 'vol007'), (4, 'vol011'), (4, 'vol012'), (4, 'vol018'), (5, 'vol004'), (5, 'vol008'), (5, 'vol013'), (5, 'vol016'), (14, 'vol001'), (14, 'vol005'), (14, 'vol010'), (14, 'vol014'), (14, 'vol019'), (8, 'vol002'), (8, 'vol006'), (8, 'vol009'), (8, 'vol017'), (8, 'vol020'), (9, 'vol003'), (9, 'vol007'), (9, 'vol011'), (9, 'vol015'), (11, 'vol004'), (11, 'vol008'), (11, 'vol012'), (11, 'vol016'), (11, 'vol018'), (12, 'vol005'), (12, 'vol010'), (12, 'vol013'), (12, 'vol019'), (13, 'vol001'), (13, 'vol006'), (13, 'vol009'), (13, 'vol014'), (13, 'vol017'), (13, 'vol020'), (13, 'vol002'), (13, 'vol007'), (13, 'vol011'), (13, 'vol015'), (15, 'vol003'), (15, 'vol008'), (15, 'vol012'), (15, 'vol016'), (15, 'vol018'), (23, 'vol006'), (23, 'vol007'), (24, 'vol012'), (24, 'vol017'), (25, 'vol001'), (25, 'vol002')");
        
        $this->addSql("INSERT INTO DISPONIBILIDAD (dia, hora, CODVOL, NUM_HORAS) VALUES ('LUNES', '16:00', 'vol001', 3), ('VIERNES', '10:00', 'vol002', 4), ('MARTES', '17:00', 'vol006', 2), ('SABADO', '10:00', 'vol007', 5)");

        $this->addSql("INSERT INTO VOL_PREFIERE_TACT (CODVOL, CODTIPO) VALUES ('vol001', 1), ('vol001', 3), ('vol002', 2), ('vol002', 4), ('vol003', 5), ('vol003', 6), ('vol004', 7), ('vol004', 8), ('vol005', 1), ('vol005', 5), ('vol008', 2), ('vol008', 6), ('vol009', 3), ('vol009', 7), ('vol010', 4), ('vol010', 8), ('vol011', 1), ('vol011', 2), ('vol013', 3), ('vol013', 4), ('vol014', 5), ('vol014', 6), ('vol015', 7), ('vol015', 8), ('vol016', 1), ('vol016', 4), ('vol017', 2), ('vol017', 5), ('vol018', 3), ('vol018', 6), ('vol019', 4), ('vol019', 7), ('vol020', 5), ('vol020', 8), ('vol006', 2), ('vol006', 6), ('vol007', 4), ('vol007', 5), ('vol012', 1), ('vol012', 8)");

        $this->addSql("INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (1, 6), (1, 3), (10, 6), (10, 2), (11, 6), (11, 7), (13, 4), (13, 6), (15, 7), (15, 8), (16, 2), (16, 6), (18, 6), (18, 7), (19, 1), (19, 3), (20, 5), (20, 3), (21, 4), (21, 6), (22, 3), (22, 7), (9, 4), (9, 6), (12, 5), (14, 5), (14, 1), (17, 1), (17, 8), (23, 4), (23, 7), (24, 8), (24, 1), (25, 6), (25, 2)");

        $this->addSql("INSERT INTO SOLICITUD (CODVOL, CODACT, status, fecha_solicitud, mensaje) VALUES ('vol006', 2, 'PENDIENTE', '2026-01-10 10:00:00', 'Me interesa mucho.'), ('vol007', 2, 'ACEPTADA', '2026-01-05 09:00:00', 'Tengo experiencia.'), ('vol008', 3, 'PENDIENTE', '2026-01-18 11:30:00', 'Quiero ayudar.'), ('vol009', 5, 'RECHAZADA', '2026-01-15 12:00:00', 'No puedo ir.'), ('vol010', 6, 'ACEPTADA', '2026-01-14 16:00:00', 'Me encantan los perros.'), ('vol011', 8, 'PENDIENTE', '2026-01-19 10:00:00', 'Soy fuerte.'), ('vol012', 10, 'ACEPTADA', '2026-01-12 09:15:00', 'Estudio ASIR.'), ('vol013', 10, 'PENDIENTE', '2026-01-18 18:00:00', 'Me gusta la seguridad.'), ('vol001', 5, 'PENDIENTE', '2026-01-20 08:30:00', 'Tengo libre.'), ('vol002', 6, 'ACEPTADA', '2026-01-10 17:00:00', 'Tengo perro.'), ('vol003', 4, 'PENDIENTE', '2026-02-01 10:00:00', 'Quiero participar'), ('vol004', 4, 'ACEPTADA', '2026-01-25 09:30:00', 'Soy vecino'), ('vol005', 4, 'RECHAZADA', '2026-01-26 11:00:00', 'No cumples perfil'), ('vol001', 14, 'ACEPTADA', '2026-01-20 15:00:00', 'Me gusta la robótica'), ('vol002', 14, 'PENDIENTE', '2026-01-21 16:30:00', 'Tengo experiencia'), ('vol008', 16, 'PENDIENTE', '2026-03-01 09:00:00', 'Soy enfermera'), ('vol009', 16, 'ACEPTADA', '2026-03-02 10:00:00', 'Ayudo en logística'), ('vol011', 18, 'PENDIENTE', '2026-05-10 12:00:00', 'Tengo coche'), ('vol012', 18, 'RECHAZADA', '2026-05-11 13:00:00', 'Ya esta lleno'), ('vol015', 20, 'ACEPTADA', '2026-04-01 17:00:00', 'Soy árbitro'), ('vol016', 20, 'PENDIENTE', '2026-04-02 18:00:00', 'Puedo llevar agua'), ('vol018', 21, 'PENDIENTE', '2026-05-20 08:00:00', 'Me gusta el mar'), ('vol019', 21, 'ACEPTADA', '2026-05-21 09:00:00', 'Voy con amigos'), ('vol020', 22, 'PENDIENTE', '2026-01-25 19:00:00', 'Hablo inglés también'), ('vol006', 23, 'PENDIENTE', '2026-05-10 10:00:00', 'Quiero aprender a reciclar'), ('vol012', 24, 'ACEPTADA', '2026-05-10 11:00:00', 'Soy programador'), ('vol001', 25, 'PENDIENTE', '2026-05-15 09:00:00', 'Me gusta cocinar')");

        $this->addSql("INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (1, 2), (1, 1), (2, 4), (3, 3), (4, 6), (4, 15), (5, 3), (6, 3), (7, 10), (8, 3), (9, 13), (9, 15), (10, 2), (11, 11), (12, 3), (12, 10), (13, 15), (14, 4), (14, 9), (15, 10), (16, 3), (17, 9), (18, 1), (18, 10), (19, 4), (19, 10), (20, 3), (21, 14), (22, 4), (22, 10), (23, 12), (24, 4), (24, 9), (25, 2)");
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
        $this->addSql('ALTER TABLE ACTIVIDAD DROP CONSTRAINT FK_C930A3E9ED28F88B');
        $this->addSql('ALTER TABLE VOL_PARTICIPA_ACT DROP CONSTRAINT FK_706140E930D1B74F');
        $this->addSql('ALTER TABLE VOL_PARTICIPA_ACT DROP CONSTRAINT FK_706140E99661D5E0');
        $this->addSql('ALTER TABLE ACT_ASOCIADO_TACT DROP CONSTRAINT FK_9B6A42AF30D1B74F');
        $this->addSql('ALTER TABLE ACT_ASOCIADO_TACT DROP CONSTRAINT FK_9B6A42AFDC0ED945');
        $this->addSql('ALTER TABLE ACT_PRACTICA_ODS DROP CONSTRAINT FK_4FC76DA630D1B74F');
        $this->addSql('ALTER TABLE ACT_PRACTICA_ODS DROP CONSTRAINT FK_4FC76DA6AC4A56');
        $this->addSql('ALTER TABLE CREDENCIALES DROP CONSTRAINT FK_6B3529C09661D5E0');
        $this->addSql('ALTER TABLE CREDENCIALES DROP CONSTRAINT FK_6B3529C0ED28F88B');
        $this->addSql('ALTER TABLE CREDENCIALES DROP CONSTRAINT FK_6B3529C05DC26F56');
        $this->addSql('ALTER TABLE DISPONIBILIDAD DROP CONSTRAINT FK_A042D1129661D5E0');
        $this->addSql('ALTER TABLE SOLICITUD DROP CONSTRAINT FK_D210622F9661D5E0');
        $this->addSql('ALTER TABLE SOLICITUD DROP CONSTRAINT FK_D210622F30D1B74F');
        $this->addSql('ALTER TABLE VOLUNTARIO DROP CONSTRAINT FK_2AFD2CC1D5860D32');
        $this->addSql('ALTER TABLE VOL_PREFIERE_TACT DROP CONSTRAINT FK_570D03019661D5E0');
        $this->addSql('ALTER TABLE VOL_PREFIERE_TACT DROP CONSTRAINT FK_570D0301DC0ED945');

        $this->addSql('DROP TABLE ACTIVIDAD');
        $this->addSql('DROP TABLE VOL_PARTICIPA_ACT');
        $this->addSql('DROP TABLE ACT_ASOCIADO_TACT');
        $this->addSql('DROP TABLE ACT_PRACTICA_ODS');
        $this->addSql('DROP TABLE ADMINISTRADOR');
        $this->addSql('DROP TABLE CICLO');
        $this->addSql('DROP TABLE CREDENCIALES');
        $this->addSql('DROP TABLE DISPONIBILIDAD');
        $this->addSql('DROP TABLE ODS');
        $this->addSql('DROP TABLE ORGANIZACION');
        $this->addSql('DROP TABLE SOLICITUD');
        $this->addSql('DROP TABLE TIPO_ACTIVIDAD');
        $this->addSql('DROP TABLE VOLUNTARIO');
        $this->addSql('DROP TABLE VOL_PREFIERE_TACT');
    }
}
