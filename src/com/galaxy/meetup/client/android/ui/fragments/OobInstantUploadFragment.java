/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.ui.activity.OobDeviceActivity;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog.AlertDialogListener;

/**
 * 
 * @author sihai
 *
 */
public class OobInstantUploadFragment extends Fragment implements
		AlertDialogListener {

	private RadioGroup mUploadChoice;
	
	public OobInstantUploadFragment()
    {
    }

    private void doNextStep()
    {
        int i = mUploadChoice.getCheckedRadioButtonId();
        final boolean wifiOnly;
        final boolean enabled;
        final OobDeviceActivity context;
        final EsAccount account;
        OzActions ozactions;
        if(i == R.id.option_wifi_only)
        {
            enabled = true;
            wifiOnly = true;
        } else
        if(i == R.id.option_wifi_and_mobile)
        {
            enabled = true;
            wifiOnly = false;
        } else
        {
            wifiOnly = true;
            enabled = false;
        }
        context = (OobDeviceActivity)getActivity();
        account = (EsAccount)context.getIntent().getParcelableExtra("account");
        (new AsyncTask() {

            protected final Object doInBackground(Object aobj[])
            {
                EsAccountsData.saveInstantUploadPhotoWifiOnly(context, account, wifiOnly);
                EsAccountsData.saveInstantUploadVideoWifiOnly(context, account, true);
                EsAccountsData.saveInstantUploadEnabled(context, account, enabled);
                InstantUpload.setPhotoWiFiOnlySetting(context, wifiOnly);
                InstantUpload.setVideoWiFiOnlySetting(context, true);
                InstantUpload.enableInstantUpload(context, account, enabled);
                return null;
            }

        }).execute((Object[])null);
        if(enabled)
        {
            if(wifiOnly)
                ozactions = OzActions.CAMERA_SYNC_WIFI_ONLY_OPTED_IN;
            else
                ozactions = OzActions.CAMERA_SYNC_OPTED_IN;
        } else
        {
            ozactions = OzActions.CAMERA_SYNC_OPTED_OUT;
        }
        EsAnalytics.recordActionEvent(context, account, ozactions, OzViews.OOB_CAMERA_SYNC);
    }

    public final boolean commit()
    {
        boolean flag;
        boolean flag2;
        flag = true;
        int i;
        boolean flag1;
        if(mUploadChoice.getCheckedRadioButtonId() == R.id.option_disable)
            i = ((flag) ? 1 : 0);
        else
            i = 0;
        flag1 = InstantUpload.isSyncEnabled((EsAccount)getActivity().getIntent().getParcelableExtra("account"));
        flag2 = ContentResolver.getMasterSyncAutomatically();
        if((!flag2 || !flag1) && i == 0) {
        	if(!flag2) {
	            FragmentManager fragmentmanager1 = getFragmentManager();
	            if(fragmentmanager1.findFragmentByTag("photo_master_dialog") == null)
	            {
	                AlertFragmentDialog alertfragmentdialog1 = AlertFragmentDialog.newInstance(getString(R.string.oob_master_sync_dialog_title), getString(R.string.oob_master_sync_dialog_message), getString(R.string.ok), null);
	                alertfragmentdialog1.setTargetFragment(this, 0);
	                alertfragmentdialog1.show(fragmentmanager1, "photo_master_dialog");
	            }
        	} else {
        		FragmentManager fragmentmanager = getFragmentManager();
                if(fragmentmanager.findFragmentByTag("photo_sync_dialog") == null)
                {
                    String s = getString(R.string.es_google_iu_provider);
                    int j = R.string.oob_enable_sync_dialog_message;
                    Object aobj[] = new Object[1];
                    aobj[0] = s;
                    String s1 = getString(j, aobj);
                    AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(getString(R.string.oob_enable_sync_dialog_title), s1, getString(R.string.yes), getString(R.string.no));
                    alertfragmentdialog.setTargetFragment(this, 0);
                    alertfragmentdialog.show(fragmentmanager, "photo_sync_dialog");
                }
        	}
        	return false;
    	} else { 
        	doNextStep();
        	return flag;
        }
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.oob_instant_upload_fragment, viewgroup, false);
        mUploadChoice = (RadioGroup)view.findViewById(R.id.uploadChoice);
        mUploadChoice.check(R.id.option_wifi_and_mobile);
        if(((ConnectivityManager)getActivity().getSystemService("connectivity")).getNetworkInfo(0) == null)
        {
            View view1 = mUploadChoice.findViewById(R.id.option_wifi_and_mobile);
            if(view1 != null)
                mUploadChoice.removeView(view1);
            mUploadChoice.check(R.id.option_wifi_only);
        }
        return view;
    }

    public final void onDialogCanceled(String s)
    {
    }

    public final void onDialogListClick(int i, Bundle bundle)
    {
    }

    public final void onDialogNegativeClick(String s)
    {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s)
    {
        doNextStep();
        ((OobDeviceActivity)getActivity()).onContinue();
    }

}
