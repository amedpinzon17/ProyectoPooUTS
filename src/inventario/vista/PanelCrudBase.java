package inventario.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Plantilla de pantalla CRUD: buscador arriba, formulario a la izquierda,
 * tabla a la derecha y botones Nuevo / Guardar / Eliminar.
 * Cada módulo (Categorías, Proveedores, Productos) hereda y completa los métodos abstractos.
 */
public abstract class PanelCrudBase extends JPanel implements Recargable {

    protected final DefaultTableModel modeloTabla;
    protected final JTable tabla;
    protected final JTextField txtBuscar = new JTextField(18);
    protected final JButton btnNuevo = new JButton("Nuevo");
    protected final JButton btnGuardar = new JButton("Guardar");
    protected final JButton btnEliminar = new JButton("Eliminar");
    private final String titulo;

    protected PanelCrudBase(String titulo, String[] columnas) {
        this.titulo = titulo;
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        this.tabla = new JTable(modeloTabla);
    }

    /** Debe llamarse al final del constructor de la subclase (ya con sus campos creados). */
    protected final void construirUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ---- encabezado + buscador ----
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 20f));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Ver todos");
        btnBuscar.addActionListener(e -> recargar());
        btnTodos.addActionListener(e -> { txtBuscar.setText(""); recargar(); });
        txtBuscar.addActionListener(e -> recargar());
        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buscador.add(new JLabel("Buscar:"));
        buscador.add(txtBuscar);
        buscador.add(btnBuscar);
        buscador.add(btnTodos);
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(lblTitulo, BorderLayout.WEST);
        norte.add(buscador, BorderLayout.EAST);
        add(norte, BorderLayout.NORTH);

        // ---- formulario + botones ----
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBorder(BorderFactory.createTitledBorder("Datos"));
        JPanel arriba = new JPanel(new BorderLayout());   // el formulario queda pegado arriba
        arriba.add(crearFormulario(), BorderLayout.NORTH);
        izquierda.add(arriba, BorderLayout.CENTER);
        izquierda.add(botones, BorderLayout.SOUTH);
        izquierda.setPreferredSize(new Dimension(340, 0));
        add(izquierda, BorderLayout.WEST);

        // ---- tabla ----
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(false);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                alSeleccionar(tabla.getSelectedRow());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    /** Vuelve a consultar la base de datos con el texto del buscador. */
    @Override
    public final void recargar() {
        cargarTabla(txtBuscar.getText().trim());
        Tablas.ajustarColumnas(tabla);
    }

    /** Rol sin permiso de escritura: la pantalla solo consulta. */
    public final void setSoloLectura(boolean soloLectura) {
        btnNuevo.setEnabled(!soloLectura);
        btnGuardar.setEnabled(!soloLectura);
        btnEliminar.setEnabled(!soloLectura);
    }

    /** Agrega una fila etiqueta + campo a un formulario con GridBagLayout. */
    protected static void agregarCampo(JPanel panel, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.gridy = fila;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(etiqueta), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, g);
    }

    protected static JPanel nuevoFormulario() {
        return new JPanel(new GridBagLayout());
    }

    protected abstract JPanel crearFormulario();
    protected abstract void cargarTabla(String filtro);
    protected abstract void limpiar();
    protected abstract void guardar();
    protected abstract void eliminar();
    protected abstract void alSeleccionar(int fila);
}
