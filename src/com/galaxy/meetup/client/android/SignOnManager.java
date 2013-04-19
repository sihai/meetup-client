/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AccountSettingsData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.oob.OutOfBoxResponseParcelable;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog;
import com.galaxy.meetup.client.android.ui.fragments.ProgressFragmentDialog;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public class SignOnManager implements AlertFragmentDialog.AlertDialogListener {

	private EsAccount mAccount;
    private final FragmentActivity mActivity;
    private final FragmentManager mFragmentManager;
    private Intent mIntent;
    private boolean mIsResumed;
    private EsAccount mOobAccount;
    private EsAccount mResultAccount;
    
    
    public SignOnManager(FragmentActivity fragmentactivity) {
        mActivity = fragmentactivity;
        mFragmentManager = mActivity.getSupportFragmentManager();
    }
    
	public final EsAccount getAccount() {
		return mAccount;
	}

	public final boolean isSignedIn() {
		boolean flag;
		if (mAccount != null)
			flag = true;
		else
			flag = false;
		return flag;
	}
	
	public final boolean onActivityResult(int i, int j) {
		
		if(1023 != i) {
			return false;
		}
		mOobAccount = null;
		if (j == -1) {
			Intent intent = (Intent) mIntent.getParcelableExtra("intent");
			if (intent != null) {
				mActivity.startActivity(intent);
				mActivity.finish();
			} else {
				mResultAccount = EsService.getActiveAccount(mActivity);
			}
		} else {
			switchAccounts();
		}
        return true;
    }

    public final void onCreate(Bundle bundle, Intent intent) {
        mIntent = intent;
        EsAccount esaccount = EsService.getActiveAccount(mActivity);
        if(null == esaccount) {
        	switchAccounts();
        	return;
        }
        
        if(bundle == null && mIntent.getBooleanExtra("run_oob", false)) {
            Intent intent1 = (Intent)mIntent.getParcelableExtra("intent");
            OutOfBoxResponseParcelable outofboxresponseparcelable = (OutOfBoxResponseParcelable)mIntent.getParcelableExtra("network_oob");
            AccountSettingsData accountsettingsdata = (AccountSettingsData)mIntent.getParcelableExtra("plus_pages");
            FragmentActivity fragmentactivity = mActivity;
            MobileOutOfBoxResponse mobileoutofboxresponse;
            Intent intent2;
            if(outofboxresponseparcelable != null)
                mobileoutofboxresponse = outofboxresponseparcelable.getResponse();
            else
                mobileoutofboxresponse = null;
            intent2 = Intents.getOobIntent(fragmentactivity, esaccount, mobileoutofboxresponse, accountsettingsdata, null);
            if(intent2 != null) {
                mOobAccount = esaccount;
                mActivity.startActivityForResult(intent2, 1023);
            } else {
                setAccount(esaccount);
                if(intent1 != null) {
                    mActivity.startActivity(intent1);
                    mActivity.finish();
                }
            }
        } else if(!esaccount.hasGaiaId()) {
            switchAccounts();
        } else {
            setAccount(esaccount);
            if(esaccount.isPlusPage() && bundle == null) {
                Resources resources = mActivity.getResources();
                int i = R.string.plus_page_reminder;
                Object aobj[] = new Object[1];
                aobj[0] = esaccount.getDisplayName();
                String s = resources.getString(i, aobj);
                Toast.makeText(mActivity, s, 1).show();
            }
        }
        
       
    }

    public final void onDialogCanceled(String s) {
    }

    public final void onDialogListClick(int i, Bundle bundle) {
    }

    public final void onDialogNegativeClick(String s) {
    }

    public final void onDialogPositiveClick(Bundle bundle, String s) {
        if(bundle != null)
            doSignOut(bundle.getBoolean("downgrade_account", false));
        else
            doSignOut(false);
    }

    public final void onPause() {
        mIsResumed = false;
    }

    public final boolean onResume() {
        AlertFragmentDialog alertfragmentdialog = (AlertFragmentDialog)mFragmentManager.findFragmentByTag("SignOnManager.confirm_signoff");
        if(alertfragmentdialog != null)
            alertfragmentdialog.setListener(this);
        boolean flag;
        if(mResultAccount != null) {
            setAccount(mResultAccount);
            mResultAccount = null;
            flag = true;
        } else {
            flag = false;
        }
        if(null == mAccount) {
        	if(mOobAccount != null)
                mIsResumed = true;
            else
                switchAccounts();
        	return flag;
        }
        
        if(!mAccount.equals(EsService.getActiveAccount(mActivity))) {
            switchAccounts();
        } else {
            mIsResumed = true;
            if(!mAccount.isPlusPage())
                RealTimeChatService.initiateConnection(mActivity, mAccount);
        }
        return flag;
    }

    public final void signOut(final boolean downgrading) {
        ProgressFragmentDialog.newInstance(null, mActivity.getString(R.string.sign_out_pending)).show(mFragmentManager, "SignOnManager.progress_dialog");
        (new AsyncTask() {

            protected final Object doInBackground(Object aobj[])
            {
                return Integer.valueOf(getPendingInstantUploadCount());
            }

            protected final void onPostExecute(Object obj) {
                final Integer pendingInstantUploadCount = (Integer)obj;
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {

                    public final void run()
                    {
                        continueSignOut(pendingInstantUploadCount.intValue(), downgrading);
                    }
                }, 500L);
            }

        }).execute(new Void[0]);
    }
    
    protected final void continueSignOut(int i, boolean flag)
    {
        DialogFragment dialogfragment = (DialogFragment)mFragmentManager.findFragmentByTag("SignOnManager.progress_dialog");
        if(dialogfragment != null)
            try
            {
                dialogfragment.dismiss();
            }
            catch(IllegalStateException illegalstateexception) { }
        if(!flag && i > 0)
        {
            Resources resources = mActivity.getResources();
            int j = R.plurals.sign_out_message;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            String s = resources.getQuantityString(j, i, aobj);
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(mActivity.getString(R.string.sign_out_title), s, mActivity.getString(R.string.ok), mActivity.getString(R.string.cancel));
            alertfragmentdialog.setListener(this);
            Bundle bundle = alertfragmentdialog.getArguments();
            if(bundle == null)
                bundle = new Bundle();
            bundle.putBoolean("downgrade_account", flag);
            alertfragmentdialog.setArguments(bundle);
            alertfragmentdialog.show(mFragmentManager, "SignOnManager.confirm_signoff");
        } else
        {
            doSignOut(flag);
        }
    }
    
    private void switchAccounts() {
        if(mAccount != null) {
            RealTimeChatService.allowDisconnect(mActivity, mAccount);
            EsService.removeAccount(mActivity, mAccount);
            mAccount = null;
        }
        Intent intent = (Intent)mIntent.getParcelableExtra("intent");
        mActivity.startActivity(Intents.getAccountsActivityIntent(mActivity, intent));
        mActivity.finish();
    }
    
    private void setAccount(EsAccount esaccount) {
        if(mIsResumed && mAccount != null && mAccount != esaccount)
            RealTimeChatService.allowDisconnect(mActivity, mAccount);
        mAccount = esaccount;
    }
    
    private int getPendingInstantUploadCount()
    {
        Cursor cursor = null;
        Cursor cursor1 = null;
        int i = 0;
        int j = 0;
        int k = 0;
        ContentResolver contentresolver = mActivity.getContentResolver();
        try {
        	cursor = contentresolver.query(InstantUploadFacade.INSTANT_UPLOAD_URI, null, null, null, null);
	        if(null != cursor && cursor.moveToNext()) {
	        	i = cursor.getInt(0);
	        }
	        
	        android.net.Uri.Builder builder = InstantUploadFacade.UPLOAD_ALL_URI.buildUpon();
	        builder.appendQueryParameter("account", mAccount.getName());
	        cursor1 = contentresolver.query(builder.build(), null, null, null, null);
	        if(null != cursor1 && cursor1.moveToNext()) {
	        	k = cursor1.getInt(1);
	            j = cursor1.getInt(2);
	        }
	        return i + (j - k);
        } finally {
        	if(cursor != null)
                cursor.close();
        	if(cursor1 != null)
                cursor1.close();
        }
    }
    
    private void doSignOut(boolean flag) {
        OzActions ozactions = OzActions.SETTINGS_SIGNOUT;
        if(mAccount != null)
        {
            OzViews ozviews = OzViews.getViewForLogging(mActivity);
            EsAnalytics.recordActionEvent(mActivity, mAccount, ozactions, ozviews);
        }
        EsService.removeAccount(mActivity, mAccount);
        if(mAccount != null)
            RealTimeChatService.allowDisconnect(mActivity, mAccount);
        mAccount = null;
        if(!flag)
        {
            Intent intent = (Intent)mIntent.getParcelableExtra("intent");
            mActivity.startActivity(Intents.getAccountsActivityIntent(mActivity, intent));
            mActivity.finish();
        }
    }
}
