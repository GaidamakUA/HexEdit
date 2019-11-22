/*
 * Created on 09.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.tabs;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TabTransferHandler extends TransferHandler {

    private final DataFlavor localObjectFlavor;

    public TabTransferHandler() {
        System.out.println("TabTransferHandler");
        localObjectFlavor =
                new ActivationDataFlavor(DnDTabbedPane.class,
                        DataFlavor.javaJVMLocalObjectMimeType,
                        "DnDTabbedPane");
    }

    private DnDTabbedPane source = null;

    @Override
    protected Transferable createTransferable(JComponent c) {
        System.out.println("createTransferable");
        if (c instanceof DnDTabbedPane) {
            source = (DnDTabbedPane) c;
        }
        return new DataHandler(c, localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop() || !support.isDataFlavorSupported(localObjectFlavor)) {
            System.out.println("canImport:" + support.isDrop() + " "
                    + support.isDataFlavorSupported(localObjectFlavor));
            return false;
        }
        support.setDropAction(TransferHandler.MOVE);
        TransferHandler.DropLocation tdl = support.getDropLocation();
        Point pt = tdl.getDropPoint();
        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        target.autoScrollTest(pt);
        DnDTabbedPane.DropLocation dl = target.dropLocationForPoint(pt);
        int idx = dl.getIndex();
        boolean isDropable = false;
        if (target == source) {
            isDropable =
                    target.getTabAreaBounds().contains(pt) && idx >= 0
                            && idx != target.dragTabIndex && idx != target.dragTabIndex + 1;
        } else {
            if (source != null
                    && target != source.getComponentAt(source.dragTabIndex)) {
                isDropable = target.getTabAreaBounds().contains(pt) && idx >= 0;
            }
        }
        target.getRootPane().setGlassPane(glassPane);
        glassPane.setVisible(true);
        Component c = target.getRootPane().getGlassPane();
        c.setCursor(isDropable ? DragSource.DefaultMoveDrop
                : DragSource.DefaultMoveNoDrop);
        if (isDropable) {
            support.setShowDropLocation(true);
            dl.setDropable(true);
            target.setDropLocation(dl);
            return true;
        } else {
            support.setShowDropLocation(false);
            dl.setDropable(false);
            target.setDropLocation(dl);
            return false;
        }
    }

    private BufferedImage makeDragTabImage(DnDTabbedPane tabbedPane) {
        Rectangle rect = tabbedPane.getBoundsAt(tabbedPane.dragTabIndex);
        BufferedImage image =
                new BufferedImage(tabbedPane.getWidth(),
                        tabbedPane.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        tabbedPane.paint(g);
        g.dispose();
        if (rect.x < 0) {
            rect.translate(-rect.x, 0);
        }
        if (rect.y < 0) {
            rect.translate(0, -rect.y);
        }
        if (rect.x + rect.width > image.getWidth()) {
            rect.width = image.getWidth() - rect.x;
        }
        if (rect.y + rect.height > image.getHeight()) {
            rect.height = image.getHeight() - rect.y;
        }
        return image.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    private static GhostGlassPane glassPane;

    @Override
    public int getSourceActions(JComponent c) {
        System.out.println("getSourceActions");
        DnDTabbedPane src = (DnDTabbedPane) c;
        if (glassPane == null) {
            c.getRootPane().setGlassPane(glassPane = new GhostGlassPane(src));
        }
        if (src.dragTabIndex < 0) {
            return TransferHandler.NONE;
        }
        glassPane.setImage(makeDragTabImage(src));
        c.getRootPane().getGlassPane().setVisible(true);
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        System.out.println("importData");
        if (!canImport(support)) {
            return false;
        }

        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        DnDTabbedPane.DropLocation dl = target.getDropLocation();
        try {
            DnDTabbedPane source =
                    (DnDTabbedPane) support.getTransferable()
                            .getTransferData(localObjectFlavor);
            int index = dl.getIndex(); // boolean insert = dl.isInsert();
            if (target == source) {
                source.convertTab(source.dragTabIndex, index); // getTargetTabIndex(e.getLocation()));
            } else {
                source.exportTab(source.dragTabIndex, target, index);
            }
            return true;
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent src, Transferable data, int action) {
        System.out.println("exportDone");
        glassPane.setVisible(false);
        glassPane = null;
        source = null;
    }
}