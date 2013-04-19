/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.api.CheckNearbyStreamChangeOperation;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class NearbyStreamChangeLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private boolean mError;
    private boolean mHasStreamChanged;
    private final DbLocation mLocation;
    
	public NearbyStreamChangeLoader(Context context, EsAccount esaccount, DbLocation dblocation)
    {
        super(context, null);
        mAccount = esaccount;
        mLocation = dblocation;
    }

    public final Cursor esLoadInBackground()
    {
        CheckNearbyStreamChangeOperation checknearbystreamchangeoperation = new CheckNearbyStreamChangeOperation(getContext(), mAccount, mLocation, null, null);
        checknearbystreamchangeoperation.start();
        mError = checknearbystreamchangeoperation.hasError();
        if(!mError)
            mHasStreamChanged = checknearbystreamchangeoperation.hasStreamChanged();
        return null;
    }

    public final boolean hasError()
    {
        return mError;
    }

    public final boolean hasStreamChanged()
    {
        return mHasStreamChanged;
    }

}
