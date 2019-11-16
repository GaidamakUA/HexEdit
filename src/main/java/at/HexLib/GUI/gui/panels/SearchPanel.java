/*
 * Created on 17.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.panels;

import at.HexLib.GUI.Listener.MyItemListener;
import at.HexLib.GUI.Listener.SearchActionListener;
import at.HexLib.GUI.gui.ComponentTitledBorder;
import at.HexLib.GUI.main.HexEditSample;
import at.HexLib.library.HexLib;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class SearchPanel extends JPanel {

    public HexEditSample he;
    public HexLib hexEditor;

    public JTextField txtSearchString;
    public JTextField txtSearchStart;
    public JCheckBox chkSearchAsString;
    public JCheckBox chkSearchCaseSensitive;
    public JCheckBox chkSearchMarkAll;
    public JButton btnSearchStart;
    public JButton btnSearchReset;
    public JRadioButton rdSearchDirectionForward;
    public JRadioButton rdSearchDirectionBackward;

    public SearchPanel(HexEditSample he) {
        this.he = he;
        hexEditor = he.hexEditor;
        initialize();
        buildSearchPanel();
        addListeners();
    }

    private void initialize() {
        txtSearchString = new JTextField(15);
        txtSearchStart = new JTextField();
        txtSearchStart.setText("-1");
        chkSearchAsString = new JCheckBox("Treat Search String as Byte code");
        chkSearchAsString.setToolTipText("<html>if selected the string '<i>05aefb</i>' is search in the hex-code and <b>not</b> in the ASCII-part");
        chkSearchCaseSensitive = new JCheckBox("Search string is case senitive");
        btnSearchStart = new JButton("Search");
        btnSearchReset = new JButton("Clear entry fields");
        rdSearchDirectionForward = new JRadioButton("Forward");
        rdSearchDirectionBackward = new JRadioButton("Backward");
        ButtonGroup searchDirGroup = new ButtonGroup();
        searchDirGroup.add(rdSearchDirectionForward);
        searchDirGroup.add(rdSearchDirectionBackward);
        rdSearchDirectionForward.setSelected(true);

        chkSearchMarkAll = new JCheckBox("Mark all");
        chkSearchMarkAll.setSelected(true);
    }

    private void buildSearchPanel() {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalStrut(2)); // for the border
        final Component buildLayoutSearch = buildLayoutSearch();
        add(buildLayoutSearch);
        add(Box.createHorizontalStrut(2)); // for the border

        final JCheckBox chkCursorDisplay = new JCheckBox("Show Details");
        chkCursorDisplay.setSelected(true);
        final ComponentTitledBorder componentBorder =
                new ComponentTitledBorder("Search",
                        chkCursorDisplay,
                        this,
                        BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setBorder(componentBorder);

        chkCursorDisplay.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                buildLayoutSearch.setVisible(chkCursorDisplay.isSelected());
                componentBorder.setBorderPainted(chkCursorDisplay.isSelected());
            }
        });

    }

    private Component buildLayoutSearch() {
        FormLayout layout = new FormLayout("3dlu, p, 3dlu, f:p:g,3dlu,2*(p,3dlu)", // cols
                "4*(p,3dlu),3dlu" // rows
        );
        PanelBuilder builder = new PanelBuilder(layout);
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        int colStart = 2;
        int colMax = 9;
        int rowMax = 8;
        int cols = colStart;
        int row = 1;
        CellConstraints cc = new CellConstraints();

        builder.add(new JLabel("Search String"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(txtSearchString, cc.xyw(cols, row, colMax - cols));

        cols = colStart;
        row++;
        row++;
        builder.add(chkSearchAsString, cc.xyw(cols, row, 4));
        cols += 4;

        FormLayout layoutDir = new FormLayout("3dlu,p, 3dlu", // cols
                "d, 3dlu,d" // rows
        );
        PanelBuilder builderDir = new PanelBuilder(layoutDir);
        // DefaultFormBuilder builderDir =
        // new DefaultFormBuilder(layoutDir, new FormDebugPanel());
        CellConstraints ccDir = new CellConstraints();
        int colDir = 2;
        builderDir.add(rdSearchDirectionForward, ccDir.xy(colDir, 1));
        builderDir.add(rdSearchDirectionBackward, ccDir.xy(colDir, 3));
        builderDir.setBorder(BorderFactory.createTitledBorder("Direction"));
        builder.add(builderDir.getPanel(), cc.xywh(cols, row, 1, rowMax - row + 1));
        cols++;
        cols++;
        builder.add(chkSearchMarkAll, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(chkSearchCaseSensitive, cc.xyw(cols, row, 4));
        cols += 6;
        builder.add(btnSearchReset, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(new JLabel("Start Position"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(txtSearchStart, cc.xy(cols, row));
        cols++;
        cols++;
        cols++;
        cols++;

        builder.add(btnSearchStart, cc.xy(cols, row));

        return builder.getPanel();
    }

    private void addListeners() {
        MyItemListener myItemListener = new MyItemListener(this);

        SearchActionListener srchAction = new SearchActionListener(this);
        btnSearchStart.addActionListener(srchAction);
        btnSearchReset.addActionListener(srchAction);
        chkSearchAsString.addItemListener(myItemListener);
        txtSearchString.addActionListener(srchAction);
    }

}
