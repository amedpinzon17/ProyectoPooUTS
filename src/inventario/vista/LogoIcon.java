package inventario.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

/** Logo de la aplicación (una caja de inventario) dibujado con Java2D, sin archivos externos. */
public class LogoIcon implements Icon {

    private final int tam;

    public LogoIcon(int tam) { this.tam = tam; }

    @Override
    public int getIconWidth() { return tam; }

    @Override
    public int getIconHeight() { return tam; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        double s = tam / 100.0;

        // fondo redondeado
        g2.setColor(new Color(0x1ABC9C));
        g2.fillRoundRect(0, 0, tam, tam, (int) (28 * s), (int) (28 * s));

        // cubo isométrico (3 caras)
        Polygon arriba = poly(s, 50, 18, 82, 36, 50, 54, 18, 36);
        Polygon izq = poly(s, 18, 36, 50, 54, 50, 88, 18, 70);
        Polygon der = poly(s, 82, 36, 50, 54, 50, 88, 82, 70);
        g2.setColor(Color.WHITE);
        g2.fillPolygon(arriba);
        g2.setColor(new Color(0xD5F5EE));
        g2.fillPolygon(izq);
        g2.setColor(new Color(0xA3E4D7));
        g2.fillPolygon(der);
        g2.dispose();
    }

    private static Polygon poly(double s, int... p) {
        Polygon pol = new Polygon();
        for (int i = 0; i < p.length; i += 2) pol.addPoint((int) (p[i] * s), (int) (p[i + 1] * s));
        return pol;
    }

    /** Imagen para el ícono de la ventana. */
    public static BufferedImage imagen(int tam) {
        BufferedImage img = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        new LogoIcon(tam).paintIcon(null, g, 0, 0);
        g.dispose();
        return img;
    }
}
