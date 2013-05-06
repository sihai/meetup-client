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
import com.galaxy.meetup.server.client.domain.EventSelector;
import com.galaxy.meetup.server.client.domain.request.DeleteEventRequest;
import com.galaxy.meetup.server.client.domain.response.DeleteEventResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class DeleteEventOperation extends PlusiOperation {

	private String mAuthKey;
    private String mEventId;
    
    public DeleteEventOperation(Context context, EsAccount esaccount, String s, String s1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "deleteevent", intent, operationlistener, DeleteEventResponse.class);
        mEventId = s;
        mAuthKey = s1;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        EsEventData.deleteEvent(mContext, mAccount, mEventId);
    }

    protected final Request populateRequest()
    {
    	
        DeleteEventRequest deleteeventrequest = new DeleteEventRequest();
        deleteeventrequest.eventId = mEventId;
        EventSelector eventselector = new EventSelector();
        eventselector.eventId = mEventId;
        eventselector.authKey = mAuthKey;
        deleteeventrequest.eventSelector = eventselector;
        return deleteeventrequest;
    }

}
