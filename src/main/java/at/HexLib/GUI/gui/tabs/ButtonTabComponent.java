/*
 * Created on 09.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui.tabs;

import at.HexLib.GUI.main.HexLibContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonTabComponent extends JPanel {

    private static Color colorMainBack;
    private static Color colorPressed;
    private static Color colorPlus;
    private static Color colorRolloverFore;
    private static Color colorRolloverBack;
    private final DnDTabbedPane pane;
    private String title;
    private String tip;

    public boolean isPlusButton = false;

    public ButtonTabComponent(final DnDTabbedPane pane, boolean setPlusButton) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);
        JLabel label = new JLabel() {

            public String getText() {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
        add(label);
        if (setPlusButton) {
            JButton buttonPlus = new PlusButton();
            add(buttonPlus);
            isPlusButton = true;
        } else {
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            JButton buttonMax = new MaxButton();
            add(buttonMax);
            JButton button = new TabButton();
            add(button);
        }
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    public void setPlusTabCompontents(String title, String tip) {
        this.title = title;
        this.tip = tip;

    }

    private static void createColors() {
        colorMainBack = UIManager.getColor("Button.foreground");
        colorRolloverBack = UIManager.getColor("ComboBox.selectionBackground");
        colorRolloverFore = UIManager.getColor("ComboBox.selectionForeground");
        colorPressed = UIManager.getColor("Button.disabledText");
        ;
        colorPlus = Color.GREEN;
    }

    private class TabButton extends JButton implements ActionListener {

        private static final int BUTTONSIZE = 17;

        public TabButton() {
            setPreferredSize(new Dimension(BUTTONSIZE, BUTTONSIZE));
            setToolTipText("Close this tab");
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                Component mu = pane.getComponentAt(i);
                if (mu instanceof HexLibContainer) {
                    ((HexLibContainer) mu).dispose();
                }
                pane.remove(i);
                if (pane.getSelectedIndex() == pane.getTabCount() - 1) {
                    pane.setSelectedIndex(Math.max(pane.getTabCount() - 2, 0));
                }

            }
        }

        public void updateUI() {
            createColors();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setStroke(new BasicStroke(2));
            g2.setColor(colorMainBack);
            int delta = 5;
            if (getModel().isRollover()) {
                g2.setColor(colorRolloverBack);
                g2.fillRect(3, 3, BUTTONSIZE - delta - 1, BUTTONSIZE - delta - 1);

                g2.setColor(colorRolloverFore);
            }
            if (getModel().isPressed()) {
                g2.setColor(colorPressed);
            }
            g2.drawLine(delta + 1, delta, getWidth() - delta - 1, getHeight() - delta);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta);
            g2.dispose();
        }
    }

    private class MaxButton extends JButton implements ActionListener {

        private static final int BUTTONSIZE = 17;

        public MaxButton() {
            setPreferredSize(new Dimension(BUTTONSIZE, BUTTONSIZE));
            setToolTipText("Maximize this tab");
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                pane.detachTab(i);
            }
        }

        public void updateUI() {
            createColors();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(colorMainBack);
            int delta = 6;
            if (getModel().isRollover()) {
                g2.setColor(colorRolloverBack);
            }
            if (getModel().isPressed()) {
                g2.setColor(colorPressed);
            }
            g2.drawRect(3, 3, BUTTONSIZE - delta - 1, BUTTONSIZE - delta - 1);
            g2.drawLine(3, 4, BUTTONSIZE - delta + 1, 4);
            g2.drawLine(3, 5, BUTTONSIZE - delta + 1, 5);
            g2.dispose();
        }
    }

    private class PlusButton extends JButton implements ActionListener {

        private static final int BUTTONSIZE = 17;

        public PlusButton() {
            setPreferredSize(new Dimension(BUTTONSIZE, BUTTONSIZE));
            setToolTipText("Add new tab");
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                pane.insertTab(title,
                        null,
                        new HexLibContainer(new byte[0]),
                        tip,
                        pane.getTabCount() - 1);
                pane.setTabComponentAt(pane.getTabCount() - 2,
                        new ButtonTabComponent(pane, false));

                if (pane.getSelectedIndex() == pane.getTabCount() - 1) {
                    pane.setSelectedIndex(Math.max(pane.getTabCount() - 2, 0));
                }
            }
        }

        public void updateUI() {
            createColors();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setStroke(new BasicStroke(3));
            g2.setColor(colorMainBack);
            int delta = 6;
            if (getModel().isRollover()) {
                g2.setColor(colorRolloverBack);
                g2.fillRect(3, 3, BUTTONSIZE - delta, BUTTONSIZE - delta);
                g2.setColor(colorPlus);
            } else if (getModel().isPressed()) {
                g2.setColor(colorPressed);
            }
            g2.drawLine(delta / 2 + 1, (getHeight()) / 2, getWidth() - (delta) / 2
                    - 2, (getHeight()) / 2);
            g2.drawLine((getWidth()) / 2,
                    delta / 2 + 1,
                    (getWidth()) / 2,
                    (getHeight()) - delta / 2 - 2);

            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {

        public void mouseEntered(MouseEvent e) {
            Component component =
                    e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button =
                        (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component =
                    e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button =
                        (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
