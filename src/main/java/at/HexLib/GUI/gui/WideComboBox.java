package at.HexLib.GUI.gui;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class WideComboBox extends JComboBox {

    public WideComboBox(final Object items[]) {
        super(items);
    }

    private boolean layingOut = false;

    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    @Override
    public Dimension getSize() {
        Dimension dim = super.getSize();
        if (!layingOut) {
            dim.width = Math.max(dim.width, getPreferredSize().width);
            dim.width = Math.min(dim.width, Toolkit.getDefaultToolkit().getScreenSize().width);
        }
        return dim;
    }
}