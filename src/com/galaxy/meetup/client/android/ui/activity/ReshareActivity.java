/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.ReshareFragment;

/**
 * 
 * @author sihai
 *
 */
public class ReshareActivity extends EsFragmentActivity {

	private EsAccount mAccount;
    private ReshareFragment mReshareFragment;
    private boolean mShakeDetectorWasRunning;
    
    public ReshareActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.RESHARE;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment.getId() == R.id.reshare_fragment)
            mReshareFragment = (ReshareFragment)fragment;
    }

    public void onBackPressed()
    {
        mReshareFragment.onDiscard();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.reshare_activity);
        mAccount = (EsAccount)getIntent().getParcelableExtra("account");
        showTitlebar(true);
        setTitlebarTitle(getString(R.string.reshare_title));
        createTitlebarButtons(R.menu.post_menu);
        ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
        if(shakedetector != null)
            mShakeDetectorWasRunning = shakedetector.stop();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.post_menu, menu);
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
            goHome(mAccount);
        else
        if(i == R.id.menu_post)
            mReshareFragment.reshare();
        else
        if(i == R.id.menu_discard)
            mReshareFragment.onDiscard();
        else
            flag = super.onOptionsItemSelected(menuitem);
        return flag;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.menu_post).setVisible(false);
        return true;
    }

    protected final void onPrepareTitlebarButtons(Menu menu)
    {
        int i = 0;
        while(i < menu.size()) 
        {
            MenuItem menuitem = menu.getItem(i);
            boolean flag;
            if(menuitem.getItemId() == R.id.menu_post)
                flag = true;
            else
                flag = false;
            menuitem.setVisible(flag);
            i++;
        }
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
