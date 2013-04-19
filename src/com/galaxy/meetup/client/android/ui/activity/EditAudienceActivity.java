/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.fragments.EditAudienceFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.EditAudienceFragment.OnAudienceChangeListener;

/**
 * 
 * @author sihai
 *
 */
public class EditAudienceActivity extends EsFragmentActivity implements
		OnClickListener, OnAudienceChangeListener {

	private EditAudienceFragment mEditAudienceFragment;
    private View mPositiveButton;
    private boolean mShakeDetectorWasRunning;
    
    public EditAudienceActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PEOPLE_PICKER;
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 0 && j == -1 && mEditAudienceFragment != null)
        {
            String s = intent.getStringExtra("person_id");
            if(s != null)
            {
                PersonData persondata = (PersonData)intent.getParcelableExtra("person_data");
                mEditAudienceFragment.addSelectedPerson(s, persondata);
            }
            String s1 = intent.getStringExtra("circle_id");
            if(s1 != null)
            {
                CircleData circledata = (CircleData)intent.getParcelableExtra("circle_data");
                mEditAudienceFragment.addSelectedCircle(s1, circledata);
            }
        }
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof EditAudienceFragment)
        {
            mEditAudienceFragment = (EditAudienceFragment)fragment;
            mEditAudienceFragment.setOnSelectionChangeListener(this);
            mEditAudienceFragment.setCircleSelectionEnabled(true);
            mEditAudienceFragment.setCircleUsageType(getIntent().getIntExtra("circle_usage_type", 0));
            mEditAudienceFragment.setIncludePlusPages(getIntent().getBooleanExtra("search_plus_pages_enabled", true));
            mEditAudienceFragment.setFilterNullGaiaIds(getIntent().getBooleanExtra("filter_null_gaia_ids", false));
            mEditAudienceFragment.setIncomingAudienceIsReadOnly(getIntent().getBooleanExtra("audience_is_read_only", false));
        }
    }

    public final void onAudienceChanged(String s)
    {
        if(mPositiveButton != null && mEditAudienceFragment != null)
            mPositiveButton.setEnabled(mEditAudienceFragment.isSelectionValid());
    }

    public void onClick(View view)
    {
        int i = view.getId();
        if(i != R.id.ok) {
        	if(i == R.id.cancel)
                finish(); 
        } else { 
        	Intent intent = new Intent();
            intent.putExtra("audience", mEditAudienceFragment.getAudience());
            setResult(-1, intent);
            finish();
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.edit_audience_activity);
        String s = getIntent().getStringExtra("title");
        showTitlebar(true, false);
        setTitlebarTitle(s);
        createTitlebarButtons(R.menu.edit_audience_menu);
        mPositiveButton = findViewById(R.id.ok);
        if(mEditAudienceFragment != null)
            mPositiveButton.setEnabled(mEditAudienceFragment.isSelectionValid());
        mPositiveButton.setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
        if(shakedetector != null)
            mShakeDetectorWasRunning = shakedetector.stop();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_audience_menu, menu);
        return true;
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if(mShakeDetectorWasRunning)
        {
            ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
            if(shakedetector != null)
                shakedetector.start();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == 0x102002c)
            goHome(getAccount());
        else
        if(i == R.id.search)
            onSearchRequested();
        else
            flag = false;
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.search).setVisible(false);
        return menu.hasVisibleItems();
    }

    protected void onResume()
    {
        super.onResume();
        if(isIntentAccountActive())
        {
            if(!mEditAudienceFragment.hasAudience())
            {
                AudienceData audiencedata = (AudienceData)getIntent().getParcelableExtra("audience");
                if(audiencedata != null)
                    mEditAudienceFragment.setAudience(audiencedata);
            }
        } else
        {
            finish();
        }
    }

    public boolean onSearchRequested()
    {
        boolean flag = getIntent().getBooleanExtra("search_phones_enabled", false);
        boolean flag1 = getIntent().getBooleanExtra("search_plus_pages_enabled", false);
        boolean flag2 = getIntent().getBooleanExtra("search_pub_profiles_enabled", false);
        int i = getIntent().getIntExtra("circle_usage_type", -1);
        startActivityForResult(Intents.getPeopleSearchActivityIntent(this, getAccount(), true, i, flag2, flag, flag1, true, getIntent().getBooleanExtra("filter_null_gaia_ids", false)), 0);
        return true;
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(getAccount());
    }

}
