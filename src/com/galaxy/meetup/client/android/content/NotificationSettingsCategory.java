// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.content;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

// Referenced classes of package com.google.android.apps.plus.content:
//            NotificationSetting

public class NotificationSettingsCategory
    implements Parcelable
{

    private NotificationSettingsCategory(Parcel parcel)
    {
        mDescription = parcel.readString();
        mSettings = (NotificationSetting[])parcel.createTypedArray(NotificationSetting.CREATOR);
    }

    NotificationSettingsCategory(Parcel parcel, byte byte0)
    {
        this(parcel);
    }

    public NotificationSettingsCategory(String s, List list)
    {
        mDescription = s;
        if(list != null)
        {
            mSettings = new NotificationSetting[list.size()];
            list.toArray(mSettings);
        } else
        {
            mSettings = new NotificationSetting[0];
        }
    }

    public int describeContents()
    {
        return 0;
    }

    public final String getDescription()
    {
        return mDescription;
    }

    public final NotificationSetting getSetting(int i)
    {
        return mSettings[i];
    }

    public final int getSettingsCount()
    {
        return mSettings.length;
    }

    public String toString()
    {
        return (new StringBuilder("Category: ")).append(mDescription).append(" Settings: ").append(Arrays.asList(mSettings)).toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mDescription);
        parcel.writeTypedArray(mSettings, 0);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new NotificationSettingsCategory(parcel, (byte)0);
        }

        public final Object[] newArray(int i)
        {
            return new NotificationSettingsCategory[i];
        }

    }
;
    private final String mDescription;
    private final NotificationSetting mSettings[];

}
