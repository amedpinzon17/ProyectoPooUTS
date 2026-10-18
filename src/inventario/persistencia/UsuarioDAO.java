package inventario.persistencia;

import inventario.modelo.Rol;
import inventario.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla usuario. */
public class UsuarioDAO {

    private static final String BASE =
            "SELECT u.*, r.nombre AS rol_nombre, r.descripcion AS rol_desc "
          + "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol ";

    public List<Usuario> listar(String filtro) throws SQLException {
        String sql = BASE + "WHERE u.username LIKE ? OR u.nombre_completo LIKE ? OR r.nombre LIKE ? "
                          + "ORDER BY u.username";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) ps.setString(i, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE u.username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + "WHERE u.id_usuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (username, nombre_completo, email, salt, password_hash, id_rol, activo) "
                   + "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombreCompleto());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getSalt());
            ps.setString(5, u.getPasswordHash());
            ps.setInt(6, u.getRol().getId());
            ps.setBoolean(7, u.isActivo());
            ps.executeUpdate();
        }
    }

    /** Si passwordHash es null, la contraseña actual se conserva. */
    public void actualizar(Usuario u) throws SQLException {
        boolean cambiaClave = u.getPasswordHash() != null;
        String sql = "UPDATE usuario SET username=?, nombre_completo=?, email=?, id_rol=?, activo=?"
                   + (cambiaClave ? ", salt=?, password_hash=?" : "") + " WHERE id_usuario=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombreCompleto());
            ps.setString(3, u.getEmail());
            ps.setInt(4, u.getRol().getId());
            ps.setBoolean(5, u.isActivo());
            int i = 6;
            if (cambiaClave) {
                ps.setString(i++, u.getSalt());
                ps.setString(i++, u.getPasswordHash());
            }
            ps.setInt(i, u.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void registrarAcceso(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE usuario SET ultimo_acceso = datetime('now','localtime') WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int contarAdministradoresActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
                   + "WHERE r.nombre = 'ADMINISTRADOR' AND u.activo = 1";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setEmail(rs.getString("email"));
        u.setSalt(rs.getString("salt"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setActivo(rs.getBoolean("activo"));
        String t = rs.getString("ultimo_acceso");
        u.setUltimoAcceso(t == null || t.isEmpty() ? null : LocalDateTime.parse(t.replace(' ', 'T')));
        u.setRol(new Rol(rs.getInt("id_rol"), rs.getString("rol_nombre"), rs.getString("rol_desc")));
        return u;
    }
}
