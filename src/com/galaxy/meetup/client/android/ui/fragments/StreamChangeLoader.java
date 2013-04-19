/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.api.CheckStreamChangeOperation;
import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class StreamChangeLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final String mCircleId;
    private boolean mError;
    private final boolean mFromWidget = false;
    private final String mGaiaId;
    private boolean mHasStreamChanged;
    private final String mSquareStreamId;
    private final int mView;
    
	public StreamChangeLoader(Context context, EsAccount esaccount, int i, String s, String s1, String s2, boolean flag)
    {
        super(context, null);
        mAccount = esaccount;
        mView = i;
        mCircleId = s;
        mGaiaId = s1;
        mSquareStreamId = s2;
    }

    public final Cursor esLoadInBackground()
    {
        CheckStreamChangeOperation checkstreamchangeoperation = new CheckStreamChangeOperation(getContext(), mAccount, mView, mCircleId, mGaiaId, mSquareStreamId, mFromWidget, null, null);
        checkstreamchangeoperation.start();
        mError = checkstreamchangeoperation.hasError();
        if(!mError)
            mHasStreamChanged = checkstreamchangeoperation.hasStreamChanged();
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
