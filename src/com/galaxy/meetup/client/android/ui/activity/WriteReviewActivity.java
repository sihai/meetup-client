/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.ui.fragments.WriteReviewFragment;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class WriteReviewActivity extends HostActivity {

	public WriteReviewActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new WriteReviewFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(!Property.ENABLE_REWIEWS.getBoolean())
        {
            finish();
            Log.e("WriteReviewActivity", "Writing reviews is not enabled yet, this activity should not be used.");
        }
    }
}
