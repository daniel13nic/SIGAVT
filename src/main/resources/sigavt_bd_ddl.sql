CREATE DATABASE IF NOT EXISTS sigavt_db;
USE sigavt_db;


-- ==========================================
-- Estructura de la Base de Datos
-- ==========================================

-- Catálogos
CREATE TABLE Perfil (
    id_perfil INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_perfil),
    CONSTRAINT uk_perfil_nombre UNIQUE (nombre)
);

CREATE TABLE Categoria (
    id_categoria INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_categoria),
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
);

CREATE TABLE Proveedor (
    id_proveedor INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    PRIMARY KEY (id_proveedor),
    CONSTRAINT uk_proveedor_nombre UNIQUE (nombre)
);

-- Tablas Dependientes
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT,
    id_perfil INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT fk_usuario_perfil FOREIGN KEY (id_perfil) REFERENCES Perfil(id_perfil)
);

CREATE TABLE Producto (
    id_producto INT AUTO_INCREMENT,
    id_categoria INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    unidad_medida VARCHAR(20) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_producto),
    CONSTRAINT uk_producto_nombre UNIQUE (nombre),
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES Categoria(id_categoria)
);

-- Tablas transaccionales e intermedias
CREATE TABLE Venta (
    id_venta INT AUTO_INCREMENT,
    id_producto INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_venta DATE NOT NULL,
    cantidad DECIMAL(10,3) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    total_venta DECIMAL(10,2) NOT NULL,
    anulada BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_venta),
    CONSTRAINT chk_venta_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_venta_preciounitario CHECK (precio_unitario >= 0),
    CONSTRAINT chk_venta_total CHECK (total_venta >= 0),
    CONSTRAINT fk_venta_producto FOREIGN KEY (id_producto) REFERENCES Producto(id_producto),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

CREATE TABLE producto_proveedor (
    id_producto INT NOT NULL,
    id_proveedor INT NOT NULL,
    PRIMARY KEY (id_producto, id_proveedor),
    CONSTRAINT fk_pp_producto FOREIGN KEY (id_producto) REFERENCES Producto(id_producto),
    CONSTRAINT fk_pp_proveedor FOREIGN KEY (id_proveedor) REFERENCES Proveedor(id_proveedor)
);


-- ==========================================
-- Datos de prueba

-- Inserción en Catálogos
INSERT INTO Perfil (id_perfil, nombre) VALUES
(1, 'Administrador'),
(2, 'Operador');

INSERT INTO Categoria (id_categoria, nombre) VALUES
(11, 'Carnes'),
(12, 'Bebidas'),
(13, 'Complementos'),
(14, 'Postres');

INSERT INTO Proveedor (id_proveedor, nombre, telefono) VALUES 
(1, 'Carnicería San Juan', '555-0101'), 
(2, 'Abarrotes El Zorro', '555-0202'),
(3, 'Distribuidora Pumas', '55-1234-5678');



-- Inserción en tablas dependientes
INSERT INTO Usuario (id_usuario, id_perfil, nombre, username, password, activo) VALUES
(1, 1, 'Daniel Nicolás', 'danieln', '$2a$10$XURPz3Q.y6r8H.D/E2e/n.z.7gI9bQ.fO9Wl/q.P/1J31k8wOqj9q', TRUE),
(2, 2, 'José Pérez Solano', 'JosePz', '$2a$10$XURPz3Q.y6r8H.D/E2e/n.z.7gI9bQ.fO9Wl/q.P/1J31k8wOqj9q', TRUE),
(3, 2, 'Juan Perez', 'JuanPz', '$2a$10$XURPz3Q.y6r8H.D/E2e/n.z.7gI9bQ.fO9Wl/q.P/1J31k8wOqj9q', TRUE),
(4, 2, 'David Martínez', 'davidM', '$2a$10$XURPz3Q.y6r8H.D/E2e/n.z.7gI9bQ.fO9Wl/q.P/1J31k8wOqj9q', TRUE);
-- Nota: La contraseña de prueba para todos los usuarios es 'admin123'

INSERT INTO Producto (id_producto, id_categoria, nombre, unidad_medida, precio, activo) VALUES
(8, 11, 'PASTOR', 'kg', 150.00, TRUE),
(9, 11, 'BISTEC', 'pza', 260.00, FALSE),
(10, 11, 'SUADERO', 'kg', 200.00, TRUE),
(11, 11, 'CHULETA', 'kg', 83.00, TRUE),
(12, 12, 'AGUA DE HORCHATA', 'L', 25.00, FALSE),
(13, 12, 'AGUA DE JAMAICA', 'L', 26.00, FALSE),
(16, 13, 'ORDEN DE CEBOLLAS', 'orden', 30.00, TRUE);

-- Inserción de Ventas
INSERT INTO Venta (id_venta, id_producto, id_usuario, fecha_venta, cantidad, precio_unitario, total_venta, anulada) VALUES
(1, 8, 1, '2026-08-25', 15.400, 150.00, 2310.00, FALSE),
(2, 10, 1, '2026-08-25', 4.300, 200.00, 860.00, FALSE),
(3, 11, 1, '2026-08-28', 1.000, 83.00, 83.00, FALSE),
(4, 11, 1, '2026-08-29', 4.000, 83.00, 332.00, FALSE),
(5, 10, 1, '2026-08-30', 4.300, 200.00, 860.00, FALSE);


-- Inserción de Productos - Proveedores
INSERT INTO producto_proveedor (id_producto, id_proveedor) VALUES 
(8, 1),  -- Pastor surtido por Carnicería San Juan
(10, 1), -- Suadero surtido por Carnicería San Juan
(12, 2), -- Agua de Horchata surtida por Abarrotes El Zorro
(11, 3); -- Chuleta surtida por Distribuidora Pumas