/*
 * Created on 19.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.IO;

import at.HexLib.GUI.Progess.ProgressMonitorOutputStream;
import at.HexLib.GUI.main.HexEditSample;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class SaveContent {

    public SaveContent(final JFrame master,
                       final String strFile2Save,
                       final byte[] saveField) {
        Executors.newCachedThreadPool().execute(new Runnable() {

            @Override
            public void run() {

                File someFile = new File(strFile2Save);

                FileOutputStream fos;
                try {
                    long start = System.currentTimeMillis();
                    System.out.println("Start:  " + System.currentTimeMillis());
                    fos = new FileOutputStream(someFile);

                    ProgressMonitorOutputStream prog =
                            new ProgressMonitorOutputStream(master, fos, "Save File "
                                    + strFile2Save, 100);
                    // fos.write(saveField);

                    int countByteLen4OnePercentage = saveField.length / 100;
                    for (int percentage = 0; percentage < 100; percentage++) {
                        // System.out.println("Perc="
                        // + percentage
                        // + " "
                        // + (countByteLen4OnePercentage * percentage)
                        // + " - "
                        // + (countByteLen4OnePercentage * (percentage + 1))
                        // + "/" + (saveField.length - 1) + "..."
                        // + System.currentTimeMillis());
                        prog.write(Arrays.copyOfRange(saveField,
                                countByteLen4OnePercentage
                                        * percentage,
                                countByteLen4OnePercentage
                                        * (percentage + 1)),
                                Math.max(percentage - 2, 0));
                    }

                    System.out.println("Perc=" + 100 + " "
                            + (countByteLen4OnePercentage * 100) + "/"
                            + (saveField.length - 1) + "..."
                            + (System.currentTimeMillis() - start));

                    prog.write(Arrays.copyOfRange(saveField,
                            countByteLen4OnePercentage * 100,
                            saveField.length), 99);
                    System.out.println("flush..." + (System.currentTimeMillis() - start));
                    prog.flush();
                    System.out.println("close..." + (System.currentTimeMillis() - start));
                    prog.close();
                    System.out.println("End:    " + (System.currentTimeMillis() - start));
                    HexEditSample.masterReference.hexEditor.resetHashCode();
                    HexEditSample.masterReference.hexEditor.updateChangeStatus();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
