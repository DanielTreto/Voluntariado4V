# Voluntariado4V_Web

Aplicación Web para la gestión de voluntariado, organizaciones y estudiantes, parte del proyecto **Voluntariado 4 Vientos**.

Este repositorio contiene el código fuente completo de la plataforma, dividido en un Frontend moderno (Angular) y un Backend robusto (Symfony).

---

## 🛠️ Tecnologías Utilizadas

Hemos utilizado un stack tecnológico de última generación para asegurar rendimiento, escalabilidad y mantenibilidad:

### Frontend (SPA)
*   **Framework:** **Angular 20** (Última versión disponible).
*   **Lenguaje:** TypeScript 5.9.
*   **Estilos:** Bootstrap 5.3 + Bootstrap Icons (SCSS).
*   **Gráficos:** Chart.js 4.5.
*   **Servicios en Nube:** Firebase (Autenticación y Storage).

### Backend (API REST)
*   **Framework:** **Symfony 7.3**.
*   **Lenguaje:** PHP 8.2+.
*   **Base de Datos:** SQL Server (con Doctrine ORM).
*   **Seguridad:** Autenticación mixta (JWT Firebase + Credenciales SQL).

---

## 🎨 Diseño y UX

Para detalles sobre la identidad visual, guías de estilo y acceso al archivo Figma del proyecto, consulta nuestra guía de diseño:

👉 **[Ver Documentación de Diseño y Figma](design/DESIGN.md)**

---

## 📋 Requisitos Previos

Antes de empezar, asegúrate de tener instalado en tu equipo:

1.  **Node.js** (v18 o superior) y **npm**.
2.  **PHP** (v8.2 o superior).
3.  **Composer** (Gestor de paquetes PHP).
4.  **Symfony CLI** (Recomendado para ejecutar el servidor).
5.  **SQL Server** (Instalación nativa o Docker).

---


## ⚡ Quick Start (Windows)

Hemos incluido scripts automatizados para simplificar el proceso de instalación.

### 1. Clonar el repositorio
```bash
git clone https://github.com/DanielTreto/Voluntariado4V.git
cd Voluntariado4V
```

### 2. Instalación
Ejecuta el script `install.bat`. Esto hará lo siguiente:
*   Verificar las herramientas necesarias (PHP, Composer, Node/NPM).
*   Instalar dependencias del Backend.
*   Configurar la Base de Datos (Creación y Esquema).
*   Instalar dependencias del Frontend.

### 3. Ejecución
Ejecuta el script `start.bat`. Esto hará lo siguiente:
*   Lanzar el servidor Backend de Symfony.
*   Lanzar el servidor Frontend de Angular.
*   Abrir tu navegador automáticamente.

---

## 🚀 Instalación y Configuración Manual
Si prefieres instalar manualmente o estás en un sistema no-Windows, sigue estos pasos:


### 1. Clonar el repositorio
```bash
git clone https://github.com/DanielTreto/Voluntariado4V.git
cd Voluntariado4V
```

### 2. Configuración del Backend (Symfony)

1.  Navega a la carpeta del backend:
    ```bash
    cd Voluntariado4V_Web
    cd backend
    ```

2.  Instala las dependencias de PHP:
    ```bash
    composer install
    ```

3.  Configura la conexión a base de datos:
    *   Crea un archivo `.env.local` (o edita el `.env` existente).
    *   Ajusta la variable `DATABASE_URL` con tus credenciales de SQL Server:
    ```env
    DATABASE_URL="sqlsrv://usuario:password@127.0.0.1:1433/voluntariado_db"
    ```

4.  Crea la base de datos y ejecuta las migraciones:
    ```bash
    php bin/console doctrine:database:create
    php bin/console doctrine:migrations:migrate
    ```

5.  (Opcional) Carga datos de prueba de el archivo full_database_setup.sql de la carpeta /src/BDD.

6.  Inicia el servidor de Symfony:
    ```bash
    symfony server:start
    ```
    *El backend estará disponible en: `http://localhost:8000`*

### 3. Configuración del Frontend (Angular)

1.  Abre una nueva terminal y navega a la carpeta del frontend:
    ```bash
    cd Voluntariado4V_Web
    cd frontend
    ```

2.  Instala las dependencias de Node:
    ```bash
    npm install
    ```

3.  Inicia el servidor de desarrollo:
    ```bash
    ng serve
    ```
    *La aplicación web estará disponible en: `http://localhost:4200`*

---

## 🧪 Usuarios de Prueba (Demo)

Para probar la aplicación puedes utilizar las siguientes credenciales (si has cargado los datos iniciales):

| Rol | Email | Contraseña |
|-----|-------|------------|
| **Voluntario** | `pedro@email.com` | `admin123` |
| **Organización** | `cruzroja@email.com` | `admin123` |
| **Admin** | `admin@voluntariado.com` | `admin123` |

---

## 📂 Estructura del Proyecto

*   `/backend`: API REST (Symfony).
    *   `src/Controller`: Controladores de la API (Auth, Activity, Organization, etc.).
    *   `src/Entity`: Definición de modelos de datos (ORM Doctrine).
    *   `src/Repository`: Consultas a la base de datos.
    *   `src/BBDD`: Scripts SQL iniciales (`full_database_setup.sql`).
*   `/frontend`: SPA (Angular).
    *   `src/app/components`: Componentes reutilizables (Átomos, Moléculas, Organismos).
    *   `src/app/pages`: Vistas principales (Dashboard, Home, Login).
    *   `src/app/services`: Lógica de negocio y comunicación HTTP (`ApiService`, `NotificationService`).
    *   `src/app/guards`: Protección de rutas.

---

**Desarrollado por el equipo de Voluntariado 4 Vientos**

[⬅️ Volver al Proyecto Principal](../README.md)
