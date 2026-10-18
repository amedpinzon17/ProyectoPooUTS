package inventario.persistencia;

import inventario.modelo.Categoria;
import inventario.modelo.Producto;
import inventario.modelo.ProductoNoPerecedero;
import inventario.modelo.ProductoPerecedero;
import inventario.modelo.Proveedor;
import java.sql.Connection;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla producto (con sus subtipos). */
public class ProductoDAO {

    private static final String BASE =
            "SELECT p.*, c.nombre AS cat_nombre, pr.nombre AS prov_nombre "
          + "FROM producto p "
          + "JOIN categoria c ON c.id_categoria = p.id_categoria "
          + "JOIN proveedor pr ON pr.id_proveedor = p.id_proveedor ";

    /** RF04: busca por nombre, código, categoría o proveedor. */
    public List<Producto> listar(String filtro) throws SQLException {
        String sql = BASE + "WHERE p.nombre LIKE ? OR p.codigo LIKE ? OR c.nombre LIKE ? OR pr.nombre LIKE ? "
                          + "ORDER BY p.nombre";
        List<Producto> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) ps.setString(i, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** RF08: productos en o por debajo del stock mínimo. */
    public List<Producto> listarBajoStock() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE p.stock <= p.stock_minimo ORDER BY p.stock");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, precio, stock, stock_minimo, tipo, "
                   + "fecha_vencimiento, meses_garantia, id_categoria, id_proveedor) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, p);
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET codigo=?, nombre=?, precio=?, stock=?, stock_minimo=?, tipo=?, "
                   + "fecha_vencimiento=?, meses_garantia=?, id_categoria=?, id_proveedor=? WHERE id_producto=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, p);
            ps.setInt(11, p.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM producto WHERE id_producto = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---- utilidades privadas ----

    private void asignar(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getCodigo());
        ps.setString(2, p.getNombre());
        ps.setDouble(3, p.getPrecio());
        ps.setInt(4, p.getStock());
        ps.setInt(5, p.getStockMinimo());
        ps.setString(6, p.getTipo());
        if (p.getFechaVencimiento() == null) ps.setNull(7, Types.VARCHAR);
        else ps.setString(7, p.getFechaVencimiento().toString());   // aaaa-mm-dd
        if (p.getMesesGarantia() == null) ps.setNull(8, Types.INTEGER);
        else ps.setInt(8, p.getMesesGarantia());
        ps.setInt(9, p.getCategoria().getId());
        ps.setInt(10, p.getProveedor().getId());
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p;
        if ("PERECEDERO".equals(rs.getString("tipo"))) {
            ProductoPerecedero pp = new ProductoPerecedero();
            String d = rs.getString("fecha_vencimiento");
            pp.setFechaVencimiento(d == null || d.isEmpty() ? null : LocalDate.parse(d));
            p = pp;
        } else {
            ProductoNoPerecedero pn = new ProductoNoPerecedero();
            pn.setMesesGarantia(rs.getInt("meses_garantia"));
            p = pn;
        }
        p.setId(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getDouble("precio"));
        p.setStock(rs.getInt("stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setCategoria(new Categoria(rs.getInt("id_categoria"), rs.getString("cat_nombre"), null));
        Proveedor pr = new Proveedor();
        pr.setId(rs.getInt("id_proveedor"));
        pr.setNombre(rs.getString("prov_nombre"));
        p.setProveedor(pr);
        return p;
    }
}
