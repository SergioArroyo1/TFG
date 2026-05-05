Asistente Personal Web

Proyecto que consiste en una aplicación web.

La aplicación funciona como un asistente personal que permite gestionar distintas áreas del día a día desde un único sitio, como tareas, hábitos, eventos y finanzas.

Funcionalidades actuales

Actualmente la aplicación incluye:

Registro e inicio de sesión de usuarios
Sistema de roles (usuario / administrador)
Productividad
Gestión de tareas (crear, editar y eliminar)
Gestión de hábitos
Análisis de tareas y hábitos mediante IA
## Calendario
Calendario interactivo con eventos (FullCalendar)
Crear, editar y eliminar eventos
Asociación de eventos con tareas o hábitos
Marcado de eventos como completados
Cambio de color automático según tipo:
Azul → tarea
Verde → hábito
Gris → evento normal o completado
Persistencia correcta de datos (cada evento mantiene su información propia)
Finanzas personales
Registro de ingresos y gastos
Categorías con límite de gasto
Barras de progreso por categoría
Gráfico de gastos (Chart.js)
Análisis financiero mediante IA
 ## Inteligencia Artificial
Integración con API de Gemini
Análisis automático de:
Finanzas
Tareas
Hábitos
Eventos
Respuestas optimizadas (sin formato Markdown como * o #) para mejor visualización
## Administración
Panel de administración:
Listado de usuarios
Edición de datos
Eliminación
Cambio de contraseña (encriptada)
Tecnologías utilizadas
Java 17
Spring Boot
Spring Security
Spring Data JPA
Thymeleaf
MySQL
Bootstrap 5
Chart.js
FullCalendar
API Gemini (IA)
Arquitectura

El proyecto sigue el patrón MVC (Modelo - Vista - Controlador):

Modelo → Entidades como Usuario, Transacción, Categoría, Tarea, Hábito, Evento
Vista → HTML con Thymeleaf
Controlador → Manejo de rutas y lógica

Además:

Se utiliza una capa de servicios para la lógica de negocio
Integración externa con servicios de IA
Seguridad

Se ha implementado seguridad con Spring Security:

Autenticación de usuarios
Protección de rutas
Control de roles
Contraseñas encriptadas con BCrypt
Seguridad en eventos
Cada usuario solo puede acceder a sus propios eventos
No se pueden editar ni eliminar eventos de otros usuarios
Variables sensibles
Uso de archivo .env para almacenar claves (como API Key de IA)
Añadido a .gitignore para evitar subir datos sensibles a GitHub
