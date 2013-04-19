/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EventCardAdapter;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.EventDestinationCardView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.util.HelpUrl;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class HostedEventListFragment extends HostedEsFragment implements
		LoaderCallbacks, View.OnClickListener, ItemClickListener {

	private EventCardAdapter mAdapter;
    private int mCurrentMode;
    private int mCurrentSpinnerPosition;
    private boolean mDataPresent;
    private ColumnGridView mGridView;
    private final Handler mHandler = new Handler();
    private boolean mInitialLoadDone;
    private final EsServiceListener mListener = new EsServiceListener() {

        public final void onEventHomeRequestComplete(int i)
        {
            if(mNewerReqId != null && i == mNewerReqId.intValue())
            {
                mNewerReqId = null;
                mRefreshNeeded = false;
                getActionBar().hideProgressIndicator();
                mHandler.post(new Runnable() {

                    public final void run()
                    {
                        if(getActivity() != null && !getActivity().isFinishing())
                            getLoaderManager().restartLoader(0, null, HostedEventListFragment.this);
                    }
                });
            }
    }};
    private boolean mRefreshNeeded;
    private ArrayAdapter mSpinnerAdapter;
    
    public HostedEventListFragment()
    {
        mCurrentMode = 0;
    }

    private void fetchData()
    {
        FragmentActivity fragmentactivity = getActivity();
        if(mNewerReqId == null && fragmentactivity != null)
        {
            if(!mDataPresent)
                showEmptyViewProgress(getView(), getString(R.string.loading));
            getActionBar().showProgressIndicator();
            mNewerReqId = EsService.getEventHome(fragmentactivity, mAccount);
        }
    }

    private void setCreationVisibility(int i)
    {
        getView().findViewById(R.id.createButton).setVisibility(i);
        getView().findViewById(R.id.createText).setVisibility(i);
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.MY_EVENTS;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onActionButtonClicked(int i) {
        if(0 == i) {
        	startActivity(Intents.getCreateEventActivityIntent(getActivity().getApplicationContext(), mAccount));
        }
    }

    public void onClick(View view) {
        if(!(view instanceof EventDestinationCardView)) {
        	if(view.getId() == R.id.createButton)
                startActivity(Intents.getCreateEventActivityIntent(getActivity().getApplicationContext(), mAccount));
        } else { 
        	PlusEvent plusevent = ((EventDestinationCardView)view).getEvent();
            if(plusevent != null)
            {
                String s = plusevent.id;
                String s1 = plusevent.creatorObfuscatedId;
                startActivity(Intents.getHostedEventIntent(getActivity(), mAccount, s, s1, null));
            }
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mRefreshNeeded = bundle.getBoolean("events_refresh", false);
            mInitialLoadDone = bundle.getBoolean("events_initialload", false);
            mCurrentMode = bundle.getInt("events_currentmode", 0);
            mDataPresent = bundle.getBoolean("events_datapresent", false);
        } else
        {
            mRefreshNeeded = getArguments().getBoolean("refresh", false);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new EventsLoader(getActivity(), mAccount, mCurrentMode);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.hosted_events_fragment, viewgroup, false);
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        mAdapter = new EventCardAdapter(getActivity(), mAccount, null, this, this, mGridView);
        mGridView.setAdapter(mAdapter);
        setupEmptyView(view, R.string.no_events);
        Button button = (Button)view.findViewById(R.id.createButton);
        button.setClickable(true);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public final void onLoadFinished(Loader loader, Object obj) {
        int j;
        Cursor cursor = (Cursor)obj;
        mAdapter.changeCursor(cursor);
        boolean flag;
        boolean flag1;
        int i;
        if(cursor != null && cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        mDataPresent = flag;
        if(mDataPresent && cursor.moveToFirst())
        {
            byte abyte0[] = cursor.getBlob(1);
            int k;
            if(EsEventData.isEventOver((PlusEvent)JsonUtil.fromByteArray(abyte0, PlusEvent.class), System.currentTimeMillis()))
                k = 1;
            else
                k = 0;
            mCurrentSpinnerPosition = k;
        }
        if(!mDataPresent && !mRefreshNeeded && mCurrentMode == 0)
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
            i = 0;
        else
            i = 8;
        setCreationVisibility(i);
        if(mDataPresent)
            showContent(getView());
        else
        if(mRefreshNeeded)
            showEmptyViewProgress(getView(), getString(R.string.loading));
        else
        if(flag1)
            showContent(getView());
        else
            showEmptyView(getView(), getString(R.string.no_events));
        if(mRefreshNeeded) {
        	invalidateActionBar();
            return; 
        } else { 
        	mInitialLoadDone = true;
        	if(!(loader instanceof EventsLoader)) {
        		invalidateActionBar();
                return;
        	} else { 
        		j = ((EventsLoader)loader).getCurrentMode();
        		if(!mDataPresent) { 
        			if(j != mCurrentMode)
        	            getLoaderManager().restartLoader(0, null, this);
        			invalidateActionBar();
                    return;
        		} else { 
        			mCurrentMode = j;
        			invalidateActionBar();
        	        return;
        		}
        	}
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.help)
        {
            String s = getResources().getString(R.string.url_param_help_events);
            startExternalActivity(new Intent("android.intent.action.VIEW", HelpUrl.getHelpUrl(getActivity(), s)));
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        EsService.unregisterListener(mListener);
        super.onPause();
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        mSpinnerAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(0x1090009);
        CharSequence acharsequence[] = getResources().getTextArray(R.array.event_spinner_items);
        int i = 0;
        for(int j = acharsequence.length; i < j; i++)
            mSpinnerAdapter.add(acharsequence[i].toString());

        if(mCurrentMode != 0 || mDataPresent)
            hostactionbar.showPrimarySpinner(mSpinnerAdapter, mCurrentSpinnerPosition);
        hostactionbar.showActionButton(0, R.drawable.icn_events_create_event, R.string.event_button_add_event_label);
        hostactionbar.showRefreshButton();
        if(mNewerReqId != null)
            hostactionbar.showProgressIndicator();
    }

    public final void onPrimarySpinnerSelectionChange(int i)
    {
        if(mCurrentSpinnerPosition != i)
        {
            mCurrentSpinnerPosition = i;
            int j;
            if(mCurrentSpinnerPosition == 0)
                j = 2;
            else
                j = 1;
            mCurrentMode = j;
            mGridView.setSelectionToTop();
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    public final void onResume()
    {
        EsService.registerListener(mListener);
        super.onResume();
        if(mRefreshNeeded)
            fetchData();
    }

    protected final void onResumeContentFetched(View view)
    {
        super.onResumeContentFetched(view);
        mRefreshNeeded = false;
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putBoolean("events_refresh", mRefreshNeeded);
        bundle.putBoolean("events_initialload", mInitialLoadDone);
        bundle.putInt("events_currentmode", mCurrentMode);
        bundle.putBoolean("events_datapresent", mDataPresent);
        super.onSaveInstanceState(bundle);
    }

    public final void onSpanClick(URLSpan urlspan)
    {
    }

    public final void onUserImageClick(String s, String s1)
    {
        startActivity(Intents.getProfileActivityByGaiaIdIntent(getActivity(), mAccount, s, null));
    }

    @Override
    public final void refresh()
    {
        super.refresh();
        setCreationVisibility(8);
        fetchData();
    }

    protected final void showContent(View view)
    {
        super.showContent(view);
        mGridView.setVisibility(0);
    }

    protected final void showEmptyView(View view, String s)
    {
        super.showEmptyView(view, s);
        mGridView.setVisibility(8);
    }
    
    
    static final class EventsLoader extends EsCursorLoader {

    	private final EsAccount mAccount;
        private int mMode;

        public EventsLoader(Context context, EsAccount esaccount, int mode)
        {
            super(context, EsProvider.EVENTS_ALL_URI);
            mMode = mode;
            mAccount = esaccount;
        }
        
        public final Cursor esLoadInBackground() {
        	
        	boolean flag = true;
        	Cursor cursor = null;
        	if(0 == mMode) {
        		cursor = EsEventData.getMyCurrentEvents(getContext(), mAccount, System.currentTimeMillis(), Query.PROJECTION);
                if(!flag || cursor != null && cursor.getCount() > 0)
                {
                    mMode = 2;
                }
        	} else if(1 == mMode) {
        		cursor = EsEventData.getMyPastEvents(getContext(), mAccount, System.currentTimeMillis(), Query.PROJECTION);
                if(cursor != null && cursor.getCount() > 0)
                    mMode = 1;
        	} else if(2 == mMode) {
        		 flag = false;
	             cursor = EsEventData.getMyCurrentEvents(getContext(), mAccount, System.currentTimeMillis(), Query.PROJECTION);
	             if(!flag || cursor != null && cursor.getCount() > 0)
	             {
	                 mMode = 2;
	             }
        	}
        	return cursor;
        }

        public final int getCurrentMode()
        {
            return mMode;
        }

    }

    public static interface Query {

        public static final String PROJECTION[] = {
            "_id", "event_data"
        };

    }
}
