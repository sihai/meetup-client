/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.HostedEventInviteeListFragment;

/**
 * 
 * @author sihai
 *
 */
public class HostEventInviteeListActivity extends HostActivity {

	public HostEventInviteeListActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedEventInviteeListFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.ACTIVITY;
    }
}
