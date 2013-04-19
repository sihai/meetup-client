/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity.setting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceResult;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class MessengerSettingsActivity extends BaseSettingsActivity {

	private Integer mAclSummaryToSet;
    private String mAclValueToSet;
    private String mCurrentBackend;
    private Integer mRequestId;
    private final RealTimeChatServiceListener mServiceListener = new RealTimeChatServiceListener() {

        public final void onResponseReceived(int i, RealTimeChatServiceResult realtimechatserviceresult)
        {
            if(mRequestId != null && i == mRequestId.intValue())
                if(realtimechatserviceresult.getErrorCode() == 1)
                {
                    processSetAclResult(realtimechatserviceresult.getCommand());
                } else
                {
                    if(EsLog.isLoggable("MessengerSettings", 4))
                        Log.i("MessengerSettings", (new StringBuilder("Error setting acl ")).append(realtimechatserviceresult.getErrorCode()).toString());
                    dismissDialog(1);
                    showDialog(2);
                }
        }

    };
    private Runnable mTimeoutRunnable;
    
    public MessengerSettingsActivity()
    {
    }

    private void processSetAclResult(Client.BunchServerResponse bunchserverresponse)
    {
        mRequestId = null;
        if(!bunchserverresponse.hasSetAclsResponse()) {
        	dismissDialog(1);
            showDialog(2);
        } else { 
        	Client.SetAclsResponse setaclsresponse = bunchserverresponse.getSetAclsResponse();
        	if(setaclsresponse == null || !setaclsresponse.hasStatus() || setaclsresponse.getStatus() != Data.ResponseStatus.OK) {
        		dismissDialog(1);
                showDialog(2);
        	} else { 
        		ListPreference listpreference = (ListPreference)findPreference(getString(R.string.realtimechat_acl_key));
                listpreference.setValue(mAclValueToSet);
                listpreference.setSummary(mAclSummaryToSet.intValue());
                android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString(getString(R.string.realtimechat_acl_key), mAclValueToSet);
                editor.commit();
                dismissDialog(1);
                mHandler.removeCallbacks(mTimeoutRunnable);
        	}
        }
    }

    public void onCreate(Bundle bundle)
    {
        String s;
        String s1;
        int i;
        super.onCreate(bundle);
        if(bundle != null)
        {
            if(bundle.containsKey("request_id"))
                mRequestId = Integer.valueOf(bundle.getInt("request_id"));
            if(bundle.containsKey("acl_value"))
                mAclValueToSet = bundle.getString("acl_value");
            if(bundle.containsKey("acl_summary_string_id"))
                mAclSummaryToSet = Integer.valueOf(bundle.getInt("acl_summary_string_id"));
        }
        addPreferencesFromResource(R.xml.realtimechat_preferences);
        if(RealTimeChatService.debuggable())
        {
            addPreferencesFromResource(R.xml.realtimechat_development_preferences);
            findPreference(getString(R.string.realtimechat_backend_key)).setOnPreferenceChangeListener(new BackendPreferenceChangeListener());
            mCurrentBackend = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.realtimechat_backend_key), getString(R.string.debug_realtimechat_default_backend));
        }
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        s = getString(R.string.realtimechat_acl_key);
        s1 = sharedpreferences.getString(s, getString(R.string.realtimechat_default_acl_key));
        i = -1;
        if(s1.equals(getString(R.string.key_acl_setting_anyone))) {
        	i = R.string.realtimechat_acl_subtitle_anyone;
        } else if(s1.equals(getString(R.string.key_acl_setting_my_circles))) {
        	i = R.string.realtimechat_acl_subtitle_my_circles;
        } else if(s1.equals(getString(R.string.key_acl_setting_extended_circles))) {
                i = R.string.realtimechat_acl_subtitle_extended_circles;
        }
        Preference preference = findPreference(s);
        preference.setOnPreferenceChangeListener(new AclPreferenceChangeListener());
        if(i != -1)
            preference.setSummary(i);
        String s2 = getString(R.string.realtimechat_ringtone_setting_key);
        Preference preference1 = findPreference(s2);
        String s3 = getString(R.string.notifications_preference_ringtone_default_value);
        String s4 = getRingtoneName(null, s2, s3);
        preference1.setOnPreferenceChangeListener(new BaseSettingsActivity.RingtonePreferenceChangeListener(s2, s3));
        if(s4 != null)
            preference1.setSummary(s4);
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
    	Dialog dialog = null;
        if(i == 1) {
        	dialog = new ProgressDialog(this);
            ((ProgressDialog) (dialog)).setTitle(getString(R.string.realtimechat_acl_update_pending_title));
            ((ProgressDialog) (dialog)).setMessage(getString(R.string.realtimechat_acl_update_pending));
            ((ProgressDialog) (dialog)).setCancelable(false);
            ((ProgressDialog) (dialog)).setCanceledOnTouchOutside(false);
        } else if(2 == i) {
        	android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.realtimechat_acl_update_failed_title));
            builder.setMessage(getString(R.string.realtimechat_acl_update_failed));
            builder.setPositiveButton(0x104000a, null);
            dialog = builder.create();
        }
        
        return dialog;
    }

    public void onPause()
    {
        super.onPause();
        mHandler.removeCallbacks(mTimeoutRunnable);
        RealTimeChatService.unregisterListener(mServiceListener);
    }

    public void onResume()
    {
        super.onResume();
        RealTimeChatService.registerListener(mServiceListener);
        if(mRequestId != null)
            if(!RealTimeChatService.isRequestPending(mRequestId.intValue()))
            {
                RealTimeChatServiceResult realtimechatserviceresult = RealTimeChatService.removeResult(mRequestId.intValue());
                if(realtimechatserviceresult != null && realtimechatserviceresult.getCommand() != null)
                {
                    processSetAclResult(realtimechatserviceresult.getCommand());
                } else
                {
                    dismissDialog(1);
                    showDialog(2);
                }
            } else
            {
                mHandler.postDelayed(mTimeoutRunnable, 10000L);
            }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mRequestId != null)
            bundle.putInt("request_id", mRequestId.intValue());
        if(mAclValueToSet != null)
            bundle.putString("acl_value", mAclValueToSet);
        if(mAclSummaryToSet != null)
            bundle.putInt("acl_summary_string_id", mAclSummaryToSet.intValue());
    }
    
    final class AclPreferenceChangeListener implements android.preference.Preference.OnPreferenceChangeListener {

	    public final boolean onPreferenceChange(Preference preference, Object obj)
	    {
	        if(obj instanceof String)
	        {
	            String s = (String)obj;
	            byte byte0 = -1;
	            int i;
	            if(s.equals(getString(R.string.key_acl_setting_anyone)))
	            {
	                byte0 = 1;
	                i = R.string.realtimechat_acl_subtitle_anyone;
	            } else
	            if(s.equals(getString(R.string.key_acl_setting_my_circles)))
	            {
	                byte0 = 3;
	                i = R.string.realtimechat_acl_subtitle_my_circles;
	            } else
	            {
	                boolean flag = s.equals(getString(R.string.key_acl_setting_extended_circles));
	                i = 0;
	                if(flag)
	                {
	                    byte0 = 2;
	                    i = R.string.realtimechat_acl_subtitle_extended_circles;
	                }
	            }
	            if(byte0 != -1)
	            {
	                mAclValueToSet = s;
	                mAclSummaryToSet = Integer.valueOf(i);
	                if(EsLog.isLoggable("MessengerSettings", 3))
	                    Log.d("MessengerSettings", (new StringBuilder("Changing acl to ")).append(obj).toString());
	                EsAccount esaccount = getAccount();
	                mRequestId = Integer.valueOf(RealTimeChatService.setAcl(MessengerSettingsActivity.this, esaccount, byte0));
	                showDialog(1, null);
	                mTimeoutRunnable = new TimeoutRunnable();
	                mHandler.postDelayed(mTimeoutRunnable, 10000L);
	            } else
	            if(EsLog.isLoggable("MessengerSettings", 5))
	                Log.w("MessengerSettings", (new StringBuilder("Invalid ACL value (")).append(s).append(")").toString());
	        }
	        return false;
	    }
    }

    private final class BackendPreferenceChangeListener implements android.preference.Preference.OnPreferenceChangeListener {

	    public final boolean onPreferenceChange(Preference preference, Object obj)
	    {
	        if((obj instanceof String) && !mCurrentBackend.equals(obj))
	        {
	            EsAccount esaccount = getAccount();
	            RealTimeChatService.logout(MessengerSettingsActivity.this, esaccount);
	        }
	        return true;
	    }
    }

    final class TimeoutRunnable implements Runnable {

    	public final void run()
    	{
    		dismissDialog(1);
    		showDialog(2);
    	}
    }
}
