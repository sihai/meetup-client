/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.List;

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
public class BlockedPeopleListLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final List mIncludedPersonIds;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private final String mProjection[];
    
	public BlockedPeopleListLoader(Context context, EsAccount esaccount, String as[], List arraylist)
    {
        super(context);
        setUri(EsProvider.CONTACTS_URI);
        mAccount = esaccount;
        mProjection = as;
        mIncludedPersonIds = arraylist;
    }

    public final Cursor esLoadInBackground()
    {
        Cursor cursor = EsPeopleData.getBlockedPeople(getContext(), mAccount, mProjection, mIncludedPersonIds);
        if(cursor != null)
            cursor.registerContentObserver(mObserver);
        return cursor;
    }

}
