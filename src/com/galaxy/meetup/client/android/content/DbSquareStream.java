/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author sihai
 *
 */
public class DbSquareStream extends DbSerializer implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new DbSquareStream(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new DbSquareStream[i];
        }

    };
    private final String mDescription;
    private final String mId;
    private final String mName;

    private DbSquareStream(Parcel parcel)
    {
        mId = parcel.readString();
        mName = parcel.readString();
        mDescription = parcel.readString();
    }

    DbSquareStream(Parcel parcel, byte byte0)
    {
        this(parcel);
    }

    public DbSquareStream(String s, String s1, String s2)
    {
        mId = s;
        mName = s1;
        mDescription = s2;
    }

    public static DbSquareStream[] deserialize(byte abyte0[])
    {
        DbSquareStream adbsquarestream[];
        if(abyte0 == null)
        {
            adbsquarestream = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            short word0 = bytebuffer.getShort();
            adbsquarestream = new DbSquareStream[word0];
            short word1 = 0;
            while(word1 < word0) 
            {
                adbsquarestream[word1] = new DbSquareStream(getShortString(bytebuffer), getShortString(bytebuffer), getShortString(bytebuffer));
                word1++;
            }
        }
        return adbsquarestream;
    }

    public static byte[] serialize(DbSquareStream adbsquarestream[]) throws IOException {
    	if(0 == adbsquarestream.length) {
    		return null;
    	}
    	
    	ByteArrayOutputStream bytearrayoutputstream = null;
        DataOutputStream dataoutputstream = null;
        try {
        	bytearrayoutputstream = new ByteArrayOutputStream(32);
        	dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        	byte abyte0[];
        	dataoutputstream.writeShort(adbsquarestream.length);
        	int i = adbsquarestream.length;
	        for(int j = 0; j < i; j++)
	        {
	            DbSquareStream dbsquarestream = adbsquarestream[j];
	            putShortString(dataoutputstream, dbsquarestream.mId);
	            putShortString(dataoutputstream, dbsquarestream.mName);
	            putShortString(dataoutputstream, dbsquarestream.mDescription);
	        }
	
	        return bytearrayoutputstream.toByteArray();
        } finally {
        	if(null != bytearrayoutputstream) {
        		bytearrayoutputstream.close();
        	}
        	if(null != dataoutputstream) {
        		dataoutputstream.close();
        	}
        }
    }

    public int describeContents()
    {
        return 0;
    }

    public final String getDescription()
    {
        return mDescription;
    }

    public final String getName()
    {
        return mName;
    }

    public final String getStreamId()
    {
        return mId;
    }

    public String toString()
    {
        return (new StringBuilder("{SquareStream id=")).append(mId).append(" name=").append(mName).append(" description=").append(mDescription).append("}").toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mId);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
    }
}
