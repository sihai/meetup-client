/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.service.EsService;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsFragment extends Fragment {

	protected Integer mNewerReqId;
    protected Integer mOlderReqId;
    private boolean mPaused;
    private boolean mRestoredFragment;
    
    private final Handler mHandler = new Handler() {

        public final void handleMessage(Message message) {
            if(message.what == 0)
                doShowEmptyViewProgressDelayed();
        }
        
    };
    
    public EsFragment()
    {
    }

    private void doShowEmptyViewProgress(View view)
    {
        if(isEmpty())
        {
            View view1 = view.findViewById(0x1020004);
            view1.setVisibility(0);
            view1.findViewById(R.id.list_empty_text).setVisibility(8);
            view1.findViewById(R.id.list_empty_progress).setVisibility(0);
        }
    }

    private void removeProgressViewMessages()
    {
        mHandler.removeMessages(0);
    }

    protected static void setupEmptyView(View view, int i)
    {
        ((TextView)view.findViewById(R.id.list_empty_text)).setText(i);
    }

    protected final void doShowEmptyViewProgressDelayed()
    {
        if(isAdded() && !mPaused)
        {
            View view = getView();
            if(view != null)
                doShowEmptyViewProgress(view);
        }
    }

    protected abstract boolean isEmpty();

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

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle, int i)
    {
        return layoutinflater.inflate(i, viewgroup, false);
    }

    public void onPause()
    {
        super.onPause();
        mPaused = true;
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
            getView();
            if(isEmpty())
                showEmptyView(getView());
        }
        mPaused = false;
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mNewerReqId != null)
            bundle.putInt("n_pending_req", mNewerReqId.intValue());
        if(mOlderReqId != null)
            bundle.putInt("o_pending_req", mOlderReqId.intValue());
    }

    protected final void showContent(View view)
    {
        removeProgressViewMessages();
        view.findViewById(0x1020004).setVisibility(8);
    }

    protected final void showEmptyView(View view)
    {
        removeProgressViewMessages();
        if(isEmpty())
        {
            View view1 = view.findViewById(0x1020004);
            view1.setVisibility(0);
            view1.findViewById(R.id.list_empty_text).setVisibility(0);
            view1.findViewById(R.id.list_empty_progress).setVisibility(8);
        }
    }

    protected final void showEmptyViewProgress(View view)
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

    protected final void showEmptyViewProgress(View view, String s)
    {
        if(isEmpty())
        {
            ((TextView)view.findViewById(R.id.list_empty_progress_text)).setText(s);
            showEmptyViewProgress(view);
        }
    }

    protected void updateSpinner(ProgressBar progressbar)
    {
        if(progressbar != null)
        {
            byte byte0;
            if(mNewerReqId == null && mOlderReqId == null)
                byte0 = 8;
            else
                byte0 = 0;
            progressbar.setVisibility(byte0);
        }
    }
}
