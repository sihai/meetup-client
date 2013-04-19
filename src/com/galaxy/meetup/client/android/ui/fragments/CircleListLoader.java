/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class CircleListLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final int mMaxResults;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver;
    private final String mProjection[];
    private final String mQuery;
    private final int mUsageType;
    
	public CircleListLoader(Context context, EsAccount esaccount, int i, String as[])
    {
        this(context, esaccount, i, as, null, 0);
    }

    public CircleListLoader(Context context, EsAccount esaccount, int i, String as[], String s, int j)
    {
        super(context);
        mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
        setUri(EsProvider.CIRCLES_URI);
        mAccount = esaccount;
        mProjection = as;
        mUsageType = i;
        mQuery = s;
        mMaxResults = j;
    }

    public final Cursor esLoadInBackground()
    {
        Cursor cursor = EsPeopleData.getCircles(getContext(), mAccount, mUsageType, mProjection, mQuery, mMaxResults);
        if(cursor != null)
            cursor.registerContentObserver(mObserver);
        return cursor;
    }
}
