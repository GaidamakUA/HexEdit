/*
 * Created on 09.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.tabs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GhostGlassPane extends JPanel {

    private DnDTabbedPane tabbedPane;

    public GhostGlassPane(DnDTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        // System.out.println("new GhostGlassPane");
        setOpaque(false);
        // http://bugs.sun.com/view_bug.do?bug_id=6700748
        // setCursor(null); //XXX
    }

    private BufferedImage draggingGhost = null;

    public void setImage(BufferedImage draggingGhost) {
        this.draggingGhost = draggingGhost;
    }

    public void setTargetTabbedPane(DnDTabbedPane tab) {
        tabbedPane = tab;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        DnDTabbedPane.DropLocation dl = tabbedPane.getDropLocation();
        Point p = getMousePosition(true); // dl.getDropPoint();
        if (draggingGhost != null && dl != null && p != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            if (dl.isDropable()) {
                tabbedPane.paintDropLine(g2);
            }
            // Point p = SwingUtilities.convertPoint(tabbedPane, dl.getDropPoint(), this);
            double xx = p.getX() - (draggingGhost.getWidth(this) / 2d);
            double yy = p.getY() - (draggingGhost.getHeight(this) / 2d);
            g2.drawImage(draggingGhost, (int) xx, (int) yy, this);
        }
    }
}
