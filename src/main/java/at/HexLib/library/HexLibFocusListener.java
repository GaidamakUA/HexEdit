/*
 * Created on 17.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class HexLibFocusListener implements FocusListener {

    private BasicPanel basicPanel;

    public HexLibFocusListener(BasicPanel basicPanel) {
        this.basicPanel = basicPanel;
    }

    @Override
    public void focusGained(FocusEvent e) {
        /* used for the cursor */
        basicPanel.repaint();
        if (!basicPanel.hexLib.isOwnFocusComponent(e.getOppositeComponent())) {
            FocusEvent event = getAdaptedEvent(e);
            for (FocusListener curFocus : basicPanel.hexLib.getFocusListeners()) {
                /* forward from the subcomponet to the master compontent the focus-changed event */
                curFocus.focusGained(event);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        /* used for the cursor */
        basicPanel.repaint();
        if (!basicPanel.hexLib.isOwnFocusComponent(e.getOppositeComponent())) {
            FocusEvent event = getAdaptedEvent(e);
            for (FocusListener curFocus : basicPanel.hexLib.getFocusListeners()) {
                /* forward from the subcomponet to the master compontent the focus-changed event */
                curFocus.focusLost(event);
            }
        }
    }

    private FocusEvent getAdaptedEvent(FocusEvent e) {
        Component oppositeComponent = e.getOppositeComponent();
        if (oppositeComponent != null) {
            /* prevent insights into the subcomponents and allow one global Focus handling */
            if (oppositeComponent instanceof HexLibASCII) {
                oppositeComponent = ((HexLibASCII) oppositeComponent).hexLib;
            } else if (oppositeComponent instanceof HexLibHEX) {
                oppositeComponent = ((HexLibHEX) oppositeComponent).hexLib;
            } else if (oppositeComponent instanceof HeaderLenPanel) {
                oppositeComponent = ((HeaderLenPanel) oppositeComponent).hexLib;
            } else if (oppositeComponent instanceof ColumnsLeft) {
                oppositeComponent = ((ColumnsLeft) oppositeComponent).he;
            } else if (oppositeComponent instanceof HeaderColumnPanel) {
                oppositeComponent = ((HeaderColumnPanel) oppositeComponent).hexLib;
            } else if (oppositeComponent instanceof HeaderChangedPanel) {
                oppositeComponent = ((HeaderChangedPanel) oppositeComponent).hexLib;
            }
        }

        return new FocusEvent(basicPanel.hexLib,
                e.getID(),
                e.isTemporary(),
                oppositeComponent);
    }

}
