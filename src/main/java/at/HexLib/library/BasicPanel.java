/*
 * Created on 14.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BasicPanel extends JComponent implements KeyListener {

    protected HexLib hexLib;
    protected final static int border = 2;
    protected final static int borderTwice = border * 2 + 1;

    private Color backGroundColor = Color.pink;
    private static Color colorInactiveBackGround = Color.GREEN;
    Color colorActiveCursor = Color.blue.darker();
    Color colorSecondCursor = Color.cyan.darker().darker();
    Color fontCursorForeground = Color.yellow;
    private final Color fontForeground = UIManager.getColor("TextArea.foreground");
    Color fontChangedForeground = UIManager.getColor("TextField.highlight");

    boolean hasTextFieldColors = false;
    Color separatorLine = Color.GRAY.brighter();
    Color colorLenHeaderBackGround = new Color(255, 255, 170);

    protected final java.awt.Color stripeColors[] = new java.awt.Color[2];

    protected boolean hasStripes = false;

    protected int minWidth;

    protected int ini;

    protected int end;

    protected int maxHeightPainted;
    protected int maxInactive;
    private final HexLibFocusListener focusListener;

    public BasicPanel(HexLib hexLib) {
        this.hexLib = hexLib;
        setLayout(new BorderLayout());

        // addMouseListener(this);
        addKeyListener(this);
        focusListener = new HexLibFocusListener(this);
        addFocusListener(focusListener);
    }

    public void dispose() {
        // removeMouseListener(this);
        removeKeyListener(this);
        removeFocusListener(focusListener);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println("Keypressed=" + e);

        switch (e.getKeyCode()) {
            case 33: // PgUp
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    int changeCurPos = getSelectionModel().getEndPoint();
                    changeCurPos -= (16 * (hexLib.getLines() + 1));
                    getSelectionModel().addNewEndPoint(changeCurPos);
                    scrollPane(-(hexLib.getLines() + 1));
                } else {
                    int changeCurPos = 0;
                    if (getCursorPosition() > (16 * (hexLib.getLines() + 1))) {
                        changeCurPos = getCursorPosition() - (16 * (hexLib.getLines() + 1));
                    }
                    setCursorPosition(changeCurPos);
                    updateCursor();
                }
                break;
            case 34: // PgDn
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    int changeCurPos = getSelectionModel().getEndPoint();
                    changeCurPos += (16 * (hexLib.getLines() + 1));
                    getSelectionModel().addNewEndPoint(changeCurPos);
                    scrollPane(+(hexLib.getLines() + 1));
                } else {
                    if (getCursorPosition() < (hexLib.buff.length - (16 * (hexLib.getLines() + 1)))) {
                        setCursorPosition(getCursorPosition() + (16 * (hexLib.getLines() + 1)));
                    } else {
                        setCursorPosition(hexLib.buff.length - 1);
                    }
                    updateCursor();
                }
                break;
            case 35: // End
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    int end = getSelectionModel().getEndPoint();
                    int mu = (end + 1) / 16;
                    if (end + 1 != mu * 16) {
                        /*
                         * different handling for the endpoints required, cause the first marked char
                         * is counted differently if paint starts from the endpoint or from the
                         * startpoint
                         */
                        if (getSelectionModel().getEndPoint() > getSelectionModel().getStartPoint()) {
                            getSelectionModel().addNewEndPoint((mu + 1) * 16 - 1);
                        } else {
                            getSelectionModel().addNewEndPoint((mu + 1) * 16);
                        }
                    }
                    checkScrollOneLineRequired(true);
                } else if (e.getModifiers() == (KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK)) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), hexLib.buff.length - 1);
                    } else {
                        getSelectionModel().addNewEndPoint(hexLib.buff.length - 1);
                    }
                } else if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                    setCursorPosition(hexLib.buff.length - 1);
                } else if (e.getModifiers() == 0) {
                    int mu = (getCursorPosition() + 1) / 16;
                    if (getCursorPosition() + 1 != mu * 16) {
                        setCursorPosition((mu + 1) * 16 - 1);
                    }
                }
                updateCursor();
                break;
            case 36: // Pos1
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    int start = getSelectionModel().getEndPoint();
                    int mu = (start) / 16;
                    if (start != mu * 16) {
                        /*
                         * different handling for the endpoints required, cause the first marked char
                         * is counted differently if paint starts from the endpoint or from the
                         * startpoint
                         */
                        if (getSelectionModel().getEndPoint() > getSelectionModel().getStartPoint()) {
                            getSelectionModel().addNewEndPoint((mu) * 16 - 1);
                        } else {
                            getSelectionModel().addNewEndPoint((mu) * 16);

                        }
                    }
                    checkScrollOneLineRequired(true);
                } else if (e.getModifiers() == (KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK)) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(0, getCursorPosition());
                    } else {
                        getSelectionModel().addNewEndPoint(0);
                    }
                } else if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                    setCursorPosition(0);
                } else if (e.getModifiers() == 0) {
                    int mu = getCursorPosition() / 16;
                    if (getCursorPosition() != mu * 16) {
                        setCursorPosition(mu * 16);
                    }
                }
                updateCursor();
                break;
            case 37: // <--
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    getSelectionModel().addNewEndPoint(getSelectionModel().getEndPoint() - 1);
                    checkScrollOneLineRequired(false);
                } else {
                    setCursorPosition(getCursorPosition() - 1);
                    updateCursor();
                }
                break;
            case 38: // ^--
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    getSelectionModel().addNewEndPoint(getSelectionModel().getEndPoint() - 16);
                    checkScrollOneLineRequired(false);
                } else {
                    if (getCursorPosition() > 15) {
                        setCursorPosition(getCursorPosition() - 16);
                    }
                    /* clear selection anyway, even if the cursor was not set */
                    getSelectionModel().clear();
                    updateCursor();
                }
                break;
            case 39: // -->
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    getSelectionModel().addNewEndPoint(getSelectionModel().getEndPoint() + 1);
                    checkScrollOneLineRequired(true);
                } else {
                    setCursorPosition(getCursorPosition() + 1);
                    updateCursor();
                }
                break;
            case 40: // --v
                if ((e.getModifiersEx() ^ InputEvent.SHIFT_DOWN_MASK) == 0) {
                    if (getSelectionModel().getStartPoint() < 0) {
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), getCursorPosition());
                    }
                    getSelectionModel().addNewEndPoint(getSelectionModel().getEndPoint() + 16);
                    checkScrollOneLineRequired(true);
                } else {
                    if (getCursorPosition() < (hexLib.buff.length - 16)) {
                        setCursorPosition(getCursorPosition() + 16);
                    }
                    /* clear selection anyway, even if the cursor was not set */
                    getSelectionModel().clear();
                    updateCursor();
                }
                break;
            case 65: // a
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    getSelectionModel().clear();
                    getSelectionModel().addStartAndEndPoint(0, hexLib.buff.length - 1);
                    e.consume();
                    hexLib.repaint();
                }
                break;
            case 67: // c
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    hexLib.getHexTransferHandler().copyContent2Clipboard();
                }
                break;
            case 86: // v
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    hexLib.getHexTransferHandler().pasteContentFromClipboard();
                }
                break;
            case 88: // x
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    hexLib.getHexTransferHandler().cutContent2Clipboard();
                }
                break;
            case 127: // DEL
                hexLib.getHexTransferHandler().deleteChars();
                break;
        }
    }

    void checkScrollOneLineRequired(boolean directionDown) {
        int ini = hexLib.getStart() * 16;
        int end = ini + ((hexLib.getLines()
                /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
                + 1) * 16);

        if (end > hexLib.buff.length) {
            end = hexLib.buff.length;
        }
        if (directionDown) {
            if (Math.max(getSelectionModel().getStartPoint(), getSelectionModel().getEndPoint()) >= end) {
                scrollPane(1);
                return;
            }
        } else {
            if (Math.min(getSelectionModel().getStartPoint(), getSelectionModel().getEndPoint()) <= ini) {
                scrollPane(-1);
                return;
            }
        }
        hexLib.repaint();
    }

    protected void updateCursor() {
        int n = (getCursorPosition() / 16);

        if (n < hexLib.getStart()) {
            hexLib.setStart(n);
        } else if (n >= hexLib.getStart() + hexLib.getLines()) {
            hexLib.setStart(n - (hexLib.getLines()));
        }
        hexLib.scrlRight.setValues(hexLib.getStart(), hexLib.getLines(), 0, hexLib.maxLineas);
        /* repaint ASCII + HEX - view */
        hexLib.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public boolean isFocusTraversable() {
        return true;
    }

    @Override
    public void paint(Graphics g) {
        // checkOverMaxLines();
        Color oldColor = g.getColor();
        ini = hexLib.getStart();
        /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
        end = Math.min(ini + hexLib.getLines() + 1, hexLib.maxLineas);
        if (hasTextFieldColors && !hexLib.txtFieldContainer.isEnabled()) {
            g.setColor(hexLib.getDisabledBG());
        } else if (hasTextFieldColors && !hexLib.txtFieldContainer.isEditable()) {
            g.setColor(hexLib.getInactiveBG());
        } else {
            g.setColor(getBackground());
        }

        maxInactive = -1;
        if (end == hexLib.maxLineas) {
            maxHeightPainted = borderTwice;
            int muCompare = 0;
            if ((muCompare = (end - ini)) == 0) {
                /* grant at least one line is painted (could occur when content has 0bytes-length */
                maxHeightPainted += HexLib.fontHeight;
            } else {
                maxHeightPainted += HexLib.fontHeight * muCompare;
            }
            maxInactive = getBounds().height - maxHeightPainted;
        } else {
            maxHeightPainted = Math.min(getBounds().height, hexLib.maxHeight);
        }
        g.fillRect(0, 0, minWidth, maxHeightPainted);
        if (hasStripes) {
            g.setColor(stripeColors[1]);
            int starter = ((hexLib.getStart() % 2 == 0) ? 0 : 1);
            int lastStripe = end - hexLib.getStart();
            for (int row = starter; row <= lastStripe; row += 2) {
                g.fillRect(0, row * HexLib.fontHeight, minWidth, HexLib.fontHeight);
            }
        }

        if (maxInactive > 0) {
            g.setColor(getColorInactiveBackGround());
            g.fillRect(0, maxHeightPainted - borderTwice, minWidth, maxInactive + borderTwice);
        }
        g.setColor(oldColor);
    }

    protected boolean checkOverMaxLines() {
        boolean b = hexLib.maxLineas <= hexLib.getLines() + hexLib.getStart();
        // boolean b = hexLib.maxLineas <= hexLib.getLineas() + hexLib.sb.getValue();
        if (b) {
            hexLib.setStart(hexLib.maxLineas - hexLib.getLines() - 1);
        }
        return b;
    }

    protected void rect(Graphics g, int x, int y, int s) {
        g.drawRect((HexLib.fontWidth * x) + border - 1, (HexLib.fontHeight * y), (HexLib.fontWidth * s), HexLib.fontHeight - 1);
    }

    protected void createRect(Graphics g, int x, int y, int s) {
        g.drawRect((HexLib.fontWidth * x), (HexLib.fontHeight * y), (HexLib.fontWidth * s) + border * 2, HexLib.fontHeight - 1);
    }

    protected void printString(Graphics g, String s, int x, int y) {
        g.drawString(s, (HexLib.fontWidth * x) + border, ((HexLib.fontHeight * (y + 1)) - HexLib.fontMaxDescent - 1)

        );
    }

    protected void printStringSingle(Graphics g, String s, int x, int y, int hexCursor) {
        g.drawString(s, (HexLib.fontWidth * x) + border - hexCursor, ((HexLib.fontHeight * (y + 1)) - HexLib.fontMaxDescent - 1)

        );
    }

    protected void printString(Graphics g, String s, double x, int y) {
        g.drawString(s, (int) (HexLib.fontWidth * x) + border, ((HexLib.fontHeight * (y + 1)) - HexLib.fontMaxDescent) + border);
    }

    @Override
    public void setBackground(Color bg) {
        backGroundColor = bg;
        createZebraColors(bg, bg);
    }

    @Override
    public Color getBackground() {
        return backGroundColor;
    }

    protected boolean paintCursorOnly = false;

    public void repaintCursorOnly() {
        paintCursorOnly = true;
        paint(getGraphics());
        paintCursorOnly = false;
    }

    public static void setColorInactiveBackGround(Color muColorInactiveBackGround) {
        colorInactiveBackGround = muColorInactiveBackGround;
    }

    public Color getColorInactiveBackGround() {
        return colorInactiveBackGround;
    }

    /**
     * the cursor is stored in a static way, i.e. all can access to the content. <br>
     * Main reason to have it static is to use the firePropertyChanged-Method
     *
     * @return
     */
    public void setCursorPosition(int cursor) {
        setCursorPositionInternal(cursor, true);
    }

    void setCursorPositionInternal(int cursor, boolean clearSelection) {
        if (cursor < 0) {
            cursor = 0;
        } else if (cursor > hexLib.buff.length - 1) {
            cursor = hexLib.buff.length - 1;
        }
        int muOldCursor = hexLib.cursor;
        hexLib.cursor = cursor;
        hexLib.firePropertyChange(HexLib.cursorProperty, muOldCursor, cursor);

        if (clearSelection) {
            getSelectionModel().clear();
        }
    }

    /**
     * the cursor is stored in a static way, i.e. all can access to the content. <br>
     * Main reason to have it static is to use the firePropertyChanged-Method
     *
     * @return
     */
    public int getCursorPosition() {
        return hexLib.cursor;
    }

    /**
     * Compute zebra background stripe colors.
     */
    protected void createZebraColors() {
        createZebraColors(UIManager.getColor("TextArea.background"), UIManager.getColor("TextArea.selectionBackground"));
    }

    protected void createZebraColors(Color basisColor, Color stripeBasisColor) {
        /* prevent some annoying Errors - just in case - 2013-12-13 */
        if (basisColor == null) {
            basisColor = new Color(255, 255, 255);
        }
        if (stripeBasisColor == null) {
            stripeBasisColor = new Color(0, 0, 128);
        }
        /* END */

        stripeColors[0] = stripeColors[1] = basisColor;
        final java.awt.Color sel = stripeBasisColor;

        // computed colors depending on the selected color
        final float[] bgHSB =
                java.awt.Color.RGBtoHSB(stripeColors[0].getRed(), stripeColors[0].getGreen(), stripeColors[0].getBlue(), null);
        final float[] selHSB = java.awt.Color.RGBtoHSB(sel.getRed(), sel.getGreen(), sel.getBlue(), null);
        Color muColor =
                java.awt.Color.getHSBColor((selHSB[1] == 0.0 || selHSB[2] == 0.0) ? bgHSB[0] : selHSB[0],
                        0.08f * selHSB[1] + 0.8f * bgHSB[1],
                        bgHSB[2] + ((bgHSB[2] < 0.5f) ? 0.05f : -0.05f));
        stripeColors[1] = new ColorUIResource(muColor); // to have it alternative
    }

    public Color getFontForeground() {
        if (!hexLib.txtFieldContainer.isEnabled()) {
            return hexLib.getInactiveFG();
        }
        return fontForeground;
    }

    void scrollPane(int unitsToScroll) {
        if (unitsToScroll != 0) {
            hexLib.setStart(hexLib.getStart() + unitsToScroll);
            if (checkOverMaxLines()) {
                // we corrected the limit ==> set the value to the very end as well
                hexLib.scrlRight.setValue(hexLib.scrlRight.getMaximum() - hexLib.scrlRight.getVisibleAmount());
            } else {
                hexLib.scrlRight.setValue(hexLib.getStart());
            }
        }
        /* repaint in case of mark was set */
        hexLib.repaint();
    }

    HexLibSelectionModel getSelectionModel() {
        return hexLib.getSelectionModel();
    }
}
