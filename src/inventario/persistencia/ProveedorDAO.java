package inventario.persistencia;

import inventario.modelo.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla proveedor. */
public class ProveedorDAO {

    public List<Proveedor> listar(String filtro) throws SQLException {
        String sql = "SELECT * FROM proveedor WHERE nombre LIKE ? OR nit LIKE ? ORDER BY nombre";
        List<Proveedor> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ps.setString(2, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = new Proveedor();
                    p.setId(rs.getInt("id_proveedor"));
                    p.setNombre(rs.getString("nombre"));
                    p.setNit(rs.getString("nit"));
                    p.setTelefono(rs.getString("telefono"));
                    p.setEmail(rs.getString("email"));
                    p.setDireccion(rs.getString("direccion"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public void insertar(Proveedor p) throws SQLException {
        String sql = "INSERT INTO proveedor (nombre, nit, telefono, email, direccion) VALUES (?,?,?,?,?)";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, p);
            ps.executeUpdate();
        }
    }

    public void actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE proveedor SET nombre=?, nit=?, telefono=?, email=?, direccion=? WHERE id_proveedor=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, p);
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM proveedor WHERE id_proveedor = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void asignar(PreparedStatement ps, Proveedor p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getNit());
        ps.setString(3, p.getTelefono());
        ps.setString(4, p.getEmail());
        ps.setString(5, p.getDireccion());
    }
}
