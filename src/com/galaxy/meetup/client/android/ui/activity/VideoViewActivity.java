/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.VideoViewFragment;

/**
 * 
 * @author sihai
 *
 */
public class VideoViewActivity extends EsFragmentActivity {

	public VideoViewActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.VIDEO;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.video_view_activity);
        if(bundle == null)
        {
            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
            Intent intent = new Intent(getIntent());
            fragmenttransaction.add(R.id.video_view_fragment_container, new VideoViewFragment(intent));
            fragmenttransaction.commit();
        }
    }

    public void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

}
