# Voluntariado4V_AppMovil

Aplicación Móvil nativa para Android, parte del proyecto **Voluntariado 4 Vientos**. Permite a los voluntarios inscribirse en actividades y gestionar su perfil desde cualquier lugar.

---

## 📱 Tecnologías Utilizadas

*   **Lenguaje:** Java.
*   **IDE Recomendado:** Android Studio Narwhal.
*   **API Minima:** Android 13 (API 33).
*   **Comunicación:** Retrofit (Cliente HTTP).

---

## 📋 Requisitos Previos

*   **Android Studio** instalado.
*   **JDK 17** o superior (configurado en Android Studio).
*   **Dispositivo Android** o Emulador configurado.
*   **Backend Ejecutándose**: La app necesita conectarse al servidor Symfony (asegúrate de que la IP esté configurada correctamente si usas un dispositivo físico).

---

## ⚡ Instalación y Ejecución

1.  **Abrir el Proyecto**:
    *   Abre Android Studio.
    *   Selecciona "Open" y navega a la carpeta `Voluntariado4V_AppMovil-main`.

2.  **Sincronizar Gradle**:
    *   Deja que Android Studio descargue las dependencias y sincronice el proyecto.

3.  **Configurar IP del Backend** (Importante):
    *   Si usas el **Emulador**: `http://10.0.2.2:8000` suele funcionar por defecto.
    *   Si usas un **Dispositivo Físico**: Asegúrate de que el móvil y el PC estén en la misma WiFi. Cambia la URL base en la configuración de Retrofit (usualmente en `NetworkModule` o `ApiService`) a la IP local de tu PC (ej: `http://192.168.1.XX:8000`).

4.  **Ejecutar**:
    *   Pulsa el botón "Run" (Triángulo verde) en Android Studio.

---

## 📂 Estructura del Proyecto

*   `app/src/main/java`: Código fuente Java (Activities, Fragments, Adapters).
*   `app/src/main/res`: Recursos (Layouts XML, Strings, Drawables).
*   `app/build.gradle`: Configuración de dependencias.

---

## 🎨 Diseño y UX

Para conocer los lineamientos de diseño Material Design y recursos gráficos de la app:

👉 **[Ver Documentación de Diseño Móvil](design/DESIGN.md)**

---

[⬅️ Volver al Proyecto Principal](../README.md)
