package inventario.vista;

import inventario.controlador.CategoriaController;
import inventario.controlador.ProductoController;
import inventario.controlador.ProveedorController;
import inventario.modelo.Categoria;
import inventario.modelo.InventarioException;
import inventario.modelo.Producto;
import inventario.modelo.ProductoNoPerecedero;
import inventario.modelo.ProductoPerecedero;
import inventario.modelo.Proveedor;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

/** CRUD de productos (RF01, RF04, RF05, RF08, RF10). */
public class PanelProductos extends PanelCrudBase {

    private static final String PERECEDERO = "Perecedero";
    private static final String NO_PERECEDERO = "No perecedero";

    private final ProductoController controlador = new ProductoController();
    private final CategoriaController categorias = new CategoriaController();
    private final ProveedorController proveedores = new ProveedorController();

    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JTextField txtStockMin = new JTextField("5");
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{NO_PERECEDERO, PERECEDERO});
    private final JTextField txtVencimiento = new JTextField();
    private final JTextField txtGarantia = new JTextField("0");
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JComboBox<Proveedor> cmbProveedor = new JComboBox<>();

    private List<Producto> lista = new ArrayList<>();
    private int idActual = 0;

    public PanelProductos() {
        super("Productos", new String[]{"ID", "Código", "Nombre", "Precio", "Stock", "Mín.", "Tipo",
            "Detalle", "Categoría", "Proveedor"});
        construirUI();
        cmbTipo.addActionListener(e -> ajustarCamposPorTipo());
        ajustarCamposPorTipo();
        resaltarStockBajo();
        recargar();
    }

    @Override
    protected JPanel crearFormulario() {
        JPanel p = nuevoFormulario();
        agregarCampo(p, 0, "Código *", txtCodigo);
        agregarCampo(p, 1, "Nombre *", txtNombre);
        agregarCampo(p, 2, "Precio *", txtPrecio);
        agregarCampo(p, 3, "Stock *", txtStock);
        agregarCampo(p, 4, "Stock mínimo *", txtStockMin);
        agregarCampo(p, 5, "Tipo", cmbTipo);
        agregarCampo(p, 6, "Vence (aaaa-mm-dd)", txtVencimiento);
        agregarCampo(p, 7, "Garantía (meses)", txtGarantia);
        agregarCampo(p, 8, "Categoría *", cmbCategoria);
        agregarCampo(p, 9, "Proveedor *", cmbProveedor);
        return p;
    }

    /** Polimorfismo en la vista: cada tipo habilita sus propios campos. */
    private void ajustarCamposPorTipo() {
        boolean perecedero = PERECEDERO.equals(cmbTipo.getSelectedItem());
        txtVencimiento.setEnabled(perecedero);
        txtGarantia.setEnabled(!perecedero);
        if (!perecedero) txtVencimiento.setText("");
    }

    private void cargarCombos() {
        try {
            Categoria catSel = (Categoria) cmbCategoria.getSelectedItem();
            Proveedor provSel = (Proveedor) cmbProveedor.getSelectedItem();
            cmbCategoria.removeAllItems();
            for (Categoria c : categorias.listar("")) cmbCategoria.addItem(c);
            cmbProveedor.removeAllItems();
            for (Proveedor p : proveedores.listar("")) cmbProveedor.addItem(p);
            seleccionarPorId(cmbCategoria, catSel == null ? 0 : catSel.getId());
            seleccionarPorId(cmbProveedor, provSel == null ? 0 : provSel.getId());
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private <T> void seleccionarPorId(JComboBox<T> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            T item = combo.getItemAt(i);
            int itemId = item instanceof Categoria ? ((Categoria) item).getId() : ((Proveedor) item).getId();
            if (itemId == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    protected void cargarTabla(String filtro) {
        cargarCombos();
        try {
            lista = controlador.listar(filtro);
            modeloTabla.setRowCount(0);
            for (Producto p : lista) {
                modeloTabla.addRow(new Object[]{p.getId(), p.getCodigo(), p.getNombre(), Formato.moneda(p.getPrecio()),
                    p.getStock(), p.getStockMinimo(), p.getTipo().equals("PERECEDERO") ? PERECEDERO : NO_PERECEDERO,
                    p.getDetalleEspecial(), p.getCategoria().getNombre(), p.getProveedor().getNombre()});
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    /** RF08: pinta de rojo las filas con stock en o bajo el mínimo. */
    private void resaltarStockBajo() {
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int fila, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, fila, col);
                if (!sel) {
                    boolean bajo = fila < lista.size() && lista.get(fila).estaBajoMinimo();
                    c.setBackground(bajo ? new Color(255, 220, 220) : Color.WHITE);
                }
                return c;
            }
        });
    }

    @Override
    protected void limpiar() {
        idActual = 0;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMin.setText("5");
        txtVencimiento.setText("");
        txtGarantia.setText("0");
        cmbTipo.setSelectedItem(NO_PERECEDERO);
        tabla.clearSelection();
        txtCodigo.requestFocus();
    }

    @Override
    protected void guardar() {
        Producto p;
        try {
            if (PERECEDERO.equals(cmbTipo.getSelectedItem())) {
                ProductoPerecedero pp = new ProductoPerecedero();
                String f = txtVencimiento.getText().trim();
                pp.setFechaVencimiento(f.isEmpty() ? null : LocalDate.parse(f));
                p = pp;
            } else {
                ProductoNoPerecedero pn = new ProductoNoPerecedero();
                pn.setMesesGarantia(Integer.parseInt(txtGarantia.getText().trim()));
                p = pn;
            }
            p.setId(idActual);
            p.setCodigo(txtCodigo.getText());
            p.setNombre(txtNombre.getText());
            p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            p.setStock(Integer.parseInt(txtStock.getText().trim()));
            p.setStockMinimo(Integer.parseInt(txtStockMin.getText().trim()));
            p.setCategoria((Categoria) cmbCategoria.getSelectedItem());
            p.setProveedor((Proveedor) cmbProveedor.getSelectedItem());
        } catch (NumberFormatException e) {
            Mensajes.error(this, "Precio, stock, stock mínimo y garantía deben ser números válidos.");
            return;
        } catch (DateTimeParseException e) {
            Mensajes.error(this, "La fecha de vencimiento debe tener el formato aaaa-mm-dd (ej. 2026-12-31).");
            return;
        }
        try {
            controlador.guardar(p);
            Mensajes.info(this, "Producto guardado correctamente.");
            limpiar();
            recargar();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void eliminar() {
        if (idActual == 0) {
            Mensajes.advertencia(this, "Seleccione un producto de la tabla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el producto seleccionado?")) return;
        try {
            controlador.eliminar(idActual);
            limpiar();
            recargar();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void alSeleccionar(int fila) {
        Producto p = lista.get(fila);
        idActual = p.getId();
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(Formato.plano(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        txtStockMin.setText(String.valueOf(p.getStockMinimo()));
        cmbTipo.setSelectedItem(p.getTipo().equals("PERECEDERO") ? PERECEDERO : NO_PERECEDERO);
        txtVencimiento.setText(p.getFechaVencimiento() == null ? "" : p.getFechaVencimiento().toString());
        txtGarantia.setText(p.getMesesGarantia() == null ? "0" : String.valueOf(p.getMesesGarantia()));
        seleccionarPorId(cmbCategoria, p.getCategoria().getId());
        seleccionarPorId(cmbProveedor, p.getProveedor().getId());
    }
}
