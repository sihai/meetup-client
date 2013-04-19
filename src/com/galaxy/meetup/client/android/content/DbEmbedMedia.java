/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import WriteReviewOperation.MediaRef;
import android.text.TextUtils;

import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.PlusPhoto;
import com.galaxy.meetup.server.client.domain.PlusPhotoAlbum;
import com.galaxy.meetup.server.client.domain.Thing;
import com.galaxy.meetup.server.client.domain.VideoObject;
import com.galaxy.meetup.server.client.domain.WebPage;

/**
 * 
 * @author sihai
 *
 */
public class DbEmbedMedia extends DbSerializer {

	protected String mAlbumId;
    protected String mContentUrl;
    protected String mDescription;
    protected short mHeight;
    protected String mImageUrl;
    protected boolean mIsAlbum;
    protected boolean mIsPanorama;
    protected boolean mIsVideo;
    protected String mOwnerId;
    protected long mPhotoId;
    protected String mTitle;
    protected short mWidth;
    
    protected DbEmbedMedia()
    {
    }

    public DbEmbedMedia(PlusPhoto plusphoto)
    {
        initPlusPhoto(plusphoto);
    }

    public DbEmbedMedia(PlusPhotoAlbum plusphotoalbum)
    {
        int i;
        boolean flag;
        if(plusphotoalbum.associatedMedia != null)
            i = plusphotoalbum.associatedMedia.size();
        else
            i = 0;
        if(i > 0)
            initPlusPhoto((PlusPhoto)plusphotoalbum.associatedMedia.get(0));
        if(i > 1)
            flag = true;
        else
            flag = false;
        mIsAlbum = flag;
    }

    public DbEmbedMedia(Thing thing)
    {
        if(TextUtils.isEmpty(thing.name))
        {
            mTitle = thing.description;
        } else
        {
            mTitle = thing.name;
            mDescription = thing.description;
        }
        mContentUrl = thing.url;
        mImageUrl = thing.imageUrl;
    }

    public DbEmbedMedia(VideoObject videoobject)
    {
        mContentUrl = videoobject.url;
        mImageUrl = videoobject.thumbnailUrl;
        mWidth = (short)PrimitiveUtils.safeInt(videoobject.widthPx);
        mHeight = (short)PrimitiveUtils.safeInt(videoobject.heightPx);
        if(!TextUtils.isEmpty(videoobject.name))
        {
            mTitle = videoobject.name;
            mDescription = videoobject.description;
        } else
        {
            mTitle = videoobject.description;
        }
        mIsVideo = true;
    }

    public DbEmbedMedia(WebPage webpage)
    {
        mTitle = webpage.name;
        mDescription = webpage.description;
        mContentUrl = webpage.url;
        mImageUrl = webpage.imageUrl;
    }

    public static DbEmbedMedia deserialize(byte abyte0[])
    {
        boolean flag = true;
        DbEmbedMedia dbembedmedia;
        if(abyte0 == null)
        {
            dbembedmedia = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            dbembedmedia = new DbEmbedMedia();
            dbembedmedia.mTitle = getShortString(bytebuffer);
            dbembedmedia.mDescription = getShortString(bytebuffer);
            dbembedmedia.mContentUrl = getShortString(bytebuffer);
            dbembedmedia.mImageUrl = getShortString(bytebuffer);
            dbembedmedia.mOwnerId = getShortString(bytebuffer);
            dbembedmedia.mAlbumId = getShortString(bytebuffer);
            dbembedmedia.mPhotoId = bytebuffer.getLong();
            dbembedmedia.mWidth = bytebuffer.getShort();
            dbembedmedia.mHeight = bytebuffer.getShort();
            boolean flag1;
            boolean flag2;
            if(bytebuffer.get() == 1)
                flag1 = flag;
            else
                flag1 = false;
            dbembedmedia.mIsPanorama = flag1;
            if(bytebuffer.get() == 1)
                flag2 = flag;
            else
                flag2 = false;
            dbembedmedia.mIsVideo = flag2;
            if(bytebuffer.get() != 1)
                flag = false;
            dbembedmedia.mIsAlbum = flag;
        }
        return dbembedmedia;
    }

