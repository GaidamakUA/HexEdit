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

    protected HexLib he;
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

    public BasicPanel(HexLib he) {
        this.he = he;
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
                    changeCurPos -= (16 * (he.getLines() + 1));
                    getSelectionModel().addNewEndPoint(changeCurPos);
                    scrollPane(-(he.getLines() + 1));
                } else {
                    int changeCurPos = 0;
                    if (getCursorPosition() > (16 * (he.getLines() + 1))) {
                        changeCurPos = getCursorPosition() - (16 * (he.getLines() + 1));
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
                    changeCurPos += (16 * (he.getLines() + 1));
                    getSelectionModel().addNewEndPoint(changeCurPos);
                    scrollPane(+(he.getLines() + 1));
                } else {
                    if (getCursorPosition() < (he.buff.length - (16 * (he.getLines() + 1)))) {
                        setCursorPosition(getCursorPosition() + (16 * (he.getLines() + 1)));
                    } else {
                        setCursorPosition(he.buff.length - 1);
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
                        getSelectionModel().addStartAndEndPoint(getCursorPosition(), he.buff.length - 1);
                    } else {
                        getSelectionModel().addNewEndPoint(he.buff.length - 1);
                    }
                } else if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                    setCursorPosition(he.buff.length - 1);
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
                    if (getCursorPosition() < (he.buff.length - 16)) {
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
                    getSelectionModel().addStartAndEndPoint(0, he.buff.length - 1);
                    e.consume();
                    he.repaint();
                }
                break;
            case 67: // c
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    he.getHexTransferHandler().copyContent2Clipboard();
                }
                break;
            case 86: // v
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    he.getHexTransferHandler().pasteContentFromClipboard();
                }
                break;
            case 88: // x
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    he.getHexTransferHandler().cutContent2Clipboard();
                }
                break;
            case 127: // DEL
                he.getHexTransferHandler().deleteChars();
                break;
        }
    }

    void checkScrollOneLineRequired(boolean directionDown) {
        int ini = he.getStart() * 16;
        int end = ini + ((he.getLines()
                /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
                + 1) * 16);

        if (end > he.buff.length) {
            end = he.buff.length;
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
        he.repaint();
    }

    protected void updateCursor() {
        int n = (getCursorPosition() / 16);

        if (n < he.getStart()) {
            he.setStart(n);
        } else if (n >= he.getStart() + he.getLines()) {
            he.setStart(n - (he.getLines()));
        }
        he.scrlRight.setValues(he.getStart(), he.getLines(), 0, he.maxLineas);
        /* repaint ASCII + HEX - view */
        he.repaint();
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
        ini = he.getStart();
        /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
        end = Math.min(ini + he.getLines() + 1, he.maxLineas);
        if (hasTextFieldColors && !he.txtFieldContainer.isEnabled()) {
            g.setColor(he.getDisabledBG());
        } else if (hasTextFieldColors && !he.txtFieldContainer.isEditable()) {
            g.setColor(he.getInactiveBG());
        } else {
            g.setColor(getBackground());
        }

        maxInactive = -1;
        if (end == he.maxLineas) {
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
            maxHeightPainted = Math.min(getBounds().height, he.maxHeight);
        }
        g.fillRect(0, 0, minWidth, maxHeightPainted);
        if (hasStripes) {
            g.setColor(stripeColors[1]);
            int starter = ((he.getStart() % 2 == 0) ? 0 : 1);
            int lastStripe = end - he.getStart();
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
        boolean b = he.maxLineas <= he.getLines() + he.getStart();
        // boolean b = he.maxLineas <= he.getLineas() + he.sb.getValue();
        if (b) {
            he.setStart(he.maxLineas - he.getLines() - 1);
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
        } else if (cursor > he.buff.length - 1) {
            cursor = he.buff.length - 1;
        }
        int muOldCursor = he.cursor;
        he.cursor = cursor;
        he.firePropertyChange(HexLib.cursorProperty, muOldCursor, cursor);

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
        return he.cursor;
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
        if (!he.txtFieldContainer.isEnabled()) {
            return he.getInactiveFG();
        }
        return fontForeground;
    }

    void scrollPane(int unitsToScroll) {
        if (unitsToScroll != 0) {
            he.setStart(he.getStart() + unitsToScroll);
            if (checkOverMaxLines()) {
                // we corrected the limit ==> set the value to the very end as well
                he.scrlRight.setValue(he.scrlRight.getMaximum() - he.scrlRight.getVisibleAmount());
            } else {
                he.scrlRight.setValue(he.getStart());
            }
        }
        /* repaint in case of mark was set */
        he.repaint();
    }

    HexLibSelectionModel getSelectionModel() {
        return he.getSelectionModel();
    }
}
