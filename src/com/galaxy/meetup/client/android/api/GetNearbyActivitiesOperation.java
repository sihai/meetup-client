/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.server.client.domain.ActivityFilters;
import com.galaxy.meetup.server.client.domain.ClientEmbedOptions;
import com.galaxy.meetup.server.client.domain.FieldRequestOptions;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.NearbyStreamRequestLatLongE7;
import com.galaxy.meetup.server.client.domain.UpdateFilter;
import com.galaxy.meetup.server.client.domain.request.NearbyStreamRequest;
import com.galaxy.meetup.server.client.domain.response.NearbyStreamResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetNearbyActivitiesOperation extends PlusiOperation {

	private final String mContinuationToken;
    private final DbLocation mLocation;
    private final int mMaxCount;
    private final EsSyncAdapterService.SyncState mSyncState;
    
	public GetNearbyActivitiesOperation(Context context, EsAccount esaccount, DbLocation dblocation, String s, int i, EsSyncAdapterService.SyncState syncstate, Intent intent, 
            HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "nearbystream", null, operationlistener, NearbyStreamResponse.class);
        mLocation = dblocation;
        mContinuationToken = s;
        if(i <= 0)
            i = 10;
        mMaxCount = i;
        mSyncState = syncstate;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        NearbyStreamResponse nearbystreamresponse = (NearbyStreamResponse)genericjson;
        String s = EsPostsData.buildActivitiesStreamKey(null, null, mLocation, false, 2);
        EsPostsData.updateStreamActivities(mContext, mAccount, s, nearbystreamresponse.stream.update, "DEFAULT", mContinuationToken, nearbystreamresponse.stream.continuationToken, mSyncState);
    }

    protected final GenericJson populateRequest()
    {
        NearbyStreamRequest nearbystreamrequest = new NearbyStreamRequest();
        nearbystreamrequest.latLongE7 = new NearbyStreamRequestLatLongE7();
        nearbystreamrequest.latLongE7.latitude = Integer.valueOf(mLocation.getLatitudeE7());
        nearbystreamrequest.latLongE7.longitude = Integer.valueOf(mLocation.getLongitudeE7());
        nearbystreamrequest.continuationToken = mContinuationToken;
        nearbystreamrequest.maxResults = Integer.valueOf(mMaxCount);
        nearbystreamrequest.activityFilters = new ActivityFilters();
        nearbystreamrequest.activityFilters.fieldRequestOptions = new FieldRequestOptions();
        nearbystreamrequest.activityFilters.fieldRequestOptions.includeLegacyMediaData = Boolean.FALSE;
        nearbystreamrequest.activityFilters.fieldRequestOptions.includeEmbedsData = Boolean.TRUE;
        nearbystreamrequest.activityFilters.updateFilter = new UpdateFilter();
        nearbystreamrequest.activityFilters.updateFilter.includeNamespace = EsPostsData.getStreamNamespaces(false);
        nearbystreamrequest.embedOptions = new ClientEmbedOptions();
        nearbystreamrequest.embedOptions.includeType = EsPostsData.getEmbedsWhitelist();
        return nearbystreamrequest;
    }

}
