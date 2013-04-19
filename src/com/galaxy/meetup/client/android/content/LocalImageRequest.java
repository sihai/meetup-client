/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import WriteReviewOperation.MediaRef;
import android.net.Uri;

import com.galaxy.meetup.client.android.content.cache.ImageRequest;

/**
 * 
 * @author sihai
 *
 */
public class LocalImageRequest extends ImageRequest {

	private int mHashCode;
    private final int mHeight;
    private final Uri mUri;
    private final int mWidth;
    
    public LocalImageRequest(MediaRef mediaref, int i, int j)
    {
        if(mediaref == null || !mediaref.hasLocalUri())
        {
            throw new IllegalArgumentException("MediaRef must have a local URI");
        } else
        {
            mUri = mediaref.getLocalUri();
            mWidth = i;
            mHeight = j;
            return;
        }
    }

    public final boolean equals(Object obj)
    {
        boolean flag = true;
        if(obj == this) {
        	return true;
        }
        
        if(!(obj instanceof LocalImageRequest))
        {
            flag = false;
        } else
        {
            LocalImageRequest localimagerequest = (LocalImageRequest)obj;
            if(!mUri.equals(localimagerequest.mUri) || mWidth != localimagerequest.mWidth || mHeight != localimagerequest.mHeight)
                flag = false;
        }
        return flag;
        
    }

    public final int getHeight()
    {
        return mHeight;
    }

    public final Uri getUri()
    {
        return mUri;
    }

    public final String getUriForLogging()
    {
        return mUri.toString();
    }

    public final int getWidth()
    {
        return mWidth;
    }

    public final int hashCode()
    {
        if(mHashCode == 0)
            mHashCode = 31 * (31 * (527 + mUri.hashCode()) + mWidth) + mHeight;
        return mHashCode;
    }

    public final boolean isEmpty()
    {
        boolean flag;
        if(mUri == null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final String toString()
    {
        return (new StringBuilder("LocalImageRequest: ")).append(mUri.toString()).append(" (").append(mWidth).append(", ").append(mHeight).append(")").toString();
    }

}
