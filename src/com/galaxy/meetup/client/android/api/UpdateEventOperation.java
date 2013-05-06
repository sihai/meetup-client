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
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.request.UpdateEventRequest;
import com.galaxy.meetup.server.client.v2.response.Response;
import com.galaxy.meetup.server.client.v2.response.UpdateEventResponse;

/**
 * 
 * @author sihai
 *
 */
public class UpdateEventOperation extends PlusiOperation {

	private Event mEvent;
	
	public UpdateEventOperation(Context context, EsAccount esaccount,
			Event event, Intent intent,
			HttpOperation.OperationListener operationlistener) {
		super(context, esaccount, "update_event", intent, operationlistener,
				UpdateEventResponse.class);
		mEvent = event;
	}

	protected final void handleResponse(Response response) throws IOException {
		Event event = ((UpdateEventResponse) response).getEvent();
		if (null != event) {
			mEvent.setName(event.getName());
			mEvent.setDescription(event.getDescription());
			mEvent.setTheme(event.getTheme());
			mEvent.setStartTime(event.getStartTime());
			mEvent.setEndTime(event.getEndTime());
			mEvent.setLocation(event.getLocation());
			EsEventData.insertEvent(mContext, mAccount, mEvent);
		}
	}

	protected final Request populateRequest() {
		UpdateEventRequest request = new UpdateEventRequest();
		request.setEvent(mEvent);
		return request;
	}
}
