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
import com.galaxy.meetup.server.client.domain.DataPersonList;
import com.galaxy.meetup.server.client.domain.DataSyncStateToken;
import com.galaxy.meetup.server.client.domain.DataSystemGroups;
import com.galaxy.meetup.server.client.domain.DataViewerCircles;
import com.galaxy.meetup.server.client.domain.LoadSocialNetworkRequestPersonListOptions;
import com.galaxy.meetup.server.client.domain.LoadSocialNetworkRequestSystemGroupsOptions;
import com.galaxy.meetup.server.client.domain.LoadSocialNetworkRequestViewerCirclesOptions;
import com.galaxy.meetup.server.client.domain.request.LoadSocialNetworkRequest;
import com.galaxy.meetup.server.client.domain.response.LoadSocialNetworkResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class LoadSocialNetworkOperation extends PlusiOperation {

	private DataViewerCircles mCircleList;
    private final boolean mLoadCircles;
    private final boolean mLoadPeople;
    private final int mMaxPeople;
    private DataPersonList mPersonList;
    private final String mSyncStateToken;
    private DataSystemGroups mSystemGroups;
    
    public LoadSocialNetworkOperation(Context context, EsAccount esaccount, boolean flag, boolean flag1, int i, String s, Intent intent, 
            HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "loadsocialnetwork", null, operationlistener, LoadSocialNetworkResponse.class);
        mSyncStateToken = s;
        mLoadCircles = flag;
        mLoadPeople = flag1;
        mMaxPeople = i;
    }

    public final DataViewerCircles getCircleList()
    {
        return mCircleList;
    }

    public final DataPersonList getPersonList()
    {
        return mPersonList;
    }

    public final DataSystemGroups getSystemGroups()
    {
        return mSystemGroups;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        LoadSocialNetworkResponse loadsocialnetworkresponse = (LoadSocialNetworkResponse)response;
        mCircleList = loadsocialnetworkresponse.viewerCircles;
        mSystemGroups = loadsocialnetworkresponse.systemGroups;
        mPersonList = loadsocialnetworkresponse.personList;
    }

    protected final Request populateRequest() {
        LoadSocialNetworkRequest loadsocialnetworkrequest = new LoadSocialNetworkRequest();
        if(mLoadCircles)
        {
            loadsocialnetworkrequest.circlesOptions = new LoadSocialNetworkRequestViewerCirclesOptions();
            loadsocialnetworkrequest.circlesOptions.includeCircles = Boolean.valueOf(true);
            loadsocialnetworkrequest.circlesOptions.includeMemberCounts = Boolean.valueOf(true);
            loadsocialnetworkrequest.systemGroupsOptions = new LoadSocialNetworkRequestSystemGroupsOptions();
            loadsocialnetworkrequest.systemGroupsOptions.includeSystemGroups = Boolean.valueOf(true);
            loadsocialnetworkrequest.systemGroupsOptions.includeMemberCounts = Boolean.valueOf(true);
        }
        if(mLoadPeople) {
	        loadsocialnetworkrequest.personListOptions = new LoadSocialNetworkRequestPersonListOptions();
	        loadsocialnetworkrequest.personListOptions.includePeople = Boolean.valueOf(true);
	        loadsocialnetworkrequest.personListOptions.maxPeople = Integer.valueOf(mMaxPeople);
	        loadsocialnetworkrequest.personListOptions.includeExtendedProfileInfo = Boolean.valueOf(true);
	        if(mSyncStateToken != null) {
	        	DataSyncStateToken datasyncstatetoken = (DataSyncStateToken)JsonUtil.fromByteArray(mSyncStateToken.getBytes(), DataSyncStateToken.class);
	        	loadsocialnetworkrequest.personListOptions.syncStateToken = datasyncstatetoken;
	        }
        }
        return loadsocialnetworkrequest;
    }

}
