/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public class DbSerializer {

	protected DbSerializer() {
	}

	private static String decodeUtf8(byte bytes[]) {
		String s;
		try {
			s = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError();
		}
		return s;
	}

	protected static String getShortString(ByteBuffer bytebuffer) {
		short word0 = bytebuffer.getShort();
		String s;
		if (word0 > 0) {
			byte abyte0[] = new byte[word0];
			bytebuffer.get(abyte0);
			s = decodeUtf8(abyte0);
		} else {
			s = null;
		}
		return s;
	}

	static List<String> getShortStringList(ByteBuffer bytebuffer) {
		List<String> arraylist = new ArrayList<String>();
		int i = bytebuffer.getInt();
		for (int j = 0; j < i; j++)
			arraylist.add(getShortString(bytebuffer));

		return arraylist;
	}

    protected static void putShortString(DataOutputStream dataoutputstream, String s) throws IOException {
        if(s != null)
        {
            byte abyte0[] = s.getBytes("UTF-8");
            dataoutputstream.writeShort(abyte0.length);
            dataoutputstream.write(abyte0);
        } else {
            dataoutputstream.writeShort(0);
        }
    }

    protected static void putShortStringList(DataOutputStream dataoutputstream, List<String> list) throws IOException {
        dataoutputstream.writeInt(list.size());
        for(Iterator<String> iterator = list.iterator(); iterator.hasNext(); putShortString(dataoutputstream, (String)iterator.next()));
    }
}
