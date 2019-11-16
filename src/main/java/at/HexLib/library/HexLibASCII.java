package at.HexLib.library;

import java.awt.*;
import java.awt.event.KeyEvent;

public class HexLibASCII extends BasicContentPanel {

    public HexLibASCII(HexLib he) {
        super(he);
        setFontObjects();
    }

    public void setFontObjects() {
        minWidth = HexLib.fontWidth * (16) + borderTwice;
        setPreferredSize(new Dimension(minWidth, 0));
        setMinimumSize(new Dimension(minWidth, 0));
    }

    public void paint(Graphics g) {
        if (g == null) {
            return;
        }
        if (he.buff == null) {
            // nothing to paint resp. not yet initialized
            return;
        }
        int ini = he.getStart() * 16;
        int fin = ini + ((he.getLines()
                /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
                + 2) * 16);
        if (fin > he.buff.length) {
            fin = he.buff.length;
        }

        int x = 0;
        int y = 0;
        if (paintCursorOnly) {
            /* background is not cleared ==> painting cursor is possible */
            int muCursor = getCursorPosition() - ini;
            y = muCursor / 16;
            x = muCursor - y * 16;
            /* reduce CPU-load to paint only the changed cursor */
            ini = getCursorPosition();
            if (ini < 0) {
                return;
            }
            fin = Math.min(ini + 1, he.buff.length);
        } else {
            /* clear content and repaint complete container */
            super.paint(g);
        }
        g.setFont(HexLib.font);

        for (int n = Math.max(ini, 0); n < fin; n++) {
            if (checkCurPosPaintable(n)) {
                if (hasFocus() && getSelectionModel().isEmpty()) {
                    if (hasStripes && (he.getStart() + y) % 2 == 0) {
                        g.setColor(stripeColors[1]);
                    } else {
                        g.setColor(getBackground());
                    }
                    fillRect4Cursor(g, x, y, 1);
                    g.setColor(colorActiveCursor);
                    if (he.cursorBlink != null && he.cursorBlink.isActive()) {
                        rect(g, x, y, 1);
                    } else {
                        fillRect4Cursor(g, x, y, 1);
                    }
                } else {
                    g.setColor(colorActiveCursor);
                    rect(g, x, y, 1);
                }
                if (hasFocus() && getSelectionModel().isEmpty()) {
                    if (he.cursorBlink != null && he.cursorBlink.isActive()) {
                        g.setColor(getFontForeground());
                    } else {
                        g.setColor(fontCursorForeground);
                    }
                } else {
                    g.setColor(getFontForeground());
                }
            } else {
                if (getSelectionModel().isPositionWithinMarkPos(n)) {
                    g.setColor(colorMarkPos);
                    fillRect4Mark(g, (x), y, 1);
                    if (n == getCursorPosition()) {
                        /* mark cursor to be recognized more easily in multiple marks */
                        g.setColor(colorSecondCursor);
                        rect(g, x, y, 1);
                    }
                }
                g.setColor(getFontForeground());
            }
            if (he.buff[n] == 0) {
                printString(g, "", (x++), y);
            } else if (he.buff[n] < 0) {
                printString(g, "" + ((char) (256 + he.buff[n])), (x++), y);
            } else {
                printString(g, "" + (char) he.buff[n], (x++), y);
            }
            if (x == 16) {
                x = 0;
                y++;
            }
        }
    }

    private void fillRect4Cursor(Graphics g, int x, int y, int s) {
        g.fillRect(((HexLib.fontWidth) * x) + border,
                (HexLib.fontHeight * y),// + border + 1,
                (HexLib.fontWidth * s),
                HexLib.fontHeight - 1);
    }

    private void fillRect4Mark(Graphics g, int x, int y, int s) {
        g.fillRect((HexLib.fontWidth) * x + border,
                (HexLib.fontHeight * y),// + border + 1,
                ((HexLib.fontWidth) * (s)),
                HexLib.fontHeight);
    }

    // calcular la posicion del raton
    public int calcCursorPos(int x, int y) {
        x = x / HexLib.fontWidth;
        y = y / HexLib.fontHeight;
        int total = x + ((y + he.getStart()) * 16);
        if (total > he.buff.length - 1) {
            total = he.buff.length - 1;
        } else if (total < 0) {
            total = 0;
        }
        return total;
    }

    // KeyListener
    // @Override
    // public void keyPressed(KeyEvent e) {
    // super.keyPressed(e);
    // if (e.isConsumed()) {
    // return;
    // }
    public void keyTyped(KeyEvent e) {
        if (getCursorPosition() > he.buff.length) {
            return;
        }
        if (he.txtFieldContainer.isEditable() && he.txtFieldContainer.isEnabled()
                && isPrintableChar(e.getKeyChar())) {
            he.buff[getCursorPosition()] = (byte) e.getKeyChar();
            he.reCalcHashCode();
            if (getCursorPosition() != (he.buff.length - 1)) {
                setCursorPosition(getCursorPosition() + 1);
            }
            updateCursor();
        }
    }

    boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) && c != KeyEvent.CHAR_UNDEFINED
                && block != null && block != Character.UnicodeBlock.SPECIALS;
    }

}
