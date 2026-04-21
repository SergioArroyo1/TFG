
DROP TABLE Usuario, Tarea, Habito, HabitoHistorial, Evento, Categoria, Transaccion CASCADE ;


-- 1. Tabla Usuarios
CREATE TABLE Usuario (
                         id_usuario SERIAL PRIMARY KEY,
                         nombre VARCHAR(100),
                         email VARCHAR(100) UNIQUE,
                         contrasena VARCHAR(100),
                         rol VARCHAR(20)
);

CREATE TABLE Tarea (
                       id_tarea SERIAL PRIMARY KEY,
                       titulo VARCHAR(100),
                       descripcion TEXT,
                       fecha_limite DATE,
                       prioridad INT,
                       estado VARCHAR(50),
                       id_usuario INT,
                       FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

CREATE TABLE Habito (
                        id_habito SERIAL PRIMARY KEY,
                        nombre VARCHAR(100),
                        descripcion TEXT,
                        frecuencia VARCHAR(50),
                        id_usuario INT,
                        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);


-- 4. Tabla Historial de Hábitos
CREATE TABLE HabitoHistorial (
                                 id_historial SERIAL PRIMARY KEY,
                                 id_habito INT NOT NULL,
                                 fecha DATE NOT NULL,
                                 cumplimiento BOOLEAN DEFAULT FALSE,
                                 FOREIGN KEY (id_habito) REFERENCES Habito(id_habito) ON DELETE CASCADE
);

-- 5. Tabla Eventos / Calendario
CREATE TABLE Evento (
                        id_evento SERIAL PRIMARY KEY,
                        titulo VARCHAR(150) NOT NULL,
                        descripcion TEXT,
                        fecha DATE NOT NULL,
                        id_usuario INT NOT NULL,
                        id_tarea INT,
                        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                        FOREIGN KEY (id_tarea) REFERENCES Tarea(id_tarea) ON DELETE SET NULL
);

ALTER TABLE Evento
    ADD COLUMN id_habito INT NULL;

ALTER TABLE Evento
    ADD CONSTRAINT fk_evento_habito
        FOREIGN KEY (id_habito) REFERENCES Habito(id_habito)
            ON DELETE SET NULL;

-- ======================
-- CATEGORIA
-- ======================
CREATE TABLE Categoria (
                           id_categoria SERIAL PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           limite_mensual DOUBLE PRECISION NOT NULL,

                           id_usuario BIGINT NOT NULL,
                           CONSTRAINT fk_categoria_usuario
                               FOREIGN KEY (id_usuario)
                                   REFERENCES usuario(id_usuario)
                                   ON DELETE CASCADE
);

-- ======================
-- TRANSACCION
-- ======================
CREATE TABLE Transaccion (
                             id_transaccion SERIAL PRIMARY KEY,
                             titulo VARCHAR(150),
                             cantidad DOUBLE PRECISION NOT NULL,
                             tipo VARCHAR(20) NOT NULL, -- INGRESO / GASTO
                             fecha DATE NOT NULL,

                             id_usuario BIGINT NOT NULL,
                             id_categoria BIGINT,

                             CONSTRAINT fk_transaccion_usuario
                                 FOREIGN KEY (id_usuario)
                                     REFERENCES usuario(id_usuario)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_transaccion_categoria
                                 FOREIGN KEY (id_categoria)
                                     REFERENCES categoria(id_categoria)
                                     ON DELETE SET NULL
);

-- =========================
-- Datos de prueba TFG
-- =========================

-- 1. Usuarios
INSERT INTO Usuario (nombre, email, contrasena, rol)
VALUES (
           'Administrador',
           'admin@tfp.com',
           '$2a$10$aRZwY8mIoGunNT7kRV357.6nhKEzLQ8Qi0Ui0HhhBz5WMvrIBtqN6',
           'ADMIN'
       );


-- 2. Tareas
INSERT INTO Tarea (titulo, descripcion, fecha_limite, prioridad, estado, id_usuario)
VALUES
    ('Estudiar bases de datos', 'Repasar conceptos de SQL y relaciones', '2026-03-15', 3, 'pendiente', 1),
    ('Proyecto de IA', 'Desarrollar modelo de predicción de hábitos', '2026-03-20', 5, 'pendiente', 1);

-- 3. Hábitos
INSERT INTO Habito (nombre, descripcion, frecuencia, id_usuario)
VALUES
    ('Beber agua', 'Tomar al menos 2 litros de agua diarios', 'diario', 1),
    ('Ejercicio', 'Ejercitar al menos 30 minutos', 'diario', 1),
    ('Estudio', 'Dedicar 2 horas a repaso', 'diario', 1);


-- 4. Historial de hábitos
INSERT INTO HabitoHistorial (id_habito, fecha, cumplimiento)
VALUES
    (1, '2026-03-01', TRUE),
    (1, '2026-03-02', FALSE),
    (1, '2026-03-03', TRUE),
    (2, '2026-03-01', TRUE),
    (2, '2026-03-02', TRUE),
    (3, '2026-03-01', FALSE);


-- 5. Eventos / Calendario
INSERT INTO Evento (titulo, descripcion, fecha, id_usuario, id_tarea)
VALUES
    ('Reunión con tutor', 'Revisión de avances del TFG', '2026-03-10', 1, 2),
    ('Entrega parcial', 'Subir avances a la plataforma', '2026-03-12', 1, 1);


INSERT INTO categoria (nombre, limite_mensual, id_usuario) VALUES
                                                               ('Comida', 300, 1),
                                                               ('Transporte', 150, 1),
                                                               ('Ocio', 200, 1),
                                                               ('Ahorro', 500, 1);

INSERT INTO transaccion (titulo, cantidad, tipo, fecha, id_usuario, id_categoria) VALUES
                                                                                      ('Sueldo', 1200.00, 'INGRESO', '2026-04-01', 1, 4),
                                                                                      ('Freelance', 300.00, 'INGRESO', '2026-04-10', 1, 4);



-- =========================
-- Indices para mejorar consultas frecuentes
-- =========================
CREATE INDEX idx_tarea_usuario ON Tarea(id_usuario);
CREATE INDEX idx_habito_usuario ON Habito(id_usuario);
CREATE INDEX idx_evento_fecha ON Evento(fecha);

SELECT * FROM Usuario;


SELECT * FROM Evento;