package inventario.vista;

import inventario.controlador.ProveedorController;
import inventario.modelo.InventarioException;
import inventario.modelo.Proveedor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** CRUD de proveedores (RF03, RF05). */
public class PanelProveedores extends PanelCrudBase {

    private final ProveedorController controlador = new ProveedorController();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtNit = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDireccion = new JTextField();
    private List<Proveedor> lista = new ArrayList<>();
    private int idActual = 0;

    public PanelProveedores() {
        super("Proveedores", new String[]{"ID", "Nombre", "NIT", "Teléfono", "Email", "Dirección"});
        construirUI();
        recargar();
    }

    @Override
    protected JPanel crearFormulario() {
        JPanel p = nuevoFormulario();
        agregarCampo(p, 0, "Nombre *", txtNombre);
        agregarCampo(p, 1, "NIT *", txtNit);
        agregarCampo(p, 2, "Teléfono", txtTelefono);
        agregarCampo(p, 3, "Email", txtEmail);
        agregarCampo(p, 4, "Dirección", txtDireccion);
        return p;
    }

    @Override
    protected void cargarTabla(String filtro) {
        try {
            lista = controlador.listar(filtro);
            modeloTabla.setRowCount(0);
            for (Proveedor p : lista) {
                modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getNit(),
                    p.getTelefono(), p.getEmail(), p.getDireccion()});
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void limpiar() {
        idActual = 0;
        txtNombre.setText("");
        txtNit.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        tabla.clearSelection();
        txtNombre.requestFocus();
    }

    @Override
    protected void guardar() {
        Proveedor p = new Proveedor();
        p.setId(idActual);
        p.setNombre(txtNombre.getText());
        p.setNit(txtNit.getText());
        p.setTelefono(txtTelefono.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setDireccion(txtDireccion.getText().trim());
        try {
            controlador.guardar(p);
            Mensajes.info(this, "Proveedor guardado correctamente.");
            limpiar();
            recargar();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void eliminar() {
        if (idActual == 0) {
            Mensajes.advertencia(this, "Seleccione un proveedor de la tabla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el proveedor seleccionado?")) return;
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
        Proveedor p = lista.get(fila);
        idActual = p.getId();
        txtNombre.setText(p.getNombre());
        txtNit.setText(p.getNit());
        txtTelefono.setText(p.getTelefono() == null ? "" : p.getTelefono());
        txtEmail.setText(p.getEmail() == null ? "" : p.getEmail());
        txtDireccion.setText(p.getDireccion() == null ? "" : p.getDireccion());
    }
}
