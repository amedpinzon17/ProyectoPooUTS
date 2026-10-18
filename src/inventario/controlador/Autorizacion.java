package inventario.controlador;

import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.modelo.Permisos;
import inventario.modelo.Sesion;
import inventario.modelo.Usuario;

/** Verifica en el controlador (no solo en la pantalla) que el rol pueda modificar un módulo. */
public final class Autorizacion {

    private Autorizacion() { }

    public static void exigirEscritura(Modulo modulo) throws InventarioException {
        Usuario u = Sesion.getUsuario();
        if (u == null) throw new InventarioException("Debe iniciar sesión.");
        if (!Permisos.puedeEscribir(u.getNombreRol(), modulo)) {
            throw new InventarioException("Su rol (" + u.getNombreRol() + ") no tiene permiso para modificar "
                    + modulo.getTitulo() + ".");
        }
    }
}
