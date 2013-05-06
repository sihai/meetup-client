/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.request.PublishEventRequest;
import com.galaxy.meetup.server.client.v2.response.PublishEventResponse;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class CreateEventOperation extends PlusiOperation {

	private static final List EVENT_EMBED_TYPES = Collections.unmodifiableList(Arrays.asList(new String[] {
        "PLUS_EVENT", "EVENT", "THING"
    }));
    private final AudienceData mAudience;
    private final String mExternalId;
    private final Event mEvent;
	    
	public CreateEventOperation(Context context, EsAccount esaccount, Event event, AudienceData audiencedata, String s, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, esaccount, "publish_event", intent, operationlistener, PublishEventResponse.class);
        mEvent = event;
        mAudience = audiencedata;
        mExternalId = s;
    }

    protected final void handleResponse(Response response) throws IOException {
    	PublishEventResponse pr = (PublishEventResponse)response;
    	if(pr.isSucceed()) {
    		EsEventData.insertEvent(mContext, mAccount, pr.getEvent());
    	}
    }

    @Override
    protected final com.galaxy.meetup.server.client.v2.request.Request populateRequest() {
        PublishEventRequest request = new PublishEventRequest();
        request.setEvent(mEvent);
        return request;
    }

}
