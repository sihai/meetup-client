/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.cache.CachedImageRequest;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 *
 */
public class MediaImageRequest extends CachedImageRequest {

	private static Matcher sCanonicalMatcher = Pattern.compile("http://images\\d+-focus-opensocial.googleusercontent.com/gadgets/proxy").matcher("");
    private String mCanonicalUrl;
    private final boolean mCropAndResize;
    private String mDownloadUrl;
    private int mHashCode;
    private final int mHeight;
    private final int mMediaType;
    private final String mUrl;
    private final int mWidth;
    
    public MediaImageRequest() {
        this(null, 0, 0, 0, false);
    }

    public MediaImageRequest(String url, int i, int j) {
        this(url, 3, j, j, true);
    }

    public MediaImageRequest(String url, int mediaType, int width, int height, boolean cropAndResize) {
        if(url == null) {
            throw new NullPointerException();
        } else {
            mUrl = url;
            mMediaType = mediaType;
            mWidth = width;
            mHeight = height;
            mCropAndResize = cropAndResize;
            return;
        }
    }
    
    public static boolean areCanonicallyEqual(MediaImageRequest mediaimagerequest, MediaImageRequest mediaimagerequest1)
    {
        return TextUtils.equals(mediaimagerequest.getCanonicalUrl(), mediaimagerequest1.getCanonicalUrl());
    }

    public static boolean areCanonicallyEqual(MediaImageRequest mediaimagerequest, String s)
    {
        return TextUtils.equals(mediaimagerequest.getCanonicalUrl(), canonicalize(s));
    }

    public static boolean areCanonicallyEqual(String s, String s1)
    {
        return TextUtils.equals(canonicalize(s), canonicalize(s1));
    }

	private static String canonicalize(String s) {
		if (!TextUtils.isEmpty(s))
			synchronized (sCanonicalMatcher) {
				sCanonicalMatcher.reset(s);
				s = sCanonicalMatcher
						.replaceFirst("http://images1-focus-opensocial.googleusercontent.com/gadgets/proxy");
			}
		return s;
	}

	private String getCanonicalUrl() {
		if (mCanonicalUrl == null)
			mCanonicalUrl = canonicalize(mUrl);
		return mCanonicalUrl;
	}

    private String getDownloadUrl(String s)
    {
        if(mDownloadUrl == null)
        {
            mDownloadUrl = s.replace("&google_plus:card_type=nonsquare", "").replace("&google_plus:widget", "");
            if(mCropAndResize && mWidth != 0)
                if(mWidth == mHeight)
                    mDownloadUrl = ImageUtils.getCroppedAndResizedUrl(mWidth, mDownloadUrl);
                else
                    mDownloadUrl = ImageUtils.getCenterCroppedAndResizedUrl(mWidth, mHeight, mDownloadUrl);
            if(mDownloadUrl.startsWith("//"))
                mDownloadUrl = (new StringBuilder("http:")).append(mDownloadUrl).toString();
        }
        return mDownloadUrl;
    }

    public boolean equals(Object obj) {
    	
    	if(!(obj instanceof MediaImageRequest)) {
    		return false;
    	}
    	MediaImageRequest mediaimagerequest = (MediaImageRequest)obj;
    	if(mediaimagerequest.mWidth != mWidth || mediaimagerequest.mHeight != mHeight || mediaimagerequest.mMediaType != mMediaType || !areCanonicallyEqual(this, mediaimagerequest)) {
    		return false;
    	}
    	return true;
    }

	protected final String getCacheFilePrefix() {
		return "M";
	}

	public final String getCanonicalDownloadUrl() {
		return getDownloadUrl(getCanonicalUrl());
	}

	public String getDownloadUrl() {
		return getDownloadUrl(mUrl);
	}

	public final int getHeight() {
		return mHeight;
	}

	public final int getMediaType() {
		return mMediaType;
	}

	public final String getUrl() {
		return mUrl;
	}

	public final int getWidth() {
		return mWidth;
	}

	public int hashCode() {
		if (mHashCode == 0) {
			String s = getCanonicalUrl();
			if (s != null)
				mHashCode = s.hashCode();
			else
				mHashCode = 1;
		}
		return mHashCode;
	}

	public final boolean isEmpty() {
		boolean flag;
		if (mUrl == null)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public String toString() {
		return (new StringBuilder("MediaImageRequest: type="))
				.append(mMediaType).append(" ").append(mUrl).append(" (")
				.append(mWidth).append(", ").append(mHeight).append(")")
				.toString();
	}

}
