-- =====================================================================
--  Proyecto POO - Gestor de Inventario
--  Script de base de datos para SQLite 3
--  La aplicación lo ejecuta sola la primera vez (crea data/inventario.db).
--  También puede abrirse en DB Browser for SQLite o en NetBeans.
--  Para reiniciar los datos: cierre la aplicación y borre data/inventario.db
-- =====================================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------------
-- ROL y USUARIO (control de acceso)
-- password_hash = SHA-256(salt + contraseña); nunca se guarda en texto plano
-- ---------------------------------------------------------------------
CREATE TABLE rol (
    id_rol       INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre       TEXT NOT NULL UNIQUE,
    descripcion  TEXT
);

CREATE TABLE usuario (
    id_usuario       INTEGER PRIMARY KEY AUTOINCREMENT,
    username         TEXT NOT NULL UNIQUE,
    nombre_completo  TEXT NOT NULL,
    email            TEXT,
    salt             TEXT NOT NULL,
    password_hash    TEXT NOT NULL,
    id_rol           INTEGER NOT NULL,
    activo           INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0,1)),
    ultimo_acceso    TEXT,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- CATEGORIA (RF02)
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
    id_categoria  INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre        TEXT NOT NULL UNIQUE,
    descripcion   TEXT
);

-- ---------------------------------------------------------------------
-- PROVEEDOR (RF03)
-- ---------------------------------------------------------------------
CREATE TABLE proveedor (
    id_proveedor  INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre        TEXT NOT NULL,
    nit           TEXT NOT NULL UNIQUE,
    telefono      TEXT,
    email         TEXT,
    direccion     TEXT
);

