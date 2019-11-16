/*
 * Created on 22.04.2011
 *
 * Version: NewTest
 */

package at.HexLib.library;

import at.HexLib.library.HexTransfer.copyStringAction;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class HexTransferable implements Transferable {

    // This DataFlavor object is used when we transfer Color objects directly
    protected static DataFlavor hexFlavor =
            new DataFlavor(HexTransferable.class,
                    "Java HexLib Object");

    // These are the data flavors we support
    protected static DataFlavor[] supportedFlavors = {hexFlavor, // Transfer as a Hex
            // object
            // DataFlavor.stringFlavor,
            // // Transfer as a String
            // object
            DataFlavor.plainTextFlavor,
            // Transfer as a stream of Unicode text
            DataFlavor.getTextPlainUnicodeFlavor()};

    byte[] buffer;                                                   // The
    // content
    // we
    // encapsulate
    // and
    // transfer

    private copyStringAction copyAction;

    /**
     * Create a new HexTransferable that encapsulates the specified content
     *
     * @param copyAction
     */
    public HexTransferable(byte[] buffer, copyStringAction copyAction) {
        this.buffer = buffer;
        this.copyAction = copyAction;
    }

    /**
     * Return a list of DataFlavors we can support
     */
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    /**
     * Check whether a specified DataFlavor is available
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor curFlavor : supportedFlavors) {
            if (flavor.equals(curFlavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Transfer the data. Given a specified DataFlavor, return an Object appropriate for
     * that flavor. Throw UnsupportedFlavorException if we don't support the requested
     * flavor.
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException,
            IOException {
        if (flavor.equals(hexFlavor)) {
            return buffer;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return getStringRepresentationOfContent();
            // return buffer;
        } else if (flavor.equals(DataFlavor.plainTextFlavor)
                || flavor.equals(DataFlavor.getTextPlainUnicodeFlavor())) {
            return getStringRepresentationOfContent();
        }
        // return new ByteArrayInputStream(buffer.toString().getBytes("Unicode"));
        else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public Object getStringRepresentationOfContent() {
        StringBuilder selectionContent = new StringBuilder();
        for (byte curByte : buffer) {
            if (copyAction == HexTransfer.copyStringAction.BINARY) {
                if (curByte < 0) {
                    selectionContent.append((char) (256 + curByte));
                } else {
                    selectionContent.append((char) curByte);
                }
            } else if (copyAction == HexTransfer.copyStringAction.BINARY_AS_STRING) {
                selectionContent.append(HexLib.convertToHex(curByte, 2));
            }
        }
        return selectionContent.toString();
    }
}