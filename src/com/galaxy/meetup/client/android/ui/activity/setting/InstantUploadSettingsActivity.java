/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import java.util.Map;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.util.AccountsUtil;

/**
 * 
 * @author sihai
 *
 */
public class InstantUploadSettingsActivity extends BaseSettingsActivity implements OnClickListener {

	private static final Uri BUY_QUTOA_URI = Uri.parse("https://www.google.com/settings/storage/");
    private static final Uri LEARN_MORE_URI = Uri.parse("https://support.google.com/plus/?p=full_size_upload");
    private static final String PROJECTION_PICASA_SETTINGS[] = {
        "auto_upload_enabled", "sync_on_wifi_only", "sync_on_roaming", "sync_on_battery", "video_upload_wifi_only", "upload_full_resolution", "quota_limit", "quota_used"
    };
    private static String sBuyQuotaKey;
    private static String sConnectionPhotoKey;
    private static String sConnectionVideoKey;
    private static String sInstantUploadKey;
    private static String sLearnMoreKey;
    private static IntentFilter sMatchFilter;
    private static String sOnBatteryKey;
    private static String sRoamingUploadKey;
    private static String sSyncNowKey;
    private static String sUploadSizeKey;
    private static boolean sWifiOnly;
    private boolean mIsReceiverRegistered;
    private boolean mIsUploading;
    private boolean mMasterSyncEnabled;
    private boolean mPhotoSyncEnabled;
    private int mQuotaLimit;
    private int mQuotaUsed;
    private AsyncTaskLoader mSettingsLoader;
    private final BroadcastReceiver mUploadsProgressReceiver = new BroadcastReceiver() {

        public final void onReceive(Context context, Intent intent)
        {
            if(intent != null && "iu.upload_all_progress".equals(intent.getAction()))
            {
                final int progress = intent.getIntExtra("upload_all_progress", -1);
                final int total = intent.getIntExtra("upload_all_count", -1);
                final int state = intent.getIntExtra("upload_all_state", -1);
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        InstantUploadSettingsActivity instantuploadsettingsactivity = InstantUploadSettingsActivity.this;
                        boolean flag;
                        String s;
                        String s1;
                        if(total != progress)
                            flag = true;
                        else
                            flag = false;
                        instantuploadsettingsactivity.mIsUploading = flag;
                        if(!mIsUploading)
                        {
                            s = getString(R.string.photo_sync_preference_title);
                            s1 = getString(R.string.photo_sync_preference_summary);
                            Toast.makeText(instantuploadsettingsactivity, R.string.photo_upload_finished, 0).show();
                            unregisterReceiver(mUploadsProgressReceiver);
                        } else
                        {
                            s = getString(R.string.photo_sync_preference_cancel_title);
                            if(state == 0 || state == 1)
                            {
                                int i = R.string.photo_upload_now_inprogress_summary;
                                Object aobj[] = new Object[2];
                                aobj[0] = Integer.valueOf(progress);
                                aobj[1] = Integer.valueOf(total);
                                s1 = instantuploadsettingsactivity.getString(i, aobj);
                            } else
                            {
                                String s2 = InstantUploadSettingsActivity.access$200(instantuploadsettingsactivity, state);
                                s1 = getString(R.string.photo_upload_now_paused_summary, new Object[] {
                                    s2
                                });
                            }
                        }
                        Preference preference = findPreference(InstantUploadSettingsActivity.sSyncNowKey);
                        preference.setTitle(s);
                        preference.setSummary(s1);
                    }
                });
            }
        }
    };
    
    public InstantUploadSettingsActivity()
    {
        mQuotaLimit = -1;
        mQuotaUsed = -1;
    }

    private void recordUserAction(OzActions ozactions)
    {
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            EsAnalytics.recordActionEvent(this, esaccount, ozactions, OzViews.getViewForLogging(this));
    }

    private void updateEnabledStates(boolean flag)
    {
        boolean flag1 = true;
        boolean flag2 = InstantUploadFacade.isOutOfQuota(mQuotaLimit, mQuotaUsed);
        Preference preference = findPreference(sConnectionPhotoKey);
        boolean flag3;
        Preference preference1;
        boolean flag4;
        Preference preference2;
        boolean flag5;
        Preference preference3;
        boolean flag6;
        Preference preference4;
        boolean flag7;
        Preference preference5;
        boolean flag8;
        Preference preference6;
        if(!flag || !sWifiOnly)
            flag3 = flag1;
        else
            flag3 = false;
        preference.setShouldDisableView(flag3);
        preference1 = findPreference(sConnectionPhotoKey);
        if(flag && !sWifiOnly)
            flag4 = flag1;
        else
            flag4 = false;
        preference1.setEnabled(flag4);
        preference2 = findPreference(sConnectionVideoKey);
        if(!flag || !sWifiOnly)
            flag5 = flag1;
        else
            flag5 = false;
        preference2.setShouldDisableView(flag5);
        preference3 = findPreference(sConnectionVideoKey);
        if(flag && !sWifiOnly)
            flag6 = flag1;
        else
            flag6 = false;
        preference3.setEnabled(flag6);
        preference4 = findPreference(sUploadSizeKey);
        if(flag && !flag2)
            flag7 = flag1;
        else
            flag7 = false;
        preference4.setEnabled(flag7);
        findPreference(sBuyQuotaKey).setEnabled(flag);
        findPreference(sLearnMoreKey).setEnabled(flag1);
        preference5 = findPreference(sRoamingUploadKey);
        if(!flag || !sWifiOnly)
            flag8 = flag1;
        else
            flag8 = false;
        preference5.setShouldDisableView(flag8);
        preference6 = findPreference(sRoamingUploadKey);
        if(!flag || sWifiOnly)
            flag1 = false;
        preference6.setEnabled(flag1);
        findPreference(sOnBatteryKey).setEnabled(flag);
        findPreference(sSyncNowKey).setEnabled(flag);
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	
    	if(-1 == i) {
    		mIsUploading = true;
            recordUserAction(OzActions.CS_SETTINGS_SYNC_ALL);
            Preference preference = findPreference(sSyncNowKey);
            preference.setTitle(R.string.photo_sync_preference_cancel_title);
            preference.setSummary(R.string.photo_upload_starting_summary);
            AsyncTask asynctask = new AsyncTask() {

                protected final Object doInBackground(Object aobj[])
                {
                    Uri uri = InstantUploadFacade.UPLOAD_ALL_URI;
                    ContentValues contentvalues = new ContentValues();
                    contentvalues.put("account", getAccount().getName());
                    getContentResolver().insert(uri, contentvalues);
                    return null;
                }

            };
            registerReceiver(mUploadsProgressReceiver, sMatchFilter);
            asynctask.execute(new Void[] {
                null
            });
    	}
    	
    	dismissDialog(0);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(sInstantUploadKey == null)
        {
            sInstantUploadKey = getString(R.string.photo_instant_upload_preference_key);
            sRoamingUploadKey = getString(R.string.photo_roaming_upload_preference_key);
            sOnBatteryKey = getString(R.string.photo_on_battery_preference_key);
            sConnectionPhotoKey = getString(R.string.photo_connection_preference_key);
            sConnectionVideoKey = getString(R.string.video_connection_preference_key);
            sSyncNowKey = getString(R.string.photo_sync_preference_key);
            sUploadSizeKey = getString(R.string.photo_upload_size_preference_key);
            sBuyQuotaKey = getString(R.string.photo_buy_quota_preference_key);
            sLearnMoreKey = getString(R.string.photo_learn_more_preference_key);
            IntentFilter intentfilter = new IntentFilter();
            sMatchFilter = intentfilter;
            intentfilter.addAction("iu.upload_all_progress");
            android.net.NetworkInfo networkinfo = ((ConnectivityManager)getSystemService("connectivity")).getNetworkInfo(0);
            boolean flag = false;
            if(networkinfo == null)
                flag = true;
            sWifiOnly = flag;
        }
        addPreferencesFromResource(R.xml.photo_preferences);
        PhotoPreferenceChangeListener photopreferencechangelistener = new PhotoPreferenceChangeListener();
        Preference preference = findPreference(sInstantUploadKey);
        preference.setOnPreferenceChangeListener(photopreferencechangelistener);
        hookMasterSwitch(null, (CheckBoxPreference)preference);
        findPreference(sConnectionPhotoKey).setOnPreferenceChangeListener(photopreferencechangelistener);
        findPreference(sConnectionVideoKey).setOnPreferenceChangeListener(photopreferencechangelistener);
        findPreference(sRoamingUploadKey).setOnPreferenceChangeListener(photopreferencechangelistener);
        findPreference(sOnBatteryKey).setOnPreferenceChangeListener(photopreferencechangelistener);
        findPreference(sUploadSizeKey).setOnPreferenceChangeListener(photopreferencechangelistener);
        findPreference(sBuyQuotaKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference1)
            {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(InstantUploadSettingsActivity.BUY_QUTOA_URI);
                startActivity(intent);
                return true;
            }

        });
        findPreference(sLearnMoreKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference1)
            {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(InstantUploadSettingsActivity.LEARN_MORE_URI);
                startActivity(intent);
                return true;
            }
        });
        findPreference(sSyncNowKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference1)
            {
                if(!mIsUploading)
                {
                    showDialog(0);
                } else
                {
                    mIsUploading = false;
                    unregisterReceiver(mUploadsProgressReceiver);
                    getContentResolver().delete(InstantUploadSettingsActivity.access$600(InstantUploadSettingsActivity.this), null, null);
                    preference1.setTitle(getString(R.string.photo_sync_preference_title));
                    preference1.setSummary(getString(R.string.photo_sync_preference_summary));
                }
                return true;
            }

        });
        updateEnabledStates(((CheckBoxPreference)preference).isChecked());
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
        android.app.AlertDialog alertdialog;
        if(i == 0)
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.photo_upload_confirmation);
            builder.setPositiveButton(R.string.yes, this);
            builder.setNegativeButton(R.string.no, this);
            alertdialog = builder.create();
        } else
        {
            alertdialog = null;
        }
        return alertdialog;
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if(mSettingsLoader != null)
            mSettingsLoader.reset();
    }

    public void onPause()
    {
        super.onPause();
        if(mSettingsLoader != null)
            mSettingsLoader.stopLoading();
        unregisterReceiver(mUploadsProgressReceiver);
    }

    public void onResume()
    {
        super.onResume();
        mPhotoSyncEnabled = ContentResolver.getSyncAutomatically(AccountsUtil.newAccount(getAccount().getName()), "iu.EsGoogleIuProvider");
        mMasterSyncEnabled = ContentResolver.getMasterSyncAutomatically();
        if(mMasterSyncEnabled && mPhotoSyncEnabled)
        {
            if(mSettingsLoader == null)
                mSettingsLoader = new SystemSettingLoader(this);
            mSettingsLoader.startLoading();
        } else
        {
            finish();
        }
    }

    public Intent registerReceiver(BroadcastReceiver broadcastreceiver, IntentFilter intentfilter)
    {
        boolean flag = mIsReceiverRegistered;
        Intent intent = null;
        if(!flag)
        {
            intent = super.registerReceiver(broadcastreceiver, intentfilter);
            mIsReceiverRegistered = true;
        }
        return intent;
    }

    public void unregisterReceiver(BroadcastReceiver broadcastreceiver)
    {
        if(mIsReceiverRegistered)
        {
            super.unregisterReceiver(broadcastreceiver);
            mIsReceiverRegistered = false;
        }
    }
    
    
    //============================================================================================================
    //
    //============================================================================================================
    
    static void access$2200(InstantUploadSettingsActivity instantuploadsettingsactivity, Map map)
    {
        if(!(map != null && instantuploadsettingsactivity.mMasterSyncEnabled && instantuploadsettingsactivity.mPhotoSyncEnabled)) {
        	return;
        }
        	
        int i1;
        String s4;
        String s6;
        String s7;
        CheckBoxPreference checkboxpreference = (CheckBoxPreference)instantuploadsettingsactivity.findPreference(sInstantUploadKey);
        Integer integer = (Integer)map.get("auto_upload_enabled");
        boolean flag;
        boolean flag2;
        boolean flag3;
        int i;
        String s;
        int j;
        int k;
        String s1;
        int l;
        boolean flag4;
        CheckBoxPreference checkboxpreference1;
        CheckBoxPreference checkboxpreference2;
        ListPreference listpreference;
        ListPreference listpreference1;
        String s2;
        String s3;
        EsListPreference eslistpreference;
        String s9;
        boolean flag5;
        if(integer.intValue() == 1)
            flag = true;
        else
            flag = false;
        if(checkboxpreference == null)
        {
            Switch switch1 = instantuploadsettingsactivity.getMasterSwitch();
            if(integer.intValue() == 1)
                flag5 = true;
            else
                flag5 = false;
            switch1.setChecked(flag5);
        } else
        {
            boolean flag1;
            if(integer.intValue() == 1)
                flag1 = true;
            else
                flag1 = false;
            checkboxpreference.setChecked(flag1);
        }
        checkboxpreference1 = (CheckBoxPreference)instantuploadsettingsactivity.findPreference(sRoamingUploadKey);
        if(((Integer)map.get("sync_on_roaming")).intValue() == 1)
            flag2 = true;
        else
            flag2 = false;
        checkboxpreference1.setChecked(flag2);
        checkboxpreference2 = (CheckBoxPreference)instantuploadsettingsactivity.findPreference(sOnBatteryKey);
        if(((Integer)map.get("sync_on_battery")).intValue() != 1)
            flag3 = true;
        else
            flag3 = false;
        checkboxpreference2.setChecked(flag3);
        listpreference = (ListPreference)instantuploadsettingsactivity.findPreference(sConnectionPhotoKey);
        if(sWifiOnly)
            i = 1;
        else
            i = ((Integer)map.get("sync_on_wifi_only")).intValue();
        if(Integer.valueOf(i).intValue() == 1)
        {
            s = "WIFI_ONLY";
            j = R.string.photo_connection_preference_summary_wifi;
        } else
        {
            s = "WIFI_OR_MOBILE";
            j = R.string.photo_connection_preference_summary_mobile;
        }
        listpreference.setSummary(j);
        listpreference.setValue(s);
        listpreference1 = (ListPreference)instantuploadsettingsactivity.findPreference(sConnectionVideoKey);
        if(sWifiOnly)
            k = 1;
        else
            k = ((Integer)map.get("video_upload_wifi_only")).intValue();
        if(Integer.valueOf(k).intValue() == 1)
        {
            s1 = "WIFI_ONLY";
            l = R.string.video_connection_preference_summary_wifi;
        } else
        {
            s1 = "WIFI_OR_MOBILE";
            l = R.string.video_connection_preference_summary_mobile;
        }
        listpreference1.setSummary(l);
        listpreference1.setValue(s1);
        i1 = ((Integer)map.get("upload_full_resolution")).intValue();
        instantuploadsettingsactivity.mQuotaLimit = ((Integer)map.get("quota_limit")).intValue();
        instantuploadsettingsactivity.mQuotaUsed = ((Integer)map.get("quota_used")).intValue();
        if(instantuploadsettingsactivity.mQuotaLimit != -1 && instantuploadsettingsactivity.mQuotaUsed != -1)
            flag4 = true;
        else
            flag4 = false;
        s2 = InstantUpload.getSizeText(instantuploadsettingsactivity, instantuploadsettingsactivity.mQuotaLimit);
        s3 = InstantUpload.getSizeText(instantuploadsettingsactivity, instantuploadsettingsactivity.mQuotaLimit - instantuploadsettingsactivity.mQuotaUsed);
        if(flag4)
            s4 = instantuploadsettingsactivity.getString(R.string.photo_upload_size_quota_available, new Object[] {
                s3, s2
            });
        else
            s4 = instantuploadsettingsactivity.getString(R.string.photo_upload_size_quota_unknown);
        if(InstantUploadFacade.isOutOfQuota(instantuploadsettingsactivity.mQuotaLimit, instantuploadsettingsactivity.mQuotaUsed)) {
        	 s9 = instantuploadsettingsactivity.getString(R.string.photo_upload_size_preference_summary_overquota);
             s6 = s1;
             s7 = s9;
        } else {
        	if(i1 == 1)
            {
                String s8 = instantuploadsettingsactivity.getString(R.string.photo_upload_size_preference_summary_full, new Object[] {
                    s4
                });
                s6 = "FULL";
                s7 = s8;
            } else
            {
                String s5 = instantuploadsettingsactivity.getString(R.string.photo_upload_size_preference_summary_standard);
                s6 = "STANDARD";
                s7 = s5;
            }
        }
           
        eslistpreference = (EsListPreference)instantuploadsettingsactivity.findPreference(sUploadSizeKey);
        eslistpreference.setSummary(s7);
        eslistpreference.setValue(s6);
        eslistpreference.setEntrySummaryArgument(s4);
        instantuploadsettingsactivity.updateEnabledStates(flag);
       
    }
    
    static String access$200(InstantUploadSettingsActivity instantuploadsettingsactivity, int i) {
    	
    	 String s = null;
    	switch(i) {
	    	case 0:
	    	case 1:
	    		s = null;
	    		break;
	    	case 2:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_no_wifi);
	    		break;
	    	case 3:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_roaming);
	    		break;
	    	case 4:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_no_power);
	    		break;
	    	case 8:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_no_background_data);
	    		break;
	    	case 9:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_quota);
	    		break;
	    	case 5:
	    	case 10:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_user_auth);
	    		break;
	    	case 11:
	    	case 12:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_no_sdcard);
	    		break;
	    	case 6:
	    	case 7:
	    	default:
	    		s = instantuploadsettingsactivity.getString(R.string.photo_upload_now_status_unknown);
	    		break;
    	}
    	
    	return s;
    }
    
    static Uri access$600(InstantUploadSettingsActivity instantuploadsettingsactivity)
    {
        EsAccount esaccount = instantuploadsettingsactivity.getAccount();
        android.net.Uri.Builder builder = InstantUploadFacade.UPLOAD_ALL_URI.buildUpon();
        builder.appendQueryParameter("account", esaccount.getName());
        return builder.build();
    }
    
    final class PhotoPreferenceChangeListener implements android.preference.Preference.OnPreferenceChangeListener {

	    public final boolean onPreferenceChange(final Preference preference, final Object obj)
	    {
	    	final EsAccount account = getAccount();
	        String s = preference.getKey();
	        if(TextUtils.equals(InstantUploadSettingsActivity.sInstantUploadKey, s)) {
	        	
	            final Boolean boolean5 = (Boolean)obj;
	            updateEnabledStates(boolean5.booleanValue());
	            OzActions ozactions1;
	            if(boolean5.booleanValue())
	                ozactions1 = OzActions.CS_SETTINGS_OPTED_IN;
	            else
	                ozactions1 = OzActions.CS_SETTINGS_OPTED_OUT;
	            InstantUploadSettingsActivity.this.recordUserAction(ozactions1);
	            (new AsyncTask() {
	
	                protected final Object doInBackground(Object aobj[])
	                {
	                    EsAccountsData.saveInstantUploadEnabled(InstantUploadSettingsActivity.this, account, boolean5.booleanValue());
	                    InstantUpload.enableInstantUpload(InstantUploadSettingsActivity.this, account, boolean5.booleanValue());
	                    return null;
	                }
	
	            }).execute((Object[])null);
	        } else if(TextUtils.equals(InstantUploadSettingsActivity.sRoamingUploadKey, s)) {
	        	final Boolean boolean4 = (Boolean)obj;
	            OzActions ozactions;
	            if(boolean4.booleanValue())
	                ozactions = OzActions.CS_SETTINGS_ROAMING_ENABLED;
	            else
	                ozactions = OzActions.CS_SETTINGS_ROAMING_DISABLED;
	            recordUserAction(ozactions);
	            (new AsyncTask() {
	            	
	                protected final Object doInBackground(Object aobj[])
	                {
	                    InstantUpload.setRoamingUploadSetting(InstantUploadSettingsActivity.this, boolean4.booleanValue());
	                    return null;
	                }
	            }).execute((Object[])null);
	        } else if(TextUtils.equals(InstantUploadSettingsActivity.sOnBatteryKey, s)) {
	        	final Boolean boolValue = (Boolean)obj;
	            (new AsyncTask() {
	
	                protected final Object doInBackground(Object aobj[])
	                {
	                    InstantUploadSettingsActivity instantuploadsettingsactivity = InstantUploadSettingsActivity.this;
	                    boolean flag;
	                    if(!boolValue.booleanValue())
	                        flag = true;
	                    else
	                        flag = false;
	                    InstantUpload.setOnBatterySetting(instantuploadsettingsactivity, flag);
	                    return null;
	                }
	            }).execute((Object[])null);
	        } else if(TextUtils.equals(InstantUploadSettingsActivity.sConnectionPhotoKey, s)) {
	            String s6 = (String)obj;
	            Boolean boolean3;
	            if(TextUtils.equals(s6, "WIFI_ONLY"))
	            {
	                recordUserAction(OzActions.CS_SETTINGS_UPLOAD_VIA_PHOTOS_AND_VIDEOS_VIA_WIFI_ONLY);
	                preference.setSummary(R.string.photo_connection_preference_summary_wifi);
	                boolean3 = Boolean.valueOf(true);
	            } else
	            if(TextUtils.equals(s6, "WIFI_OR_MOBILE"))
	            {
	                recordUserAction(OzActions.CS_SETTINGS_UPLOAD_VIA_PHOTOS_AND_VIDEOS_VIA_MOBILE);
	                preference.setSummary(R.string.photo_connection_preference_summary_mobile);
	                boolean3 = Boolean.valueOf(false);
	            } else
	            {
	                boolean3 = null;
	            }
	            if(boolean3 != null) {
	            	final Boolean wifiOnly = boolean3;
	                (new AsyncTask() {
	
	                    protected final Object doInBackground(Object aobj[])
	                    {
	                        EsAccountsData.saveInstantUploadPhotoWifiOnly(InstantUploadSettingsActivity.this, account, wifiOnly.booleanValue());
	                        InstantUpload.setPhotoWiFiOnlySetting(InstantUploadSettingsActivity.this, wifiOnly.booleanValue());
	                        return null;
	                    }
	
	                }).execute((Object[])null);
	            }
	        } else if(TextUtils.equals(InstantUploadSettingsActivity.sConnectionVideoKey, s)) {
	            String s5 = (String)obj;
	            Boolean boolean2;
	            if(TextUtils.equals(s5, "WIFI_ONLY"))
	            {
	                recordUserAction(OzActions.CS_SETTINGS_UPLOAD_VIA_VIDEOS_VIA_WIFI_ONLY);
	                preference.setSummary(R.string.video_connection_preference_summary_wifi);
	                boolean2 = Boolean.valueOf(true);
	            } else
	            if(TextUtils.equals(s5, "WIFI_OR_MOBILE"))
	            {
	                recordUserAction(OzActions.CS_SETTINGS_UPLOAD_VIA_VIDEOS_VIA_MOBILE);
	                preference.setSummary(R.string.video_connection_preference_summary_mobile);
	                boolean2 = Boolean.valueOf(false);
	            } else
	            {
	                boolean2 = null;
	            }
	            if(boolean2 != null) {
	            	final Boolean wifiOnly = boolean2;
	                (new AsyncTask() {
	
	                    protected final Object doInBackground(Object aobj[])
	                    {
	                        EsAccountsData.saveInstantUploadVideoWifiOnly(InstantUploadSettingsActivity.this, account, wifiOnly.booleanValue());
	                        InstantUpload.setVideoWiFiOnlySetting(InstantUploadSettingsActivity.this, wifiOnly.booleanValue());
	                        return null;
	                    }
	                }).execute((Object[])null);
	            }
	        } else if(TextUtils.equals(InstantUploadSettingsActivity.sUploadSizeKey, s)) {
	            String s1 = (String)obj;
	            boolean flag;
	            String s2;
	            String s3;
	            String s4;
	            Boolean boolean1;
	            if(mQuotaLimit != -1 && mQuotaUsed != -1)
	                flag = true;
	            else
	                flag = false;
	            s2 = InstantUpload.getSizeText(InstantUploadSettingsActivity.this, mQuotaLimit);
	            s3 = InstantUpload.getSizeText(InstantUploadSettingsActivity.this, mQuotaLimit - mQuotaUsed);
	            if(flag)
	                s4 = getString(R.string.photo_upload_size_quota_available, new Object[] {
	                    s3, s2
	                });
	            else
	                s4 = getString(R.string.photo_upload_size_quota_unknown);
	            if(TextUtils.equals(s1, "FULL"))
	            {
	                recordUserAction(OzActions.ENABLE_FULL_SIZE_PHOTO_UPLOAD);
	                preference.setSummary(getString(R.string.photo_upload_size_preference_summary_full, new Object[] {
	                    s4
	                }));
	                boolean1 = Boolean.valueOf(true);
	            } else
	            {
	                recordUserAction(OzActions.ENABLE_STANDARD_SIZE_PHOTO_UPLOAD);
	                preference.setSummary(R.string.photo_upload_size_preference_summary_standard);
	                boolean1 = Boolean.valueOf(false);
	            }
	            final Boolean fullResolution = boolean1;
	            (new AsyncTask() {
	
	                protected final Object doInBackground(Object aobj[])
	                {
	                    InstantUpload.setFullResolutionSetting(InstantUploadSettingsActivity.this, fullResolution.booleanValue());
	                    return null;
	                }
	
	            }).execute((Object[])null);
	        }
			return true;
	    }
	}
    
    
    static final class SystemSettingLoader extends AsyncTaskLoader {
    	
    	private final InstantUploadSettingsActivity mActivity;
        private boolean mLoaderException;
        private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
        private boolean mObserverRegistered;

        public SystemSettingLoader(InstantUploadSettingsActivity instantuploadsettingsactivity)
        {
            super(instantuploadsettingsactivity);
            mActivity = instantuploadsettingsactivity;
        }
        
        private Map esLoadInBackground()
        {
        	// TODO
            return null;
        }

        public Map loadInBackground() {
            if(mLoaderException) 
            	return null; 
            
            try {
            	return esLoadInBackground();
            } catch(SQLiteException sqliteexception) {
            	Log.w("EsAsyncTaskLoader", "loadInBackground failed", sqliteexception);
                mLoaderException = true;
                return null;
            }
        }

        public final void deliverResult(Object obj)
        {
            Map map = (Map)obj;
            if(!mLoaderException && isStarted())
            {
                super.deliverResult(map);
                InstantUploadSettingsActivity.access$2200(mActivity, map);
            }
        }

        protected final void onAbandon()
        {
            if(mObserverRegistered)
            {
                getContext().getContentResolver().unregisterContentObserver(mObserver);
                mObserverRegistered = false;
            }
        }

        protected final void onReset()
        {
            onAbandon();
        }

        protected final void onStartLoading()
        {
            if(!mObserverRegistered)
            {
                getContext().getContentResolver().registerContentObserver(InstantUploadFacade.SETTINGS_URI, true, mObserver);
                mObserverRegistered = true;
            }
            forceLoad();
        }
    }
}
