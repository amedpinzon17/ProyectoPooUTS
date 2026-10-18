package inventario.persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Conexión JDBC a SQLite (RF11).
 * La base de datos es un archivo (data/inventario.db) que se crea y se llena con datos
 * de prueba automáticamente la primera vez, por lo que no hay que instalar ningún servidor.
 */
public final class ConexionBD {

    private static final String SCRIPT = "/inventario/recursos/inventario_sqlite.sql";
    private static final Properties CONFIG = new Properties();
    private static boolean inicializada = false;

    static {
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                CONFIG.load(in);
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer db.properties: " + e.getMessage());
        }
    }

    private ConexionBD() { }

    private static File archivoBD() {
        return new File(CONFIG.getProperty("db.path", "data/inventario.db")).getAbsoluteFile();
    }

    /** Ruta del archivo de base de datos (para mostrarla en mensajes). */
    public static String getRuta() {
        return archivoBD().getPath();
    }

    /** Abre una conexión nueva. Quien la use debe cerrarla (try-with-resources). */
    public static Connection getConnection() throws SQLException {
        inicializar();
        return abrir();
    }

    private static Connection abrir() throws SQLException {
        Properties props = new Properties();
        props.setProperty("foreign_keys", "true");   // SQLite no valida llaves foráneas si no se activa
        return DriverManager.getConnection("jdbc:sqlite:" + archivoBD().getPath(), props);
    }

    /** Crea la carpeta, el archivo y las tablas si todavía no existen. */
    private static synchronized void inicializar() throws SQLException {
        if (inicializada) return;
        File archivo = archivoBD();
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists() && !carpeta.mkdirs()) {
            throw new SQLException("No se pudo crear la carpeta " + carpeta.getPath());
        }
        try (Connection c = abrir()) {
            if (!existeTabla(c, "usuario")) {
                ejecutarScript(c);
            }
        }
        inicializada = true;
    }

    private static boolean existeTabla(Connection c, String tabla) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT 1 FROM sqlite_master WHERE type='table' AND name='" + tabla + "'")) {
            return rs.next();
        }
    }

    /** Ejecuta inventario_sqlite.sql (tablas + datos de prueba) en una sola transacción. */
    private static void ejecutarScript(Connection c) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = ConexionBD.class.getResourceAsStream(SCRIPT)) {
            if (in == null) throw new SQLException("No se encontró el script " + SCRIPT);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().startsWith("--")) sb.append(linea).append('\n');
            }
        } catch (IOException e) {
            throw new SQLException("No se pudo leer el script de la base de datos", e);
        }
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try (Statement st = c.createStatement()) {
            for (String sentencia : sb.toString().split(";")) {
                if (!sentencia.trim().isEmpty()) st.execute(sentencia);
            }
            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    /** Prueba rápida para mostrar el estado en la ventana. */
    public static boolean probar() {
        try (Connection c = getConnection()) {
            return !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