    private void initPlusPhoto(PlusPhoto plusphoto)
    {
        mImageUrl = plusphoto.originalMediaPlayerUrl;
        mOwnerId = plusphoto.ownerObfuscatedId;
        mAlbumId = plusphoto.albumId;
        String s = plusphoto.photoId;
        long l;
        boolean flag;
        if(s == null)
            l = 0L;
        else
            l = PrimitiveUtils.safeLong(Long.valueOf(s));
        mPhotoId = l;
        if(plusphoto.thumbnail != null)
        {
            mWidth = (short)PrimitiveUtils.safeInt(plusphoto.thumbnail.widthPx);
            mHeight = (short)PrimitiveUtils.safeInt(plusphoto.thumbnail.heightPx);
        }
        if(PrimitiveUtils.safeBoolean(plusphoto.isVideo) && !TextUtils.isEmpty(plusphoto.originalContentUrl))
            flag = true;
        else
            flag = false;
        mIsVideo = flag;
        mIsPanorama = "PHOTOSPHERE".equals(plusphoto.mediaType);
        if(mIsVideo)
            mContentUrl = plusphoto.originalContentUrl;
    }

    public static byte[] serialize(DbEmbedMedia dbembedmedia)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(64);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        putShortString(dataoutputstream, dbembedmedia.mTitle);
        putShortString(dataoutputstream, dbembedmedia.mDescription);
        putShortString(dataoutputstream, dbembedmedia.mContentUrl);
        putShortString(dataoutputstream, dbembedmedia.mImageUrl);
        putShortString(dataoutputstream, dbembedmedia.mOwnerId);
        putShortString(dataoutputstream, dbembedmedia.mAlbumId);
        dataoutputstream.writeLong(dbembedmedia.mPhotoId);
        dataoutputstream.writeShort(dbembedmedia.mWidth);
        dataoutputstream.writeShort(dbembedmedia.mHeight);
        dataoutputstream.writeBoolean(dbembedmedia.mIsPanorama);
        dataoutputstream.writeBoolean(dbembedmedia.mIsVideo);
        dataoutputstream.writeBoolean(dbembedmedia.mIsAlbum);
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public final String getAlbumId()
    {
        return mAlbumId;
    }

    public final String getContentUrl()
    {
        String s;
        if(mIsVideo)
            s = null;
        else
            s = mContentUrl;
        return s;
    }

    public final String getDescription()
    {
        return mDescription;
    }

    public final short getHeight()
    {
        return mHeight;
    }

    public final String getImageUrl()
    {
        return mImageUrl;
    }

    public final MediaRef.MediaType getMediaType()
    {
        MediaRef.MediaType mediatype;
        if(mIsVideo)
            mediatype = MediaRef.MediaType.VIDEO;
        else if(mIsPanorama)
            mediatype = MediaRef.MediaType.PANORAMA;
        else
            mediatype = MediaRef.MediaType.IMAGE;
        return mediatype;
    }

    public final String getOwnerId()
    {
        return mOwnerId;
    }

    public final long getPhotoId()
    {
        return mPhotoId;
    }

    public final String getTitle()
    {
        return mTitle;
    }

    public final String getVideoUrl()
    {
        String s;
        if(mIsVideo)
            s = mContentUrl;
        else
            s = null;
        return s;
    }

    public final short getWidth()
    {
        return mWidth;
    }

    public final boolean isAlbum()
    {
        return mIsAlbum;
    }

    public final boolean isPanorama()
    {
        return mIsPanorama;
    }

    public final boolean isVideo()
    {
        return mIsVideo;
    }
}
