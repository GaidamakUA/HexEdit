/*
 * Created on 14.03.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.Listener;

import at.HexLib.GUI.gui.panels.SearchPanel;
import at.HexLib.library.HexLib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchActionListener implements ActionListener {

    private SearchPanel searchPanel;
    private HexLib hexEditor;

    public SearchActionListener(SearchPanel searchPanel) {
        this.searchPanel = searchPanel;
        hexEditor = searchPanel.hexEditor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchPanel.btnSearchStart) {
            int searchStart = 0;
            if (searchPanel.txtSearchStart.getText().trim().length() > 0) {
                searchStart = Integer.parseInt(searchPanel.txtSearchStart.getText());
            }
            try {
                int foundPosition = -1;
                ArrayList<Point> listMark = new ArrayList<Point>();
                if (searchPanel.chkSearchMarkAll.isSelected()) {
                    listMark =
                            searchAllEntries(searchPanel.txtSearchString.getText(),
                                    !searchPanel.chkSearchAsString.isSelected(),
                                    searchPanel.chkSearchCaseSensitive.isSelected());
                }
                if (searchPanel.rdSearchDirectionForward.isSelected()) {
                    foundPosition =
                            hexEditor.indexOf(searchPanel.txtSearchString.getText(),
                                    !searchPanel.chkSearchAsString.isSelected(),
                                    searchPanel.chkSearchCaseSensitive.isSelected(),
                                    searchStart);
                    if (foundPosition >= 0) {
                        searchPanel.txtSearchStart.setText((foundPosition + 1) + "");
                    }

                } else {
                    foundPosition =
                            hexEditor.lastIndexOf(searchPanel.txtSearchString.getText(),
                                    !searchPanel.chkSearchAsString.isSelected(),
                                    searchPanel.chkSearchCaseSensitive.isEnabled()
                                            && searchPanel.chkSearchCaseSensitive.isSelected(),
                                    searchStart);
                    if (foundPosition >= 0) {
                        searchPanel.txtSearchStart.setText((foundPosition - 1) + "");

                    }
                }
                if (!listMark.isEmpty()) {
                    /* multiple search successful (at least one entry found) */
                    markAllPositionsAndJump(listMark, searchStart);
                } else
                    /* single search */
                    if (foundPosition >= 0) {
                        hexEditor.setCursorPostion(foundPosition);
                        int textLength = searchPanel.txtSearchString.getText().length();
                        if (searchPanel.chkSearchAsString.isSelected()) {
                            textLength /= 2;
                        }
                        listMark.add(new Point(foundPosition, foundPosition + textLength - 1));
                        /*
                         * any part request the focus to have the keyboard-interaction with CTRL-C
                         * working
                         */
                        hexEditor.requestFocus4Hex();
                        hexEditor.getSelectionModel().setSelectionIntervals(listMark);
                    } else {
                        searchPanel.txtSearchStart.setText("-1");
                        JOptionPane.showMessageDialog(searchPanel.he,
                                "Search could not find content!",
                                "Search finished",
                                JOptionPane.WARNING_MESSAGE);
                    }
            } catch (IllegalArgumentException e1) {
                JOptionPane.showMessageDialog(searchPanel.he,
                        "Search cannot performed with the provided content!\n\nString is not a Hex-Byte-representation!",
                        "Incorrect parameter",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == searchPanel.btnSearchReset) {
            searchPanel.txtSearchStart.setText("-1");
            searchPanel.txtSearchString.setText("");
            searchPanel.chkSearchAsString.setSelected(false);
            searchPanel.rdSearchDirectionBackward.setSelected(false);
            searchPanel.rdSearchDirectionForward.setSelected(true);
        } else if (e.getSource() == searchPanel.txtSearchString) {
            searchPanel.btnSearchStart.doClick();
        }
    }

    void markAllPositionsAndJump(ArrayList<Point> listMark, int searchStart) {
        int curJump2 = listMark.get(0).x;
        if (searchPanel.rdSearchDirectionForward.isSelected()) {
            for (Point curPoint : listMark) {
                if (curPoint.x > searchStart) {
                    curJump2 = curPoint.x;
                    break;
                }
            }
        }
        hexEditor.setCursorPostion(curJump2);
        searchPanel.txtSearchStart.setText(curJump2 + "");
        /* mark all found entries */
        hexEditor.getSelectionModel()
                .setSelectionMode(hexEditor.getSelectionModel().MULTIPLE_INTERVAL_SELECTION);
        /* any part request the focus to have the keyboard-interaction with CTRL-C working */
        hexEditor.requestFocus4Hex();
        hexEditor.getSelectionModel().setSelectionIntervals(listMark);
    }

    private ArrayList<Point> searchAllEntries(String text,
                                              boolean isDataString,
                                              boolean isCaseSensitive) {
        int searchStart = -1;
        ArrayList<Point> retList = new ArrayList<Point>();
        int foundPosition =
                hexEditor.indexOf(searchPanel.txtSearchString.getText(),
                        !searchPanel.chkSearchAsString.isSelected(),
                        searchPanel.chkSearchCaseSensitive.isSelected(),
                        searchStart);
        int textLength = text.length();
        if (searchPanel.chkSearchAsString.isSelected()) {
            textLength /= 2;
        }
        while (foundPosition >= 0) {
            retList.add(new Point(foundPosition, foundPosition + textLength - 1));
            searchStart = foundPosition + 1;
            foundPosition =
                    hexEditor.indexOf(searchPanel.txtSearchString.getText(),
                            !searchPanel.chkSearchAsString.isSelected(),
                            searchPanel.chkSearchCaseSensitive.isSelected(),
                            searchStart);
        }
        return retList;
    }

}
