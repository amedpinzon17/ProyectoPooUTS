package inventario.vista;

import inventario.controlador.MovimientoController;
import inventario.controlador.ProductoController;
import inventario.modelo.InventarioException;
import inventario.modelo.Movimiento;
import inventario.modelo.Producto;
import inventario.modelo.ProductoPerecedero;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Reportes y alertas del inventario (RF08, RF09). */
public class PanelReportes extends JPanel implements Recargable {

    private static final String[] REPORTES = {
        "Productos con stock bajo (alerta)",
        "Perecederos por vencer (30 días) o vencidos",
        "Inventario valorizado",
        "Historial de movimientos"
    };

    private final ProductoController productos = new ProductoController();
    private final MovimientoController movimientos = new MovimientoController();
    private final JComboBox<String> cmbReporte = new JComboBox<>(REPORTES);
    private final JLabel lblResumen = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int f, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelReportes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Reportes y alertas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnGenerar = new JButton("Generar");
        btnGenerar.addActionListener(e -> generar());
        barra.add(new JLabel("Reporte:"));
        barra.add(cmbReporte);
        barra.add(btnGenerar);
        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(barra, BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        tabla.setRowHeight(24);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);
        generar();
    }

    @Override
    public final void recargar() {
        generar();
    }

    public final void generar() {
        try {
            switch (cmbReporte.getSelectedIndex()) {
                case 0: stockBajo(); break;
                case 1: porVencer(); break;
                case 2: valorizado(); break;
                default: historial(); break;
            }
            Tablas.ajustarColumnas(tabla);
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void stockBajo() throws InventarioException {
        modelo.setDataVector(new Object[0][0], new Object[]{"Código", "Producto", "Stock", "Mínimo", "Proveedor"});
        int n = 0;
        for (Producto p : productos.listarBajoStock()) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getStock(), p.getStockMinimo(),
                p.getProveedor().getNombre()});
            n++;
        }
        lblResumen.setText(n + " producto(s) por debajo o en el stock mínimo.");
    }

    private void porVencer() throws InventarioException {
        modelo.setDataVector(new Object[0][0], new Object[]{"Código", "Producto", "Vencimiento", "Días restantes", "Stock"});
        int n = 0;
        for (Producto p : productos.listar("")) {
            if (p instanceof ProductoPerecedero) {
                ProductoPerecedero pp = (ProductoPerecedero) p;
                if (pp.getFechaVencimiento() != null && pp.diasParaVencer() <= 30) {
                    modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), pp.getFechaVencimiento(),
                        pp.estaVencido() ? "VENCIDO" : pp.diasParaVencer(), p.getStock()});
                    n++;
                }
            }
        }
        lblResumen.setText(n + " producto(s) perecedero(s) vencidos o próximos a vencer.");
    }

    private void valorizado() throws InventarioException {
        modelo.setDataVector(new Object[0][0], new Object[]{"Código", "Producto", "Precio", "Stock", "Valor total"});
        double total = 0;
        for (Producto p : productos.listar("")) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), Formato.moneda(p.getPrecio()), p.getStock(), Formato.moneda(p.valorInventario())});
            total += p.valorInventario();
        }
        lblResumen.setText("Valor total del inventario: " + Formato.moneda(total));
    }

    private void historial() throws InventarioException {
        modelo.setDataVector(new Object[0][0], new Object[]{"Fecha", "Producto", "Tipo", "Cantidad", "Total", "Usuario"});
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        double compras = 0, ventas = 0;
        for (Movimiento m : movimientos.listar()) {
            modelo.addRow(new Object[]{m.getFecha().format(fmt), m.getNombreProducto(), m.getTipo(),
                m.getCantidad(), Formato.moneda(m.getTotal()), m.getNombreUsuario()});
            if (m.getTipo() == Movimiento.Tipo.COMPRA) compras += m.getTotal(); else ventas += m.getTotal();
        }
        lblResumen.setText("Total compras: " + Formato.moneda(compras) + "   |   Total ventas: " + Formato.moneda(ventas));
    }
}
