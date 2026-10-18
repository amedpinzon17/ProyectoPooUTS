package inventario.persistencia;

import inventario.modelo.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla rol. */
public class RolDAO {

    public List<Rol> listar() throws SQLException {
        List<Rol> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id_rol, nombre, descripcion FROM rol ORDER BY id_rol");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Rol(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        }
        return lista;
    }
}
