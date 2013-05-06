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
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.EmbedsPerson;
import com.galaxy.meetup.server.client.domain.EventSelector;
import com.galaxy.meetup.server.client.domain.request.EventManageGuestsRequest;
import com.galaxy.meetup.server.client.domain.response.EventManageGuestsResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class EventManageGuestOperation extends PlusiOperation {

	private final String mAuthKey;
    private final boolean mBlacklist;
    private final String mEmail;
    private final String mEventId;
    private final String mGaiaId;
    
    public EventManageGuestOperation(Context context, EsAccount esaccount, String s, String s1, boolean flag, String s2, String s3, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "eventmanageguests", intent, operationlistener, EventManageGuestsResponse.class);
        mEventId = s;
        mAuthKey = s1;
        mBlacklist = flag;
        mGaiaId = s2;
        mEmail = s3;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        EventManageGuestsResponse eventmanageguestsresponse = (EventManageGuestsResponse)response;
        if(eventmanageguestsresponse.success != null && eventmanageguestsresponse.success.booleanValue())
            EsEventData.updateEventInviteeList(mContext, mAccount, mEventId, mBlacklist, mGaiaId, mEmail);
    }

    protected final Request populateRequest()
    {
        EventManageGuestsRequest eventmanageguestsrequest = new EventManageGuestsRequest();
        eventmanageguestsrequest.eventId = mEventId;
        String s;
        EventSelector eventselector;
        EmbedsPerson embedsperson;
        if(mBlacklist)
            s = "ADD";
        else
            s = "REMOVE";
        eventmanageguestsrequest.actionType = s;
        eventmanageguestsrequest.invitee = new ArrayList();
        eventselector = new EventSelector();
        eventselector.authKey = mAuthKey;
        eventselector.eventId = mEventId;
        eventmanageguestsrequest.eventSelector = eventselector;
        embedsperson = new EmbedsPerson();
        embedsperson.setOwnerObfuscatedId(mGaiaId);
        embedsperson.setEmail(mEmail);
        eventmanageguestsrequest.invitee.add(embedsperson);
        return eventmanageguestsrequest;
    }
}
