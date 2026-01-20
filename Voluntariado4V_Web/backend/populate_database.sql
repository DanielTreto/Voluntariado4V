USE VOLUNTARIADOBD;
GO

-- Configuración inicial
SET ANSI_NULLS ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET QUOTED_IDENTIFIER ON;
SET NUMERIC_ROUNDABORT OFF;
SET NOCOUNT ON;
GO

PRINT 'Iniciando población de datos...';

-- ============================================
-- 1. TABLAS MAESTRAS / CATÁLOGOS
-- ============================================

-- 1.1 CICLOS
PRINT 'Insertando Ciclos...';
IF NOT EXISTS (SELECT 1 FROM CICLO WHERE CODCICLO = 'DAM')
    INSERT INTO CICLO (CODCICLO, NOMBRE, CURSO) VALUES ('DAM', 'Desarrollo de Aplicaciones Multiplataforma', 2);
IF NOT EXISTS (SELECT 1 FROM CICLO WHERE CODCICLO = 'DAW')
    INSERT INTO CICLO (CODCICLO, NOMBRE, CURSO) VALUES ('DAW', 'Desarrollo de Aplicaciones Web', 2);

-- 1.2 ODS (Objetivos de Desarrollo Sostenible)
PRINT 'Insertando ODS...';
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 1) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (1, 'Fin de la pobreza');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 2) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (2, 'Hambre cero');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 3) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (3, 'Salud y bienestar');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 4) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (4, 'Educacion de calidad');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 5) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (5, 'Igualdad de genero');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 6) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (6, 'Agua limpia y saneamiento');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 7) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (7, 'Energia asequible y no contaminante');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 8) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (8, 'Trabajo decente y crecimiento economico');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 9) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (9, 'Industria, innovacion e infraestructura');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 10) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (10, 'Reduccion de las desigualdades');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 11) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (11, 'Ciudades y comunidades sostenibles');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 12) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (12, 'Produccion y consumo responsables');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 13) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (13, 'Accion por el clima');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 14) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (14, 'Vida submarina');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 15) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (15, 'Vida de ecosistemas terrestres');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 16) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (16, 'Paz, justicia e instituciones solidas');
IF NOT EXISTS (SELECT 1 FROM ODS WHERE NUMODS = 17) INSERT INTO ODS (NUMODS, DESCRIPCION) VALUES (17, 'Alianzas para lograr los objetivos');

-- 1.3 TIPOS DE ACTIVIDAD
PRINT 'Insertando Tipos de Actividad...';
SET IDENTITY_INSERT TIPO_ACTIVIDAD ON;
IF NOT EXISTS (SELECT 1 FROM TIPO_ACTIVIDAD WHERE CODTIPO = 1) INSERT INTO TIPO_ACTIVIDAD (CODTIPO, DESCRIPCION) VALUES (1, 'Acompañamiento'); -- ñ fits, user said tildes
IF NOT EXISTS (SELECT 1 FROM TIPO_ACTIVIDAD WHERE CODTIPO = 1) INSERT INTO TIPO_ACTIVIDAD (CODTIPO, DESCRIPCION) VALUES (1, 'Acompanamiento');
IF NOT EXISTS (SELECT 1 FROM TIPO_ACTIVIDAD WHERE CODTIPO = 2) INSERT INTO TIPO_ACTIVIDAD (CODTIPO, DESCRIPCION) VALUES (2, 'Educativo');
IF NOT EXISTS (SELECT 1 FROM TIPO_ACTIVIDAD WHERE CODTIPO = 3) INSERT INTO TIPO_ACTIVIDAD (CODTIPO, DESCRIPCION) VALUES (3, 'Medioambiental');
IF NOT EXISTS (SELECT 1 FROM TIPO_ACTIVIDAD WHERE CODTIPO = 4) INSERT INTO TIPO_ACTIVIDAD (CODTIPO, DESCRIPCION) VALUES (4, 'Social');
SET IDENTITY_INSERT TIPO_ACTIVIDAD OFF;

-- ============================================
-- 2. USUARIOS (Admin, Org, Vol)
-- ============================================

-- 2.1 ADMINISTRADORES
PRINT 'Insertando Administradores...';
IF NOT EXISTS (SELECT 1 FROM ADMINISTRADOR WHERE correo = 'admin@example.com')
    INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, firebase_uid, AVATAR)
    VALUES ('adm001', 'Carlos', 'Administrador Garcia', 'admin@example.com', '600111222', NULL, NULL);

