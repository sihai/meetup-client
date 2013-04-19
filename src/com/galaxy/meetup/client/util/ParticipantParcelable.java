/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author sihai
 *
 */
public class ParticipantParcelable implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new ParticipantParcelable(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new ParticipantParcelable[i];
        }

    };
    private final String mName;
    private final String mParticipantId;

    ParticipantParcelable(Parcel parcel)
    {
        mName = parcel.readString();
        mParticipantId = parcel.readString();
    }

    public ParticipantParcelable(String s, String s1)
    {
        mName = s;
        mParticipantId = s1;
    }

    public int describeContents()
    {
        return 0;
    }

    public final String getName()
    {
        return mName;
    }

    public final String getParticipantId()
    {
        return mParticipantId;
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder(64);
        stringbuilder.append("Name: ").append(mName);
        stringbuilder.append("ParticipantId: ").append(mParticipantId);
        return stringbuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mName);
        parcel.writeString(mParticipantId);
    }
}
