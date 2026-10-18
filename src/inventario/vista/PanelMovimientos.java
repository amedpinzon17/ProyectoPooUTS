package inventario.vista;

import inventario.controlador.MovimientoController;
import inventario.controlador.ProductoController;
import inventario.modelo.InventarioException;
import inventario.modelo.Movimiento;
import inventario.modelo.Producto;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/** Registro de compras y ventas con actualización automática de stock (RF06, RF07). */
public class PanelMovimientos extends JPanel implements Recargable {

    private final MovimientoController controlador = new MovimientoController();
    private final ProductoController productos = new ProductoController();

    private final JComboBox<Producto> cmbProducto = new JComboBox<>();
    private final JComboBox<Movimiento.Tipo> cmbTipo = new JComboBox<>(Movimiento.Tipo.values());
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextField txtPrecio = new JTextField(8);
    private final JTextField txtObservacion = new JTextField(18);
    private final JTable tabla;
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Producto", "Tipo", "Cantidad", "Precio unit.", "Total", "Usuario", "Observación"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) { return false; }
    };

    /** @param soloVentas true para el rol VENDEDOR (no puede registrar compras). */
    public PanelMovimientos(boolean soloVentas) {
        tabla = new JTable(modeloTabla);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Compras y ventas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nuevo movimiento"));
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> registrar());
        txtObservacion.setColumns(30);
        celda(form, 0, 0, new JLabel("Producto:"), 0);
        celda(form, 1, 0, cmbProducto, 1);
        celda(form, 2, 0, new JLabel("Tipo:"), 0);
        celda(form, 3, 0, cmbTipo, 0);
        celda(form, 4, 0, new JLabel("Cantidad:"), 0);
        celda(form, 5, 0, spnCantidad, 0);
        celda(form, 6, 0, new JLabel("Precio unit.:"), 0);
        celda(form, 7, 0, txtPrecio, 0);
        celda(form, 0, 1, new JLabel("Observación:"), 0);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 1; g.gridy = 1; g.gridwidth = 5; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 6);
        form.add(txtObservacion, g);
        g = new GridBagConstraints();
        g.gridx = 6; g.gridy = 1; g.gridwidth = 2; g.insets = new Insets(4, 6, 4, 6);
        form.add(btnRegistrar, g);

        tabla.setRowHeight(24);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.add(form, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        if (soloVentas) {
            cmbTipo.setSelectedItem(Movimiento.Tipo.VENTA);
            cmbTipo.setEnabled(false);
        }
        cmbProducto.addActionListener(e -> sugerirPrecio());
        recargar();
    }

    private void sugerirPrecio() {
        Producto p = (Producto) cmbProducto.getSelectedItem();
        if (p != null) txtPrecio.setText(Formato.plano(p.getPrecio()));
    }

    @Override
    public final void recargar() {
        try {
            cmbProducto.removeAllItems();
            for (Producto p : productos.listar("")) cmbProducto.addItem(p);
            modeloTabla.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Movimiento m : controlador.listar()) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getFecha().format(fmt), m.getNombreProducto(),
                    m.getTipo(), m.getCantidad(), Formato.moneda(m.getPrecioUnitario()), Formato.moneda(m.getTotal()),
                    m.getNombreUsuario(), m.getObservacion()});
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
        Tablas.ajustarColumnas(tabla);
    }

    private static void celda(JPanel p, int x, int y, java.awt.Component c, double peso) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y; g.weightx = peso; g.insets = new Insets(4, 6, 4, 6);
        g.fill = peso > 0 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.WEST;
        p.add(c, g);
    }

    private void registrar() {
        Producto p = (Producto) cmbProducto.getSelectedItem();
        if (p == null) {
            Mensajes.advertencia(this, "No hay productos registrados.");
            return;
        }
        Movimiento m = new Movimiento();
        m.setIdProducto(p.getId());
        m.setTipo((Movimiento.Tipo) cmbTipo.getSelectedItem());
        m.setCantidad((Integer) spnCantidad.getValue());
        m.setObservacion(txtObservacion.getText().trim());
        try {
            m.setPrecioUnitario(Double.parseDouble(txtPrecio.getText().trim()));
        } catch (NumberFormatException e) {
            Mensajes.error(this, "El precio unitario debe ser un número válido.");
            return;
        }
        try {
            controlador.registrar(m);
            Mensajes.info(this, "Movimiento registrado. El stock fue actualizado.");
            txtObservacion.setText("");
            spnCantidad.setValue(1);
            recargar();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }
}
