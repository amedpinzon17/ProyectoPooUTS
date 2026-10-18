package inventario.persistencia;

import inventario.modelo.InventarioException;
import inventario.modelo.Movimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de compras y ventas (RF06 / RF07). */
public class MovimientoDAO {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Movimiento> listar() throws SQLException {
        String sql = "SELECT m.*, p.nombre, u.username FROM movimiento m "
                   + "JOIN producto p ON p.id_producto = m.id_producto "
                   + "JOIN usuario u ON u.id_usuario = m.id_usuario "
                   + "ORDER BY m.fecha DESC, m.id_movimiento DESC";
        List<Movimiento> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id_movimiento"));
                m.setIdProducto(rs.getInt("id_producto"));
                m.setNombreProducto(rs.getString("nombre"));
                m.setIdUsuario(rs.getInt("id_usuario"));
                m.setNombreUsuario(rs.getString("username"));
                m.setTipo(Movimiento.Tipo.valueOf(rs.getString("tipo")));
                m.setCantidad(rs.getInt("cantidad"));
                m.setPrecioUnitario(rs.getDouble("precio_unitario"));
                m.setFecha(LocalDateTime.parse(rs.getString("fecha").replace(' ', 'T')));
                m.setObservacion(rs.getString("observacion"));
                lista.add(m);
            }
        }
        return lista;
    }

    /**
     * Registra el movimiento y actualiza el stock en una sola transacción.
     * Si es una venta y no hay stock suficiente, no se guarda nada.
     */
    public void registrar(Movimiento m) throws SQLException, InventarioException {
        try (Connection c = ConexionBD.getConnection()) {
            c.setAutoCommit(false);
            try {
                int delta = m.getTipo() == Movimiento.Tipo.COMPRA ? m.getCantidad() : -m.getCantidad();
                // El UPDATE solo se aplica si el stock resultante no queda negativo (operación atómica).
                int filas;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE producto SET stock = stock + ? WHERE id_producto = ? AND stock + ? >= 0")) {
                    ps.setInt(1, delta);
                    ps.setInt(2, m.getIdProducto());
                    ps.setInt(3, delta);
                    filas = ps.executeUpdate();
                }
                if (filas == 0) {
                    try (PreparedStatement ps = c.prepareStatement("SELECT stock FROM producto WHERE id_producto = ?")) {
                        ps.setInt(1, m.getIdProducto());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) throw new InventarioException("El producto no existe.");
                            throw new InventarioException("Stock insuficiente: disponible " + rs.getInt(1)
                                    + ", solicitado " + m.getCantidad() + ".");
                        }
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO movimiento (id_producto, id_usuario, tipo, cantidad, precio_unitario, fecha, observacion) "
                      + "VALUES (?,?,?,?,?,?,?)")) {
                    ps.setInt(1, m.getIdProducto());
                    ps.setInt(2, m.getIdUsuario());
                    ps.setString(3, m.getTipo().name());
                    ps.setInt(4, m.getCantidad());
                    ps.setDouble(5, m.getPrecioUnitario());
                    ps.setString(6, LocalDateTime.now().format(FORMATO));
                    ps.setString(7, m.getObservacion());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | InventarioException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
