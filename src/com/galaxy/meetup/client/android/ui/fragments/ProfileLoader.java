/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.Loader;

import com.galaxy.meetup.client.android.EsAsyncTaskLoader;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class ProfileLoader extends EsAsyncTaskLoader {

	private final EsAccount mAccount;
    private EsPeopleData.ProfileAndContactData mData;
    private final boolean mFullProfileNeeded;
    private final Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private boolean mObserverRegistered;
    private final String mPersonId;
    
    public ProfileLoader(Context context, EsAccount esaccount, String s, boolean flag)
    {
        super(context);
        mAccount = esaccount;
        mPersonId = s;
        mFullProfileNeeded = flag;
    }

    public final void deliverResult(Object obj)
    {
        EsPeopleData.ProfileAndContactData profileandcontactdata = (EsPeopleData.ProfileAndContactData)obj;
        if(!isReset())
        {
            mData = profileandcontactdata;
            if(isStarted())
                super.deliverResult(profileandcontactdata);
        }
    }

    public final Object esLoadInBackground()
    {
        return EsPeopleData.getProfileAndContactData(getContext(), mAccount, mPersonId, mFullProfileNeeded);
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
        super.onReset();
        mData = null;
    }

    protected final void onStartLoading()
    {
        if(!mObserverRegistered)
        {
            getContext().getContentResolver().registerContentObserver(Uri.withAppendedPath(EsProvider.CONTACT_BY_PERSON_ID_URI, mPersonId), false, mObserver);
            mObserverRegistered = true;
        }
        if(mData == null)
            forceLoad();
    }
}
