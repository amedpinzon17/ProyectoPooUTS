package inventario.controlador;

import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.modelo.Movimiento;
import inventario.modelo.Permisos;
import inventario.modelo.Sesion;
import inventario.modelo.Usuario;
import inventario.persistencia.MovimientoDAO;
import java.sql.SQLException;
import java.util.List;

/** Registro de compras y ventas con actualización automática de stock. */
public class MovimientoController {

    private final MovimientoDAO dao = new MovimientoDAO();

    public List<Movimiento> listar() throws InventarioException {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }

    public void registrar(Movimiento m) throws InventarioException {
        Usuario u = Sesion.getUsuario();
        if (u == null) throw new InventarioException("Debe iniciar sesión para registrar movimientos.");
        if (!Permisos.puedeEscribir(u.getNombreRol(), Modulo.MOVIMIENTOS)) {
            throw new InventarioException("Su rol no tiene permiso para registrar movimientos.");
        }
        if (m.getTipo() == Movimiento.Tipo.COMPRA && Permisos.soloVentas(u.getNombreRol())) {
            throw new InventarioException("Su rol solo puede registrar ventas.");
        }
        m.setIdUsuario(u.getId());
        if (m.getIdProducto() == 0) throw new InventarioException("Seleccione un producto.");
        if (m.getCantidad() <= 0) throw new InventarioException("La cantidad debe ser mayor que cero.");
        if (m.getPrecioUnitario() < 0) throw new InventarioException("El precio no puede ser negativo.");
        try {
            dao.registrar(m);
        } catch (SQLException e) {
            throw InventarioException.desde(e);
        }
    }
}
