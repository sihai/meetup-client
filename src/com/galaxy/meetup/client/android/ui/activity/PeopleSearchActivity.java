/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchFragment;

/**
 * 
 * @author sihai
 *
 */
public class PeopleSearchActivity extends EsFragmentActivity implements PeopleSearchFragment.OnSelectionChangeListener {

	private PeopleSearchFragment mSearchFragment;
	
	public PeopleSearchActivity()
    {
    }

    private boolean isPickerMode()
    {
        return getIntent().getBooleanExtra("picker_mode", false);
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PEOPLE_SEARCH;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof PeopleSearchFragment)
        {
            mSearchFragment = (PeopleSearchFragment)fragment;
            ProgressBar progressbar = (ProgressBar)findViewById(R.id.progress_spinner);
            mSearchFragment.setProgressBar(progressbar);
            mSearchFragment.setOnSelectionChangeListener(this);
            Intent intent = getIntent();
            boolean flag = getAccount().isPlusPage();
            mSearchFragment.setCircleUsageType(intent.getIntExtra("search_circles_usage", -1));
            PeopleSearchFragment peoplesearchfragment = mSearchFragment;
            boolean flag1;
            if(!isPickerMode() && !flag)
                flag1 = true;
            else
                flag1 = false;
            peoplesearchfragment.setAddToCirclesActionEnabled(flag1);
            mSearchFragment.setPublicProfileSearchEnabled(intent.getBooleanExtra("search_pub_profiles_enabled", false));
            mSearchFragment.setPhoneOnlyContactsEnabled(intent.getBooleanExtra("search_phones_enabled", false));
            mSearchFragment.setPlusPagesEnabled(intent.getBooleanExtra("search_plus_pages_enabled", false));
            mSearchFragment.setPeopleInCirclesEnabled(intent.getBooleanExtra("search_in_circles_enabled", true));
            mSearchFragment.setInitialQueryString(intent.getStringExtra("query"));
        }
    }

    public final void onCircleSelected(String s, CircleData circledata)
    {
        if(isPickerMode())
        {
            Intent intent = new Intent();
            intent.putExtra("circle_id", s);
            intent.putExtra("circle_data", circledata);
            setResult(-1, intent);
            finish();
            return;
        } else
        {
            throw new IllegalStateException();
        }
    }

    public void onClick(View view)
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.people_search_activity);
        showTitlebar(true);
        setTitlebarTitle(getString(R.string.search_people_tab_text));
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        if(16908332 == menuitem.getItemId()) {
        	goHome(getAccount());
        	return true;
        }
        return false;
    }

    public final void onPersonSelected(String s, String s1, PersonData persondata)
    {
        if(isPickerMode())
        {
            Intent intent = new Intent();
            intent.putExtra("person_id", s);
            intent.putExtra("person_data", persondata);
            setResult(-1, intent);
            finish();
        } else
        if(s1 != null)
            startExternalActivity(new Intent("android.intent.action.VIEW", Uri.withAppendedPath(android.provider.ContactsContract.Contacts.CONTENT_LOOKUP_URI, s1)));
        else
            startActivity(Intents.getProfileActivityIntent(this, getAccount(), s, null));
    }

    protected void onResume()
    {
        super.onResume();
        if(isIntentAccountActive())
        {
            if(mSearchFragment != null)
                mSearchFragment.startSearch();
        } else
        {
            finish();
        }
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(getAccount());
    }

}
