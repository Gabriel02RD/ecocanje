package ecocanje.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel personalizado con fondo de degradado.
 * Mejora el diseño visual de las vistas en EcoCanje.
 */
public class GradientPanel extends JPanel {
    private final Color color1;
    private final Color color2;

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (color1 == null || color2 == null) return;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
