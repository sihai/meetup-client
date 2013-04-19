/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.api.GetSquaresOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.EsSquaresData;

/**
 * 
 * @author sihai
 *
 */
public class SquareListLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private boolean mIsDataStale;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private final String mProjection[];
    
    public SquareListLoader(Context context, EsAccount esaccount, String as[])
    {
        super(context);
        setUri(EsProvider.SQUARES_URI);
        mAccount = esaccount;
        mProjection = as;
    }

    public final Cursor esLoadInBackground()
    {
        Cursor cursor;
        Context context = getContext();
        long l = EsSquaresData.queryLastSquaresSyncTimestamp(getContext(), mAccount);
        boolean flag;
        GetSquaresOperation getsquaresoperation;
        boolean flag1;
        if(System.currentTimeMillis() - l > 0xdbba0L)
            flag = true;
        else
            flag = false;
        mIsDataStale = flag;
        if(l <= 0L) {
        	getsquaresoperation = new GetSquaresOperation(context, mAccount, null, null, null);
            getsquaresoperation.start();
            flag1 = getsquaresoperation.hasError();
            if(flag1) {
            	return null;
            }
        }
        
        Cursor cursor1 = EsSquaresData.getJoinedSquares(getContext(), mAccount, mProjection, "square_name");
        if(cursor1 != null)
            cursor1.registerContentObserver(mObserver);
        cursor = cursor1;
        return cursor;
    }

    public final boolean isDataStale()
    {
        return mIsDataStale;
    }
}
