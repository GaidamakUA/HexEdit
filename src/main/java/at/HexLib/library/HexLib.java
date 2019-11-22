package at.HexLib.library;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class HexLib extends JPanel implements AdjustmentListener, ClipboardOwner {

    /**
     * automatic generated
     */
    private static final long serialVersionUID = -2981812315423877420L;
    byte[] buff;
    protected static Font font = Font.decode("Monospaced");
    protected static Font fontBold = font.deriveFont(Font.BOLD);
    protected static int fontHeight = 16;
    protected static int fontWidth = 16;
    protected static int fontMaxDescent;

    JScrollBar scrlRight;
    private int start = 0;
    private final int minLines = 10;
    private int lines = minLines;
    protected int maxLineas = getLines();
    protected int maxHeight = 0;

    private HeaderChangedPanel pnlInactiveHeader;
    private HexLibASCII jHexEditorASCII;
    private HexLibHEX jHexEditorHEX;
    private HeaderColumnPanel headerColumnPanel;
    private ColumnsLeft columnLeft;
    private HeaderLenPanel headerLenPanel;
    protected CursorBlink cursorBlink;

    private Color colorHexBackGround =
            UIManager.getColor("TextArea.background");
    private Color colorASCIIBackGround =
            UIManager.getColor("TextArea.background");
    private Color colorInactiveBackGround =
            UIManager.getColor("TextArea.inactiveForeground");
    private Color disabledBG =
            UIManager.getColor("TextField.disabledBackground");
    private Color inactiveBG =
            UIManager.getColor("TextField.inactiveBackground");
    private Color inactiveFG =
            UIManager.getColor("TextField.inactiveForeground");
    private Color colorHexCounterBackGround = new Color(255,
            255,
            74);

    private long hashCode;
    private boolean continousScroll = true;
    private ComponentAdapter myCompListener;
    private HexLibSelectionModel selectionModel;

    public static String contentChangedProperty = "contentChanged";
    public static String cursorProperty = "cursorPosChanged";
    protected JTextField txtFieldContainer = new JTextField();
    protected int cursor;

    private HexTransfer hexTransfer;

    public HexLib() {
        this(new byte[0]);
    }

    public HexLib(byte[] buff) {
        super();

        initialize();
        buildLayout();
        addListeners();
        setByteContent(buff);

        setColorInactiveBackGround(colorInactiveBackGround);
    }

    private void initialize() {
        /* second the panels, cause they rely on the font-sizes */
        columnLeft = new ColumnsLeft(this);

        headerColumnPanel = new HeaderColumnPanel(this);

        headerLenPanel = new HeaderLenPanel(this);

        jHexEditorHEX = new HexLibHEX(this);
        jHexEditorHEX.setBackground(getColorHexBackGround());
        jHexEditorASCII = new HexLibASCII(this);
        jHexEditorASCII.setBackground(colorASCIIBackGround);
        calcFontObjects();

        scrlRight = new JScrollBar(JScrollBar.VERTICAL);
        scrlRight.setMinimum(0);

        pnlInactiveHeader = new HeaderChangedPanel(this);

        cursorBlink = new CursorBlink(this);
        setSelectionModel(new HexLibSelectionModel());

        setHexTransferHandler(new HexTransfer(getMe()));
    }

    public void calcFontObjects() {
        /* first the fontsize to initialize */
        fontHeight = getFontMetrics(font).getHeight();
        fontWidth = getFontMetrics(font).stringWidth(" ") + 1;
        fontMaxDescent = getFontMetrics(font).getMaxDescent();

        columnLeft.setFontObjects();
        headerColumnPanel.setFontObjects();
        headerLenPanel.setFontObjects();
        jHexEditorASCII.setFontObjects();
        jHexEditorHEX.setFontObjects();
    }

    private void addListeners() {
        scrlRight.addAdjustmentListener(this);
        myCompListener = new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                /* in case of master windows state has been changed */
                jHexEditorASCII.checkOverMaxLines();
                checkVisibleProportions();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                /* in case of master windows state has been changed */
                jHexEditorASCII.checkOverMaxLines();
                checkVisibleProportions();
            }

            private Rectangle getBounds() {
                // TODO Auto-generated method stub
                return null;
            }
        };
        addComponentListener(myCompListener);
    }

    public HexLib getMe() {
        return this;
    }

    public void buildLayout() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        addComponent(gbl, pnlInactiveHeader, 0, 0, 1, 1, 0, 0);
        addComponent(gbl, headerColumnPanel, 1, 0, 1, 1, 0, 0);
        addComponent(gbl, headerLenPanel, 2, 0, 2, 1, 0, 0);
        addComponent(gbl, columnLeft, 0, 1, 1, 1, 0, 1.0);
        addComponent(gbl, jHexEditorHEX, 1, 1, 1, 1, 0, 1.0);
        addComponent(gbl, jHexEditorASCII, 2, 1, 1, 1, 0, 1.0);
        addComponent(gbl, scrlRight, 3, 1, 1, 1, 0, 1.0);
    }

    private void addComponent(GridBagLayout gbl,
                              JComponent c,
                              int x,
                              int y,
                              int width,
                              int height,
                              double weightx,
                              double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbl.setConstraints(c, gbc);
        add(c);
    }

    public void dispose() {
        scrlRight.removeAdjustmentListener(this);
        removeComponentListener(myCompListener);
        jHexEditorASCII.dispose();
        jHexEditorHEX.dispose();
        headerColumnPanel.dispose();
        headerLenPanel.dispose();

        selectionModel.dispose();
    }

    public byte[] getByteContent() {
        return buff;
    }

    public void setByteContent(byte[] buff) {
        this.buff = buff;
        /*
         * since the old buf content not any longer exists ==> initiate GarbageCollector to do
         * his work
         */
        System.gc();

        if (cursorBlink != null) {
            cursorBlink.stop();
        }
        cursorBlink = null;
        headerLenPanel.setStrLength(buff != null ? buff.length : 0);
        if (buff == null) {
            buff = new byte[0];
            scrlRight.setMaximum(0);
            maxLineas = 0;
        }
        resetHashCode();

        if (getLines() == 0) {
            setLines(10);
        }
        maxLineas = (buff.length / 16);
        if (maxLineas * 16 < buff.length) {
            maxLineas++;
        }
        scrlRight.setMaximum(maxLineas);
        clearSelection();
        setStart(0);
        columnLeft.setMaxDigits((int) (Math.log(buff.length) / Math.log(16)));
        maxHeight = maxLineas * fontHeight + BasicPanel.borderTwice;
        jHexEditorASCII.setCursorPosition(0);
        jHexEditorASCII.requestFocus();
        revalidate();
        repaint();

        cursorBlink = new CursorBlink(this);
        checkVisibleProportions();
    }

    public void resetHashCode() {
        hashCode = calcHashCode();
        reCalcHashCode = hashCode;
    }

    private void clearSelection() {
        selectionModel.clear();
    }

    private long reCalcHashCode;

    public void paint(Graphics g) {
        super.paint(g);
    }

    protected int getStart() {
        return start;
    }

    protected int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        if (lines < minLines) {
            this.lines = minLines;
        } else {
            this.lines = lines;
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (isContinousScroll() || !e.getValueIsAdjusting()) {
            // System.out.println("paint adjusted " + System.currentTimeMillis() + "/"
            // + e.getValue() + "/" + sb.getMaximum());
            if (e.getValue() != getStart()) {
                setStart(e.getValue());
                jHexEditorHEX.checkOverMaxLines();
                repaint();
            }
        }
    }

    protected static String convertToHex(int num, int countLen) {
        String convInt = "0";
        if (num > 0) {
            convInt = Integer.toHexString(num);
        } else if (num < 0) {
            return Integer.toHexString(num & 0xff);
        }
        if (convInt.length() == countLen) {
            return convInt;
        }
        StringBuilder conv = new StringBuilder(countLen + 1);
        for (int i = 0; i < countLen - convInt.length(); i++) {
            conv.append('0');
        }
        conv.append(convInt);
        return conv.toString();
    }

    public void setStart(int start) {
        if (start < 0) {
            this.start = 0;
        } else if (start > maxLineas) {
            this.start = maxLineas;
        } else {
            this.start = start;
        }
    }

    public void repaintCursorOnly() {
        if (jHexEditorASCII.hasFocus()) {
            jHexEditorASCII.repaintCursorOnly();
        } else if (jHexEditorHEX.hasFocus()) {
            jHexEditorHEX.repaintCursorOnly();
        }
    }

    public void setColorHexBackGround(Color colorHexBackGround) {
        this.colorHexBackGround = colorHexBackGround;
        jHexEditorHEX.setBackground(colorHexBackGround);
        jHexEditorHEX.repaint();
        jHexEditorASCII.setBackground(colorHexBackGround);
        jHexEditorASCII.repaint();
    }

    public Color getColorHexBackGround() {
        return colorHexBackGround;
    }

    public void setColorInactiveBackGround(Color colorInactiveBackGround) {
        this.colorInactiveBackGround = colorInactiveBackGround;
        pnlInactiveHeader.setBackground(colorInactiveBackGround);
        BasicPanel.setColorInactiveBackGround(colorInactiveBackGround);
        repaint();
    }

    public Color getColorInactiveBackGround() {
        return colorInactiveBackGround;
    }

    public void setColorBorderBackGround(Color colorBorderBackGround) {
        colorHexCounterBackGround = colorBorderBackGround;
        headerColumnPanel.setBackground(colorBorderBackGround);
        columnLeft.setBackground(colorBorderBackGround);
        repaint();
    }

    public Color getColorBorderBackGround() {
        return colorHexCounterBackGround;
    }

    public void setConvertBytesLen(boolean convertBytesLen) {
        headerLenPanel.setConvertBytes(convertBytesLen);
    }

    public boolean isConvertBytesLen() {
        return headerLenPanel.isConvertBytes();
    }

    public long getBlinkIntervall() {
        return cursorBlink.getBlinkIntervall();
    }

    public void setBlinkIntervall(String text) {
        if (cursorBlink != null) {
            cursorBlink.setBlinkIntervall(Long.parseLong(text));
        }

    }

    /**
     * set the cursor for the Hex AND ASCII-part and scroll the view to the cursor-position
     *
     * @param cursPos
     */
    public void setCursorPostion(int cursPos) {
        setCursorPostion(cursPos + "");
    }

    /**
     * set the cursor for the Hex AND ASCII-part and scroll the view to the cursor-position
     *
     * @param cursPos
     */
    public void setCursorPostion(String cursPos) {
        int mu = Integer.parseInt(cursPos);
        if (mu > buff.length) {
            jHexEditorASCII.setCursorPosition(buff.length - 1);
        } else {
            jHexEditorASCII.setCursorPosition(mu);
        }
        selectionModel.clear();
        int curMinPosDisplayed = getStart() * 16;
        int curMaxPosDisplayed = curMinPosDisplayed + getLines() * 16;
        if (jHexEditorASCII.getCursorPosition() > curMaxPosDisplayed
                || jHexEditorASCII.getCursorPosition() < curMinPosDisplayed) {
            int muStart = jHexEditorASCII.getCursorPosition() / 16 - 2;
            if (muStart < 0) {
                muStart = 0;
            }
            if (muStart + getLines() > maxLineas) {
                muStart = maxLineas - getLines()
                        /* cause the first line is counted as 0 */
                        - 1;
            }
            setStart(muStart);
            scrlRight.setValue(muStart);
        }

        repaint();

    }

    public int getCursorPosition() {
        return jHexEditorHEX.getCursorPosition();
    }

    public void setHexAlwaysStartFirstPosition(boolean isHexAlwaysStartFirstPosition) {
        jHexEditorHEX.setHexAlwaysStartFirstPosition(isHexAlwaysStartFirstPosition);
    }

    public boolean isHexAlwaysStartFirstPosition() {
        return jHexEditorHEX.isHexAlwaysStartFirstPosition();
    }

    public void requestFocus4Hex() {
        jHexEditorHEX.requestFocus();
    }

    public void requestFocus4ASCII() {
        jHexEditorASCII.requestFocus();
    }

    public boolean isContentChanged() {
        return hashCode != reCalcHashCode;
    }

    protected void reCalcHashCode() {
        /* run in separate Thread to reduce the workload on large files while typing */
        Executors.newCachedThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                final long oldHash = reCalcHashCode;
                reCalcHashCode = calcHashCode();
                if (oldHash != reCalcHashCode) {
                    /* since the hashCode was changed ==> update */
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            pnlInactiveHeader.repaint();
                            firePropertyChange(contentChangedProperty,
                                    // true, false);
                                    hashCode == oldHash,
                                    hashCode == reCalcHashCode);
                        }
                    });
                }
            }
        });
    }

    private synchronized long calcHashCode() {
        return Arrays.hashCode(buff);
    }

    public void setFontSize(int newFontSize) {
        font = font.deriveFont((float) newFontSize);
        fontBold = fontBold.deriveFont((float) newFontSize);
        calcFontObjects();

        revalidate();
        repaint();
    }

    public boolean isHexBeanEditable() {
        return txtFieldContainer.isEditable();
    }

    public void setHexBeanEditable(boolean isEditable) {
        txtFieldContainer.setEditable(isEditable);
        repaint();
    }

    public boolean isHexBeanEnabled() {
        return txtFieldContainer.isEnabled();
    }

    public void setHexBeanEnabled(boolean isEditable) {
        txtFieldContainer.setEnabled(isEditable);
        repaint();
    }

    public Color getDisabledBG() {
        return disabledBG;
    }

    public void setDisabledBG(Color disabledBG) {
        this.disabledBG = disabledBG;
    }

    public Color getInactiveBG() {
        return inactiveBG;
    }

    public void setInactiveBG(Color inactiveBG) {
        this.inactiveBG = inactiveBG;
    }

    public Color getInactiveFG() {
        return inactiveFG;
    }

    public void setInactiveFG(Color inactiveFG) {
        this.inactiveFG = inactiveFG;
    }

    public boolean isHexEditorVisible() {
        return jHexEditorHEX.isVisible();
    }

    public void setHexEditorVisible(boolean b) {
        jHexEditorHEX.setVisible(b);
        headerColumnPanel.setVisible(b);
    }

    public boolean isASCIIEditorVisible() {
        return jHexEditorASCII.isVisible();
    }

    public void setASCIIEditorVisible(boolean b) {
        jHexEditorASCII.setVisible(b);
        headerLenPanel.setVisible(b);
    }

    /**
     * Returns the index within this buffer of the first occurrence of the specified
     * byte-array. <br>
     * If no such sequence is found , then <code>-1</code> is returned.
     *
     * @param dataToFind      the search String
     * @param isDataString    true if the String is a String false if the the dataToFind is a
     *                        representation of bytes, e.g. "0a5f"
     * @param isCaseSensitive true when the search should be caseSenistive. Is only used, when
     *                        <code>isDataString==false</code>
     * @param startPos        startPosition from which the search should start
     * @return the index of the first occurrence of the String or <code>-1</code> if not
     * found.
     */
    public int indexOf(String dataToFind,
                       boolean isDataString,
                       boolean isCaseSensitive,
                       int startPos) {
        if (isDataString) {
            byte[] srchBytesLower = null;
            byte[] srchBytesUpper = null;
            if (isCaseSensitive) {
                srchBytesLower = dataToFind.getBytes();
                srchBytesUpper = dataToFind.getBytes();
            } else {
                srchBytesUpper = dataToFind.toUpperCase().getBytes();
                srchBytesLower = dataToFind.toLowerCase().getBytes();
            }
            for (byte curByte : srchBytesLower) {
                if (curByte >= 0x80) {
                    /* convert into signed Bytes */
                    curByte -= 0xFF;
                }
            }
            for (byte curByte : srchBytesUpper) {
                if (curByte >= 0x80) {
                    /* convert into signed Bytes */
                    curByte -= 0xFF;
                }
            }
            return indexOf(srchBytesLower, srchBytesUpper, startPos);
        } else {
            byte[] srchBytes = fromHexString(dataToFind);
            return indexOf(srchBytes, startPos);
        }
    }

    public int indexOf(byte[] dataToFind, int startPos) {
        return indexOf(dataToFind, dataToFind, startPos);
    }

    private int indexOf(byte[] dataToFindLowerCase,
                        byte[] dataToFindUpperCase,
                        int startPos) {
        // public int indexOf(byte[] srcData, byte[] dataToFind) {
        int iDataLen = buff.length;
        int iDataToFindLen = dataToFindLowerCase.length;
        // boolean bGotData = false;
        int iMatchDataCntr = 0;
        if (startPos < 0) {
            startPos = 0;
        } else if (startPos > iDataLen) {
            startPos = iDataLen - 1;
        }
        for (int i = startPos; i < iDataLen; i++) {
            if (buff[i] == dataToFindLowerCase[iMatchDataCntr]
                    || buff[i] == dataToFindUpperCase[iMatchDataCntr]) {
                iMatchDataCntr++;
                // bGotData = true;
            } else {
                if (buff[i] == dataToFindLowerCase[0]
                        || buff[i] == dataToFindUpperCase[0]) {
                    iMatchDataCntr = 1;
                } else {
                    iMatchDataCntr = 0;
                    // bGotData = false;
                }
            }

            if (iMatchDataCntr == iDataToFindLen) {
                return i - iDataToFindLen + 1;
            }
        }
        return -1;
    }

    public int lastIndexOf(String dataToFind,
                           boolean isDataString,
                           boolean isCaseSensitive,
                           int startPos) {
        if (isDataString) {
            byte[] srchBytesLower;
            byte[] srchBytesUpper;
            if (isCaseSensitive) {
                srchBytesLower = dataToFind.getBytes();
                srchBytesUpper = dataToFind.getBytes();
            } else {
                srchBytesUpper = dataToFind.toUpperCase().getBytes();
                srchBytesLower = dataToFind.toLowerCase().getBytes();
            }
            for (byte curByte : srchBytesLower) {
                if (curByte >= 0x80) {
                    /* convert into signed Bytes */
                    curByte -= 0xFF;
                }
            }
            for (byte curByte : srchBytesUpper) {
                if (curByte >= 0x80) {
                    /* convert into signed Bytes */
                    curByte -= 0xFF;
                }
            }
            return lastIndexOf(srchBytesLower, srchBytesUpper, startPos);
        } else {
            byte[] srchBytes = fromHexString(dataToFind);
            return lastIndexOf(srchBytes, startPos);
        }
    }

    public int lastIndexOf(byte[] dataToFind, int startPos) {
        return lastIndexOf(dataToFind, dataToFind, startPos);

    }

    private int lastIndexOf(byte[] dataToFindLowerCase,
                            byte[] dataToFindUpperCase,
                            int startPos) {
        int iDataLen = buff.length - 1;
        int iDataToFindLen = dataToFindLowerCase.length - 1;
        int iMatchDataCntr = iDataToFindLen;
        if (startPos < 0) {
            startPos = iDataLen;
        } else if (startPos > iDataLen) {
            startPos = iDataLen;
        }
        for (int i = startPos; i >= 0; i--) {
            if (buff[i] == dataToFindLowerCase[iMatchDataCntr]
                    || buff[i] == dataToFindUpperCase[iMatchDataCntr]) {
                iMatchDataCntr--;
            } else {
                if (buff[i] == dataToFindLowerCase[iDataToFindLen]
                        || buff[i] == dataToFindUpperCase[iDataToFindLen]) {
                    iMatchDataCntr = iDataToFindLen - 1;
                } else {
                    iMatchDataCntr = iDataToFindLen;
                }
            }

            if (iMatchDataCntr < 0) {
                return i;
            }
        }

        return -1;
    }

    /*-------------------------------------------------------------*/
    /*--------------For Search of Hex-String-----------------------*/
    /*-------------------------------------------------------------*/

    /**
     * precomputed translate table for chars 0..'f'
     */
    private static final byte[] correspondingNibble = new byte['f' + 1];

    // -------------------------- STATIC METHODS --------------------------

    static {
        // only 0..9 A..F a..f have meaning. rest are errors.
        for (int i = 0; i <= 'f'; i++) {
            correspondingNibble[i] = -1;
        }
        for (int i = '0'; i <= '9'; i++) {
            correspondingNibble[i] = (byte) (i - '0');
        }
        for (int i = 'A'; i <= 'F'; i++) {
            correspondingNibble[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'a'; i <= 'f'; i++) {
            correspondingNibble[i] = (byte) (i - 'a' + 10);
        }
    }

    /**
     * convert a single char to corresponding nibble using a precalculated array. Based on
     * code by: Brian Marquis Orion Group Software Engineers http://www.ogse.com
     *
     * @param c char to convert. must be 0-9 a-f A-F, no spaces, plus or minus signs.
     * @return corresponding integer 0..15
     * @throws IllegalArgumentException on invalid c.
     */
    private int charToNibble(char c) {
        if (c > 'f') {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
        int nibble = correspondingNibble[c];
        if (nibble < 0) {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
        return nibble;
    }

    /**
     * Convert a hex string to an unsigned byte array. Permits upper or lower case hex.
     *
     * @param s String must have even number of characters. and be formed only of digits 0-9
     *          A-F or a-f. No spaces, minus or plus signs.
     * @return corresponding unsigned byte array. see
     * http://mindprod.com/jgloss/unsigned.html
     */
    private byte[] fromHexString(String s) {
        int stringLength = s.length();
        if ((stringLength & 0x1) != 0) {
            throw new IllegalArgumentException("fromHexString requires an even number of hex characters");
        }
        byte[] bytes = new byte[stringLength / 2];
        for (int i = 0, j = 0; i < stringLength; i += 2, j++) {
            int high = charToNibble(s.charAt(i));
            int low = charToNibble(s.charAt(i + 1));
            // You can store either unsigned 0..255 or signed -128..127 bytes in a byte type.
            bytes[j] = (byte) ((high << 4) | low);
        }
        return bytes;
    }

    /**
     * checks if the content will be repainted while the Scrollbar is moved up/down
     *
     * @return
     */
    public boolean isContinousScroll() {
        return continousScroll;
    }

    /**
     * checks if the content will be repainted while the Scrollbar is moved up/down
     *
     * @return
     */
    public void setContinousScroll(boolean continousScroll) {
        this.continousScroll = continousScroll;
    }

    public void checkVisibleProportions() {
        if (getLines() > 0) {
            int extent =
                    (int) ((maxLineas - getLines()) * (float) getLines() / maxLineas);
            Rectangle rec = getMe().getBounds();
            int starterLines = -2; // headerline
            int muLineas = (rec.height / fontHeight) + starterLines;
            if (muLineas == starterLines) {
                return;
            }
            /*
             * required to update everything, cause e.g. new file loaded at scroll to the end,
             * ONLY the Hex is painted properly, while the ASCII remains on the same part
             */
            setLines(muLineas);
            if (getLines() > maxLineas) {
                /* in case of a small file */
                setLines(maxLineas);
                setStart(0);
            }

            scrlRight.setVisibleAmount(getLines());
            /*
             * reposition the Scrollbar cause with few lines the position might have changed
             * after a resize
             */
            scrlRight.setValue(getStart());
        }
    }

    @Override
    public void scrollRectToVisible(Rectangle aRect) {
        // System.out.println("scroll Rect to visible: " + aRect);
        super.scrollRectToVisible(aRect);
    }

    public HexLibSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void setSelectionModel(HexLibSelectionModel selectionModel) {
        this.selectionModel = selectionModel;
        this.selectionModel.setUpdatePainter(this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        System.out.println("lost ownership with copy-paste");
    }

    public HexTransfer getHexTransferHandler() {
        return hexTransfer;
    }

    public void setHexTransferHandler(HexTransfer hexTransferHandler) {
        this.hexTransfer = hexTransferHandler;
    }

    protected boolean isOwnFocusComponent(Component oppositeComponent) {
        if (oppositeComponent == columnLeft
                || oppositeComponent == headerColumnPanel
                || oppositeComponent == headerLenPanel
                || oppositeComponent == scrlRight
                || oppositeComponent == jHexEditorASCII
                || oppositeComponent == jHexEditorHEX) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasFocus() {
        return columnLeft.hasFocus() || headerColumnPanel.hasFocus()
                || headerLenPanel.hasFocus() || scrlRight.hasFocus()
                || jHexEditorASCII.hasFocus() || jHexEditorHEX.hasFocus();
    }

    public static boolean instanceOfHexLib(Object obj) {
        if (obj instanceof HexLib || obj instanceof HexLibHEX
                || obj instanceof HexLibASCII || obj instanceof HeaderLenPanel
                || obj instanceof HeaderChangedPanel
                || obj instanceof HeaderColumnPanel) {
            return true;
        }
        return false;
    }
}
