package inventario.controlador;

import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.modelo.Proveedor;
import inventario.persistencia.ProveedorDAO;
import java.sql.SQLException;
import java.util.List;

/** Validaciones y operaciones de proveedores (CRUD). */
public class ProveedorController {

    private final ProveedorDAO dao = new ProveedorDAO();

    public List<Proveedor> listar(String filtro) throws InventarioException {
        try {
            return dao.listar(filtro);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void guardar(Proveedor p) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.PROVEEDORES);
        if (vacio(p.getNombre())) throw new InventarioException("El nombre del proveedor es obligatorio.");
        if (vacio(p.getNit())) throw new InventarioException("El NIT del proveedor es obligatorio.");
        if (!vacio(p.getEmail()) && !p.getEmail().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new InventarioException("El correo electrónico no tiene un formato válido.");
        }
        if (!vacio(p.getTelefono()) && !p.getTelefono().matches("^[0-9+\\- ]{7,20}$")) {
            throw new InventarioException("El teléfono solo debe tener números (mínimo 7 dígitos).");
        }
        try {
            if (p.getId() == 0) dao.insertar(p); else dao.actualizar(p);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void eliminar(int id) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.PROVEEDORES);
        try {
            dao.eliminar(id);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    private boolean vacio(String s) { return s == null || s.trim().isEmpty(); }
}
