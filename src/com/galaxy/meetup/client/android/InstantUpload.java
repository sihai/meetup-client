/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.service.AndroidNotification;
import com.galaxy.meetup.client.android.service.CameraMonitor;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class InstantUpload {

	private static final String INSTANT_SHARE_PROJECTION[] = {
        "instant_share_eventid"
    };
    private static final String INSTANT_UPLOAD_PROJECTION[] = {
        "auto_upload_enabled"
    };
    private static final String PROJECTION_UPLOAD_SIZE[] = {
        "upload_full_resolution", "full_size_disabled", "quota_limit", "quota_used"
    };
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    
    public static void cancelAllUploads(Context context, EsAccount esaccount)
    {
        Uri uri = InstantUploadFacade.UPLOAD_ALL_URI.buildUpon().appendQueryParameter("account", esaccount.getName()).build();
        context.getContentResolver().delete(uri, null, null);
    }

    public static void enableInstantUpload(Context context, EsAccount esaccount, boolean flag)
    {
        ContentResolver contentresolver = context.getContentResolver();
        ContentValues contentvalues = new ContentValues();
        if(flag)
        {
            contentvalues.put("auto_upload_account_name", esaccount.getName());
            contentvalues.put("auto_upload_account_type", AccountsUtil.ACCOUNT_TYPE);
        }
        int i;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues.put("auto_upload_enabled", Integer.valueOf(i));
        contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
        if(flag)
            ensureSyncEnabled(esaccount);
        startMonitoring(context);
    }

    public static void ensureSyncEnabled(EsAccount esaccount)
    {
        ContentResolver.setSyncAutomatically(AccountsUtil.newAccount(esaccount.getName()), "com.galaxy.meetup.client.android.iu.EsGalaxyIuProvider", true);
    }

    public static String getInstantShareEventId(Context context)
    {
        Cursor cursor = null;
        String s;
        Uri uri = InstantUploadFacade.SETTINGS_URI;
        try {
        	cursor = context.getContentResolver().query(uri, INSTANT_SHARE_PROJECTION, null, null, null);
        	s = null;
            if(cursor == null || !cursor.moveToFirst()) {
            	return null;
            }
            
            return cursor.getString(0);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static String getSizeText(Context context, int i)
    {
        String s;
        if(i < 900)
        {
            int l = R.string.full_size_megabyte;
            Object aobj2[] = new Object[1];
            aobj2[0] = Integer.valueOf(i);
            s = context.getString(l, aobj2);
        } else
        if(i < 0xe1000)
        {
            int k = R.string.full_size_gigabyte;
            Object aobj1[] = new Object[1];
            aobj1[0] = Double.valueOf(Math.max((double)i / 1024D, 1.0D));
            s = context.getString(k, aobj1);
        } else
        {
            int j = R.string.full_size_terabyte;
            Object aobj[] = new Object[1];
            aobj[0] = Double.valueOf(Math.max((double)i / 1048576D, 1.0D));
            s = context.getString(j, aobj);
        }
        return s;
    }

    public static boolean isEnabled(Context context)
    {
        Cursor cursor = null;
        Uri uri = InstantUploadFacade.SETTINGS_URI;
        try {
        	cursor = context.getContentResolver().query(uri, INSTANT_UPLOAD_PROJECTION, null, null, null);
        	if(null != cursor && cursor.moveToFirst()) {
        		return cursor.getInt(0) != 0;
        	}
        	
        	return false;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static boolean isInstantShareEnabled(Context context)
    {
        boolean flag;
        if(getInstantShareEventId(context) != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static boolean isSyncEnabled(EsAccount esaccount)
    {
        return ContentResolver.getSyncAutomatically(AccountsUtil.newAccount(esaccount.getName()), "com.galaxy.meetup.client.android.iu.EsGalaxyIuProvider");
    }

    public static void setFullResolutionSetting(Context context, boolean flag)
    {
        ContentResolver contentresolver = context.getContentResolver();
        int i;
        ContentValues contentvalues;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues = new ContentValues();
        contentvalues.put("upload_full_resolution", Integer.valueOf(i));
        contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
    }

    public static void setOnBatterySetting(Context context, boolean flag)
    {
        ContentResolver contentresolver = context.getContentResolver();
        int i;
        ContentValues contentvalues;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues = new ContentValues();
        contentvalues.put("sync_on_battery", Integer.valueOf(i));
        contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
    }

    public static void setPhotoWiFiOnlySetting(Context context, boolean flag)
    {
        ContentResolver contentresolver = context.getContentResolver();
        int i;
        ContentValues contentvalues;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues = new ContentValues();
        contentvalues.put("sync_on_wifi_only", Integer.valueOf(i));
        contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
    }

    public static void setRoamingUploadSetting(Context context, boolean flag)
    {
        ContentResolver contentresolver = context.getContentResolver();
        int i;
        ContentValues contentvalues;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues = new ContentValues();
        contentvalues.put("sync_on_roaming", Integer.valueOf(i));
        contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
    }

    public static void setVideoWiFiOnlySetting(Context context, boolean flag)
    {
        ContentResolver contentresolver = context.getContentResolver();
        int i;
        ContentValues contentvalues;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues = new ContentValues();
        contentvalues.put("video_upload_wifi_only", Integer.valueOf(i));
        contentresolver.update(InstantUploadFacade.SETTINGS_URI, contentvalues, null, null);
    }

    public static void showFirstTimeFullSizeNotification(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedpreferences.getBoolean("iu.first_time_full_size_shown", false) || null == esaccount) {
        	if(EsLog.isLoggable("iu.InstantUpload", 4))
                if(esaccount == null)
                    Log.i("iu.InstantUpload", "No first time; account is null, retry");
                else
                    Log.i("iu.InstantUpload", "First time already shown");
        	return;
        }
        
        // TODO
    }

    public static void showOutOfQuotaNotification(Context context, EsAccount esaccount, int i, int j, boolean flag)
    {
        boolean flag1 = InstantUploadFacade.isOutOfQuota(i, j);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!flag) {
        	sharedpreferences.edit().putBoolean("iu.received_low_quota", false).putBoolean("iu.received_no_quota", false).commit();
            showFirstTimeFullSizeNotification(context, esaccount);
            AndroidNotification.cancelQuotaNotification(context, esaccount);
            return;
        }
        
        // TODO
    }

    public static void startMonitoring(final Context context) {
        sHandler.post(new Runnable() {

            public final void run()
            {
                PackageManager packagemanager = context.getPackageManager();
                ComponentName componentname = new ComponentName(context.getPackageName(), CameraMonitor.class.getName());
                if(packagemanager.getComponentEnabledSetting(componentname) != 1)
                    packagemanager.setComponentEnabledSetting(componentname, 1, 1);
                CameraMonitor.registerObservers(context.getApplicationContext());
            }
        });
    }
}
