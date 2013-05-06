/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.SharePhotosToEventRequest;
import com.galaxy.meetup.server.client.domain.response.SharePhotosToEventResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SharePhotosToEventOperation extends PlusiOperation {

	final String mEventId;
    final List mPhotoIds;
    
    public SharePhotosToEventOperation(Context context, EsAccount esaccount, Intent intent, List list, String s, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "sharephotostoevent", intent, operationlistener, SharePhotosToEventResponse.class);
        mEventId = s;
        mPhotoIds = list;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
    }

    protected final Request populateRequest()
    {
        SharePhotosToEventRequest sharephotostoeventrequest = new SharePhotosToEventRequest();
        sharephotostoeventrequest.eventId = mEventId;
        sharephotostoeventrequest.photoId = mPhotoIds;
        return sharephotostoeventrequest;
    }
}
