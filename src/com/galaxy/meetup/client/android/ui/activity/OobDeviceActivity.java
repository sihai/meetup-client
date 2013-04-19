/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.view.BottomActionBar;

/**
 * 
 * @author sihai
 *
 */
public class OobDeviceActivity extends EsFragmentActivity implements
		OnClickListener {

	public OobDeviceActivity()
    {
    }

    protected EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    public void onActivityResult(int i, int j, Intent intent) {
    	if(1 == i) {
    		if(j != 0)
            {
                setResult(j);
                finish();
                overridePendingTransition(0, 0);
            }
    	} else {
    		super.onActivityResult(i, j, intent);
    	}
    }

    public void onBackPressed()
    {
        EsAccount esaccount = getAccount();
        if(Intents.isInitialOobIntent(getIntent()))
            EsService.removeAccount(this, esaccount);
        setResult(0);
        super.onBackPressed();
    }

    public void onClick(View view) {
    	
    	// TODO 16908313, 16908314 use R.xxx instead 
        int viewId = view.getId();
        if(16908313 == viewId) {
        	onBackPressed();
        } else if(16908314 == viewId) {
        	onContinuePressed();
        }
      
    }

    public void onContinue()
    {
        Intent intent = getIntent();
        AccountSettingsData accountsettingsdata = (AccountSettingsData)intent.getParcelableExtra("plus_pages");
        Intent intent1 = Intents.getNextOobIntent(this, getAccount(), accountsettingsdata, intent);
        if(intent1 != null)
        {
            startActivityForResult(intent1, 1);
        } else
        {
            EsAccountsData.setOobComplete(this, getAccount());
            setResult(-1);
            finish();
        }
    }

    public void onContinuePressed()
    {
        onContinue();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        EsAccountsData.setHasVisitedOob(this, true);
    }

    protected void onPostCreate(Bundle bundle)
    {
        super.onPostCreate(bundle);
        EsAccount esaccount = getAccount();
        Intent intent = getIntent();
        BottomActionBar bottomactionbar = (BottomActionBar)findViewById(R.id.bottom_bar);
        AccountSettingsData accountsettingsdata = (AccountSettingsData)intent.getParcelableExtra("plus_pages");
        if(!Intents.isInitialOobIntent(intent))
            bottomactionbar.addButton(0x1020019, R.string.signup_back, this);
        if(!Intents.isLastOobIntent(this, esaccount, accountsettingsdata, intent))
            bottomactionbar.addButton(0x102001a, R.string.signup_continue, this);
        else
            bottomactionbar.addButton(0x102001a, R.string.signup_done, this);
    }

}
