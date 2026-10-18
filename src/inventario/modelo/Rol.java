package inventario.modelo;

/** Rol de un usuario del sistema (define a qué módulos puede entrar). */
public class Rol {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ALMACENISTA = "ALMACENISTA";
    public static final String VENDEDOR = "VENDEDOR";

    private int id;
    private String nombre;
    private String descripcion;

    public Rol() { }

    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return nombre; }
}
