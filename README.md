# Voluntariado 4 Vientos - Sistema Integral de Gestión

Bienvenido al repositorio oficial del proyecto **Voluntariado 4 Vientos**. Este sistema permite la gestión integral de actividades de voluntariado, conectando a organizaciones con estudiantes y voluntarios.

El proyecto consta de dos componentes principales:

1.  **Plataforma Web**: Gestión administrativa y portal de usuario.
2.  **Aplicación Móvil**: App nativa para voluntarios.

---

## 🚀 Instalación y Ejecución Automática

Hemos automatizado la instalación del entorno para la versión Web para facilitar el despliegue.

### 1. Clonar el repositorio
```bash
git clone https://github.com/DanielTreto/Voluntariado4V.git
cd Voluntariado4V
```

### 2. Instalación (`install.bat`)
Ejecuta el archivo `install.bat` haciendo doble clic o desde la terminal. Este script se encargará de:
*   Verificar que tengas instalados PHP, Composer y Node.js.
*   Instalar las dependencias del **Backend** (Symfony).
*   Configurar el entorno (creando el archivo `.env` si no existe).
*   Crear la base de datos y actualizar el esquema.
*   **Poblar la base de datos** con datos iniciales (Roles, Usuarios de prueba, etc.).
*   Instalar las dependencias del **Frontend** (Angular).

### 3. Ejecución (`start.bat`)
Una vez completada la instalación, ejecuta `start.bat`. Este script:
*   Verifica que todas las herramientas estén instaladas.
*   Arranca el servidor de Symfony (Backend) en el puerto `8000`.
*   Arranca el servidor de Angular (Frontend) en el puerto `4200` y abre el navegador automáticamente.

> *Para más detalles sobre la instalación manual, consulta el [README de la Web](Voluntariado4V_Web/README.md).*
>
> *Para documentación sobre la App Móvil, consulta el [README de la App](Voluntariado4V_AppMovil-main/README.md).*

---

## 🏗️ Estructura del Repositorio

### 1. 🌐 Plataforma Web (`/Voluntariado4V_Web`)
Una Single Page Application (SPA) moderna conectada a una API REST.

*   **Frontend**: Angular 20, TypeScript, Bootstrap 5.
*   **Backend**: Symfony 7.3, PHP 8.2, Doctrine ORM.
*   **Base de Datos**: SQL Server.
*   **Características**:
    *   Gestión de Usuarios (Voluntarios, Organizaciones, Admin).
    *   Publicación y Gestión de Actividades.
    *   Control de Asistencia y Solicitudes.

### 2. 📱 Aplicación Móvil (`/Voluntariado4V_AppMovil-main`)
Aplicación nativa para dispositivos Android diseñada para potenciar la experiencia del voluntario.

*   **Plataforma**: Android Nativo.
*   **Lenguaje**: Kotlin / Java.
*   **Herramienta de Construcción**: Gradle.
*   **Funcionalidades**:
    *   Visualización de actividades disponibles.
    *   Inscripción rápida a eventos.
    *   Gestión de perfil del voluntario.

---

## 🛠️ Requisitos del Sistema

Para ejecutar el ecosistema completo necesitarás:


*   **Web**: Node.js, PHP, Composer, Symfony CLI, SQL Server (con controladores PHP instalados y extensión `zip` habilitada).
    *   **Importante**: Para usar SQL Server con PHP, necesitas descargar los controladores de [Microsoft Drivers for PHP for SQL Server](https://learn.microsoft.com/en-us/sql/connect/php/download-drivers-php-sql-server).
    *   Para PHP 8.2 en XAMPP, se recomiendan: `php_sqlsrv_82_ts_x64.dll` y `php_pdo_sqlsrv_82_ts_x64.dll`.
*   **Móvil**: Android Studio (para compilar y ejecutar la app móvil).

---

**Desarrollado para el proyecto Voluntariado 4 Vientos**