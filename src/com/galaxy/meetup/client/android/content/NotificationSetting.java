/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationSetting implements Parcelable {

	private final DataNotificationSettingsDeliveryOption mSetting;
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new NotificationSetting(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new NotificationSetting[i];
        }

    };
    
    private NotificationSetting(Parcel parcel)
    {
        mSetting = new DataNotificationSettingsDeliveryOption();
        mSetting.bucketId = parcel.readString();
        mSetting.offnetworkBucketId = parcel.readString();
        mSetting.category = parcel.readString();
        mSetting.description = parcel.readString();
        mSetting.enabledForEmail = readBoolean(parcel);
        mSetting.enabledForPhone = readBoolean(parcel);
    }

    public NotificationSetting(NotificationSetting notificationsetting)
    {
        mSetting = new DataNotificationSettingsDeliveryOption();
        mSetting.bucketId = notificationsetting.mSetting.bucketId;
        mSetting.offnetworkBucketId = notificationsetting.mSetting.offnetworkBucketId;
        mSetting.category = notificationsetting.mSetting.category;
        mSetting.description = notificationsetting.mSetting.description;
        mSetting.enabledForEmail = notificationsetting.mSetting.enabledForEmail;
        mSetting.enabledForPhone = notificationsetting.mSetting.enabledForPhone;
    }

    public NotificationSetting(DataNotificationSettingsDeliveryOption datanotificationsettingsdeliveryoption)
    {
        mSetting = datanotificationsettingsdeliveryoption;
    }

    private static Boolean readBoolean(Parcel parcel)
    {
        int i = parcel.readInt();
        Boolean boolean1;
        if(i == 1)
            boolean1 = Boolean.TRUE;
        else
        if(i == 0)
            boolean1 = Boolean.FALSE;
        else
            boolean1 = null;
        return boolean1;
    }

    private static void writeBoolean(Parcel parcel, Boolean boolean1)
    {
        byte byte0;
        if(boolean1 == null)
            byte0 = -1;
        else
        if(boolean1.booleanValue())
            byte0 = 1;
        else
            byte0 = 0;
        parcel.writeInt(byte0);
    }

    public int describeContents()
    {
        return 0;
    }

    public final DataNotificationSettingsDeliveryOption getDeliveryOption()
    {
        return mSetting;
    }

    public final String getDescription()
    {
        return mSetting.description;
    }

    public final boolean isEnabled()
    {
        boolean flag;
        if(mSetting.enabledForPhone != null && mSetting.enabledForPhone.booleanValue())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void setEnabled(boolean flag)
    {
        mSetting.enabledForPhone = Boolean.valueOf(flag);
    }

    public String toString()
    {
        return (new StringBuilder("{Setting ")).append(mSetting.description).append(" enabled=").append(mSetting.enabledForPhone).append(" id=").append(mSetting.bucketId).append(" offNetId=").append(mSetting.offnetworkBucketId).append("}").toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mSetting.bucketId);
        parcel.writeString(mSetting.offnetworkBucketId);
        parcel.writeString(mSetting.category);
        parcel.writeString(mSetting.description);
        writeBoolean(parcel, mSetting.enabledForEmail);
        writeBoolean(parcel, mSetting.enabledForPhone);
    }
}
