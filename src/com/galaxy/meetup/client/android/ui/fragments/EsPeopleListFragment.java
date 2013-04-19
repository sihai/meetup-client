/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ImageCache;
import com.galaxy.meetup.client.android.service.ServiceResult;

/**
 * 
 * @author sihai
 *
 */
public abstract class EsPeopleListFragment extends EsFragment implements
		OnScrollListener, RecyclerListener, OnItemClickListener {

	
	protected ImageCache mAvatarCache;
    private final DataSetObserver mCircleContentObserver = new DataSetObserver() {

        public final void onChanged()
        {
            if(mListView != null)
                mListView.invalidate();
            updateView(getView());
        }

    };
    
    protected CircleNameResolver mCircleNameResolver;
    private final Handler mHandler = new Handler();
    protected ListView mListView;
    protected Integer mPendingRequestId;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onAddPeopleToCirclesComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onCircleSyncComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onDismissSuggestedPeopleRequestComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onEventManageGuestComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onRemovePeopleRequestComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

        public final void onSetCircleMembershipComplete(int i, ServiceResult serviceresult)
        {
            handleServiceCallback(i, serviceresult);
        }

    };
    
    public EsPeopleListFragment()
    {
    }

    protected final void addCircleMembership(String s, String s1, ArrayList arraylist)
    {
        mPendingRequestId = EsService.setCircleMembership(getActivity(), getAccount(), s, s1, (String[])arraylist.toArray(new String[0]), null);
        ProgressFragmentDialog.newInstance(null, getString(EsPeopleData.getMembershipChangeMessageId(arraylist, null)), false).show(getFragmentManager(), "req_pending");
    }

    protected EsAccount getAccount()
    {
        return (EsAccount)getActivity().getIntent().getParcelableExtra("account");
    }

    protected abstract ListAdapter getAdapter();

    protected abstract int getEmptyText();

    protected final void handleServiceCallback(int i, ServiceResult serviceresult)
    {
        if(mPendingRequestId != null && i == mPendingRequestId.intValue()) {
        	DialogFragment dialogfragment = (DialogFragment)getFragmentManager().findFragmentByTag("req_pending");
            if(dialogfragment != null)
                dialogfragment.dismiss();
            mPendingRequestId = null;
            if(serviceresult != null && serviceresult.hasError())
                Toast.makeText(getActivity(), R.string.transient_server_error, 0).show();
        }
    }

    protected abstract View inflateView(LayoutInflater layoutinflater, ViewGroup viewgroup);

    protected abstract boolean isError();

    protected abstract boolean isLoaded();

    public final void onActivityResult(int i, int j, Intent intent)
    {
        if(j == -1 && i == 0)
        {
            final String personId = intent.getStringExtra("person_id");
            final String personName = intent.getStringExtra("display_name");
            final ArrayList selectedCircleIds = intent.getStringArrayListExtra("selected_circle_ids");
            mHandler.post(new Runnable() {

                public final void run()
                {
                    addCircleMembership(personId, personName, selectedCircleIds);
                }

            });
        }
        super.onActivityResult(i, j, intent);
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAvatarCache = ImageCache.getInstance(activity);
        mCircleNameResolver = new CircleNameResolver(activity, getLoaderManager(), getAccount());
        mCircleNameResolver.registerObserver(mCircleContentObserver);
    }

    public void onCreate(Bundle bundle)
    {
        if(bundle != null && bundle.containsKey("request_id"))
            mPendingRequestId = Integer.valueOf(bundle.getInt("request_id"));
        super.onCreate(bundle);
        mCircleNameResolver.initLoader();
        onInitLoaders(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = inflateView(layoutinflater, viewgroup);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setAdapter(getAdapter());
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setRecyclerListener(this);
        return view;
    }

    protected abstract void onInitLoaders(Bundle bundle);

    public void onMovedToScrapHeap(View view)
    {
        if(view instanceof Recyclable)
            ((Recyclable)view).onRecycle();
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        if(mPendingRequestId != null && !EsService.isRequestPending(mPendingRequestId.intValue()))
        {
            ServiceResult serviceresult = EsService.removeResult(mPendingRequestId.intValue());
            handleServiceCallback(mPendingRequestId.intValue(), serviceresult);
            mPendingRequestId = null;
        }
        updateView(getView());
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mPendingRequestId != null)
            bundle.putInt("request_id", mPendingRequestId.intValue());
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        if(i == 2)
            mAvatarCache.pause();
        else
            mAvatarCache.resume();
    }

    protected final void showCircleMembershipDialog(String s, String s1)
    {
        startActivityForResult(Intents.getCircleMembershipActivityIntent(getActivity(), getAccount(), s, s1, false), 0);
    }

    protected void updateView(View view)
    {
        boolean flag;
        if(!isLoaded() || !mCircleNameResolver.isLoaded())
            flag = true;
        else
            flag = false;
        if(!flag) {
        	if(!isError()) {
        		view.findViewById(R.id.server_error).setVisibility(8);
        		if(!isEmpty()) {
        			showContent(view);
        			return;
        		} else {
        			((TextView)view.findViewById(R.id.list_empty_text)).setText(getEmptyText());
        	        showEmptyView(view);
        	        return;
        		}
        	} else { 
        		view.findViewById(R.id.server_error).setVisibility(0);
        		showContent(view);
        		return;
        	}
        } else {
        	showEmptyViewProgress(view);
        	return;
        }
    }

}
