package inventario;

import inventario.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada de la aplicación: abre la pantalla de inicio de sesión. */
public class InventarioPOO {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si falla, se usa el aspecto por defecto.
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