-- ---------------------------------------------------------------------
-- PRODUCTO (RF01, RF10)
-- PERECEDERO usa fecha_vencimiento; NO_PERECEDERO usa meses_garantia
-- ---------------------------------------------------------------------
CREATE TABLE producto (
    id_producto        INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo             TEXT NOT NULL UNIQUE,
    nombre             TEXT NOT NULL,
    precio             REAL NOT NULL CHECK (precio >= 0),
    stock              INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    stock_minimo       INTEGER NOT NULL DEFAULT 5 CHECK (stock_minimo >= 0),
    tipo               TEXT NOT NULL CHECK (tipo IN ('PERECEDERO','NO_PERECEDERO')),
    fecha_vencimiento  TEXT,
    meses_garantia     INTEGER,
    id_categoria       INTEGER NOT NULL,
    id_proveedor       INTEGER NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_producto_nombre ON producto(nombre);

-- ---------------------------------------------------------------------
-- MOVIMIENTO (RF06, RF07): compras y ventas
-- ---------------------------------------------------------------------
CREATE TABLE movimiento (
    id_movimiento    INTEGER PRIMARY KEY AUTOINCREMENT,
    id_producto      INTEGER NOT NULL,
    id_usuario       INTEGER NOT NULL,
    tipo             TEXT NOT NULL CHECK (tipo IN ('COMPRA','VENTA')),
    cantidad         INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario  REAL NOT NULL CHECK (precio_unitario >= 0),
    fecha            TEXT NOT NULL DEFAULT (datetime('now','localtime')),
    observacion      TEXT,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (id_usuario)  REFERENCES usuario(id_usuario)   ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- PEDIDO / DETALLE_PEDIDO (reposición a proveedores; para la siguiente entrega)
-- ---------------------------------------------------------------------
CREATE TABLE pedido (
    id_pedido     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_proveedor  INTEGER NOT NULL,
    fecha         TEXT NOT NULL DEFAULT (datetime('now','localtime')),
    estado        TEXT NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE','RECIBIDO','CANCELADO')),
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE detalle_pedido (
    id_detalle       INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pedido        INTEGER NOT NULL,
    id_producto      INTEGER NOT NULL,
    cantidad         INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario  REAL NOT NULL CHECK (precio_unitario >= 0),
    FOREIGN KEY (id_pedido)   REFERENCES pedido(id_pedido)     ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- VISTA de alertas (RF08): productos en o por debajo del stock mínimo
-- ---------------------------------------------------------------------
CREATE VIEW v_productos_bajo_stock AS
SELECT p.id_producto, p.codigo, p.nombre, p.stock, p.stock_minimo,
       c.nombre AS categoria, pr.nombre AS proveedor
FROM producto p
JOIN categoria c  ON c.id_categoria  = p.id_categoria
JOIN proveedor pr ON pr.id_proveedor = p.id_proveedor
WHERE p.stock <= p.stock_minimo;

-- ---------------------------------------------------------------------
-- DATOS DE PRUEBA
-- ---------------------------------------------------------------------
INSERT INTO rol (nombre, descripcion) VALUES
 ('ADMINISTRADOR', 'Acceso total: usuarios, inventario, compras/ventas y reportes'),
 ('ALMACENISTA',   'Gestiona productos, categorías, proveedores, compras/ventas y reportes'),
 ('VENDEDOR',      'Consulta productos, registra ventas y ve reportes');

-- Contraseñas de prueba:  admin / Admin123   almacen / Almacen123   vendedor / Vendedor123
INSERT INTO usuario (username, nombre_completo, email, salt, password_hash, id_rol, activo) VALUES
 ('admin', 'Administrador del Sistema', 'admin@inventario.com', 'sAltAdm1n0001', '4150d08347b530fb3427cedc23af1a9399ea34b3fe965e58253161078c9244d0', 1, 1),
 ('almacen', 'Laura Almacenista', 'almacen@inventario.com', 'sAltAlm4c0002', '1a5997fbb18392c34fc3d77f5a14dc6778f42a3be03bb48ff45745b908a8bf30', 2, 1),
 ('vendedor', 'Carlos Vendedor', 'vendedor@inventario.com', 'sAltV3nd0003', 'f5dcd1bce70df8e2b8da9013eceb2268920602b76deaf435caa7c3cbd924da0f', 3, 1);

INSERT INTO categoria (nombre, descripcion) VALUES
 ('Lácteos',     'Leche, quesos, yogures'),
 ('Abarrotes',   'Granos, enlatados y productos secos'),
 ('Aseo',        'Productos de limpieza del hogar'),
 ('Papelería',   'Útiles escolares y de oficina');

INSERT INTO proveedor (nombre, nit, telefono, email, direccion) VALUES
 ('Distribuidora Andina S.A.S.', '900123456-1', '6076345000', 'ventas@andina.com',   'Cra 27 # 45-10, Bucaramanga'),
 ('Alimentos del Oriente Ltda.', '800987654-3', '6076422211', 'pedidos@oriente.com', 'Calle 35 # 20-15, Bucaramanga'),
 ('Papeles y Más',               '901555777-9', '3101234567', 'info@papelesymas.com','Av. González Valencia # 12-30');

INSERT INTO producto (codigo, nombre, precio, stock, stock_minimo, tipo, fecha_vencimiento, meses_garantia, id_categoria, id_proveedor) VALUES
 ('LAC-001', 'Leche entera 1L',         4200,  40, 10, 'PERECEDERO',    date('now','+20 day'), NULL, 1, 2),
 ('LAC-002', 'Yogur fresa 200g',        2500,   4,  8, 'PERECEDERO',    date('now','+10 day'), NULL, 1, 2),
 ('ABA-001', 'Arroz 500g',              2800, 120, 30, 'NO_PERECEDERO', NULL, 0,  2, 1),
 ('ABA-002', 'Aceite vegetal 1L',      11500,   6, 10, 'NO_PERECEDERO', NULL, 0,  2, 1),
 ('ASE-001', 'Detergente líquido 1L',   9800,  25,  8, 'NO_PERECEDERO', NULL, 0,  3, 1),
 ('PAP-001', 'Cuaderno 100 hojas',      5500,  60, 15, 'NO_PERECEDERO', NULL, 0,  4, 3),
 ('PAP-002', 'Calculadora científica', 48000,   3,  5, 'NO_PERECEDERO', NULL, 12, 4, 3);

INSERT INTO movimiento (id_producto, id_usuario, tipo, cantidad, precio_unitario, observacion) VALUES
 (1, 1, 'COMPRA',  50, 3800, 'Compra inicial'),
 (1, 3, 'VENTA',   10, 4200, 'Venta mostrador'),
 (3, 2, 'COMPRA', 150, 2500, 'Compra inicial'),
 (3, 3, 'VENTA',   30, 2800, 'Venta mostrador');
