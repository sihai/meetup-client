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

import com.galaxy.meetup.client.android.api.ApiUtils;
import com.galaxy.meetup.server.client.domain.EmbedsSquare;
import com.galaxy.meetup.server.client.domain.SquareInvite;
import com.galaxy.meetup.server.client.domain.SquareUpdate;

/**
 * 
 * @author sihai
 *
 */
public class DbEmbedSquare extends DbSerializer {

	protected String mAboutSquareId;
    protected String mAboutSquareName;
    protected String mImageUrl;
    protected boolean mIsInvitation;
    protected String mSquareId;
    protected String mSquareName;
    protected String mSquareStreamId;
    protected String mSquareStreamName;
    
    protected DbEmbedSquare()
    {
    }

    private DbEmbedSquare(SquareUpdate squareupdate)
    {
        if(squareupdate != null)
        {
            mSquareId = squareupdate.obfuscatedSquareId;
            mSquareName = squareupdate.squareName;
            mSquareStreamId = squareupdate.squareStreamId;
            mSquareStreamName = squareupdate.squareStreamName;
        }
    }

    private DbEmbedSquare(SquareUpdate squareupdate, EmbedsSquare embedssquare)
    {
        this(squareupdate);
        mAboutSquareId = resolveSquareId(embedssquare.getCommunityId(), embedssquare.getUrl());
        mAboutSquareName = embedssquare.getName();
        mImageUrl = ApiUtils.prependProtocol(embedssquare.getImageUrl());
    }

    private DbEmbedSquare(SquareUpdate squareupdate, SquareInvite squareinvite)
    {
        this(squareupdate);
        mAboutSquareId = resolveSquareId(squareinvite.communityId, squareinvite.url);
        mAboutSquareName = squareinvite.name;
        mImageUrl = ApiUtils.prependProtocol(squareinvite.imageUrl);
        mIsInvitation = true;
    }

    public static DbEmbedSquare deserialize(byte abyte0[])
    {
        boolean flag = true;
        DbEmbedSquare dbembedsquare;
        if(abyte0 == null)
        {
            dbembedsquare = null;
        } else
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            dbembedsquare = new DbEmbedSquare();
            dbembedsquare.mSquareId = getShortString(bytebuffer);
            dbembedsquare.mSquareName = getShortString(bytebuffer);
            dbembedsquare.mSquareStreamId = getShortString(bytebuffer);
            dbembedsquare.mSquareStreamName = getShortString(bytebuffer);
            dbembedsquare.mAboutSquareId = getShortString(bytebuffer);
            dbembedsquare.mAboutSquareName = getShortString(bytebuffer);
            dbembedsquare.mImageUrl = getShortString(bytebuffer);
            if(bytebuffer.get() != 1)
                flag = false;
            dbembedsquare.mIsInvitation = flag;
        }
        return dbembedsquare;
    }

    private static String resolveSquareId(String s, String s1)
    {
        if(TextUtils.isEmpty(s))
            if(s1 != null && s1.startsWith("communities/"))
                s = s1.substring(12);
            else
                s = null;
        return s;
    }

    private static byte[] serialize(DbEmbedSquare dbembedsquare)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(128);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        putShortString(dataoutputstream, dbembedsquare.mSquareId);
        putShortString(dataoutputstream, dbembedsquare.mSquareName);
        putShortString(dataoutputstream, dbembedsquare.mSquareStreamId);
        putShortString(dataoutputstream, dbembedsquare.mSquareStreamName);
        putShortString(dataoutputstream, dbembedsquare.mAboutSquareId);
        putShortString(dataoutputstream, dbembedsquare.mAboutSquareName);
        putShortString(dataoutputstream, dbembedsquare.mImageUrl);
        dataoutputstream.writeBoolean(dbembedsquare.mIsInvitation);
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public static byte[] serialize(SquareUpdate squareupdate)
        throws IOException
    {
        return serialize(new DbEmbedSquare(squareupdate));
    }

    public static byte[] serialize(SquareUpdate squareupdate, EmbedsSquare embedssquare)
        throws IOException
    {
        return serialize(new DbEmbedSquare(squareupdate, embedssquare));
    }

    public static byte[] serialize(SquareUpdate squareupdate, SquareInvite squareinvite)
        throws IOException
    {
        return serialize(new DbEmbedSquare(squareupdate, squareinvite));
    }

    public final String getAboutSquareId()
    {
        return mAboutSquareId;
    }

    public final String getAboutSquareName()
    {
        return mAboutSquareName;
    }

    public final String getImageUrl()
    {
        return mImageUrl;
    }

    public final String getSquareId()
    {
        return mSquareId;
    }

    public final String getSquareName()
    {
        return mSquareName;
    }

    public final String getSquareStreamId()
    {
        return mSquareStreamId;
    }

    public final String getSquareStreamName()
    {
        return mSquareStreamName;
    }

    public final boolean isInvitation()
    {
        return mIsInvitation;
    }
}
