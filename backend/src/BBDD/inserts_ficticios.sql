/* =============================================================
   ARCHIVO 2: INSERCIÓN DE DATOS FICTICIOS 
   (ORGANIZACIONES, VOLUNTARIOS Y ACTIVIDADES)
   ============================================================= */

USE VOLUNTARIADOBD
GO

-- 1. INSERTAR DATOS EN TABLA ORGANIZACION (AHORA AQUÍ)
-- Al ser IDENTITY(1,1) y ser la primera carga, se generarán los IDs 1 al 5.
INSERT INTO ORGANIZACION (NOMBRE, TIPO_ORG, CORREO, TELEFONO, SECTOR, AMBITO, DESCRIPCION)
VALUES
('AMAVIR', 'FUNDACION', 'amavir@example.com', '600000001', 'SALUD', 'LOCAL', 'Organización dedicada a la salud de los mayores.'),
('ANA', 'ONG', 'ana@example.com', '600000002', 'SOCIAL', 'REGIONAL', 'ONG enfocada en la ayuda social.'),
('CUATROVIENTOS', 'ASOCIACION', 'cuatrocientos@example.com', '600000003', 'EDUCATIVO', 'NACIONAL', 'Asociación educativa.'),
('SOLERA', 'ENTIDAD PÚBLICA', 'solera@example.com', '600000004', 'AMBIENTAL', 'INTERNACIONAL', 'Entidad pública ambiental.'),
('UNZUTXIKI', 'OTRA', 'unzutxiki@example.com', '600000005', 'CULTURAL', 'LOCAL', 'Organización cultural local.');
GO

-- 2. INSERTAR DATOS EN TABLA VOLUNTARIO (READAPTADOS)
-- Se han añadido los campos DNI y ESTADO.
INSERT INTO VOLUNTARIO (NOMBRE, APELLIDO1, APELLIDO2, CORREO, TELEFONO, FECHA_NACIMIENTO, DESCRIPCION, CODCICLO, DNI, ESTADO)
VALUES
('Juan', 'Pérez', 'García', 'juan.perez@example.com', '600000006', '1995-05-20', 'Voluntario entusiasta.', '1SMR', '12345678A', 'ACTIVO'),
('María', 'López', 'Sánchez', 'maria.lopez@example.com', '600000007', '1998-07-15', 'Interesada en actividades sociales.', '2DAM', '87654321B', 'ACTIVO'),
('Carlos', 'Gómez', 'Martínez', 'carlos.gomez@example.com', '600000008', '1990-03-10', NULL, '1DAM', '11223344C', 'PENDIENTE'),
('Ana', 'Fernández', 'Ruiz', 'ana.fernandez@example.com', '600000009', '2000-01-01', 'Estudiante de marketing.', '1DAM', '44332211D', 'SUSPENDIDO'),
('Luis', 'Martín', 'Hernández', 'luis.martin@example.com', '600000010', '1992-11-25', 'Experiencia en educación.', '2SMR', '99887766E', 'ACTIVO');
GO

-- 3. INSERTAR DATOS EN TABLA ACTIVIDAD
INSERT INTO ACTIVIDAD (NOMBRE, DURACION_SESION, FECHA_INICIO, FECHA_FIN, N_MAX_VOLUNTARIOS, CODORG, DESCRIPCION)
VALUES
('ACTIVIDAD1', '02:00:00', '2023-10-01', '2023-10-02', 10, 1, 'Taller de salud para mayores'),
('ACTIVIDAD2','01:30:00', '2023-11-15', '2023-11-15', 5, 2, 'Reparto de alimentos'),
('ACTIVIDAD3','03:00:00', '2023-12-01', '2023-12-03', 20, 3, 'Jornada educativa'),
('ACTIVIDAD4','01:00:00', '2024-01-10', '2024-01-10', 15, 4, 'Limpieza de playa'),
('ACTIVIDAD5','02:30:00', '2024-02-20', '2024-02-21', 8, 5, 'Festival cultural');
GO

-- 4. INSERTAR DATOS RELACIONADOS

-- DISPONIBILIDAD
INSERT INTO DISPONIBILIDAD (CODVOL, DIA, HORA)
VALUES
(1, 'LUNES', '3-4'),
(1, 'MIÉRCOLES', '5-6'),
(2, 'MARTES', '4-5'),
(2, 'JUEVES', '6-7'),
(3, 'VIERNES', '7-8'),
(4, 'SÁBADO', '8-9'),
(5, 'DOMINGO', '9-10');

-- VOL_PARTICIPA_ACT
INSERT INTO VOL_PARTICIPA_ACT (CODVOL, CODACT)
VALUES
(1, 1),  -- Juan participa en Taller de salud
(2, 2),  -- María participa en Reparto de alimentos
(3, 3),  -- Carlos participa en Jornada educativa
(4, 4),  -- Ana participa en Limpieza de playa
(5, 5);  -- Luis participa en Festival cultural

-- VOL_PREFIERE_TACT
INSERT INTO VOL_PREFIERE_TACT (CODVOL, CODTIPO)
VALUES
(1, 1),  -- Juan prefiere Educativa
(2, 2),  -- María prefiere Social
(3, 3),  -- Carlos prefiere Ambiental
(4, 4),  -- Ana prefiere Cultural
(5, 5);  -- Luis prefiere Deportiva

-- ACT_PRACTICA_ODS
INSERT INTO ACT_PRACTICA_ODS (CODACT, NUMODS)
VALUES
(1, 3),  -- Taller de salud (Salud y bienestar)
(2, 2),  -- Reparto de alimentos (Hambre cero)
(3, 4),  -- Jornada educativa (Educación de calidad)
(4, 1),  -- Limpieza de playa (Fin de la pobreza)
(5, 5);  -- Festival cultural (Igualdad de género)

-- ACT_ASOCIADO_TACT
INSERT INTO ACT_ASOCIADO_TACT (CODACT, CODTIPO)
VALUES
(1, 1),  -- Taller de salud (Educativa)
(2, 2),  -- Reparto de alimentos (Social)
(3, 1),  -- Jornada educativa (Educativa)
(4, 3),  -- Limpieza de playa (Ambiental)
(5, 4);  -- Festival cultural (Cultural)