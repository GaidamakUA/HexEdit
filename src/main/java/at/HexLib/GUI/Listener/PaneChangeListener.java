/*
 * Created on 16.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.Listener;

import at.HexLib.GUI.main.HexEditSample;
import at.HexLib.GUI.main.HexLibContainer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;


public class PaneChangeListener implements ChangeListener {

    private HexEditSample hexEditSample;

    public PaneChangeListener() {
        this.hexEditSample = HexEditSample.masterReference;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Component sel = hexEditSample.tabPnHexEdit.getSelectedComponent();
        // System.out.println("State change current selection = " + sel.hashCode()
        // + " " + sel);
        if (sel instanceof HexLibContainer) {
            HexLibContainer curEditor = (HexLibContainer) sel;
            HexEditSample master = HexEditSample.masterReference;
            master.hexEditor = curEditor;
            curEditor.requestFocus4Hex();
            master.pnlCursor.setCursorPosIntoTextField();
        }
    }

}
