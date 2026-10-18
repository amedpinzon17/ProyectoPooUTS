package inventario.vista;

import inventario.controlador.UsuarioController;
import inventario.modelo.InventarioException;
import inventario.modelo.Rol;
import inventario.modelo.Usuario;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** CRUD de usuarios y asignación de roles (solo ADMINISTRADOR). */
public class PanelUsuarios extends PanelCrudBase {

    private final UsuarioController controlador = new UsuarioController();
    private final JTextField txtUsername = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JPasswordField txtClave = new JPasswordField();
    private final JComboBox<Rol> cmbRol = new JComboBox<>();
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);
    private List<Usuario> lista = new ArrayList<>();
    private int idActual = 0;

    public PanelUsuarios() {
        super("Usuarios y roles", new String[]{"ID", "Usuario", "Nombre completo", "Email", "Rol", "Estado",
            "Último acceso"});
        construirUI();
        recargar();
    }

    @Override
    protected JPanel crearFormulario() {
        JPanel p = nuevoFormulario();
        agregarCampo(p, 0, "Usuario *", txtUsername);
        agregarCampo(p, 1, "Nombre completo *", txtNombre);
        agregarCampo(p, 2, "Email", txtEmail);
        agregarCampo(p, 3, "Contraseña", txtClave);
        agregarCampo(p, 4, "Rol *", cmbRol);
        agregarCampo(p, 5, "Estado", chkActivo);
        return p;
    }

    @Override
    protected void cargarTabla(String filtro) {
        try {
            Rol sel = (Rol) cmbRol.getSelectedItem();
            cmbRol.removeAllItems();
            for (Rol r : controlador.listarRoles()) cmbRol.addItem(r);
            if (sel != null) {
                for (int i = 0; i < cmbRol.getItemCount(); i++) {
                    if (cmbRol.getItemAt(i).getId() == sel.getId()) cmbRol.setSelectedIndex(i);
                }
            }
            lista = controlador.listar(filtro);
            modeloTabla.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Usuario u : lista) {
                modeloTabla.addRow(new Object[]{u.getId(), u.getUsername(), u.getNombreCompleto(), u.getEmail(),
                    u.getNombreRol(), u.isActivo() ? "Activo" : "Inactivo",
                    u.getUltimoAcceso() == null ? "Nunca" : u.getUltimoAcceso().format(fmt)});
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void limpiar() {
        idActual = 0;
        txtUsername.setText("");
        txtNombre.setText("");
        txtEmail.setText("");
        txtClave.setText("");
        chkActivo.setSelected(true);
        tabla.clearSelection();
        txtUsername.requestFocus();
    }

    @Override
    protected void guardar() {
        Usuario u = new Usuario();
        u.setId(idActual);
        u.setUsername(txtUsername.getText());
        u.setNombreCompleto(txtNombre.getText());
        u.setEmail(txtEmail.getText().trim());
        u.setRol((Rol) cmbRol.getSelectedItem());
        u.setActivo(chkActivo.isSelected());
        try {
            controlador.guardar(u, new String(txtClave.getPassword()));
            Mensajes.info(this, "Usuario guardado correctamente.");
            limpiar();
            recargar();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    @Override
    protected void eliminar() {
        if (idActual == 0) {
            Mensajes.advertencia(this, "Seleccione un usuario de la tabla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el usuario seleccionado?")) return;
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
        Usuario u = lista.get(fila);
        idActual = u.getId();
        txtUsername.setText(u.getUsername());
        txtNombre.setText(u.getNombreCompleto());
        txtEmail.setText(u.getEmail() == null ? "" : u.getEmail());
        txtClave.setText(""); // vacía = conservar la contraseña actual
        chkActivo.setSelected(u.isActivo());
        for (int i = 0; i < cmbRol.getItemCount(); i++) {
            if (cmbRol.getItemAt(i).getId() == u.getRol().getId()) cmbRol.setSelectedIndex(i);
        }
    }
}
