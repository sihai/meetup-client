/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;

/**
 * 
 * @author sihai
 *
 */
public class OobSuggestedPeopleActivity extends OobDeviceActivity {

	public OobSuggestedPeopleActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.OOB_ADD_PEOPLE_VIEW;
    }

    public final void onContinue()
    {
        super.onContinue();
        EsAccountsData.setWarmWelcomeTimestamp(this, getAccount(), System.currentTimeMillis(), false);
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.oob_suggested_people_activity);
        String s = getString(R.string.app_name);
        showTitlebar(false);
        setTitlebarTitle(s);
        createTitlebarButtons(R.menu.suggested_people_menu);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.suggested_people_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.search)
        {
            onSearchRequested();
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if(android.os.Build.VERSION.SDK_INT < 11)
            menu.findItem(R.id.search).setVisible(false);
        return true;
    }

    protected final void onPrepareTitlebarButtons(Menu menu)
    {
        for(int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);

        menu.findItem(R.id.search).setVisible(true);
    }

    public boolean onSearchRequested()
    {
        startActivity(Intents.getPeopleSearchActivityIntent(this, getAccount(), false, -1, true, false, true, false, false));
        return true;
    }
}
