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
import com.galaxy.meetup.client.android.content.EsAnalyticsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.ClientOzExtension;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.PostClientLogsRequest;
import com.galaxy.meetup.server.client.domain.response.PostClientLogsResponse;

/**
 * 
 * @author sihai
 *
 */
public class PostClientLogsOperation extends PlusiOperation {

	private ClientOzExtension mClientOzExtension;
	
	public PostClientLogsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "postclientlogs", intent, operationlistener, PostClientLogsResponse.class);
        mClientOzExtension = EsAnalyticsData.createClientOzExtension(context);
    }

    public final List getClientOzEvents()
    {
        List list;
        if(mClientOzExtension == null)
            list = null;
        else
            list = mClientOzExtension.clientEvent;
        return list;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        onStartResultProcessing();
    }

    protected final GenericJson populateRequest()
    {
        PostClientLogsRequest postclientlogsrequest = new PostClientLogsRequest();
        postclientlogsrequest.enableTracing = Boolean.valueOf(true);
        postclientlogsrequest.clientLog = mClientOzExtension;
        return postclientlogsrequest;
    }

    public final void setClientOzEvents(List list)
    {
        mClientOzExtension.clientEvent = list;
    }
}
