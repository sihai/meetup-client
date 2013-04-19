/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class CallToActionData implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new CallToActionData(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new CallToActionData[i];
        }

    };
    
	public final String mDeepLinkId;
    public final String mLabel;
    public final String mUrl;
    
	public CallToActionData(Parcel parcel)
    {
        mLabel = parcel.readString();
        mUrl = parcel.readString();
        mDeepLinkId = parcel.readString();
    }

    public CallToActionData(String s, String s1, String s2)
    {
        if(TextUtils.isEmpty(s1) && TextUtils.isEmpty(s2))
        {
            throw new IllegalArgumentException("At least one of url or deepLinkId is required.");
        } else
        {
            mLabel = s;
            mUrl = s1;
            mDeepLinkId = s2;
            return;
        }
    }

    public static CallToActionData fromExtras(Bundle bundle) {
        if(null == bundle) {
        	return null;
        }
        
        CallToActionData calltoactiondata = null;
        String s = bundle.getString("label");
        String s1 = bundle.getString("url");
        String s2 = bundle.getString("deepLinkId");
        if(TextUtils.isEmpty(s1))
        {
            boolean flag = TextUtils.isEmpty(s2);
            if(flag)
                return null;
        }
        calltoactiondata = new CallToActionData(s, s1, s2);
        return calltoactiondata;
    }

    public int describeContents()
    {
        return 0;
    }

	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof CallToActionData)) {
			return false;
		} else {
			CallToActionData calltoactiondata = (CallToActionData) obj;
			if (!TextUtils.equals(mLabel, calltoactiondata.mLabel)
					|| !TextUtils.equals(mUrl, calltoactiondata.mUrl)
					|| !TextUtils.equals(mDeepLinkId,
							calltoactiondata.mDeepLinkId))
				return false;
		}

		return true;

	}

    public int hashCode()
    {
        int i;
        int j;
        int k;
        int l;
        String s;
        int i1;
        if(mLabel == null)
            i = 0;
        else
            i = mLabel.hashCode();
        j = 31 * (i + 527);
        if(mUrl == null)
            k = 0;
        else
            k = mUrl.hashCode();
        l = 31 * (j + k);
        s = mDeepLinkId;
        i1 = 0;
        if(s != null)
            i1 = mDeepLinkId.hashCode();
        return l + i1;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mLabel);
        parcel.writeString(mUrl);
        parcel.writeString(mDeepLinkId);
    }

}
