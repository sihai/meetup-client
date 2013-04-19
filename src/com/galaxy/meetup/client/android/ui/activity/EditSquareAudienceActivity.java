/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.EditSquareAudienceFragment;

/**
 * 
 * @author sihai
 *
 */
public class EditSquareAudienceActivity extends HostActivity {

	public EditSquareAudienceActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new EditSquareAudienceFragment();
    }

    protected final int getContentView()
    {
        return R.layout.edit_square_audience_activity;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PEOPLE_PICKER;
    }

    protected void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

}
