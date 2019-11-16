/*
 * Created on 15.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import java.awt.*;
import java.text.NumberFormat;

public class HeaderLenPanel extends BasicPanel {

    private String strLength = "";
    private static double iKiloByte = 1024;
    private static double iMegaByte = iKiloByte * iKiloByte;
    private static String strBytes = "Bytes";
    private static String strKiloByte = "KByte";
    private static String strMegaByte = "MByte";

    private boolean convertBytes = false;
    private int calcLen;
    Color fontForeground = Color.black;

    public HeaderLenPanel(HexLib he) {
        super(he);
        setBackground(colorLenHeaderBackGround);
        setFontObjects();
    }

    public void setFontObjects() {
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
        g.setColor(fontForeground);
        printString(g, getStrLength(), 0.25d, 0);
        g.setColor(oldColor);
    }

    public void setStrLength(int buffLength) {
        calcLen = buffLength;
        StringBuilder result = new StringBuilder(30);
        result.append("Length: ");
        if (buffLength > iMegaByte && convertBytes) {
            result.append(round(buffLength / iMegaByte, 2));
            result.append(" ");
            result.append(strMegaByte);
        } else if (buffLength > iKiloByte && convertBytes) {
            result.append(round(buffLength / iKiloByte, 2));
            result.append(" ");
            result.append(strKiloByte);
        } else {
            result.append(NumberFormat.getIntegerInstance().format(buffLength));
            result.append(" ");
            result.append(strBytes);
        }
        strLength = result.toString();
    }

    public String getStrLength() {
        return strLength;
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void setConvertBytes(boolean convertBytes) {
        this.convertBytes = convertBytes;
        setStrLength(calcLen);
        repaint();
    }

    public boolean isConvertBytes() {
        return convertBytes;
    }

}
