/*
 * Created on 21.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.IO;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class LoadContent {

    int maxProgress = 50;
    private ProgressMonitor progMonitor;

    public void createMonitor(JFrame win) {
        progMonitor = getProgessMonitor(win);
        setProgress(0);
    }

    public boolean OutOfMemExceptionRaised = false;

    public byte[] readFileNIO(String file) {
        try {
            System.out.println("Start NIO #2: " + (System.currentTimeMillis()));
            progMonitor.setNote(file);
            setProgress(1);
            System.out.println("Start NIO #3: " + (System.currentTimeMillis()));

            FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
            System.out.println("Start NIO #4: " + (System.currentTimeMillis()));

            // Get the file's size and then map it into memory
            int sz = (int) fileChannel.size();
            if (sz == 0) {
                return new byte[0];
            }
            OutOfMemExceptionRaised = false;
            byte[] buf = new byte[sz];

            int blockSize = sz / maxProgress;
            int curStep = 0;
            for (; curStep < maxProgress && !progMonitor.isCanceled(); curStep++) {
                int offSet = blockSize * curStep;
                MappedByteBuffer bb =
                        fileChannel.map(FileChannel.MapMode.READ_ONLY, offSet, blockSize);
                byte[] readBytes = new byte[blockSize];

                bb.get(readBytes);
                System.arraycopy(readBytes, 0, buf, offSet, blockSize);
                setProgress(curStep);
            }
            if (progMonitor.isCanceled()) {
                try {
                    /* try to return a properly truncated input */
                    return Arrays.copyOf(buf, curStep * blockSize);
                } catch (OutOfMemoryError e) {
                    OutOfMemExceptionRaised = true;
                    /* attempt failed ==> return the full array */
                    return buf;
                }
            } else {
                int offSetRemaining = blockSize * maxProgress;
                if (sz - offSetRemaining > 0) {
                    /* read the remaining bytes (due to rounding-error) */
                    MappedByteBuffer bb =
                            fileChannel.map(FileChannel.MapMode.READ_ONLY,
                                    offSetRemaining,
                                    sz - offSetRemaining);
                    byte[] readBytes = new byte[sz - offSetRemaining];

                    bb.get(readBytes);
                    System.arraycopy(readBytes,
                            0,
                            buf,
                            offSetRemaining,
                            sz - offSetRemaining);
                }
            }
            return buf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public boolean isMonitorCancelled() {
        return progMonitor.isCanceled();
    }

    private void setProgress(final int curStep) {
        SwingUtilities.invokeLater(() -> {
            try {
                progMonitor.setProgress(curStep);
            } catch (Exception e) {
                // nothing to do, cause the ProgressMonitor could be already be closed
            }
        });
    }

    public void hideProgessMonitor() {
        SwingUtilities.invokeLater(() -> progMonitor.close());
    }

    private ProgressMonitor getProgessMonitor(JFrame win) {
        ProgressMonitor monitor = new ProgressMonitor(win, "Reading ", null, 0, maxProgress);
        monitor.setMillisToDecideToPopup(0);
        monitor.setMillisToPopup(0);
        return monitor;
    }
}
