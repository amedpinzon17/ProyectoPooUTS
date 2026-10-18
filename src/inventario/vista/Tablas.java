package inventario.vista;

import java.awt.FontMetrics;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/** Utilidades para tablas. */
public final class Tablas {

    private Tablas() { }

    /** Ajusta el ancho de cada columna al contenido (con scroll horizontal si no cabe). */
    public static void ajustarColumnas(JTable tabla) {
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        FontMetrics fm = tabla.getFontMetrics(tabla.getFont());
        int filas = Math.min(tabla.getRowCount(), 200);
        for (int c = 0; c < tabla.getColumnCount(); c++) {
            TableColumn col = tabla.getColumnModel().getColumn(c);
            int ancho = fm.stringWidth(String.valueOf(col.getHeaderValue())) + 34;
            for (int r = 0; r < filas; r++) {
                Object v = tabla.getValueAt(r, c);
                ancho = Math.max(ancho, fm.stringWidth(v == null ? "" : v.toString()) + 22);
            }
            col.setPreferredWidth(Math.min(ancho, 280));
        }
    }
}
