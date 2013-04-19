/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.NetworkStatisticsFragment;

/**
 * 
 * @author sihai
 *
 */
public class NetworkStatisticsActivity extends EsFragmentActivity {

	private EsAccount mAccount;
	
	public NetworkStatisticsActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.network_statistics_activity);
        mAccount = (EsAccount)getIntent().getParcelableExtra("account");
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else
        {
            showTitlebar(true);
            setTitlebarTitle(getString(R.string.preferences_network_bandwidth_title));
            createTitlebarButtons(R.menu.network_statistics_menu);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.network_statistics_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        int i = menuitem.getItemId();
        boolean flag;
        if(i == 0x102002c)
        {
            goHome(mAccount);
            flag = true;
        } else
        if(i == R.id.clear || i == R.id.customize)
        {
            ((NetworkStatisticsFragment)getSupportFragmentManager().findFragmentById(R.id.network_statistics_fragment)).onMenuItemSelected(menuitem);
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(android.os.Build.VERSION.SDK_INT < 11)
            menu.findItem(R.id.clear).setVisible(false);
        return true;
    }

    public final void onPrepareTitlebarButtons(Menu menu)
    {
    }

    public void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }

}
