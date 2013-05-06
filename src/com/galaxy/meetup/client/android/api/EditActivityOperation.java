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
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.domain.request.EditActivityRequest;
import com.galaxy.meetup.server.client.domain.response.EditActivityResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class EditActivityOperation extends PlusiOperation {

	private final String mActivityId;
    private final String mContent;
    private final boolean mReshare;
    
	public EditActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, boolean flag)
    {
        super(context, esaccount, "editactivity", intent, operationlistener, EditActivityResponse.class);
        mActivityId = s;
        mContent = s1;
        mReshare = flag;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        EditActivityResponse editactivityresponse = (EditActivityResponse)response;
        if(editactivityresponse != null)
        {
            Update update = editactivityresponse.update;
            if(update != null && mActivityId.equals(update.updateId))
                (new GetActivityOperation(mContext, mAccount, mActivityId, null, null, null, null)).start();
        }
    }

    protected final Request populateRequest()
    {
        EditActivityRequest editactivityrequest = new EditActivityRequest();
        editactivityrequest.externalId = mActivityId;
        editactivityrequest.updateText = mContent;
        editactivityrequest.preserveExistingAttachment = Boolean.valueOf(true);
        editactivityrequest.isReshare = Boolean.valueOf(mReshare);
        return editactivityrequest;
    }

}
