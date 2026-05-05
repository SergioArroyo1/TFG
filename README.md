# Asistente Personal Web

Proyecto que consiste en una aplicación web.

La aplicación funciona como un asistente personal que permite gestionar distintas áreas del día a día desde un único sitio, como tareas, hábitos, eventos y finanzas.

---

## Funcionalidades actuales

Actualmente la aplicación incluye:

* Registro e inicio de sesión de usuarios
* Sistema de roles (usuario / administrador)
* Gestión de tareas (crear, editar y eliminar)
* Gestión de hábitos
* Calendario interactivo con eventos
* Sistema de finanzas personales:

  * Registro de ingresos y gastos
  * Categorías con límite de gasto
  * Barras de progreso por categoría
  * Gráfico de gastos
* Panel de administración:

  * Listado de usuarios
  * Edición de datos
  * Eliminación
  * Cambio de contraseña (con hash)

---

## Tecnologías utilizadas

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Thymeleaf
* MySQL
* Bootstrap 5
* Chart.js
* FullCalendar

---

## Arquitectura

El proyecto sigue el patrón **MVC (Modelo - Vista - Controlador)**:

* **Modelo** → Entidades como Usuario, Transacción, Categoría, etc.
* **Vista** → HTML con Thymeleaf
* **Controlador** → Manejo de rutas y lógica de interacción

Además, se utiliza una capa de servicios para la lógica de negocio.

---

## Seguridad

Se ha implementado seguridad con Spring Security:

* Autenticación de usuarios
* Protección de rutas
* Control de roles
* Contraseñas encriptadas con BCrypt

---

## Estado del proyecto

El proyecto se encuentra en desarrollo, pero ya cuenta con las funcionalidades principales implementadas y funcionando.

Se seguirán añadiendo mejoras tanto a nivel visual como funcional.

