/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.SelectSquareCategoryFragment;

/**
 * 
 * @author sihai
 *
 */
public class SelectSquareCategoryActivity extends HostActivity {

	public SelectSquareCategoryActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new SelectSquareCategoryFragment();
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
