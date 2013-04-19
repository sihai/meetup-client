/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.EventLocationFragment;
import com.galaxy.meetup.server.client.domain.Place;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EventLocationActivity extends EsFragmentActivity implements EventLocationFragment.OnLocationSelectedListener {

	private String mInitialQuery;
	
	public EventLocationActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.LOCATION_PICKER;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof EventLocationFragment)
        {
            EventLocationFragment eventlocationfragment = (EventLocationFragment)fragment;
            eventlocationfragment.setOnLocationSelectedListener(this);
            if(mInitialQuery != null)
                eventlocationfragment.setInitialQueryString(mInitialQuery);
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle == null)
        {
            byte abyte0[] = getIntent().getByteArrayExtra("location");
            if(abyte0 != null)
                mInitialQuery = ((Place)JsonUtil.fromByteArray(abyte0, Place.class)).getName();
        }
        setContentView(R.layout.event_location_activity);
        showTitlebar(true);
        setTitlebarTitle(getString(R.string.event_location_activity_title));
    }

    public final void onLocationSelected(Place place)
    {
        Intent intent = new Intent();
        if(place != null)
            intent.putExtra("location", JsonUtil.toByteArray(place));
        setResult(-1, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        if(16908332 == menuitem.getItemId()) {
        	onBackPressed();
        	return true;
        }
        return false;
    }

    public void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected final void onTitlebarLabelClick()
    {
        onBackPressed();
    }
}
