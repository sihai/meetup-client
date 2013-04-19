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
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.request.UpdateEventRequest;
import com.galaxy.meetup.server.client.domain.response.UpdateEventResponse;

/**
 * 
 * @author sihai
 *
 */
public class UpdateEventOperation extends PlusiOperation {

	private PlusEvent mPlusEvent;
	
	public UpdateEventOperation(Context context, EsAccount esaccount, PlusEvent plusevent, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "updateevent", intent, operationlistener, UpdateEventResponse.class);
        mPlusEvent = plusevent;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        PlusEvent plusevent = ((UpdateEventResponse)genericjson).event;
        if(plusevent != null)
        {
            mPlusEvent.setName(plusevent.getName());
            mPlusEvent.setDescription(plusevent.getDescription());
            mPlusEvent.setTheme(plusevent.getTheme());
            mPlusEvent.setStartTime(plusevent.getStartTime());
            mPlusEvent.setStartDate(plusevent.getStartDate());
            mPlusEvent.setEndTime(plusevent.getEndTime());
            mPlusEvent.setEndDate(plusevent.getEndDate());
            mPlusEvent.setLocation(plusevent.getLocation());
            mPlusEvent.setEventOptions(plusevent.getEventOptions());
            mPlusEvent.setDisplayContent(plusevent.getDisplayContent());
            mPlusEvent.setDescription(plusevent.getDescription());
            EsEventData.insertEvent(mContext, mAccount, null, mPlusEvent, null);
        }
    }

    protected final GenericJson populateRequest()
    {
    	UpdateEventRequest genericjson = new UpdateEventRequest();
        genericjson.event = mPlusEvent;
        return genericjson;
    }
}
