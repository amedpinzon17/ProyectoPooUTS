package inventario.controlador;

import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.modelo.Rol;
import inventario.modelo.Sesion;
import inventario.modelo.Usuario;
import inventario.persistencia.RolDAO;
import inventario.persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.List;

/** CRUD de usuarios con las reglas de seguridad del sistema. */
public class UsuarioController {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final RolDAO rolDao = new RolDAO();

    public List<Usuario> listar(String filtro) throws InventarioException {
        try {
            return dao.listar(filtro);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public List<Rol> listarRoles() throws InventarioException {
        try {
            return rolDao.listar();
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    /** passwordPlano vacío al editar = conservar la contraseña actual. */
    public void guardar(Usuario u, String passwordPlano) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.USUARIOS);
        if (u.getUsername() == null || !u.getUsername().trim().matches("^[A-Za-z0-9._-]{4,30}$")) {
            throw new InventarioException("El usuario debe tener de 4 a 30 caracteres (letras, números, punto, guion).");
        }
        if (u.getNombreCompleto() == null || u.getNombreCompleto().trim().isEmpty()) {
            throw new InventarioException("El nombre completo es obligatorio.");
        }
        if (u.getEmail() != null && !u.getEmail().isEmpty()
                && !u.getEmail().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new InventarioException("El correo electrónico no tiene un formato válido.");
        }
        if (u.getRol() == null) throw new InventarioException("Seleccione un rol.");

        boolean nuevo = u.getId() == 0;
        boolean hayClave = passwordPlano != null && !passwordPlano.isEmpty();
        if (nuevo && !hayClave) throw new InventarioException("La contraseña es obligatoria para un usuario nuevo.");
        if (hayClave && passwordPlano.length() < 6) {
            throw new InventarioException("La contraseña debe tener al menos 6 caracteres.");
        }
        u.setUsername(u.getUsername().trim());
        u.setNombreCompleto(u.getNombreCompleto().trim());

        try {
            if (!nuevo) {
                Usuario anterior = dao.buscarPorId(u.getId());
                if (anterior == null) throw new InventarioException("El usuario ya no existe.");
                Usuario actual = Sesion.getUsuario();
                if (actual != null && actual.getId() == u.getId() && !u.isActivo()) {
                    throw new InventarioException("No puede desactivar su propio usuario.");
                }
                boolean eraAdminActivo = anterior.esAdministrador() && anterior.isActivo();
                boolean seguiraAdminActivo = Rol.ADMINISTRADOR.equals(u.getRol().getNombre()) && u.isActivo();
                if (eraAdminActivo && !seguiraAdminActivo && dao.contarAdministradoresActivos() <= 1) {
                    throw new InventarioException("Debe existir al menos un administrador activo.");
                }
            }
            if (hayClave) {
                u.setSalt(SeguridadUtil.generarSalt());
                u.setPasswordHash(SeguridadUtil.hash(u.getSalt(), passwordPlano));
            } else {
                u.setPasswordHash(null); // conservar la actual
            }
            if (nuevo) dao.insertar(u); else dao.actualizar(u);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void eliminar(int id) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.USUARIOS);
        Usuario actual = Sesion.getUsuario();
        if (actual != null && actual.getId() == id) {
            throw new InventarioException("No puede eliminar el usuario con el que inició sesión.");
        }
        try {
            Usuario objetivo = dao.buscarPorId(id);
            if (objetivo != null && objetivo.esAdministrador() && objetivo.isActivo()
                    && dao.contarAdministradoresActivos() <= 1) {
                throw new InventarioException("Debe existir al menos un administrador activo.");
            }
            dao.eliminar(id);
        } catch (SQLException e) {
            if (InventarioException.esLlaveForanea(e)) {
                throw new InventarioException("Este usuario tiene movimientos registrados y no se puede eliminar. "
                        + "Desactívelo en su lugar (casilla Activo).", e);
            }
            throw InventarioException.desde(e);
        }
    }
}
