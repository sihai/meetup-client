/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.server.client.domain.GetMobileExperimentsRequestRequestedFlag;
import com.galaxy.meetup.server.client.domain.request.GetMobileExperimentsRequest;
import com.galaxy.meetup.server.client.domain.response.GetMobileExperimentsResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetMobileExperimentsOperation extends PlusiOperation {

	public GetMobileExperimentsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getmobileexperiments", null, null, GetMobileExperimentsResponse.class);
    }

    protected final void handleResponse(Response response) throws IOException
    {
        GetMobileExperimentsResponse getmobileexperimentsresponse = (GetMobileExperimentsResponse)response;
        EsAccountsData.insertExperiments(mContext, mAccount, getmobileexperimentsresponse.experiment);
    }

    protected final Request populateRequest()
    {
        GetMobileExperimentsRequest getmobileexperimentsrequest = new GetMobileExperimentsRequest();
        getmobileexperimentsrequest.requestedflag = new ArrayList();
        List arraylist = Property.getExperimentIds();
        int i = arraylist.size();
        for(int j = 0; j < i; j++)
        {
            GetMobileExperimentsRequestRequestedFlag getmobileexperimentsrequestrequestedflag = new GetMobileExperimentsRequestRequestedFlag();
            getmobileexperimentsrequestrequestedflag.flagId = (String)arraylist.get(j);
            getmobileexperimentsrequest.requestedflag.add(getmobileexperimentsrequestrequestedflag);
        }
        return getmobileexperimentsrequest;
    }
}
