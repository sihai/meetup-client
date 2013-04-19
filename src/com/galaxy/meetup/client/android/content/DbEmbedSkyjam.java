/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.text.TextUtils;

import com.galaxy.meetup.server.client.domain.PlayMusicAlbum;
import com.galaxy.meetup.server.client.domain.PlayMusicTrack;

/**
 * 
 * @author sihai
 *
 */
public class DbEmbedSkyjam  extends DbSerializer {

	protected String mAlbum;
    protected String mArtist;
    protected String mImageUrl;
    protected String mMarketUrl;
    protected String mPreviewUrl;
    protected String mSong;
    
    protected DbEmbedSkyjam()
    {
    }

    public DbEmbedSkyjam(PlayMusicAlbum playmusicalbum)
    {
        mArtist = playmusicalbum.byArtist.name;
        mAlbum = playmusicalbum.name;
        mImageUrl = playmusicalbum.imageUrl;
        mMarketUrl = playmusicalbum.offerUrlWithSessionIndex;
        mPreviewUrl = playmusicalbum.audioUrlWithSessionIndex;
    }

    public DbEmbedSkyjam(PlayMusicTrack playmusictrack)
    {
        mSong = playmusictrack.name;
        mArtist = playmusictrack.byArtist.name;
        mAlbum = playmusictrack.inAlbum.name;
        mImageUrl = playmusictrack.inAlbum.imageUrl;
        mMarketUrl = playmusictrack.offerUrlWithSessionIndex;
        mPreviewUrl = playmusictrack.audioEmbedUrlWithSessionIndex;
    }

    public static DbEmbedSkyjam deserialize(byte abyte0[])
    {
        DbEmbedSkyjam dbembedskyjam;
        if(abyte0 == null)
        {
            dbembedskyjam = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            dbembedskyjam = new DbEmbedSkyjam();
            dbembedskyjam.mSong = getShortString(bytebuffer);
            dbembedskyjam.mArtist = getShortString(bytebuffer);
            dbembedskyjam.mAlbum = getShortString(bytebuffer);
            dbembedskyjam.mImageUrl = getShortString(bytebuffer);
            dbembedskyjam.mMarketUrl = getShortString(bytebuffer);
            dbembedskyjam.mPreviewUrl = getShortString(bytebuffer);
        }
        return dbembedskyjam;
    }

    private static byte[] serialize(DbEmbedSkyjam dbembedskyjam)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(256);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        putShortString(dataoutputstream, dbembedskyjam.mSong);
        putShortString(dataoutputstream, dbembedskyjam.mArtist);
        putShortString(dataoutputstream, dbembedskyjam.mAlbum);
        putShortString(dataoutputstream, dbembedskyjam.mImageUrl);
        putShortString(dataoutputstream, dbembedskyjam.mMarketUrl);
        putShortString(dataoutputstream, dbembedskyjam.mPreviewUrl);
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public static byte[] serialize(PlayMusicAlbum playmusicalbum)
        throws IOException
    {
        return serialize(new DbEmbedSkyjam(playmusicalbum));
    }

    public static byte[] serialize(PlayMusicTrack playmusictrack)
        throws IOException
    {
        return serialize(new DbEmbedSkyjam(playmusictrack));
    }

    public final String getAlbum()
    {
        return mAlbum;
    }

    public final String getArtist()
    {
        return mArtist;
    }

    public final String getImageUrl()
    {
        return mImageUrl;
    }

    public final String getMarketUrl()
    {
        return mMarketUrl;
    }

    public final String getPreviewUrl()
    {
        return mPreviewUrl;
    }

    public final String getSong()
    {
        return mSong;
    }

    public final boolean isAlbum()
    {
        return TextUtils.isEmpty(mSong);
    }
}
