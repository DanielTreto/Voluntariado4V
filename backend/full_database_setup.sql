-- SCRIPT DE CREACIÓN COMPLETA DE LA BASE DE DATOS VOLUNTARIADOBD
-- Este script crea la base de datos, las tablas y carga datos maestros necesarios.

USE master;
GO

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'VOLUNTARIADOBD')
BEGIN
    CREATE DATABASE VOLUNTARIADOBD;
END
GO

USE VOLUNTARIADOBD;
GO

-- =============================================
-- 1. ELIMINACIÓN DE TABLAS SI EXISTEN (Orden inverso por FKS)
-- =============================================
IF OBJECT_ID('dbo.CREDENCIALES', 'U') IS NOT NULL DROP TABLE dbo.CREDENCIALES;
IF OBJECT_ID('dbo.ACT_PRACTICA_ODS', 'U') IS NOT NULL DROP TABLE dbo.ACT_PRACTICA_ODS;
IF OBJECT_ID('dbo.ACT_ASOCIADO_TACT', 'U') IS NOT NULL DROP TABLE dbo.ACT_ASOCIADO_TACT;
IF OBJECT_ID('dbo.VOL_PARTICIPA_ACT', 'U') IS NOT NULL DROP TABLE dbo.VOL_PARTICIPA_ACT;
IF OBJECT_ID('dbo.VOL_PREFIERE_TACT', 'U') IS NOT NULL DROP TABLE dbo.VOL_PREFIERE_TACT;
IF OBJECT_ID('dbo.SOLICITUD', 'U') IS NOT NULL DROP TABLE dbo.SOLICITUD;
IF OBJECT_ID('dbo.DISPONIBILIDAD', 'U') IS NOT NULL DROP TABLE dbo.DISPONIBILIDAD;
IF OBJECT_ID('dbo.ACTIVIDAD', 'U') IS NOT NULL DROP TABLE dbo.ACTIVIDAD;
IF OBJECT_ID('dbo.VOLUNTARIO', 'U') IS NOT NULL DROP TABLE dbo.VOLUNTARIO;
IF OBJECT_ID('dbo.ORGANIZACION', 'U') IS NOT NULL DROP TABLE dbo.ORGANIZACION;
IF OBJECT_ID('dbo.ADMINISTRADOR', 'U') IS NOT NULL DROP TABLE dbo.ADMINISTRADOR;
IF OBJECT_ID('dbo.TIPO_ACTIVIDAD', 'U') IS NOT NULL DROP TABLE dbo.TIPO_ACTIVIDAD;
IF OBJECT_ID('dbo.ODS', 'U') IS NOT NULL DROP TABLE dbo.ODS;
IF OBJECT_ID('dbo.CICLO', 'U') IS NOT NULL DROP TABLE dbo.CICLO;
GO

-- =============================================
-- 2. CREACIÓN DE TABLAS
-- =============================================

CREATE TABLE ODS (
    numods INT NOT NULL, 
    descripcion NVARCHAR(70) NOT NULL, 
    PRIMARY KEY (numods)
);

CREATE TABLE CICLO (
    codciclo NVARCHAR(10) NOT NULL, 
    nombre NVARCHAR(70) NOT NULL, 
    curso INT NOT NULL, 
    PRIMARY KEY (codciclo)
);

CREATE TABLE TIPO_ACTIVIDAD (
    codtipo INT IDENTITY(1,1) NOT NULL, 
    descripcion NVARCHAR(20) NOT NULL, 
    PRIMARY KEY (codtipo)
);

