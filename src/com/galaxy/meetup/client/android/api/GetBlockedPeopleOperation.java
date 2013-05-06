/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.response.LoadBlockedPeopleResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetBlockedPeopleOperation extends PlusiOperation {

	public GetBlockedPeopleOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "loadblockedpeople", intent, operationlistener, LoadBlockedPeopleResponse.class);
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        LoadBlockedPeopleResponse loadblockedpeopleresponse = (LoadBlockedPeopleResponse)response;
        EsPeopleData.insertBlockedPeople(mContext, getAccount(), loadblockedpeopleresponse.person);
    }

    protected final Request populateRequest()
    {
    	return new Request();
    }
}
