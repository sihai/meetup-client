/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;

/**
 * 
 * @author sihai
 *
 */
public class EsUrlGatewayActivity extends EsFragmentActivity {

	private static HashMap sKnownUnsupportedUri = new HashMap();
    protected EsAccount mAccount;
    protected String mActivityId;
    protected String mAlbumId;
    protected String mAuthKey;
    protected String mDesktopActivityId;
    private String mEventCreatorId;
    private String mEventId;
    private String mEventInvitationToken;
    protected String mGaiaId;
    protected String mHangoutDomain;
    protected String mHangoutId;
    protected String mHangoutServiceId;
    protected String mName;
    protected long mPhotoId;
    protected String mProfileId;
    protected boolean mProfileIdValidated;
    protected int mRequestType;
    private String mRsvpType;
    private String mSquareId;
    
    public EsUrlGatewayActivity()
    {
        mRequestType = 0;
    }

    private static long parseLong(String s)
    {
    	try {
    		return Long.parseLong(s);
    	} catch (NumberFormatException numberformatexception) {
    		return 0L;
    	}
    }

    private void parseUri(Uri uri)
    {
    	// TODO
    }

    private void processEventUri(String s, String s1, String s2, Uri uri)
    {
        mRequestType = 27;
        mEventCreatorId = s1;
        mEventId = s;
        mRsvpType = s2;
        mEventInvitationToken = uri.getQueryParameter("gpinv");
    }

    private void redirect(Intent intent)
    {
        intent.addFlags(0x2010000);
        intent.putExtra("from_url_gateway", true);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.UNKNOWN;
    }

    protected final boolean isReadyToRedirect()
    {
        // TODO
    	return false;
    }

    protected final void launchBrowser(Uri uri)
    {
        HashSet hashset = (HashSet)sKnownUnsupportedUri.get(mAccount.getName());
        if(hashset == null)
        {
            hashset = new HashSet();
            sKnownUnsupportedUri.put(mAccount.getName(), hashset);
        }
        hashset.add(uri);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(0x80000);
        intent.setData(uri);
        List list = getPackageManager().queryIntentActivities(intent, 0x10000);
        if(null != list) {
        	int size = list.size();
        	for(int i = 0; i < size; i++) {
        		ActivityInfo activityinfo = ((ResolveInfo)list.get(i)).activityInfo;
        		if(activityinfo == null || getPackageName().equals(activityinfo.packageName)) 
        			 continue; 
        		else {
        			intent.setComponent(new ComponentName(activityinfo.packageName, activityinfo.name));
        			break;
        		}
        	}
        }
        redirect(intent);
    }

    protected void onCreate(Bundle bundle)
    {
        Intent intent;
        super.onCreate(bundle);
        intent = getIntent();
        if(null != bundle) {
        	mRequestType = bundle.getInt("request_type");
            mProfileId = bundle.getString("profile_id");
            mProfileIdValidated = bundle.getBoolean("profile_id_validated");
            mName = bundle.getString("name");
            mActivityId = bundle.getString("activity_id");
            mDesktopActivityId = bundle.getString("activity_id");
            mAlbumId = bundle.getString("album_id");
            mPhotoId = bundle.getLong("photo_id");
            mHangoutId = bundle.getString("hangout_id");
            mHangoutDomain = bundle.getString("hangout_domain");
            mHangoutServiceId = bundle.getString("service-id");
            mEventId = bundle.getString("event_id");
            mEventCreatorId = bundle.getString("event_creator_id");
            mEventInvitationToken = bundle.getString("event_invitation_token");
            mSquareId = bundle.getString("square_id");
        } else {
        	Uri uri;
            if(intent.hasExtra("customAppUri"))
                uri = Uri.parse(intent.getStringExtra("customAppUri"));
            else
                uri = intent.getData();
            if(null == uri) {
            	finish();
            	return;
            }
            parseUri(uri);
        }
        
        mAccount = EsAccountsData.getActiveAccount(this);
        if(mAccount == null)
        {
            intent.setComponent(new ComponentName(this, UrlGatewayLoaderActivity.class));
            startActivity(Intents.getAccountsActivityIntent(this, intent));
            finish();
        }
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("request_type", mRequestType);
        bundle.putString("profile_id", mProfileId);
        bundle.putBoolean("profile_id_validated", mProfileIdValidated);
        bundle.putString("name", mName);
        bundle.putString("activity_id", mActivityId);
        bundle.putString("activity_id", mDesktopActivityId);
        bundle.putString("album_id", mAlbumId);
        bundle.putLong("photo_id", mPhotoId);
        bundle.putString("hangout_id", mHangoutId);
        bundle.putString("hangout_domain", mHangoutDomain);
        bundle.putString("service-id", mHangoutServiceId);
        bundle.putString("event_id", mEventId);
        bundle.putString("event_creator_id", mEventCreatorId);
        bundle.putString("event_invitation_token", mEventInvitationToken);
        bundle.putString("square_id", mSquareId);
    }

    protected final void redirect()
    {
        // TODO
    }

    protected final void redirectToBrowser()
    {
        Uri uri = getIntent().getData();
        if("http".equals(uri.getScheme()) || "content".equals(uri.getScheme()))
            uri = uri.buildUpon().scheme("https").build();
        HashSet hashset = (HashSet)sKnownUnsupportedUri.get(mAccount.getName());
        if(hashset != null && hashset.contains(uri))
        {
            launchBrowser(uri);
        } else
        {
            UnrecognizedLinkDialog unrecognizedlinkdialog = new UnrecognizedLinkDialog();
            Bundle bundle = new Bundle();
            bundle.putParcelable("url", uri);
            unrecognizedlinkdialog.setArguments(bundle);
            unrecognizedlinkdialog.show(getSupportFragmentManager(), "unsupported");
        }
    }
    
    
    public static class UnrecognizedLinkDialog extends DialogFragment implements android.content.DialogInterface.OnClickListener {

	    public void onCancel(DialogInterface dialoginterface)
	    {
	        FragmentActivity fragmentactivity = getActivity();
	        if(fragmentactivity != null)
	            fragmentactivity.finish();
	    }

	    public void onClick(DialogInterface dialoginterface, int i)
	    {
	    	if(-2 == i) {
	    		dialoginterface.dismiss();
		        getActivity().finish();
	    	} else if(-1 == i) {
	    		Uri uri = (Uri)getArguments().getParcelable("url");
		        ((EsUrlGatewayActivity)getActivity()).launchBrowser(uri);
	    	}
	    }

	    public final Dialog onCreateDialog(Bundle bundle)
	    {
	        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_EmeraldSea_Dark_Dialog));
	        builder.setTitle(R.string.unsupported_link_dialog_title);
	        builder.setMessage(R.string.unsupported_link_dialog_message);
	        builder.setPositiveButton(0x104000a, this);
	        builder.setNegativeButton(0x1040000, this);
	        builder.setCancelable(true);
	        return builder.create();
	    }
	
	    public UnrecognizedLinkDialog()
	    {
	    }
	}
}
