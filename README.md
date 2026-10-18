# 📦 Gestor de Inventario — Proyecto POO

Aplicación de escritorio en Java (Swing) con **inicio de sesión, usuarios y roles**, base de datos **SQLite** y proyecto de NetBeans (Ant) listo para ejecutar.

## 🚀 Cómo ejecutarlo en NetBeans

### 1. Abrir el proyecto
1. Abre NetBeans.
2. Ve a **File → Open Project**.
3. Selecciona la carpeta `InventarioPOO` que contiene `build.xml`, `src` y `nbproject`.
4. Haz clic en **Open Project**.

### 2. Configurar el JDK
1. Ve a **Tools → Java Platforms**.
2. Verifica que aparezca un JDK versión 17 o superior.
3. Si no aparece, instala un JDK compatible y agrégalo mediante **Add Platform**.
4. Haz clic derecho sobre el proyecto `InventarioPOO` y selecciona **Properties**.
5. Entra en **Libraries** y selecciona el JDK 17 o superior en **Java Platform**, si la opción está disponible.
6. Haz clic en **OK**.

### 3. Verificar la librería SQLite
1. Haz clic derecho sobre el proyecto y selecciona **Properties**.
2. Entra en **Libraries**.
3. Verifica que esté incluida la librería `lib/sqlite-jdbc-3.46.1.3.jar`.
4. Si no aparece, pulsa **Add JAR/Folder**, busca el archivo dentro de la carpeta `lib` del proyecto y selecciónalo.
5. Haz clic en **OK**.

### 4. Compilar y ejecutar
1. Haz clic derecho sobre el proyecto y selecciona **Clean and Build**.
2. Espera a que termine y comprueba que aparezca el mensaje `BUILD SUCCESSFUL`.
3. Haz clic derecho sobre el proyecto y selecciona **Run**.
4. Si NetBeans solicita la clase principal, selecciona `inventario.InventarioPOO`.
5. Espera a que aparezca la ventana de inicio de sesión.

### 5. Iniciar sesión

Utiliza las siguientes credenciales:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `Admin123` | ADMINISTRADOR |
| `almacen` | `Almacen123` | ALMACENISTA |
| `vendedor` | `Vendedor123` | VENDEDOR |

La base de datos **se crea automáticamente** la primera vez que se ejecuta la aplicación, siempre que la inicialización esté configurada correctamente. El archivo se guarda en `data/inventario.db`, con las tablas y los datos de prueba.

El driver de SQLite está incluido en `lib/sqlite-jdbc-3.46.1.3.jar`. No se necesita instalar un servidor de base de datos ni configurar una contraseña para SQLite.

Para volver a los datos de prueba, cierra la aplicación y elimina `data/inventario.db`. Esta acción también elimina los datos guardados localmente.

## 📂 Estructura del Proyecto

```text
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

Seguridad: contraseñas con `SHA-256(sal + contraseña)`; permisos validados en el controlador; no se puede eliminar ni desactivar al último administrador activo ni al usuario con sesión abierta; cada compra o venta guarda el usuario que la hizo.

## 🧰 Lista de Tecnologías Usadas
- **Java 17+** (POO: herencia, polimorfismo, encapsulamiento) · **Swing** (aspecto Nimbus)
- **JDBC** + **sqlite-jdbc 3.46.1.3** · **SQLite 3**
- **NetBeans** con proyecto **Apache Ant**
- **Graphviz** (diagrama entidad-relación)

## 🔧 Solución de problemas
- *«No se pudo abrir la base de datos»*: verifica que la aplicación tenga permisos de escritura en la ubicación de la base de datos y que `lib/sqlite-jdbc-3.46.1.3.jar` aparezca en Libraries del proyecto.
- *«La base de datos está ocupada»*: cierra DB Browser u otro programa que tenga abierto `inventario.db`.
- Si NetBeans pregunta por **Resolve Reference Problems** en las librerías, selecciona `lib/sqlite-jdbc-3.46.1.3.jar`.
- Si tu JDK es más antiguo que 17: instala un JDK compatible y configúralo en **Tools → Java Platforms** y en las propiedades del proyecto.
- Si aparece `BUILD FAILED`, revisa la pestaña **Output** de NetBeans para identificar el error de compilación.
- Si la aplicación no abre, comprueba que la clase principal sea `inventario.InventarioPOO`.

## 🚧 Pendiente
- Módulo de pedidos a proveedores (tablas `pedido` y `detalle_pedido` ya creadas).
- Edición de permisos por rol desde pantalla, exportación de reportes, pruebas funcionales y Javadoc completo.
