/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.xmpp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * @author sihai
 *
 */
public class LogInputStream extends InputStream {
	
	private static final LinkedList mLogs = new LinkedList();
    private final InputStream mInputStream;
    private final StringBuffer mLogBuffer = new StringBuffer();
    
	public LogInputStream(InputStream inputstream)
    {
        mInputStream = inputstream;
    }

    public static String getLog()
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(Iterator iterator = mLogs.iterator(); iterator.hasNext(); stringbuffer.append("\n"))
            stringbuffer.append((StringBuffer)iterator.next());

        return stringbuffer.toString();
    }

    public final int available()
        throws IOException
    {
        return mInputStream.available();
    }

    public final void close()
        throws IOException
    {
        mInputStream.close();
        mLogs.add(mLogBuffer);
        if(mLogs.size() > 3)
            mLogs.removeFirst();
    }

    public final void mark(int i)
    {
        mInputStream.mark(i);
    }

    public final boolean markSupported()
    {
        return mInputStream.markSupported();
    }

    public final int read()
        throws IOException
    {
        int i;
        try
        {
            i = mInputStream.read();
            mLogBuffer.append((char)i);
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
        return i;
    }

    public final int read(byte abyte0[]) throws IOException {
        int i;
        i = mInputStream.read(abyte0);
        for(int j = 0; j < i; j++) {
        	mLogBuffer.append((char)abyte0[j]);
        }
        return i;
    }

    public final int read(byte abyte0[], int i, int j) throws IOException {
        int k;
        k = mInputStream.read(abyte0, i, j);
        int end = i + k;
        for(int l = i; l < end; l++) {
        	mLogBuffer.append((char)abyte0[l]);
        }
        return k;
    }

    public final synchronized void reset() throws IOException {
        mInputStream.reset();
    }

    public final long skip(long l) throws IOException
    {
        return mInputStream.skip(l);
    }

}
