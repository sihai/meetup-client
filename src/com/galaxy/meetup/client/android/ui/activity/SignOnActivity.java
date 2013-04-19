/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.Collections;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.PlatformContractUtils;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public class SignOnActivity extends BaseAccountSelectionActivity {

	private ComponentName mCallingActivity;
	
	public SignOnActivity()
    {
    }

    public static boolean finishIfNoAccount(Activity activity, EsAccount esaccount)
    {
        boolean flag = true;
        if(esaccount != null && !esaccount.equals(EsService.getActiveAccount(activity)))
        {
            activity.setResult(0);
            if(activity.getIntent().getBooleanExtra("from_signup", false))
            {
                Intent intent = new Intent();
                intent.putExtra("no_account", flag);
                activity.setResult(0, intent);
                activity.finish();
            } else
            {
                Intent intent1 = (Intent)activity.getIntent().getParcelableExtra("intent");
                if(intent1 != null)
                {
                    activity.startActivity(intent1);
                    activity.finish();
                } else
                {
                    activity.finish();
                }
            }
        } else
        {
            flag = false;
        }
        return flag;
    }

    private void fireIntent(int i)
    {
        Intent intent = getIntent();
        String s;
        Intent intent1;
        if(mCallingActivity == null)
            s = null;
        else
            s = mCallingActivity.getPackageName();
        intent1 = Intents.getTargetIntent(this, intent, s);
        if(intent1 == null)
        {
            setResult(i);
            finish();
        } else
        if(mCallingActivity == null)
        {
            startActivity(intent1);
            finish();
        } else
        {
            intent1.putExtra("from_signup", true);
            intent1.setFlags(0xfdffffff & intent1.getFlags());
            startActivityForResult(intent1, 11);
        }
    }

    private void recordEvent(EsAccount esaccount)
    {
        Collections.emptyMap();
        String s;
        if(mCallingActivity == null)
            s = null;
        else
            s = mCallingActivity.getPackageName();
        if(s != null)
            PlatformContractUtils.getCallingPackageAnalytics(new ApiaryApiInfo(null, null, null, null, null, new ApiaryApiInfo(null, null, s, PlatformContractUtils.getCertificate(s, getPackageManager()), null)));
        EsAnalytics.recordEvent(this, esaccount, getAnalyticsInfo(), OzActions.PLATFORM_CONNECT_SELECT_ACCOUNT);
    }

    protected final String getUpgradeOrigin()
    {
        String s;
        if(Intents.getTargetIntent(this, getIntent(), null).getComponent().getClassName().equals(PlusOneActivity.class.getName()))
            s = "PLUS_ONE";
        else
            s = "DEFAULT";
        return s;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PLATFORM_THIRD_PARTY_APP;
    }

    protected final void onAccountSet(MobileOutOfBoxResponse mobileoutofboxresponse, EsAccount esaccount, AccountSettingsData accountsettingsdata)
    {
        Intent intent = Intents.getOobIntent(this, esaccount, mobileoutofboxresponse, accountsettingsdata, getUpgradeOrigin());
        if(esaccount != null)
        {
            OzActions _tmp = OzActions.PLATFORM_CONNECT_SELECT_ACCOUNT;
            recordEvent(esaccount);
            if(mobileoutofboxresponse != null)
            {
                OzActions _tmp1 = OzActions.PLATFORM_CONNECT_SHOW_OOB;
                recordEvent(esaccount);
            }
        }
        if(intent != null)
            startActivityForResult(intent, 10);
        else
            fireIntent(-1);
    }

    public void onActivityResult(int i, int j, Intent intent) {
        
    	if(10 == i) {
    		if(j == -1)
            {
                fireIntent(j);
            } else
            {
                setResult(j);
                finish();
            }
    	} else if(11 == i) {
    		if(intent != null && intent.getBooleanExtra("no_account", false))
            {
                EsAccount esaccount = EsService.getActiveAccount(this);
                if(esaccount == null || !esaccount.hasGaiaId())
                    showAccountList();
                else
                    fireIntent(j);
            } else
            {
                setResult(j, intent);
                finish();
            }
    	} else {
    		super.onActivityResult(i, j, intent);
    	}
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(bundle != null)
            mCallingActivity = (ComponentName)bundle.getParcelable("SignOnActivity#callingActivity");
        else
            mCallingActivity = getCallingActivity();
        EsAccount esaccount = EsService.getActiveAccount(this);
        if(esaccount != null && esaccount.hasGaiaId()) {
        	if(bundle == null)
                fireIntent(-1);
        	return;
        } else {
        	showAccountSelectionOrUpgradeAccount(bundle);
        	return;
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("SignOnActivity#callingActivity", mCallingActivity);
    }

}