IF NOT EXISTS (SELECT 1 FROM ADMINISTRADOR WHERE correo = 'admin2@example.com')
    INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, firebase_uid, AVATAR)
    VALUES ('adm002', 'Maria', 'Admin Lopez', 'admin2@example.com', '600333444', NULL, NULL);

-- 2.2 ORGANIZACIONES
PRINT 'Insertando Organizaciones...';
IF NOT EXISTS (SELECT 1 FROM ORGANIZACION WHERE CORREO = 'org@gmail.com')
    INSERT INTO ORGANIZACION (CODORG, NOMBRE, TIPO_ORG, CORREO, TELEFONO, SECTOR, AMBITO, firebase_uid, PERSONA_CONTACTO, DESCRIPCION, ESTADO, AVATAR, DIRECCION, WEB)
    VALUES ('org001', 'Cruz Roja Española', 'ONG', 'org@gmail.com', '912345678', 'SOCIAL', 'NACIONAL', NULL, 'Juan Perez', 'Organizacion humanitaria', 'ACTIVO', NULL, 'Calle Mayor 1', 'https://www.cruzroja.es');

IF NOT EXISTS (SELECT 1 FROM ORGANIZACION WHERE CORREO = 'org2@gmail.com')
    INSERT INTO ORGANIZACION (CODORG, NOMBRE, TIPO_ORG, CORREO, TELEFONO, SECTOR, AMBITO, firebase_uid, PERSONA_CONTACTO, DESCRIPCION, ESTADO, AVATAR, DIRECCION, WEB)
    VALUES ('org002', 'Greenpeace España', 'ONG', 'org2@gmail.com', '987654321', 'AMBIENTAL', 'INTERNACIONAL', NULL, 'Ana Martinez', 'Organizacion ecologista', 'ACTIVO', NULL, 'Calle San Bernardo 107', 'https://www.greenpeace.org/es');

-- 2.3 VOLUNTARIOS
PRINT 'Insertando Voluntarios...';
IF NOT EXISTS (SELECT 1 FROM VOLUNTARIO WHERE CORREO = 'testVoluntario@gmail.com')
    INSERT INTO VOLUNTARIO (CODVOL, NOMBRE, APELLIDO1, APELLIDO2, CORREO, TELEFONO, FECHA_NACIMIENTO, DESCRIPCION, CODCICLO, DNI, firebase_uid, ESTADO, AVATAR)
    VALUES ('vol001', 'Pedro', 'Voluntario', 'Sanchez', 'testVoluntario@gmail.com', '655111222', '2000-05-15', 'Estudiante de DAM', 'DAM', '12345678A', NULL, 'ACTIVO', NULL);

IF NOT EXISTS (SELECT 1 FROM VOLUNTARIO WHERE CORREO = 'voluntario2@gmail.com')
    INSERT INTO VOLUNTARIO (CODVOL, NOMBRE, APELLIDO1, APELLIDO2, CORREO, TELEFONO, FECHA_NACIMIENTO, DESCRIPCION, CODCICLO, DNI, firebase_uid, ESTADO, AVATAR)
    VALUES ('vol002', 'Laura', 'Gonzalez', 'Ruiz', 'voluntario2@gmail.com', '666222333', '2001-08-20', 'Estudiante de DAW', 'DAW', '87654321B', NULL, 'ACTIVO', NULL);

-- ============================================
-- 3. CREDENCIALES (Login)
-- ============================================
PRINT 'Insertando Credenciales...';
-- Admin
IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'admin@example.com')
    INSERT INTO CREDENCIALES (user_type, correo, password, id_admin)
    VALUES ('ADMIN', 'admin@example.com', 'admin', 'adm001');
ELSE
    UPDATE CREDENCIALES SET id_admin = 'adm001' WHERE correo = 'admin@example.com' AND id_admin IS NULL;

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'admin2@example.com')
    INSERT INTO CREDENCIALES (user_type, correo, password, id_admin)
    VALUES ('ADMIN', 'admin2@example.com', 'admin123', 'adm002');
