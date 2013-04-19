// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.content;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

// Referenced classes of package com.google.android.apps.plus.content:
//            NotificationSettingsCategory

public class NotificationSettingsData
    implements Parcelable
{

    private NotificationSettingsData(Parcel parcel)
    {
        mEmailAddress = parcel.readString();
        mMobileNotificationType = parcel.readString();
        mCategories = (NotificationSettingsCategory[])parcel.createTypedArray(NotificationSettingsCategory.CREATOR);
    }

    NotificationSettingsData(Parcel parcel, byte byte0)
    {
        this(parcel);
    }

    public NotificationSettingsData(String s, String s1, List list)
    {
        mEmailAddress = s;
        mMobileNotificationType = s1;
        if(list != null)
        {
            mCategories = new NotificationSettingsCategory[list.size()];
            list.toArray(mCategories);
        } else
        {
            mCategories = new NotificationSettingsCategory[0];
        }
    }

    public int describeContents()
    {
        return 0;
    }

    public final int getCategoriesCount()
    {
        return mCategories.length;
    }

    public final NotificationSettingsCategory getCategory(int i)
    {
        return mCategories[i];
    }

    public final String getEmailAddress()
    {
        return mEmailAddress;
    }

    public final String getMobileNotificationType()
    {
        return mMobileNotificationType;
    }

    public String toString()
    {
        return Arrays.asList(mCategories).toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mEmailAddress);
        parcel.writeString(mMobileNotificationType);
        parcel.writeTypedArray(mCategories, 0);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new NotificationSettingsData(parcel, (byte)0);
        }

        public final Object[] newArray(int i)
        {
            return new NotificationSettingsData[i];
        }

    }
;
    private final NotificationSettingsCategory mCategories[];
    private final String mEmailAddress;
    private final String mMobileNotificationType;

}
