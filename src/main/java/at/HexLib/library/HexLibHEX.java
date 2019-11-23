package at.HexLib.library;

import java.awt.*;
import java.awt.event.KeyEvent;

public class HexLibHEX extends BasicContentPanel {

    private int hexCursor = 0;

    public HexLibHEX(HexLib he) {
        super(he);
        setFontObjects();
    }

    public void setFontObjects() {
        minWidth = (HexLib.fontWidth * +((16 * 3) - 1)) + borderTwice;
        setPreferredSize(new Dimension(minWidth, 0));
        setMinimumSize(new Dimension(minWidth, 0));
    }

    boolean isSameThread = false;
    private boolean doesHexAlwaysStartFirstPosition;

    public void paint(Graphics g) {
        if (g == null) {
            return;
        }
        if (hexLib.buff == null) {
            // nothing to paint resp. not yet initialized
            return;
        }
        int ini = hexLib.getStart() * 16;
        int end = ini + ((hexLib.getLines()
                /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
                + 2) * 16);

        if (end > hexLib.buff.length) {
            end = hexLib.buff.length;
        }

        // cursor hex
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
            end = Math.min(ini + 1, hexLib.buff.length);
        } else {
            /* clear content and repaint complete container */
            super.paint(g);
            g.setColor(getFontForeground());
        }
        g.setFont(HexLib.font);

        for (int n = ini; n < end; n++) {
            String byteAsString = hexLib.convertToHex(hexLib.buff[n], 2);
            if (checkCurPosPaintable(n)) {
                int hexCursorPos = (x * 3) + hexCursor;
                if (hasFocus() && getSelectionModel().isEmpty()) {
                    /* First the background */
                    g.setColor(colorSecondCursor);
                    fillRect4Cursor(g, (x * 3), y, 2);
                    if (hexLib.cursorBlink != null && hexLib.cursorBlink.isActive()) {
                        g.setColor(colorActiveCursor);
                    } else {
                        if (hasStripes && (hexLib.getStart() + y) % 2 == 0) {
                            g.setColor(stripeColors[1]);
                        } else {
                            g.setColor(getBackground());
                        }
                    }
                    fillRect4Cursor(g, hexCursorPos, y, 1);
                } else {
                    g.setColor(colorSecondCursor);
                    rect(g, (x * 3), y, 2);
                }

                /* Second the foreground */
                if (hasFocus() && getSelectionModel().isEmpty()) {
                    g.setColor(fontCursorForeground);
                    printString(g, byteAsString, ((x++) * 3), y);
                    if (hexLib.cursorBlink != null && !hexLib.cursorBlink.isActive()) {
                        g.setColor(getFontForeground());
                        printStringSingle(g,
                                byteAsString.substring(hexCursor, hexCursor + 1),
                                hexCursorPos,
                                y,
                                hexCursor);
                    }
                } else {
                    g.setColor(getFontForeground());
                    printString(g, byteAsString, ((x++) * 3), y);
                }
            } else {
                if (getSelectionModel().isPositionWithinMarkPos(n)) {
                    g.setColor(colorMarkPos);
                    fillRect4Mark(g, (x * 3), y, 2);
                    if (n == getCursorPosition()) {
                        /* mark cursor to be recognized more easily in multiple marks */
                        g.setColor(colorSecondCursor);
                        rect(g, (x * 3), y, 2);
                    }
                }
                g.setColor(getFontForeground());
                printString(g, byteAsString, ((x++) * 3), y);
            }
            if (x == 16) {
                x = 0;
                y++;
            }
        }

        g.setColor(separatorLine);
        if (maxInactive > 0) {
            g.drawLine(minWidth - 1, 0, minWidth - 1, maxHeightPainted - borderTwice
                    - 1);
        } else {
            g.drawLine(minWidth - 1, 0, minWidth - 1, maxHeightPainted);
        }
        g.setColor(getFontForeground());

    }

    private void fillRect4Cursor(Graphics g, int x, int y, int s) {
        g.fillRect(((HexLib.fontWidth) * x) + border - hexCursor,
                (HexLib.fontHeight * y),// + border + 1,
                ((HexLib.fontWidth - 1) * s) + hexCursor,
                HexLib.fontHeight);
    }

    private void fillRect4Mark(Graphics g, int x, int y, int s) {
        g.fillRect((HexLib.fontWidth) * x - (HexLib.fontWidth - border) / 2,
                (HexLib.fontHeight * y),// + border + 1,
                ((HexLib.fontWidth) * (s + 1)),
                HexLib.fontHeight);
    }

    // postion of the rectangle
    public int calcCursorPos(int x, int y) {

        int muX = x / (HexLib.fontWidth * 3);
        y = y / HexLib.fontHeight;
        int total = muX + ((y + hexLib.getStart()) * 16);
        int cursPos = x - muX * HexLib.fontWidth * 3 - HexLib.fontWidth;
        if (total > hexLib.buff.length - 1) {
            total = hexLib.buff.length - 1;
            cursPos = 1;
        }
        if (cursPos <= 0 || isHexAlwaysStartFirstPosition()) {
            hexCursor = 0;
        } else {
            hexCursor = 1;
        }
        return total;
    }

    @Override
    public void setCursorPosition(int cursor) {
        setCursorPosition(cursor, getCursorPosition() < cursor);
    }

    void setCursorPosition(final int cursor, final boolean forward) {
        if (isHexAlwaysStartFirstPosition()
                || Math.abs(cursor - getCursorPosition()) > 1) {
            hexCursor = 0;
            if (0 <= cursor && cursor <= (hexLib.buff.length - 1)) {
                super.setCursorPosition(cursor);
            }
            return;
        }
        if (forward) {
            if (hexCursor == 0) {
                hexCursor = 1;
            } else {
                if (cursor <= (hexLib.buff.length - 1)) {
                    hexCursor = 0;
                    super.setCursorPosition(cursor);
                }
            }
        } else {
            if (hexCursor == 0) {
                if (cursor >= 0) {
                    hexCursor = 1;
                    super.setCursorPosition(cursor);
                }
            } else {
                hexCursor = 0;
            }
        }
    }

    // KeyListener
    public void keyTyped(KeyEvent e) {
        // debug("keyTyped("+e+")");
        if (getCursorPosition() > hexLib.buff.length) {
            return;
        }
        if (hexLib.txtFieldContainer.isEditable() && hexLib.txtFieldContainer.isEnabled()) {
            char c = e.getKeyChar();
            if (((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F'))
                    || ((c >= 'a') && (c <= 'f'))) {
                char[] str = new char[2];
                String n = hexLib.convertToHex((int) hexLib.buff[getCursorPosition()], 2);
                str[1 - hexCursor] = n.charAt(1 - hexCursor);
                str[hexCursor] = e.getKeyChar();
                hexLib.buff[getCursorPosition()] =
                        (byte) Integer.parseInt(new String(str), 16);

                if (hexCursor != 1) {
                    hexCursor = 1;
                } else if (getCursorPosition() != (hexLib.buff.length - 1)) {
                    setCursorPosition(getCursorPosition() + 1);
                    hexCursor = 0;
                }
                hexLib.reCalcHashCode();
                updateCursor();
            }
        }
    }

    public void setHexAlwaysStartFirstPosition(boolean isHexAlwaysStartFirstPosition) {
        this.doesHexAlwaysStartFirstPosition = isHexAlwaysStartFirstPosition;
    }

    public boolean isHexAlwaysStartFirstPosition() {
        return doesHexAlwaysStartFirstPosition;
    }


}
