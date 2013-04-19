/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class AboutSettingsActivity extends BaseSettingsActivity implements
		OnClickListener {

	private static final Uri PRIVACY = Uri.parse("http://m.google.com/app/plus/serviceurl?type=privacy");
    private static final Uri TERMS = Uri.parse("http://m.google.com/app/plus/serviceurl?type=tos");
    private static String sLicenseKey;
    private static String sNetworkStatsKey;
    private static String sNetworkTransactionsKey;
    private static String sPrivacyKey;
    private static String sRemoveAccountKey;
    private static String sTermsKey;
    
    public AboutSettingsActivity()
    {
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
        if(-1 == i) {
        	 Intent intent = Intents.getHostNavigationActivityIntent(this, getAccount());
             intent.putExtra("sign_out", true);
             intent.setFlags(0x4000000);
             startActivity(intent);
             finish();
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(sLicenseKey == null)
        {
            sLicenseKey = getString(R.string.license_preference_key);
            sPrivacyKey = getString(R.string.privacy_policy_preference_key);
            sTermsKey = getString(R.string.terms_of_service_preference_key);
            sRemoveAccountKey = getString(R.string.remove_account_preference_key);
            sNetworkTransactionsKey = getString(R.string.network_transactions_preference_key);
            sNetworkStatsKey = getString(R.string.network_stats_preference_key);
        }
        if(EsLog.ENABLE_DOGFOOD_FEATURES)
            addPreferencesFromResource(R.xml.network_stats_preferences);
        addPreferencesFromResource(R.xml.about_preferences);
        final EsAccount account;
        try
        {
            PackageInfo packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            findPreference("build_version").setSummary(packageinfo.versionName);
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            findPreference("build_version").setSummary("?");
        }
        account = getAccount();
        if(EsLog.ENABLE_DOGFOOD_FEATURES)
        {
            findPreference(sNetworkTransactionsKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

                public final boolean onPreferenceClick(Preference preference)
                {
                    Intent intent = Intents.getNetworkRequestsIntent(AboutSettingsActivity.this, account);
                    startActivity(intent);
                    return true;
                }
            });
            findPreference(sNetworkStatsKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

                public final boolean onPreferenceClick(Preference preference)
                {
                    Intent intent = Intents.getNetworkStatisticsIntent(AboutSettingsActivity.this, account);
                    startActivity(intent);
                    return true;
                }
            });
        }
        findPreference(sLicenseKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference)
            {
                Intent intent = Intents.getLicenseActivityIntent(AboutSettingsActivity.this);
                startActivity(intent);
                return true;
            }
        });
        findPreference(sPrivacyKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference)
            {
                startExternalActivity(new Intent("android.intent.action.VIEW", AboutSettingsActivity.PRIVACY));
                return true;
            }
        });
        findPreference(sTermsKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference)
            {
                EsAccount esaccount = getAccount();
                if(esaccount != null)
                {
                    android.content.Context context = getBaseContext();
                    OzViews ozviews = OzViews.getViewForLogging(context);
                    EsAnalytics.recordActionEvent(context, esaccount, OzActions.SETTINGS_TOS, ozviews);
                }
                startExternalActivity(new Intent("android.intent.action.VIEW", AboutSettingsActivity.TERMS));
                return true;
            }

        });
        findPreference(sRemoveAccountKey).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {

            public final boolean onPreferenceClick(Preference preference)
            {
                showDialog(0);
                return true;
            }

        });
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
    	Dialog dialog = null;
    	if(0 == i) {
    		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.preferences_remove_account_title);
            builder.setMessage(R.string.preferences_remove_account_dialog_message);
            builder.setPositiveButton(R.string.ok, this);
            builder.setNegativeButton(R.string.cancel, this);
            dialog = builder.create();
    	}
    	return dialog;
    }
}
