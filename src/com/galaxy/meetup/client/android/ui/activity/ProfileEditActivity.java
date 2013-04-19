/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.ProfileEditFragment;

/**
 * 
 * @author sihai
 *
 */
public class ProfileEditActivity extends BaseActivity {

	private EsAccount mAccount;
    private ProfileEditFragment mFragment;
    
    public ProfileEditActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        mFragment = new ProfileEditFragment();
        return mFragment;
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PROFILE;
    }

    public void onBackPressed()
    {
        if(mFragment != null)
            mFragment.onDiscard();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mAccount = (EsAccount)getIntent().getParcelableExtra("account");
    }
}
