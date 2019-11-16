/*
 * Created on 07.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui;

/**
 * MySwing: Advanced Swing Utilites
 * Copyright (C) 2005  Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ComponentTitledBorder implements Border, MouseListener,
        MouseMotionListener, SwingConstants {

    private int offset = 7;
    private int edgeOffset = 2;
    private Component comp;
    private JComponent container;
    private Rectangle rect;
    private Border border;
    private boolean mouseEntered = false;
    private boolean leftOrientation = true;
    private JLabel additionalTitle;

    private boolean isBorderPainted = true;

    public ComponentTitledBorder(String additionalTitle,
                                 Component comp,
                                 JComponent container,
                                 Border border) {
        this(comp, container, border);
        leftOrientation = false;
        this.additionalTitle = new JLabel(additionalTitle);
        this.additionalTitle.setOpaque(true);

    }

    public ComponentTitledBorder(Component comp,
                                 JComponent container,
                                 Border border) {
        this.comp = comp;
        this.container = container;
        this.border = border;
        container.addMouseListener(this);
        container.addMouseMotionListener(this);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c,
                            Graphics g,
                            int x,
                            int y,
                            int width,
                            int height) {
        Insets borderInsets = border.getBorderInsets(c);
        Insets insets = getBorderInsets(c);
        int temp = (insets.top - borderInsets.top) / 2;
        if (isBorderPainted) {
            border.paintBorder(c,
                    g,
                    x + edgeOffset,
                    y + temp,
                    width - edgeOffset * 2,
                    height - temp);
        } else {
            border.paintBorder(c,
                    g,
                    x + edgeOffset,
                    y + temp,
                    width - edgeOffset * 2,
                    edgeOffset);
        }
        Dimension size = comp.getPreferredSize();
        if (leftOrientation) {
            rect = new Rectangle(offset, 0, size.width, size.height);
        } else {
            rect =
                    new Rectangle(width - size.width - offset, 0, size.width, size.height);
        }
        if (additionalTitle != null) {
            Rectangle muRect =
                    new Rectangle(offset,
                            0,
                            additionalTitle.getPreferredSize().width,
                            size.height);
            SwingUtilities.paintComponent(g, additionalTitle, (Container) c, muRect);
        }
        SwingUtilities.paintComponent(g, comp, (Container) c, rect);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Dimension size = comp.getPreferredSize();
        Insets insets = border.getBorderInsets(c);
        insets.top = Math.max(insets.top, size.height);
        return insets;
    }

    private void dispatchEvent(MouseEvent me) {
        if (rect != null && rect.contains(me.getX(), me.getY())) {
            dispatchEvent(me, me.getID());
        }
    }

    private void dispatchEvent(MouseEvent me, int id) {
        Point pt = me.getPoint();
        if (leftOrientation) {
            pt.translate(-offset, 0);
        } else {
            Dimension size = comp.getPreferredSize();
            pt.translate(-container.getSize().width + size.width, 0);
        }

        comp.setSize(rect.width, rect.height);
        comp.dispatchEvent(new MouseEvent(comp,
                id,
                me.getWhen(),
                me.getModifiers(),
                pt.x,
                pt.y,
                me.getClickCount(),
                me.isPopupTrigger(),
                me.getButton()));
        if (!comp.isValid()) {
            container.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        dispatchEvent(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (mouseEntered) {
            mouseEntered = false;
            dispatchEvent(me, MouseEvent.MOUSE_EXITED);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        dispatchEvent(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        dispatchEvent(me);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if (rect == null) {
            return;
        }

        if (mouseEntered == false && rect.contains(me.getX(), me.getY())) {
            mouseEntered = true;
            dispatchEvent(me, MouseEvent.MOUSE_ENTERED);
        } else if (mouseEntered == true) {
            if (rect.contains(me.getX(), me.getY()) == false) {
                mouseEntered = false;
                dispatchEvent(me, MouseEvent.MOUSE_EXITED);
            } else {
                dispatchEvent(me, MouseEvent.MOUSE_MOVED);
            }
        }
    }

    public boolean isBorderPainted() {
        return isBorderPainted;
    }

    public void setBorderPainted(boolean isBorderPainted) {
        this.isBorderPainted = isBorderPainted;
    }

}
