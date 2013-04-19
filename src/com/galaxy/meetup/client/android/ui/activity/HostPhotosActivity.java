/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.HostedPhotosFragment;

/**
 * 
 * @author sihai
 *
 */
public class HostPhotosActivity extends HostActivity {

	public HostPhotosActivity()
    {
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedPhotosFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTOS_LIST;
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

    protected final void onUpButtonLaunchNewTask()
    {
        TaskStackBuilder taskstackbuilder = TaskStackBuilder.create(this);
        String s = getIntent().getStringExtra("owner_id");
        if(s == null)
            s = getIntent().getStringExtra("photos_of_user_id");
        if(!getAccount().isMyGaiaId(s))
            taskstackbuilder.addNextIntent(Intents.getStreamActivityIntent(this, getAccount()));
        taskstackbuilder.addNextIntent(Intents.getProfilePhotosActivityIntent(this, getAccount(), (new StringBuilder("g:")).append(s).toString()));
        taskstackbuilder.startActivities();
    }
}
