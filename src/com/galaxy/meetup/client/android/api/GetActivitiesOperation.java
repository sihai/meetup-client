/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.ClientEmbedOptions;
import com.galaxy.meetup.server.client.domain.FieldRequestOptions;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.StreamParams;
import com.galaxy.meetup.server.client.domain.UpdateFilter;
import com.galaxy.meetup.server.client.domain.UpdateMixinFilter;
import com.galaxy.meetup.server.client.domain.request.GetActivitiesRequest;
import com.galaxy.meetup.server.client.domain.response.GetActivitiesResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetActivitiesOperation extends PlusiOperation {

	private final String mCircleId;
    private final String mContinuationToken;
    private final boolean mFromWidget;
    private final String mGaiaId;
    private final int mMaxCount;
    private long mRequestTime;
    private final String mSquareStreamId;
    private final EsSyncAdapterService.SyncState mSyncState;
    private final int mView;
    
    public GetActivitiesOperation(Context context, EsAccount esaccount, int i, String s, String s1, String s2, boolean flag, 
            String s3, int j, EsSyncAdapterService.SyncState syncstate, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getactivities", null, operationlistener, GetActivitiesResponse.class);
        mView = i;
        if(!TextUtils.isEmpty(s) && s.startsWith("f."))
            s = s.substring(2);
        mCircleId = s;
        mGaiaId = s1;
        mSquareStreamId = s2;
        mFromWidget = flag;
        mContinuationToken = s3;
        if(j <= 0)
            j = 10;
        mMaxCount = j;
        mSyncState = syncstate;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetActivitiesResponse getactivitiesresponse = (GetActivitiesResponse)genericjson;
        String s;
        if(mView == 4)
            s = EsPostsData.buildSquareStreamKey(mGaiaId, mSquareStreamId, false);
        else
            s = EsPostsData.buildActivitiesStreamKey(mGaiaId, mCircleId, null, mFromWidget, mView);
        if(EsLog.isLoggable("GetActivitiesOp", 4))
        {
            boolean flag = TextUtils.equals(mContinuationToken, getactivitiesresponse.stream.continuationToken);
            StringBuilder stringbuilder = new StringBuilder();
            String s1;
            StringBuilder stringbuilder1;
            Object obj;
            StringBuilder stringbuilder2;
            String s2;
            if(flag)
                s1 = "!!!!!";
            else
                s1 = "";
            stringbuilder1 = stringbuilder.append(s1).append("Sent token ").append(mContinuationToken).append(" at time ").append(mRequestTime).append(" and received token ").append(getactivitiesresponse.stream.continuationToken).append(" with ");
            if(getactivitiesresponse.stream.update == null)
                obj = "0";
            else
                obj = Integer.valueOf(getactivitiesresponse.stream.update.size());
            stringbuilder2 = stringbuilder1.append(obj).append(" activities for ");
            if(mAccount == null)
                s2 = "?";
            else
                s2 = mAccount.getName();
            Log.i("GetActivitiesOp", stringbuilder2.append(s2).toString());
        }
        EsPostsData.updateStreamActivities(mContext, mAccount, s, getactivitiesresponse.stream.update, "MOBILE", mContinuationToken, getactivitiesresponse.stream.continuationToken, mSyncState);
    }
    
    protected final GenericJson populateRequest() {
        boolean flag = true;
        GetActivitiesRequest getactivitiesrequest = new GetActivitiesRequest();
        getactivitiesrequest.streamParams = new StreamParams();
        StreamParams streamparams = getactivitiesrequest.streamParams;
        String s = "ALL";
        switch(mView) {
	        case 0:
	        	s = "CIRCLES";
	        	break;
	        case 1:
	        	s = "WHATS_HOT";
	        	break;
	        case 2:
	        	s = "ALL";
	        	break;
	        case 3:
	        	s = "ALL";
	        	break;
	        case 4:
	        	break;
        	default:
        		s = "ALL";
        		break;
        }
        
        streamparams.viewType = s;
        boolean flag1;
        boolean flag2;
        if(mView != 3 && mView != 0 || mCircleId != null || mGaiaId != null)
            getactivitiesrequest.streamParams.sort = "LATEST";
        else
            getactivitiesrequest.streamParams.sort = "BEST";
        getactivitiesrequest.streamParams.focusGroupId = mCircleId;
        getactivitiesrequest.streamParams.productionStreamOid = mGaiaId;
        getactivitiesrequest.streamParams.squareStreamId = mSquareStreamId;
        getactivitiesrequest.continuesToken = mContinuationToken;
        getactivitiesrequest.streamParams.maxNumUpdates = Integer.valueOf(mMaxCount);
        getactivitiesrequest.streamParams.collapserType = "MOBILE";
        getactivitiesrequest.streamParams.maxComments = Integer.valueOf(0);
        getactivitiesrequest.streamParams.maxNumImages = Integer.valueOf(4);
        getactivitiesrequest.streamParams.fieldRequestOptions = new FieldRequestOptions();
        getactivitiesrequest.streamParams.fieldRequestOptions.includeLegacyMediaData = Boolean.FALSE;
        getactivitiesrequest.streamParams.fieldRequestOptions.includeEmbedsData = Boolean.TRUE;
        getactivitiesrequest.streamParams.updateFilter = new UpdateFilter();
        getactivitiesrequest.streamParams.updateFilter.includeNamespace = EsPostsData.getStreamNamespaces(mFromWidget);
        if(mView == 0 && mCircleId == null && mGaiaId == null)
            flag1 = flag;
        else
            flag1 = false;
        if(!flag1)
            flag2 = flag;
        else
            flag2 = false;
        getactivitiesrequest.skipPopularMixin = Boolean.valueOf(flag2);
        getactivitiesrequest.streamParams.updateMixinFilter = new UpdateMixinFilter();
        getactivitiesrequest.streamParams.updateMixinFilter.mixinType = EsPostsData.getMixinsWhitelist(flag1);
        getactivitiesrequest.embedOptions = new ClientEmbedOptions();
        getactivitiesrequest.embedOptions.includeType = EsPostsData.getEmbedsWhitelist();
        if(mFromWidget)
            flag = false;
        getactivitiesrequest.isUserInitiated = Boolean.valueOf(flag);
        mRequestTime = System.currentTimeMillis();
        return getactivitiesrequest;
    }

}
