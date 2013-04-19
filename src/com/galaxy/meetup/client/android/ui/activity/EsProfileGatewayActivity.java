/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.fragments.ProgressFragmentDialog;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class EsProfileGatewayActivity extends FragmentActivity {

	protected EsAccount mAccount;
    private final Handler mHandler = new Handler();
    protected Integer mPendingRequestId;
    protected String mPersonId;
    protected String mPersonName;
    protected boolean mRedirected;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onSetCircleMembershipComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

    };
    
    public EsProfileGatewayActivity()
    {
    }

    protected final void handleServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mPendingRequestId != null && i == mPendingRequestId.intValue())
        {
            DialogFragment dialogfragment = (DialogFragment)getSupportFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mPendingRequestId = null;
            if(serviceresult != null && serviceresult.hasError())
            {
                Toast.makeText(getApplicationContext(), R.string.transient_server_error, 0).show();
            } else
            {
                android.content.Context context = getApplicationContext();
                int j = R.string.add_to_circle_confirmation_toast;
                Object aobj[] = new Object[1];
                aobj[0] = mPersonName;
                Toast.makeText(context, getString(j, aobj), 0).show();
            }
            finish();
        }
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        boolean flag = false;
        if(0 == i) {
        	flag = false;
            if(j == -1)
            {
                final ArrayList circleIds = intent.getStringArrayListExtra("selected_circle_ids");
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        setCircleMembership(circleIds);
                    }
                });
                flag = true;
            }
        }
        
        if(!flag)
            finish();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mAccount = (EsAccount)bundle.getParcelable("account");
            mPersonId = bundle.getString("person_id");
            mPersonName = bundle.getString("person_name");
            if(bundle.containsKey("pending_req_id"))
                mPendingRequestId = Integer.valueOf(bundle.getInt("pending_req_id"));
            mRedirected = bundle.getBoolean("redirected");
        } else
        {
            mAccount = EsAccountsData.getActiveAccount(this);
        }
        if(mAccount != null && !mRedirected) {
        	if(getIntent().getData() == null)
                finish();
        } else { 
        	finish();
        }
    }

    public void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
            mPendingRequestId = null;
        }
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("account", mAccount);
        bundle.putString("person_id", mPersonId);
        bundle.putString("person_name", mPersonName);
        if(mPendingRequestId != null)
            bundle.putInt("pending_req_id", mPendingRequestId.intValue());
        bundle.putBoolean("redirected", mRedirected);
    }

    protected final void setCircleMembership(ArrayList arraylist)
    {
    	try {
    		mPendingRequestId = EsService.setCircleMembership(this, mAccount, mPersonId, mPersonName, (String[])arraylist.toArray(new String[0]), null);
    		ProgressFragmentDialog progressfragmentdialog = ProgressFragmentDialog.newInstance(null, getString(R.string.add_to_circle_operation_pending), false);
    		progressfragmentdialog.show(getSupportFragmentManager(), "req_pending");
    	} catch (Throwable throwable) {
    		if(EsLog.isLoggable("EsProfileGatewayActivity", 6))
                Log.e("EsProfileGatewayActivity", "Cannot show dialog", throwable);
    	}
    }

    protected final void showCirclePicker()
    {
        startActivityForResult(Intents.getCircleMembershipActivityIntent(this, mAccount, mPersonId, mPersonName, false), 0);
    }
}
