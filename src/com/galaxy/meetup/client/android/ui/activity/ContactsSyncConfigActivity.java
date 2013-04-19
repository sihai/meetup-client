/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.OobContactsSyncFragment;
import com.galaxy.meetup.client.android.ui.view.BottomActionBar;

/**
 * 
 * @author sihai
 *
 */
public class ContactsSyncConfigActivity extends EsFragmentActivity implements
		OnClickListener {

	private OobContactsSyncFragment mFragment;
	
	public ContactsSyncConfigActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CONTACTS_SYNC_CONFIG;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof OobContactsSyncFragment)
            mFragment = (OobContactsSyncFragment)fragment;
    }

    public void onClick(View view)
    {
        if(view.getId() == 0x1020019 && mFragment != null)
        {
            mFragment.commit();
            finish();
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.oob_contacts_sync_activity);
        ((BottomActionBar)findViewById(R.id.bottom_bar)).addButton(0x1020019, R.string.signup_done, this);
        showTitlebar(false);
        setTitlebarTitle(getString(R.string.app_name));
    }
}
