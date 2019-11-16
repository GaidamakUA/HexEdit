/*
 * Created on 07.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.panels;

import at.HexLib.GUI.Listener.CaretPositionListener;
import at.HexLib.GUI.gui.ComponentTitledBorder;
import at.HexLib.GUI.main.HexEditSample;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CursorPanel extends JPanel {

    public JFormattedTextField txtCursorPosDec;
    public JFormattedTextField txtCursorPosHex;
    public JFormattedTextField txtCursorChange;
    public JButton btnCursorChange;
    private JButton btnCursorChangeStart;
    private JButton btnCursorChangeEnd;
    private HexEditSample he;


    public CursorPanel(HexEditSample he) {
        this.he = he;
        initialize();
        buildCursorDisplay();
        addListeners();
    }

    private void initialize() {
        txtCursorPosDec = new SameHeightTextField(0);
        txtCursorPosDec.setEditable(false);
        txtCursorPosDec.setHorizontalAlignment(JTextField.RIGHT);
        txtCursorPosHex = new SameHeightTextField(0);
        txtCursorPosHex.setEditable(false);
        txtCursorPosHex.setHorizontalAlignment(JTextField.RIGHT);
        setCursorPosIntoTextField();

        txtCursorChange = new SameHeightTextField(he.hexEditor.getCursorPosition());
        txtCursorChange.setHorizontalAlignment(JTextField.RIGHT);
        btnCursorChange = new JButton("... to Input");
        btnCursorChangeEnd = new JButton("... to End");
        btnCursorChangeStart = new JButton("... to Start");
    }

    private void addListeners() {
        btnCursorChange.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                he.hexEditor.setCursorPostion(txtCursorChange.getValue().toString());
            }
        });

        btnCursorChangeStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                he.hexEditor.setCursorPostion(0 + "");
            }
        });

        btnCursorChangeEnd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                he.hexEditor.setCursorPostion((he.hexEditor.getByteContent().length - 1)
                        + "");
            }
        });

        CaretPositionListener cpl2 = new CaretPositionListener(txtCursorChange);
        cpl2.setDynamicFormatting(true);
    }

    private void buildCursorDisplay() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        final Component buildCursorInfoDisplay = buildCursorInfoDisplay();
        final Component buildCursorChangeDisplay = buildCursorChangeDisplay();
        add(Box.createHorizontalStrut(2)); // for the border
        add(buildCursorInfoDisplay);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(buildCursorChangeDisplay);

        final JCheckBox chkCursorDisplay = new JCheckBox("Show Details");
        chkCursorDisplay.setSelected(true);
        final ComponentTitledBorder componentBorder =
                new ComponentTitledBorder("Cursor",
                        chkCursorDisplay,
                        this,
                        BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setBorder(componentBorder);

        chkCursorDisplay.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                buildCursorChangeDisplay.setVisible(chkCursorDisplay.isSelected());
                buildCursorInfoDisplay.setVisible(chkCursorDisplay.isSelected());
                componentBorder.setBorderPainted(chkCursorDisplay.isSelected());
            }
        });
    }

    private Component buildCursorInfoDisplay() {
        FormLayout layout = new FormLayout("3dlu, f:p:g, 3dlu", // cols
                "f:d,3dlu,f:d,4dlu,f:d" // rows
        );
        PanelBuilder builder = new PanelBuilder(layout);
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        int colStart = 2;
        int cols = colStart;
        int row = 1;
        CellConstraints cc = new CellConstraints();

        builder.add(new JLabel("Current Position"), cc.xy(cols, row, "c,c"));

        cols = colStart;
        row++;
        row++;
        builder.add(txtCursorPosDec, cc.xy(cols, row));
        row++;
        row++;
        builder.add(txtCursorPosHex, cc.xy(cols, row));

        return builder.getPanel();

    }

    private Component buildCursorChangeDisplay() {
        FormLayout layout = new FormLayout("3dlu,f:p:g,6dlu,f:p:g,3dlu", // cols
                "3*(f:d,3dlu),f:d,3dlu" // rows
        );
        PanelBuilder builder = new PanelBuilder(layout);
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        int colStart = 2;
        int cols = colStart;
        int row = 1;
        int colMax = 5;
        CellConstraints cc = new CellConstraints();

        builder.add(new JLabel("Change Position"),
                cc.xyw(cols, row, colMax - cols, "c,c"));

        cols = colStart;
        row++;
        row++;
        builder.add(txtCursorChange, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(btnCursorChangeStart, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(btnCursorChange, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(btnCursorChangeEnd, cc.xy(cols, row));

        return builder.getPanel();
    }

    public void setCursorPosIntoTextField() {
        txtCursorPosDec.setValue(he.hexEditor.getCursorPosition());
        txtCursorPosHex.setText("0x"
                + Integer.toHexString(he.hexEditor.getCursorPosition()));
    }

    private class SameHeightTextField extends JFormattedTextField {

        public SameHeightTextField(Integer value) {
            // super(NumberFormat.getInstance());
            setValue(value);
            // super(string, i);
        }

        @Override
        public Dimension getPreferredSize() {
            return btnCursorChange.getPreferredSize();
        }
    }

}
