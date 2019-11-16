/*
 * Created on 15.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import java.awt.*;

public class HeaderChangedPanel extends BasicPanel {

    private String strContentChanged = "*";
    private int iStrLength = 0;
    private int iStrHeight = 0;

    public HeaderChangedPanel(HexLib he) {
        super(he);
        setBackground(he.getColorInactiveBackGround());
        Dimension minDimension = new Dimension();
        minDimension.setSize(0, HexLib.fontHeight + borderTwice);
        setPreferredSize(minDimension);
    }


    @Override
    public void setBackground(Color backGroundColor) {
        super.setBackground(backGroundColor);
    }

    @Override
    public void paint(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, getBounds().width, getBounds().height);
        g.setColor(fontChangedForeground);
        // g.setColor(Color.yellow);
        if (he.isContentChanged()) {
            g.setFont(HexLib.fontBold);
            g.drawString(strContentChanged,
                    (getBounds().width - HexLib.fontWidth) / 2
                    ,
                    (getBounds().height - HexLib.fontHeight - HexLib.fontMaxDescent)
                            / 2
                            + HexLib.fontHeight - 1);
        }
        g.setColor(oldColor);
    }


}
