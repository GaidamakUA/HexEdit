/*
 * Created on 02.03.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.Listener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowList implements WindowListener {

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