CREATE TABLE ADMINISTRADOR (
    id NVARCHAR(20) NOT NULL, 
    nombre NVARCHAR(50) NOT NULL, 
    apellidos NVARCHAR(100) NOT NULL, 
    correo NVARCHAR(100) NOT NULL, 
    telefono NVARCHAR(20) NOT NULL, 
    password NVARCHAR(255) NOT NULL, 
    avatar NVARCHAR(255), 
    firebase_uid NVARCHAR(128),
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX UNIQ_62B5A7C477040BC9 ON ADMINISTRADOR (correo) WHERE correo IS NOT NULL;

CREATE TABLE ORGANIZACION (
    codorg NVARCHAR(20) NOT NULL, 
    nombre NVARCHAR(50) NOT NULL, 
    tipo_org NVARCHAR(25) NOT NULL, 
    correo NVARCHAR(50) NOT NULL, 
    telefono NVARCHAR(9) NOT NULL, 
    sector NVARCHAR(20) NOT NULL, 
    ambito NVARCHAR(20) NOT NULL, 
    password NVARCHAR(255) NOT NULL, 
    persona_contacto NVARCHAR(100), 
    descripcion NVARCHAR(500), 
    estado NVARCHAR(20) NOT NULL, 
    firebase_uid NVARCHAR(128),
    avatar NVARCHAR(255),
    PRIMARY KEY (codorg)
);
CREATE UNIQUE INDEX UNIQ_9912454A77040BC9 ON ORGANIZACION (correo) WHERE correo IS NOT NULL;
CREATE UNIQUE INDEX UNIQ_9912454AC1E70A7F ON ORGANIZACION (telefono) WHERE telefono IS NOT NULL;

CREATE TABLE VOLUNTARIO (
    codvol NVARCHAR(20) NOT NULL, 
    nombre NVARCHAR(30) NOT NULL, 
    apellido1 NVARCHAR(30) NOT NULL, 
    apellido2 NVARCHAR(30), 
    correo NVARCHAR(50) NOT NULL, 
    telefono NVARCHAR(9) NOT NULL, 
    fecha_nacimiento DATE NOT NULL, 
    descripcion NVARCHAR(500), 
    dni NVARCHAR(9) NOT NULL, 
    password NVARCHAR(255) NOT NULL, 
    estado NVARCHAR(10) NOT NULL, 
    CODCICLO NVARCHAR(10) NOT NULL, 
    firebase_uid NVARCHAR(128),
    avatar NVARCHAR(255),
    PRIMARY KEY (codvol)
);
CREATE UNIQUE INDEX UNIQ_2AFD2CC177040BC9 ON VOLUNTARIO (correo) WHERE correo IS NOT NULL;
CREATE UNIQUE INDEX UNIQ_2AFD2CC17F8F253B ON VOLUNTARIO (dni) WHERE dni IS NOT NULL;
CREATE INDEX IDX_2AFD2CC1D5860D32 ON VOLUNTARIO (CODCICLO);

CREATE TABLE ACTIVIDAD (
    CODACT INT IDENTITY(1,1) NOT NULL, 
    nombre NVARCHAR(70) NOT NULL, 
    duracion_sesion NVARCHAR(20) NOT NULL, 
    fecha_inicio DATE NOT NULL, 
    fecha_fin DATE NOT NULL, 
    n_max_voluntarios INT NOT NULL, 
    descripcion NVARCHAR(500) NOT NULL, 
    estado NVARCHAR(20) NOT NULL, 
    CODORG NVARCHAR(20) NOT NULL, 
    ubicacion NVARCHAR(255),
    imagen NVARCHAR(255),
    PRIMARY KEY (CODACT)
);
CREATE INDEX IDX_C930A3E9ED28F88B ON ACTIVIDAD (CODORG);

CREATE TABLE SOLICITUD (
    id INT IDENTITY(1,1) NOT NULL,
    CODVOL NVARCHAR(20) NOT NULL,
    CODACT INT NOT NULL,
    status NVARCHAR(20) NOT NULL,
    fecha_solicitud DATETIME NOT NULL,
    mensaje NVARCHAR(MAX),
    PRIMARY KEY (id)
);
CREATE INDEX IDX_SOLICITUD_CODVOL ON SOLICITUD (CODVOL);
CREATE INDEX IDX_SOLICITUD_CODACT ON SOLICITUD (CODACT);

CREATE TABLE CREDENCIALES (
    id INT IDENTITY(1,1) NOT NULL, 
    user_type NVARCHAR(20) NOT NULL, 
    correo NVARCHAR(180) NOT NULL, 
    password NVARCHAR(255) NOT NULL, 
    CODVOL NVARCHAR(20), 
    CODORG NVARCHAR(20), 
    CODADMIN NVARCHAR(20), 
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX UNIQ_6B3529C077040BC9 ON CREDENCIALES (correo) WHERE correo IS NOT NULL;
CREATE UNIQUE INDEX UNIQ_6B3529C09661D5E0 ON CREDENCIALES (CODVOL) WHERE CODVOL IS NOT NULL;
CREATE UNIQUE INDEX UNIQ_6B3529C0ED28F88B ON CREDENCIALES (CODORG) WHERE CODORG IS NOT NULL;
CREATE UNIQUE INDEX UNIQ_6B3529C0642B8210 ON CREDENCIALES (CODADMIN) WHERE CODADMIN IS NOT NULL;

CREATE TABLE DISPONIBILIDAD (
    dia NVARCHAR(10) NOT NULL, 
    hora NVARCHAR(10) NOT NULL, 
    CODVOL NVARCHAR(20) NOT NULL, 
    PRIMARY KEY (CODVOL, dia, hora)
);
CREATE INDEX IDX_A042D1129661D5E0 ON DISPONIBILIDAD (CODVOL);

CREATE TABLE VOL_PREFIERE_TACT (
    CODVOL NVARCHAR(20) NOT NULL, 
    CODTIPO INT NOT NULL, 
    PRIMARY KEY (CODVOL, CODTIPO)
);
CREATE INDEX IDX_570D03019661D5E0 ON VOL_PREFIERE_TACT (CODVOL);
CREATE INDEX IDX_570D0301DC0ED945 ON VOL_PREFIERE_TACT (CODTIPO);

CREATE TABLE VOL_PARTICIPA_ACT (
    CODACT INT NOT NULL, 
    CODVOL NVARCHAR(20) NOT NULL, 
    PRIMARY KEY (CODACT, CODVOL)
);
CREATE INDEX IDX_706140E930D1B74F ON VOL_PARTICIPA_ACT (CODACT);
CREATE INDEX IDX_706140E99661D5E0 ON VOL_PARTICIPA_ACT (CODVOL);

CREATE TABLE ACT_ASOCIADO_TACT (
    CODACT INT NOT NULL, 
    CODTIPO INT NOT NULL, 
    PRIMARY KEY (CODACT, CODTIPO)
);
CREATE INDEX IDX_9B6A42AF30D1B74F ON ACT_ASOCIADO_TACT (CODACT);
CREATE INDEX IDX_9B6A42AFDC0ED945 ON ACT_ASOCIADO_TACT (CODTIPO);

CREATE TABLE ACT_PRACTICA_ODS (
    CODACT INT NOT NULL, 
    NUMODS INT NOT NULL, 
    PRIMARY KEY (CODACT, NUMODS)
);
CREATE INDEX IDX_4FC76DA630D1B74F ON ACT_PRACTICA_ODS (CODACT);
CREATE INDEX IDX_4FC76DA6AC4A56 ON ACT_PRACTICA_ODS (NUMODS);

-- =============================================
-- 3. CONSTRAINTS (FOREIGN KEYS)
-- =============================================

ALTER TABLE ACTIVIDAD ADD CONSTRAINT FK_C930A3E9ED28F88B FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG);
ALTER TABLE SOLICITUD ADD CONSTRAINT FK_SOLICITUD_VOLUNTARIO FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
ALTER TABLE SOLICITUD ADD CONSTRAINT FK_SOLICITUD_ACTIVIDAD FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_706140E930D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
ALTER TABLE VOL_PARTICIPA_ACT ADD CONSTRAINT FK_706140E99661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_9B6A42AF30D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
ALTER TABLE ACT_ASOCIADO_TACT ADD CONSTRAINT FK_9B6A42AFDC0ED945 FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO);
ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_4FC76DA630D1B74F FOREIGN KEY (CODACT) REFERENCES ACTIVIDAD (CODACT);
ALTER TABLE ACT_PRACTICA_ODS ADD CONSTRAINT FK_4FC76DA6AC4A56 FOREIGN KEY (NUMODS) REFERENCES ODS (NUMODS);
ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C09661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C0ED28F88B FOREIGN KEY (CODORG) REFERENCES ORGANIZACION (CODORG);
ALTER TABLE CREDENCIALES ADD CONSTRAINT FK_6B3529C0642B8210 FOREIGN KEY (CODADMIN) REFERENCES ADMINISTRADOR (id);
ALTER TABLE DISPONIBILIDAD ADD CONSTRAINT FK_A042D1129661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
ALTER TABLE VOLUNTARIO ADD CONSTRAINT FK_2AFD2CC1D5860D32 FOREIGN KEY (CODCICLO) REFERENCES CICLO (CODCICLO);
ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_570D03019661D5E0 FOREIGN KEY (CODVOL) REFERENCES VOLUNTARIO (CODVOL);
ALTER TABLE VOL_PREFIERE_TACT ADD CONSTRAINT FK_570D0301DC0ED945 FOREIGN KEY (CODTIPO) REFERENCES TIPO_ACTIVIDAD (CODTIPO);

