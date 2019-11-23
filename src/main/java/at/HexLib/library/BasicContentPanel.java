/*
 * Created on 11.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class BasicContentPanel extends BasicPanel implements
        MouseListener,
        MouseMotionListener,
        MouseWheelListener {

    Color colorMarkPos = UIManager.getColor("TextField.selectionBackground");
    int leftAndShift = MouseEvent.BUTTON1_DOWN_MASK
            + MouseEvent.SHIFT_DOWN_MASK;
    int leftAndCtrl = MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.CTRL_DOWN_MASK;

    protected abstract int calcCursorPos(int x, int y);

    protected abstract void setFontObjects();

    public BasicContentPanel(HexLib he) {
        super(he);
        hasStripes = true;
        createZebraColors();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        hasTextFieldColors = true;

        setAutoscrolls(true);
        setFocusable(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        removeMouseListener(this);
        removeMouseMotionListener(this);
        removeMouseWheelListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int muX = e.getX();
        if (muX > getSize().width) {
            muX = getSize().width - border * 2;
        } else if (muX < 0) {
            muX = 0;
        }
        int mousePressedPos = calcCursorPos(muX, e.getY());
        if (e.getModifiersEx() == (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK)) {
            /* only allow the proper modifiers to be expand the selection */
            if (getSelectionModel().getStartPoint() < 0) {
                /*
                 * it occurs, that dragged mouse does not necessarily start with a MousePressed
                 * [2011-04-29]
                 */
                getSelectionModel().addStartAndEndPoint(getCursorPosition(),
                        mousePressedPos);
            } else {
                getSelectionModel().addNewEndPoint(mousePressedPos);
            }
        } else if ((e.getModifiersEx() ^ leftAndShift) == 0) {
            /* only allow the proper modifiers to be expand the selection */
            getSelectionModel().addNewEndPoint(mousePressedPos);
        } else if ((e.getModifiersEx() ^ leftAndCtrl) == 0) {
            getSelectionModel().addStartAndEndPoint(hexLib.getCursorPosition(),
                    mousePressedPos);
        }
        int muReleasePointY = e.getPoint().y;
        int unitsToScroll = 0;
        if (muReleasePointY < 0) {
            unitsToScroll = -1 + hexLib.getLines() * muReleasePointY / getSize().height;
        } else if (muReleasePointY > getSize().height) {
            unitsToScroll =
                    1 + hexLib.getLines() * (muReleasePointY - getSize().height)
                            / getSize().height;
        }
        scrollPane(unitsToScroll);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /*
         * without this workaround the current component might not get the focus and can never
         * get it
         */
        if (!hasFocus()) {
            hexLib.requestFocusInWindow();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int pointYPressed = calcCursorPos(e.getX(), e.getY());
        boolean canCursorBeChanged = false;
        boolean clearSelection = true;
        if (e.getModifiersEx() == (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK)) {
            /* Mouse button left ONLY, without any additional modifiers (=Keys) */
            getSelectionModel().clear();
            getSelectionModel().addNewStartPoint(pointYPressed);
            canCursorBeChanged = true;
        } else if ((e.getModifiersEx() ^ leftAndShift) == 0) {
            /* Mouse button left AND Shift, without any additional modifiers (=Keys) */
            getSelectionModel().addStartAndEndPoint(hexLib.getCursorPosition(),
                    pointYPressed);
            hexLib.repaint();
        } else if ((e.getModifiersEx() ^ leftAndCtrl) == 0) {
            canCursorBeChanged = true;
            clearSelection = false;
        }

        if (canCursorBeChanged && pointYPressed >= 0) {
            setCursorPositionInternal(pointYPressed, clearSelection);
            this.requestFocus();
            hexLib.repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int unitsToScroll = e.getUnitsToScroll();
        scrollPane(unitsToScroll);
    }

    protected boolean checkCurPosPaintable(int pos2Check) {
        return (pos2Check == getCursorPosition() && !getSelectionModel().isPositionWithinMarkPos(pos2Check));
    }

}
