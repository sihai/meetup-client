/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.api.SquareSearchQueryOperation;
import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class SquareSearchLoader extends AsyncTaskLoader {

	public static final SquareSearchLoaderResults ABORTED = new SquareSearchLoaderResults();
    private final EsAccount mAccount;
    private final String mContinuationToken;
    private SquareSearchLoaderResults mData;
    private final int mMinQueryLength = 2;
    private volatile SquareSearchQueryOperation mOperation;
    private final String mQuery;
    
    public SquareSearchLoader(Context context, EsAccount esaccount, String s, int i, String s1)
    {
        super(context);
        mAccount = esaccount;
        mQuery = s;
        mContinuationToken = s1;
    }

    private void abort()
    {
        SquareSearchQueryOperation squaresearchqueryoperation = mOperation;
        if(squaresearchqueryoperation != null)
            squaresearchqueryoperation.abort();
        mOperation = null;
    }

    public SquareSearchLoaderResults loadInBackground() {
        if(TextUtils.isEmpty(mQuery) || mQuery.length() < mMinQueryLength) {
        	return new SquareSearchLoaderResults();
        }
        
        try {
        	SquareSearchLoaderResults squaresearchloaderresults = null;
	        Context context = getContext();
	        EsAccount esaccount = mAccount;
	        String s = mQuery;
	        SquareSearchQueryOperation squaresearchqueryoperation = new SquareSearchQueryOperation(context, esaccount, s, null, null);
	        mOperation = squaresearchqueryoperation;
	        SquareSearchLoaderResults squaresearchloaderresults1;
	        squaresearchqueryoperation.start();
	        if(squaresearchqueryoperation.isAborted()) {
	        	squaresearchloaderresults1 = ABORTED;
	        	mOperation = null;
	        	squaresearchloaderresults = squaresearchloaderresults1;
	        } else {
	        	mOperation = null;
	        	if(squaresearchqueryoperation.hasError())
	            {
	                squaresearchqueryoperation.logError("SquareSearch");
	                squaresearchloaderresults = null;
	            } else
	            {
	                squaresearchloaderresults = new SquareSearchLoaderResults(mContinuationToken, squaresearchqueryoperation.getContinuationToken(), squaresearchqueryoperation.getSquareSearchResults());
	            }
	        }
	        if(squaresearchqueryoperation.hasError())
	        {
	            squaresearchqueryoperation.logError("SquareSearch");
	            squaresearchloaderresults = null;
	        } else
	        {
	            squaresearchloaderresults = new SquareSearchLoaderResults(mContinuationToken, squaresearchqueryoperation.getContinuationToken(), squaresearchqueryoperation.getSquareSearchResults());
	        }
	        
	        return squaresearchloaderresults;
        } finally {
        	mOperation = null;
        }
        
    }

    public final boolean cancelLoad()
    {
        abort();
        return super.cancelLoad();
    }

    public final void deliverResult(Object obj)
    {
        SquareSearchLoaderResults squaresearchloaderresults = (SquareSearchLoaderResults)obj;
        if(!isReset())
        {
            mData = squaresearchloaderresults;
            if(isStarted())
                super.deliverResult(squaresearchloaderresults);
        }
    }

    public final String getContinuationToken()
    {
        return mContinuationToken;
    }

    public final void onAbandon()
    {
        abort();
    }

    protected final void onStartLoading()
    {
        if(mData == null)
            forceLoad();
    }
}
