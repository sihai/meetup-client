/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.api.GetMobileExperimentsOperation;
import com.galaxy.meetup.client.android.api.GetSettingsOperation;
import com.galaxy.meetup.client.android.api.SetSettingsOperation;
import com.galaxy.meetup.client.android.hangout.GCommApp;
import com.galaxy.meetup.client.android.iu.InstantUploadSyncService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.service.AndroidContactsSync;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.SkyjamPlaybackService;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.GetMobileExperimentsResponseExperiment;
import com.galaxy.meetup.server.client.domain.GetMobileExperimentsResponseExperimentValue;
import com.galaxy.meetup.server.client.domain.ShareboxSettings;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EsAccountsData {

	private static List sExperimentListeners = new ArrayList();
    private static Map sExperiments = new HashMap();
    private static boolean sHadSharingRoster;
    
    public static void registerExperimentListener(ExperimentListener experimentlistener)
    {
        sExperimentListeners.add(experimentlistener);
    }
    
    public static void unregisterExperimentListener(ExperimentListener experimentlistener)
    {
        sExperimentListeners.remove(experimentlistener);
    }
    
    public static synchronized EsAccount getActiveAccountUnsafe(Context context)
    {
        SharedPreferences sharedpreferences;
        sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = sharedpreferences.getInt("active_account", -1);
        if(-1 != i) {
        	return getAccount(context, i, true);
        }
        i = sharedpreferences.getInt("active", -1);
        if(-1 != i) {
        	return getAccount(context, i, true);
        }
        return null;
    }
    
    public static boolean isAccountUpgradeRequired(Context context, EsAccount esaccount)
    {
        int i = esaccount.getIndex();
        if(-1 == i) {
        	return false;
        }
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        boolean flag1 = sharedpreferences.contains((new StringBuilder()).append(i).append(".gaia_id").toString());
        boolean flag = false;
        if(!flag1)
        {
            boolean flag2 = sharedpreferences.contains((new StringBuilder()).append(i).append(".user_id").toString());
            flag = false;
            if(flag2)
                flag = true;
        }
        return flag;
    }
    
    public static boolean isContactsStatsSyncPreferenceSet(Context context, EsAccount esaccount)
    {
        int i = esaccount.getIndex();
        boolean flag;
        if(i == -1)
            flag = true;
        else
            flag = context.getSharedPreferences("accounts", 0).contains((new StringBuilder()).append(i).append(".contacts_stats_sync").toString());
        return flag;
    }
    
    public static boolean isContactsSyncEnabled(Context context, EsAccount esaccount)
    {
        boolean flag = esaccount.isPlusPage();
        boolean flag1 = false;
        if(!flag)
        {
            SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
            int i = esaccount.getIndex();
            flag1 = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".contacts_sync").toString(), false);
        }
        return flag1;
    }
    
    public static boolean isContactsStatsSyncEnabled(Context context, EsAccount esaccount)
    {
        boolean flag = esaccount.isPlusPage();
        boolean flag1 = false;
        if(!flag)
        {
            SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
            int i = esaccount.getIndex();
            flag1 = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".contacts_stats_sync").toString(), false);
        }
        return flag1;
    }
    
    public static boolean isContactsSyncPreferenceSet(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        boolean flag;
        if(i == -1)
            flag = true;
        else
            flag = sharedpreferences.contains((new StringBuilder()).append(i).append(".contacts_sync").toString());
        return flag;
    }
    
    public static void setWarmWelcomeTimestamp(Context context, EsAccount esaccount, long l, boolean flag)
    {
        int i = esaccount.getIndex();
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        if(!flag || sharedpreferences.getLong((new StringBuilder()).append(i).append(".warm_welcome_ts").toString(), 0L) <= l) {
        	android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong((new StringBuilder()).append(i).append(".warm_welcome_ts").toString(), l);
            editor.putBoolean((new StringBuilder()).append(i).append(".settings_synced").toString(), flag);
            editor.commit();
            if(!flag)
                EsService.uploadChangedSettings(context, esaccount);
        }
    }
    
    public static void saveInstantUploadPhotoWifiOnly(Context context, EsAccount esaccount, boolean flag)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean((new StringBuilder()).append(i).append(".iu_photos_wifi_only").toString(), flag);
        editor.commit();
    }
    
    public static void saveInstantUploadVideoWifiOnly(Context context, EsAccount esaccount, boolean flag)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean((new StringBuilder()).append(i).append(".iu_videos_wifi_only").toString(), flag);
        editor.commit();
    }
    
    public static void saveInstantUploadEnabled(Context context, EsAccount esaccount, boolean flag)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        if(sharedpreferences.getInt("active_account", -1) == i)
        {
            android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean((new StringBuilder()).append(i).append(".iu_enabled").toString(), flag);
            editor.commit();
        }
    }

	public static void upgradeAccount(Context context, EsAccount esaccount) throws Exception {
		GetSettingsOperation getsettingsoperation = new GetSettingsOperation(context, esaccount, false, null, null);
		getsettingsoperation.start();
		if (getsettingsoperation.hasError()) {
			throw new Exception("Account upgrade failed",
					getsettingsoperation.getException());
		} else {
			android.content.SharedPreferences.Editor editor = context
					.getSharedPreferences("accounts", 0).edit();
			int i = esaccount.getIndex();
			editor.putInt("active_account", i);
			editor.remove("active");
			editor.remove((new StringBuilder()).append(i).append(".user_id")
					.toString());
			editor.remove((new StringBuilder()).append(i).append(".name")
					.toString());
			editor.commit();
			ContentResolver.requestSync(
					AccountsUtil.newAccount(esaccount.getName()),
					"com.galaxy.meetup.client.android.content.EsProvider",
					new Bundle());
			return;
		}
	}
	
	public static void setOobComplete(Context context, EsAccount esaccount) {
		SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
		int i = esaccount.getIndex();
		if (i != -1) {
			android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putBoolean((new StringBuilder()).append(i).append(".contacts_oob_completed").toString(), true);
			editor.putBoolean((new StringBuilder()).append(i).append(".iu_oob_completed").toString(), true);
			editor.commit();
		}
	}

	public static void setOneClickAddTooltipShown(Context context, EsAccount esaccount)
    {
        int i = esaccount.getIndex();
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        editor.putBoolean((new StringBuilder()).append(i).append(".one_click_tooltip_shown").toString(), true);
        editor.commit();
    }
	
	public static void saveRecentImagesTimestamp(Context context, long l)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        long l1 = sharedpreferences.getLong("recent_images_timestamp", 0L);
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong("recent_images_timestamp", Math.max(l1, l));
        editor.commit();
    }
	
	public static void saveContactsSyncCleanupStatus(Context context, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        editor.putBoolean("contacts_clean", flag);
        editor.commit();
    }
    
	public static boolean hasSeenReportAbusetWarningDialog(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        return sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".seen_hangout_abuse_warning").toString(), false);
    }
	
	public static boolean hasOneClickAddTooltipBeenShown(Context context, EsAccount esaccount)
    {
        int i = esaccount.getIndex();
        return context.getSharedPreferences("accounts", 0).getBoolean((new StringBuilder()).append(i).append(".one_click_tooltip_shown").toString(), false);
    }
    
    public static boolean hasLoggedInThePast(Context context)
    {
        return context.getSharedPreferences("accounts", 0).contains("last_active");
    }
    
    public static boolean hasVisitedOob(Context context)
    {
        return context.getSharedPreferences("accounts", 0).getBoolean("visited_oob", false);
    }
    
    public static void setHasVisitedOob(Context context, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        editor.putBoolean("visited_oob", flag);
        editor.commit();
    }
    
    public static synchronized EsAccount getActiveAccount(Context context) {
        int i = context.getSharedPreferences("accounts", 0).getInt("active_account", -1);
        if(-1 == i) {
        	return null;
        }
        return getAccount(context, i, false);
    }
    
    public static synchronized EsAccount insertAccount(Context context, String gaiaId, String accountName, String diaplayName, boolean isChild, boolean isPlusPage) {
        
        int i;
        int j;
        if(EsLog.isLoggable("EsAccountsData", 3))
            Log.d("EsAccountsData", (new StringBuilder("insertAccount: ")).append(accountName).toString());
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        i = -1;
        j = sharedpreferences.getInt("count", 0);
        
        for(int k = 0; k < j; k++) {
        	String s3 = sharedpreferences.getString((new StringBuilder()).append(k).append(".account_name").toString(), null);
        	if(s3 == null || !s3.equals(accountName)) {
        		continue;
        	}
        	i = k;
        }
        
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        if(i == -1)
        {
            i = j;
            editor.putInt("count", j + 1);
        }
        editor.putString((new StringBuilder()).append(i).append(".gaia_id").toString(), gaiaId);
        editor.putString((new StringBuilder()).append(i).append(".account_name").toString(), accountName);
        editor.putString((new StringBuilder()).append(i).append(".display_name").toString(), diaplayName);
        editor.putBoolean((new StringBuilder()).append(i).append(".is_child").toString(), isChild);
        editor.putBoolean((new StringBuilder()).append(i).append(".is_plus_page").toString(), isPlusPage);
        editor.putInt("active_account", i);
        editor.putInt("last_active", i);
        editor.commit();
        EsAccount esaccount = new EsAccount(accountName, gaiaId, diaplayName, isChild, isPlusPage, i);
        loadExperiments(context, esaccount);
        return esaccount;
    }
    
    public static String getExperiment(String s, String s1) {
    	synchronized(sExperiments) {
    		String s2 = (String)sExperiments.get(s);
    		if(null != s2) {
    			return s2;
    		} else {
    			return s1;
    		}
    	}
    }
    
    private static EsAccount getAccount(Context context, int i, boolean flag)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        String s = sharedpreferences.getString((new StringBuilder()).append(i).append(".gaia_id").toString(), null);
        EsAccount esaccount;
        if(s != null || flag)
        {
            if(EsLog.isLoggable("EsAccountsData", 3))
            {
                StringBuilder stringbuilder = new StringBuilder("EsAccount.getAccount returning account for gaiaId: ");
                String s1;
                String s2;
                if(s == null)
                    s1 = "null";
                else
                    s1 = s;
                Log.d("EsAccountsData", stringbuilder.append(s1).toString());
            }
            String s2 = sharedpreferences.getString((new StringBuilder()).append(i).append(".account_name").toString(), null);
            if(s2 == null)
                s2 = sharedpreferences.getString((new StringBuilder()).append(i).append(".name").toString(), null);
            esaccount = new EsAccount(s2, s, sharedpreferences.getString((new StringBuilder()).append(i).append(".display_name").toString(), null), sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".is_child").toString(), false), sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".is_plus_page").toString(), false), i);
        } else
        {
            if(EsLog.isLoggable("EsAccountsData", 6))
                Log.e("EsAccountsData", (new StringBuilder("EsAccount.getAccount failed to get account ")).append(i).toString());
            esaccount = null;
        }
        return esaccount;
    }
    
    public static void loadExperiments(Context context)
    {
        EsAccount esaccount = getActiveAccount(context);
        if(esaccount != null)
            loadExperiments(context, esaccount);
    }
    
    public static long loadRecentImagesTimestamp(Context context)
    {
        return context.getSharedPreferences("accounts", 0).getLong("recent_images_timestamp", 0L);
    }
    
    public static long queryLastPhotoNotificationTimestamp(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        return sharedpreferences.getLong((new StringBuilder()).append(i).append(".last_photo_notification_ts").toString(), -1L);
    }
    
    public static long queryLastSyncTimestamp(Context context, EsAccount esaccount)
    {
        try {
	    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
	        return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT last_sync_time  FROM account_status", null);
        } catch (SQLiteDoneException sqlitedoneexception) {
        	return -1L;
        }
    }
    
    public static void uploadChangedSettings(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        if(!sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".settings_synced").toString(), true)) {
        	long l = sharedpreferences.getLong((new StringBuilder()).append(i).append(".warm_welcome_ts").toString(), 0L);
            if(l != 0L)
            {
                SetSettingsOperation setsettingsoperation = new SetSettingsOperation(context, esaccount, l, null, null);
                setsettingsoperation.start();
                if(setsettingsoperation.hasError())
                    Log.e("EsAccountsData", (new StringBuilder("Could not upload settings: ")).append(setsettingsoperation.getErrorCode()).toString(), setsettingsoperation.getException());
            }
        }
    }
    
    public static ArrayList getStreamViewList(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        String as[] = sharedpreferences.getString((new StringBuilder()).append(i).append(".stream_views").toString(), "").split(",");
        ArrayList arraylist = new ArrayList();
        int j = as.length;
        for(int k = 0; k < j; k++)
        {
            String s = as[k];
            if(!TextUtils.isEmpty(s))
                arraylist.add(s);
        }

        if(arraylist.isEmpty())
        {
            arraylist.add("v.whatshot");
            arraylist.add("v.all.circles");
            arraylist.add("v.nearby");
        }
        return arraylist;
    }
    
    public static void activateAccount(Context context, EsAccount esaccount, String s)
    {
        EsDatabaseHelper esdatabasehelper = EsDatabaseHelper.getDatabaseHelper(context, esaccount);
        esdatabasehelper.createNewDatabase();
        SQLiteDatabase sqlitedatabase = esdatabasehelper.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("last_sync_time", Integer.valueOf(-1));
        contentvalues.put("last_stats_sync_time", Integer.valueOf(-1));
        contentvalues.put("last_contacted_time", Integer.valueOf(-1));
        contentvalues.put("wipeout_stats", Integer.valueOf(-1));
        contentvalues.put("circle_sync_time", Integer.valueOf(-1));
        contentvalues.put("people_sync_time", Integer.valueOf(-1));
        contentvalues.putNull("people_last_update_token");
        contentvalues.put("avatars_downloaded", Integer.valueOf(0));
        contentvalues.put("user_id", esaccount.getGaiaId());
        sqlitedatabase.update("account_status", contentvalues, null, null);
        EsPeopleData.activateAccount();
        EsSyncAdapterService.activateAccount(context, esaccount.getName());
        InstantUploadSyncService.activateAccount(context, esaccount.getName());
        InstantUpload.showFirstTimeFullSizeNotification(context, esaccount);
        EsPeopleData.insertSelf(context, esaccount, s);
    }
    
    public static List accountsChanged(Context context)
    {
        if(EsLog.isLoggable("EsAccountsData", 3))
            Log.d("EsAccountsData", "accountsChanged");
        List list = filterRemovedAccounts(context);
        for(Iterator iterator = list.iterator(); iterator.hasNext(); deactivateAccount(context, (String)iterator.next(), true));
        return list;
    }
    
    private static synchronized List filterRemovedAccounts(Context context)
    {
        List list = AccountsUtil.getAccounts(context);
        ArrayList arraylist = new ArrayList(list.size());
        for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(((Account)iterator.next()).name));
        
        ArrayList arraylist1 = new ArrayList();
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int count = sharedpreferences.getInt("count", 0);
        
        for(int j = 0; j < count; j++) {
        	String s = sharedpreferences.getString((new StringBuilder()).append(j).append(".account_name").toString(), null);
        	 if(s != null && !arraylist.contains(s))
                 arraylist1.add(s);
        }
        return arraylist1;
    }
    
    public static synchronized void updateAccount(Context context, EsAccount esaccount, String s, String s1, boolean flag) {
        if(EsLog.isLoggable("EsAccountsData", 3))
            Log.d("EsAccountsData", (new StringBuilder("updateAccount: ")).append(esaccount.getName()).toString());
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putString((new StringBuilder()).append(i).append(".gaia_id").toString(), s);
        editor.putString((new StringBuilder()).append(i).append(".account_name").toString(), esaccount.getName());
        editor.putString((new StringBuilder()).append(i).append(".display_name").toString(), s1);
        editor.putBoolean((new StringBuilder()).append(i).append(".is_child").toString(), flag);
        editor.commit();
        return;
    }
    
    public static boolean hasSeenMinorHangoutWarningDialog(Context context, EsAccount esaccount) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        return sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".seen_hangout_minor_warning").toString(), false);
    }
    
    public static boolean hasSeenMinorPublicExtendedDialog(Context context, EsAccount esaccount) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        return sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".minor_public_extended_dialog").toString(), false);
    }
    
    public static boolean hasSeenLocationDialog(Context context, EsAccount esaccount) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        return sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".location_dialog").toString(), false);
    }
    
    public static boolean hasSeenWarmWelcome(Context context, EsAccount esaccount) {
        boolean flag = Property.WARM_WELCOME_ON_LOGIN.get().equalsIgnoreCase("false");
        boolean flag1 = false;
        if(!flag) {
        	return flag1;
        }
        
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        long j = sharedpreferences.getLong((new StringBuilder()).append(i).append(".warm_welcome_ts").toString(), 0L) - 0L;
        flag1 = false;
        if(j != 0)
            flag1 = true;
        return flag1;
    }
    
    public static boolean needContactSyncOob(Context context, EsAccount esaccount) {
        boolean flag = AndroidContactsSync.isContactsProviderAvailable(context);
        boolean flag1 = false;
        if(!flag) 
        	return false; 
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        boolean flag2 = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".contacts_oob_completed").toString(), false);
        flag1 = false;
        if(!flag2)
            flag1 = true;
        return flag1;
    }
    
    public static boolean needInstantUploadOob(Context context, EsAccount esaccount) {
        boolean flag = true;
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        if(!sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".iu_oob_completed").toString(), false)) {
        	return true;
        }
        boolean flag1 = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".iu_enabled").toString(), false);
        boolean flag2 = InstantUpload.isSyncEnabled(esaccount);
        boolean flag3 = ContentResolver.getMasterSyncAutomatically();
        if(!flag1 || flag2 && flag3)
            flag = false;
        return flag;
    }
    
    public static void saveMinorPublicExtendedDialogSeenPreference(Context context, EsAccount esaccount, boolean flag) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putBoolean((new StringBuilder()).append(i).append(".minor_public_extended_dialog").toString(), true);
        editor.commit();
    }
    
    public static void saveLocationDialogSeenPreference(Context context, EsAccount esaccount, boolean flag) {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putBoolean((new StringBuilder()).append(i).append(".location_dialog").toString(), true);
        editor.commit();
    }
    
    public static void saveReportAbuseWarningDialogSeenPreference(Context context, EsAccount esaccount, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putBoolean((new StringBuilder()).append(i).append(".seen_hangout_abuse_warning").toString(), true);
        editor.commit();
    }
    
    public static void saveMinorHangoutWarningDialogSeenPreference(Context context, EsAccount esaccount, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putBoolean((new StringBuilder()).append(i).append(".seen_hangout_minor_warning").toString(), true);
        editor.commit();
    }
    
    public static void saveContactsSyncPreference(Context context, EsAccount esaccount, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putBoolean((new StringBuilder()).append(i).append(".contacts_sync").toString(), flag);
        editor.commit();
    }
    
    public static void saveContactsStatsSyncPreference(Context context, EsAccount esaccount, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putBoolean((new StringBuilder()).append(i).append(".contacts_stats_sync").toString(), flag);
        editor.commit();
    }
    
    public static void saveLastPhotoNotificationTimestamp(Context context, EsAccount esaccount, long l)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong((new StringBuilder()).append(i).append(".last_photo_notification_ts").toString(), l);
        editor.commit();
    }
    
    public static void saveContactsStatsSyncCleanupStatus(Context context, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        editor.putBoolean("contacts_stats_clean", flag);
        editor.commit();
    }
    
    private static void loadExperiments(Context context, EsAccount esaccount)
    {
        String s = context.getSharedPreferences("accounts", 0).getString((new StringBuilder()).append(esaccount.getName()).append(".flags").toString(), null);
        List list = null;
        if(s != null)
            list = ((ExperimentList)JsonUtil.toBean(s, ExperimentList.class)).flags;
        loadExperiments(list);
    }

    private static void loadExperiments(List list)
    {
    	synchronized(sExperiments) {
    		sExperiments.clear();
    		if(null == list) {
    			return;
    		}
    		
    		GetMobileExperimentsResponseExperiment getmobileexperimentsresponseexperiment;
    		String s;
    		String s1 = null;
            int size = list.size();
            for(int j = 0; j < size; j++) {
            	s1 = null;
            	getmobileexperimentsresponseexperiment = (GetMobileExperimentsResponseExperiment)list.get(j);
                s = getmobileexperimentsresponseexperiment.flagType;
                if("BOOLEAN".equals(s)) {
                	s1 = "TRUE";
                } else if("STRING".equals(s))
                {
                    if(null != getmobileexperimentsresponseexperiment.value)
                    	s1 = getmobileexperimentsresponseexperiment.value.stringValue;
                } else
                if("DOUBLE".equals(s))
                {
                    GetMobileExperimentsResponseExperimentValue getmobileexperimentsresponseexperimentvalue1 = getmobileexperimentsresponseexperiment.value;
                    s1 = null;
                    if(getmobileexperimentsresponseexperimentvalue1 != null)
                    {
                        Double double1 = getmobileexperimentsresponseexperiment.value.doubleValue;
                        s1 = null;
                        if(double1 != null)
                            s1 = Double.toString(getmobileexperimentsresponseexperiment.value.doubleValue.doubleValue());
                    }
                } else
                {
                    boolean flag = "LONG".equals(s);
                    s1 = null;
                    if(flag)
                    {
                        GetMobileExperimentsResponseExperimentValue getmobileexperimentsresponseexperimentvalue = getmobileexperimentsresponseexperiment.value;
                        s1 = null;
                        if(getmobileexperimentsresponseexperimentvalue != null)
                        {
                            Long long1 = getmobileexperimentsresponseexperiment.value.longValue;
                            s1 = null;
                            if(long1 != null)
                                s1 = Long.toString(getmobileexperimentsresponseexperiment.value.longValue.longValue());
                        }
                    }
                }
                
                if(s1 != null)
                    sExperiments.put(getmobileexperimentsresponseexperiment.flagId, s1);
            }
    	}
    }
    
    public static void restoreAccountSettings(final Context context, final EsAccount esaccount) {
    	
        if(!esaccount.isPlusPage()) {
        	if(!needContactSyncOob(context, esaccount))
                if(isContactsStatsSyncEnabled(context, esaccount))
                    EsService.disableWipeoutStats(context, esaccount);
                else
                    EsService.enableAndPerformWipeoutStats(context, esaccount);
            if(!needInstantUploadOob(context, esaccount))
            {
                SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
                int i = esaccount.getIndex();
                final boolean enabled = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".iu_enabled").toString(), false);
                final boolean photosWifiOnly = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".iu_photos_wifi_only").toString(), false);
                final boolean videosWifiOnly = sharedpreferences.getBoolean((new StringBuilder()).append(i).append(".iu_videos_wifi_only").toString(), false);
                (new AsyncTask() {

                    protected final Object doInBackground(Object aobj[])
                    {
                        InstantUpload.setPhotoWiFiOnlySetting(context, photosWifiOnly);
                        InstantUpload.setVideoWiFiOnlySetting(context, videosWifiOnly);
                        InstantUpload.enableInstantUpload(context, esaccount, enabled);
                        return null;
                    }

                }).execute((Object[])null);
            } 
        } else { 
        	(new AsyncTask() {

                protected final Object doInBackground(Object aobj[])
                {
                    InstantUpload.enableInstantUpload(context, esaccount, false);
                    return null;
                }

            }).execute((Object[])null);
        }
    }
    
    public static synchronized EsAccount getAccountByName(Context context, String s) {
    	
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = sharedpreferences.getInt("count", 0);
        EsAccount esaccount = null;
        for(int j = 0; j < i; j++) {
        	String s1 = sharedpreferences.getString((new StringBuilder()).append(j).append(".account_name").toString(), null);
            if(s.equals(s1)) {
            	esaccount = new EsAccount(s1, sharedpreferences.getString((new StringBuilder()).append(j).append(".gaia_id").toString(), null), sharedpreferences.getString((new StringBuilder()).append(j).append(".display_name").toString(), null), sharedpreferences.getBoolean((new StringBuilder()).append(j).append(".is_child").toString(), false), sharedpreferences.getBoolean((new StringBuilder()).append(j).append(".is_plus_page").toString(), false), j);
            	return esaccount;
            }
        }
        return null;
    }
    
    public static void syncExperiments(Context context, EsAccount esaccount) throws IOException {
        GetMobileExperimentsOperation getmobileexperimentsoperation = new GetMobileExperimentsOperation(context, esaccount, null, null);
        getmobileexperimentsoperation.start();
        getmobileexperimentsoperation.logAndThrowExceptionIfFailed("EsAccountsData");
    }
    
    public static void insertExperiments(Context context, EsAccount esaccount, List list)
    {
        ExperimentList experimentlist = new ExperimentList();
        experimentlist.flags = list;
        String s = (new StringBuilder()).append(esaccount.getName()).append(".flags").toString();
        String s1 = experimentlist.toJsonString();
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        if(!TextUtils.equals(sharedpreferences.getString(s, null), s1))
        {
            android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(s, s1);
            editor.commit();
            loadExperiments(list);
            int i = sExperimentListeners.size();
            for(int j = 0; j < i; j++)
                ((ExperimentListener)sExperimentListeners.get(j)).onExperimentsChanged();

        }
    }
    
    public static synchronized void deactivateAccount(final Context context, String s, boolean flag)
    {
        final EsAccount esaccount;
        if(EsLog.isLoggable("EsAccountsData", 3))
            Log.d("EsAccountsData", (new StringBuilder("deactivateAccount: ")).append(s).toString());
        esaccount = getAccountByName(context, s);
        if(null == esaccount) {
        	return;
        }
        EsAccount esaccount1;
        EsSyncAdapterService.deactivateAccount(s);
        InstantUploadSyncService.deactivateAccount(context, s);
        esaccount1 = getActiveAccount(context);
        if(null != esaccount1 && esaccount1.getIndex() == esaccount.getIndex()) {
        	android.content.SharedPreferences.Editor editor;
            editor = context.getSharedPreferences("accounts", 0).edit();
            editor.remove("active_account");
            if(android.os.Build.VERSION.SDK_INT < 9) { 
            	editor.commit(); 
            } else { 
            	editor.apply();
            }
            android.content.SharedPreferences.Editor editor1;
            editor1 = context.getSharedPreferences("streams", 0).edit();
            editor1.remove("circle");
            if(android.os.Build.VERSION.SDK_INT >= 9) {
            	editor1.apply();
            } else {
            	editor1.commit();
            }
            
            (new Handler(Looper.getMainLooper())).post(new Runnable() {

                public final void run()
                {
                    RealTimeChatService.logout(context, esaccount);
                    SkyjamPlaybackService.logOut(context);
                }

            });
        }
        
        InstantUpload.cancelAllUploads(context, esaccount);
        InstantUpload.enableInstantUpload(context, null, false);
        EsEventData.disableInstantShare(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("iu.received_low_quota", false).putBoolean("iu.received_no_quota", false).commit();
        ((AlarmManager)context.getSystemService("alarm")).cancel(Intents.getEventFinishedIntent(context, null));
        EsNotificationData.deactivateAccount(context, esaccount);
        EsProvider.deleteDatabase(context, esaccount);
        AndroidContactsSync.deactivateAccount(context, esaccount);
        deleteAccount(context, s, flag);
        GCommApp.deactivateAccount(context, esaccount);
        ImageCache.getInstance(context).clear();
        if(EsLog.isLoggable("EsAccountsData", 3))
            Log.d("EsAccountsData", (new StringBuilder("Account deactivated: ")).append(s).toString());
    }
    
    public static void saveServerSettings(Context context, EsAccount esaccount, AccountSettingsData accountsettingsdata)
    {
        if(accountsettingsdata.getWarmWelcomeTimestamp() != null)
            setWarmWelcomeTimestamp(context, esaccount, accountsettingsdata.getWarmWelcomeTimestamp().longValue(), true);
        if(accountsettingsdata.getShareboxSettings() != null)
            savePostingPreferences(context, esaccount, accountsettingsdata.getShareboxSettings());
    }
    
    public static boolean isContactsStatsWipeoutNeeded(Context context, EsAccount esaccount) {
    	
    	try {
    		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
    		return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT wipeout_stats  FROM account_status", null) == 1L;
    	} catch (SQLiteDoneException sqlitedoneexception) {
    		return false;
    	}
    }
    
    public static void saveContactsStatsWipeoutNeeded(Context context, EsAccount esaccount, boolean flag)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        int i;
        if(flag)
            i = 1;
        else
            i = 0;
        contentvalues.put("wipeout_stats", Integer.valueOf(i));
        sqlitedatabase.update("account_status", contentvalues, null, null);
    }
    
    public static void saveLastContactedTimestamp(Context context, EsAccount esaccount, long l)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("last_contacted_time", Long.valueOf(l));
        sqlitedatabase.update("account_status", contentvalues, null, null);
        context.getContentResolver().notifyChange(EsProvider.ACCOUNT_STATUS_URI, null);
    }
    
    public static void saveAudience(Context context, EsAccount esaccount, byte abyte0[])
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues(2);
        contentvalues.put("audience_data", abyte0);
        sqlitedatabase.update("account_status", contentvalues, null, null);
    }
    
    public static void savePostingPreferences(Context context, EsAccount esaccount, ShareboxSettings shareboxsettings)
    {
        String s;
        boolean flag1;
        boolean flag;
        android.content.SharedPreferences.Editor editor;
        if(shareboxsettings.defaultSharingRosters != null)
        {
            sHadSharingRoster = true;
            AudienceData audiencedata = EsPeopleData.convertSharingRosterToAudience(context, esaccount, shareboxsettings.defaultSharingRosters);
            if(audiencedata != null)
                try
                {
                    saveAudience(context, esaccount, DbAudienceData.serialize(audiencedata));
                }
                catch(IOException ioexception)
                {
                    Log.e("EsAccountsData", "Error saving default audience");
                }
        } else
        {
            sHadSharingRoster = false;
        }
        flag = true;
        s = shareboxsettings.lastLocationDisplayType;
        flag1 = false;
        if(null != s) {
        	if(s.equals("HIDE")) 
        		flag = false;
        	else if(s.equals("SHOW_CITY_LEVEL")) {
                flag1 = true;
        	}
        }
        
        editor = context.getSharedPreferences("streams", 0).edit();
        editor.putBoolean("want_locations", flag);
        editor.putBoolean("city_level_location", flag1);
        editor.commit();
        
    }
    
    private static synchronized void deleteAccount(Context context, String s, boolean flag)
    {
        if(EsLog.isLoggable("EsAccountsData", 3))
            Log.d("EsAccountsData", (new StringBuilder("deleteAccount: ")).append(s).toString());
        EsAccount esaccount = getAccountByName(context, s);
        if(null == esaccount) {
        	return;
        }
        
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.remove((new StringBuilder()).append(i).append(".gaia_id").toString());
        editor.remove((new StringBuilder()).append(i).append(".display_name").toString());
        editor.remove((new StringBuilder()).append(i).append(".is_child").toString());
        editor.remove((new StringBuilder()).append(i).append(".is_plus_page").toString());
        editor.remove((new StringBuilder()).append(i).append(".location_dialog").toString());
        editor.remove((new StringBuilder()).append(i).append(".warm_welcome_ts").toString());
        editor.remove((new StringBuilder()).append(i).append(".last_photo_notification_ts").toString());
        editor.remove((new StringBuilder()).append(i).append(".seen_hangout_minor_warning").toString());
        editor.remove((new StringBuilder()).append(i).append(".seen_hangout_abuse_warning").toString());
        editor.remove((new StringBuilder()).append(i).append(".stream_views").toString());
        editor.remove((new StringBuilder()).append(i).append(".settings_synced").toString());
        editor.remove((new StringBuilder()).append(i).append(".minor_public_extended_dialog").toString());
        editor.remove((new StringBuilder()).append(i).append(".one_click_tooltip_shown").toString());
        editor.remove("contacts_clean");
        editor.remove("contacts_stats_clean");
        editor.remove("recent_images_timestamp");
        if(flag)
        {
            editor.remove((new StringBuilder()).append(i).append(".account_name").toString());
            editor.remove((new StringBuilder()).append(i).append(".contacts_sync").toString());
            editor.remove((new StringBuilder()).append(i).append(".contacts_stats_sync").toString());
            editor.remove((new StringBuilder()).append(i).append(".iu_enabled").toString());
            editor.remove((new StringBuilder()).append(i).append(".iu_photos_wifi_only").toString());
            editor.remove((new StringBuilder()).append(i).append(".iu_videos_wifi_only").toString());
            editor.remove((new StringBuilder()).append(i).append(".contacts_oob_completed").toString());
            editor.remove((new StringBuilder()).append(i).append(".iu_oob_completed").toString());
            editor.remove((new StringBuilder()).append(i).append(".find_people_promo_ts").toString());
            editor.remove((new StringBuilder()).append(i).append(".flags").toString());
        }
        editor.commit();
    }
    
    public static boolean hadSharingRoster()
    {
        return sHadSharingRoster;
    }

    public static long getLastDatabaseCleanupTimestamp(Context context, EsAccount esaccount)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("accounts", 0);
        int i = esaccount.getIndex();
        return sharedpreferences.getLong((new StringBuilder()).append(i).append(".cleanup_timestamp").toString(), 0L);
    }
    
    public static void saveLastDatabaseCleanupTimestamp(Context context, EsAccount esaccount, long l)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        int i = esaccount.getIndex();
        editor.putLong((new StringBuilder()).append(i).append(".cleanup_timestamp").toString(), l);
        editor.commit();
    }
    
    public static void onAccountUpgradeRequired(Context context, int i)
    {
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences("accounts", 0).edit();
        editor.remove((new StringBuilder()).append(i).append(".gaia_id").toString());
        editor.putLong((new StringBuilder()).append(i).append(".user_id").toString(), 0L);
        editor.commit();
    }
    
    public static void updateAudienceHistory(Context context, EsAccount esaccount, AudienceData audiencedata)
    {
        // TODO
    }
    
    public static class ExperimentList extends GenericJson {

        public List flags;

    }
    
	public static interface ExperimentListener {

		void onExperimentsChanged();
	}
}
