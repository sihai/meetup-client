/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.galaxy.meetup.client.android.api.GetViewerSquareOperation;
import com.galaxy.meetup.client.android.content.DbSquareStream;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class SquareCategoryLoader extends AsyncTaskLoader {

	private static final String PROJECTION[] = {
        "square_streams", "last_sync"
    };
    private final EsAccount mAccount;
    private DbSquareStream mData[];
    private boolean mIsDataStale;
    private final String mSquareId;
    
    public SquareCategoryLoader(Context context, EsAccount esaccount, String s)
    {
        super(context);
        mAccount = esaccount;
        mSquareId = s;
    }

    public final void deliverResult(Object obj)
    {
        DbSquareStream adbsquarestream[] = (DbSquareStream[])obj;
        if(!isReset())
        {
            mData = adbsquarestream;
            if(isStarted())
                super.deliverResult(adbsquarestream);
        }
    }

    public final boolean isDataStale()
    {
        return mIsDataStale;
    }

    public final Object loadInBackground() {
        Uri uri = EsProvider.appendAccountParameter(EsProvider.SQUARES_URI.buildUpon().appendPath(mSquareId), mAccount).build();
        Cursor cursor = null;
        try {
        	cursor = getContext().getContentResolver().query(uri, PROJECTION, null, null, null);
        	if(null != cursor && cursor.moveToFirst()) {
        		long l = cursor.getLong(1);
        		if(l > 0L) {
        			DbSquareStream adbsquarestream[];
        	        boolean flag2;
        	        if(System.currentTimeMillis() - l > 0xdbba0L)
        	            flag2 = true;
        	        else
        	            flag2 = false;
        	        mIsDataStale = flag2;
        	        return DbSquareStream.deserialize(cursor.getBlob(0));
        		}
        	}
        	DbSquareStream adbsquarestream[] = null;
        	GetViewerSquareOperation getviewersquareoperation = new GetViewerSquareOperation(getContext(), mAccount, mSquareId, null, null);
            getviewersquareoperation.start();
            boolean flag = getviewersquareoperation.hasError();
            if(!flag)
            {
                Cursor cursor1 = getContext().getContentResolver().query(uri, PROJECTION, null, null, null);
                if(cursor1 != null)
                {
                    boolean flag1 = cursor1.moveToFirst();
                    adbsquarestream = null;
                    if(flag1)
                        adbsquarestream = DbSquareStream.deserialize(cursor1.getBlob(0));
                }
            }
            return adbsquarestream;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    protected final void onStartLoading()
    {
        if(mData == null)
            forceLoad();
    }
}
