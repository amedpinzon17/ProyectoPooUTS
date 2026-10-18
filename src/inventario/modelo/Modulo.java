package inventario.modelo;

/** Módulos (pantallas) de la aplicación. */
public enum Modulo {
    USUARIOS("Usuarios"),
    PRODUCTOS("Productos"),
    CATEGORIAS("Categorías"),
    PROVEEDORES("Proveedores"),
    MOVIMIENTOS("Compras y ventas"),
    REPORTES("Reportes y alertas");

    private final String titulo;

    Modulo(String titulo) { this.titulo = titulo; }

    public String getTitulo() { return titulo; }
}
