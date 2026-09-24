package alerts;

import alerts.ui.AlertWindow;
import alerts.ui.Theme;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("A desktop display is required to open the GUI. "
                + "Backend tests can run without a display.");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            new AlertWindow().setVisible(true);
        });
    }
}
