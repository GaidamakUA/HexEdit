package at.HexLib.GUI.test;

//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//http://terai.xrea.jp/Swing/DnDExportTabbedPane.html

import at.HexLib.GUI.gui.tabs.ButtonTabComponent;
import at.HexLib.GUI.gui.tabs.DnDTabbedPane;
import at.HexLib.GUI.gui.tabs.TabTransferHandler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import org.pushingpixels.substance.api.SubstanceConstants.MenuGutterFillKind;
//import org.pushingpixels.substance.api.SubstanceLookAndFeel;

public class MainPanel extends JPanel {

    private final DnDTabbedPane tab = new DnDTabbedPane();

    public MainPanel() {
        super(new BorderLayout());
        DnDTabbedPane sub = new DnDTabbedPane();
        sub.addTab("Title aa", new JLabel("aaa"));
        sub.addTab("Title bb", new JScrollPane(new JTree()));
        sub.addTab("Title cc",
                new JScrollPane(new JTextArea("123412341234\n46746745\n245342\n")));

        tab.addTab("JTree 00", new JScrollPane(new JTree()));
        tab.addTab("JLabel 01", new JLabel("Test"));
        tab.addTab("JTable 02", new JScrollPane(makeJTable()));
        tab.addTab("JTextArea 03", new JScrollPane(makeJTextArea()));
        // tab.addTab("JLabel 04",
        // new JLabel("<html>asfasfdasdfasdfsa<br>asfdd13412341234123446745fgh"));
        // tab.addTab("null 05", null);
        // tab.addTab("JTabbedPane 06", sub);
        // tab.addTab("Title 000000000000000006", new JScrollPane(new JTree()));
        // ButtonTabComponent
        for (int i = 0; i < tab.getTabCount(); i++) {
            tab.setTabComponentAt(i, new ButtonTabComponent(tab, false));
        }

        /********** additional ****/
        JLabel lblEmpty = new JLabel("<html><p align=center>Empty space for new Tab</p>");
        lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
        tab.addTab("",
                lblEmpty);
        ButtonTabComponent componentPlus = new ButtonTabComponent(tab, true);

        tab.setTabComponentAt(tab.getTabCount() - 1, componentPlus);
        componentPlus.setPlusTabCompontents("Empty tab",
                "<html><p align=center>Empty content</p>");
        tab.setEnabledAt(tab.getTabCount() - 1, false);

        tab.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                Component sel = tab.getSelectedComponent();
                System.out.println("current selection = " + sel.hashCode() + " " + sel);
            }
        });

        /********** additional ****/
        DnDTabbedPane sub2 = new DnDTabbedPane();
        sub2.addTab("Title aa", new JLabel("aaa"));
        sub2.addTab("Title bb", new JScrollPane(new JTree()));
        sub2.addTab("Title cc",
                new JScrollPane(new JTextArea("123412341234\n46746745\n245342\n")));

        TransferHandler handler = new TabTransferHandler();
        for (JTabbedPane t : java.util.Arrays.asList(tab, sub, sub2)) {
            t.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            t.setTransferHandler(handler);
        }

        JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(tab);
        p.add(sub2);
        add(p);
        add(makeCheckBoxPanel(), BorderLayout.NORTH);
        setPreferredSize(new Dimension(320, 240));
    }

    private JComponent makeCheckBoxPanel() {
        final JCheckBox tcheck = new JCheckBox("Top", true);
        tcheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tab.setTabPlacement(tcheck.isSelected() ? JTabbedPane.TOP
                        : JTabbedPane.RIGHT);
            }
        });
        final JCheckBox scheck = new JCheckBox("SCROLL_TAB_LAYOUT", true);
        scheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tab.setTabLayoutPolicy(scheck.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT
                        : JTabbedPane.WRAP_TAB_LAYOUT);
            }
        });
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(tcheck);
        p.add(scheck);
        return p;
    }

    private JTextArea makeJTextArea() {
        JTextArea textArea = new JTextArea("asfasdfasfasdfas\nafasfasdfaf\n");
        textArea.setTransferHandler(null); // XXX
        return textArea;
    }

    private JTable makeJTable() {
        String[] columnNames = {"String", "Integer", "Boolean"};
        Object[][] data = {{"AAA", 1, true}, {"BBB", 2, false},};
        TableModel model = new DefaultTableModel(data, columnNames) {

            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        return new JTable(model);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
            // SubstanceLookAndFeel.setSkin(new TwilightSkin());
            //      SubstanceLookAndFeel.setToUseConstantThemesOnDialogs(true);
            //      UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, true);
            //      UIManager.put(SubstanceLookAndFeel.MENU_GUTTER_FILL_KIND,
            //                    MenuGutterFillKind.HARD_FILL);

        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("DnDExportTabbedPane");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

