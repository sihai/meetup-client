/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsAsyncTaskLoader;
import com.galaxy.meetup.client.android.LabelPreference;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.service.AndroidContactsSync;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.AndroidUtils;
import com.galaxy.meetup.client.util.HelpUrl;

/**
 * 
 * @author sihai
 *
 */
public class SettingsActivity extends BaseSettingsActivity {
	
	private static String sContactsStatsSyncKey;
    private static String sContactsSyncKey;
    private static String sHangoutKey;
    private static String sHangoutOnOffKey;
    private static String sInstantUploadKey;
    private static String sMessengerKey;
    private static String sMessengerOnOffKey;
    private static String sNotificationsKey;
    private static String sNotificationsOnOffKey;
    
    public SettingsActivity()
    {
    }

    private void setOnOffLabel(LabelPreference labelpreference, boolean flag)
    {
        Resources resources = getResources();
        if(flag)
        {
            labelpreference.setLabel(getString(R.string.preference_on));
            labelpreference.setLabelColor(resources.getColor(R.color.preference_label_on));
        } else
        {
            labelpreference.setLabel(getString(R.string.preference_off));
            labelpreference.setLabelColor(resources.getColor(R.color.preference_label_off));
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setTitle(getString(R.string.home_menu_settings));
        Object obj = getIntent().getParcelableExtra("account");
        if(null == obj) {
        	 if(android.os.Build.VERSION.SDK_INT >= 14 && "android.intent.action.MANAGE_NETWORK_USAGE".equals(getIntent().getAction()))
                 obj = EsAccountsData.getActiveAccount(getApplicationContext());
        }
        
        if(null == obj) {
        	finish();
        	return;
        }
        
        getIntent().putExtra("account", (EsAccount)obj);
        Object obj1 = obj;
        
        if(sNotificationsKey == null)
        {
            sNotificationsKey = getString(R.string.communication_preference_notifications_key);
            sMessengerKey = getString(R.string.communication_preference_messenger_key);
            sHangoutKey = getString(R.string.communication_preference_hangout_key);
            sInstantUploadKey = getString(R.string.photo_preference_instant_upload_key);
            sContactsSyncKey = getString(R.string.contacts_sync_preference_key);
            sContactsStatsSyncKey = getString(R.string.contacts_stats_sync_preference_key);
            sNotificationsOnOffKey = getString(R.string.notifications_preference_enabled_key);
            sMessengerOnOffKey = getString(R.string.realtimechat_notify_setting_key);
            sHangoutOnOffKey = getString(R.string.hangout_notify_setting_key);
        }
        
        EsAccount esaccount = null;
        if((obj1 instanceof EsAccount)) {
        	esaccount = (EsAccount)obj1;
        	if(esaccount.isPlusPage())
            {
                addPreferencesFromResource(R.xml.main_preferences_plus_page);
            } else
            {
                addPreferencesFromResource(R.xml.main_preferences);
                addPreferencesFromResource(R.xml.contacts_sync_preferences);
                if(AndroidContactsSync.isAndroidSyncSupported(this))
                {
                    CheckBoxPreference checkboxpreference1 = (CheckBoxPreference)findPreference(sContactsSyncKey);
                    checkboxpreference1.setChecked(EsAccountsData.isContactsSyncEnabled(this, esaccount));
                    checkboxpreference1.setOnPreferenceChangeListener(new ContactsSyncPreferenceChangeListener(esaccount));
                }
                CheckBoxPreference checkboxpreference = (CheckBoxPreference)findPreference(sContactsStatsSyncKey);
                Resources resources = getResources();
                int i;
                if(AndroidUtils.hasTelephony(this))
                    i = R.string.contacts_stats_sync_preference_enabled_phone_summary;
                else
                    i = R.string.contacts_stats_sync_preference_enabled_tablet_summary;
                checkboxpreference.setSummary(resources.getString(i));
                checkboxpreference.setChecked(EsAccountsData.isContactsStatsSyncEnabled(this, esaccount));
                checkboxpreference.setOnPreferenceChangeListener(new ContactsStatsSyncPreferenceChangeListener(esaccount));
            }
            putAccountExtra(getPreferenceScreen(), esaccount);
            return;
        } else if((obj1 instanceof Account)) {
        	esaccount = EsService.getActiveAccount(this);
            if(null == esaccount) {
                Toast.makeText(this, getString(R.string.not_signed_in), 1).show();
                finish();
                return;
            } else {
            	getIntent().putExtra("account", esaccount);
            	if(esaccount.isPlusPage())
                {
                    addPreferencesFromResource(R.xml.main_preferences_plus_page);
                } else
                {
                    addPreferencesFromResource(R.xml.main_preferences);
                    addPreferencesFromResource(R.xml.contacts_sync_preferences);
                    if(AndroidContactsSync.isAndroidSyncSupported(this))
                    {
                        CheckBoxPreference checkboxpreference1 = (CheckBoxPreference)findPreference(sContactsSyncKey);
                        checkboxpreference1.setChecked(EsAccountsData.isContactsSyncEnabled(this, esaccount));
                        checkboxpreference1.setOnPreferenceChangeListener(new ContactsSyncPreferenceChangeListener(esaccount));
                    }
                    CheckBoxPreference checkboxpreference = (CheckBoxPreference)findPreference(sContactsStatsSyncKey);
                    Resources resources = getResources();
                    int i;
                    if(AndroidUtils.hasTelephony(this))
                        i = R.string.contacts_stats_sync_preference_enabled_phone_summary;
                    else
                        i = R.string.contacts_stats_sync_preference_enabled_tablet_summary;
                    checkboxpreference.setSummary(resources.getString(i));
                    checkboxpreference.setChecked(EsAccountsData.isContactsStatsSyncEnabled(this, esaccount));
                    checkboxpreference.setOnPreferenceChangeListener(new ContactsStatsSyncPreferenceChangeListener(esaccount));
                }
                putAccountExtra(getPreferenceScreen(), esaccount);
            }
        }
        
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.preferences_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.menu_help)
        {
            startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(this, getResources().getString(R.string.url_param_help_settings))));
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public void onResume() {
        LabelPreference labelpreference2;
        SharedPreferences sharedpreferences;
        Resources resources;
        LabelPreference labelpreference;
        LabelPreference labelpreference1;
        EsAccount esaccount;
        super.onResume();
        if(!getAccount().isPlusPage())
        {
            boolean flag = ContentResolver.getSyncAutomatically(AccountsUtil.newAccount(getAccount().getName()), "com.galaxy.meetup.client.android.iu.EsGalaxyIuProvider");
            boolean flag1 = ContentResolver.getMasterSyncAutomatically();
            LabelPreference labelpreference3 = (LabelPreference)findPreference(sInstantUploadKey);
            if(flag1 && flag)
            {
                labelpreference3.setSummary(null);
                labelpreference3.setOnPreferenceClickListener(null);
                (new InstantUploadSettingsLoader(this)).startLoading();
            } else
            {
                if(flag1)
                {
                    String s = getString(R.string.es_google_iu_provider);
                    labelpreference3.setSummary(getString(R.string.photo_sync_disabled_summary, new Object[] {
                        s
                    }));
                } else
                {
                    labelpreference3.setSummary(R.string.master_sync_disabled_summary);
                }
                labelpreference3.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

                    public final boolean onPreferenceClick(Preference preference)
                    {
                        startActivity(new Intent("android.settings.SYNC_SETTINGS"));
                        return true;
                    }

                });
                setOnOffLabel(labelpreference3, false);
            }
        }
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        resources = getResources();
        labelpreference = (LabelPreference)findPreference(sNotificationsKey);
        if(labelpreference != null)
            setOnOffLabel(labelpreference, sharedpreferences.getBoolean(sNotificationsOnOffKey, resources.getBoolean(R.bool.notifications_preference_enabled_default_value)));
        labelpreference1 = (LabelPreference)findPreference(sMessengerKey);
        if(labelpreference1 != null)
            setOnOffLabel(labelpreference1, sharedpreferences.getBoolean(sMessengerOnOffKey, resources.getBoolean(R.bool.realtimechat_notify_setting_default_value)));
        labelpreference2 = (LabelPreference)findPreference(sHangoutKey);
        if(labelpreference2 == null) {
        	return;
        }
        esaccount = getAccount();
        if(esaccount == null || Hangout.getSupportedStatus(this, esaccount) != Hangout.SupportStatus.SUPPORTED) {
        	PreferenceScreen preferencescreen = getPreferenceScreen();
            if(preferencescreen != null)
                preferencescreen.removePreference(labelpreference2);
        } else {
        	setOnOffLabel(labelpreference2, sharedpreferences.getBoolean(sHangoutOnOffKey, resources.getBoolean(R.bool.hangout_notify_setting_default_value)));
        }
        
    }
    
    
    private final class ContactsStatsSyncPreferenceChangeListener implements android.preference.Preference.OnPreferenceChangeListener {

    	private final EsAccount mAccount;

	    ContactsStatsSyncPreferenceChangeListener(EsAccount esaccount)
	    {
	        super();
	        mAccount = esaccount;
	    }
	    
	    public final boolean onPreferenceChange(Preference preference, Object obj)
	    {
	        boolean flag = ((Boolean)obj).booleanValue();
	        SettingsActivity settingsactivity = SettingsActivity.this;
	        EsAccountsData.saveContactsStatsSyncPreference(settingsactivity, mAccount, flag);
	        EsService.saveLastContactedTimestamp(settingsactivity, mAccount, -1L);
	        EsAnalytics.recordImproveSuggestionsPreferenceChange(settingsactivity, mAccount, flag, OzViews.GENERAL_SETTINGS);
	        if(flag)
	            EsService.disableWipeoutStats(settingsactivity, mAccount);
	        else
	            EsService.enableAndPerformWipeoutStats(settingsactivity, mAccount);
	        return true;
	    }

    }

    private final class ContactsSyncPreferenceChangeListener implements android.preference.Preference.OnPreferenceChangeListener {

    	private final EsAccount mAccount;

	    ContactsSyncPreferenceChangeListener(EsAccount esaccount)
	    {
	        super();
	        mAccount = esaccount;
	    }
	    
	    public final boolean onPreferenceChange(Preference preference, Object obj)
	    {
	        boolean flag = ((Boolean)obj).booleanValue();
	        if(EsAccountsData.isContactsSyncEnabled(SettingsActivity.this, mAccount) != flag)
	        {
	            EsAccountsData.saveContactsSyncPreference(SettingsActivity.this, mAccount, flag);
	            AndroidContactsSync.requestSync(SettingsActivity.this, true);
	        }
	        return true;
	    }

    }

    private final class InstantUploadSettingsLoader extends EsAsyncTaskLoader {

    	public InstantUploadSettingsLoader(Context context)
        {
            super(context);
        }
    	
	    public final Object esLoadInBackground() {
	        boolean flag;
	        Cursor cursor = null;
	        flag = true;
	        ContentResolver contentresolver = getContext().getContentResolver();
	        android.net.Uri uri = InstantUploadFacade.SETTINGS_URI;
	        String as[] = new String[1];
	        as[0] = "auto_upload_enabled";
	        try {
	        	cursor = contentresolver.query(uri, as, null, null, null);
	        	if(null != cursor && cursor.moveToFirst()) {
	        		if(cursor.getInt(0) != 1)
	    	            flag = false;
	        		mHandler.post(new Runnable() {
	        			
	    	            public final void run()
	    	            {
	    	                LabelPreference labelpreference = (LabelPreference)findPreference(SettingsActivity.sInstantUploadKey);
	    	                // TODO
	    	                setOnOffLabel(labelpreference, true/*instantUploadEnabled*/);
	    	            }
	    	
	    	        });
	        	}
	        	return null;
	        } finally {
	        	if(null != cursor) {
	        		cursor.close();
	        	}
	        }
	    }
	
	    protected final void onStartLoading()
	    {
	        forceLoad();
	    }

    }
}
