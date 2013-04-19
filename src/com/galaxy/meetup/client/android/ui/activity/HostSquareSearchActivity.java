/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.HostedSquareSearchFragment;

/**
 * 
 * @author sihai
 *
 */
public class HostSquareSearchActivity extends HostActivity {

	public HostSquareSearchActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedSquareSearchFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SQUARE_SEARCH;
    }

}
