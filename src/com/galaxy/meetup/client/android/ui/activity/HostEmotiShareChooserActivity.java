/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.HostedEmotiShareChooserFragment;

/**
 * 
 * @author sihai
 *
 */
public class HostEmotiShareChooserActivity extends HostActivity {

	public HostEmotiShareChooserActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedEmotiShareChooserFragment();
    }

    protected final int getContentView()
    {
        return R.layout.host_emotishare_chooser_activity;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.COMPOSE;
    }

}
