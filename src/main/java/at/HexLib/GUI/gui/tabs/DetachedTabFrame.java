/*
 * Created on 24.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.tabs;

import at.HexLib.GUI.main.HexEditSample;
import at.HexLib.GUI.main.HexLibContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class DetachedTabFrame extends JFrame {

    private DnDTabbedPane tabbedPane;
    private float resizedSize = 0.9f;
    private int index;
    private int tabIndex;
    private JComponent tabComponent;
    private String title;
    private String toolTip;
    private Icon icon;

    public DetachedTabFrame(DnDTabbedPane tabbedPane, int index) {
        this.tabbedPane = tabbedPane;
        this.index = index;
        initialize();
        addListeners();

        setVisible(true);
        toFront();

        tabComponent.requestFocusInWindow();
    }

    private void initialize() {
        setIconImages(HexEditSample.masterReference.getIconImages());

        tabIndex = index;
        tabComponent = (JComponent) tabbedPane.getComponentAt(tabIndex);

        icon = tabbedPane.getIconAt(tabIndex);
        title = tabbedPane.getTitleAt(tabIndex);
        toolTip = tabbedPane.getToolTipTextAt(tabIndex);

        tabbedPane.removeTabAt(index);

        /* to have proper focused components in HexLibContainer */
        tabbedPane.requestFocus(false);

        // tabComponent.setPreferredSize(tabComponent.getSize());

        setTitle(title);
        getContentPane().add(tabComponent);
        /* set the DetachTab on same size as parentWindow */
        Window parentWindow = HexEditSample.masterReference;

        setSize(new Dimension((int) (parentWindow.getWidth() * resizedSize),
                (int) (parentWindow.getHeight() * resizedSize)));
        setLocation(new Point(parentWindow.getLocation().x
                + parentWindow.getWidth() - getWidth(),
                parentWindow.getLocation().y
                        + parentWindow.getHeight() - getHeight()));

    }

    private DetachedTabFrame getMe() {
        return this;
    }

    private void addListeners() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(@SuppressWarnings("unused")
                                              WindowEvent event) {
                String finalTitle = getMe().getTitle();
                getMe().removeWindowListener(this);
                getMe().dispose();

                int tabIndexTemp = Math.min(tabIndex, tabbedPane.getTabCount());
                tabbedPane.insertTab(finalTitle,
                        icon,
                        tabComponent,
                        toolTip,
                        tabIndexTemp);
                tabbedPane.setTabComponentAt(tabIndexTemp,
                        new ButtonTabComponent(tabbedPane, false));

                // c.setBorder(border);
                tabbedPane.setSelectedComponent(tabComponent);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                /* hack to show the master-window (=HexEditSample) if the Dialog is activated */

                Rectangle dirtyRegion =
                        javax.swing.RepaintManager.currentManager(HexEditSample.masterReference)
                                .getDirtyRegion(HexEditSample.masterReference.getRootPane());
                // System.out.println("Windows dirty region=" + dirtyRegion + "/"
                // + dirtyRegion.isEmpty());
                if (dirtyRegion != null && dirtyRegion.isEmpty()) {
                    // HexEditSample.masterReference.setVisible(false);
                    HexEditSample.masterReference.setVisible(true);
                    getMe().toFront();
                }
            }

        });

        WindowFocusListener windowFocusListener = new WindowFocusListener() {

            long start;

            long end;

            public void windowGainedFocus(@SuppressWarnings("unused")
                                                  WindowEvent e) {
                start = System.currentTimeMillis();
            }

            public void windowLostFocus(@SuppressWarnings("unused")
                                                WindowEvent e) {
                end = System.currentTimeMillis();
                long elapsed = end - start;
                // System.out.println(elapsed);
                if (elapsed < 100) {
                    getMe().toFront();
                }

                getMe().removeWindowFocusListener(this);
            }
        };

        /*
         * This is a small hack to avoid Windows GUI bug, that prevent a new window from
         * stealing focus (without this windowFocusListener, most of the time the new frame
         * would just blink from foreground to background). A windowFocusListener is added to
         * the frame, and if the time between the frame being in foreground and the frame
         * being in background is less that 100ms, it just brings the windows to the front
         * once again. Then it removes the windowFocusListener. Note that this hack would not
         * be required on Linux or UNIX based systems.
         */

        addWindowFocusListener(windowFocusListener);
    }

    public void dispose() {
        super.dispose();
    }

    public void setFileName(String filename, HexLibContainer hexLibContainer) {
        if (tabComponent instanceof HexLibContainer
                && hexLibContainer.equals(tabComponent)) {
            setTitle(filename);
        }
    }
}
