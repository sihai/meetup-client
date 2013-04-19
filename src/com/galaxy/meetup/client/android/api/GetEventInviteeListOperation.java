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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.GetEventInviteeListRequest;
import com.galaxy.meetup.server.client.domain.response.GetEventInviteeListResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetEventInviteeListOperation extends PlusiOperation {

	private final String mAuthKey;
    private final String mEventId;
    private final boolean mIncludeBlacklist;
    
	public GetEventInviteeListOperation(Context context, EsAccount esaccount, String s, String s1, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "geteventinviteelist", intent, operationlistener, GetEventInviteeListResponse.class);
        mEventId = s;
        mIncludeBlacklist = flag;
        mAuthKey = s1;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetEventInviteeListResponse geteventinviteelistresponse = (GetEventInviteeListResponse)genericjson;
        EsEventData.insertEventInviteeList(mContext, mAccount, mEventId, geteventinviteelistresponse.invitee);
    }

    protected final GenericJson populateRequest()
    {
        GetEventInviteeListRequest geteventinviteelistrequest = new GetEventInviteeListRequest();
        geteventinviteelistrequest.eventId = mEventId;
        geteventinviteelistrequest.includeAdminBlacklist = Boolean.valueOf(mIncludeBlacklist);
        EventSelector eventselector = new EventSelector();
        eventselector.eventId = mEventId;
        eventselector.authKey = mAuthKey;
        geteventinviteelistrequest.eventSelector = eventselector;
        return geteventinviteelistrequest;
    }

}
