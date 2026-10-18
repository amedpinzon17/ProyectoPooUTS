# 📦 Gestor de Inventario — Proyecto POO

Aplicación de escritorio en Java (Swing) con **inicio de sesión, usuarios y roles**, base de datos **SQLite** y proyecto de NetBeans (Ant) listo para ejecutar.

## 🚀 Cómo ejecutarlo en NetBeans (sin instalar nada más)
1. NetBeans → **File → Open Project** → seleccione la carpeta `InventarioPOO` (el ícono de taza de café indica que es un proyecto Java).
2. Clic derecho sobre el proyecto → **Run** (clase principal `inventario.InventarioPOO`). Requiere JDK 17 o superior.
3. Inicie sesión con `admin` / `Admin123`.

La base de datos **se crea sola** la primera vez: aparece el archivo `data/inventario.db` con las tablas y los datos de prueba. El driver de SQLite ya viene incluido en `lib/sqlite-jdbc-3.46.1.3.jar` y configurado en las librerías del proyecto. No hay servidor ni contraseña de base de datos.

Para volver a los datos de prueba: cierre la aplicación y borre `data/inventario.db`.

## 📂 Estructura del Proyecto

```
InventarioPOO/
├── build.xml, manifest.mf, nbproject/     ← proyecto NetBeans (Ant)
├── lib/sqlite-jdbc-3.46.1.3.jar           ← driver JDBC de SQLite
├── sql/inventario_db_sqlite.sql           ← script de la base de datos
├── docs/diagrama_ER.png                   ← diagrama entidad-relación
├── src/
│   ├── db.properties                      ← ruta del archivo de base de datos
│   └── inventario/
│       ├── InventarioPOO.java             ← clase principal (abre el login)
│       ├── modelo/                        ← Categoria, Proveedor, Producto (abstracta), ProductoPerecedero,
│       │                                    ProductoNoPerecedero, Movimiento, Rol, Usuario, Permisos, Sesion
│       ├── persistencia/                  ← ConexionBD (JDBC/SQLite) y DAOs
│       ├── controlador/                   ← validaciones, reglas de negocio, autenticación y permisos
│       ├── vista/                         ← LoginFrame, MenuPrincipal, LogoIcon, PanelCrudBase y un panel por módulo
│       └── recursos/inventario_sqlite.sql ← copia del script que se ejecuta al crear la base
└── test/
```

- **Lista de Menú de Opciones** con logo e ítems de navegación (cada rol ve solo lo que puede usar): Inicio, Usuarios, Productos, Categorías, Proveedores, Compras y ventas, Reportes y alertas.
- **Vistas - CRUD** con tablas de ejemplo en Usuarios, Productos, Categorías y Proveedores.
- **Salir** con mensaje informativo de cierre de sesión.

### Usuarios y roles
| Módulo | ADMINISTRADOR | ALMACENISTA | VENDEDOR |
|---|---|---|---|
| Usuarios | completo | — | — |
| Productos | completo | completo | solo consulta |
| Categorías | completo | completo | — |
| Proveedores | completo | completo | — |
| Compras y ventas | completo | completo | solo ventas |
| Reportes y alertas | consulta | consulta | consulta |

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `Admin123` | ADMINISTRADOR |
| `almacen` | `Almacen123` | ALMACENISTA |
| `vendedor` | `Vendedor123` | VENDEDOR |

Seguridad: contraseñas con `SHA-256(sal + contraseña)`; permisos validados en el controlador; no se puede eliminar ni desactivar al último administrador activo ni al usuario con sesión abierta; cada compra o venta guarda el usuario que la hizo.

## 🧰 Lista de Tecnologías Usadas
- **Java 17+** (POO: herencia, polimorfismo, encapsulamiento) · **Swing** (aspecto Nimbus)
- **JDBC** + **sqlite-jdbc 3.46.1.3** · **SQLite 3**
- **NetBeans** con proyecto **Apache Ant**
- **Graphviz** (diagrama entidad-relación)

## 🔧 Solución de problemas
- *«No se pudo abrir la base de datos»*: verifique que exista la carpeta `data/` con permisos de escritura y que `lib/sqlite-jdbc-3.46.1.3.jar` aparezca en Libraries del proyecto.
- *«La base de datos está ocupada»*: cierre DB Browser u otro programa que tenga abierto `inventario.db`.
- Si NetBeans pregunta por "Resolve Reference Problems" en las librerías, apunte a `lib/sqlite-jdbc-3.46.1.3.jar`.
- Si su JDK es más antiguo que 17: Project Properties → Sources → Source/Binary Format.

## 🚧 Pendiente
- Módulo de pedidos a proveedores (tablas `pedido` y `detalle_pedido` ya creadas).
- Edición de permisos por rol desde pantalla, exportación de reportes, pruebas funcionales y Javadoc completo.
