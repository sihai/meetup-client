/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.HostedAlbumsFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedFragment;
import com.galaxy.meetup.client.android.ui.fragments.HostedProfileFragment;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;

/**
 * 
 * @author sihai
 *
 */
public class ProfileActivity extends HostActivity {

	private int mCurrentSpinnerIndex;
    private ArrayAdapter mPrimarySpinnerAdapter;
    
    public ProfileActivity()
    {
        mCurrentSpinnerIndex = 0;
    }

    public static ArrayAdapter createSpinnerAdapter(Context context)
    {
        ArrayAdapter arrayadapter = new ArrayAdapter(context, R.layout.simple_spinner_item);
        arrayadapter.setDropDownViewResource(0x1090009);
        arrayadapter.add(context.getString(R.string.profile_posts_tab_text));
        arrayadapter.add(context.getString(R.string.profile_photos_tab_text));
        return arrayadapter;
    }

    private static HostedFragment getFragmentForPosition(int i) {
    	HostedFragment fragment = null;
    	if(0 == i) {
    		fragment = new HostedProfileFragment();
    	} else if(1 == i) {
    		fragment = new HostedAlbumsFragment();
    	}
    	return fragment;
    }

    protected final Fragment createDefaultFragment()
    {
        return getFragmentForPosition(mCurrentSpinnerIndex);
    }

    protected final Bundle getExtrasForLogging() {
        String s = getIntent().getStringExtra("person_id");
        if(TextUtils.isEmpty(s)) {
        	return null; 
        } else { 
        	String s1 = EsPeopleData.extractGaiaId(s);
        	if(TextUtils.isEmpty(s1)) 
        		return null; 
        	else 
        		return EsAnalyticsData.createExtras("extra_gaia_id", s1);
        }
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.LOOP_USER;
    }

    protected final void onAttachActionBar(HostActionBar hostactionbar)
    {
        super.onAttachActionBar(hostactionbar);
        hostactionbar.showPrimarySpinner(mPrimarySpinnerAdapter, mCurrentSpinnerIndex);
    }

    public final void onAttachFragment(Fragment fragment) {
        if(!(fragment instanceof HostedProfileFragment)) {
        	if(fragment instanceof HostedAlbumsFragment)
                ((HostedAlbumsFragment)fragment).relinquishPrimarySpinner();
        } else { 
        	((HostedProfileFragment)fragment).relinquishPrimarySpinner();
        }
        super.onAttachFragment(fragment);
        return;
    }

    protected void onCreate(Bundle bundle)
    {
        mPrimarySpinnerAdapter = createSpinnerAdapter(this);
        if(bundle != null) {
        	super.onCreate(bundle);
            return; 
        }
        
        int type = getIntent().getIntExtra("profile_view_type", 0);
        if(0 == type) {
        	mCurrentSpinnerIndex = 0;
        } else if(1 == type) {
        	mCurrentSpinnerIndex = 1;
        }
        
        String s = getIntent().getStringExtra("notif_id");
        if(s != null)
            EsService.markNotificationAsRead(this, getAccount(), s);
        super.onCreate(bundle);
    }

    public final void onPrimarySpinnerSelectionChange(int i) {
        super.onPrimarySpinnerSelectionChange(i);
        if(mCurrentSpinnerIndex == i) {
        	return;
        }
        HostedFragment hostedfragment = getFragmentForPosition(i);
        if(hostedfragment != null)
        {
            mCurrentSpinnerIndex = i;
            replaceFragment(hostedfragment);
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
}
