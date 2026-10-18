package inventario.controlador;

import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.modelo.Producto;
import inventario.persistencia.ProductoDAO;
import java.sql.SQLException;
import java.util.List;

/** Validaciones y operaciones de productos (CRUD, búsqueda y alertas). */
public class ProductoController {

    private final ProductoDAO dao = new ProductoDAO();

    public List<Producto> listar(String filtro) throws InventarioException {
        try {
            return dao.listar(filtro);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public List<Producto> listarBajoStock() throws InventarioException {
        try {
            return dao.listarBajoStock();
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void guardar(Producto p) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.PRODUCTOS);
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            throw new InventarioException("El código del producto es obligatorio.");
        }
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new InventarioException("El nombre del producto es obligatorio.");
        }
        if (p.getPrecio() < 0) throw new InventarioException("El precio no puede ser negativo.");
        if (p.getStock() < 0) throw new InventarioException("El stock no puede ser negativo.");
        if (p.getStockMinimo() < 0) throw new InventarioException("El stock mínimo no puede ser negativo.");
        if (p.getCategoria() == null) throw new InventarioException("Seleccione una categoría (créela primero si no existe).");
        if (p.getProveedor() == null) throw new InventarioException("Seleccione un proveedor (créelo primero si no existe).");
        String error = p.validarReglas(); // polimorfismo: cada tipo valida lo suyo
        if (error != null) throw new InventarioException(error);
        p.setCodigo(p.getCodigo().trim());
        p.setNombre(p.getNombre().trim());
        try {
            if (p.getId() == 0) dao.insertar(p); else dao.actualizar(p);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void eliminar(int id) throws InventarioException {
        Autorizacion.exigirEscritura(Modulo.PRODUCTOS);
        try {
            dao.eliminar(id);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }
}
