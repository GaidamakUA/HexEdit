/*
 * Created on 12.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.main;

import at.HexLib.GUI.Listener.PropertiesListener;
import at.HexLib.GUI.gui.tabs.ButtonTabComponent;
import at.HexLib.GUI.gui.tabs.DetachedTabFrame;
import at.HexLib.GUI.gui.tabs.DnDTabbedPane;
import at.HexLib.library.HexLib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

public class HexLibContainer extends HexLib {

    public static final String EMPTY_FILENAME = "No file name";
    private static final String changedSign = "*";
    private String filename = EMPTY_FILENAME;
    private String filenameTooltip = EMPTY_FILENAME;
    private DnDTabbedPane tabPn;
    private PropertiesListener myPropListener;
    private FocusAdapter focusListener;

    public HexLibContainer(byte[] byteContent) {
        super(byteContent);
        tabPn = HexEditSample.masterReference.tabPnHexEdit;
        setBackground(Color.WHITE);
        setInactiveBG(Color.GRAY);
        setDisabledBG(Color.GRAY);
        addListeners();
    }

    private void addListeners() {
        myPropListener = new PropertiesListener(this);
        addPropertyChangeListener(myPropListener);

        focusListener = new FocusAdapter() {

            boolean lostRequiresAction = false;
            private JComponent requiredActionComponent;

            @Override
            public void focusGained(FocusEvent e) {
                if (!e.isTemporary() && e.getSource() instanceof HexLibContainer) {
                    if (((JComponent) e.getComponent()).getRootPane().getParent() instanceof DetachedTabFrame
                            && e.getOppositeComponent() != null
                            && instanceOfHexLib(e.getOppositeComponent())
                            && (((JComponent) e.getOppositeComponent()).getRootPane()
                            .getParent() instanceof HexEditSample)) {
                        /*
                         * The hack here is required, cause if the focus is changed from HexLib1 (in
                         * HexEditSample) to HexLib2 (in own Dialog) and then back to any other
                         * component in HexEditSample, e.g. TextField, Cursor-Field, etc. then the
                         * HexLib1 gains once more the focus, although the focus is set somewhere
                         * else... As a workaround: we set the focus to a visible component in
                         * HexEditSample and THEN request once more the focus for the new component. I
                         * know it is a hack, but any other improvements welcomed
                         */
                        requiredActionComponent = (JComponent) e.getComponent();
                        HexEditSample.masterReference.tabPnHexEdit.requestFocus(false);
                        lostRequiresAction = true;
                        return;
                    }

                    HexEditSample.masterReference.hexEditor =
                            (HexLibContainer) e.getSource();
                    /* update GUI-parts to reflect the changed focus */
                    HexEditSample.masterReference.pnlCursor.setCursorPosIntoTextField();
                    HexEditSample.masterReference.pnlOptions.setOptionsFromHexLib();
                    if (getFilename().equals(EMPTY_FILENAME)) {
                        HexEditSample.masterReference.txtFileName.setText("");
                    } else {
                        HexEditSample.masterReference.txtFileName.setText(getFilenameTooltip());
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (lostRequiresAction) {
                    lostRequiresAction = false;
                    requiredActionComponent.requestFocus();
                    return;
                }
            }

        };
        addFocusListener(focusListener);
    }

    @Override
    public void dispose() {
        removePropertyChangeListener(myPropListener);
        removeFocusListener(focusListener);
        super.dispose();
    }

    public void setFilename(String filename) {
        if (filename.equals(EMPTY_FILENAME)) {
            this.filename = EMPTY_FILENAME;
        } else {
            this.filename = new File(filename).getName();
        }
        setFilenameInTab(this.filename);
        setFilenameTooltip(filename);
    }

    private void setFilenameInTab(String filename) {
        for (int i = 0; i < tabPn.getTabCount(); i++) {
            if (tabPn.getComponentAt(i) == this) {
                if (tabPn.getTabComponentAt(i) instanceof ButtonTabComponent
                        && !((ButtonTabComponent) tabPn.getTabComponentAt(i)).isPlusButton) {
                    tabPn.setTitleAt(i, filename);
                    /* update the size as well for the change-indicator */
                    tabPn.getTabComponentAt(i).revalidate();
                }
            }
        }
        /* check as well for the detached Frames */
        Frame[] allFrames = Frame.getFrames();
        for (Frame curFrame : allFrames) {
            if (curFrame instanceof DetachedTabFrame) {
                ((DetachedTabFrame) curFrame).setFileName(filename, this);
            }
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getFilenameTooltip() {
        return filenameTooltip;
    }

    public void setFilenameTooltip(String filenameTooltip) {
        this.filenameTooltip = filenameTooltip;

        int sel = tabPn.getSelectedIndex();
        if (sel >= 0 && tabPn.getTabComponentAt(sel) instanceof ButtonTabComponent
                && !((ButtonTabComponent) tabPn.getTabComponentAt(sel)).isPlusButton) {
            tabPn.setToolTipTextAt(sel, getFilenameTooltip());
        }
    }

    public void updateChangeStatus() {
        if (isContentChanged()) {
            setFilenameInTab(changedSign + filename);
        } else {
            setFilenameInTab(filename);
        }
        /* in case of Save, which changes the status */
        repaint();
    }
}
