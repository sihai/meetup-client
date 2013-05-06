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
import com.galaxy.meetup.server.client.domain.request.EventsHomeRequest;
import com.galaxy.meetup.server.client.domain.response.EventsHomeResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class EventHomePageOperation extends PlusiOperation {

	public EventHomePageOperation(Context context, EsAccount esaccount,
			Intent intent, HttpOperation.OperationListener operationlistener) {
		super(context, esaccount, "event_home", intent, operationlistener, EventsHomeResponse.class);
	}

	protected final void handleResponse(Response response) throws IOException {
		EventsHomeResponse r = (EventsHomeResponse) response;
		EsEventData.insertEventHomeList(mContext, mAccount, r.getUpcoming(),
				r.getDeclinedUpcoming(), r.getPast(), r.getResolvedPerson());
	}

	protected final Request populateRequest() {
		return new EventsHomeRequest();
	}
    
}
