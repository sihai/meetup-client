/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.UpdateNotificationsLastReadTimeRequest;
import com.galaxy.meetup.server.client.domain.response.UpdateNotificationsLastReadTimeResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SetNotificationLastReadTimeOperation extends PlusiOperation {
	
	private final double mReadTimestamp;
	
	public SetNotificationLastReadTimeOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, double d)
    {
        super(context, esaccount, "updatenotificationslastreadtime", intent, operationlistener, UpdateNotificationsLastReadTimeResponse.class);
        mReadTimestamp = d;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        onStartResultProcessing();
    }

    protected final Request populateRequest()
    {
    	UpdateNotificationsLastReadTimeRequest response = new UpdateNotificationsLastReadTimeRequest();
    	response.timeMs = Double.valueOf(mReadTimestamp);
    	return response;
    }
}
