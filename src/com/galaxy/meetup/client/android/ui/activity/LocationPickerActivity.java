/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.LocationPickerFragment;

/**
 * 
 * @author sihai
 *
 */
public class LocationPickerActivity extends HostActivity implements
		OnCancelListener, OnClickListener {

	private LocationPickerFragment mLocationPickerFragment;
	
	public LocationPickerActivity()
    {
		System.out.println("test");
    }

    protected final Fragment createDefaultFragment()
    {
        return new LocationPickerFragment();
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    protected final int getContentView()
    {
        return R.layout.location_picker_activity;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.LOCATION_PICKER;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        if(fragment instanceof LocationPickerFragment)
        {
            mLocationPickerFragment = (LocationPickerFragment)fragment;
            mLocationPickerFragment.setSearchMode(false);
        }
    }

    public void onBackPressed()
    {
        if(mLocationPickerFragment == null || !mLocationPickerFragment.onBackPressed())
            super.onBackPressed();
    }

    public void onCancel(DialogInterface dialoginterface)
    {
        finish();
    }

    public void onClick(DialogInterface dialoginterface, int i) {
    	if(-2 == i) {
    		finish();
    	} else if(-1 == i) {
    		startActivity(Intents.getLocationSettingActivityIntent());
    	}
    	dialoginterface.dismiss();
        return;
    }

    public Dialog onCreateDialog(int i, Bundle bundle) {
    	Dialog dialog = null;
    	if(29341608 == i) {
    		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.location_provider_disabled).setPositiveButton(R.string.yes, this).setNegativeButton(R.string.no, this);
            builder.setOnCancelListener(this);
            dialog = builder.create();
    	}
        return dialog;
    }

    public void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    public final void onUpButtonClick()
    {
        if(mLocationPickerFragment == null || !mLocationPickerFragment.onUpButtonClicked())
            super.onUpButtonClick();
    }

}
