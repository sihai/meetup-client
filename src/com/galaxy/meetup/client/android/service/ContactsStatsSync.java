/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.galaxy.meetup.client.android.api.SyncMobileContactsOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.AndroidUtils;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class ContactsStatsSync {

	private static final String PROJECTION_FOR_ICS_AND_LATER[] = {
        "times_contacted", "last_time_contacted", "sourceid", "data_set"
    };
    private static final String PROJECTION_FOR_PRE_ICS[] = {
        "times_contacted", "last_time_contacted", "sourceid"
    };
    private boolean isFirstStatsSync;
    private final EsAccount mAccount;
    private final List mContacts = new ArrayList();
    private final Context mContext;
    private final EsSyncAdapterService.SyncState mSyncState;
    private long maxLastContacted;
    
    private ContactsStatsSync(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        maxLastContacted = -1L;
        mContext = context;
        mAccount = esaccount;
        mSyncState = syncstate;
    }

    public static void sync(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
       // TODO
    }

    private void upload()
    {
        // TODO
    }

    public static void wipeout(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        if(EsLog.isLoggable("ContactsStatsSync", 3))
            Log.d("ContactsStatsSync", "Contacts stats wipeout operation started");
        (new SyncMobileContactsOperation(context, esaccount, String.valueOf(AndroidUtils.getAndroidId(context)), null, "WIPEOUT", intent, operationlistener)).startThreaded();
        if(EsLog.isLoggable("ContactsStatsSync", 3))
            Log.d("ContactsStatsSync", "Contacts stats wipeout operation complete");
    }
}
