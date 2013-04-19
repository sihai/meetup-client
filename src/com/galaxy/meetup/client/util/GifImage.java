/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author sihai
 *
 */
public class GifImage {

	private static final byte sColorTableBuffer[] = new byte[768];
    int mBackgroundColor;
    int mBackgroundIndex;
    private final byte[] mData;
    boolean mError;
    int[] mGlobalColorTable;
    int mGlobalColorTableSize;
    boolean mGlobalColorTableUsed;
    int mHeaderSize;
    private int mHeight;
    private int mWidth;

    public GifImage(byte[] bytes) {
    	GifHeaderStream gifheaderstream = null;
    	mError = false;
        mGlobalColorTable = new int[256];
        mData = bytes;
        try {
	        gifheaderstream = new GifHeaderStream(bytes);
	        int first = gifheaderstream.read();
	        int second = gifheaderstream.read();
	        int third = gifheaderstream.read();
	        if(first != 71 || second != 73 || third != 70) {
	        	mError = true;
	        } else {
	        	gifheaderstream.skip(3L);
	            mWidth = readShort(gifheaderstream);
	            mHeight = readShort(gifheaderstream);
	            int i = gifheaderstream.read();
	            int j = i & 0x80;
	            boolean flag3 = false;
	            if(j != 0)
	                flag3 = true;
	            mGlobalColorTableUsed = flag3;
	            mGlobalColorTableSize = 2 << (i & 7);
	            mBackgroundIndex = gifheaderstream.read();
	            gifheaderstream.skip(1L);
	            if(mGlobalColorTableUsed) {
	                readColorTable(gifheaderstream, mGlobalColorTable, mGlobalColorTableSize);
	                mBackgroundColor = mGlobalColorTable[mBackgroundIndex];
	            }
	        }
        } catch (IOException e) {
        	mError = true;
        } finally {
        	if(null != gifheaderstream) {
        		try {
        			gifheaderstream.close();
        		} catch (IOException e) {
        			// 
        		}
        	}
        }
    }
    
    public static boolean isGif(byte abyte0[]) {
        boolean flag = true;
        if(abyte0.length < 3 || abyte0[0] != 71 || abyte0[1] != 73 || abyte0[2] != 70)
            flag = false;
        return flag;
    }

    private static boolean readColorTable(InputStream inputstream, int ai[], int i) throws IOException {
    	synchronized(sColorTableBuffer) {
    		int j = i * 3;
    		if(inputstream.read(sColorTableBuffer, 0, j) < j) {
    			return false;
    		}
    		
    		byte abyte1[] = sColorTableBuffer;
            int l = 0;
            int k;
            for(k = 0; k < i; k++)
            {
                int i1 = l + 1;
                int j1 = 0xff & abyte1[l];
                byte abyte2[] = sColorTableBuffer;
                int k1 = i1 + 1;
                int l1 = 0xff & abyte2[i1];
                byte abyte3[] = sColorTableBuffer;
                int i2 = k1 + 1;
                int j2 = 0xff & abyte3[k1];
                ai[k] = j2 | (0xff000000 | j1 << 16 | l1 << 8);
                l = i2;
            }
            return true;
    	}
    }

    private static int readShort(InputStream inputstream) throws IOException{
        return inputstream.read() | inputstream.read() << 8;
    }

    public final byte[] getData() {
        return mData;
    }

    public final int getHeight() {
        return mHeight;
    }

    public final int getSizeEstimate() {
        return mData.length + 4 * mGlobalColorTable.length;
    }

    public final int getWidth() {
        return mWidth;
    }
    
	private final class GifHeaderStream extends ByteArrayInputStream {

		private GifHeaderStream(byte abyte0[]) {
			super(abyte0);
		}

		public final int getPosition() {
			return pos;
		}
	}
}