package inventario.modelo;

/**
 * Matriz de permisos por rol.
 *
 *   Módulo              ADMINISTRADOR   ALMACENISTA   VENDEDOR
 *   Usuarios            completo        -             -
 *   Productos           completo        completo      solo lectura
 *   Categorías          completo        completo      -
 *   Proveedores         completo        completo      -
 *   Compras y ventas    completo        completo      solo ventas
 *   Reportes            completo        completo      completo (son de consulta)
 */
public final class Permisos {

    public enum Nivel { NINGUNO, LECTURA, COMPLETO }

    private Permisos() { }

    public static Nivel nivel(String rol, Modulo m) {
        if (rol == null) return Nivel.NINGUNO;
        switch (rol) {
            case Rol.ADMINISTRADOR:
                return Nivel.COMPLETO;
            case Rol.ALMACENISTA:
                return m == Modulo.USUARIOS ? Nivel.NINGUNO : Nivel.COMPLETO;
            case Rol.VENDEDOR:
                switch (m) {
                    case PRODUCTOS: return Nivel.LECTURA;
                    case MOVIMIENTOS: return Nivel.COMPLETO;
                    case REPORTES: return Nivel.COMPLETO;
                    default: return Nivel.NINGUNO;
                }
            default:
                return Nivel.NINGUNO;
        }
    }

    public static boolean puedeVer(String rol, Modulo m) {
        return nivel(rol, m) != Nivel.NINGUNO;
    }

    public static boolean puedeEscribir(String rol, Modulo m) {
        return nivel(rol, m) == Nivel.COMPLETO;
    }

    /** El vendedor solo puede registrar ventas, no compras. */
    public static boolean soloVentas(String rol) {
        return Rol.VENDEDOR.equals(rol);
    }
}
