/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.oob;

import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxResponseParcelable implements Parcelable {

	private MobileOutOfBoxResponse mResponse;
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new OutOfBoxResponseParcelable(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new OutOfBoxResponseParcelable[i];
        }

    };
    
    private OutOfBoxResponseParcelable(Parcel parcel)
    {
        int i = parcel.readInt();
        if(i > 0)
        {
            byte abyte0[] = new byte[i];
            parcel.readByteArray(abyte0);
            mResponse = (MobileOutOfBoxResponse)JsonUtil.fromByteArray(abyte0, MobileOutOfBoxResponse.class);
        }
    }

    public OutOfBoxResponseParcelable(MobileOutOfBoxResponse mobileoutofboxresponse)
    {
        mResponse = mobileoutofboxresponse;
    }

    public int describeContents()
    {
        return 0;
    }

    public final MobileOutOfBoxResponse getResponse()
    {
        return mResponse;
    }

	public void writeToParcel(Parcel parcel, int i) {
		if (mResponse != null) {
			byte abyte0[] = JsonUtil.toByteArray(mResponse);
			parcel.writeInt(abyte0.length);
			parcel.writeByteArray(abyte0);
		} else {
			parcel.writeInt(0);
		}
	}
}
