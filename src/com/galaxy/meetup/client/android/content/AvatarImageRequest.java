/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.cache.CachedImageRequest;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class AvatarImageRequest extends CachedImageRequest {

	private String mDownloadUrl;
    private final String mGaiaId;
    private int mHashCode;
    private final int mSize;
    private final int mSizeInPx;
    private final String mUrl;
    
    public AvatarImageRequest(String gaiaId, String url, int size, int sizeInPx) {
        mGaiaId = gaiaId;
        mUrl = url;
        mSize = size;
        mSizeInPx = sizeInPx;
    }
    
	@Override
	protected String getCacheFilePrefix() {
		String prefix = null;
		switch(mSize) {
			case 0:
				prefix = "AT";
				break;
			case 1:
				prefix = "AS";
				break;
			case 2:
				prefix = "AM";
				break;
			default:
				break;
		}
		return prefix;
	}

	public final String getCanonicalDownloadUrl() {
        return getDownloadUrl();
    }

    public final String getDownloadUrl() {
        if(mDownloadUrl == null)
            mDownloadUrl = ImageUtils.getCroppedAndResizedUrl(mSizeInPx, mUrl);
        return mDownloadUrl;
    }

    public final String getGaiaId() {
        return mGaiaId;
    }

    public final String getUriForLogging() {
        return (new StringBuilder("avatar:")).append(mGaiaId).append("/size=").append(mSize).toString();
    }

	public final boolean isEmpty() {
		return false;
	}
    
	public final int hashCode() {
		if (mHashCode == 0) {
			if (mUrl != null)
				mHashCode = mUrl.hashCode();
			else
				mHashCode = 1;
			mHashCode = 31 * mHashCode + mSize;
		}
		return mHashCode;
	}
	
	public final boolean equals(Object obj) {
		
		if(!(obj instanceof AvatarImageRequest)) {
			return false;
		}
		AvatarImageRequest avatarimagerequest = (AvatarImageRequest)obj;
		if(avatarimagerequest.mSize != mSize || !TextUtils.equals(mUrl, avatarimagerequest.mUrl)) {
			return false;
		}
		return true;
    }
	
    public final String toString() {
    	
    	String size = null;
		switch(mSize) {
			case 0:
				size = "tiny";
				break;
			case 1:
				size = "small";
				break;
			case 2:
				size = "medium";
				break;
			default:
				break;
		}
		return (new StringBuilder("AvatarImageRequest: ")).append(mGaiaId).append(" (").append(size).append(")").toString();
    }
}
