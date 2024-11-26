
# Proyect-Spring

## Descripción
**Proyect-Spring** es una aplicación diseñada para gestionar productos y usuarios, con dos roles principales: **Cliente** y **Administrador**. Los usuarios pueden realizar operaciones como registro, gestión de perfiles, y manejo de productos. El sistema también incluye características como autenticación JWT y subida de imágenes, además de estar integrado con servicios de Azure.

---

## Tecnologías utilizadas
- **Lenguaje principal:** Java
- **Framework backend:** Spring Boot
- **Base de datos:** PostgreSQL
- **Seguridad:** Spring Security con JWT
- **Almacenamiento de imágenes:** Azure Blob Storage
- **Email Service:** Azure Communication Services
- **Documentación API:** OpenAPI (Swagger)

---

## Estructura del proyecto
El proyecto está organizado en las siguientes carpetas:

1. **`configuration`**: Configuración general del proyecto.
2. **`exception`**:
    - `advice`: Manejadores globales para excepciones.
    - `exception`: Clases personalizadas para manejo de errores.
3. **`persistence`**:
    - `entity`: Entidades de la base de datos.
    - `persistence`: Repositorios para operaciones con la base de datos.
4. **`presentation`**:
    - `controller`: Controladores REST para manejar las solicitudes.
    - `dto`: Objetos de transferencia de datos.
5. **`security`**:
    - `component`: Clases auxiliares relacionadas con la seguridad.
    - `configuration`: Configuración de autenticación y autorización.
    - `filter`: Filtros de seguridad como JWT.
6. **`service`**:
    - `implementation`: Implementaciones de servicios.
    - `interfaces`: Interfaces de los servicios.
7. **`Utils`**: Clases utilitarias para lógica general.

---

## Base de datos
### Configuración
En el archivo `application.properties` se encuentran los detalles para conectarse a la base de datos PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spring
spring.datasource.username=postgres
spring.datasource.password=Postgres123
```

### Estructura de tablas
#### Cliente (`Client`)
```sql
CREATE TABLE Client (
  email VARCHAR(150) PRIMARY KEY NOT null,
  ClientID VARCHAR(20) UNIQUE,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  password VARCHAR(255),
  phone VARCHAR(20),
  state BOOLEAN DEFAULT TRUE,
  imageURL VARCHAR(255),
  role VARCHAR(50) default 'CLIENT' UNIQUE
);
```

#### Administrador (`Administrator`)
```sql
CREATE TABLE Administrator (
  email VARCHAR(150) PRIMARY KEY NOT NULL,
  AdministratorID VARCHAR(20) UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  state BOOLEAN DEFAULT TRUE,
  imageURL VARCHAR(255),
  role VARCHAR(50) default 'ADMINISTRATOR' UNIQUE
);
```

#### Productos (`Products`)
```sql
CREATE TABLE Products (
  idProduct SERIAL PRIMARY KEY,
  nameProduct VARCHAR(255),
  description VARCHAR(200),
  price DECIMAL(10, 2) NOT NULL CHECK (price >= 0) DEFAULT 0.00,
  stockQuantity INT,
  category VARCHAR(200),
  imageURL VARCHAR(255)
);
```

---

## Endpoints principales
### **Clientes**
#### Registro de cliente
- **Endpoint:** `POST /client/register`
- **Descripción:** Permite registrar un cliente y enviar un correo de confirmación.
- **Body:**
  ```json
  {
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "password123",
    "phone": "1234567890"
  }
  ```

#### Subida de imagen
- **Endpoint:** `POST /client/upload`
- **Descripción:** Permite a clientes o administradores subir imágenes asociadas al perfil.
- **Headers:**
  ```json
  {
    "Authorization": "Bearer <JWT_TOKEN>"
  }
  ```
#### Perfil
- **Endpoint:** `GET /client/profile`
- **Descripción:** Permite a los clientes visualizar su perfil y a los administradores ver la informacion de cualquier cliente 
- **Headers:**
  ```json
  {
    "Authorization": "Bearer <JWT_TOKEN>"
  }
  ```
---

#### Cambio de contraseña
- **Endpoint:** `PUT /client/updatePassword`
- **Descripción:** Permite cambiar la contraseña del cliente autenticado.
- **Headers:**
  ```json
  {
    "Authorization": "Bearer <JWT_TOKEN>"
  }
  ```
--- 
#### Actualizar datos
- **Endpoint:** `PUT /client/update`
- **Descripción:** Permite al cliente cambiar sus datos y al administrador cambiar los datos del cliente lo necesite
- **Headers:**
  ```json
  {
    "Authorization": "Bearer <JWT_TOKEN>"
  }
  ```
--- 
#### Eliminar
- **Endpoint:** `DELETE /client/delete`
- **Descripción:** Permite eliminar su cuenta si el cliente o el administrador asi lo desea
- - **Headers:**
```json
{
  "Authorization": "Bearer <JWT_TOKEN>"
}
```

##

## Funcionalidades futuras
1. **Gestión de productos**:
    - Registro y actualización de productos.
    - Consultas filtradas por categorías y disponibilidad.
2. **Administrador**:
    - Gestión de clientes.
    - Manejo avanzado de roles y permisos.

---

## Cómo ejecutar el proyecto
1. Clona el repositorio.
2. Configura una base de datos PostgreSQL y actualiza las credenciales en el archivo `application.properties`.
3. Ejecuta el comando:
   ```bash
   mvn spring-boot:run
   ```
4. Accede a la documentación Swagger en:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---
