/*
 * Created on 21.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.IO;

import java.io.ByteArrayOutputStream;


public class MyOutputByteStream extends ByteArrayOutputStream {

    public MyOutputByteStream(int size) {
        super(size);
    }

    public byte[] getBuffer() {
        return super.buf;
    }
}
