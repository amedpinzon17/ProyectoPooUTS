package inventario.vista;

import inventario.controlador.CategoriaController;
import inventario.modelo.Categoria;
import inventario.modelo.InventarioException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** CRUD de categorías (RF02, RF05). */
public class PanelCategorias extends PanelCrudBase {

    private final CategoriaController controlador = new CategoriaController();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private List<Categoria> lista = new ArrayList<>();
    private int idActual = 0;

    public PanelCategorias() {
        super("Categorías", new String[]{"ID", "Nombre", "Descripción"});
        construirUI();
        recargar();
    }

    @Override
    protected JPanel crearFormulario() {
        JPanel p = nuevoFormulario();
        agregarCampo(p, 0, "Nombre *", txtNombre);
        agregarCampo(p, 1, "Descripción", txtDescripcion);
        return p;
    }

    @Override
    protected void cargarTabla(String filtro) {
        try {
            lista = controlador.listar(filtro);
            modeloTabla.setRowCount(0);
            for (Categoria c : lista) {
                modeloTabla.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion()});
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void limpiar() {
        idActual = 0;
        txtNombre.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
        txtNombre.requestFocus();
    }

    @Override
    protected void guardar() {
        Categoria c = new Categoria(idActual, txtNombre.getText(), txtDescripcion.getText().trim());
        try {
            controlador.guardar(c);
            Mensajes.info(this, "Categoría guardada correctamente.");
            limpiar();
            recargar();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void eliminar() {
        if (idActual == 0) {
            Mensajes.advertencia(this, "Seleccione una categoría de la tabla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar la categoría seleccionada?")) return;
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
        Categoria c = lista.get(fila);
        idActual = c.getId();
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion() == null ? "" : c.getDescripcion());
    }
}
