package inventario.controlador;

import inventario.modelo.InventarioException;
import inventario.modelo.Sesion;
import inventario.modelo.Usuario;
import inventario.persistencia.UsuarioDAO;
import java.sql.SQLException;

/** Inicio de sesión. */
public class AuthController {

    private final UsuarioDAO dao = new UsuarioDAO();

    /** Valida las credenciales, registra el acceso y abre la sesión. */
    public Usuario login(String username, String password) throws InventarioException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new InventarioException("Ingrese su usuario y su contraseña.");
        }
        try {
            Usuario u = dao.buscarPorUsername(username.trim());
            // Mismo mensaje si el usuario no existe o la clave es incorrecta (no revela cuál falló).
            if (u == null || !SeguridadUtil.coincide(u.getPasswordHash(),
                    SeguridadUtil.hash(u.getSalt(), password))) {
                throw new InventarioException("Usuario o contraseña incorrectos.");
            }
            if (!u.isActivo()) {
                throw new InventarioException("Su usuario está inactivo. Contacte al administrador.");
            }
            dao.registrarAcceso(u.getId());
            Sesion.iniciar(u);
            return u;
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void logout() {
        Sesion.cerrar();
    }
}
