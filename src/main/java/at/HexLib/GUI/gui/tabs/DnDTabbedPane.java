/*
 * Created on 09.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.tabs;

/**
 * Modified DnDTabbedPane.java
 * http://java-swing-tips.blogspot.com/2008/04/drag-and-drop-tabs-in-jtabbedpane.html
 * //http://terai.xrea.jp/Swing/DnDExportTabbedPane.html
 * <p>
 * originally written by Terai Atsuhiro.
 * so that tabs can be transfered from one pane to another.
 * eed3si9n.
 */

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DnDTabbedPane extends JTabbedPane {

    private static final int LINEWIDTH = 3;
    private final Rectangle lineRect = new Rectangle();
    public int dragTabIndex = -1;

    public static final class DropLocation extends TransferHandler.DropLocation {

        private final int index;

        private DropLocation(Point p, int index) {
            super(p);
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        private boolean dropable = true;

        public void setDropable(boolean flag) {
            dropable = flag;
        }

        public boolean isDropable() {
            return dropable;
        }
    }

    private void clickArrowButton(String actionKey) {
        ActionMap map = getActionMap();
        if (map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED,
                        null,
                        0,
                        0));
            }
        }
    }

    public static Rectangle rBackward = new Rectangle();
    public static Rectangle rForward = new Rectangle();
    private static int rwh = 20;
    private static int buttonsize = 30;             // XXX 30 is magic number of
    // scroll button size

    public void autoScrollTest(Point pt) {
        Rectangle r = getTabAreaBounds();
        int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP || tabPlacement == BOTTOM) {
            rBackward.setBounds(r.x, r.y, rwh, r.height);
            rForward.setBounds(r.x + r.width - rwh - buttonsize,
                    r.y,
                    rwh + buttonsize,
                    r.height);
        } else if (tabPlacement == LEFT || tabPlacement == RIGHT) {
            rBackward.setBounds(r.x, r.y, r.width, rwh);
            rForward.setBounds(r.x,
                    r.y + r.height - rwh - buttonsize,
                    r.width,
                    rwh + buttonsize);
        }
        if (rBackward.contains(pt)) {
            clickArrowButton("scrollTabsBackwardAction");
        } else if (rForward.contains(pt)) {
            clickArrowButton("scrollTabsForwardAction");
        }
    }

    public DnDTabbedPane() {
        super();
        Handler h = new Handler();
        addMouseListener(h);
        addMouseMotionListener(h);
        addPropertyChangeListener(h);
    }

    private DropMode dropMode = DropMode.INSERT;

    public DropLocation dropLocationForPoint(Point p) {
        boolean isTB =
                getTabPlacement() == JTabbedPane.TOP
                        || getTabPlacement() == JTabbedPane.BOTTOM;
        switch (dropMode) {
            case INSERT:
                Rectangle tar = getTabAreaBounds();
                Rectangle r;
                for (int i = 0; i < getTabCount(); i++) {
                    r = getBoundsAt(i);
                    if (isTB) {
                        r.x = r.x - r.width / 2;
                        r.y = tar.y;
                        r.height = tar.height;
                    } else {
                        r.x = tar.x;
                        r.y = tar.y - r.height / 2;
                        r.width = tar.width;
                    }
                    if (r.contains(p)) {
                        return new DropLocation(p, i);
                    }
                }
                if (tar.contains(p)) {
                    return new DropLocation(p, getTabCount());
                }
                break;
            case USE_SELECTION:
            case ON:
            case ON_OR_INSERT:
            default:
                assert false : "Unexpected drop mode";
        }
        return new DropLocation(p, -1);
    }

    private transient DropLocation dropLocation;

    public final DropLocation getDropLocation() {
        return dropLocation;
    }

    public Object setDropLocation(TransferHandler.DropLocation location) {
        Object retVal = null;
        DropLocation old = dropLocation;
        if (location instanceof DropLocation) {
            dropLocation = (DropLocation) location;
            firePropertyChange("dropLocation", old, dropLocation);
        }
        return retVal;
    }

    public void exportTab(int dragIndex, JTabbedPane target, int targetIndex) {
        System.out.println("exportTab");
        if (targetIndex < 0) {
            return;
        }

        Component cmp = getComponentAt(dragIndex);
        Container parent = target;
        while (parent != null) {
            if (cmp == parent) {
                return;
            }
            parent = parent.getParent();
        }

        Component tab = getTabComponentAt(dragIndex);
        String str = getTitleAt(dragIndex);
        Icon icon = getIconAt(dragIndex);
        String tip = getToolTipTextAt(dragIndex);
        boolean flg = isEnabledAt(dragIndex);
        remove(dragIndex);
        target.insertTab(str, icon, cmp, tip, targetIndex);
        target.setEnabledAt(targetIndex, flg);
        target.setTabComponentAt(targetIndex, tab);
        target.setSelectedIndex(targetIndex);
        if (tab != null && tab instanceof JComponent) {
            ((JComponent) tab).scrollRectToVisible(tab.getBounds());
        }
    }

    public void convertTab(int prev, int next) {
        System.out.println("convertTab");
        if (next < 0 || prev == next) {
            return;
        }
        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String str = getTitleAt(prev);
        Icon icon = getIconAt(prev);
        String tip = getToolTipTextAt(prev);
        boolean flg = isEnabledAt(prev);
        int tgtindex = prev > next ? next : next - 1;
        remove(prev);
        insertTab(str, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, flg);
        // When you drag'n'drop a disabled tab, it finishes enabled and selected.
        // pointed out by dlorde
        if (flg) {
            setSelectedIndex(tgtindex);
        }
        // I have a component in all tabs (jlabel with an X to close the tab) and when i move
        // a tab the component disappear.
        // pointed out by Daniel Dario Morales Salas
        setTabComponentAt(tgtindex, tab);
    }

    public void paintDropLine(Graphics g) {
        DropLocation loc = getDropLocation();
        if (loc == null || !loc.isDropable()) {
            return; // !loc.isInsert()) return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setColor(Color.RED);
        Rectangle r =
                SwingUtilities.convertRectangle(this,
                        getDropLineRect(loc),
                        getRootPane().getGlassPane());
        g2.fill(r);
    }

    public Rectangle getDropLineRect(DnDTabbedPane.DropLocation loc) {
        int index = loc.getIndex();
        if (index < 0) {
            lineRect.setRect(0, 0, 0, 0);
            return lineRect;
        }
        // Point pt = loc.getDropPoint();
        boolean isZero = index == 0;
        Rectangle r = getBoundsAt(isZero ? 0 : index - 1);
        if (getTabPlacement() == JTabbedPane.TOP
                || getTabPlacement() == JTabbedPane.BOTTOM) {
            lineRect.setRect(r.x - LINEWIDTH / 2 + r.width * (isZero ? 0 : 1),
                    r.y,
                    LINEWIDTH,
                    r.height);
        } else {
            lineRect.setRect(r.x,
                    r.y - LINEWIDTH / 2 + r.height * (isZero ? 0 : 1),
                    r.width,
                    LINEWIDTH);
        }
        return lineRect;
    }

    public Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        int xx = tabbedRect.x;
        int yy = tabbedRect.y;
        Component c = getSelectedComponent();
        if (c == null) {
            return tabbedRect;
        }
        Rectangle compRect = getSelectedComponent().getBounds();
        int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        } else if (tabPlacement == RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.translate(-xx, -yy);
        // tabbedRect.grow(2, 2);
        return tabbedRect;
    }

    private class Handler implements MouseInputListener, PropertyChangeListener { // ,

        // BeforeDrag

        private void repaintDropLocation(DropLocation loc) {
            Component c = getRootPane().getGlassPane();
            if (c instanceof GhostGlassPane) {
                GhostGlassPane glassPane = (GhostGlassPane) c;
                glassPane.setTargetTabbedPane(DnDTabbedPane.this);
                glassPane.repaint();
            }
        }

        // PropertyChangeListener
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if ("dropLocation".equals(propertyName)) {
                // System.out.println("propertyChange: dropLocation");
                repaintDropLocation(getDropLocation());
            }
        }

        // MouseListener
        @Override
        public void mousePressed(MouseEvent e) {
            DnDTabbedPane src = (DnDTabbedPane) e.getSource();
            if (src.getTabCount() <= 1) {
                startPt = null;
                return;
            }
            Point tabPt = e.getPoint(); // e.getDragOrigin();
            int idx = src.indexAtLocation(tabPt.x, tabPt.y);
            // disabled tab, null component problem.
            // pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
            startPt =
                    (idx < 0 || !src.isEnabledAt(idx) || src.getComponentAt(idx) == null) ? null
                            : tabPt;
        }

        private Point startPt;
        int gestureMotionThreshold = DragSource.getDragThreshold();

        // private final Integer gestureMotionThreshold =
        // (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");
        @Override
        public void mouseDragged(MouseEvent e) {
            Point tabPt = e.getPoint(); // e.getDragOrigin();
            if (startPt != null
                    && Math.sqrt(Math.pow(tabPt.x - startPt.x, 2)
                    + Math.pow(tabPt.y - startPt.y, 2)) > gestureMotionThreshold) {
                DnDTabbedPane src = (DnDTabbedPane) e.getSource();
                TransferHandler th = src.getTransferHandler();
                dragTabIndex = src.indexAtLocation(tabPt.x, tabPt.y);
                th.exportAsDrag(src, e, TransferHandler.MOVE);
                lineRect.setRect(0, 0, 0, 0);
                src.getRootPane().getGlassPane().setVisible(true);
                src.setDropLocation(new DropLocation(tabPt, -1));
                startPt = null;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    }

    /**
     * Detaches the <code>index</code> tab in a seperate frame. When the frame is closed,
     * the tab is automatically reinserted into the tabbedPane.
     *
     * @param index index of the tabbedPane to be detached
     */
    public void detachTab(int index) {
        if (index < 0 || index >= getTabCount()) {
            return;
        }

        new DetachedTabFrame(this, index);
    }

    public Component add(Component component, boolean isPlusTab) {
        int maxSize = Math.max(0, getTabCount() - 1);
        Component mu = super.add(component, maxSize);
        if (isPlusTab) {
            ButtonTabComponent componentPlus = new ButtonTabComponent(this, true);
            setTabComponentAt(maxSize, componentPlus);
            componentPlus.setPlusTabCompontents("Empty tab",
                    "<html><p align=center>Empty content</p>");
        } else {
            setTabComponentAt(maxSize, new ButtonTabComponent(getMe(), false));
        }
        return mu;
    }

    public DnDTabbedPane getMe() {
        return this;
    }

}
