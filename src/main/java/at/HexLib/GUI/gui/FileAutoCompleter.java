/*
 * Created on 22.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.gui;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.io.File;
import java.util.ArrayList;

/*@author Santhosh Kumar T - santhosh@in.fiorano.com
 * Modifications by LeO 2011-02-23
 * - let popup-menu appear any time
 * - list dirs and files separately
 * - insert now is a replace from the starting position
 */
public class FileAutoCompleter extends AutoCompleter {

    private String fileSeparator = System.getProperty("file.separator");

    public FileAutoCompleter(JTextComponent comp) {
        super(comp);
    }

    protected boolean updateListData() {
        String value = textComp.getText();
        int caretPos = textComp.getCaret().getDot();
        int index = value.lastIndexOf(fileSeparator, caretPos);
        if (index == -1) {
            if ((index = value.indexOf(fileSeparator, caretPos)) == -1) {
                return false;
            }
            caretPos = index + 1;
        }
        String dir = value.substring(0, index + 1);
        final String prefix =

                index == caretPos ? null : value.substring(index + 1, caretPos)
                        .toLowerCase();
        File[] files = new File(dir).listFiles((dir1, name) -> prefix == null || name.toLowerCase().startsWith(prefix));
        if (files == null) {
            list.setListData(new String[0]);
            return true;
        } else {
            ArrayList<String> listDir = new ArrayList<String>();
            ArrayList<String> listFiles = new ArrayList<String>();
            for (File checkFile : files) {
                if (checkFile.isDirectory()) {
                    listDir.add(checkFile.getName());
                } else {
                    listFiles.add(checkFile.getName());
                }
            }
            String[] fileList = new String[files.length];
            int curFileCounter = 0;
            for (String strDir : listDir) {
                fileList[curFileCounter++] = strDir + fileSeparator;
            }
            for (String strFiles : listFiles) {
                fileList[curFileCounter++] = strFiles;
            }
            if (files.length == 1 && fileList[0].equalsIgnoreCase(prefix)) {
                list.setListData(new String[0]);
            } else {
                list.setListData(fileList);
            }
            return true;
        }
    }

    protected void acceptedListItem(String selected) {
        if (selected == null) {
            return;
        }

        String value = textComp.getText();
        int caretPos = textComp.getCaret().getDot();
        int index = value.lastIndexOf(fileSeparator, caretPos);
        if (index == -1) {
            if ((index = value.indexOf(fileSeparator, caretPos)) == -1) {
                return;
            }
        }
        int prefixlen = textComp.getDocument().getLength() - index - 1;
        try {
            textComp.getDocument().remove(index + 1, prefixlen);
            textComp.getDocument().insertString(index + 1, selected,
                    null);
            textComp.setCaretPosition(textComp.getText().length());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}