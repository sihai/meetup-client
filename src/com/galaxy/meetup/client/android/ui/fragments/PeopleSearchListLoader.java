/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class PeopleSearchListLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private final String mProjection[];
    private Uri mQueryUri;
    
    public PeopleSearchListLoader(Context context, EsAccount esaccount, String as[], String s, boolean flag, boolean flag1, boolean flag2, 
            String s1, int i)
    {
        super(context);
        setUri(EsProvider.CONTACTS_URI);
        String s2;
        if(flag2)
            s2 = "gaia_id IS NOT NULL";
        else
            s2 = null;
        setSelection(s2);
        mAccount = esaccount;
        mProjection = as;
        mQueryUri = EsProvider.buildPeopleQueryUri(mAccount, s, flag, flag1, s1, 10);
    }

    public final Cursor esLoadInBackground()
    {
        Context context = getContext();
        boolean flag = EsPeopleData.ensurePeopleSynced(context, mAccount);
        Cursor cursor = null;
        if(flag)
        {
            Cursor cursor1 = context.getContentResolver().query(mQueryUri, mProjection, getSelection(), null, null);
            if(cursor1 != null)
                cursor1.registerContentObserver(mObserver);
            cursor = cursor1;
        }
        return cursor;
    }
}
