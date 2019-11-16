/*
 * Created on 19.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.Progess;

import javax.swing.*;
import java.awt.*;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * Class to display progress monitor for output stream
 *
 * @version $Id: ProgressMonitorOutputStream.java,v 1.1 2004/01/15 13:36:08 showard Exp $
 **/

public class ProgressMonitorOutputStream extends FilterOutputStream {

    private ProgressMonitor monitor;
    private long count;

    public ProgressMonitorOutputStream(Component parentComponent,
                                       OutputStream out,
                                       Object message,
                                       int sizeOutputStreamLength) {
        super(out);

        monitor =
                new ProgressMonitor(parentComponent,
                        message,
                        null,
                        0,
                        sizeOutputStreamLength);
        monitor.setMillisToDecideToPopup(50);
        monitor.setMillisToPopup(100);
    }

    /**
     * Get the ProgressMonitor object being used by this stream. Normally this isn't needed
     * unless you want to do something like change the descriptive text partway through
     * reading the file.
     *
     * @return the ProgressMonitor object used by this object
     */
    public ProgressMonitor getProgressMonitor() {
        return monitor;
    }


    public void write(byte[] b, int progress) throws IOException {
        out.write(b, 0, b.length);
        updateMonitor(progress);
        if (monitor.isCanceled()) {
            InterruptedIOException exc =
                    new InterruptedIOException("Progress Canceled");
            exc.bytesTransferred = (int) count;
            throw exc;
        }
    }

    public void updateMonitor(final int count) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                monitor.setProgress(count);
            }
        });
    }

    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    public void close() throws IOException {
        out.close();
        updateMonitor(monitor.getMaximum());
    }

}
