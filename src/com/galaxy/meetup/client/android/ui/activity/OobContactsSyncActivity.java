/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.OobContactsSyncFragment;

/**
 * 
 * @author sihai
 *
 */
public class OobContactsSyncActivity extends OobDeviceActivity {

	private OobContactsSyncFragment mSyncFragment;
	
	public OobContactsSyncActivity()
    {
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.OOB_IMPROVE_CONTACTS_VIEW;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof OobContactsSyncFragment)
            mSyncFragment = (OobContactsSyncFragment)fragment;
    }

    public final void onContinuePressed()
    {
        if(mSyncFragment != null)
        {
            mSyncFragment.commit();
            super.onContinuePressed();
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.oob_contacts_sync_activity);
        showTitlebar(false);
        setTitlebarTitle(getString(R.string.app_name));
    }

}
