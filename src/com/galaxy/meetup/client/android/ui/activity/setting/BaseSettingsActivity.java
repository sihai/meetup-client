/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.activity.BaseActivity;

/**
 * 
 * @author sihai
 *
 */
public class BaseSettingsActivity extends PreferenceActivity {

	private static final OzViews VIEW;
    protected Handler mHandler;
    protected Switch mMasterSwitch;
    protected boolean mViewNavigationRecorded;

    static 
    {
        VIEW = OzViews.GENERAL_SETTINGS;
    }
    
    public BaseSettingsActivity()
    {
    }

    public void finish()
    {
        EsAccount esaccount = getAccount();
        if(esaccount != null)
            BaseActivity.recordReverseViewNavigation(this, esaccount, VIEW, null);
        super.finish();
    }

    protected final EsAccount getAccount()
    {
        android.os.Parcelable parcelable = getIntent().getParcelableExtra("account");
        EsAccount esaccount;
        if(parcelable == null)
            esaccount = null;
        else
        if(parcelable instanceof EsAccount)
            esaccount = (EsAccount)parcelable;
        else
            esaccount = null;
        return esaccount;
    }

    protected final Switch getMasterSwitch()
    {
        return mMasterSwitch;
    }

    protected final String getRingtoneName(String s, String s1, String s2)
    {
        if(s == null)
            s = PreferenceManager.getDefaultSharedPreferences(this).getString(s1, s2);
        String s3;
        if(s == null || s.length() == 0)
        {
            s3 = getString(R.string.realtimechat_settings_silent_ringtone);
        } else
        {
            Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(s));
            if(ringtone == null)
                s3 = null;
            else
                s3 = ringtone.getTitle(this);
        }
        return s3;
    }

    protected final void hookMasterSwitch(PreferenceCategory preferencecategory, final CheckBoxPreference preference)
    {
        if(android.os.Build.VERSION.SDK_INT >= 14 && preference != null && (onIsHidingHeaders() || !onIsMultiPane()))
        {
            mMasterSwitch = new Switch(this);
            ActionBar actionbar = getActionBar();
            int i = getResources().getDimensionPixelSize(R.dimen.action_bar_switch_padding);
            mMasterSwitch.setPadding(0, 0, i, 0);
            actionbar.setDisplayOptions(16, 16);
            actionbar.setCustomView(mMasterSwitch, new android.app.ActionBar.LayoutParams(-2, -2, 21));
            mMasterSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                public final void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
                {
                    android.preference.Preference.OnPreferenceChangeListener onpreferencechangelistener = preference.getOnPreferenceChangeListener();
                    if(onpreferencechangelistener == null || onpreferencechangelistener.onPreferenceChange(preference, Boolean.valueOf(flag)))
                        preference.setChecked(flag);
                }
            });
            mMasterSwitch.setChecked(preference.isChecked());
            getPreferenceScreen().removePreference(preference);
        }
    }

    public void onCreate(Bundle bundle)
    {
        boolean flag = true;
        super.onCreate(bundle);
        mHandler = new Handler(getMainLooper());
        if(android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(flag);
        if(bundle == null)
            flag = false;
        mViewNavigationRecorded = flag;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
    	
        int id = menuitem.getItemId();
        if(16908332 == id) {
        	 finish();
        	 return true;
        }
        return false;
    }

    public void onResume()
    {
        super.onResume();
        EsAccount esaccount = getAccount();
        if(!mViewNavigationRecorded && esaccount != null)
        {
            BaseActivity.recordViewNavigation(this, esaccount, VIEW);
            mViewNavigationRecorded = true;
        }
    }

    protected final void putAccountExtra(PreferenceGroup preferencegroup, EsAccount esaccount)
    {
        int i = 0;
        int j = preferencegroup.getPreferenceCount();
        while(i < j) 
        {
            Preference preference = preferencegroup.getPreference(i);
            Intent intent = preference.getIntent();
            if(intent != null)
            {
                ComponentName componentname = intent.getComponent();
                boolean flag;
                if(componentname != null && TextUtils.equals(getPackageName(), componentname.getPackageName()))
                    flag = true;
                else
                    flag = false;
                if(flag)
                    intent.putExtra("account", esaccount);
            }
            if(preference instanceof PreferenceGroup)
                putAccountExtra((PreferenceGroup)preference, esaccount);
            i++;
        }
    }

    public final void startExternalActivity(Intent intent)
    {
        intent.addFlags(0x80000);
        startActivity(intent);
    }
    
    protected final class RingtonePreferenceChangeListener implements android.preference.Preference.OnPreferenceChangeListener {

    	private String mDefaultPath;
 	    private String mKey;
 	
 	    public RingtonePreferenceChangeListener(String s, String s1)
 	    {
 	        super();
 	        mKey = s;
 	        mDefaultPath = s1;
 	    }
 	    
	    public final boolean onPreferenceChange(Preference preference, Object obj)
	    {
	        String s = getRingtoneName((String)obj, mKey, mDefaultPath);
	        if(s != null)
	            preference.setSummary(s);
	        return true;
	    }
    }
}
