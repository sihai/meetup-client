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
public class SuggestedPeopleListLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private boolean mFirstRun;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private final String mProjection[];
    private final boolean mRefreshDataOnStart;
    
	public SuggestedPeopleListLoader(Context context, EsAccount esaccount, String as[], boolean flag)
    {
        super(context);
        mFirstRun = true;
        mRefreshDataOnStart = flag;
        setUri(EsProvider.CONTACTS_URI);
        mAccount = esaccount;
        mProjection = as;
    }

    public final Cursor esLoadInBackground()
    {
        Context context = getContext();
        EsAccount esaccount = mAccount;
        String as[] = mProjection;
        boolean flag;
        Cursor cursor;
        if(mFirstRun && mRefreshDataOnStart)
            flag = true;
        else
            flag = false;
        cursor = EsPeopleData.getSuggestedPeople(context, esaccount, as, flag, false);
        if(cursor != null)
        {
            mFirstRun = false;
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }

}
