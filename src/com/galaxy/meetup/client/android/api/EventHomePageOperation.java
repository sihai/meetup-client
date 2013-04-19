/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.EventsHomeRequest;
import com.galaxy.meetup.server.client.domain.response.EventsHomeResponse;

/**
 * 
 * @author sihai
 *
 */
public class EventHomePageOperation extends PlusiOperation {

	public EventHomePageOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "eventhome", intent, operationlistener, EventsHomeResponse.class);
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
    	EventsHomeResponse r = (EventsHomeResponse)genericjson;
        EsEventData.insertEventHomeList(mContext, mAccount, r.getUpcoming(),  r.getDeclinedUpcoming(),  r.getPast(),  r.getResolvedPerson());
    }

    protected final GenericJson populateRequest()
    {
    	return new EventsHomeRequest();
    }
    
}