-- =============================================
-- 4. INSERT DATA (DATOS MAESTROS)
-- =============================================

-- ODS
INSERT INTO ODS (NUMODS, DESCRIPCION)  
VALUES  
(1, 'Fin de la pobreza'),  
(2, 'Hambre cero'),  
(3, 'Salud y bienestar'),  
(4, 'Educación de calidad'),  
(5, 'Igualdad de género'),  
(6, 'Agua limpia y saneamiento'),  
(7, 'Energía asequible y no contaminante'),  
(8, 'Trabajo decente y crecimiento económico'),  
(9, 'Industria, innovación e infraestructura'),  
(10, 'Reducción de las desigualdades'),  
(11, 'Ciudades y comunidades sostenibles'),  
(12, 'Producción y consumo responsables'),  
(13, 'Acción por el clima'),  
(14, 'Vida submarina'),  
(15, 'Vida de ecosistemas terrestres'),  
(16, 'Paz, justicia e instituciones sólidas'),  
(17, 'Alianzas para lograr los objetivos');

-- TIPO_ACTIVIDAD
INSERT INTO TIPO_ACTIVIDAD (DESCRIPCION)
VALUES
('Digital'),
('Salud'),
('Educativo'),
('Ambiental'),
('Deportivo'),
('Social'),
('Cultural'),
('Tecnico');

-- CICLO
INSERT INTO CICLO (CODCICLO, NOMBRE, CURSO)
VALUES
('1SC', 'SERVICIOS COMERCIALES', 1),
('2SC', 'SERVICIOS COMERCIALES', 2),
('1AAG', 'AUXILIAR ADMINISTRATIVO GENERAL', 1),
('2AAG', 'AUXILIAR ADMINISTRATIVO GENERAL', 2),
('1GA', 'GESTIÓN ADMINISTRATIVA', 1),
('2GA', 'GESTIÓN ADMINISTRATIVA', 2),
('1AC', 'ACTIVIDADES COMERCIALES', 1),
('2AC', 'ACTIVIDADES COMERCIALES', 2),
('1SMR', 'SISTEMAS MICROINFORMÁTICOS Y REDES', 1),
('2SMR', 'SISTEMAS MICROINFORMÁTICOS Y REDES', 2),
('1AF', 'ADMINISTRACIÓN Y FINANZAS', 1),
('2AF', 'ADMINISTRACIÓN Y FINANZAS', 2),
('1CI', 'COMERCIO INTERNACIONAL', 1),
('2CI', 'COMERCIO INTERNACIONAL', 2),
('1GV', 'GESTIÓN DE VENTAS', 1),
('2GV', 'GESTIÓN DE VENTAS', 2),
('1TL', 'TRANSPORTE Y LOGÍSTICA', 1),
('2TL', 'TRANSPORTE Y LOGÍSTICA', 2),
('1ASIR', 'ADMINISTRACIÓN DE SISTEMAS INFORMÁTICOS Y REDES', 1),
('2ASIR', 'ADMINISTRACIÓN DE SISTEMAS INFORMÁTICOS Y REDES', 2),
('1DAM', 'DESARROLLO DE APLICACIONES MULTIPLATAFORMA', 1),
('2DAM', 'DESARROLLO DE APLICACIONES MULTIPLATAFORMA', 2);

-- ADMINISTRADOR
INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, password, avatar) 
VALUES ('adm001', 'Admin', 'Principal', 'admin@voluntariado.com', '600123456', 'admin123', 'https://avatar.iran.liara.run/public/job/police/male'); 

INSERT INTO CREDENCIALES (user_type, correo, password, CODADMIN)
VALUES ('ADMINISTRADOR', 'admin@voluntariado.com', 'admin123', 'adm001');

