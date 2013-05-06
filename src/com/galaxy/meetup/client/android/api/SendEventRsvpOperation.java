/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.EventSelector;
import com.galaxy.meetup.server.client.domain.request.EventRespondRequest;
import com.galaxy.meetup.server.client.domain.response.EventRespondResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SendEventRsvpOperation extends PlusiOperation {
	
	private final String mAuthKey;
    private final String mEventId;
    private final String mRollbackRsvpType;
    private final String mRsvpType;
    
	public SendEventRsvpOperation(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, Intent intent, 
            HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "eventrespond", null, null, EventRespondResponse.class);
        mAuthKey = s1;
        mEventId = s;
        mRsvpType = s2;
        mRollbackRsvpType = s3;
    }

    private void rollback()
    {
        if(TextUtils.equals(EsEventData.getRsvpType(EsEventData.getPlusEvent(mContext, mAccount, mEventId)), mRsvpType))
            EsEventData.setRsvpType(mContext, mAccount, mEventId, mRollbackRsvpType);
        EsEventData.refreshEvent(mContext, mAccount, mEventId);
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        EventRespondResponse eventrespondresponse = (EventRespondResponse)response;
        if(eventrespondresponse.result != null && !TextUtils.equals(eventrespondresponse.result, Status.SUCCESS.name()))
            rollback();
        else
            EsEventData.setRsvpType(mContext, mAccount, mEventId, mRsvpType);
    }

    public final void onHttpOperationComplete(int i, String s, Exception exception)
    {
        if(i != 200 || exception != null)
            rollback();
    }

    protected final Request populateRequest()
    {
        EventRespondRequest eventrespondrequest = new EventRespondRequest();
        eventrespondrequest.eventId = mEventId;
        eventrespondrequest.response = mRsvpType;
        EventSelector eventselector = new EventSelector();
        eventselector.authKey = mAuthKey;
        eventselector.eventId = mEventId;
        eventrespondrequest.eventSelector = eventselector;
        return eventrespondrequest;
    }

    
	private static enum Status {
		UNKNOWN,
		SUCCESS,
		REJECTED_OFF_NETWORK_DISPLAY_NAME;
	}
}
