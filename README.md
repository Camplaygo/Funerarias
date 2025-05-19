# Sistema de Gestión de Funerarias

Este es un sistema de gestión para funerarias desarrollado en Java con una interfaz gráfica de usuario (Swing) que se conecta a una base de datos Supabase.

## Estructura del Proyecto

```
Funerarias/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── funerarias/
│                   ├── GestionUsuarios.java
│                   ├── LoginApp.java
│                   ├── LoginFrame.java
│                   ├── SupabaseConfig.java
│                   ├── SupabaseService.java
│                   └── Usuario.java
├── lib/
│   ├── gson-2.8.9.jar
│   ├── httpclient-4.5.13.jar
│   └── httpcore-4.4.13.jar
├── pom.xml
└── compile_and_run.bat
```

## Requisitos

- Java 11 o superior
- Maven 3.6 o superior
- Conexión a Internet (para acceder a Supabase)

## Configuración

1. Clona o descarga este repositorio
2. Asegúrate de que todas las dependencias estén en la carpeta `lib/`
3. Configura las credenciales de Supabase en `SupabaseConfig.java`

## Cómo ejecutar

### Usando Maven:

```bash
mvn clean compile assembly:single
java -jar target/funerarias-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Usando el script de compilación:

Simplemente ejecuta el archivo `compile_and_run.bat` en Windows.

## Características

- Autenticación de usuarios
- Gestión de usuarios (solo administradores)
- Interfaz gráfica intuitiva
- Conexión segura con Supabase

## Licencia

Este proyecto está bajo la Licencia MIT.