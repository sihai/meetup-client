/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.PhotoPickerFragment;

/**
 * 
 * @author sihai
 *
 */
public class PhotoPickerActivity extends EsFragmentActivity {

	private int mCropMode;
    private String mDisplayName;
    
	public PhotoPickerActivity()
    {
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PHOTO_PICKER;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.photo_picker_activity);
        if(bundle == null)
        {
            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
            Intent intent = new Intent(getIntent());
            fragmenttransaction.add(R.id.photo_picker_fragment_container, new PhotoPickerFragment(intent));
            fragmenttransaction.commit();
        }
        mDisplayName = getIntent().getStringExtra("display_name");
        mCropMode = getIntent().getIntExtra("photo_picker_crop_mode", 0);
        String s;
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else
        {
            showTitlebar(true);
            createTitlebarButtons(R.menu.album_view_menu);
        }
        if(mCropMode != 0)
            s = getString(R.string.photo_picker_sublabel);
        else
            s = mDisplayName;
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            ActionBar actionbar = getActionBar();
            actionbar.setTitle(s);
            actionbar.setSubtitle(null);
        } else
        {
            setTitlebarTitle(s);
            setTitlebarSubtitle(null);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        int id = menuitem.getItemId();
        if(16908332 == id) {
        	finish();
        	return true;
        }
        return false;
    }

    protected final void onPrepareTitlebarButtons(Menu menu)
    {
        for(int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);

    }

    protected void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected final void onTitlebarLabelClick()
    {
        finish();
    }

}
