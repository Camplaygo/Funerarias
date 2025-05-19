@echo off
setlocal enabledelayedexpansion

:: Título
@title Funerarias - Compilación y Ejecución

:: 1. Configuración inicial
echo [INFO] Iniciando proceso de compilación y ejecución...
echo.

:: 2. Configurar rutas
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
set "MAVEN_HOME=C:\Users\deivi\OneDrive\Documentos\apache-maven-3.9.9-bin\apache-maven-3.9.9"

:: Usar variables entre comillas para rutas con espacios
set "JAVA_EXE="%JAVA_HOME%\bin\java.exe""
set "MVN_EXE="%MAVEN_HOME%\bin\mvn.cmd""

:: Agregar al PATH
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

:: 3. Verificar Java
echo [1/4] Verificando instalación de Java...
%JAVA_EXE% -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] No se pudo encontrar Java en %JAVA_HOME%
    echo.
    echo Solución:
    echo 1. Verifica que tengas instalado el JDK en 'C:\Program Files\Java\jdk-24'
    echo 2. Si está instalado en otra ubicación, actualiza la variable JAVA_HOME en este script
    pause
    exit /b 1
)

for /f "tokens=3" %%a in ('%JAVA_EXE% -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VERSION=%%a"
echo [INFO] Java version: %JAVA_VERSION%
echo [INFO] JAVA_HOME: %JAVA_HOME%

:: 4. Verificar Maven
echo [2/4] Verificando Maven...
if not exist %MVN_EXE% (
    echo [ERROR] No se encontró Maven en %MAVEN_HOME%
    echo.
    echo Solución:
    echo 1. Verifica que Maven esté instalado en la ruta especificada
    echo 2. O descarga Maven desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

:: 5. Configurar Maven Wrapper
echo [3/4] Configurando Maven Wrapper...
if not exist ".mvn" mkdir ".mvn"
if not exist ".mvn\wrapper" mkdir ".mvn\wrapper"
if not exist "mvnw.cmd" (
    echo [INFO] Creando Maven Wrapper...
    %MVN_EXE% -N io.takari:maven:wrapper -Dmaven=3.9.9
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] No se pudo crear el Maven Wrapper
        echo.
        echo Solución:
        echo 1. Verifica tu conexión a internet
        echo 2. Ejecuta manualmente: %MVN_EXE% -N io.takari:maven:wrapper -Dmaven=3.9.9
        pause
        exit /b 1
    )
)

:: 6. Limpiar y compilar
echo [4/4] Limpiando y compilando el proyecto...
if exist "mvnw.cmd" (
    call mvnw.cmd clean compile
) else (
    %MVN_EXE% clean compile
)

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Error al compilar el proyecto.
    echo.
    echo Posibles soluciones:
    echo 1. Verifica tu conexión a internet
    echo 2. Ejecuta manualmente: mvnw.cmd clean compile
    echo 3. O ejecuta: %MVN_EXE% clean compile
    pause
    exit /b %ERRORLEVEL%
)

:: 7. Ejecutar la aplicación
echo [INFO] Iniciando la aplicación...
if exist "mvnw.cmd" (
    call mvnw.cmd exec:java -Dexec.mainClass="com.funerarias.LoginApp"
) else (
    %MVN_EXE% exec:java -Dexec.mainClass="com.funerarias.LoginApp"
)

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Error al ejecutar la aplicación.
    echo.
    echo Intenta ejecutar manualmente:
    echo mvnw.cmd exec:java -Dexec.mainClass="com.funerarias.LoginApp"
    echo O:
    echo %MVN_EXE% exec:java -Dexec.mainClass="com.funerarias.LoginApp"
    echo.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [COMPLETADO] Proceso finalizado correctamente.
pause
