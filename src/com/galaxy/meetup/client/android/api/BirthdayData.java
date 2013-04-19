/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author sihai
 *
 */
public class BirthdayData implements Parcelable {

	private String mGaiaId;
    private String mName;
    private int mYear;
    
	public BirthdayData(Parcel parcel)
    {
        mGaiaId = parcel.readString();
        mName = parcel.readString();
        mYear = parcel.readInt();
    }

    public BirthdayData(String s, String s1, int i)
    {
        mGaiaId = s;
        mName = s1;
        mYear = i;
    }

    public int describeContents()
    {
        return 0;
    }

    public final String getGaiaId()
    {
        return mGaiaId;
    }

    public final String getName()
    {
        return mName;
    }

    public final int getYear()
    {
        return mYear;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mGaiaId);
        parcel.writeString(mName);
        parcel.writeInt(mYear);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new BirthdayData(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new BirthdayData[i];
        }

    };

}
