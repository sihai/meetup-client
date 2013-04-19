/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.HostedFragment;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.HostActionBar.HostActionBarListener;
import com.galaxy.meetup.client.android.ui.view.HostActionBar.OnUpButtonClickListener;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public abstract class HostActivity extends BaseActivity implements
		HostActionBarListener, OnUpButtonClickListener {

	private HostActionBar mActionBar;
    private HostedFragment mHostedFragment;
    
    public HostActivity()
    {
    }

    private void attachActionBar()
    {
        if(mActionBar != null)
        {
            mActionBar.reset();
            mHostedFragment.attachActionBar(mActionBar);
            onAttachActionBar(mActionBar);
            mActionBar.commit();
        }
    }

    protected EsAccount getAccount()
    {
        return (EsAccount)getIntent().getExtras().getParcelable("account");
    }

    protected int getContentView()
    {
        return R.layout.host_activity;
    }

    protected final int getDefaultFragmentContainerViewId()
    {
        return R.id.fragment_container;
    }

    protected final boolean isIntentAccountActive()
    {
        EsAccount esaccount = (EsAccount)getIntent().getParcelableExtra("account");
        boolean flag = false;
        if(esaccount != null)
            if(!esaccount.equals(EsService.getActiveAccount(this)))
            {
                boolean flag1 = EsLog.isLoggable("HostActivity", 6);
                flag = false;
                if(flag1)
                    Log.e("HostActivity", (new StringBuilder("Activity finished because it is associated with a signed-out account: ")).append(getClass().getName()).toString());
            } else
            {
                flag = true;
            }
        return flag;
    }

    public final void onActionBarInvalidated()
    {
        attachActionBar();
    }

    public final void onActionButtonClicked(int i)
    {
        if(mHostedFragment != null)
            mHostedFragment.onActionButtonClicked(i);
    }

    protected void onAttachActionBar(HostActionBar hostactionbar)
    {
    }

    public void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof HostedFragment)
        {
            mHostedFragment = (HostedFragment)fragment;
            attachActionBar();
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(getContentView());
        mActionBar = (HostActionBar)findViewById(R.id.title_bar);
        mActionBar.setOnUpButtonClickListener(this);
        mActionBar.setHostActionBarListener(this);
        mActionBar.setUpButtonContentDescription(getString(R.string.nav_up_content_description));
        if(mHostedFragment != null)
            attachActionBar();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.host_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(mHostedFragment != null && mHostedFragment.onOptionsItemSelected(menuitem))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        HostedFragment hostedfragment = mHostedFragment;
        boolean flag = false;
        if(hostedfragment != null)
        {
            int i = menu.size();
            for(int j = 0; j < i; j++)
                menu.getItem(j).setVisible(false);

            mHostedFragment.onPrepareOptionsMenu(menu);
            flag = true;
        }
        return flag;
    }

    public void onPrimarySpinnerSelectionChange(int i)
    {
        if(mHostedFragment != null)
            mHostedFragment.onPrimarySpinnerSelectionChange(i);
    }

    public final void onRefreshButtonClicked()
    {
        if(mHostedFragment != null)
            mHostedFragment.refresh();
    }

    protected void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    public void onUpButtonClick()
    {
        if(getIntent().getBooleanExtra("from_url_gateway", false) || getIntent().getBooleanExtra("com.google.plus.analytics.intent.extra.FROM_NOTIFICATION", false))
        {
            onUpButtonLaunchNewTask();
            finish();
        } else
        {
            onBackPressed();
        }
    }

    protected void onUpButtonLaunchNewTask()
    {
        TaskStackBuilder taskstackbuilder = TaskStackBuilder.create(this);
        taskstackbuilder.addNextIntent(Intents.getStreamActivityIntent(this, getAccount()));
        taskstackbuilder.startActivities();
    }

    protected final void replaceFragment(Fragment fragment)
    {
        super.replaceFragment(fragment);
        if(mHostedFragment != null)
            mHostedFragment.recordNavigationAction();
    }
}
