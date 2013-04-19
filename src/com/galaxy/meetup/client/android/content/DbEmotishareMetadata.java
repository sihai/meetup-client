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
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author sihai
 *
 */
public class DbEmotishareMetadata extends DbSerializer implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new DbEmotishareMetadata(parcel, (byte)0);
        }

        public final Object[] newArray(int i)
        {
            return new DbEmotishareMetadata[i];
        }

    };
    
    private List mCategory;
    private DbEmbedEmotishare mEmbed;
    private int mGeneration;
    private MediaRef mIconRef;
    private String mIconUrl;
    private int mId;
    private String mShareText;
    
    private DbEmotishareMetadata()
    {
    }

    public DbEmotishareMetadata(int i, ArrayList arraylist, String s, String s1, DbEmbedEmotishare dbembedemotishare, int j)
    {
        mId = i;
        mCategory = arraylist;
        mShareText = s;
        mIconUrl = s1;
        mGeneration = j;
        mEmbed = dbembedemotishare;
        mIconRef = createMediaRef(mIconUrl);
    }

    private DbEmotishareMetadata(Parcel parcel)
    {
        mId = parcel.readInt();
        mCategory = new ArrayList();
        parcel.readStringList(mCategory);
        mShareText = parcel.readString();
        mIconUrl = parcel.readString();
        mGeneration = parcel.readInt();
        mEmbed = (DbEmbedEmotishare)parcel.readParcelable(DbEmbedEmotishare.class.getClassLoader());
        mIconRef = createMediaRef(mIconUrl);
    }

    DbEmotishareMetadata(Parcel parcel, byte byte0)
    {
        this(parcel);
    }

    public static MediaRef createMediaRef(String s)
    {
        return new MediaRef(null, 0L, s, null, MediaRef.MediaType.IMAGE);
    }

    public static DbEmotishareMetadata deserialize(byte abyte0[])
    {
        DbEmotishareMetadata dbemotisharemetadata;
        if(abyte0 == null)
        {
            dbemotisharemetadata = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            dbemotisharemetadata = new DbEmotishareMetadata();
            dbemotisharemetadata.mId = bytebuffer.getInt();
            dbemotisharemetadata.mCategory = new ArrayList(getShortStringList(bytebuffer));
            dbemotisharemetadata.mShareText = getShortString(bytebuffer);
            dbemotisharemetadata.mIconUrl = getShortString(bytebuffer);
            dbemotisharemetadata.mGeneration = bytebuffer.getInt();
            dbemotisharemetadata.mEmbed = DbEmbedEmotishare.deserialize(bytebuffer);
            dbemotisharemetadata.mIconRef = createMediaRef(dbemotisharemetadata.mIconUrl);
        }
        return dbemotisharemetadata;
    }

    public static byte[] serialize(DbEmotishareMetadata dbemotisharemetadata)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(64);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        dataoutputstream.writeInt(dbemotisharemetadata.mId);
        putShortStringList(dataoutputstream, dbemotisharemetadata.mCategory);
        putShortString(dataoutputstream, dbemotisharemetadata.mShareText);
        putShortString(dataoutputstream, dbemotisharemetadata.mIconUrl);
        dataoutputstream.writeInt(dbemotisharemetadata.mGeneration);
        dataoutputstream.write(DbEmbedEmotishare.serialize(dbemotisharemetadata.mEmbed));
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public int describeContents()
    {
        return 0;
    }

    public final DbEmbedEmotishare getEmbed()
    {
        return mEmbed;
    }

    public final int getGeneration()
    {
        return mGeneration;
    }

    public final MediaRef getIconRef()
    {
        return mIconRef;
    }

    public final int getId()
    {
        return mId;
    }

    public final MediaRef getImageRef()
    {
        MediaRef mediaref;
        if(mEmbed == null)
            mediaref = null;
        else
            mediaref = mEmbed.getImageRef();
        return mediaref;
    }

    public final String getImageUrl()
    {
        String s;
        if(mEmbed == null)
            s = null;
        else
            s = mEmbed.getImageUrl();
        return s;
    }

    public final String getName()
    {
        String s;
        if(mEmbed == null)
            s = null;
        else
            s = mEmbed.getName();
        return s;
    }

    public final String getShareText()
    {
        return mShareText;
    }

    public final String getType()
    {
        String s;
        if(mEmbed == null)
            s = null;
        else
            s = mEmbed.getType();
        return s;
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder("TypedImageEmbed name: ");
        String s;
        if(mEmbed == null)
            s = null;
        else
            s = mEmbed.getName();
        return stringbuilder.append(s).append(", ID: ").append(mId).append(", cat: ").append(mCategory).append(", share: ").append(mShareText).append(", icon: ").append(mIconUrl).append(", gen: ").append(mGeneration).append(", embed: ").append(mEmbed).toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mId);
        parcel.writeStringList(mCategory);
        parcel.writeString(mShareText);
        parcel.writeString(mIconUrl);
        parcel.writeInt(mGeneration);
        parcel.writeParcelable(mEmbed, 0);
    }

}
