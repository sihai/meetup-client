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
import com.galaxy.meetup.server.client.domain.SearchQuery;
import com.galaxy.meetup.server.client.domain.request.SearchQueryRequest;
import com.galaxy.meetup.server.client.domain.response.SearchQueryResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SquareSearchQueryOperation extends PlusiOperation {

	private final String mQuery;
    private List mSquareResults;
    
    public SquareSearchQueryOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "searchquery", null, null, SearchQueryResponse.class);
        mQuery = s;
    }

    public final String getContinuationToken()
    {
        return null;
    }

    public final List getSquareSearchResults()
    {
        return mSquareResults;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        SearchQueryResponse searchqueryresponse = (SearchQueryResponse)response;
        if(searchqueryresponse.results != null && searchqueryresponse.results.squareResults != null)
            mSquareResults = searchqueryresponse.results.squareResults.result;
    }

    protected final Request populateRequest()
    {
        SearchQueryRequest searchqueryrequest = new SearchQueryRequest();
        searchqueryrequest.searchQuery = new SearchQuery();
        searchqueryrequest.searchQuery.queryText = mQuery;
        searchqueryrequest.searchQuery.filter = "SQUARES";
        return searchqueryrequest;
    }
}
