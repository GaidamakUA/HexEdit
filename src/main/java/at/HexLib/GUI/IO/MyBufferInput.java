/*
 * Created on 19.02.2011
 *
 * Version: NewTest
 */

package at.HexLib.GUI.IO;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class MyBufferInput extends BufferedInputStream {

    public static void main(String[] args) {
        MyBufferInput bsd = new MyBufferInput(null);

        System.err.println(System.getProperty("java.version"));
        System.err.println(bsd.getBufferSize());
    }

    public MyBufferInput(InputStream in) {
        super(in);
    }

    public int getBufferSize() {
        return super.buf.length;
    }

    public byte[] getBuffer() {
        return buf;
    }
}
