/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class HangoutSettingsActivity extends BaseSettingsActivity {

	public HangoutSettingsActivity()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.hangout_preferences);
        String s = getString(R.string.hangout_ringtone_setting_key);
        String s1 = getString(R.string.hangout_ringtone_setting_default_value);
        String s2 = PreferenceManager.getDefaultSharedPreferences(this).getString(s, s1);
        Preference preference = findPreference(s);
        String s3 = getRingtoneName(null, s, s2);
        preference.setOnPreferenceChangeListener(new BaseSettingsActivity.RingtonePreferenceChangeListener(s, s2));
        if(s3 != null)
            preference.setSummary(s3);
    }
}
