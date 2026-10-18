package inventario.controlador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/** Utilidades de seguridad: sal aleatoria y hash SHA-256 de contraseñas. */
public final class SeguridadUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SeguridadUtil() { }

    /** Sal aleatoria de 16 caracteres hexadecimales. */
    public static String generarSalt() {
        byte[] b = new byte[8];
        RANDOM.nextBytes(b);
        return aHex(b);
    }

    /** SHA-256(salt + contraseña) en hexadecimal minúscula. */
    public static String hash(String salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return aHex(md.digest((salt + password).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /** Comparación en tiempo constante. */
    public static boolean coincide(String hashEsperado, String hashCalculado) {
        return MessageDigest.isEqual(
                hashEsperado.getBytes(StandardCharsets.UTF_8),
                hashCalculado.getBytes(StandardCharsets.UTF_8));
    }

    private static String aHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte x : bytes) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
