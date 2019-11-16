/*
 * Created on 20.05.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.test;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SampleFocus extends JFrame {

    public SampleFocus(String titel) {
        setTitle(titel);
        JTextField txtField1 = new JTextField("default-click");
        JTextField txtField2 = new JTextField("alternative-Text");
        JTextField txtField3 = new JTextField("own diaolog textfield");

        final JTextArea dummyLabel = new JTextArea(10, 20);
        dummyLabel.setText("empty textarea, which is focusable");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(txtField1);
        add(dummyLabel);
        add(txtField2);

        JDialog altDialog = new JDialog(this);
        altDialog.add(txtField3);
        altDialog.setVisible(true);
        altDialog.pack();

        FocusAdapter myFocusListner = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("Event gained:[" +
                        "] =" + e);
                if (e.getComponent() instanceof JTextField) {
                    if (((javax.swing.JComponent) e.getComponent()).getRootPane()
                            .getParent() instanceof JDialog) {
                        if (e.getOppositeComponent() instanceof JTextField) {
                            JComponent mu = (JComponent) e.getComponent();
                            dummyLabel.requestFocus(false);
                            mu.requestFocus(true);
                            return;
                        }
                    }
                    System.out.println("gained for TextField: "
                            + ((JTextField) e.getComponent()).getText());
                } else {
                    System.out.println("gained for component: " + e.getComponent());
                }
            }

        };

        txtField1.addFocusListener(myFocusListner);
        txtField2.addFocusListener(myFocusListner);
        txtField3.addFocusListener(myFocusListner);

        // dummyLabel.addFocusListener(myFocusListner);
    }

    public static void main(String[] args) {
        JFrame frame = new SampleFocus("FocusListener  - sample");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
