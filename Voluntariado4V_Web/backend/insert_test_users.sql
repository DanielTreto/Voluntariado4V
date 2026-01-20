USE VOLUNTARIADOBD;
GO
SET ANSI_NULLS ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET QUOTED_IDENTIFIER ON;
SET NUMERIC_ROUNDABORT OFF;
GO

-- ============================================
-- 1. INSERTAR CICLOS
-- ============================================
IF NOT EXISTS (SELECT 1 FROM CICLO WHERE CODCICLO = 'DAM')
BEGIN
    INSERT INTO CICLO (CODCICLO, NOMBRE, CURSO) VALUES ('DAM', 'Desarrollo de Aplicaciones Multiplataforma', 2);
END

IF NOT EXISTS (SELECT 1 FROM CICLO WHERE CODCICLO = 'DAW')
BEGIN
    INSERT INTO CICLO (CODCICLO, NOMBRE, CURSO) VALUES ('DAW', 'Desarrollo de Aplicaciones Web', 2);
END

-- ============================================
-- 2. INSERTAR ADMINISTRADORES
-- ============================================
IF NOT EXISTS (SELECT 1 FROM ADMINISTRADOR WHERE correo = 'admin@example.com')
BEGIN
    INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, firebase_uid, AVATAR)
    VALUES ('adm001', 'Carlos', 'Administrador García', 'admin@example.com', '600111222', NULL, NULL);
END

IF NOT EXISTS (SELECT 1 FROM ADMINISTRADOR WHERE correo = 'admin2@example.com')
BEGIN
    INSERT INTO ADMINISTRADOR (id, nombre, apellidos, correo, telefono, firebase_uid, AVATAR)
    VALUES ('adm002', 'María', 'Admin López', 'admin2@example.com', '600333444', NULL, NULL);
END

-- ============================================
-- 3. INSERTAR ORGANIZACIONES
-- ============================================
IF NOT EXISTS (SELECT 1 FROM ORGANIZACION WHERE CORREO = 'org@gmail.com')
BEGIN
    INSERT INTO ORGANIZACION (CODORG, NOMBRE, TIPO_ORG, CORREO, TELEFONO, SECTOR, AMBITO, firebase_uid, PERSONA_CONTACTO, DESCRIPCION, ESTADO, AVATAR, DIRECCION, WEB)
    VALUES ('org001', 'Cruz Roja Española', 'ONG', 'org@gmail.com', '912345678', 'SOCIAL', 'NACIONAL', NULL, 'Juan Pérez', 'Organización humanitaria', 'ACTIVO', NULL, 'Calle Mayor 1', 'https://www.cruzroja.es');
END

IF NOT EXISTS (SELECT 1 FROM ORGANIZACION WHERE CORREO = 'org2@gmail.com')
BEGIN
    INSERT INTO ORGANIZACION (CODORG, NOMBRE, TIPO_ORG, CORREO, TELEFONO, SECTOR, AMBITO, firebase_uid, PERSONA_CONTACTO, DESCRIPCION, ESTADO, AVATAR, DIRECCION, WEB)
    VALUES ('org002', 'Greenpeace España', 'ONG', 'org2@gmail.com', '987654321', 'AMBIENTAL', 'INTERNACIONAL', NULL, 'Ana Martínez', 'Organización ecologista', 'ACTIVO', NULL, 'Calle San Bernardo 107', 'https://www.greenpeace.org/es');
END

-- ============================================
-- 4. INSERTAR VOLUNTARIOS
-- ============================================
IF NOT EXISTS (SELECT 1 FROM VOLUNTARIO WHERE CORREO = 'testVoluntario@gmail.com')
BEGIN
    INSERT INTO VOLUNTARIO (CODVOL, NOMBRE, APELLIDO1, APELLIDO2, CORREO, TELEFONO, FECHA_NACIMIENTO, DESCRIPCION, CODCICLO, DNI, firebase_uid, ESTADO, AVATAR)
    VALUES ('vol001', 'Pedro', 'Voluntario', 'Sánchez', 'testVoluntario@gmail.com', '655111222', '2000-05-15', 'Estudiante de DAM', 'DAM', '12345678A', NULL, 'ACTIVO', NULL);
END

IF NOT EXISTS (SELECT 1 FROM VOLUNTARIO WHERE CORREO = 'voluntario2@gmail.com')
BEGIN
    INSERT INTO VOLUNTARIO (CODVOL, NOMBRE, APELLIDO1, APELLIDO2, CORREO, TELEFONO, FECHA_NACIMIENTO, DESCRIPCION, CODCICLO, DNI, firebase_uid, ESTADO, AVATAR)
    VALUES ('vol002', 'Laura', 'González', 'Ruiz', 'voluntario2@gmail.com', '666222333', '2001-08-20', 'Estudiante de DAW', 'DAW', '87654321B', NULL, 'ACTIVO', NULL);
END

-- ============================================
-- 5. INSERTAR CREDENCIALES
-- ============================================
IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'admin@example.com')
BEGIN
    INSERT INTO CREDENCIALES (user_type, correo, password)
    VALUES ('ADMIN', 'admin@example.com', 'admin');
END

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'admin2@example.com')
BEGIN
    INSERT INTO CREDENCIALES (user_type, correo, password)
    VALUES ('ADMIN', 'admin2@example.com', 'admin123');
END

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'org@gmail.com')
BEGIN
    INSERT INTO CREDENCIALES (user_type, correo, password, CODORG)
    VALUES ('ORGANIZACION', 'org@gmail.com', '123456', 'org001');
END

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'org2@gmail.com')
BEGIN
    INSERT INTO CREDENCIALES (user_type, correo, password, CODORG)
    VALUES ('ORGANIZACION', 'org2@gmail.com', 'org123', 'org002');
END

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'testVoluntario@gmail.com')
BEGIN
    INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL)
    VALUES ('VOLUNTARIO', 'testVoluntario@gmail.com', '1234', 'vol001');
END

IF NOT EXISTS (SELECT 1 FROM CREDENCIALES WHERE correo = 'voluntario2@gmail.com')
BEGIN
    INSERT INTO CREDENCIALES (user_type, correo, password, CODVOL)
    VALUES ('VOLUNTARIO', 'voluntario2@gmail.com', 'vol123', 'vol002');
END

GO

PRINT '¡Usuarios insertados correctamente!';
