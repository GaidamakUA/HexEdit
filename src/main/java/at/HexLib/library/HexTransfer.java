/*
 * Created on 22.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.Executors;

public class HexTransfer {

    Clipboard clipboard = Toolkit.getDefaultToolkit()
            .getSystemClipboard();
    private HexLib he;

    public static enum insertActions {
        OVERWRITE, INSERTandDELETE_SELECTION
    }

    ;

    public static enum copyStringAction {
        BINARY, BINARY_AS_STRING
    }

    ;

    private copyStringAction copyAction = copyStringAction.BINARY_AS_STRING;
    private insertActions insertAction = insertActions.INSERTandDELETE_SELECTION;
    private boolean inActionDelete;

    public HexTransfer(HexLib he) {
        this.he = he;
    }

    public void copyContent2Clipboard() {
        ArrayList<Point> listMarks = he.getSelectionModel().getSelectionList();
        if (listMarks == null || listMarks.size() == 0) {
            /* nothing selected */
            return;
        }
        /* to have all selection points in a sorted order */
        TreeSet<Integer> selPoints = new TreeSet<Integer>();
        for (Point curSelection : listMarks) {
            for (int startSel = curSelection.x; startSel <= curSelection.y; startSel++) {
                selPoints.add(startSel);
            }
        }

        byte[] copyBytes = new byte[selPoints.size()];
        int count = 0;
        for (Integer curPos : selPoints) {
            copyBytes[count++] = he.buff[curPos];
        }
        HexTransferable copy2ClipBoard =
                new HexTransferable(copyBytes, getCopyAction());
        clipboard.setContents(copy2ClipBoard, he);
    }

    public synchronized void pasteContentFromClipboard() {
        Transferable clipData = clipboard.getContents(clipboard);
        if (clipData != null) {
            try {
                if (he.getSelectionModel().getSelectionList().size() > 1) {
                    /*
                     * insert with multiple selection is not supported (cause it is unknown where to
                     * insert the content
                     */
                    Toolkit.getDefaultToolkit().beep();
                } else if (clipData.isDataFlavorSupported(HexTransferable.hexFlavor)) {
                    byte[] mu =
                            (byte[]) clipData.getTransferData(HexTransferable.hexFlavor);
                    insertContent(mu);
                } else if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String s =
                            (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                    insertContent(s.getBytes());
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (Exception e) {
                System.err.println("Problems getting data:" + e);
            }
        }
    }

    private void insertContent(byte[] content) {
        if (content != null && content.length >= 0) {
            if (getInsertAction() == insertActions.OVERWRITE) {
                int cursorPos = he.getCursorPosition();
                int end = Math.min(cursorPos + content.length, he.buff.length);
                System.arraycopy(content, 0, he.buff, cursorPos, end - cursorPos);
                he.reCalcHashCode();
                he.repaint();
            } else if (getInsertAction() == insertActions.INSERTandDELETE_SELECTION) {
                int cursorPos = he.getCursorPosition();
                ArrayList<Point> sel = he.getSelectionModel().getSelectionList();
                int delBytes = 0;
                if (sel.size() == 1) {
                    delBytes = Math.abs(sel.get(0).x - sel.get(0).y) + 1;
                }
                byte[] destArray = new byte[he.buff.length + content.length - delBytes];
                System.arraycopy(he.buff, 0, destArray, 0, cursorPos);
                System.arraycopy(content, 0, destArray, cursorPos, content.length);
                System.arraycopy(he.buff,
                        cursorPos + delBytes,
                        destArray,
                        cursorPos + content.length,
                        he.buff.length - cursorPos - delBytes);
                int muStart = he.getStart();
                he.setByteContent(destArray);
                he.setCursorPostion(cursorPos);
                he.setStart(muStart);
                he.scrlRight.setValue(muStart);
            }
        }
    }

    /**
     * delete the Byte from the cursor + the bytes which are marked
     */
    void deleteChars() {
        if (inActionDelete) {
            return;
        } else {
            inActionDelete = true;
        }
        /* run in separate Thread to reduce the workload on large files while typing */
        Executors.newCachedThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                // final long start = System.currentTimeMillis();
                final int cursorPos = he.getCursorPosition();
                ArrayList<Point> listMarks = he.getSelectionModel().getSelectionList();
                /* to have all selection points in a sorted order */
                ArrayList<Integer> selPoints = new ArrayList<Integer>();
                for (Point curSelection : listMarks) {
                    for (int startSel = curSelection.x; startSel <= curSelection.y; startSel++) {
                        if (!selPoints.contains(startSel)) {
                            selPoints.add(startSel);
                        }
                    }
                }
                /* add the cursorpos as well in case of a single char del */
                if (!selPoints.contains(cursorPos)) {
                    selPoints.add(cursorPos);
                }
                Collections.sort(selPoints);

                final byte[] destArray = new byte[he.buff.length - selPoints.size()];
                int destByte = 0;
                int destBytePos = 0;
                /* copy only those Bytes which are not in the Selection-list */
                for (Integer curRemPos : selPoints) {
                    System.arraycopy(he.buff,
                            destByte,
                            destArray,
                            destBytePos,
                            curRemPos - destByte);
                    destBytePos += curRemPos - destByte;
                    destByte = curRemPos + 1;
                }
                System.arraycopy(he.buff,
                        destByte,
                        destArray,
                        destBytePos,
                        he.buff.length - destByte);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        int muStart = he.getStart();
                        he.setByteContent(destArray);
                        he.setCursorPostion(cursorPos);
                        he.setStart(muStart);
                        he.scrlRight.setValue(muStart);
                        inActionDelete = false;
                        // System.out.println("finished del="
                        // + (System.currentTimeMillis() - start));
                    }
                });
            }
        });
    }

    public insertActions getInsertAction() {
        return insertAction;
    }

    public void setInsertAction(insertActions insertAction) {
        this.insertAction = insertAction;
    }

    public copyStringAction getCopyAction() {
        return copyAction;
    }

    public void setCopyAction(copyStringAction copyAction) {
        this.copyAction = copyAction;
    }

    public void cutContent2Clipboard() {
        copyContent2Clipboard();
        deleteChars();
    }
}
