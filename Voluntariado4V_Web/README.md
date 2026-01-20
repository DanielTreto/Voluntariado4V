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
*   **Base de Datos:** MySQL (con Doctrine ORM).
*   **Seguridad:** Autenticación mixta (JWT Firebase + Credenciales SQL).

---

## 📋 Requisitos Previos

Antes de empezar, asegúrate de tener instalado en tu equipo:

1.  **Node.js** (v18 o superior) y **npm**.
2.  **PHP** (v8.2 o superior).
3.  **Composer** (Gestor de paquetes PHP).
4.  **Symfony CLI** (Recomendado para ejecutar el servidor).
5.  **MySQL Server** (XAMPP, Docker o instalación nativa).

---


## ⚡ Quick Start (Windows)

We have included automated scripts to simplify the installation process.

### 1. Installation
Run the `install.bat` script. This will:
*   Check for required tools (PHP, Composer, Node/NPM).
*   Install Backend dependencies.
*   Setup the Database (Create & Schema).
*   Install Frontend dependencies.

### 2. Execution
Run the `start.bat` script. This will:
*   Launch the Symfony Backend server.
*   Launch the Angular Frontend server.
*   Open your browser automatically.

---

## 🚀 Manual Installation and Setup
If you prefer to install manually or are on a non-Windows system, follow these steps:


### 1. Clonar el repositorio
```bash
git clone https://github.com/DanielTreto/Voluntariado4V_Web.git
cd Voluntariado4V_Web
```

### 2. Configuración del Backend (Symfony)

1.  Navega a la carpeta del backend:
    ```bash
    cd backend
    ```

2.  Instala las dependencias de PHP:
    ```bash
    composer install
    ```

3.  Configura la conexión a base de datos:
    *   Crea un archivo `.env.local` (o edita el `.env` existente).
    *   Ajusta la variable `DATABASE_URL` con tus credenciales de MySQL:
    ```env
    DATABASE_URL="mysql://usuario:password@127.0.0.1:3306/voluntariado_db?serverVersion=8.0&charset=utf8mb4"
    ```

4.  Crea la base de datos y el esquema:
    ```bash
    php bin/console doctrine:database:create
    php bin/console doctrine:schema:update --force
    ```

5.  (Opcional) Carga datos de prueba si dispones de accesorios (fixtures).

6.  Inicia el servidor de Symfony:
    ```bash
    symfony server:start
    ```
    *El backend estará disponible en: `http://localhost:8000`*

### 3. Configuración del Frontend (Angular)

1.  Abre una nueva terminal y navega a la carpeta del frontend:
    ```bash
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

Para probar la aplicación puedes utilizar las siguientes credenciales pre-configuradas (si has cargado los datos iniciales):

| Rol | Email | Contraseña |
|-----|-------|------------|
| **Voluntario** | `testVoluntario@gmail.com` | `1234` |
| **Organización** | `solera@example.com` | `temp1234` |
| **Admin** | *(Requiere registro en BBDD)* | --- |

---

## 📂 Estructura del Proyecto

*   `/backend`: API REST (Symfony). Controladores en `src/Controller`, Entidades en `src/Entity`.
*   `/frontend`: SPA (Angular). Componentes en `src/app/components`, Páginas en `src/app/pages`.
*   `/BBDD`: Scripts SQL de inicialización y modelo de datos.

---

**Desarrollado por el equipo de Voluntariado 4 Vientos**
