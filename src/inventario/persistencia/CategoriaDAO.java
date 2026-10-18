package inventario.persistencia;

import inventario.modelo.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla categoria. */
public class CategoriaDAO {

    public List<Categoria> listar(String filtro) throws SQLException {
        String sql = "SELECT id_categoria, nombre, descripcion FROM categoria WHERE nombre LIKE ? ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Categoria(rs.getInt(1), rs.getString(2), rs.getString(3)));
                }
            }
        }
        return lista;
    }

    public void insertar(Categoria cat) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getNombre());
            ps.setString(2, cat.getDescripcion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cat.setId(keys.getInt(1));
            }
        }
    }

    public void actualizar(Categoria cat) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cat.getNombre());
            ps.setString(2, cat.getDescripcion());
            ps.setInt(3, cat.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM categoria WHERE id_categoria = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
