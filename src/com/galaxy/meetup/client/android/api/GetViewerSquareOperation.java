/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsSquaresData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.GetViewerSquareOzRequest;
import com.galaxy.meetup.server.client.domain.response.GetViewerSquareOzResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetViewerSquareOperation extends PlusiOperation {

	private final String mSquareId;
	
	public GetViewerSquareOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getviewersquare", intent, operationlistener, GetViewerSquareOzResponse.class);
        mSquareId = s;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        GetViewerSquareOzResponse getviewersquareozresponse = (GetViewerSquareOzResponse)response;
        EsSquaresData.insertSquare(mContext, mAccount, getviewersquareozresponse.viewerSquare);
    }

    protected final Request populateRequest()
    {
    	GetViewerSquareOzRequest response = new GetViewerSquareOzRequest();
        response.obfuscatedSquareId = mSquareId;
        return response;
    }

}
