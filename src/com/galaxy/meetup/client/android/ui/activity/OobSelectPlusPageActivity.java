/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.OobSelectPlusPageFragment;
import com.galaxy.meetup.client.android.ui.view.ActionButton;

/**
 * 
 * @author sihai
 *
 */
public class OobSelectPlusPageActivity extends OobDeviceActivity {

	private EsAccount mAccount;
    private ActionButton mContinueButton;
    private OobSelectPlusPageFragment mFragment;
    
    public OobSelectPlusPageActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        EsAccount esaccount;
        if(mAccount != null)
            esaccount = mAccount;
        else
            esaccount = super.getAccount();
        return esaccount;
    }

    public void onActivityResult(int i, int j, Intent intent) {
    	if(1 == i) {
    		if(j == 0)
            {
                EsService.removeAccount(this, getAccount());
                mAccount = null;
            }
    	}
    	super.onActivityResult(i, j, intent);
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof OobSelectPlusPageFragment)
            mFragment = (OobSelectPlusPageFragment)fragment;
    }

    public final void onContinue()
    {
        mAccount = EsAccountsData.getActiveAccount(this);
        super.onContinue();
    }

    public final void onContinuePressed()
    {
        if(mFragment != null)
            mFragment.activateAccount();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.oob_select_plus_page_activity);
        showTitlebar(false);
        setTitlebarTitle(getString(R.string.app_name));
        if(bundle != null)
            mAccount = (EsAccount)bundle.getParcelable("active_account");
    }

    protected void onPostCreate(Bundle bundle)
    {
        super.onPostCreate(bundle);
        mContinueButton = (ActionButton)findViewById(0x102001a);
        if(mFragment != null)
            setContinueButtonEnabled(mFragment.isAccountSelected());
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAccount != null)
            bundle.putParcelable("active_account", mAccount);
    }

    public final void setContinueButtonEnabled(boolean flag)
    {
        if(mContinueButton != null)
            mContinueButton.setEnabled(flag);
    }
}
