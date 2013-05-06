/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.SearchUtils;
import com.galaxy.meetup.server.client.domain.ActivityFilters;
import com.galaxy.meetup.server.client.domain.ActivityRequestData;
import com.galaxy.meetup.server.client.domain.ClientEmbedOptions;
import com.galaxy.meetup.server.client.domain.FieldRequestOptions;
import com.galaxy.meetup.server.client.domain.SearchQuery;
import com.galaxy.meetup.server.client.domain.UpdateFilter;
import com.galaxy.meetup.server.client.domain.request.SearchQueryRequest;
import com.galaxy.meetup.server.client.domain.response.SearchQueryResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SearchActivitiesOperation extends PlusiOperation {

	private final String mContinuationToken;
    private final String mQuery;
    
    public SearchActivitiesOperation(Context context, EsAccount esaccount, String s, String s1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "searchquery", intent, operationlistener, SearchQueryResponse.class);
        mQuery = s;
        mContinuationToken = s1;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        SearchQueryResponse searchqueryresponse = (SearchQueryResponse)response;
        if(searchqueryresponse.results != null && searchqueryresponse.results.activityResults != null)
        {
            EsPostsData.updateStreamActivities(mContext, mAccount, SearchUtils.getSearchKey(mQuery), searchqueryresponse.results.activityResults.stream.update, "DEFAULT", mContinuationToken, searchqueryresponse.results.activityResults.shownActivitiesBlob, null);
            SearchUtils.insertSearchResults(mContext, mAccount, mQuery, searchqueryresponse.results.activityResults.shownActivitiesBlob);
        } else
        {
            SearchUtils.insertSearchResults(mContext, mAccount, null, null);
        }
    }

    protected final Request populateRequest()
    {
        SearchQueryRequest searchqueryrequest = new SearchQueryRequest();
        searchqueryrequest.searchQuery = new SearchQuery();
        searchqueryrequest.searchQuery.queryText = mQuery;
        searchqueryrequest.searchQuery.sort = "RECENT";
        searchqueryrequest.searchQuery.filter = "TACOS";
        searchqueryrequest.activityRequestData = new ActivityRequestData();
        if(!TextUtils.isEmpty(mContinuationToken))
            searchqueryrequest.activityRequestData.shownActivitiesBlob = mContinuationToken;
        searchqueryrequest.activityRequestData.activityFilters = new ActivityFilters();
        searchqueryrequest.activityRequestData.activityFilters.fieldRequestOptions = new FieldRequestOptions();
        searchqueryrequest.activityRequestData.activityFilters.fieldRequestOptions.includeLegacyMediaData = Boolean.FALSE;
        searchqueryrequest.activityRequestData.activityFilters.fieldRequestOptions.includeEmbedsData = Boolean.TRUE;
        searchqueryrequest.activityRequestData.activityFilters.updateFilter = new UpdateFilter();
        searchqueryrequest.activityRequestData.activityFilters.updateFilter.includeNamespace = EsPostsData.getStreamNamespaces(false);
        searchqueryrequest.embedOptions = new ClientEmbedOptions();
        searchqueryrequest.embedOptions.includeType = EsPostsData.getEmbedsWhitelist();
        return searchqueryrequest;
    }
}
