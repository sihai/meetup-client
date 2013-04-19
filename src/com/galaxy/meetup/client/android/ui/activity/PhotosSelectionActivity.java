/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.PhotosSelectionFragment;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;

/**
 * 
 * @author sihai
 *
 */
public class PhotosSelectionActivity extends HostActivity {

	public PhotosSelectionActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new PhotosSelectionFragment();
    }

    protected final int getContentView()
    {
        return R.layout.photos_selection_activity;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTOS_LIST;
    }

    protected final void onAttachActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.setVisibility(8);
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
