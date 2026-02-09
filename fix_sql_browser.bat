@echo off
echo Habilitando servicio SQL Browser...
sc config SQLBrowser start= auto
if %errorlevel% neq 0 (
    echo Error al habilitar el servicio. Asegúrate de ejecutar como Administrador.
    pause
    exit /b
)

echo Iniciando servicio SQL Browser...
net start SQLBrowser
if %errorlevel% neq 0 (
    echo Error al iniciar el servicio.
    pause
    exit /b
)

echo Servicio iniciado correctamente. Puedes cerrar esta ventana.
pause
