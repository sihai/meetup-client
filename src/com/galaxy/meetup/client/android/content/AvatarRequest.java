/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.cache.ImageRequest;

/**
 * 
 * @author sihai
 *
 */
public class AvatarRequest extends ImageRequest {

	private final String mAvatarUrl;
    private final String mContactLookupKey;
    private final String mGaiaId;
    private int mHashCode;
    private final int mIdType;
    private final boolean mRounded;
    private final int mSize;
    
	public AvatarRequest() {
		this(null, 0);
	}

	public AvatarRequest(String gaiaId, int size) {
		this(gaiaId, size, false);
	}

	public AvatarRequest(String gaiaId, int size, boolean rounded) {
		this(gaiaId, null, size, rounded);
	}

	public AvatarRequest(String gaiaId, String avatarUrl, int size) {
		this(gaiaId, avatarUrl, size, false);
	}

	public AvatarRequest(String gaiaId, String avatarUrl, int size, boolean rounded) {
		mIdType = 0;
		mGaiaId = gaiaId;
		mAvatarUrl = avatarUrl;
		mContactLookupKey = null;
		mSize = size;
		mRounded = rounded;
	}
    
	public final String getAvatarUrl() {
		return mAvatarUrl;
	}

	public final String getGaiaId() {
		return mGaiaId;
	}

	public final int getSize() {
		return mSize;
	}

	public final String getUriForLogging() {
		return (new StringBuilder("avatar:")).append(mGaiaId).append("/size=")
				.append(mSize).toString();
	}
	
	public final boolean isEmpty() {
		boolean flag;
		if (mGaiaId == null)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public final boolean isRounded() {
		return mRounded;
	}
	
	public final String toString() {
		// FIXME
		return "";
    }
	
	public final boolean equals(Object obj) {
		if (obj != this) {
			if (!(obj instanceof AvatarRequest)) {
				return false;
			}
			AvatarRequest avatarrequest = (AvatarRequest) obj;
			if (mSize != avatarrequest.mSize
					|| mRounded != avatarrequest.mRounded
					|| !TextUtils.equals(mGaiaId, avatarrequest.mGaiaId)) {
				return false;
			}
		}
		return true;
	}
	
	public final int hashCode() {
        if(mHashCode == 0) {
            if(mGaiaId != null)
                mHashCode = mGaiaId.hashCode() ^ mSize;
            else
                mHashCode = mSize;
            if(mRounded)
                mHashCode = 1 + mHashCode;
        }
        return mHashCode;
    }
}
