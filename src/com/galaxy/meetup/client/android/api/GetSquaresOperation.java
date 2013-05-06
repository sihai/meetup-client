/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsSquaresData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.server.client.domain.request.GetSquaresOzRequest;
import com.galaxy.meetup.server.client.domain.response.GetSquaresOzResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetSquaresOperation extends PlusiOperation {

	private final EsSyncAdapterService.SyncState mSyncState = null;
	
	public GetSquaresOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, EsSyncAdapterService.SyncState syncstate)
    {
        super(context, esaccount, "getsquares", intent, operationlistener, GetSquaresOzResponse.class);
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        GetSquaresOzResponse getsquaresozresponse = (GetSquaresOzResponse)response;
        Object obj;
        Object obj1;
        Object obj2;
        int i;
        if(getsquaresozresponse.invitedSquare != null)
            obj = getsquaresozresponse.invitedSquare;
        else
            obj = new ArrayList();
        if(getsquaresozresponse.joinedSquare != null)
            obj1 = getsquaresozresponse.joinedSquare;
        else
            obj1 = new ArrayList();
        if(getsquaresozresponse.suggestedSquare != null)
            obj2 = getsquaresozresponse.suggestedSquare;
        else
            obj2 = new ArrayList();
        i = EsSquaresData.insertSquares(mContext, mAccount, ((java.util.List) (obj)), ((java.util.List) (obj1)), ((java.util.List) (obj2)));
        if(mSyncState != null)
            mSyncState.incrementCount(i);
    }

    protected final Request populateRequest()
    {
        GetSquaresOzRequest getsquaresozrequest = new GetSquaresOzRequest();
        getsquaresozrequest.includePeopleInCommon = Boolean.valueOf(false);
        ArrayList arraylist = new ArrayList(3);
        arraylist.add("INVITED");
        arraylist.add("JOINED");
        arraylist.add("SUGGESTED");
        getsquaresozrequest.squareType = arraylist;
        return getsquaresozrequest;
    }

}
