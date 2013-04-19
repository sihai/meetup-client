/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;

/**
 * 
 * @author sihai
 *
 */
public class AddedToCircleActivity extends EsFragmentActivity {

	public AddedToCircleActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.NOTIFICATIONS_CIRCLE;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.added_to_circle_activity);
        if(bundle == null)
        {
            String s = getIntent().getStringExtra("notif_id");
            if(s != null)
                EsService.markNotificationAsRead(this, getAccount(), s);
        }
        showTitlebar(false, true);
        setTitlebarTitle(getString(R.string.added_to_circle_title));
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
    	boolean flag = false;
        if(16908332 == menuitem.getItemId()) {
        	goHome(getAccount());
            flag = true;
        }
        return flag;
    }

    public void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(getAccount());
    }
}
