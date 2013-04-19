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
public class PeopleListLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final String mCircleId;
    private final String mExcludedCircleId;
    private final boolean mFilterNullGaiaIds;
    private final boolean mIncludePlusPages;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver;
    private final String mProjection[];
    
    public PeopleListLoader(Context context, EsAccount esaccount, String as[], String s)
    {
        super(context);
        mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
        setUri(EsProvider.CONTACTS_URI);
        mAccount = esaccount;
        mProjection = as;
        mCircleId = s;
        mExcludedCircleId = null;
        mIncludePlusPages = true;
        mFilterNullGaiaIds = false;
    }

    public PeopleListLoader(Context context, EsAccount esaccount, String as[], String s, boolean flag, boolean flag1)
    {
        super(context);
        mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
        setUri(EsProvider.CONTACTS_URI);
        mAccount = esaccount;
        mProjection = as;
        mCircleId = null;
        mExcludedCircleId = s;
        mIncludePlusPages = flag;
        mFilterNullGaiaIds = flag1;
    }

    public final Cursor esLoadInBackground()
    {
        String s = "in_my_circles=1";
        if(mFilterNullGaiaIds)
            s = (new StringBuilder()).append(s).append(" AND gaia_id IS NOT NULL").toString();
        if(!mIncludePlusPages)
            s = (new StringBuilder()).append(s).append(" AND profile_type!=2").toString();
        Cursor cursor = EsPeopleData.getPeople(getContext(), mAccount, mCircleId, mExcludedCircleId, mProjection, s, null);
        if(cursor != null)
            cursor.registerContentObserver(mObserver);
        return cursor;
    }
}
