/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.DeleteCircleRequest;
import com.galaxy.meetup.server.client.domain.response.DeleteCircleResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class DeleteCirclesOperation extends PlusiOperation {

	private final ArrayList mCircleIds;
	
	public DeleteCirclesOperation(Context context, EsAccount esaccount, ArrayList arraylist, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "deletecircle", null, null, DeleteCircleResponse.class);
        mCircleIds = arraylist;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        EsPeopleData.removeDeletedCircles(mContext, getAccount(), mCircleIds);
    }

    @Override
    protected final Request populateRequest()
    {
        DeleteCircleRequest deletecirclerequest = new DeleteCircleRequest();
        deletecirclerequest.circleId = new ArrayList();
        String s;
        for(Iterator iterator = mCircleIds.iterator(); iterator.hasNext(); deletecirclerequest.circleId.add(EsPeopleData.buildCircleId(s)))
            s = (String)iterator.next();
        return deletecirclerequest;
    }

}
