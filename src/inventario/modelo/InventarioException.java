package inventario.modelo;

import java.sql.SQLException;

/**
 * Excepción controlada del sistema (RNF06). Todos los errores de validación,
 * de negocio o de base de datos llegan a la vista con un mensaje legible.
 */
public class InventarioException extends Exception {

    public InventarioException(String mensaje) {
        super(mensaje);
    }

    public InventarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    private static String texto(SQLException e) {
        return e.getMessage() == null ? "" : e.getMessage();
    }

    /** Violación de una restricción UNIQUE (nombre, código, NIT, usuario repetido). */
    public static boolean esDuplicado(SQLException e) {
        return texto(e).contains("UNIQUE constraint failed");
    }

    /** Violación de una llave foránea (registro en uso o referencia inexistente). */
    public static boolean esLlaveForanea(SQLException e) {
        return texto(e).contains("FOREIGN KEY constraint failed");
    }

    /** Traduce un error SQL a un mensaje entendible por el usuario. */
    public static InventarioException desde(SQLException e) {
        String m = texto(e);
        if (esDuplicado(e)) {
            return new InventarioException("Ya existe un registro con ese dato único (nombre, código, NIT o usuario).", e);
        }
        if (esLlaveForanea(e)) {
            return new InventarioException("No se puede completar la operación: el registro está relacionado con otros "
                    + "datos (por ejemplo, una categoría o proveedor con productos).", e);
        }
        if (m.contains("CHECK constraint failed") || m.contains("NOT NULL constraint failed")) {
            return new InventarioException("Hay datos inválidos o vacíos: revise los campos obligatorios y los números.", e);
        }
        if (m.contains("database is locked") || m.contains("SQLITE_BUSY")) {
            return new InventarioException("La base de datos está ocupada. Cierre otros programas que la tengan abierta "
                    + "(por ejemplo DB Browser) e intente de nuevo.", e);
        }
        if (m.contains("unable to open database") || m.contains("No suitable driver")) {
            return new InventarioException("No se pudo abrir la base de datos. Verifique la carpeta data/ y que "
                    + "lib/sqlite-jdbc esté en las librerías del proyecto.", e);
        }
        return new InventarioException("Error de base de datos: " + m, e);
    }
}
