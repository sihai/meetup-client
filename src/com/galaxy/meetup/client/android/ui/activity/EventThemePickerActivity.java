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
import com.galaxy.meetup.client.android.ui.fragments.EsTabActivity;
import com.galaxy.meetup.client.android.ui.fragments.EventThemeListFragment;
import com.galaxy.meetup.client.android.ui.fragments.EventThemeListFragment.OnThemeSelectedListener;

/**
 * 
 * @author sihai
 *
 */
public class EventThemePickerActivity extends EsTabActivity implements
		OnThemeSelectedListener {

	public EventThemePickerActivity()
    {
        super(0, R.id.fragment_container);
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.EVENT_THEMES;
    }

    public final void onAttachFragment(Fragment fragment) {
        if(!(fragment instanceof EventThemeListFragment)) {
        	return;
        }
        
        ((EventThemeListFragment)fragment).setOnThemeSelectedListener(this);
        int index = getTabIndexForFragment(fragment);;
        if(0 == index) {
        	onAttachFragment(0, fragment);
        } else if(1 == index) {
        	onAttachFragment(1, fragment);
        }
        return;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.event_theme_picker_activity);
        addTab(0, R.id.event_themes_featured_button, R.id.event_themes_featured_fragment);
        addTab(1, R.id.event_themes_patterns_button, R.id.event_themes_patterns_fragment);
        showTitlebar(true);
        setTitlebarTitle(getString(R.string.event_picker_activity_title));
    }

    protected final Fragment onCreateTab(int i) {
    	
    	Fragment fragment = null;
    	if(0 == i) {
    		fragment = new EventThemeListFragment(0);
    	} else if(1 == i) {
    		fragment = new EventThemeListFragment(1);
    	}
    	return fragment;
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

    public final void onThemeSelected(int i, String s)
    {
        Intent intent = new Intent();
        intent.putExtra("theme_id", i);
        intent.putExtra("theme_url", s);
        setResult(-1, intent);
        finish();
    }

    protected final void onTitlebarLabelClick()
    {
        onBackPressed();
    }
}
