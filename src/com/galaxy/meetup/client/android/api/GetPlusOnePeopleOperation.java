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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.GetPlusonePeopleRequest;
import com.galaxy.meetup.server.client.domain.response.GetPlusonePeopleResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetPlusOnePeopleOperation extends PlusiOperation {

	private final int mNumPeopleToReturn = 50;
    private List mPeople;
    private final String mPlusOneId;
    
    public GetPlusOnePeopleOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, int i)
    {
        super(context, esaccount, "getplusonepeople", null, null, GetPlusonePeopleResponse.class);
        mPlusOneId = s;
    }

    public final List getPeople()
    {
        return mPeople;
    }

    protected final void handleResponse(GenericJson genericjson)
        throws IOException
    {
        mPeople = ((GetPlusonePeopleResponse)genericjson).person;
    }

    protected final GenericJson populateRequest()
    {
        GetPlusonePeopleRequest getplusonepeoplerequest = new GetPlusonePeopleRequest();
        getplusonepeoplerequest.plusoneId = mPlusOneId;
        getplusonepeoplerequest.numPeopleToReturn = Integer.valueOf(mNumPeopleToReturn);
        return getplusonepeoplerequest;
    }
}
