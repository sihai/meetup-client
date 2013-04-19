/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.galaxy.meetup.server.client.domain.DataPlusOne;

/**
 * 
 * @author sihai
 *
 */
public class DbPlusOneData extends DbSerializer {

	private int mCount;
    private String mId;
    private boolean mPlusOnedByMe;
    
    public DbPlusOneData()
    {
    }

    private DbPlusOneData(DataPlusOne dataplusone)
    {
        mId = dataplusone.id;
        mCount = dataplusone.globalCount.intValue();
        mPlusOnedByMe = dataplusone.isPlusonedByViewer.booleanValue();
    }

    public DbPlusOneData(String s, int i, boolean flag)
    {
        mId = s;
        mCount = i;
        mPlusOnedByMe = flag;
    }

    public static DbPlusOneData deserialize(byte abyte0[])
    {
        boolean flag = true;
        DbPlusOneData dbplusonedata;
        if(abyte0 == null)
        {
            dbplusonedata = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            String s = getShortString(bytebuffer);
            int i = bytebuffer.getInt();
            if(bytebuffer.get() != 1)
                flag = false;
            dbplusonedata = new DbPlusOneData(s, i, flag);
        }
        return dbplusonedata;
    }

    public static byte[] serialize(DbPlusOneData dbplusonedata) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(32);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        putShortString(dataoutputstream, dbplusonedata.mId);
        dataoutputstream.writeInt(dbplusonedata.mCount);
        int i;
        byte abyte0[];
        if(dbplusonedata.mPlusOnedByMe)
            i = 1;
        else
            i = 0;
        dataoutputstream.write(i);
        abyte0 = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public static byte[] serialize(DataPlusOne dataplusone) throws IOException
    {
        return serialize(new DbPlusOneData(dataplusone));
    }

    public final int getCount()
    {
        return mCount;
    }

    public final String getId()
    {
        return mId;
    }

    public final boolean isPlusOnedByMe()
    {
        return mPlusOnedByMe;
    }

    public final void setId(String s)
    {
        mId = s;
    }

    public final void updatePlusOnedByMe(boolean flag)
    {
        if(mPlusOnedByMe != flag)
        {
            mPlusOnedByMe = flag;
            int i = mCount;
            int j;
            if(flag)
                j = 1;
            else
                j = -1;
            mCount = j + i;
        }
    }
}
