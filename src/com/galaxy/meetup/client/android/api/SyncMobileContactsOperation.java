/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.SyncMobileContactsRequest;
import com.galaxy.meetup.server.client.domain.response.SyncMobileContactsResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SyncMobileContactsOperation extends PlusiOperation {

	private final List mContacts;
    private final String mDevice;
    private boolean mSuccess;
    private String mSyncType;
    
	public SyncMobileContactsOperation(Context context, EsAccount esaccount, String s, List list, String s1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "syncmobilecontacts", intent, operationlistener, SyncMobileContactsResponse.class);
        mSyncType = "FULL";
        mSuccess = false;
        mDevice = s;
        mContacts = list;
        mSyncType = s1;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        SyncMobileContactsResponse syncmobilecontactsresponse = (SyncMobileContactsResponse)response;
        boolean flag;
        if(!TextUtils.isEmpty(syncmobilecontactsresponse.status) && syncmobilecontactsresponse.status.equals("SUCCESS"))
            flag = true;
        else
            flag = false;
        mSuccess = flag;
        if(mSuccess && mSyncType.equals("WIPEOUT"))
            EsAccountsData.saveContactsStatsWipeoutNeeded(mContext, mAccount, false);
    }

    protected final Request populateRequest()
    {
        SyncMobileContactsRequest syncmobilecontactsrequest = new SyncMobileContactsRequest();
        syncmobilecontactsrequest.device = mDevice;
        syncmobilecontactsrequest.type = mSyncType;
        syncmobilecontactsrequest.contact = mContacts;
        return syncmobilecontactsrequest;
    }

}
