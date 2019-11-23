/*
 * Created on 07.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.panels;

import at.HexLib.GUI.Listener.MyItemListener;
import at.HexLib.GUI.gui.ComponentTitledBorder;
import at.HexLib.GUI.gui.WideComboBox;
import at.HexLib.GUI.main.HexEditSample;
import at.HexLib.library.HexTransfer;
import at.HexLib.library.HexTransfer.copyStringAction;
import at.HexLib.library.HexTransfer.insertActions;
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

public class OptionPanel extends JPanel {

    private HexEditSample he;
    private JButton btnColorHexBackGround;
    private JLabel lblColorHexBackGround;
    private JButton btnColorInactiveBackGround;
    private JLabel lblColorInactiveBackGround;

    public JCheckBox chkConvertBytes;
    private JTextField txtBlinkIntervall;
    private JButton btnBlinkIntervall;
    private JButton btnFocusHex;
    private JButton btnFocusASCII;
    public JCheckBox chkHexStartBegin;
    public JCheckBox chkHexIsEditable;
    public JCheckBox chkHexIsEnabled;
    public JCheckBox chkContinousScroll;
    public JCheckBox chkHexVisible;
    public JCheckBox chkASCIIVisible;

    public WideComboBox cmbCopyAction;
    public WideComboBox cmbPasteAction;
    public boolean isOptionSetStatus;

    public OptionPanel(HexEditSample he) {
        this.he = he;
        initialize();
        buildOptionPanel();
        addListeners();
    }

    private void initialize() {
        btnColorHexBackGround = new JButton("Change...");
        lblColorHexBackGround = new JLabel("HexEditor");
        lblColorHexBackGround.setOpaque(true);

        btnColorInactiveBackGround = new JButton("Change...");
        lblColorInactiveBackGround = new JLabel("'Border' HexEditor");
        lblColorInactiveBackGround.setOpaque(true);

        chkConvertBytes = new JCheckBox("Convert Byes into KB/MB");

        txtBlinkIntervall = new JTextField("", 5);
        btnBlinkIntervall = new JButton("Apply new Intervall");

        btnFocusASCII = new JButton("... ASCII");
        btnFocusHex = new JButton("... HEX");

        chkHexStartBegin =
                new JCheckBox("<html>Start HexEdit-MouseCursor<br><b>always</b> at first char of a Byte");
        chkHexIsEditable = new JCheckBox("is HexEdit Editable");
        chkHexIsEnabled = new JCheckBox("is HexEdit Enabled");

        chkContinousScroll = new JCheckBox("repaint while Scrollbar moves");
        chkHexVisible = new JCheckBox("set Hex editor visible");
        chkASCIIVisible = new JCheckBox("set ASCII editor visible");

        cmbCopyAction =
                new WideComboBox(new String[]{"String to clipboard",
                        "Hex representation of bytes"});
        cmbPasteAction =
                new WideComboBox(new String[]{"Overwrite content",
                        "Delete selection and insert content"});

        setOptionsFromHexLib();
    }

    private void buildOptionPanel() {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalStrut(2)); // for the border
        final Component buildLayoutOptions = buildLayoutOptions();
        add(buildLayoutOptions);
        add(Box.createHorizontalStrut(2)); // for the border

        final JCheckBox chkCursorDisplay = new JCheckBox("Show Details");
        chkCursorDisplay.setSelected(true);
        final ComponentTitledBorder componentBorder =
                new ComponentTitledBorder("Options for HexEditor",
                        chkCursorDisplay,
                        this,
                        BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setBorder(componentBorder);

        chkCursorDisplay.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                buildLayoutOptions.setVisible(chkCursorDisplay.isSelected());
                componentBorder.setBorderPainted(chkCursorDisplay.isSelected());
            }
        });

    }

    private JComponent buildLayoutOptions() {
        FormLayout layout = new FormLayout("3dlu,p, 3dlu,f:d:g,3dlu,p, 3dlu", // cols
                "9*(3dlu,p),3dlu" // rows
        );
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        PanelBuilder builder = new PanelBuilder(layout);
        int colStart = 2;
        int cols = colStart;
        int row = 2;
        int colMax = 7;
        CellConstraints cc = new CellConstraints();

        cols = colStart;
        builder.add(new JLabel("Background Color for"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(lblColorHexBackGround, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(btnColorHexBackGround, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(new JLabel("Background Color for"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(lblColorInactiveBackGround, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(btnColorInactiveBackGround, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        JPanel muPanel = new JPanel();
        muPanel.setLayout(new BoxLayout(muPanel, BoxLayout.X_AXIS));
        muPanel.add(chkConvertBytes);
        muPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        muPanel.add(chkContinousScroll);
        builder.add(muPanel, cc.xyw(cols, row, colMax - cols));

        cols = colStart;
        row++;
        row++;
        builder.add(new JLabel("Cursor time (ms)"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(txtBlinkIntervall, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(btnBlinkIntervall, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(chkHexStartBegin, cc.xyw(cols, row, colMax - cols - 1));
        // builder.add(chkHexStartBegin, cc.xyw(cols, row, colMax - cols));
        builder.add(buildFocusPanel(), cc.xywh(colMax - 1, row, 1, 5));

        cols = colStart;
        row++;
        row++;
        builder.add(chkHexIsEditable, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(chkHexIsEnabled, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(chkHexVisible, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(chkASCIIVisible, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        //    JPanel pnlFontSlider = FontSizePanel.getPanel(hexLib);
        //    pnlFontSlider.setBorder(BorderFactory.createTitledBorder("Font resize"));
        //    builder.add(pnlFontSlider, cc.xyw(cols, row, colMax - cols));

        cols = colStart;
        row++;
        row++;
        builder.add(buildCopyPastePanel(), cc.xyw(cols, row, colMax - cols));

        return builder.getPanel();

    }

    private void addListeners() {
        btnColorHexBackGround.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color c =
                        JColorChooser.showDialog(he,
                                "Choose a color...",
                                lblColorHexBackGround.getBackground());
                if (c != null) {
                    lblColorHexBackGround.setBackground(c);
                    he.hexEditor.setColorHexBackGround(c);
                }
            }
        });
        btnColorInactiveBackGround.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color c =
                        JColorChooser.showDialog(he,
                                "Choose a color...",
                                lblColorInactiveBackGround.getBackground());
                if (c != null) {
                    lblColorInactiveBackGround.setBackground(c);
                    he.hexEditor.setColorBorderBackGround(c);
                }
            }
        });

        btnBlinkIntervall.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                he.hexEditor.setBlinkIntervall(txtBlinkIntervall.getText());
            }
        });

        btnFocusASCII.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                he.pnlCursor.txtCursorChange.setText(he.hexEditor.getCursorPosition()
                        + "");
                he.pnlCursor.btnCursorChange.doClick();
                he.hexEditor.requestFocus4ASCII();
            }
        });
        btnFocusHex.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                he.pnlCursor.txtCursorChange.setText(he.hexEditor.getCursorPosition()
                        + "");
                he.pnlCursor.btnCursorChange.doClick();
                he.hexEditor.requestFocus4Hex();
            }
        });

        MyItemListener myItemListener = new MyItemListener(this);
        chkHexStartBegin.addItemListener(myItemListener);
        chkHexIsEditable.addItemListener(myItemListener);
        chkHexIsEnabled.addItemListener(myItemListener);
        chkConvertBytes.addItemListener(myItemListener);
        chkASCIIVisible.addItemListener(myItemListener);
        chkHexVisible.addItemListener(myItemListener);
        chkContinousScroll.addItemListener(myItemListener);
        cmbCopyAction.addItemListener(myItemListener);
        cmbPasteAction.addItemListener(myItemListener);

    }

    JPanel buildCopyPastePanel() {
        FormLayout layout = new FormLayout("3dlu,2*(d, 3dlu,f:'content':g,3dlu)", // cols
                "2*(3dlu,p),3dlu" // rows
        );
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        PanelBuilder builder = new PanelBuilder(layout);
        int colStart = 2;
        int cols = colStart;
        int row = 2;
        int colMax = 5;
        CellConstraints cc = new CellConstraints();

        cols = colStart;
        builder.add(new JLabel("Copy content"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(cmbCopyAction, cc.xy(cols, row));

        // row++;
        // row++;
        // cols = colStart;
        cols++;
        cols++;
        builder.add(new JLabel("Paste does"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(cmbPasteAction, cc.xy(cols, row));

        builder.setBorder(BorderFactory.createTitledBorder("Copy-Paste Settings"));
        return builder.getPanel();
    }

    JPanel buildFocusPanel() {
        FormLayout layout = new FormLayout("3dlu,f:p:g,3dlu", // cols
                "2*(3dlu,p),3dlu" // rows
        );
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        PanelBuilder builder = new PanelBuilder(layout);
        int colStart = 2;
        int cols = colStart;
        int row = 2;
        int colMax = 5;
        CellConstraints cc = new CellConstraints();

        builder.add(btnFocusASCII, cc.xy(cols, row));
        row++;
        row++;
        builder.add(btnFocusHex, cc.xy(cols, row));
        builder.setBorder(BorderFactory.createTitledBorder("Change focus to ..."));

        return builder.getPanel();
    }

    public void setOptionsFromHexLib() {
        isOptionSetStatus = true;
        txtBlinkIntervall.setText(he.hexEditor.getBlinkIntervall() + "");

        lblColorHexBackGround.setBackground(he.hexEditor.getColorHexBackGround());
        lblColorInactiveBackGround.setBackground(he.hexEditor.getColorBorderBackGround());
        chkConvertBytes.setSelected(he.hexEditor.isConvertBytesLen());
        chkContinousScroll.setSelected(he.hexEditor.isContinousScroll());

        chkHexStartBegin.setSelected(he.hexEditor.isHexAlwaysStartFirstPosition());
        chkHexIsEditable.setSelected(he.hexEditor.isHexBeanEditable());
        chkHexIsEnabled.setSelected(he.hexEditor.isHexBeanEnabled());

        chkHexVisible.setSelected(he.hexEditor.isHexEditorVisible());
        chkASCIIVisible.setSelected(he.hexEditor.isASCIIEditorVisible());

        HexTransfer hexTransferHandler = he.hexEditor.getHexTransferHandler();
        if (hexTransferHandler.getCopyAction() == copyStringAction.BINARY) {
            cmbCopyAction.setSelectedIndex(0);
        } else {
            cmbCopyAction.setSelectedIndex(1);
        }

        if (hexTransferHandler.getInsertAction() == insertActions.OVERWRITE) {
            cmbPasteAction.setSelectedIndex(0);
        } else {
            cmbPasteAction.setSelectedIndex(1);
        }
        isOptionSetStatus = false;
    }
}
