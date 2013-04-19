/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.NotificationSetting;
import com.galaxy.meetup.client.android.content.NotificationSettingsCategory;
import com.galaxy.meetup.client.android.content.NotificationSettingsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;

/**
 * 
 * @author sihai
 *
 */
public class NotificationSettingsActivity extends BaseSettingsActivity {

	private Integer mGetNotificationsRequestId;
    private NotificationSettingsData mNotificationSettings;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onChangeNotificationsRequestComplete(EsAccount esaccount, ServiceResult serviceresult)
        {
            if(esaccount.equals(getAccount()) && serviceresult.hasError())
                Toast.makeText(NotificationSettingsActivity.this, R.string.notification_settings_save_failed, 0).show();
        }

        public final void onGetNotificationSettings(int i, EsAccount esaccount, NotificationSettingsData notificationsettingsdata)
        {
            if(esaccount.equals(getAccount()) && mGetNotificationsRequestId != null && mGetNotificationsRequestId.equals(Integer.valueOf(i)))
            {
                mGetNotificationsRequestId = null;
                mNotificationSettings = notificationsettingsdata;
                setupPreferences();
                dismissDialog(0x7f0a003f);
            }
        }

    };
    
    public NotificationSettingsActivity()
    {
    }

    private void setupPreferences()
    {
        PreferenceScreen preferencescreen = getPreferenceScreen();
        if(preferencescreen != null)
            preferencescreen.removeAll();
        addPreferencesFromResource(R.xml.notifications_preferences);
        PreferenceScreen preferencescreen1 = getPreferenceScreen();
        CheckBoxPreference checkboxpreference = (CheckBoxPreference)findPreference(getString(R.string.notifications_preference_enabled_key));
        String s;
        Preference preference;
        String s1;
        String s2;
        BaseSettingsActivity.RingtonePreferenceChangeListener ringtonepreferencechangelistener;
        if(getAccount() != null)
            checkboxpreference.setEnabled(true);
        else
            checkboxpreference.setEnabled(false);
        hookMasterSwitch(null, checkboxpreference);
        checkboxpreference.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {

            public final boolean onPreferenceChange(Preference preference2, Object obj)
            {
                NotificationSettingsActivity notificationsettingsactivity = NotificationSettingsActivity.this;
                boolean flag;
                if(obj == Boolean.TRUE)
                    flag = true;
                else
                    flag = false;
                notificationsettingsactivity.updatedEnabledStates(flag);
                return true;
            }

        });
        s = getString(R.string.notifications_preference_ringtone_key);
        preference = findPreference(s);
        s1 = getString(R.string.notifications_preference_ringtone_default_value);
        s2 = getRingtoneName(null, s, s1);
        ringtonepreferencechangelistener = new BaseSettingsActivity.RingtonePreferenceChangeListener(s, s1);
        preference.setOnPreferenceChangeListener(ringtonepreferencechangelistener);
        if(s2 != null)
            preference.setSummary(s2);
        if(mNotificationSettings != null)
        {
            final NotificationSettingsData settings = mNotificationSettings;
            int i = 0;
            for(int j = settings.getCategoriesCount(); i < j; i++)
            {
                NotificationSettingsCategory notificationsettingscategory = settings.getCategory(i);
                PreferenceCategory preferencecategory1 = new PreferenceCategory(this);
                preferencecategory1.setTitle(notificationsettingscategory.getDescription());
                preferencecategory1.setOrder(1000 * (i + 2));
                preferencescreen1.addPreference(preferencecategory1);
                int k = 0;
                for(int l = notificationsettingscategory.getSettingsCount(); k < l; k++)
                {
                    final NotificationSetting setting = notificationsettingscategory.getSetting(k);
                    CheckBoxPreference checkboxpreference1 = new CheckBoxPreference(this);
                    checkboxpreference1.setLayoutResource(R.layout.label_preference);
                    checkboxpreference1.setTitle(setting.getDescription());
                    checkboxpreference1.setChecked(setting.isEnabled());
                    android.preference.Preference.OnPreferenceChangeListener onpreferencechangelistener = new android.preference.Preference.OnPreferenceChangeListener() {

                        public final boolean onPreferenceChange(Preference preference2, Object obj)
                        {
                            setting.setEnabled(((Boolean)obj).booleanValue());
                            ArrayList arraylist = new ArrayList(1);
                            arraylist.add(new NotificationSetting(setting));
                            ArrayList arraylist1 = new ArrayList(1);
                            arraylist1.add(new NotificationSettingsCategory(null, arraylist));
                            NotificationSettingsData notificationsettingsdata = new NotificationSettingsData(settings.getEmailAddress(), settings.getMobileNotificationType(), arraylist1);
                            EsService.changeNotificationSettings(NotificationSettingsActivity.this, getAccount(), notificationsettingsdata);
                            return true;
                        }
                    };
                    checkboxpreference1.setOnPreferenceChangeListener(onpreferencechangelistener);
                    preferencecategory1.addPreference(checkboxpreference1);
                }

            }

        } else
        {
            PreferenceCategory preferencecategory = new PreferenceCategory(this);
            preferencecategory.setTitle(getString(R.string.notifications_preference_no_network_category));
            preferencecategory.setOrder(2000);
            preferencescreen1.addPreference(preferencecategory);
            Preference preference1 = new Preference(this);
            preference1.setLayoutResource(R.layout.label_preference);
            preference1.setTitle(getString(R.string.notifications_preference_no_network_alert));
            preference1.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

                public final boolean onPreferenceClick(Preference preference2)
                {
                    mGetNotificationsRequestId = EsService.getNotificationSettings(getBaseContext(), getAccount());
                    showDialog(0x7f0a003f);
                    return true;
                }
            });
            preferencecategory.addPreference(preference1);
        }
        updatedEnabledStates(checkboxpreference.isChecked());
    }

    private void updatedEnabledStates(boolean flag)
    {
        PreferenceScreen preferencescreen = getPreferenceScreen();
        int i = 0;
        for(int j = preferencescreen.getPreferenceCount(); i < j; i++)
            preferencescreen.getPreference(i).setEnabled(flag);

        Preference preference = findPreference(getString(R.string.notifications_preference_enabled_key));
        if(preference != null)
            preference.setEnabled(true);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            if(bundle.containsKey("pending_request_id"))
                mGetNotificationsRequestId = Integer.valueOf(bundle.getInt("pending_request_id"));
            if(bundle.containsKey("notification_settings"))
                mNotificationSettings = (NotificationSettingsData)bundle.getParcelable("notification_settings");
        }
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
    	Dialog dialog = null;
    	if(2131361855 == i) {
    		dialog = new ProgressDialog(this);
            ((ProgressDialog) (dialog)).setProgressStyle(0);
            ((ProgressDialog) (dialog)).setMessage(getString(R.string.loading));
            ((ProgressDialog) (dialog)).setCancelable(false);
    	}
    	return dialog;
    }

    public void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        EsAccount esaccount = getAccount();
        if(mGetNotificationsRequestId != null) {
        	if(!EsService.isRequestPending(mGetNotificationsRequestId.intValue()))
            {
                ServiceResult serviceresult = EsService.removeResult(mGetNotificationsRequestId.intValue());
                if(serviceresult != null && serviceresult.hasError())
                {
                    mGetNotificationsRequestId = null;
                    mNotificationSettings = null;
                    setupPreferences();
                    dismissDialog(0x7f0a003f);
                } else
                {
                    mGetNotificationsRequestId = EsService.getNotificationSettings(this, esaccount);
                }
            } 
        } else { 
        	if(mNotificationSettings == null)
            {
                mGetNotificationsRequestId = EsService.getNotificationSettings(this, esaccount);
                showDialog(0x7f0a003f);
            } else
            {
                setupPreferences();
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mGetNotificationsRequestId != null)
            bundle.putInt("pending_request_id", mGetNotificationsRequestId.intValue());
        if(mNotificationSettings != null)
            bundle.putParcelable("notification_settings", mNotificationSettings);
    }
}
