/*
 * Created on 14.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import java.awt.*;

public class ColumnsLeft extends BasicPanel {

    /*
     * how many digits are displayed on the left-side
     */
    private int maxDigits = 8;
    HexLib he;
    Color fontForeground = Color.black;

    public ColumnsLeft(HexLib he) {
        super(he);
        this.he = he;

        this.setLayout(new BorderLayout(1, 1));
        setDigitCounter(maxDigits);
        hasStripes = true;
        setBackground(he.getColorBorderBackGround());
        createZebraColors(he.getColorBorderBackGround(),
                he.getColorBorderBackGround());
    }

    public void setDigitCounter(int count) {
        maxDigits = count;
        setFontObjects();
    }

    public void setFontObjects() {
        minWidth = HexLib.fontWidth * (maxDigits + 1) + borderTwice;
        setPreferredSize(new Dimension(minWidth, 0));
        setMinimumSize(new Dimension(minWidth, 0));
    }

    public void setMaxDigits(int maxDigit) {
        if (maxDigit < 1) {
            maxDigit = 1;
        }
        setDigitCounter(maxDigit);
    }

    public int getMaxDigits() {
        return maxDigits;
    }

    public void paint(Graphics g) {
        /*
         * required especially for resize during e.g. no content yet set, but content will be
         * painted, and no proper layout is yet done!!! (otherwise the content looks weird)
         */
        minWidth = getSize().width;
        super.paint(g);
        g.setColor(fontForeground);

        /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
        end = Math.min(ini + he.getLines() + 2, he.maxLineas);
        int y = 0;
        for (int n = ini; n < end; n++) {
            if (checkCurPosPaintable(n)) {
                // if (n == (getCursorPosition() / 16)) {
                createRect(g, 0, y, maxDigits + 1);
                g.setFont(HexLib.fontBold);
            } else {
                g.setFont(HexLib.font);
            }
            printString(g, he.convertToHex(n, maxDigits) + ".:", 0, y++);
        }
    }

    protected boolean checkCurPosPaintable(int pos2Check) {
        return (pos2Check == getCursorPosition() / 16 && getSelectionModel().isPositionWithinMarkPos(pos2Check));
    }

}
