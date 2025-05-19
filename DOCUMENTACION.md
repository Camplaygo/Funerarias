# Documentación del Sistema de Gestión de Funerarias

## Índice
1. [Descripción General](#descripción-general)
2. [Requisitos del Sistema](#requisitos-del-sistema)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Configuración](#configuración)
5. [Guía de Uso](#guía-de-uso)
6. [API y Servicios](#api-y-servicios)
7. [Mantenimiento](#mantenimiento)
8. [Solución de Problemas](#solución-de-problemas)

## Descripción General

El Sistema de Gestión de Funerarias es una aplicación de escritorio desarrollada en Java que permite la gestión de usuarios y el acceso a funcionalidades específicas según los permisos del usuario. La aplicación se conecta a una base de datos Supabase para el almacenamiento y recuperación de información.

## Requisitos del Sistema

- Java JDK 11 o superior
- Maven 3.9.9
- Conexión a Internet (para acceder a Supabase)
- 4GB de RAM mínimo
- 500MB de espacio en disco

## Estructura del Proyecto

```
Funerarias/
├── src/
│   └── main/
│       └── java/com/funerarias/
│           ├── GestionUsuarios.java  # Lógica de gestión de usuarios
│           ├── HomeFrame.java        # Pantalla principal de la aplicación
│           ├── LoginApp.java         # Punto de entrada de la aplicación
│           ├── LoginFrame.java       # Pantalla de inicio de sesión
│           ├── SupabaseConfig.java   # Configuración de Supabase
│           ├── SupabaseService.java  # Servicio para interactuar con Supabase
│           └── Usuario.java          # Modelo de datos de usuario
├── target/                          # Archivos compilados
├── pom.xml                          # Configuración de Maven
└── README.md                        # Este archivo
```

## Configuración

### 1. Configuración de Supabase

1. Crear una cuenta en [Supabase](https://supabase.com/)
2. Crear un nuevo proyecto
3. Configurar las tablas necesarias:
   ```sql
   CREATE TABLE usuarios (
     id SERIAL PRIMARY KEY,
     nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
     contrasena_hash VARCHAR(255) NOT NULL,
     es_admin BOOLEAN DEFAULT false
   );
   ```
4. Actualizar las credenciales en `SupabaseConfig.java`

### 2. Variables de Entorno

Crear un archivo `.env` en la raíz del proyecto con las siguientes variables:

```
SUPABASE_URL=tu_url_de_supabase
SUPABASE_KEY=tu_clave_api_de_supabase
```

## Guía de Uso

### Compilación

```bash
mvn clean package
```

### Ejecución

```bash
java -jar target/funerarias-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Flujo de la Aplicación

1. **Inicio de Sesión**
   - Ingresar nombre de usuario y contraseña
   - Los usuarios administradores tendrán acceso a funciones adicionales

2. **Pantalla Principal**
   - Muestra un mensaje de bienvenida
   - Botones de navegación según los permisos del usuario

3. **Panel de Administración** (solo para administradores)
   - Gestión de usuarios
   - Agregar/eliminar usuarios
   - Modificar permisos

## API y Servicios

### SupabaseService

Clase principal para interactuar con la API de Supabase:

- `autenticarUsuario(String usuario, String contrasena)`: Autentica un usuario
- `crearUsuario(Usuario usuario)`: Crea un nuevo usuario
- `eliminarUsuario(String nombreUsuario)`: Elimina un usuario
- `obtenerUsuarios()`: Obtiene la lista de usuarios

### GestionUsuarios

Maneja la lógica de negocio para la gestión de usuarios:
- Autenticación
- Validación de credenciales
- Gestión de sesiones

## Mantenimiento

### Actualizar Dependencias

Para actualizar las dependencias de Maven:

```bash
mvn versions:display-dependency-updates
mvn versions:use-latest-releases
```

### Generar Documentación

```bash
mvn javadoc:javadoc
```

## Solución de Problemas

### Problemas Comunes

1. **Error de Conexión a Supabase**
   - Verificar la conexión a internet
   - Comprobar las credenciales en `SupabaseConfig.java`
   - Asegurarse de que la URL y la clave de API sean correctas

2. **Error de Autenticación**
   - Verificar que el usuario y contraseña sean correctos
   - Asegurarse de que el usuario exista en la base de datos

3. **Problemas de Compilación**
   - Asegurarse de tener Java JDK 11 o superior instalado
   - Ejecutar `mvn clean install` para limpiar y reinstalar dependencias

### Registro de Errores

Los registros se guardan en `logs/application.log` con el siguiente formato:

```
[FECHA] [NIVEL] [CLASE] - MENSAJE
```

## Contribución

1. Hacer fork del repositorio
2. Crear una rama para la nueva característica (`git checkout -b feature/nueva-caracteristica`)
3. Hacer commit de los cambios (`git commit -am 'Añadir nueva característica'`)
4. Hacer push a la rama (`git push origin feature/nueva-caracteristica`)
5. Crear un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.
