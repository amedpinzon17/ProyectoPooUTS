package inventario.controlador;

import inventario.modelo.Categoria;
import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.persistencia.CategoriaDAO;
import java.sql.SQLException;
import java.util.List;

/** Validaciones y operaciones de categorías (CRUD). */
public class CategoriaController {

    private final CategoriaDAO dao = new CategoriaDAO();

    public List<Categoria> listar(String filtro) throws InventarioException {
        try {
            return dao.listar(filtro);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void guardar(Categoria c) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.CATEGORIAS);
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            throw new InventarioException("El nombre de la categoría es obligatorio.");
        }
        c.setNombre(c.getNombre().trim());
        try {
            if (c.getId() == 0) dao.insertar(c); else dao.actualizar(c);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void eliminar(int id) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.CATEGORIAS);
        try {
            dao.eliminar(id);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }
}
