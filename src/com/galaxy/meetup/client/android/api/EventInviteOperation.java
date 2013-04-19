/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.EventSelector;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.InviteEventRequest;

/**
 * 
 * @author sihai
 *
 */
public class EventInviteOperation extends PlusiOperation {

	private final AudienceData mAudience;
    private final String mAuthKey;
    private final String mEventId;
    private final String mOwnerId;
    
	public EventInviteOperation(Context context, EsAccount esaccount, String s, String s1, String s2, AudienceData audiencedata, Intent intent,  HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "inviteevent", intent, operationlistener, GenericJson.class);
        mEventId = s;
        mOwnerId = s2;
        mAuthKey = s1;
        mAudience = audiencedata;
    }

    protected final void handleResponse(GenericJson genericjson)
    {
    }

    protected final GenericJson populateRequest()
    {
        InviteEventRequest inviteeventrequest = new InviteEventRequest();
        inviteeventrequest.eventId = mEventId;
        inviteeventrequest.organizerId = mOwnerId;
        inviteeventrequest.inviteRoster = EsPeopleData.convertAudienceToSharingRoster(mAudience);
        EventSelector eventselector = new EventSelector();
        eventselector.eventId = mEventId;
        eventselector.authKey = mAuthKey;
        inviteeventrequest.eventSelector = eventselector;
        return inviteeventrequest;
    }

}