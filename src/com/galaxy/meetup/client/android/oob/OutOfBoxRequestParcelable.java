/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.oob;

import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.server.client.domain.request.MobileOutOfBoxRequest;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxRequestParcelable implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new OutOfBoxRequestParcelable(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new OutOfBoxRequestParcelable[i];
        }

    };

	private MobileOutOfBoxRequest mRequest;
	
	OutOfBoxRequestParcelable(Parcel parcel)
    {
        int i = parcel.readInt();
        if(i > 0)
        {
            byte abyte0[] = new byte[i];
            parcel.readByteArray(abyte0);
            mRequest = (MobileOutOfBoxRequest)JsonUtil.fromByteArray(abyte0, MobileOutOfBoxRequest.class);
        }
    }

    public OutOfBoxRequestParcelable(MobileOutOfBoxRequest mobileoutofboxrequest)
    {
        mRequest = mobileoutofboxrequest;
    }

    public int describeContents()
    {
        return 0;
    }

    public final MobileOutOfBoxRequest getRequest()
    {
        return mRequest;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        if(mRequest != null)
        {
            byte abyte0[] = JsonUtil.toByteArray(mRequest);
            parcel.writeInt(abyte0.length);
            parcel.writeByteArray(abyte0);
        } else
        {
            parcel.writeInt(0);
        }
    }
}
