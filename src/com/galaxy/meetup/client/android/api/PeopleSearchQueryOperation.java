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
import com.galaxy.meetup.server.client.domain.PeopleRequestData;
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
public class PeopleSearchQueryOperation extends PlusiOperation {

	private final String mContinuationToken;
    private boolean mInludePlusPages;
    private String mNewContinuationToken;
    private List mPeopleResults;
    private final String mQuery;
    
	public PeopleSearchQueryOperation(Context context, EsAccount esaccount, String s, String s1, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "searchquery", null, null, SearchQueryResponse.class);
        mQuery = s;
        mContinuationToken = s1;
        mInludePlusPages = flag;
    }

    public final String getContinuationToken()
    {
        return mNewContinuationToken;
    }

    public final List getPeopleSearchResults()
    {
        return mPeopleResults;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        SearchQueryResponse searchqueryresponse = (SearchQueryResponse)response;
        if(searchqueryresponse.results != null && searchqueryresponse.results.peopleResults != null)
        {
            mNewContinuationToken = searchqueryresponse.results.peopleResults.shownPeopleBlob;
            mPeopleResults = searchqueryresponse.results.peopleResults.result;
        }
    }

    protected final Request populateRequest()
    {
        SearchQueryRequest searchqueryrequest = new SearchQueryRequest();
        searchqueryrequest.searchQuery = new SearchQuery();
        searchqueryrequest.searchQuery.queryText = mQuery;
        SearchQuery searchquery = searchqueryrequest.searchQuery;
        String s;
        if(mInludePlusPages)
            s = "PEOPLE";
        else
            s = "PEOPLE_ONLY";
        searchquery.filter = s;
        if(mContinuationToken != null)
        {
            searchqueryrequest.peopleRequestData = new PeopleRequestData();
            searchqueryrequest.peopleRequestData.shownPeopleBlob = mContinuationToken;
        }
        return searchqueryrequest;
    }

}