-- ORGANIZACION
INSERT INTO ORGANIZACION (codorg, nombre, tipo_org, correo, telefono, sector, ambito, password, persona_contacto, descripcion, estado, avatar)
VALUES 
('org001', 'Cruz Roja', 'ONG', 'cruzroja@email.com', '948111111', 'Social', 'NACIONAL', 'admin123', 'Maria Garcia', 'Ayuda humanitaria internacional.', 'ACTIVO', 'https://images.unsplash.com/photo-1584515933487-779824d29609'),
('org002', 'Banco de Alimentos', 'ONG', 'banco@email.com', '948222222', 'Social', 'REGIONAL', 'admin123', 'Juan Perez', 'Recogida y distribucion de alimentos.', 'ACTIVO', 'https://images.unsplash.com/photo-1593113598332-cd288d649433'),
('org003', 'Greenpeace', 'ONG', 'greenpeace@email.com', '948333333', 'Ambiental', 'INTERNACIONAL', 'admin123', 'Laura Sanz', 'Defensa del medio ambiente.', 'PENDIENTE', 'https://images.unsplash.com/photo-1500485035595-cbe6f645feb1'),
('org004', 'Asoc. San Juan', 'ASOCIACIÓN', 'sanjuan@email.com', '948444444', 'Salud', 'LOCAL', 'admin123', 'Carlos Ruiz', 'Apoyo a pacientes.', 'SUSPENDIDO', 'https://images.unsplash.com/photo-1538108149393-fbbd81895907'),
('org005', 'Fundacion Ilusion', 'FUNDACIÓN', 'ilusion@email.com', '948555555', 'Educativo', 'NACIONAL', 'admin123', 'Elena Muro', 'Educacion para todos.', 'ACTIVO', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6');

-- VOLUNTARIO
INSERT INTO VOLUNTARIO (codvol, nombre, apellido1, apellido2, correo, telefono, fecha_nacimiento, descripcion, dni, password, estado, CODCICLO, avatar)
VALUES
('vol001', 'Pedro', 'Gomez', 'Sanz', 'pedro@email.com', '600111111', '2000-01-15', 'Estudiante motivado.', '11111111A', 'admin123', 'ACTIVO', '1DAM', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d'),
('vol002', 'Lucia', 'Martin', 'Perez', 'lucia@email.com', '600222222', '1999-05-20', 'Me gusta ayudar.', '22222222B', 'admin123', 'ACTIVO', '2DAM', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2'),
('vol003', 'Jorge', 'Ruiz', 'Lopez', 'jorge@email.com', '600333333', '2001-12-10', 'Busco practicas.', '33333333C', 'admin123', 'PENDIENTE', '1SMR', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d'),
('vol004', 'Ana', 'Blanco', 'Gil', 'ana@email.com', '600444444', '1998-07-30', NULL, '44444444D', 'admin123', 'SUSPENDIDO', '1AC', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80'),
('vol005', 'Miguel', 'Soto', 'Diaz', 'miguel@email.com', '600555555', '2002-03-25', 'Apasionado del deporte.', '55555555E', 'admin123', 'ACTIVO', '1ASIR', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e');

-- ACTIVIDAD
INSERT INTO ACTIVIDAD (nombre, duracion_sesion, fecha_inicio, fecha_fin, n_max_voluntarios, descripcion, estado, CODORG, ubicacion, imagen)
VALUES
('Recogida Alimentos Navidad', '04:00:00', '2025-12-20', '2025-12-22', 50, 'Campaña anual de navidad.', 'FINALIZADA', 'org002', 'Centro Comercial La Morea', 'https://images.unsplash.com/photo-1512406981147-3844a4962c4a'),
('Apoyo Escolar Primaria', '01:30:00', '2026-01-10', '2026-06-20', 10, 'Clases de refuerzo.', 'ACTIVO', 'org005', 'Colegio Publico San Jorge', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b'),
('Maraton Solidaria', '05:00:00', '2026-03-15', '2026-03-15', 100, 'Evento deportivo benefico.', 'PENDIENTE', 'org001', 'Vuelta del Castillo', 'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3'),
('Limpieza Rio Arga', '03:00:00', '2026-04-22', '2026-04-22', 30, 'Dia de la tierra.', 'EN_PROGRESO', 'org003', 'Paseo del Arga', 'https://images.unsplash.com/photo-1618477461853-5f8dd1219df0'),
('Charla Salud Mental', '02:00:00', '2025-11-15', '2025-11-15', 20, 'Charla concienciacion.', 'FINALIZADA', 'org004', 'Civivox Iturrama', 'https://images.unsplash.com/photo-1544367563-12123d8965cd'),
('Torneo Futbol Sala', '02:00:00', '2026-05-10', '2026-05-12', 40, 'Torneo inter-escolar.', 'ACTIVO', 'org005', 'Polideportivo Arrosadia', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6'),
('Visita Residencia', '01:30:00', '2026-02-14', '2026-02-14', 15, 'Acompañamiento mayores.', 'SUSPENDIDA', 'org001', 'Residencia La Vaguada', 'https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4');

-- CREDENCIALES (Mapping Users)
INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES ('ORGANIZACION', 'cruzroja@email.com', 'admin123', 'org001');
INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES ('ORGANIZACION', 'banco@email.com', 'admin123', 'org002');
INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES ('ORGANIZACION', 'greenpeace@email.com', 'admin123', 'org003');
INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES ('ORGANIZACION', 'sanjuan@email.com', 'admin123', 'org004');
INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES ('ORGANIZACION', 'ilusion@email.com', 'admin123', 'org005');

INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES ('VOLUNTARIO', 'pedro@email.com', 'admin123', 'vol001');
INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES ('VOLUNTARIO', 'lucia@email.com', 'admin123', 'vol002');
INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES ('VOLUNTARIO', 'jorge@email.com', 'admin123', 'vol003');
INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES ('VOLUNTARIO', 'ana@email.com', 'admin123', 'vol004');
INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES ('VOLUNTARIO', 'miguel@email.com', 'admin123', 'vol005');

-- RELACIONES (Participation, Preferences, ODS)
-- Vol 1 & 2 in Act 1
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (1, 'vol001');
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (1, 'vol002');
-- Vol 5 in Act 2
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (2, 'vol005');

-- Availability
INSERT INTO DISPONIBILIDAD (dia, hora, CODVOL) VALUES ('LUNES', '16:00', 'vol001');
INSERT INTO DISPONIBILIDAD (dia, hora, CODVOL) VALUES ('VIERNES', '10:00', 'vol002');

-- ODS Practices

-- =============================================
-- 6. INSERT ADDITIONAL DATA (COMPREHENSIVE TEST SET)
-- =============================================

-- NEW ADMIN
INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, password, avatar) 
VALUES ('adm002', 'Super', 'Admin', 'super@voluntariado.com', '600999999', 'admin123', 'https://avatar.iran.liara.run/public/job/police/male');
INSERT INTO CREDENCIALES (user_type, correo, password, CODADMIN)
VALUES ('ADMINISTRADOR', 'super@voluntariado.com', 'admin123', 'adm002');

-- NEW ORGANIZATIONS (org006 - org015)
INSERT INTO ORGANIZACION (codorg, nombre, tipo_org, correo, telefono, sector, ambito, password, persona_contacto, descripcion, estado, avatar) VALUES 
('org006', 'Médicos Sin Fronteras', 'ONG', 'msf@email.com', '948666666', 'Salud', 'INTERNACIONAL', 'admin123', 'Dr. House', 'Asistencia médica en zonas de conflicto.', 'ACTIVO', 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528'),
('org007', 'WWF', 'ONG', 'wwf@email.com', '948777777', 'Ambiental', 'INTERNACIONAL', 'admin123', 'Panda Bear', 'Conservación de la naturaleza.', 'ACTIVO', 'https://images.unsplash.com/photo-1535083252457-6080fe29be45'),
('org008', 'Cáritas', 'ONG', 'caritas@email.com', '948888888', 'Social', 'NACIONAL', 'admin123', 'Padre Ángel', 'Ayuda a los desfavorecidos.', 'ACTIVO', 'https://images.unsplash.com/photo-1593113598332-cd288d649433'),
('org009', 'Asociación de Vecinos', 'ASOCIACIÓN', 'vecinos@email.com', '948999999', 'Social', 'LOCAL', 'admin123', 'Paco El Vecino', 'Mejora del barrio.', 'PENDIENTE', 'https://images.unsplash.com/photo-1511632765486-a01980e01a18'),
('org010', 'Club Deportivo', 'ASOCIACIÓN', 'deporte@email.com', '948000000', 'Deportivo', 'LOCAL', 'admin123', 'Entrenador Joe', 'Fomento del deporte base.', 'ACTIVO', 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211'),
('org011', 'Protectora Animales', 'ONG', 'animales@email.com', '948121212', 'Ambiental', 'REGIONAL', 'admin123', 'Frank Cuesta', 'Cuidado de animales abandonados.', 'ACTIVO', 'https://images.unsplash.com/photo-1450779209076-0920eb596823'),
('org012', 'Fundación Ciencia', 'FUNDACIÓN', 'ciencia@email.com', '948131313', 'Tecnológico', 'NACIONAL', 'admin123', 'Marie Curie', 'Divulgación científica.', 'ACTIVO', 'https://images.unsplash.com/photo-1507413245164-6160d8298b31'),
('org013', 'Teatro Amateur', 'ASOCIACIÓN', 'teatro@email.com', '948141414', 'Cultural', 'LOCAL', 'admin123', 'Shakespeare', 'Obras de teatro locales.', 'SUSPENDIDO', 'https://images.unsplash.com/photo-1503095392269-271ff41cd0be'),
('org014', 'Cruz Verde', 'ONG', 'cruzverde@email.com', '948151515', 'Salud', 'REGIONAL', 'admin123', 'Luigi', 'Apoyo sanitario rural.', 'PENDIENTE', 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7'),
('org015', 'Hackers for Good', 'ASOCIACIÓN', 'hackers@email.com', '948161616', 'Tecnológico', 'INTERNACIONAL', 'admin123', 'Neo', 'Ciberseguridad ética.', 'ACTIVO', 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5');

INSERT INTO CREDENCIALES (user_type, correo, password, CODORG) VALUES 
('ORGANIZACION', 'msf@email.com', 'admin123', 'org006'),
('ORGANIZACION', 'wwf@email.com', 'admin123', 'org007'),
('ORGANIZACION', 'caritas@email.com', 'admin123', 'org008'),
('ORGANIZACION', 'vecinos@email.com', 'admin123', 'org009'),
('ORGANIZACION', 'deporte@email.com', 'admin123', 'org010'),
('ORGANIZACION', 'animales@email.com', 'admin123', 'org011'),
('ORGANIZACION', 'ciencia@email.com', 'admin123', 'org012'),
('ORGANIZACION', 'teatro@email.com', 'admin123', 'org013'),
('ORGANIZACION', 'cruzverde@email.com', 'admin123', 'org014'),
('ORGANIZACION', 'hackers@email.com', 'admin123', 'org015');

-- NEW VOLUNTEERS (vol006 - vol020)
INSERT INTO VOLUNTARIO (codvol, nombre, apellido1, apellido2, correo, telefono, fecha_nacimiento, descripcion, dni, password, estado, CODCICLO, avatar) VALUES
('vol006', 'Sofia', 'López', 'García', 'sofia@email.com', '600666666', '2001-02-02', 'Estudiante de enfermería.', '66666666F', 'admin123', 'ACTIVO', '1AAG', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330'),
('vol007', 'David', 'Fernandez', 'Ruiz', 'david@email.com', '600777777', '2000-03-03', 'Amante de la naturaleza.', '77777777G', 'admin123', 'ACTIVO', '2DAM', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e'),
('vol008', 'Elena', 'Sanchez', 'Mora', 'elena@email.com', '600888888', '1999-04-04', 'Creativa y social.', '88888888H', 'admin123', 'PENDIENTE', '1GA', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80'),
('vol009', 'Pablo', 'Diaz', 'Cano', 'pablo@email.com', '600999000', '1998-05-05', 'Deportista.', '99999999I', 'admin123', 'ACTIVO', '2SMR', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde'),
('vol010', 'Carmen', 'Torres', 'Vila', 'carmen@email.com', '600101010', '2002-06-06', 'Voluntariada en sangre.', '00000000J', 'admin123', 'ACTIVO', '1af', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2'),
('vol011', 'Luis', 'Ramirez', 'Paz', 'luis@email.com', '600121212', '2001-07-07', NULL, '12121212K', 'admin123', 'SUSPENDIDO', '2AF', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d'),
('vol012', 'Raquel', 'Molina', 'Sol', 'raquel@email.com', '600131313', '2000-08-08', 'Me gusta programar.', '13131313L', 'admin123', 'ACTIVO', '1DAM', 'https://images.unsplash.com/photo-1554151228-14d9def656ec'),
('vol013', 'Victor', 'Gil', 'Luz', 'victor@email.com', '600141414', '1999-09-09', NULL, '14141414M', 'admin123', 'PENDIENTE', '2DAM', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12'),
('vol014', 'Nuria', 'Vazquez', 'Rey', 'nuria@email.com', '600151515', '1998-10-10', 'Administrativa.', '15151515N', 'admin123', 'ACTIVO', '1GA', 'https://images.unsplash.com/photo-1542206395-9feb3edaa68d'),
('vol015', 'Alberto', 'Serrano', 'Mar', 'alberto@email.com', '600161616', '2002-11-11', 'Marketing digital.', '16161616O', 'admin123', 'ACTIVO', '1AC', 'https://images.unsplash.com/photo-1531427186611-ecfd6d936c79'),
('vol016', 'Sara', 'Crespo', 'Rio', 'sara@email.com', '600171717', '2001-12-12', 'Ventas.', '17171717P', 'admin123', 'ACTIVO', '2GV', 'https://images.unsplash.com/photo-1580489944761-15a19d654956'),
('vol017', 'Javier', 'Ibañez', 'Ola', 'javier@email.com', '600181818', '2000-01-01', 'Tecnologia.', '18181818Q', 'admin123', 'PENDIENTE', '1ASIR', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d'),
('vol018', 'Marina', 'Pascual', 'Flor', 'marina@email.com', '600191919', '1999-02-02', NULL, '19191919R', 'admin123', 'ACTIVO', '2TL', 'https://images.unsplash.com/photo-1599566150163-29194dcaad36'),
('vol019', 'Roberto', 'Leon', 'Sol', 'roberto@email.com', '600202020', '1998-03-03', 'Logistica.', '20202020S', 'admin123', 'SUSPENDIDO', '1TL', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7'),
('vol020', 'Cristina', 'Garrido', 'Luz', 'cristina@email.com', '600212121', '2002-04-04', 'Comercio.', '21212121T', 'admin123', 'ACTIVO', '1CI', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb');

INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL) VALUES
('VOLUNTARIO', 'sofia@email.com', 'admin123', 'vol006'),
('VOLUNTARIO', 'david@email.com', 'admin123', 'vol007'),
('VOLUNTARIO', 'elena@email.com', 'admin123', 'vol008'),
('VOLUNTARIO', 'pablo@email.com', 'admin123', 'vol009'),
('VOLUNTARIO', 'carmen@email.com', 'admin123', 'vol010'),
('VOLUNTARIO', 'luis@email.com', 'admin123', 'vol011'),
('VOLUNTARIO', 'raquel@email.com', 'admin123', 'vol012'),
('VOLUNTARIO', 'victor@email.com', 'admin123', 'vol013'),
('VOLUNTARIO', 'nuria@email.com', 'admin123', 'vol014'),
('VOLUNTARIO', 'alberto@email.com', 'admin123', 'vol015'),
('VOLUNTARIO', 'sara@email.com', 'admin123', 'vol016'),
('VOLUNTARIO', 'javier@email.com', 'admin123', 'vol017'),
('VOLUNTARIO', 'marina@email.com', 'admin123', 'vol018'),
('VOLUNTARIO', 'roberto@email.com', 'admin123', 'vol019'),
('VOLUNTARIO', 'cristina@email.com', 'admin123', 'vol020');

-- NEW ACTIVITIES
INSERT INTO ACTIVIDAD (nombre, duracion_sesion, fecha_inicio, fecha_fin, n_max_voluntarios, descripcion, estado, CODORG, ubicacion, imagen) VALUES
('Campaña Vacunación', '08:00:00', '2026-06-01', '2026-06-15', 20, 'Apoyo logistico.', 'PENDIENTE', 'org006', 'Pamplona', 'https://images.unsplash.com/photo-1632053000632-4d2c478a0448'),
('Reforestación Bosque', '05:00:00', '2026-03-21', '2026-03-21', 100, 'Plantar arboles.', 'ACTIVO', 'org007', 'Sierra de Urbasa', 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09'),
('Comedor Social Fin de Semana', '03:00:00', '2026-01-01', '2026-12-31', 5, 'Servir comidas.', 'ACTIVO', 'org008', 'Pamplona', 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c'),
('Fiesta del Barrio', '06:00:00', '2026-07-10', '2026-07-12', 30, 'Organizacion eventos.', 'PENDIENTE', 'org009', 'Rochapea', 'https://images.unsplash.com/photo-1530103862676-de3c9da59af7'),
('Entrenamiento Inclusivo', '01:30:00', '2026-02-01', '2026-05-30', 4, 'Ayuda entrenadores.', 'ACTIVO', 'org010', 'Polideportivo Arrosadia', 'https://images.unsplash.com/photo-1518611012118-696072aa579a'),
('Paseo Perros Refugio', '02:00:00', '2026-01-20', '2026-12-20', 10, 'Pasear perros.', 'ACTIVO', 'org011', 'Mutilva', 'https://images.unsplash.com/photo-1625316708582-7c38734be31d'),
('Taller Robotica Niños', '02:00:00', '2025-10-01', '2025-10-30', 8, 'Enseñar robotica basica.', 'FINALIZADA', 'org012', 'Civivox', 'https://images.unsplash.com/photo-1589254065878-42c9da9e2ab2'),
('Montaje Escenario', '04:00:00', '2026-04-10', '2026-04-10', 10, 'Montar escenario obra.', 'SUSPENDIDA', 'org013', 'Barañain', 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30'),
('Revisión Médica Rural', '05:00:00', '2026-08-05', '2026-08-07', 6, 'Acompañamiento rutas.', 'PENDIENTE', 'org014', 'Zona Norte', 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528'),
('Auditoria Seguridad', '03:00:00', '2026-09-01', '2026-09-30', 5, 'Analisis redes ONGs.', 'ACTIVO', 'org015', 'Online', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b'),
('Recogida Juguetes', '03:00:00', '2025-12-01', '2025-12-15', 20, 'Navidad para todos.', 'FINALIZADA', 'org001', 'Pamplona', 'https://images.unsplash.com/photo-1512353087810-25dfcd100962'),
('Apoyo Digital Mayores', '01:30:00', '2026-01-15', '2026-06-15', 5, 'Enseñar uso movil.', 'ACTIVO', 'org015', 'Residencia', 'https://images.unsplash.com/photo-1485981133625-f1a0918f3075'),
('Torneo Baloncesto', '04:00:00', '2026-05-20', '2026-05-22', 15, 'Arbitraje y mesa.', 'PENDIENTE', 'org010', 'Pamplona', 'https://images.unsplash.com/photo-1505666287802-931dc83948e9'),
('Limpieza Playa', '04:00:00', '2026-06-05', '2026-06-05', 50, 'Dia oceanos.', 'PENDIENTE', 'org003', 'San Sebastian', 'https://images.unsplash.com/photo-1621451537084-482c73073a0f'),
('Clases Español', '01:30:00', '2026-02-01', '2026-06-30', 3, 'Para inmigrantes.', 'ACTIVO', 'org001', 'Pamplona', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40');

-- NEW REQUESTS (SOLICITUDES)
-- Linking existing/new volunteers to activities
INSERT INTO SOLICITUD (CODVOL, CODACT, status, fecha_solicitud, mensaje) VALUES
('vol006', 2, 'PENDIENTE', '2026-01-10 10:00:00', 'Me interesa mucho.'),
('vol007', 2, 'ACEPTADA', '2026-01-05 09:00:00', 'Tengo experiencia.'),
('vol008', 3, 'PENDIENTE', '2026-01-18 11:30:00', 'Quiero ayudar.'),
('vol009', 5, 'RECHAZADA', '2026-01-15 12:00:00', 'No puedo ir.'),
('vol010', 6, 'ACEPTADA', '2026-01-14 16:00:00', 'Me encantan los perros.'),
('vol011', 8, 'PENDIENTE', '2026-01-19 10:00:00', 'Soy fuerte.'),
('vol012', 10, 'ACEPTADA', '2026-01-12 09:15:00', 'Estudio ASIR.'),
('vol013', 10, 'PENDIENTE', '2026-01-18 18:00:00', 'Me gusta la seguridad.'),
('vol001', 5, 'PENDIENTE', '2026-01-20 08:30:00', 'Tengo libre.'),
('vol002', 6, 'ACEPTADA', '2026-01-10 17:00:00', 'Tengo perro.');

-- MORE JUNCTION DATA
-- Preferences
INSERT INTO VOL_PREFIERE_TACT (CODVOL, CODTIPO) VALUES ('vol006', 2), ('vol006', 6);
INSERT INTO VOL_PREFIERE_TACT (CODVOL, CODTIPO) VALUES ('vol007', 4), ('vol007', 5);
INSERT INTO VOL_PREFIERE_TACT (CODVOL, CODTIPO) VALUES ('vol012', 1), ('vol012', 8);

-- Availability
INSERT INTO DISPONIBILIDAD (dia, hora, CODVOL) VALUES ('MARTES', '17:00', 'vol006');
INSERT INTO DISPONIBILIDAD (dia, hora, CODVOL) VALUES ('SABADO', '10:00', 'vol007');

-- Participation (Accepted requests assumed participations)
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (2, 'vol007');
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (6, 'vol010');
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (10, 'vol012');
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES (6, 'vol002');

-- Activity Types
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (2, 4); -- Reforestacion -> Ambiental
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (2, 6); -- Reforestacion -> Social
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (5, 5); -- Entrenamiento -> Deportivo
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (7, 5); -- Robotica -> Educativo
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (7, 1); -- Robotica -> Digital
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (10, 1); -- Auditoria -> Digital
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (10, 8); -- Auditoria -> Tecnico

-- Activity ODS
INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (2, 13); -- Accion por el clima
INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (2, 15); -- Vida ecosistemas
INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (3, 2); -- Hambre cero
INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (5, 3); -- Salud
INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (5, 10); -- Reduccion desigualdades


-- =============================================
-- 7. INSERT MASSIVE JUNCTION DATA
-- =============================================

-- VOL_PARTICIPA_ACT (Volunteers in Activities)
INSERT INTO VOL_PARTICIPA_ACT (CODACT, CODVOL) VALUES 
(1, 'vol003'), (1, 'vol004'), (1, 'vol005'), (1, 'vol008'),
(3, 'vol001'), (3, 'vol002'), (3, 'vol006'), (3, 'vol009'), (3, 'vol015'),
(4, 'vol003'), (4, 'vol007'), (4, 'vol011'), (4, 'vol012'), (4, 'vol018'),
(5, 'vol004'), (5, 'vol008'), (5, 'vol013'), (5, 'vol016'),
(7, 'vol001'), (7, 'vol005'), (7, 'vol010'), (7, 'vol014'), (7, 'vol019'),
(8, 'vol002'), (8, 'vol006'), (8, 'vol009'), (8, 'vol017'), (8, 'vol020'),
(9, 'vol003'), (9, 'vol007'), (9, 'vol011'), (9, 'vol015'),
(11, 'vol004'), (11, 'vol008'), (11, 'vol012'), (11, 'vol016'), (11, 'vol018'),
(12, 'vol005'), (12, 'vol010'), (12, 'vol013'), (12, 'vol019'),
(13, 'vol001'), (13, 'vol006'), (13, 'vol009'), (13, 'vol014'), (13, 'vol017'), (13, 'vol020'),
(14, 'vol002'), (14, 'vol007'), (14, 'vol011'), (14, 'vol015'),
(15, 'vol003'), (15, 'vol008'), (15, 'vol012'), (15, 'vol016'), (15, 'vol018');

-- VOL_PREFIERE_TACT (Volunteer Preferences)
INSERT INTO VOL_PREFIERE_TACT (CODVOL, CODTIPO) VALUES
('vol001', 1), ('vol001', 3), ('vol002', 2), ('vol002', 4),
('vol003', 5), ('vol003', 6), ('vol004', 7), ('vol004', 8),
('vol005', 1), ('vol005', 5), ('vol008', 2), ('vol008', 6),
('vol009', 3), ('vol009', 7), ('vol010', 4), ('vol010', 8),
('vol011', 1), ('vol011', 2), ('vol013', 3), ('vol013', 4),
('vol014', 5), ('vol014', 6), ('vol015', 7), ('vol015', 8),
('vol016', 1), ('vol016', 4), ('vol017', 2), ('vol017', 5),
('vol018', 3), ('vol018', 6), ('vol019', 4), ('vol019', 7),
('vol020', 5), ('vol020', 8);

-- ACT_ASOCIADO_TACT (Activity Categories)
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES
(1, 6), (1, 3), -- Campaña Vacunacion: Social, Educativo
(3, 6), (3, 2), -- Comedor Social: Social, Salud
(4, 6), (4, 7), -- Fiesta Barrio: Social, Cultural
(6, 4), (6, 6), -- Paseo Perros: Ambiental, Social
(8, 7), (8, 8), -- Montaje Escenario: Cultural, Tecnico
(9, 2), (9, 6), -- Revision Medica: Salud, Social
(11, 6), (11, 7), -- Juguetes: Social, Cultural
(12, 1), (12, 3), -- Apoyo Digital: Digital, Educativo
(13, 5), (13, 3), -- Baloncesto: Deportivo, Educativo
(14, 4), (14, 6), -- Playa: Ambiental, Social
(15, 3), (15, 7); -- Clases Español: Educativo, Cultural

-- SOLICITUD (More Requests)
INSERT INTO SOLICITUD (CODVOL, CODACT, status, fecha_solicitud, mensaje) VALUES
('vol003', 4, 'PENDIENTE', '2026-02-01 10:00:00', 'Quiero participar'),
('vol004', 4, 'ACEPTADA', '2026-01-25 09:30:00', 'Soy vecino'),
('vol005', 4, 'RECHAZADA', '2026-01-26 11:00:00', 'No cumples perfil'),
('vol001', 7, 'ACEPTADA', '2026-01-20 15:00:00', 'Me gusta la robotica'),
('vol002', 7, 'PENDIENTE', '2026-01-21 16:30:00', 'Tengo experiencia'),
('vol008', 9, 'PENDIENTE', '2026-03-01 09:00:00', 'Soy enfermera'),
('vol009', 9, 'ACEPTADA', '2026-03-02 10:00:00', 'Ayudo en logistica'),
('vol011', 11, 'PENDIENTE', '2026-05-10 12:00:00', 'Tengo coche'),
('vol012', 11, 'RECHAZADA', '2026-05-11 13:00:00', 'Ya esta lleno'),
('vol015', 13, 'ACEPTADA', '2026-04-01 17:00:00', 'Soy arbitro'),
('vol016', 13, 'PENDIENTE', '2026-04-02 18:00:00', 'Puedo llevar agua'),
('vol018', 14, 'PENDIENTE', '2026-05-20 08:00:00', 'Me gusta el mar'),
('vol019', 14, 'ACEPTADA', '2026-05-21 09:00:00', 'Voy con amigos'),
('vol020', 15, 'PENDIENTE', '2026-01-25 19:00:00', 'Hablo ingles tambien');

GO
