/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpLoader extends CursorLoader {

	private final Uri mActivityUri;
    private final Uri mCommentsUri;
    private boolean mNeedToRefreshComments;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private boolean mObserverRegistered;
    
	public StreamOneUpLoader(Context context, EsAccount esaccount, String s)
    {
        super(context);
        mActivityUri = EsProvider.buildActivityViewUri(esaccount, s);
        android.net.Uri.Builder builder = EsProvider.COMMENTS_VIEW_BY_ACTIVITY_ID_URI.buildUpon();
        builder.appendPath(s);
        EsProvider.appendAccountParameter(builder, esaccount);
        mCommentsUri = builder.build();
    }

    public final Cursor loadInBackground() {
        // TODO
    	return null;
    }

    public final boolean needToRefreshComments()
    {
        return mNeedToRefreshComments;
    }

    protected final void onAbandon()
    {
        if(mObserverRegistered)
        {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
            mObserverRegistered = false;
        }
    }

    protected final void onReset()
    {
        cancelLoad();
        super.onReset();
        onAbandon();
    }

    protected final void onStartLoading()
    {
        super.onStartLoading();
        if(!mObserverRegistered)
        {
            ContentResolver contentresolver = getContext().getContentResolver();
            contentresolver.registerContentObserver(mActivityUri, false, mObserver);
            contentresolver.registerContentObserver(mCommentsUri, false, mObserver);
            mObserverRegistered = true;
        }
    }

    protected final void onStopLoading()
    {
    }

}
