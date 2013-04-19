/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.LocalReviewFragment;

/**
 * 
 * @author sihai
 *
 */
public class LocalReviewActivity extends HostActivity {

	public LocalReviewActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new LocalReviewFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.ACTIVITY;
    }

}
