package inventario.vista;

import java.awt.Component;
import javax.swing.JOptionPane;

/** Cuadros de diálogo reutilizables. */
public final class Mensajes {

    private Mensajes() { }

    public static void info(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String texto) {
        return JOptionPane.showConfirmDialog(padre, texto, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
