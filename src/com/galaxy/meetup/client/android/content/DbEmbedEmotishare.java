/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import WriteReviewOperation.MediaRef;
import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.server.client.domain.EmbedClientItem;
import com.galaxy.meetup.server.client.domain.Emotishare;
import com.galaxy.meetup.server.client.domain.Thing;
import com.galaxy.meetup.server.client.domain.Thumbnail;

/**
 * 
 * @author sihai
 *
 */
public class DbEmbedEmotishare extends DbSerializer implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new DbEmbedEmotishare(parcel, (byte)0);
        }

        public final Object[] newArray(int i)
        {
            return new DbEmbedEmotishare[i];
        }

    };
    
    private String mDescription;
    private MediaRef mImageRef;
    private String mImageUrl;
    private String mName;
    private String mType;
    private String mUrl;
    
    private DbEmbedEmotishare()
    {
    }

    private DbEmbedEmotishare(Parcel parcel)
    {
        mUrl = parcel.readString();
        mImageUrl = parcel.readString();
        mName = parcel.readString();
        mDescription = parcel.readString();
        mType = parcel.readString();
        mImageRef = DbEmotishareMetadata.createMediaRef(mImageUrl);
    }

    DbEmbedEmotishare(Parcel parcel, byte byte0)
    {
        this(parcel);
    }

    public DbEmbedEmotishare(Emotishare emotishare)
    {
        mUrl = emotishare.url;
        mType = emotishare.emotion;
        mName = emotishare.name;
        mDescription = emotishare.description;
        if(emotishare.proxiedImage != null)
            mImageUrl = emotishare.proxiedImage.getImageUrl();
    }

    public DbEmbedEmotishare(String s, String s1, String s2, String s3)
    {
        mUrl = s2;
        mType = s;
        mName = s1;
        mDescription = s3;
        mImageUrl = s2;
        mImageRef = DbEmotishareMetadata.createMediaRef(s2);
    }

    public static DbEmbedEmotishare deserialize(ByteBuffer bytebuffer)
    {
        DbEmbedEmotishare dbembedemotishare;
        if(bytebuffer == null)
        {
            dbembedemotishare = null;
        } else
        {
            dbembedemotishare = new DbEmbedEmotishare();
            dbembedemotishare.mUrl = getShortString(bytebuffer);
            dbembedemotishare.mImageUrl = getShortString(bytebuffer);
            dbembedemotishare.mName = getShortString(bytebuffer);
            dbembedemotishare.mDescription = getShortString(bytebuffer);
            dbembedemotishare.mType = getShortString(bytebuffer);
            dbembedemotishare.mImageRef = DbEmotishareMetadata.createMediaRef(dbembedemotishare.mImageUrl);
        }
        return dbembedemotishare;
    }

    public static DbEmbedEmotishare deserialize(byte abyte0[])
    {
        DbEmbedEmotishare dbembedemotishare;
        if(abyte0 == null)
            dbembedemotishare = null;
        else
            dbembedemotishare = deserialize(ByteBuffer.wrap(abyte0));
        return dbembedemotishare;
    }

    public static byte[] serialize(DbEmbedEmotishare dbembedemotishare)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(64);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        putShortString(dataoutputstream, dbembedemotishare.mUrl);
        putShortString(dataoutputstream, dbembedemotishare.getImageUrl());
        putShortString(dataoutputstream, dbembedemotishare.mName);
        putShortString(dataoutputstream, dbembedemotishare.mDescription);
        putShortString(dataoutputstream, dbembedemotishare.mType);
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public final EmbedClientItem createEmbed()
    {
        EmbedClientItem embedclientitem = new EmbedClientItem();
        embedclientitem.type = new ArrayList();
        embedclientitem.type.add("EMOTISHARE");
        Emotishare emotishare = new Emotishare();
        emotishare.url = mUrl;
        emotishare.emotion = mType;
        emotishare.name = mName;
        if(mImageUrl != null)
        {
            emotishare.proxiedImage = new Thumbnail();
            emotishare.proxiedImage.setImageUrl(mImageUrl) ;
        }
        embedclientitem.emotishare = emotishare;
        embedclientitem.type.add("THING");
        Thing thing = new Thing();
        thing.url = mUrl;
        thing.name = mName;
        thing.imageUrl = mImageUrl;
        embedclientitem.thing = thing;
        return embedclientitem;
    }

    public int describeContents()
    {
        return 0;
    }

    public final MediaRef getImageRef()
    {
        return mImageRef;
    }

    public final String getImageUrl()
    {
        String s;
        if(mImageUrl != null)
            s = mImageUrl;
        else
            s = mUrl;
        return s;
    }

    public final String getName()
    {
        return mName;
    }

    public final String getType()
    {
        return mType;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mUrl);
        parcel.writeString(mImageUrl);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
        parcel.writeString(mType);
    }


}
