package alerts.ui;

import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public final class Theme {
    private Theme() { }

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep Swing's default appearance if the system style is unavailable.
        }

        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        }
        FontUIResource font = new FontUIResource(base.deriveFont(Font.PLAIN, 13f));
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}