ELSE
    UPDATE CREDENCIALES SET id_admin = 'adm002' WHERE correo = 'admin2@example.com' AND id_admin IS NULL;

-- Organizacion
IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'org@gmail.com')
    INSERT INTO CREDENCIALES (user_type, correo, password, CODORG)
    VALUES ('ORGANIZACION', 'org@gmail.com', '123456', 'org001');

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'org2@gmail.com')
    INSERT INTO CREDENCIALES (user_type, correo, password, CODORG)
    VALUES ('ORGANIZACION', 'org2@gmail.com', 'org123', 'org002');

-- Voluntario
IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'testVoluntario@gmail.com')
    INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL)
    VALUES ('VOLUNTARIO', 'testVoluntario@gmail.com', '1234', 'vol001');

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'voluntario2@gmail.com')
    INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL)
    VALUES ('VOLUNTARIO', 'voluntario2@gmail.com', 'vol123', 'vol002');

-- ============================================
-- 4. ACTIVIDADES
-- ============================================
PRINT 'Insertando Actividades...';
SET IDENTITY_INSERT ACTIVIDAD ON;

-- Actividad 1: Recogida de Alimentos (Cruz Roja)
IF NOT EXISTS (SELECT 1 FROM ACTIVIDAD WHERE CODACT = 1)
BEGIN
    INSERT INTO ACTIVIDAD (CODACT, nombre, duracion_sesion, fecha_inicio, fecha_fin, n_max_voluntarios, CODORG, descripcion, estado, ubicacion, imagen)
    VALUES (1, 'Gran Recogida de Alimentos', '4 horas', '2026-06-01', '2026-06-05', 50, 'org001', 'Campaña anual para recolectar alimentos no perecederos.', 'EN_PROGRESO', 'Supermercados de Madrid', NULL);
    
    -- Relaciones (ODS y Tipo)
    INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (1, 2); -- Hambre cero
    INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (1, 4); -- Social
END

-- Actividad 2: Limpieza de Playa (Greenpeace)
IF NOT EXISTS (SELECT 1 FROM ACTIVIDAD WHERE CODACT = 2)
BEGIN
    INSERT INTO ACTIVIDAD (CODACT, nombre, duracion_sesion, fecha_inicio, fecha_fin, n_max_voluntarios, CODORG, descripcion, estado, ubicacion, imagen)
    VALUES (2, 'Limpieza de Playa Barceloneta', '3 horas', '2026-07-15', '2026-07-15', 30, 'org002', 'Jornada de limpieza de residuos plasticos.', 'PENDIENTE', 'Playa Barceloneta', NULL);
    
    -- Relaciones (ODS y Tipo)
    INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (2, 13); -- Acción por el clima
    INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS) VALUES (2, 3);  -- Salud y bienestar
    INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO) VALUES (2, 3); -- Medioambiental
END

SET IDENTITY_INSERT ACTIVIDAD OFF;

-- ============================================
-- 5. SOLICITUDES
-- ============================================
PRINT 'Insertando Solicitudes...';
SET IDENTITY_INSERT SOLICITUD ON;

-- Voluntario 1 --> Actividad 1
IF NOT EXISTS (SELECT 1 FROM SOLICITUD WHERE id = 1)
    INSERT INTO SOLICITUD (id, CODVOL, CODACT, status, fecha_solicitud, mensaje)
    VALUES (1, 'vol001', 1, 'PENDIENTE', '2026-05-20', 'Me gustaria mucho participar.');

SET IDENTITY_INSERT SOLICITUD OFF;

-- ============================================
-- 6. DISPONIBILIDAD
-- ============================================
PRINT 'Insertando Disponibilidad...';
IF NOT EXISTS (SELECT 1 FROM DISPONIBILIDAD WHERE CODVOL = 'vol001' AND DIA = 'LUNES')
    INSERT INTO DISPONIBILIDAD (CODVOL, DIA, NUM_HORAS) VALUES ('vol001', 'LUNES', 4);

IF NOT EXISTS (SELECT 1 FROM DISPONIBILIDAD WHERE CODVOL = 'vol001' AND DIA = 'JUEVES')
    INSERT INTO DISPONIBILIDAD (CODVOL, DIA, NUM_HORAS) VALUES ('vol001', 'JUEVES', 3);

PRINT '¡Población de base de datos completada exitosamente!';
