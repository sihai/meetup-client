/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.NetworkTransactionsListFragment;

/**
 * 
 * @author sihai
 *
 */
public class NetworkTransactionsActivity extends EsFragmentActivity {

	private EsAccount mAccount;
	
	public NetworkTransactionsActivity()
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

    public void onAttachedToWindow()
    {
        ProgressBar progressbar;
        if(android.os.Build.VERSION.SDK_INT < 11)
            progressbar = (ProgressBar)findViewById(R.id.progress_spinner);
        else
            progressbar = (ProgressBar)findViewById(R.id.action_bar_progress_spinner_view);
        ((NetworkTransactionsListFragment)getSupportFragmentManager().findFragmentById(R.id.network_transactions_fragment)).setProgressBar(progressbar);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.network_transactions);
        mAccount = (EsAccount)getIntent().getParcelableExtra("account");
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else
        {
            showTitlebar(true);
            setTitlebarTitle(getString(R.string.preferences_network_transactions_title));
            createTitlebarButtons(R.menu.network_transactions_menu);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.network_transactions_menu, menu);
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
        if(i == R.id.clear)
        {
            ((NetworkTransactionsListFragment)getSupportFragmentManager().findFragmentById(R.id.network_transactions_fragment)).clear();
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
