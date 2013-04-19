/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class BlockFragment extends ProgressFragmentDialog {

	private EsAccount mAccount;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onSetBlockedRequestComplete(int i, ServiceResult serviceresult)
        {
            handleSetBlockedCallback(i, serviceresult);
        }
    };
    
    private int mSetBlockedRequestId;
    
	public static interface Listener {
        void onBlockCompleted(boolean flag);
    }
	
	public BlockFragment()
    {
    }

    public static BlockFragment getInstance(Context context, EsAccount esaccount, String s, String s1, boolean flag, boolean flag1)
    {
        int i;
        BlockFragment blockfragment;
        String s2;
        Bundle bundle;
        if(flag1)
        {
            if(flag)
                i = R.string.block_page_operation_pending;
            else
                i = R.string.block_person_operation_pending;
        } else
        if(flag)
            i = R.string.unblock_page_operation_pending;
        else
            i = R.string.unblock_person_operation_pending;
        blockfragment = new BlockFragment();
        s2 = context.getResources().getString(i);
        bundle = new Bundle();
        bundle.putString("message", s2);
        blockfragment.setArguments(bundle);
        blockfragment.setCancelable(false);
        blockfragment.mAccount = esaccount;
        blockfragment.mSetBlockedRequestId = EsService.setPersonBlocked(context, esaccount, s, s1, flag1).intValue();
        return blockfragment;
    }

    protected final void handleSetBlockedCallback(int i, ServiceResult serviceresult) {
    	
    	if(mSetBlockedRequestId == i) {
    		dismiss();
            Listener listener;
            if(getTargetFragment() instanceof Listener)
                listener = (Listener)getTargetFragment();
            else
            if(getActivity() instanceof Listener)
                listener = (Listener)getActivity();
            else
                listener = null;
            if(listener != null)
                if(serviceresult != null && serviceresult.hasError())
                {
                    Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
                    listener.onBlockCompleted(false);
                } else
                {
                    listener.onBlockCompleted(true);
                }
    	}
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null && bundle.containsKey("set_blocked_req_id"))
        {
            mSetBlockedRequestId = bundle.getInt("set_blocked_req_id");
            mAccount = (EsAccount)bundle.getParcelable("set_account");
        }
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onResume()
    {
        super.onResume();
        EsAccount esaccount = mAccount;
        boolean flag = false;
        if(esaccount != null)
            if(!mAccount.equals(EsService.getActiveAccount(getActivity())))
            {
                boolean flag1 = EsLog.isLoggable("BlockFragment", 6);
                flag = false;
                if(flag1)
                    Log.e("BlockFragment", (new StringBuilder("Activity finished because it is associated with a signed-out account: ")).append(getActivity().getClass().getName()).toString());
            } else
            {
                flag = true;
            }
        if(flag)
        {
            EsService.registerListener(mServiceListener);
            if(!EsService.isRequestPending(mSetBlockedRequestId))
            {
                ServiceResult serviceresult = EsService.removeResult(mSetBlockedRequestId);
                handleSetBlockedCallback(mSetBlockedRequestId, serviceresult);
            }
        } else
        {
            getActivity().finish();
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("set_blocked_req_id", mSetBlockedRequestId);
        bundle.putParcelable("set_account", mAccount);
    }

    public final void show(FragmentActivity fragmentactivity)
    {
        show(fragmentactivity.getSupportFragmentManager(), "block_pending");
    }
}
