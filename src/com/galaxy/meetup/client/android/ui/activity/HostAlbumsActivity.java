/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.HostedAlbumsFragment;

/**
 * 
 * @author sihai
 *
 */
public class HostAlbumsActivity extends HostActivity {

	public HostAlbumsActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedAlbumsFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTOS_LIST;
    }

}
