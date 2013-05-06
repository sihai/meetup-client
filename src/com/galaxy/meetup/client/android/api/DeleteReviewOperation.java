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
import com.galaxy.meetup.server.client.domain.request.DeletePlaceReviewRequest;
import com.galaxy.meetup.server.client.domain.response.DeletePlaceReviewResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class DeleteReviewOperation extends PlusiOperation {

	private String cid;
	
	public DeleteReviewOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s)
    {
        super(context, esaccount, "deleteplacereview", intent, operationlistener, DeletePlaceReviewResponse.class);
        cid = s;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
    }

    protected final Request populateRequest()
    {
    	DeletePlaceReviewRequest response = new DeletePlaceReviewRequest();
        response.cid = cid;
        return response;
    }

}
