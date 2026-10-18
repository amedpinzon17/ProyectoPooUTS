package inventario.vista;

import inventario.controlador.AuthController;
import inventario.controlador.ProductoController;
import inventario.modelo.InventarioException;
import inventario.modelo.Modulo;
import inventario.modelo.Permisos;
import inventario.modelo.Producto;
import inventario.modelo.Usuario;
import inventario.persistencia.ConexionBD;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana principal: menú lateral con logo e ítems según el rol del usuario,
 * área de contenido (CardLayout) y barra de estado.
 */
public class MenuPrincipal extends JFrame {

    private static final Color FONDO = new Color(0x1F2D3D);
    private static final Color HOVER = new Color(0x2C3E50);
    private static final Color ACTIVO = new Color(0x1ABC9C);
    private static final String INICIO = "inicio";

    private final Usuario usuario;
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final Map<Modulo, Recargable> pantallas = new EnumMap<>(Modulo.class);
    private final Map<String, NavItem> items = new LinkedHashMap<>();
    private NavItem seleccionado;

    public MenuPrincipal(Usuario usuario) {
        super("Gestor de Inventario - POO");
        this.usuario = usuario;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { salir(); }
        });
        setIconImage(LogoIcon.imagen(64));
        setSize(1180, 680);
        setMinimumSize(new Dimension(980, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        contenedor.add(crearInicio(), INICIO);
        crearPantallas();
        add(crearMenuLateral(), BorderLayout.WEST);
        add(contenedor, BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
        mostrar(INICIO);
    }

    // ------------------------------------------------------------ pantallas según permisos
    private void crearPantallas() {
        String rol = usuario.getNombreRol();
        for (Modulo m : Modulo.values()) {
            if (!Permisos.puedeVer(rol, m)) continue;
            boolean lectura = !Permisos.puedeEscribir(rol, m);
            switch (m) {
                case USUARIOS:
                    registrar(m, new PanelUsuarios());
                    break;
                case PRODUCTOS:
                    PanelProductos pp = new PanelProductos();
                    pp.setSoloLectura(lectura);
                    registrar(m, pp);
                    break;
                case CATEGORIAS:
                    PanelCategorias pc = new PanelCategorias();
                    pc.setSoloLectura(lectura);
                    registrar(m, pc);
                    break;
                case PROVEEDORES:
                    PanelProveedores pv = new PanelProveedores();
                    pv.setSoloLectura(lectura);
                    registrar(m, pv);
                    break;
                case MOVIMIENTOS:
                    registrar(m, new PanelMovimientos(Permisos.soloVentas(rol)));
                    break;
                case REPORTES:
                    registrar(m, new PanelReportes());
                    break;
                default:
                    break;
            }
        }
    }

    private <T extends JPanel & Recargable> void registrar(Modulo m, T panel) {
        pantallas.put(m, panel);
        contenedor.add(panel, m.name());
    }

    private void mostrar(String clave) {
        if (!INICIO.equals(clave)) {
            Recargable r = pantallas.get(Modulo.valueOf(clave));
            if (r != null) r.recargar(); // datos siempre frescos desde la BD
        }
        cartas.show(contenedor, clave);
        if (seleccionado != null) seleccionado.setSeleccionado(false);
        seleccionado = items.get(clave);
        if (seleccionado != null) seleccionado.setSeleccionado(true);
    }

    // ------------------------------------------------------------ menú lateral
    private JPanel crearMenuLateral() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(FONDO);
        lateral.setPreferredSize(new Dimension(240, 0));

        // cabecera: logo + nombre + usuario
        JPanel cab = new JPanel();
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        cab.setBackground(FONDO);
        cab.setBorder(BorderFactory.createEmptyBorder(20, 16, 16, 16));
        JLabel logo = new JLabel(new LogoIcon(64));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel app = etiqueta("Inventario POO", 18, true, Color.WHITE);
        JLabel nombre = etiqueta(usuario.getNombreCompleto(), 12, false, new Color(0xBDC3C7));
        JLabel rol = etiqueta("Rol: " + usuario.getNombreRol(), 12, true, ACTIVO);
        cab.add(logo);
        cab.add(Box.createVerticalStrut(10));
        cab.add(app);
        cab.add(Box.createVerticalStrut(10));
        cab.add(nombre);
        cab.add(rol);
        lateral.add(cab, BorderLayout.NORTH);

        // ítems de navegación
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(FONDO);
        agregarItem(nav, INICIO, "Inicio");
        for (Modulo m : Modulo.values()) {
            if (pantallas.containsKey(m)) agregarItem(nav, m.name(), m.getTitulo());
        }
        lateral.add(nav, BorderLayout.CENTER);

        // salir
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(FONDO);
        NavItem salir = new NavItem("Salir", this::salir);
        pie.add(salir, BorderLayout.CENTER);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        lateral.add(pie, BorderLayout.SOUTH);
        return lateral;
    }

    private void agregarItem(JPanel nav, String clave, String texto) {
        NavItem it = new NavItem(texto, () -> mostrar(clave));
        items.put(clave, it);
        nav.add(it);
    }

    private static JLabel etiqueta(String texto, int tam, boolean negrita, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(negrita ? Font.BOLD : Font.PLAIN, (float) tam));
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ------------------------------------------------------------ inicio
    private JPanel crearInicio() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(60, 70, 40, 70));

        JLabel hola = new JLabel("Bienvenido, " + usuario.getNombreCompleto());
        hola.setFont(hola.getFont().deriveFont(Font.BOLD, 28f));
        JLabel rol = new JLabel("Rol: " + usuario.getNombreRol()
                + (usuario.getRol().getDescripcion() == null ? "" : " — " + usuario.getRol().getDescripcion()));
        rol.setFont(rol.getFont().deriveFont(14f));
        p.add(hola);
        p.add(Box.createVerticalStrut(8));
        p.add(rol);
        p.add(Box.createVerticalStrut(28));

        JLabel acceso = new JLabel("Sus accesos en el sistema:");
        acceso.setFont(acceso.getFont().deriveFont(Font.BOLD, 15f));
        p.add(acceso);
        p.add(Box.createVerticalStrut(8));
        for (Modulo m : Modulo.values()) {
            Permisos.Nivel n = Permisos.nivel(usuario.getNombreRol(), m);
            if (n == Permisos.Nivel.NINGUNO) continue;
            String detalle = n == Permisos.Nivel.LECTURA ? "solo consulta"
                    : (m == Modulo.MOVIMIENTOS && Permisos.soloVentas(usuario.getNombreRol()) ? "solo ventas"
                    : m == Modulo.REPORTES ? "consulta" : "acceso completo");
            JLabel l = new JLabel("•  " + m.getTitulo() + "  (" + detalle + ")");
            l.setFont(l.getFont().deriveFont(14f));
            p.add(l);
            p.add(Box.createVerticalStrut(4));
        }
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        boolean ok = ConexionBD.probar();
        JLabel est = new JLabel(ok ? "● Conectado a la base de datos" : "● Sin conexión a la base de datos");
        est.setForeground(ok ? new Color(0, 130, 60) : new Color(190, 30, 30));
        JLabel us = new JLabel("Sesión: " + usuario.getUsername() + " (" + usuario.getNombreRol() + ")");
        barra.add(est, BorderLayout.WEST);
        barra.add(us, BorderLayout.EAST);
        return barra;
    }

    // ------------------------------------------------------------ alertas y salida
    /** RF08: al iniciar sesión, avisa si hay productos agotándose. */
    public void mostrarAlertasIniciales() {
        try {
            List<Producto> bajos = new ProductoController().listarBajoStock();
            if (!bajos.isEmpty()) {
                StringBuilder sb = new StringBuilder("Productos con stock bajo o agotado:\n\n");
                for (Producto p : bajos) {
                    sb.append("• ").append(p.getNombre()).append("  (stock ").append(p.getStock())
                      .append(" / mínimo ").append(p.getStockMinimo()).append(")\n");
                }
                Mensajes.advertencia(this, sb.toString());
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    /** Cierra la sesión mostrando un mensaje informativo y termina la aplicación. */
    private void salir() {
        if (!Mensajes.confirmar(this, "¿Desea cerrar sesión y salir de la aplicación?")) return;
        new AuthController().logout();
        Mensajes.info(this, "Sesión cerrada correctamente.\nHasta pronto, " + usuario.getNombreCompleto() + ".");
        dispose();
        System.exit(0);
    }

    // ------------------------------------------------------------ ítem del menú
    private static class NavItem extends JLabel {

        private boolean seleccionado;
        private boolean encima;

        NavItem(String texto, Runnable accion) {
            super(texto);
            setOpaque(true);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.PLAIN, 15f));
            setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 10));
            setHorizontalAlignment(SwingConstants.LEFT);
            setPreferredSize(new Dimension(240, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            pintar();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { accion.run(); }
                @Override
                public void mouseEntered(MouseEvent e) { encima = true; pintar(); }
                @Override
                public void mouseExited(MouseEvent e) { encima = false; pintar(); }
            });
        }

        void setSeleccionado(boolean s) {
            seleccionado = s;
            pintar();
        }

        private void pintar() {
            setBackground(seleccionado ? ACTIVO : (encima ? HOVER : FONDO));
        }
    }
}
