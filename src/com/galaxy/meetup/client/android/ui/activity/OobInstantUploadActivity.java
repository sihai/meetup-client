/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.OobInstantUploadFragment;

/**
 * 
 * @author sihai
 *
 */
public class OobInstantUploadActivity extends OobDeviceActivity {

	private OobInstantUploadFragment mFragment;
	
	public OobInstantUploadActivity()
    {
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.OOB_CAMERA_SYNC;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof OobInstantUploadFragment)
            mFragment = (OobInstantUploadFragment)fragment;
    }

    public final void onContinuePressed()
    {
        if(mFragment != null && mFragment.commit())
            super.onContinuePressed();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.oob_instant_upload_activity);
        showTitlebar(false);
        setTitlebarTitle(getString(R.string.app_name));
    }

}
