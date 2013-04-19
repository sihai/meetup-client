/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.HostedEventFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedPhotosFragment;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;

/**
 * 
 * @author sihai
 *
 */
public class EventActivity extends HostActivity {

	private int mCurrentSpinnerIndex;
    private ArrayAdapter mPrimarySpinnerAdapter;
    
    public EventActivity()
    {
        mCurrentSpinnerIndex = 0;
    }

    protected final Fragment createDefaultFragment()
    {
        return new HostedEventFragment();
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.EVENT;
    }

    protected final void onAttachActionBar(HostActionBar hostactionbar)
    {
        super.onAttachActionBar(hostactionbar);
        hostactionbar.showPrimarySpinner(mPrimarySpinnerAdapter, mCurrentSpinnerIndex);
    }

    protected void onCreate(Bundle bundle)
    {
        mPrimarySpinnerAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item);
        mPrimarySpinnerAdapter.setDropDownViewResource(0x1090009);
        mPrimarySpinnerAdapter.add(getString(R.string.event_tab_event_text));
        mPrimarySpinnerAdapter.add(getString(R.string.event_tab_photos_text));
        if(bundle == null)
        {
            String s = getIntent().getStringExtra("notif_id");
            if(s != null)
                EsService.markNotificationAsRead(this, getAccount(), s);
        }
        super.onCreate(bundle);
    }

    public final void onPrimarySpinnerSelectionChange(int i)
    {
        super.onPrimarySpinnerSelectionChange(i);
        if(mCurrentSpinnerIndex == i) {
        	return;
        }
        
        Object obj = null;
        switch(i)
        {
        default:
            obj = null;
            break;

        case 0: // '\0'
        	obj = new HostedEventFragment();
            break; /* Loop/switch isn't completed */

        case 1: // '\001'
        	obj = new HostedPhotosFragment();
            String s = getIntent().getExtras().getString("event_id");
            if(!TextUtils.isEmpty(s))
            {
                Bundle bundle = new Bundle();
                bundle.putString("event_id", s);
                ((HostedFragment) (obj)).setArguments(bundle);
            }
            break;
        }
     
        if(obj != null)
        {
            mCurrentSpinnerIndex = i;
            replaceFragment(((Fragment) (obj)));
        }
    }

    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onRestoreInstanceState(bundle);
        mCurrentSpinnerIndex = bundle.getInt("spinnerIndex", 0);
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("spinnerIndex", mCurrentSpinnerIndex);
    }

    protected final void onUpButtonLaunchNewTask()
    {
        TaskStackBuilder taskstackbuilder = TaskStackBuilder.create(this);
        taskstackbuilder.addNextIntent(Intents.getEventsActivityIntent(this, getAccount()));
        taskstackbuilder.startActivities();
    }
}
