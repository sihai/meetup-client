/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.HostedSquareStreamFragment;

/**
 * 
 * @author sihai
 *
 */
public class HostSquareStreamActivity extends HostActivity {

	public HostSquareStreamActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedSquareStreamFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SQUARE_LANDING;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle == null)
        {
            String s = getIntent().getStringExtra("notif_id");
            if(!TextUtils.isEmpty(s))
                EsService.markNotificationAsRead(this, getAccount(), s);
        }
    }
}
