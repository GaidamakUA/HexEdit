package at.HexLib.GUI.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class WideComboBox extends JComboBox {

    public WideComboBox() {
    }

    public WideComboBox(final Object items[]) {
        super(items);
    }

    public WideComboBox(ArrayList<Object> items) {
        super(items.toArray());
    }

    public WideComboBox(ComboBoxModel aModel) {
        super(aModel);
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