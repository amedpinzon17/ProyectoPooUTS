package inventario.modelo;

/** Usuario que ha iniciado sesión (uno solo a la vez). */
public final class Sesion {

    private static Usuario actual;

    private Sesion() { }

    public static void iniciar(Usuario u) { actual = u; }
    public static void cerrar() { actual = null; }
    public static Usuario getUsuario() { return actual; }
}
