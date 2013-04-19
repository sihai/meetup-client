/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.EsCompositeCursorAdapter;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.SquareListItemView;
import com.galaxy.meetup.client.android.ui.view.SquareListItemView.OnItemClickListener;

/**
 * 
 * @author sihai
 *
 */
public class SquareSearchAdapter extends EsCompositeCursorAdapter implements
		LoaderCallbacks, OnItemClickListener {

	private static int sMinWidth;
    protected final EsAccount mAccount;
    private boolean mError;
    private final Handler mHandler;
    private boolean mLandscape;
    protected SearchListAdapterListener mListener;
    private final LoaderManager mLoaderManager;
    private boolean mLoading;
    private boolean mNotFound;
    protected String mQuery;
    private SquareSearchResults mResults;
    private boolean mResultsPreserved;
    private boolean mShowProgressWhenEmpty;
    private final int mSquaresLoaderId;
    
    public SquareSearchAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount)
    {
        this(context, fragmentmanager, loadermanager, esaccount, 0);
    }

    private SquareSearchAdapter(Context context, FragmentManager fragmentmanager, LoaderManager loadermanager, EsAccount esaccount, int i) {
    	super(context);
        boolean flag = true;
        mShowProgressWhenEmpty = flag;
        mResults = new SquareSearchResults(HostedSquareListFragment.Query.PROJECTION);
        mHandler = new Handler() {

            public final void handleMessage(Message message) {
            	if(0 == message.what) {
            		showEmptySearchResults();
            	} else if(1 == message.what) {
            		mLoading = true;
                    updateSearchStatus();
                    if(mListener != null)
                        mListener.onSearchListAdapterStateChange();
            	} 
            }
        };
        for(int j = 0; j < 2; j++)
            addPartition(false, false);

        mSquaresLoaderId = 1024;
        SearchResultsFragment searchresultsfragment = (SearchResultsFragment)fragmentmanager.findFragmentByTag("square_search_results");
        if(searchresultsfragment == null)
        {
            searchresultsfragment = new SearchResultsFragment();
            fragmentmanager.beginTransaction().add(searchresultsfragment, "square_search_results").commitAllowingStateLoss();
        } else
        {
            SquareSearchResults squaresearchresults = searchresultsfragment.getSquareSearchResults();
            if(squaresearchresults != null)
            {
                mResults = squaresearchresults;
                mQuery = mResults.getQuery();
                mResultsPreserved = flag;
            }
        }
        searchresultsfragment.setSquareSearchResults(mResults);
        mLoaderManager = loadermanager;
        mAccount = esaccount;
        if(context.getResources().getConfiguration().orientation != 2)
            flag = false;
        mLandscape = flag;
        if(sMinWidth == 0)
            sMinWidth = context.getResources().getDimensionPixelSize(R.dimen.square_card_min_width);
    }

    private void updateSearchStatus() {
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(new String[] {
            "_id"
        });
        if(!TextUtils.isEmpty(mQuery) && mQuery.trim().length() >= 2) {
        	if(mError) {
        		Object aobj2[] = new Object[1];
                aobj2[0] = Integer.valueOf(3);
                esmatrixcursor.addRow(aobj2);
        	} else { 
        		if(mNotFound)
                {
                    Object aobj1[] = new Object[1];
                    aobj1[0] = Integer.valueOf(2);
                    esmatrixcursor.addRow(aobj1);
                } else
                if(mLoading && (mShowProgressWhenEmpty || mResults.getCount() > 0))
                {
                    Object aobj[] = new Object[1];
                    aobj[0] = Integer.valueOf(1);
                    esmatrixcursor.addRow(aobj);
                }
        	}
        }
        
        if(esmatrixcursor.getCount() != 0)
            showEmptySearchResults();
        changeCursor(1, esmatrixcursor);
        return;
    }

    protected final void bindView(View view, int i, Cursor cursor, int j) {
    	
    	byte byte0;
        byte byte1;
        byte byte2;
        int k;
        int l;
    	if(0 == i) {
    		((SquareListItemView)view).init(cursor, this, true, false);
            if(j == -1 + cursor.getCount() && mResults.hasMoreResults())
                mHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
					}
                	
                });
    	} else if(1 == i) {
    		byte0 = 8;
            byte1 = 8;
            byte2 = 8;
            int value = cursor.getInt(0);
            if(1 == value) {
            	byte0 = 0;
            } else if(2 == value) {
            	byte1 = 0;
            } else if(3 == value) {
            	byte2 = 0;
            }
            view.findViewById(R.id.loading).setVisibility(byte0);
            view.findViewById(R.id.not_found).setVisibility(byte1);
            view.findViewById(R.id.error).setVisibility(byte2);
    	}
    	
        if(mLandscape)
            k = 1;
        else
            k = 2;
        if(mLandscape)
            l = sMinWidth;
        else
            l = -2;
        view.setLayoutParams(new ColumnGridView.LayoutParams(k, l, 1, 1));
        return;
        
    }

    protected final int getItemViewType(int i, int j)
    {
        return i;
    }

    public final int getItemViewTypeCount()
    {
        return 2;
    }

    public final boolean isEmpty()
    {
        return TextUtils.isEmpty(mQuery);
    }

    protected final View newView(Context context, int partion, Cursor cursor, int position, ViewGroup parent) {
        LayoutInflater layoutinflater = LayoutInflater.from(context);
        View view = null;
        if(1 == partion) {
        	view = layoutinflater.inflate(R.layout.square_search_status_card, parent, false);
        } else {
        	view = layoutinflater.inflate(R.layout.square_list_item_view, parent, false);
        }
        return view;
        
    }

    public final void onClick(String s)
    {
        if(mListener != null)
            mListener.onSquareSelected(s);
    }

    public final void onCreate(Bundle bundle)
    {
        if(bundle != null)
        {
            bundle.setClassLoader(getClass().getClassLoader());
            mQuery = bundle.getString("search_list_adapter.query");
            if(bundle.containsKey("search_list_adapter.results") && !mResultsPreserved)
                mResults = (SquareSearchResults)bundle.getParcelable("search_list_adapter.results");
        }
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        SquareSearchLoader squaresearchloader;
        if(i == mSquaresLoaderId)
        {
            Context context = getContext();
            EsAccount esaccount = mAccount;
            String[] _tmp = HostedSquareListFragment.Query.PROJECTION;
            squaresearchloader = new SquareSearchLoader(context, esaccount, mQuery, 2, mResults.getContinuationToken());
        } else
        {
            squaresearchloader = null;
        }
        return squaresearchloader;
    }

    public final void onInvitationDismissed(String s)
    {
    }

    public final void onInviterImageClick(String s)
    {
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        boolean flag = true;
        SquareSearchLoaderResults squaresearchloaderresults = (SquareSearchLoaderResults)obj;
        if(loader.getId() == mSquaresLoaderId)
        {
            if(squaresearchloaderresults != SquareSearchLoader.ABORTED)
            {
                mHandler.removeMessages(1);
                boolean flag1;
                Cursor cursor;
                if(squaresearchloaderresults == null)
                    flag1 = flag;
                else
                    flag1 = false;
                mError = flag1;
                if(mError)
                    mLoading = false;
                else
                if(TextUtils.equals(squaresearchloaderresults.getToken(), mResults.getContinuationToken()) && (mResults.isEmpty() || mResults.hasMoreResults()))
                {
                    mLoading = false;
                    String s = squaresearchloaderresults.getNextToken();
                    mResults.setContinuationToken(s);
                    SquareSearchResults squaresearchresults = mResults;
                    boolean flag2;
                    if(!TextUtils.isEmpty(s))
                        flag2 = flag;
                    else
                        flag2 = false;
                    squaresearchresults.setHasMoreResults(flag2);
                    mResults.addResults(squaresearchloaderresults.getResults());
                    if(mResults.getCount() != 0)
                        flag = false;
                    mNotFound = flag;
                }
                mHandler.removeMessages(0);
                cursor = mResults.getCursor();
                if(cursor.getCount() == 0)
                    mHandler.sendEmptyMessageDelayed(0, 500L);
                else
                    changeCursor(0, cursor);
            }
            updateSearchStatus();
            if(mListener != null)
                mListener.onSearchListAdapterStateChange();
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        bundle.putString("search_list_adapter.query", mQuery);
        if(mResults.isParcelable())
            bundle.putParcelable("search_list_adapter.results", mResults);
    }

    public final void onStart()
    {
        Bundle bundle = new Bundle();
        bundle.putString("query", mQuery);
        mLoaderManager.initLoader(mSquaresLoaderId, bundle, this);
        updateSearchStatus();
    }

    public final void onStop()
    {
        mHandler.removeMessages(0);
    }

    public final void setListener(SearchListAdapterListener searchlistadapterlistener)
    {
        mListener = searchlistadapterlistener;
    }

    public final void setQueryString(String s) {
        if(TextUtils.equals(mQuery, s)) 
        	return; 
       
        mResults.setQueryString(s);
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mQuery = s;
        if(TextUtils.isEmpty(s))
        {
            mLoaderManager.destroyLoader(mSquaresLoaderId);
            clearPartitions();
            if(mListener != null)
                mListener.onSearchListAdapterStateChange();
        } else
        {
            Bundle bundle = new Bundle();
            bundle.putString("query", mQuery);
            mError = false;
            mNotFound = false;
            mLoading = false;
            mHandler.sendEmptyMessageDelayed(1, 300L);
            mLoaderManager.destroyLoader(mSquaresLoaderId);
            mLoaderManager.initLoader(mSquaresLoaderId, bundle, this);
            updateSearchStatus();
        }
    }

    protected final void showEmptySearchResults()
    {
        mHandler.removeMessages(0);
        Cursor cursor = mResults.getCursor();
        if(cursor.getCount() == 0)
            changeCursor(0, cursor);
    }
    
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    public interface SearchListAdapterListener
    {

        public abstract void onSearchListAdapterStateChange();

        public abstract void onSquareSelected(String s);
    }

    public static class SearchResultsFragment extends Fragment {
    	
    	private SquareSearchResults mResults;

        public SearchResultsFragment()
        {
            setRetainInstance(true);
        }
        
        public final SquareSearchResults getSquareSearchResults()
        {
            return mResults;
        }

        public final void setSquareSearchResults(SquareSearchResults squaresearchresults)
        {
            mResults = squaresearchresults;
        }
    }
}
