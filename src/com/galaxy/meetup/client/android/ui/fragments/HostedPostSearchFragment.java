/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.StreamAdapter;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsDatabaseHelper;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.controller.ComposeBarController;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.ItemClickListener;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter.OnQueryChangeListener;
import com.galaxy.meetup.client.android.ui.view.StreamCardView;
import com.galaxy.meetup.client.util.SearchUtils;

/**
 * 
 * @author sihai
 *
 */
public class HostedPostSearchFragment extends HostedStreamFragment implements
		OnQueryChangeListener {

	private String mDelayedQuery;
    private final EsServiceListener mPostsSearchServiceListener = new EsServiceListener() {

        public final void onSearchActivitiesComplete(int i, ServiceResult serviceresult)
        {
            if(mNewerReqId != null && i == mNewerReqId.intValue() || mOlderReqId != null && i == mOlderReqId.intValue())
            {
                mNewerReqId = null;
                mOlderReqId = null;
                HostedPostSearchFragment hostedpostsearchfragment = HostedPostSearchFragment.this;
                boolean flag;
                if(serviceresult != null && serviceresult.hasError())
                    flag = true;
                else
                    flag = false;
                hostedpostsearchfragment.mError = flag;
                updateServerErrorView();
                loadContent();
            }
        }

    };
	private String mQuery;
    private SearchViewAdapter mSearchViewAdapter;
    
    public HostedPostSearchFragment()
    {
    }

    private void createAndRunDbCleanup(final Context context, final EsAccount account, final Runnable mainThreadPostRunnable)
    {
        (new Thread(new Runnable() {

            public final void run()
            {
                SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, account).getWritableDatabase();
                sqlitedatabase.delete("search", null, null);
                String s = DatabaseUtils.sqlEscapeString((new StringBuilder()).append(SearchUtils.getSearchKey("")).append('%').toString());
                sqlitedatabase.delete("activity_streams", (new StringBuilder("stream_key LIKE ")).append(s).toString(), null);
                if(mainThreadPostRunnable != null)
                    (new Handler(Looper.getMainLooper())).post(mainThreadPostRunnable);
            }
        })).start();
    }

    private void doSearch()
    {
        mFirstLoad = true;
        prepareLoaderUri();
        getLoaderManager().restartLoader(2, null, this);
        fetchContent(true);
    }

    protected final StreamAdapter createStreamAdapter(Context context, ColumnGridView columngridview, EsAccount esaccount, android.view.View.OnClickListener onclicklistener, ItemClickListener itemclicklistener, StreamAdapter.ViewUseListener viewuselistener, StreamCardView.StreamPlusBarClickListener streamplusbarclicklistener, 
            StreamCardView.StreamMediaClickListener streammediaclicklistener, ComposeBarController composebarcontroller)
    {
        return super.createStreamAdapter(context, columngridview, esaccount, onclicklistener, itemclicklistener, viewuselistener, streamplusbarclicklistener, streammediaclicklistener, null);
    }

    protected final void fetchContent(final boolean newer)
    {
        if(!TextUtils.isEmpty(mQuery))
        {
            if(newer)
                showEmptyViewProgress(getView());
            Runnable runnable = new Runnable() {

                public final void run()
                {
                    if(!isPaused())
                    {
                        if(newer)
                        {
                            mNewerReqId = Integer.valueOf(EsService.searchActivities(getActivity(), mAccount, mQuery, false));
                            mOlderReqId = null;
                        } else
                        {
                            mNewerReqId = null;
                            mOlderReqId = Integer.valueOf(EsService.searchActivities(getActivity(), mAccount, mQuery, false));
                        }
                        updateSpinner();
                    }
                }

            };
            if(newer)
                createAndRunDbCleanup(getActivity().getApplicationContext(), mAccount, runnable);
            else
                runnable.run();
        }
    }

    public final void loadContent()
    {
        getLoaderManager().restartLoader(2, null, this);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mQuery = bundle.getString("query");
            mDelayedQuery = bundle.getString("delayed_query");
            prepareLoaderUri();
            getLoaderManager().initLoader(2, null, this);
        } else
        {
            mDelayedQuery = getArguments().getString("query");
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	if(1 == i) {
    		return null;
    	}
    	
    	return super.onCreateLoader(i, bundle);
    }

    public final void onDestroy()
    {
        super.onDestroy();
        FragmentActivity fragmentactivity = getActivity();
        if(fragmentactivity.isFinishing())
            createAndRunDbCleanup(fragmentactivity.getApplicationContext(), mAccount, null);
    }

    public final void onLoadFinished(Loader loader, Cursor cursor) {
    	
        int id = loader.getId();
        if(2 == id) {
        	super.onLoadFinished(loader, cursor);
            if(TextUtils.equals(mQuery, getArguments().getString("query")))
                mSearchViewAdapter.hideSoftInput();
        } else if(3 == id) {
        	android.view.View view;
            saveScrollPosition();
            mInnerAdapter.setMarkPostsAsRead(false);
            mInnerAdapter.changeStreamCursor(cursor);
            checkResetAnimationState();
            mEndOfStream = false;
            mPreloadRequested = false;
            view = getView();
            if(mError) {
            	showEmptyView(getView(), getString(R.string.people_list_error));
            } else {
            	if(cursor != null && cursor.getCount() > 0)
                {
                    showContent(view);
                    mEndOfStream = TextUtils.isEmpty(mContinuationToken);
                } else
                if(mNewerReqId != null || mOlderReqId != null)
                    showEmptyViewProgress(view);
                else
                if(!TextUtils.isEmpty(mQuery))
                {
                    if(mFirstLoad)
                        fetchContent(true);
                    else
                        showEmptyView(view, getString(R.string.no_posts));
                } else
                {
                    showContent(view);
                }
                mFirstLoad = false;
            }
            
            restoreScrollPosition();
            updateSpinner();
        }
        
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        onLoadFinished(loader, (Cursor)obj);
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mPostsSearchServiceListener);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showSearchView();
        mSearchViewAdapter = hostactionbar.getSearchViewAdapter();
        mSearchViewAdapter.setQueryHint(R.string.search_posts_hint_text);
        mSearchViewAdapter.addOnChangeListener(this);
        mSearchViewAdapter.requestFocus(true);
    }

    public final void onQueryClose()
    {
    }

    public final void onQueryTextChanged(CharSequence charsequence)
    {
    }

    public final void onQueryTextSubmitted(CharSequence charsequence)
    {
        String s = charsequence.toString().trim();
        if(!TextUtils.equals(s, mQuery))
            mResetAnimationState = true;
        mQuery = s;
        doSearch();
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mPostsSearchServiceListener);
        if(mNewerReqId == null) {
        	if(mOlderReqId != null && !EsService.isRequestPending(mOlderReqId.intValue()))
            {
                ServiceResult serviceresult = EsService.removeResult(mOlderReqId.intValue());
                mOlderReqId = null;
                if(!serviceresult.hasError())
                    loadContent();
            } 
        } else { 
        	if(!EsService.isRequestPending(mNewerReqId.intValue()))
            {
                ServiceResult serviceresult1 = EsService.removeResult(mNewerReqId.intValue());
                mNewerReqId = null;
                if(!serviceresult1.hasError())
                    loadContent();
            }
        }
        
        if(mDelayedQuery != null)
        {
            mQuery = mDelayedQuery;
            mDelayedQuery = null;
            mSearchViewAdapter.setQueryText(mQuery);
            doSearch();
        }

    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putString("query", mQuery);
        bundle.putString("delayed_query", mDelayedQuery);
    }

    protected final void prepareLoaderUri()
    {
        if(TextUtils.isEmpty(mQuery))
            mPostsUri = EsProvider.buildStreamUri(mAccount, "com.google.android.apps.plus.INVALID_SEARCH_QUERY");
        else
            mPostsUri = EsProvider.buildStreamUri(mAccount, SearchUtils.getSearchKey(mQuery));
    }

}
