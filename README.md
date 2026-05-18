# 📌 Asistente Personal Web

Aplicación web tipo asistente personal que permite gestionar diferentes áreas del día a día desde un único sistema: tareas, hábitos, eventos y finanzas, con soporte de inteligencia artificial.

---

# 🚀 Funcionalidades

## 🔐 Autenticación y usuarios
- Registro e inicio de sesión
- Sistema de roles (USER / ADMIN)
- Seguridad con Spring Security
- Contraseñas encriptadas con BCrypt

---

## 📋 Gestión de tareas
- Crear, editar y eliminar tareas
- Estados de tarea (pendiente / completada)
- Asociación a usuario autenticado
- Validación de formularios

---

## 📅 Calendario
- Integración con FullCalendar
- Crear, editar y eliminar eventos
- Asociación de eventos con tareas o hábitos
- Colores por tipo:
  - 🔵 Tarea
  - 🟢 Hábito
  - ⚪ Evento normal
- Persistencia de datos por usuario

---

## 💰 Finanzas personales
- Registro de ingresos y gastos
- Categorías con límites de gasto
- Barras de progreso por categoría
- Gráficos con Chart.js

---

## 🤖 Inteligencia Artificial
- Integración con API de Gemini
- Análisis automático de:
  - Finanzas
  - Tareas
  - Hábitos
  - Eventos
- Respuestas optimizadas sin formato Markdown (*, #, etc.)

---

## 🛠 Administración
- Panel de administración
- Listado de usuarios
- Edición de usuarios
- Eliminación de usuarios
- Cambio de contraseña (encriptada)

---

# 🧱 Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- Bootstrap 5
- Chart.js
- FullCalendar
- API Gemini (IA)

---

# 🏗 Arquitectura

El proyecto sigue el patrón **MVC (Modelo - Vista - Controlador)**:

- **Modelo** → Entidades (Usuario, Tarea, Evento, Hábito, Transacción)
- **Vista** → Thymeleaf (HTML dinámico)
- **Controlador** → Gestión de rutas y lógica

Además:
- Capa de servicios para la lógica de negocio
- Integración con API externa (Gemini)
- Separación clara por responsabilidades

---

# 🔐 Seguridad

- Autenticación con Spring Security
- Control de acceso por roles
- Cada usuario solo accede a sus propios datos
- Contraseñas cifradas con BCrypt
- Protección de rutas sensibles

---

# ⚙️ Instalación

## 📦 Requisitos
- Java 21
- Maven
- PostgreSQL

---

## 🐘 PostgreSQL

- Base de datos: PostgreSQL
- Puerto: **5433**

### ❓ ¿Por qué puerto 5433?
Se utiliza el puerto 5433 para evitar conflictos con instancias locales o Docker que ya usan el puerto 5432 por defecto.

---

👤 Usuario administrador (seed)

Para pruebas del tribunal:

📧 Email: admin@tfp.com
🔑 Password:1234

📊 Capturas de pantalla

Incluir capturas de:

Login / registro:
<img width="475" height="459" alt="image" src="https://github.com/user-attachments/assets/055f7a13-30d8-4414-8b51-42f56dcb39c6" />

Panel principal
Gestión de tareas
Calendario
Finanzas
Panel de administración
