/*
 * Created on 15.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import java.awt.*;


public class HeaderColumnPanel extends BasicPanel {

    Color fontForeground = Color.black;
    String[] strHeader = new String[16];

    public HeaderColumnPanel(HexLib he) {
        super(he);
        setBackground(he.getColorBorderBackGround());

        this.setLayout(new BorderLayout(1, 1));
        setFontObjects();
    }

    public void setFontObjects() {
        minWidth = (HexLib.fontWidth * +((16 * 3) - 1)) + borderTwice;
        Dimension minDimension = new Dimension();
        minDimension.setSize(minWidth, HexLib.fontHeight);
        setPreferredSize(minDimension);
        setMinimumSize(minDimension);

        strHeader = new String[16];
        for (int i = 0; i < 16; i++) {
            strHeader[i] = "." + hexLib.convertToHex(i, 1);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(separatorLine);
        g.drawLine(minWidth - 1, 0, minWidth - 1, maxHeightPainted);
        g.setColor(fontForeground);

        // g.setColor(backGroundColor);
        // g.fillRect(0, 0, minDimension.width - 1, minDimension.height);
        // g.setColor(Color.black);

        for (int n = 0; n < 16; n++) {
            if (checkCurPosPaintable(n)) {
                rect(g, n * 3, 0, 2);
                g.setFont(HexLib.fontBold);
            } else {
                g.setFont(HexLib.font);
            }
            printString(g, strHeader[n], n * 3, 0);
        }
    }


    protected void rect(Graphics g, int x, int y, int s) {
        // fill the complete header
        g.drawRect((HexLib.fontWidth * x) + border - 1,
                (HexLib.fontHeight * y),
                (HexLib.fontWidth * s),
                HexLib.fontHeight + borderTwice - 1);
    }

    protected void printString(Graphics g, String s, int x, int y) {
        // better positioning in the header
        g.drawString(s,
                (HexLib.fontWidth * x) + border,
                ((HexLib.fontHeight * (y + 1)) - HexLib.fontMaxDescent)
                        + border
        );
    }

    protected boolean checkCurPosPaintable(int pos2Check) {
        return (pos2Check == getCursorPosition() % 16 && getSelectionModel().isPositionWithinMarkPos(pos2Check));
    }

}
