# SIGAVT - Sistema de Gestión y Análisis de Ventas para Taquería

## Descripción
El Sistema de Gestión y Análisis de Ventas para Taquería (SIGAVT) es una aplicación web orientada a la administración, registro y análisis de las ventas realizadas en un negocio familiar de alimentos. El sistema permite digitalizar el proceso de registro de ventas, facilitando la captura, almacenamiento y generación de reportes para apoyar la toma de decisiones.


## Arquitectura y tecnologías
La solución se implementa bajo una arquitectura cliente-servidor y emplea el siguiente conjunto de tecnologías:
* **Lenguaje:** Java
* **Framework principal:** Spring Boot
* **Capa Web (MVC / API REST):** Spring Web
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** MariaDB
* **Seguridad:** Spring Security 

## Módulos del sistema
1. **Usuarios y seguridad:** Control de acceso mediante perfiles (Administrador, Operador, Consultor).
2. **Productos:** Catálogo y administración de los productos del negocio (categorías, precios, estado).
3. **Ventas:** Registro agregado de las ventas por producto y por día.
4. **Reportes:** Generación de métricas (por ejemplo, ventas por día, productos más vendidos, etc.).

## Instalación y configuración local
1. Clonar el repositorio.
2. Ejecutar el script DDL en una instancia local de MariaDB para crear la base de datos `sigavt_db`.
3. Configurar las credenciales de la base de datos en el archivo `application.properties`.
4. Ejecutar la aplicación a través de Maven o el IDE.

## Autor
* **Daniel Nicolás Feregrino**