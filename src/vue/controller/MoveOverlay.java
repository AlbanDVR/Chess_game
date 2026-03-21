package vue.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import java.awt.Graphics;

public class MoveOverlay extends JComponent {
    private ImageIcon icon;
    private int x;
    private int y;

    public MoveOverlay() {
        setOpaque(false);
    }

    public void update(ImageIcon icon, int x, int y) {
        this.icon = icon;
        this.x = x;
        this.y = y;
        repaint();
    }

    public void clear() {
        icon = null;
        setVisible(false);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (icon != null) {
            g.drawImage(icon.getImage(), x, y, null);
        }
    }
}

