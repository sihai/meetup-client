/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import android.content.Context;
import android.database.Cursor;

import com.android.gallery3d.common.Utils;

/**
 * 
 * @author sihai
 *
 */
public class UploadSettings {

	private static final String PROJECTION_ENABLE_ACCOUNT_WIFI[] = {
        "auto_upload_enabled", "auto_upload_account_name", "sync_on_wifi_only", "video_upload_wifi_only", "sync_on_roaming", "sync_on_battery", "instant_share_eventid", "instant_share_starttime", "instant_share_endtime", "upload_full_resolution"
    };
    private static UploadSettings sInstance;
    private boolean mAutoUploadEnabled;
    private final Context mContext;
    private long mEventEndTime;
    private String mEventId;
    private long mEventStartTime;
    private boolean mSettingsValid;
    private String mSyncAccount;
    private boolean mSyncOnBattery;
    private boolean mSyncOnRoaming;
    private boolean mUploadFullRes;
    private boolean mWifiOnlyPhoto;
    private boolean mWifiOnlyVideo;
    
    private UploadSettings(Context context)
    {
        mContext = context;
    }

    public static synchronized UploadSettings getInstance(Context context)
    {
        UploadSettings uploadsettings1;
        if(sInstance == null)
        {
            UploadSettings uploadsettings = new UploadSettings(context);
            sInstance = uploadsettings;
            uploadsettings.reloadSettings(null);
        }
        uploadsettings1 = sInstance;
        return uploadsettings1;
    }
    
    public final boolean getAutoUploadEnabled()
    {
        return mAutoUploadEnabled;
    }

    public final long getEventEndTime()
    {
        return mEventEndTime;
    }

    public final String getEventId()
    {
        return mEventId;
    }

    public final long getEventStartTime()
    {
        return mEventStartTime;
    }

    public final String getSyncAccount()
    {
        return mSyncAccount;
    }

    public final boolean getSyncOnBattery()
    {
        return mSyncOnBattery;
    }

    public final boolean getSyncOnRoaming()
    {
        return mSyncOnRoaming;
    }

    final Cursor getSystemSettingsCursor()
    {
        return mContext.getContentResolver().query(InstantUploadFacade.SETTINGS_URI, PROJECTION_ENABLE_ACCOUNT_WIFI, null, null, null);
    }

    public final boolean getUploadFullRes()
    {
        return mUploadFullRes;
    }

    public final boolean getWifiOnlyPhoto()
    {
        return mWifiOnlyPhoto;
    }

    public final boolean getWifiOnlyVideo()
    {
        return mWifiOnlyVideo;
    }

    public final void reloadSettings()
    {
        reloadSettings(null);
    }

    final void reloadSettings(Cursor cursor)
    {
    	// TODO
    	Utils.closeSilently(cursor);
    }
}
