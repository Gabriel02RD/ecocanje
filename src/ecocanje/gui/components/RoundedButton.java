package ecocanje.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Botón personalizado con esquinas redondeadas y efecto hover.
 * Utilizado en la interfaz EcoCanje.
 */
public class RoundedButton extends JButton {
    private final Color backgroundColor;
    private final Color hoverColor;
    private final Color pressedColor;
    private final int cornerRadius;

    public RoundedButton(String text, Color bgColor, Color hoverColor, int radius) {
        super(text);
        this.backgroundColor = bgColor;
        this.hoverColor = hoverColor;
        this.pressedColor = bgColor.darker();
        this.cornerRadius = radius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(ecocanje.gui.DesignConstants.BUTTON_FONT);
        setOpaque(false);

        // Efectos hover
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(backgroundColor);
            }
        });
        // Inicial background to ensure correct paint
        setBackground(backgroundColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(getBackground());
        }

        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width = Math.max(d.width, ecocanje.gui.DesignConstants.BUTTON_WIDTH);
        d.height = Math.max(d.height, ecocanje.gui.DesignConstants.BUTTON_HEIGHT);
        return d;
    }
}
