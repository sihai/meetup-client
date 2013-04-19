/**
 * galaxy inc.
 * meetup client for android
 */
package WriteReviewOperation;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class MediaRef implements Parcelable {
	
	private final String mDisplayName;
    private final Uri mLocalUri;
    private final String mOwnerGaiaId;
    private final long mPhotoId;
    private final MediaType mType;
    private final String mUrl;
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel) {
            return new MediaRef(parcel);
        }

        public final Object[] newArray(int i) {
            return new MediaRef[i];
        }

    };
    
    private MediaRef(Parcel parcel) {
        mOwnerGaiaId = parcel.readString();
        mPhotoId = parcel.readLong();
        mUrl = parcel.readString();
        String s = parcel.readString();
        if(s != null)
            mLocalUri = Uri.parse(s);
        else
            mLocalUri = null;
        mDisplayName = parcel.readString();
        mType = MediaType.valueOf(parcel.readInt());
    }

    public MediaRef(String ownerGaiaId, long photoId, String url, Uri uri, MediaType mediatype) {
        this(ownerGaiaId, photoId, url, uri, null, mediatype);
    }

    public MediaRef(String ownerGaiaId, long photoId, String url, Uri uri, String displayName, MediaType mediatype) {
        mOwnerGaiaId = ownerGaiaId;
        mPhotoId = photoId;
        mUrl = url;
        mLocalUri = uri;
        mDisplayName = displayName;
        mType = mediatype;
    }

    public MediaRef(String url, MediaType mediatype) {
        this(null, 0L, url, null, null, mediatype);
    }
    
    public int describeContents()
    {
        return 0;
    }

    public boolean equals(Object obj) {
    	if(!(obj instanceof MediaRef)) {
    		return false;
    	}
    	MediaRef mediaref = (MediaRef)obj;
    	if(mPhotoId != mediaref.mPhotoId || TextUtils.equals(mUrl, mediaref.mUrl) || mType != mediaref.mType) {
    		return false;
    	}
        boolean result;
        if(mLocalUri != null && mediaref.mLocalUri != null)
        	result = mLocalUri.equals(mediaref.mLocalUri);
        else if(mLocalUri == null && mediaref.mLocalUri == null)
        	result = true;
        else
        	result = false;
    	return result;
    }

    public final String getDisplayName()
    {
        return mDisplayName;
    }

    public final Uri getLocalUri() {
        return mLocalUri;
    }

    public final String getOwnerGaiaId() {
        return mOwnerGaiaId;
    }

    public final long getPhotoId() {
        return mPhotoId;
    }

    public final MediaType getType() {
        return mType;
    }

    public final String getUrl() {
        return mUrl;
    }

    public final boolean hasLocalUri() {
        boolean flag;
        if(mLocalUri != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean hasPhotoId() {
        boolean flag;
        if(mPhotoId != 0L)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean hasUrl() {
        boolean flag;
        if(mUrl != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public int hashCode() {
        int i = (int)(mPhotoId ^ mPhotoId >>> 32);
        if(mOwnerGaiaId != null)
            i ^= mOwnerGaiaId.hashCode();
        if(mUrl != null)
            i ^= mUrl.hashCode();
        if(mLocalUri != null)
            i ^= mLocalUri.hashCode();
        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = (new StringBuilder()).append(super.toString()).append("( ");
        String s;
        if(mDisplayName == null)
            s = "";
        else
            s = mDisplayName;
        return stringbuilder.append(s).append(" [g-").append(mOwnerGaiaId).append(", p-").append(mPhotoId).append("], ").append(mUrl).append(", ").append(mLocalUri).append(")").toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mOwnerGaiaId);
        parcel.writeLong(mPhotoId);
        parcel.writeString(mUrl);
        if(mLocalUri != null)
            parcel.writeString(mLocalUri.toString());
        else
            parcel.writeString(null);
        parcel.writeString(mDisplayName);
        parcel.writeInt(mType.getValue());
    }
    
    //=====================================================================
    //
    //=====================================================================
    public static enum MediaType {
    	
    	IMAGE(0),
        VIDEO(1),
        PANORAMA(2);
    	
    	private final int mValue;
    	
    	private MediaType(int value) {
            mValue = value;
        }
    	
    	public final int getValue() {
            return mValue;
        }
    	
    	public static MediaType valueOf(int value) {
    		for(MediaType t : MediaType.values()) {
    			if(t.mValue == value) {
    				return t;
    			}
    		}
    		return null;
    	}
    }
}
