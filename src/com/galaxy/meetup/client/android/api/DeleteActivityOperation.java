/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.DeleteActivityRequest;
import com.galaxy.meetup.server.client.domain.response.DeleteActivityResponse;

/**
 * 
 * @author sihai
 *
 */
public class DeleteActivityOperation extends PlusiOperation {

	private final String mActivityId;
	
	public DeleteActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s)
    {
        super(context, esaccount, "deleteactivity", intent, operationlistener, DeleteActivityResponse.class);
        mActivityId = s;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        EsPostsData.deleteActivity(mContext, mAccount, mActivityId);
    }

    @Override
    protected final GenericJson populateRequest()
    {
    	DeleteActivityRequest genericjson = new DeleteActivityRequest();
    	genericjson.activityId = mActivityId;
    	return genericjson;
    }

}
