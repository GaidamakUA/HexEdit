/*
 * Created on 20.03.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.Listener;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class will retain the caret positioning at the character where the mouse was
 * clicked.
 */
public class CaretPositionListener implements MouseListener {

    private boolean dynamicFormatting;

    /**
     * Convenience constructor. This class is automatically added as a MouseListener to the
     * specified formatted text fields.
     */
    public CaretPositionListener(JFormattedTextField... components) {
        registerComponent(components);
    }

    /**
     * Indicates that the formatting of the text in the formatted text field can change
     * depending on whether the text field has focus or not. The listner must be aware of
     * this so the proper caret position can be calculated.
     *
     * @param dynamicFormatting when true dynamic formatting must be considered
     */
    public void setDynamicFormatting(boolean dynamicFormatting) {
        this.dynamicFormatting = dynamicFormatting;
    }


    /**
     * Remove listeners from the specified component
     *
     * @param component the component the listeners are removed from
     */
    public void deregisterComponent(JFormattedTextField... components) {
        for (JFormattedTextField component : components) {
            component.removeMouseListener(this);
        }
    }

    /**
     * Add the required listeners to the specified component
     *
     * @param component the component the listeners are added to
     */
    public void registerComponent(JFormattedTextField... components) {
        for (JFormattedTextField component : components) {
            component.addMouseListener(this);
        }
    }

    public void mousePressed(final MouseEvent me) {
        final JFormattedTextField ftf = (JFormattedTextField) me.getSource();

        if (dynamicFormatting && ftf.getValue() != null) {
            determineCaretPosition(ftf, ftf.viewToModel(me.getPoint()));
        } else {
            int offset = ftf.getCaretPosition();
            setCaretPosition(ftf, offset);
        }
    }

    private void determineCaretPosition(final JFormattedTextField ftf, int offset) {
        // int offset = ftf.getCaretPosition();
        String text = ftf.getText();
        String value = ftf.getValue().toString();

        if (text.equals(value)) {
            setCaretPosition(ftf, offset);
            return;
        }

        int i = 0;
        int j = 0;

        // Exclude formatting characters

        while (j < offset) {
            if (text.charAt(i) == value.charAt(j)) {
                i++;
                j++;
            } else {
                offset--;
                i++;
            }
        }

        if (!ftf.hasFocus()) {
            ftf.requestFocus();
        }
        setCaretPosition(ftf, offset);
    }

    private void setCaretPosition(final JFormattedTextField ftf, final int offset) {
        SwingUtilities.invokeLater(() -> {
            try {
                ftf.setCaretPosition(offset);
            } catch (IllegalArgumentException e) {
            }
        });
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
