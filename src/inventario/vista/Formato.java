package inventario.vista;

import java.text.NumberFormat;
import java.util.Locale;

/** Formato de números para mostrar en pantalla (pesos colombianos). */
public final class Formato {

    private static final NumberFormat NUMERO = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"));

    static {
        NUMERO.setMaximumFractionDigits(2);
    }

    private Formato() { }

    /** 11500 -> "$ 11.500" */
    public static String moneda(double valor) {
        return "$ " + NUMERO.format(valor);
    }

    /** 4200.0 -> "4200" (para mostrar en un campo de texto editable). */
    public static String plano(double valor) {
        return valor == Math.rint(valor) ? String.valueOf((long) valor) : String.valueOf(valor);
    }
}
