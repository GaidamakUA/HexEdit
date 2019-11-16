package at.HexLib.GUI.main;

import at.HexLib.GUI.ButtonBarFactory;
import at.HexLib.GUI.IO.LoadContent;
import at.HexLib.GUI.IO.SaveContent;
import at.HexLib.GUI.Listener.CaretPositionListener;
import at.HexLib.GUI.Listener.PaneChangeListener;
import at.HexLib.GUI.Listener.PropertiesListener;
import at.HexLib.GUI.Listener.WindowList;
import at.HexLib.GUI.gui.FileAutoCompleter;
import at.HexLib.GUI.gui.panels.CursorPanel;
import at.HexLib.GUI.gui.panels.OptionPanel;
import at.HexLib.GUI.gui.panels.SearchPanel;
import at.HexLib.GUI.gui.tabs.DnDTabbedPane;
import at.HexLib.GUI.gui.tabs.TabTransferHandler;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class HexEditSample extends JFrame {

    public JButton btnLoad;
    private JButton btnFileDialog;
    public JButton btnSave;
    private JButton btnSaveAs;
    private JFileChooser fc;
    private JFormattedTextField txtSizeEmptyField;
    private JButton btnSizeEmpty;
    private JButton btnReSizeLength;
    private String strfileName = "d:\\TMP\\vlc-1.1.2-win32.exe";
    // private String strfileName = "";
    byte[] byteContent = new byte[0];
    public HexLibContainer hexEditor;
    public JTextField txtFileName;
    private HeapView heapView;

    LoadContent loadContent = new LoadContent();
    public CursorPanel pnlCursor;
    public OptionPanel pnlOptions;
    private SearchPanel pnlSearch;
    private JTabbedPane tbPnSettings;

    public final DnDTabbedPane tabPnHexEdit = new DnDTabbedPane();
    public static HexEditSample masterReference = null;

    public HexEditSample() {
        System.out.println("Start 1: " + System.currentTimeMillis());
        masterReference = this;

        initialize();
        setTitle("HexEdit - Editor&Viewer - Sample V0.50");

        System.out.println("Start 1: " + System.currentTimeMillis());

        add(buildLayout());
        System.out.println("Start 2: " + System.currentTimeMillis());
        addListeners();
        pack();
        setVisible(true);

        if (txtFileName.getText().length() > 0) {
            loadHexEditor(true);
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            System.out.println("cursor was now set #2");

        }

    }

    public void loadHexEditor(final boolean loadFromFile) {
        final long start = System.currentTimeMillis();
        btnLoad.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        System.out.println("Start Loading #1: "
                + (System.currentTimeMillis() - start));
        /* free memory before it is already loaded */
        hexEditor.setByteContent(new byte[0]);
        pack();
        System.out.println("Start Loading #0: "
                + (System.currentTimeMillis() - start));
        loadContent.createMonitor(this);
        Executors.newCachedThreadPool().execute(new Runnable() {

            @Override
            public void run() {

                try {
                    System.out.println("Start Loading #2: "
                            + (System.currentTimeMillis() - start) + "; "
                            + System.currentTimeMillis());
                    if (loadFromFile) {
                        /* free memory before it is already loaded */
                        byteContent = new byte[0];
                        byteContent = loadContent.readFileNIO(strfileName);
                    }
                    System.out.println("Finished after 1: "
                            + (System.currentTimeMillis() - start));
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            System.out.println("cursor was now set #4");
                            hexEditor.setByteContent(byteContent);
                            if (loadFromFile) {
                                hexEditor.setFilename(strfileName);
                            } else {
                                hexEditor.setFilename(HexLibContainer.EMPTY_FILENAME);
                                hexEditor.setFilenameTooltip("Content newly created either by New or by changed size of a content");
                            }
                            btnSave.setEnabled(false);
                            btnLoad.setEnabled(true);
                            getContentPane().removeAll();
                            getContentPane().add(buildLayout());
                            pack();
                            getContentPane().validate();
                            getContentPane().repaint();

                            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            System.out.println("cursor was now set #5");

                            if (loadContent.isMonitorCancelled()) {
                                loadContent.hideProgessMonitor();
                                if (loadContent.OutOfMemExceptionRaised) {
                                    JOptionPane.showMessageDialog(getMe(),
                                            "Not all data was loaded,"
                                                    + "due to user interruption!\n\n"
                                                    + "Data at the end might be 0 cause the attempt to\n"
                                                    + "truncate it, failed due to lack of available memory.",
                                            "User interruption",
                                            JOptionPane.WARNING_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(getMe(),
                                            "Not all data was loaded, due to user interruption!\n\n"
                                                    + "Data is truncated to the size which was loaded!",
                                            "User interruption",
                                            JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                loadContent.hideProgessMonitor();
                            }

                            System.out.println("Finished after 2: "
                                    + (System.currentTimeMillis() - start));
                        }
                    });
                } catch (OutOfMemoryError e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            loadContent.hideProgessMonitor();
                            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            JOptionPane.showMessageDialog(getMe(),
                                    "File couldn't be loaded!\n\nNot sufficient memory available!",
                                    "OutOfMemoryError",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } finally {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            btnLoad.setEnabled(true);
                            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            System.out.println("cursor was now set #1");
                        }
                    });
                }
            }

        });

    }

    private void addListeners() {
        PropertiesListener myPropListener = new PropertiesListener(this);
        txtFileName.addPropertyChangeListener(myPropListener);

        addWindowListener(new WindowList());

        btnFileDialog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File fileInput = new File(txtFileName.getText());
                fc.setSelectedFile(fileInput);
                int returnVal = fc.showOpenDialog(getMe());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    strfileName = fc.getSelectedFile().getAbsolutePath();
                    txtFileName.setText(strfileName);
                    loadHexEditor(true);
                }
            }
        });

        btnLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File fileInput = new File(txtFileName.getText());
                if (fileInput.isFile()) {
                    strfileName = txtFileName.getText();
                    loadHexEditor(true);
                } else {
                    fc.setSelectedFile(fileInput);
                    int returnVal = fc.showOpenDialog(getMe());

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        strfileName = fc.getSelectedFile().getAbsolutePath();
                        txtFileName.setText(strfileName);
                        loadHexEditor(true);
                    }
                }
            }
        });

        btnSaveAs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getMe().hexEditor.getTransferHandler();
                fc.setSelectedFile(new File(txtFileName.getText()));
                int returnVal = fc.showSaveDialog(getMe());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (fc.getSelectedFile().exists()) {
                        int confirm =
                                JOptionPane.showConfirmDialog(getMe(),
                                        "<html>The selected file already exists.<br>Do you want to overwrite?",
                                        "File already exits",
                                        JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                    strfileName = fc.getSelectedFile().getAbsolutePath();
                    txtFileName.setText(strfileName);
                    new SaveContent(getMe(),
                            txtFileName.getText(),
                            hexEditor.getByteContent());
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtFileName.getText().trim().length() == 0) {
                    btnSaveAs.doClick();
                } else {
                    strfileName = txtFileName.getText();
                    new SaveContent(getMe(),
                            txtFileName.getText(),
                            hexEditor.getByteContent());
                }
            }
        });

        btnSizeEmpty.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                /* free memory before it is already loaded */
                hexEditor.setByteContent(new byte[0]);
                /* free memory before it is already loaded */
                byteContent = new byte[0];

                byteContent = new byte[(Integer) (txtSizeEmptyField.getValue())];
                Arrays.fill(byteContent, (byte) 0);
                loadHexEditor(false);
            }
        });

        btnReSizeLength.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                byte[] muByteContent = new byte[(Integer) txtSizeEmptyField.getValue()];
                Arrays.fill(muByteContent, (byte) 0);
                System.arraycopy(byteContent,
                        0,
                        muByteContent,
                        0,
                        Math.min(muByteContent.length, byteContent.length));
                byteContent = muByteContent;
                loadHexEditor(false);
            }
        });

        CaretPositionListener cpl1 = new CaretPositionListener(txtSizeEmptyField);
        cpl1.setDynamicFormatting(true);

        PaneChangeListener changes = new PaneChangeListener();
        tabPnHexEdit.addChangeListener(changes);

    }

    protected HexEditSample getMe() {
        return this;
    }

    private void initialize() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        System.out.println("cursor was now set #3");

        hexEditor = new HexLibContainer(byteContent);
        /********** additional for Dragable TabPane ****/
        JLabel lblEmpty =
                new JLabel("<html><p align=center>Empty space for new Tab</p>");
        lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
        /* some default size in case the Plustab will be only displayed */
        lblEmpty.setPreferredSize(hexEditor.getPreferredSize());
        tabPnHexEdit.add(lblEmpty, true);
        tabPnHexEdit.setEnabledAt(tabPnHexEdit.getTabCount() - 1, false);
        tabPnHexEdit.add(hexEditor, false);
        tabPnHexEdit.setTitleAt(0, hexEditor.getFilename());
        tabPnHexEdit.setToolTipTextAt(0, hexEditor.getToolTipText());

        tabPnHexEdit.setSelectedIndex(0);
        TransferHandler handler = new TabTransferHandler();
        for (JTabbedPane t : java.util.Arrays.asList(tabPnHexEdit)) {
            t.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            t.setTransferHandler(handler);
        }
        /********** additional for Dragable TabPane ****/

        btnFileDialog = new JButton("Browse...");
        btnLoad = new JButton("Load");
        btnSaveAs = new JButton("Save As...");
        btnSave = new JButton("Save");
        btnSave.setEnabled(false);
        fc = new JFileChooser();

        byteContent = new byte[16 * 16 * 100];
        Arrays.fill(byteContent, (byte) 0);
        txtSizeEmptyField = new JFormattedTextField((byteContent.length - 1));
        btnSizeEmpty = new JButton("Fill HexEditor");
        btnReSizeLength = new JButton("Resize length");

        txtFileName = new JTextField(strfileName, 10);
        new FileAutoCompleter(txtFileName);

        pnlCursor = new CursorPanel(getMe());
        pnlOptions = new OptionPanel(getMe());
        pnlSearch = new SearchPanel(getMe());

        heapView = new HeapView();
        heapView.setShowDropShadow(true);
        heapView.setShowText(true);

        tbPnSettings = new JTabbedPane();
    }

    private JComponent buildLayout() {
        tbPnSettings.removeAll();
        tbPnSettings.add(buildMainLayout(), "General");
        tbPnSettings.add(buildOptionLayout(), "Settings");
        FormLayout layout = new FormLayout("3dlu,f:p:g, 3dlu, p, 3dlu", // cols
                "3dlu,f:p:g,3dlu" // rows
        );
        PanelBuilder builder = new PanelBuilder(layout);
        int colStart = 2;
        int cols = colStart;
        int row = 2;
        CellConstraints cc = new CellConstraints();

        builder.add(tbPnSettings, cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(tabPnHexEdit, cc.xy(cols, row));

        return builder.getPanel();
    }

    private Component buildMainLayout() {
        FormLayout layout = new FormLayout("3dlu,f:p:g, 3dlu", // cols
                "3dlu,3*(p,3dlu),f:p, f:p:g(0.25),3dlu" // rows
        );
        PanelBuilder builder = new PanelBuilder(layout);
        int colStart = 2;
        int cols = colStart;
        int row = 2;
        CellConstraints cc = new CellConstraints();

        builder.add(buildLayoutGeneric(), cc.xy(cols, row));
        cols = colStart;
        row++;
        row++;
        builder.add(pnlCursor, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(pnlSearch, cc.xy(cols, row));

        cols = colStart;
        row++;
        row++;
        builder.add(heapView, cc.xy(cols, row));

        return builder.getPanel();
    }

    private Component buildOptionLayout() {
        return pnlOptions;
    }

    private JComponent buildLayoutGeneric() {
        FormLayout layout = new FormLayout("3dlu,p, 3dlu,f:p:g,3dlu", // cols
                "6*(3dlu,p),3dlu" // rows
        );
        PanelBuilder builder = new PanelBuilder(layout);
        // DefaultFormBuilder builder =
        // new DefaultFormBuilder(layout, new FormDebugPanel());

        int colStart = 2;
        int cols = colStart;
        int row = 2;
        int colMax = 5;
        CellConstraints cc = new CellConstraints();

        JPanel muPanel = new JPanel();
        muPanel.setLayout(new BoxLayout(muPanel, BoxLayout.X_AXIS));
        muPanel.add(new JLabel("<html>Array with size (in Bytes)"));
        muPanel.add(txtSizeEmptyField);
        // builder.add(new JLabel("<html>Create Empty Array<br>With size (in Bytes)"),
        // cc.xy(cols, row));
        // cols++;
        // cols++;
        // builder.add(txtSizeEmptyField, cc.xy(cols, row));
        builder.add(muPanel, cc.xyw(cols, row, colMax - cols));
        row++;
        row++;
        cols = colStart;
        builder.add(ButtonBarFactory.buildGrowingBar(btnSizeEmpty, btnReSizeLength),
                cc.xyw(cols, row, colMax - cols));

        row++;
        row++;
        cols = colStart;
        builder.addSeparator("", cc.xyw(cols, row, colMax - cols));
        row++;
        row++;
        cols = colStart;
        builder.add(new JLabel("File name"), cc.xy(cols, row));
        cols++;
        cols++;
        builder.add(txtFileName, cc.xy(cols, row));

        row++;
        row++;
        cols = colStart;
        builder.add(ButtonBarFactory.buildGrowingBar(btnSaveAs,
                // btnSave,
                btnFileDialog
                // ,btnLoad
        ), cc.xyw(cols, row, colMax - cols));
        row++;
        row++;
        cols = colStart;
        builder.add(ButtonBarFactory.buildGrowingBar(// btnSaveAs,
                btnSave,
                // btnFileDialog,
                btnLoad),
                cc.xyw(cols, row, colMax - cols));

        /* TitleBorder created that way, cause it matches the ComponentTitleBorder */
        Border thatBorder1 = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        Border thatBorder2 = new TitledBorder(thatBorder1, "Food for HexEditor");
        builder.setBorder(thatBorder2);

        return builder.getPanel();
    }

    public static void main(String arg[]) throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new HexEditSample();
            }
        });
    }

    public void setMasterFrameSize(int newFontSize, double newResizeRatio) {
        hexEditor.setFontSize(newFontSize);

        getContentPane().removeAll();
        getContentPane().add(buildLayout());
        pack();
        getContentPane().validate();
        getContentPane().repaint();
        // win.usrPref.defaultFontSize = newFontSize;
    }
}
