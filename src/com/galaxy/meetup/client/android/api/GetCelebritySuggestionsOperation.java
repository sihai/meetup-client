/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.GetCelebritySuggestionsRequest;
import com.galaxy.meetup.server.client.domain.response.GetCelebritySuggestionsResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetCelebritySuggestionsOperation extends PlusiOperation {

	public GetCelebritySuggestionsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getcelebritysuggestions", null, null, GetCelebritySuggestionsResponse.class);
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetCelebritySuggestionsResponse getcelebritysuggestionsresponse = (GetCelebritySuggestionsResponse)genericjson;
        EsPeopleData.insertCelebritySuggestions(mContext, getAccount(), getcelebritysuggestionsresponse.category);
    }

    protected final GenericJson populateRequest()
    {
    	GetCelebritySuggestionsRequest genericjson = new GetCelebritySuggestionsRequest();
        genericjson.maxPerCategory = Integer.valueOf(10);
        return genericjson;
    }

}
