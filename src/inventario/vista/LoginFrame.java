package inventario.vista;

import inventario.controlador.AuthController;
import inventario.modelo.InventarioException;
import inventario.modelo.Usuario;
import inventario.persistencia.ConexionBD;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Pantalla de inicio de sesión. */
public class LoginFrame extends JFrame {

    private final AuthController auth = new AuthController();
    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtClave = new JPasswordField(16);
    private final JLabel lblEstado = new JLabel(" ", SwingConstants.CENTER);

    public LoginFrame() {
        super("Gestor de Inventario - Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(LogoIcon.imagen(64));
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBorder(BorderFactory.createEmptyBorder(26, 40, 22, 40));

        JLabel logo = new JLabel(new LogoIcon(84), SwingConstants.CENTER);
        JLabel titulo = new JLabel("Gestor de Inventario", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        JLabel sub = new JLabel("Inicie sesión para continuar", SwingConstants.CENTER);
        JPanel cabecera = new JPanel(new BorderLayout(0, 6));
        cabecera.add(logo, BorderLayout.NORTH);
        cabecera.add(titulo, BorderLayout.CENTER);
        cabecera.add(sub, BorderLayout.SOUTH);
        raiz.add(cabecera, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Usuario"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        form.add(txtUsuario, g);
        g.gridx = 0; g.gridy = 1; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        form.add(new JLabel("Contraseña"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        form.add(txtClave, g);
        raiz.add(form, BorderLayout.CENTER);

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnSalir = new JButton("Salir");
        btnIngresar.addActionListener(e -> ingresar());
        btnSalir.addActionListener(e -> System.exit(0));
        txtClave.addActionListener(e -> ingresar());
        txtUsuario.addActionListener(e -> txtClave.requestFocus());
        getRootPane().setDefaultButton(btnIngresar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.add(btnIngresar);
        botones.add(btnSalir);
        lblEstado.setForeground(new Color(190, 30, 30));
        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.add(botones, BorderLayout.NORTH);
        sur.add(lblEstado, BorderLayout.CENTER);
        raiz.add(sur, BorderLayout.SOUTH);

        setContentPane(raiz);
        pack();
        setSize(Math.max(getWidth(), 400), getHeight() + 20);
        setLocationRelativeTo(null);

        if (!ConexionBD.probar()) {
            lblEstado.setText("No se pudo abrir la base de datos SQLite (revise la carpeta data)");
        }
    }

    private void ingresar() {
        try {
            Usuario u = auth.login(txtUsuario.getText(), new String(txtClave.getPassword()));
            dispose();
            MenuPrincipal menu = new MenuPrincipal(u);
            menu.setVisible(true);
            menu.mostrarAlertasIniciales();
        } catch (InventarioException e) {
            lblEstado.setText(e.getMessage());
            txtClave.setText("");
            txtClave.requestFocus();
        }
    }
}
