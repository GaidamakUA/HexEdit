/*
 * Created on 07.03.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.Listener;

import at.HexLib.GUI.gui.panels.OptionPanel;
import at.HexLib.GUI.gui.panels.SearchPanel;
import at.HexLib.GUI.main.HexEditSample;
import at.HexLib.GUI.main.HexLibContainer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MyItemListener implements ItemListener {

    private OptionPanel optionPanel;
    private SearchPanel searchPanel;

    public MyItemListener(SearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    public MyItemListener(OptionPanel optionPanel) {
        this.optionPanel = optionPanel;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (optionPanel != null) {
            if (optionPanel.isOptionSetStatus) {
                return;
            }
            HexLibContainer hexEditor = HexEditSample.masterReference.hexEditor;
            if (e.getSource() == optionPanel.chkHexStartBegin) {
                hexEditor.setHexAlwaysStartFirstPosition(optionPanel.chkHexStartBegin.isSelected());
            } else if (e.getSource() == optionPanel.chkHexIsEditable) {
                hexEditor.setHexBeanEditable(optionPanel.chkHexIsEditable.isSelected());
            } else if (e.getSource() == optionPanel.chkHexIsEnabled) {
                hexEditor.setHexBeanEnabled(optionPanel.chkHexIsEnabled.isSelected());
            } else if (e.getSource() == optionPanel.chkConvertBytes) {
                hexEditor.setConvertBytesLen(optionPanel.chkConvertBytes.isSelected());
            } else if (e.getSource() == optionPanel.chkASCIIVisible) {
                hexEditor.setASCIIEditorVisible(optionPanel.chkASCIIVisible.isSelected());
            } else if (e.getSource() == optionPanel.chkHexVisible) {
                hexEditor.setHexEditorVisible(optionPanel.chkHexVisible.isSelected());
            } else if (e.getSource() == optionPanel.chkContinousScroll) {
                hexEditor.setContinousScroll(optionPanel.chkContinousScroll.isSelected());
            } else if (e.getSource() == optionPanel.cmbCopyAction) {
                if (optionPanel.cmbCopyAction.getSelectedIndex() == 0) {
                    hexEditor.getHexTransferHandler()
                            .setCopyAction(at.HexLib.library.HexTransfer.copyStringAction.BINARY);
                } else {
                    hexEditor.getHexTransferHandler()
                            .setCopyAction(at.HexLib.library.HexTransfer.copyStringAction.BINARY_AS_STRING);
                }
            } else if (e.getSource() == optionPanel.cmbPasteAction) {
                if (optionPanel.cmbPasteAction.getSelectedIndex() == 0) {
                    hexEditor.getHexTransferHandler()
                            .setInsertAction(at.HexLib.library.HexTransfer.insertActions.OVERWRITE);
                } else {
                    hexEditor.getHexTransferHandler()
                            .setInsertAction(at.HexLib.library.HexTransfer.insertActions.INSERTandDELETE_SELECTION);
                }
            }
        }
        if (searchPanel != null) {
            if (e.getSource() == searchPanel.chkSearchAsString) {
                searchPanel.chkSearchCaseSensitive.setEnabled(!searchPanel.chkSearchAsString.isSelected());
            }
        }
    }
}
