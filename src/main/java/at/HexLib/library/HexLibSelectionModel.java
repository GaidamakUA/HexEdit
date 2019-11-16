/*
 * Created on 16.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class HexLibSelectionModel {

    protected CopyOnWriteArrayList<Point> selectionPoints =
            new CopyOnWriteArrayList<Point>();

    Point markPositions =
            new Point(-1,
                    -1);
    /**
     * A value for the selectionMode property: select one contiguous range of indices at a
     * time.
     *
     * @see #setSelectionMode
     */
    public final int SINGLE_INTERVAL_SELECTION = 1;

    /**
     * A value for the selectionMode property: select one or more contiguous ranges of
     * indices at a time.
     *
     * @see #setSelectionMode
     */
    public final int MULTIPLE_INTERVAL_SELECTION = 2;

    private int selectionMode =
            MULTIPLE_INTERVAL_SELECTION;
    // SINGLE_INTERVAL_SELECTION;

    private HexLib hexEditor;

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    void addNewEndPoint(int calcCursorPos) {
        synchronized (markPositions) {
            /* check is required for Keyboard-interaction */
            if (calcCursorPos < 0) {
                calcCursorPos = 0;
            } else if (calcCursorPos >= hexEditor.buff.length) {
                calcCursorPos = hexEditor.buff.length - 1;
            }
            markPositions.y = calcCursorPos;
            if (selectionMode == SINGLE_INTERVAL_SELECTION) {
                selectionPoints.clear();
            }
            if (markPositions.x >= 0 && selectionPoints.size() > 0) {
                selectionPoints.remove(selectionPoints.size() - 1);
            }
            selectionPoints.addIfAbsent(markPositions);
        }
    }

    public void clear() {
        selectionPoints.clear();
        markPositions = new Point(-1, -1);
    }

    void addNewStartPoint(int pointYPressed) {
        synchronized (markPositions) {
            markPositions = new Point(-1, -1);
            markPositions.x = pointYPressed;
            markPositions.y = pointYPressed;
        }
    }

    int getStartPoint() {
        return markPositions.x;
    }

    int getEndPoint() {
        return markPositions.y;
    }

    void addStartAndEndPoint(int cursorPosition, int pointYPressed) {
        addNewStartPoint(cursorPosition);
        selectionPoints.addIfAbsent(markPositions);
        addNewEndPoint(pointYPressed);
    }

    boolean isPositionWithinMarkPos(int pos2Check) {
        boolean result = false;
        for (Point point2Check : selectionPoints) {
            if (point2Check.x < point2Check.y) {
                result = (point2Check.x <= pos2Check && pos2Check <= point2Check.y);
            } else if (point2Check.y < point2Check.x) {
                result = (point2Check.y <= pos2Check && pos2Check <= point2Check.x);
            } else {
                /* mark a single char (could ONLY occur through the #setSelectionIntervals */
                result = (pos2Check == point2Check.x);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public void dispose() {
        selectionPoints.clear();
    }

    /**
     * Add SelectionIntervals where each Point represents the starting and the ending of a
     * selection interval
     *
     * @param listMark
     */
    public void setSelectionIntervals(ArrayList<Point> listMark) {
        selectionPoints.clear();
        for (Point curPoint : listMark) {
            selectionPoints.add(curPoint);
        }
        hexEditor.repaint();
    }

    /**
     * required to update the selections on the GUI as well
     *
     * @param hexEditor
     */
    void setUpdatePainter(HexLib hexEditor) {
        this.hexEditor = hexEditor;
    }

    public ArrayList<Point> getSelectionList() {
        ArrayList<Point> retList = new ArrayList<Point>();
        for (Point curPoint : selectionPoints) {
            retList.add(curPoint);
        }
        return retList;
    }

    /**
     * Check if currently some selections are set
     *
     * @return true, if some selections are set
     */
    public boolean isEmpty() {
        return selectionPoints.isEmpty();
    }
}
