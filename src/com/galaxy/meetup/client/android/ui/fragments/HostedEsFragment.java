/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;

/**
 * 
 * @author sihai
 *
 */
public abstract class HostedEsFragment extends HostedFragment {

	protected EsAccount mAccount;
	protected Integer mNewerReqId;
    protected Integer mOlderReqId;
    private boolean mRestoredFragment;
    
    private final Handler mHandler = new Handler() {

        public final void handleMessage(Message message)
        {
            if(message.what == 0)
                doShowEmptyViewProgressDelayed();
        }
    };
    
    public HostedEsFragment()
    {
    }

    protected static void setupEmptyView(View view, int i)
    {
        ((TextView)view.findViewById(R.id.list_empty_text)).setText(i);
    }

    protected void doShowEmptyView(View view, String s)
    {
        if(isEmpty())
        {
            View view1 = view.findViewById(0x1020004);
            view1.setVisibility(0);
            TextView textview = (TextView)view1.findViewById(R.id.list_empty_text);
            textview.setText(s);
            textview.setVisibility(0);
            view1.findViewById(R.id.list_empty_progress).setVisibility(8);
        }
    }

    protected void doShowEmptyViewProgress(View view)
    {
        if(isEmpty())
        {
            View view1 = view.findViewById(0x1020004);
            view1.setVisibility(0);
            view1.findViewById(R.id.list_empty_text).setVisibility(8);
            view1.findViewById(R.id.list_empty_progress).setVisibility(0);
        }
    }

    protected final void doShowEmptyViewProgressDelayed()
    {
        if(isAdded() && !isPaused())
        {
            View view = getView();
            if(view != null)
                doShowEmptyViewProgress(view);
        }
    }

    public EsAccount getAccount()
    {
        return mAccount;
    }

    protected abstract boolean isEmpty();

    protected boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(mNewerReqId != null || mOlderReqId != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mRestoredFragment = true;
            if(bundle.containsKey("n_pending_req"))
                mNewerReqId = Integer.valueOf(bundle.getInt("n_pending_req"));
            if(bundle.containsKey("o_pending_req"))
                mOlderReqId = Integer.valueOf(bundle.getInt("o_pending_req"));
        }
    }

    public void onResume()
    {
        super.onResume();
        Integer integer = mNewerReqId;
        boolean flag = false;
        if(integer != null)
            if(EsService.isRequestPending(mNewerReqId.intValue()))
            {
                boolean flag1 = isEmpty();
                flag = false;
                if(flag1)
                    showEmptyViewProgress(getView());
            } else
            {
                mNewerReqId = null;
                flag = true;
            }
        if(mOlderReqId != null)
            if(EsService.isRequestPending(mOlderReqId.intValue()))
            {
                if(isEmpty())
                    showEmptyViewProgress(getView());
            } else
            {
                mOlderReqId = null;
                flag = true;
            }
        if(flag && mNewerReqId == null && mOlderReqId == null)
        {
            onResumeContentFetched(getView());
            if(isEmpty())
                showEmptyView(getView(), null);
        }
    }

    protected void onResumeContentFetched(View view)
    {
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mNewerReqId != null)
            bundle.putInt("n_pending_req", mNewerReqId.intValue());
        if(mOlderReqId != null)
            bundle.putInt("o_pending_req", mOlderReqId.intValue());
    }

    protected void onSetArguments(Bundle bundle)
    {
        super.onSetArguments(bundle);
        mAccount = (EsAccount)bundle.getParcelable("account");
    }

    protected final void removeProgressViewMessages()
    {
        mHandler.removeMessages(0);
    }

    protected void showContent(View view)
    {
        removeProgressViewMessages();
        view.findViewById(0x1020004).setVisibility(8);
    }

    protected void showEmptyView(View view, String s)
    {
        removeProgressViewMessages();
        doShowEmptyView(view, s);
    }

    protected void showEmptyViewProgress(View view)
    {
        if(mRestoredFragment)
        {
            if(!mHandler.hasMessages(0) && isEmpty())
                mHandler.sendEmptyMessageDelayed(0, 800L);
        } else
        {
            doShowEmptyViewProgress(view);
        }
    }

    protected void showEmptyViewProgress(View view, String s)
    {
        if(isEmpty())
        {
            ((TextView)view.findViewById(R.id.list_empty_progress_text)).setText(s);
            showEmptyViewProgress(view);
        }
    }

    public final void startExternalActivity(Intent intent)
    {
        intent.addFlags(0x80000);
        startActivity(intent);
    }

    protected void updateSpinner()
    {
        HostActionBar hostactionbar = getActionBar();
        if(hostactionbar != null)
            if(isProgressIndicatorVisible())
                hostactionbar.showProgressIndicator();
            else
                hostactionbar.hideProgressIndicator();
    }
}
