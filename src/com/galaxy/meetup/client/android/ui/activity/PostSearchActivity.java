/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.HostedPostSearchFragment;

/**
 * 
 * @author sihai
 *
 */
public class PostSearchActivity extends HostActivity {

	public PostSearchActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedPostSearchFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SEARCH;
    }

}
