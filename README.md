# SIGAVT - Sistema de Gestión y Análisis de Ventas para Taquería

El Sistema de Gestión y Análisis de Ventas para Taquería (SIGAVT) es una aplicación web orientada a la administración, registro y análisis de las operaciones en un negocio del sector alimenticio.

Esta aplicación fue construida con **Spring Boot 3.5.11** y **Spring Security 6**, diseñada como proyecto final para el Diplomado de Desarrollo de Sistemas con Tecnología JAVA (UNAM). Implementa una arquitectura híbrida que combina el patrón de diseño MVC con una API RESTful segura.

## Table of Contents
- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Getting Your Development Environment Setup](#getting-your-development-environment-setup)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [System Modules](#system-modules)
- [Configuration](#configuration)
- [Author](#author)

## Project Overview
El sistema permite digitalizar el proceso de captura de ventas (Punto de Venta), administrar inventarios, gestionar un padrón de proveedores y generar reportes financieros para apoyar la toma de decisiones. El proyecto demuestra la implementación de las siguientes prácticas de seguridad:
- Autenticación contra base de datos relacional con encriptación BCrypt.
- Autorización basada en roles (RBAC).
- Protección contra vulnerabilidades CSRF en formularios de mutación de estado.
- Autenticación Stateless (JWT) en la capa de la API REST.

## Technologies Used
- **Spring Boot**: 3.5.11
- **Spring Security**: 6.0+ (Con soporte para Thymeleaf Extras)
- **Spring Data JPA & Hibernate**: ORM y persistencia de datos
- **Java**: 17
- **MariaDB**: Motor de base de datos relacional
- **Thymeleaf**: Motor de plantillas (Renderizado de Vistas)
- **Bootstrap**: 5.3.0 (Interfaz de usuario)
- **Chart.js**: Renderizado de gráficos analíticos
- **Maven**: Gestor de dependencias

## Getting Your Development Environment Setup
### Recommended Versions
| Recommended             | Reference                                                                                     | Notes                                                                                                    |
|-------------------------|-----------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| Oracle Java 17 JDK      | [Download](https://www.oracle.com/java/technologies/downloads/#java17)                  | Java 17 or higher is required for Spring Boot 3                                                          |
| MariaDB                 | [Download](https://mariadb.org/download/)                                               | Required for local database setup. Port 3306.                                                            |
| IntelliJ 2022 or Higher | [Download](https://www.jetbrains.com/idea/download/)                                    | Ultimate Edition recommended. Anyway, this runs in Community Edition                                     |
| Maven 3.9.0 or higher   | [Download](https://maven.apache.org/download.cgi)                                       | [Installation Instructions](https://maven.apache.org/install.html)                                       |
| Git 2.44 or higher      | [Download](https://git-scm.com/downloads)                                               |                                                                                                          |

### Verify Installation
```bash
# Check Java version
java -version

# Check Maven version
mvn -version
```

## Building the Project

### Clone the Repository
```bash
git clone https://github.com/daniel13nic/SIGAVT.git
cd SIGAVT
```

### Database Setup
1. Abre tu gestor de base de datos (ej. DBeaver o MySQL Workbench).
2. Crea una base de datos vacía llamada `sigavt_db`.
3. **IMPORTANTE:** Como el sistema está configurado para proteger la integridad de datos (`spring.jpa.hibernate.ddl-auto=none`), debes ejecutar el script SQL incluido en este repositorio para generar la estructura de las tablas e insertar los catálogos iniciales.

### Environment Variables (Variables de Entorno)
Por mejores prácticas de seguridad, las credenciales no están hardcodeadas en el código. Antes de compilar o ejecutar, debes configurar las siguientes variables de entorno en tu sistema o en la configuración de ejecución (Run Configuration) de tu IDE:
- `DB_USER` = (tu usuario local de MariaDB, ej. root)
- `DB_PASS` = (tu contraseña local de MariaDB)

### Build with Maven
```bash
# Clean and build the project
mvn clean install
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using IDE
1. Open the project in IntelliJ IDEA.
2. Navigate to `src/main/java/com/taqueria/sigavt/SigavtApplication.java`.
3. Right-click and select "Run 'SigavtApplication'".

The application will start on **port 8080** by default.

### Access the Application
Once running, you can access the application at:
- Base URL: `http://localhost:8080`
- Login Endpoint: `http://localhost:8080/login`

**Default Credentials:**
A CommandLineRunner is configured to auto-encrypt the initial user on startup.
- **Username**: `danieln`
- **Password**: `admin1234567`
- **Role**: ADMINISTRADOR

## System Modules
1. **Usuarios y Seguridad**: Control de acceso mediante perfiles (Administrador, Operador). Implementa baja lógica (Soft delete) para proteger la integridad referencial.
2. **Productos**: Catálogo central de inventario. Incluye gestión de estados, precios y paginación a nivel de servidor.
3. **Ventas (POS)**: Punto de venta asíncrono con filtrado de categorías y cálculo automático de totales en el backend (prevención de manipulación). Implementa patrón PRG.
4. **Proveedores**: Gestión del padrón de suministros vinculado a los productos mediante una relación Many-to-Many.
5. **Reportes**: Dashboard gerencial con cálculos de métricas en tiempo real y descarga de documentos (PDF y Excel).

## Project Structure
```
SIGAVT/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/taqueria/sigavt/
│   │   │       ├── api/          # REST Controllers (JWT Protected)
│   │   │       ├── controller/   # Spring MVC Web Controllers
│   │   │       ├── model/        # JPA Entities
│   │   │       ├── repository/   # Spring Data JPA Repositories
│   │   │       ├── security/     # Spring Security Config, CustomUserDetails, JWT Utils
│   │   │       ├── service/      # Business Logic
│   │   │       └── SigavtApplication.java
│   │   └── resources/
│   │       ├── static/           # CSS, JS, Images
│   │       ├── templates/        # Thymeleaf HTML Views
│   │       └── application.properties
|           └── sigavt_bd_ddl.sql
├── pom.xml
└── README.md
```

## Configuration

### Application Properties
El sistema se configura a través de `src/main/resources/application.properties`:

```properties
spring.application.name=sigavt

# Configuración de la conexión a MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/sigavt_db
spring.datasource.username=\${DB_USER}
spring.datasource.password=\${DB_PASS}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Configuración de JPA / Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=none
```




## Author
**Daniel Nicolás Feregrino**

Diplomado de Desarrollo de Sistemas con Tecnología JAVA - UNAM
