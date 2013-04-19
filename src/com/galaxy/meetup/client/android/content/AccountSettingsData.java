/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.galaxy.meetup.server.client.domain.MobileSettingsUser;
import com.galaxy.meetup.server.client.domain.MobileSettingsUserInfo;
import com.galaxy.meetup.server.client.domain.ShareboxSettings;
import com.galaxy.meetup.server.client.domain.response.GetMobileSettingsResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class AccountSettingsData implements Parcelable {

	private boolean mIsChild;
    private String mPlusPageIds[];
    private String mPlusPageNames[];
    private String mPlusPagePhotoUrls[];
    private ShareboxSettings mShareboxSettings;
    private String mUserDisplayName;
    private String mUserGaiaId;
    private Long mWarmWelcomeTimestamp;
    
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		public final Object createFromParcel(Parcel parcel) {
			return new AccountSettingsData(parcel);
		}

		public final Object[] newArray(int i) {
			return new AccountSettingsData[i];
		}

	};
	
	private AccountSettingsData(Parcel parcel) {
        mUserGaiaId = parcel.readString();
        mUserDisplayName = parcel.readString();
        boolean flag;
        String s;
        ShareboxSettings shareboxsettings;
        if(parcel.readInt() == 1)
            flag = true;
        else
            flag = false;
        mIsChild = flag;
        s = parcel.readString();
        if(s != null)
            shareboxsettings = (ShareboxSettings)JsonUtil.toBean(s, ShareboxSettings.class);
        else
            shareboxsettings = null;
        mShareboxSettings = shareboxsettings;
        if(parcel.readInt() == 1)
            mWarmWelcomeTimestamp = Long.valueOf(parcel.readLong());
        mPlusPageNames = parcel.createStringArray();
        mPlusPageIds = parcel.createStringArray();
        mPlusPagePhotoUrls = parcel.createStringArray();
    }
    
	public AccountSettingsData(GetMobileSettingsResponse getmobilesettingsresponse)
    {
        if(getmobilesettingsresponse.user != null)
        {
            MobileSettingsUser mobilesettingsuser = getmobilesettingsresponse.user;
            boolean flag;
            if(mobilesettingsuser.isChild != null && mobilesettingsuser.isChild.booleanValue())
                flag = true;
            else
                flag = false;
            mIsChild = flag;
            if(mobilesettingsuser.info != null)
            {
                mUserGaiaId = mobilesettingsuser.info.obfuscatedGaiaId;
                mUserDisplayName = mobilesettingsuser.info.displayName;
            }
            setPlusPages(mobilesettingsuser.plusPageInfo);
        }
        if(getmobilesettingsresponse.preference != null)
            mWarmWelcomeTimestamp = getmobilesettingsresponse.preference.wwMainFlowAckTimestampMsec;
        mShareboxSettings = getmobilesettingsresponse.shareboxSettings;
    }

    private void setPlusPages(List<MobileSettingsUserInfo> list)
    {
        int i;
        if(list != null)
            i = list.size();
        else
            i = 0;
        mPlusPageNames = new String[i];
        mPlusPageIds = new String[i];
        mPlusPagePhotoUrls = new String[i];
        for(int j = 0; j < i; j++)
        {
            MobileSettingsUserInfo mobilesettingsuserinfo = list.get(j);
            mPlusPageNames[j] = mobilesettingsuserinfo.displayName;
            mPlusPageIds[j] = mobilesettingsuserinfo.obfuscatedGaiaId;
            mPlusPagePhotoUrls[j] = mobilesettingsuserinfo.photoUrl;
        }

    }

    public int describeContents()
    {
        return 0;
    }

    public final int getNumPlusPages()
    {
        return mPlusPageIds.length;
    }

    public final String getPlusPageId(int i)
    {
        return mPlusPageIds[i];
    }

    public final String getPlusPageName(int i)
    {
        return mPlusPageNames[i];
    }

    public final String getPlusPagePhotoUrl(int i)
    {
        return mPlusPagePhotoUrls[i];
    }

    public final ShareboxSettings getShareboxSettings()
    {
        return mShareboxSettings;
    }

    public final String getUserDisplayName()
    {
        return mUserDisplayName;
    }

    public final String getUserGaiaId()
    {
        return mUserGaiaId;
    }

    public final String getUserPhotoUrl()
    {
        return null;
    }

    public final Long getWarmWelcomeTimestamp()
    {
        return mWarmWelcomeTimestamp;
    }

    public final boolean isChild()
    {
        return mIsChild;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mUserGaiaId);
        parcel.writeString(mUserDisplayName);
        int j;
        String s;
        if(mIsChild)
            j = 1;
        else
            j = 0;
        parcel.writeInt(j);
        if(mShareboxSettings != null)
            s = mShareboxSettings.toJsonString();
        else
            s = null;
        parcel.writeString(s);
        if(mWarmWelcomeTimestamp != null)
        {
            parcel.writeInt(1);
            parcel.writeLong(mWarmWelcomeTimestamp.longValue());
        } else
        {
            parcel.writeInt(0);
        }
        parcel.writeStringArray(mPlusPageNames);
        parcel.writeStringArray(mPlusPageIds);
        parcel.writeStringArray(mPlusPagePhotoUrls);
    }

}