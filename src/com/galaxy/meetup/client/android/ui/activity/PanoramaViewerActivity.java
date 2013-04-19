/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import WriteReviewOperation.MediaRef;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PanoramaDetector;
import com.galaxy.meetup.client.android.service.ImageResourceManager;
import com.galaxy.meetup.client.android.service.Resource;
import com.galaxy.meetup.client.android.service.ResourceConsumer;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.ProgressFragmentDialog;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.panorama.PanoramaClient;
import com.google.android.gms.panorama.PanoramaClient.OnPanoramaInfoLoadedListener;

/**
 * 
 * @author sihai
 *
 */
public class PanoramaViewerActivity extends EsFragmentActivity implements
		ResourceConsumer, ConnectionCallbacks, OnConnectionFailedListener,
		OnPanoramaInfoLoadedListener {

	private Handler mHandler;
    private MediaRef mMediaRef;
    private PanoramaClient mPanoramaClient;
    private Resource mResource;
    
    public PanoramaViewerActivity()
    {
    }

    private void hideProgressDialog()
    {
        DialogFragment dialogfragment = (DialogFragment)getSupportFragmentManager().findFragmentByTag("progress");
        if(dialogfragment != null)
            dialogfragment.dismissAllowingStateLoss();
    }

    private void loadPanoramaInfo() {
    	
    	if(null == mResource) {
    		return;
    	}
    	
        switch(mResource.getStatus())
        {
        case 2: // '\002'
        case 3: // '\003'
        default:
            break;

        case 1: // '\001'
            android.net.Uri uri = null;
            
            hideProgressDialog();
            if(mPanoramaClient.isConnected()) {
            	boolean flag = mMediaRef.hasLocalUri();
                uri = null;
                if(flag) {
                	android.net.Uri uri1 = mMediaRef.getLocalUri();
                    if(!MediaStoreUtils.isMediaStoreUri(uri1))
                    {
                        boolean flag1 = ImageUtils.isFileUri(uri1);
                        uri = null;
                        if(flag1)
                        	uri = uri1;
                    }
                    
                }
            }
            
            if(uri == null)
                uri = EsProvider.buildPanoramaUri(mResource.getCacheFileName());
            mPanoramaClient.loadPanoramaInfoAndGrantAccess(this, uri);
            break;
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
            hideProgressDialog();
            showFailureMessage();
            finish();
            break;
        }
       
    }

    private void showFailureMessage()
    {
        Toast.makeText(this, R.string.toast_panorama_viewer_failure, 0).show();
    }

    public final void bindResources()
    {
        mResource = ImageResourceManager.getInstance(this).getMedia(mMediaRef, 1, 2, this);
    }

    protected final EsAccount getAccount()
    {
        return (EsAccount)getIntent().getParcelableExtra("account");
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    public final void onConnected()
    {
        loadPanoramaInfo();
    }

    public final void onConnectionFailed(ConnectionResult result)
    {
        showFailureMessage();
        finish();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(i != 0)
        {
            PanoramaDetector.clearCache();
            FragmentManager fragmentmanager = getSupportFragmentManager();
            if(fragmentmanager.findFragmentByTag("GMS_error") == null)
                (new GmsErrorDialogFragment(i)).show(fragmentmanager, "GMS_error");
        } else
        {
            mPanoramaClient = new PanoramaClient(this, this, this);
            mPanoramaClient.connect();
            mMediaRef = (MediaRef)getIntent().getParcelableExtra("mediaref");
            bindResources();
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                public final void run()
                {
                    showProgressDialog();
                }

            }, 200L);
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        unbindResources();
        if(mPanoramaClient != null)
        {
            mPanoramaClient.disconnect();
            mPanoramaClient = null;
        }
    }

    public final void onDisconnected()
    {
        finish();
    }

    public final void onPanoramaInfoLoaded(ConnectionResult result, Intent intent)
    {
        if(intent != null)
            startActivity(intent);
        else
            showFailureMessage();
        finish();
    }

    public final void onResourceStatusChange(Resource resource)
    {
        loadPanoramaInfo();
    }

    protected final void showProgressDialog() {
    	
        if(isFinishing() || mResource.getStatus() == 1) {
        	return;
        }
        FragmentManager fragmentmanager = getSupportFragmentManager();
        if(fragmentmanager.findFragmentByTag("progress") == null)
            ProgressFragmentDialog.newInstance(null, getString(R.string.loading_panorama)).show(fragmentmanager, "progress");
    }

    public final void unbindResources()
    {
        if(mResource != null)
            mResource.unregister(this);
    }
	
	
	public static class GmsErrorDialogFragment extends DialogFragment
    {

        public final Dialog onCreateDialog(Bundle bundle)
        {
            return GooglePlayServicesUtil.getErrorDialog(getArguments().getInt("errorCode"), getActivity(), 0);
        }

        public void onDismiss(DialogInterface dialoginterface)
        {
            FragmentActivity fragmentactivity = getActivity();
            if(fragmentactivity != null)
                fragmentactivity.finish();
        }

        public GmsErrorDialogFragment()
        {
        }

        GmsErrorDialogFragment(int i)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("errorCode", i);
            setArguments(bundle);
        }
    }
}
