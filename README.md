# Fortinity — Backend

Proyecto desarrollado para la asignatura Desarrollo Fullstack II de Duoc UC.


## Descripción del proyecto

Fortinity es una plataforma de comercio electrónico orientada a la venta de videojuegos y cuentas de plataformas digitales.

El backend permite administrar productos, categorías, usuarios, órdenes de compra y boletas mediante una API REST conectada a una base de datos MySQL.


## Tecnologías utilizadas

- Java 21.
- Spring Boot 4.0.10.
- Spring Security.
- Spring Data JPA.
- Hibernate.
- MySQL.
- Maven.
- JWT.
- Swagger y OpenAPI.
- Postman.
- Git y GitHub.


## Requisitos previos

- Java JDK 21.
- MySQL Server.
- MySQL Workbench.
- Maven Wrapper incluido en el proyecto.
- Visual Studio Code o un entorno compatible con Java.


## Configuración de la base de datos

El proyecto utiliza una base de datos MySQL con la siguiente configuración:

- Servidor: localhost.
- Puerto: 3306.
- Base de datos: fortinity_db.
- Usuario: fortinity_app.

La base de datos y el usuario deben existir antes de ejecutar el backend.

La creación y actualización de las tablas se realiza mediante JPA e Hibernate.

Para recuperar los productos y registros de demostración se requiere importar los datos correspondientes.


## Variables de entorno

Antes de ejecutar el backend, se deben configurar las siguientes variables de entorno:

- DB_PASSWORD: contraseña del usuario de MySQL.
- JWT_SECRET: clave secreta JWT codificada en Base64, de al menos 32 bytes antes de codificar.

Las credenciales y claves secretas no deben almacenarse directamente en el código fuente ni publicarse en GitHub.


## Ejecución del backend

Desde la carpeta principal del proyecto, ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

El servidor estará disponible en:

http://localhost:8080


## Arquitectura del proyecto

El backend utiliza una arquitectura organizada por capas:

- Controller: recepción de solicitudes HTTP.
- Service: lógica de negocio.
- Repository: acceso a la base de datos.
- Model: entidades del sistema.
- DTO: transferencia de datos.
- Security: generación de tokens JWT.
- Config: configuración de seguridad y documentación.


## Base de datos

Las principales tablas del sistema son:

- usuarios.
- categorias.
- productos.
- boletas.
- detalle_boleta.

Las relaciones entre las entidades permiten registrar las compras, identificar sus productos y mantener el control de stock.


## API REST versionada

La dirección base de la API es:

http://localhost:8080/api/v1

Los principales recursos disponibles son:

| Recurso | Ruta |
| --- | --- |
| Autenticación | /api/v1/auth |
| Productos | /api/v1/productos |
| Categorías | /api/v1/categorias |
| Usuarios | /api/v1/usuarios |
| Boletas | /api/v1/boletas |

Las rutas originales que utilizan el prefijo /api se mantienen por compatibilidad.


## Autenticación

El sistema utiliza tokens JWT para autenticar a los usuarios.

| Método | Endpoint | Funcionalidad |
| --- | --- | --- |
| POST | /api/v1/auth/register | Registrar usuario |
| POST | /api/v1/auth/login | Iniciar sesión |
| GET | /api/v1/auth/me | Consultar usuario autenticado |
| POST | /api/v1/auth/refresh | Renovar token JWT |

Las contraseñas se almacenan mediante BCrypt.


## Seguridad y renovación JWT

Los tokens JWT tienen una duración configurada de 3600 segundos.

El backend verifica la firma, expiración, emisor, estado y rol del usuario.

La renovación requiere presentar un token vigente mediante el encabezado Authorization de tipo Bearer.

El frontend solicita automáticamente la renovación cuando realiza una petición autenticada y quedan cinco minutos o menos de vigencia.

Si el token expira, el usuario debe iniciar sesión nuevamente.

La implementación no utiliza un refresh token independiente. El token anterior conserva su validez hasta su expiración.


## Roles y permisos

### Administrador

- Administración de productos y categorías.
- Creación y modificación de productos.
- Desactivación y restauración de productos.
- Consulta de órdenes y boletas.
- Administración de roles y estados de usuarios.


### Vendedor

- Consulta de productos.
- Consulta de detalles de productos.
- Consulta de órdenes y boletas.
- Acceso restringido a operaciones de modificación.


### Cliente

- Registro e inicio de sesión.
- Consulta del catálogo.
- Realización de compras.
- Consulta de sus propias boletas.
- Acceso restringido a recursos administrativos.


## Gestión de compras

El backend permite registrar compras realizadas por los clientes.

Durante el proceso se verifica la disponibilidad de los productos, se aplican los descuentos correspondientes y se calcula el total de la compra.

Al completar una compra, se registra la boleta, sus detalles y se actualiza el stock.

El proceso utiliza transacciones para mantener la consistencia de la información.


## Documentación Swagger

La documentación de los endpoints se encuentra disponible en:

http://localhost:8080/swagger-ui/index.html

Swagger permite consultar los métodos HTTP, parámetros, estructuras de datos y probar los servicios de la API REST.


## Pruebas realizadas

Se realizaron pruebas funcionales y de integración mediante Postman, Swagger, MySQL Workbench y el frontend.

Entre las funcionalidades verificadas se encuentran:

- Registro e inicio de sesión.
- Autenticación JWT.
- Restricción de acceso según roles.
- Creación y actualización de productos.
- Eliminación lógica y restauración de productos.
- Registro de compras y boletas.
- Actualización de stock.
- Administración de usuarios.
- Versionado de API REST.
- Renovación automática del JWT.
- Verificación de la nueva fecha de expiración.


## Integración con el frontend

El backend se encuentra integrado con una aplicación desarrollada en Next.js y React.

La comunicación se realiza mediante solicitudes HTTP a la API REST.

La configuración CORS permite la comunicación con el frontend ubicado en:

http://localhost:3000


## Repositorios

Backend:

https://github.com/BIngvab/fortinity-backend

Frontend:

https://github.com/BIngvab/fortinity

Rama de desarrollo: develop.


## Contexto académico

Proyecto desarrollado como parte de la asignatura Desarrollo Fullstack II de Duoc UC.


## Año

2026
