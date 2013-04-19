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
public class CheckStreamChangeOperation extends PlusiOperation {

	private final String mCircleId;
    private final boolean mFromWidget;
    private final String mGaiaId;
    private final String mSquareStreamId;
    private boolean mStreamHasChanged;
    private final int mView;
    
	public CheckStreamChangeOperation(Context context, EsAccount esaccount, int i, String s, String s1, String s2, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, esaccount, "getactivities", null, null, GetActivitiesResponse.class);
        mView = i;
        if(!TextUtils.isEmpty(s) && s.startsWith("f."))
            s = s.substring(2);
        mCircleId = s;
        mGaiaId = s1;
        mSquareStreamId = s2;
        mFromWidget = flag;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetActivitiesResponse getactivitiesresponse = (GetActivitiesResponse)genericjson;
        String s;
        if(mView == 4)
            s = EsPostsData.buildSquareStreamKey(mGaiaId, mSquareStreamId, false);
        else
            s = EsPostsData.buildActivitiesStreamKey(mGaiaId, mCircleId, null, false, mView);
        mStreamHasChanged = EsPostsData.hasStreamChanged(mContext, mAccount, s, getactivitiesresponse.stream.update);
    }

    public final boolean hasStreamChanged()
    {
        return mStreamHasChanged;
    }

    @Override
    protected final GenericJson populateRequest() {
        GetActivitiesRequest getactivitiesrequest = new GetActivitiesRequest();
        getactivitiesrequest.streamParams = new StreamParams();
        String s = "ALL";
        switch(mView) {
	        case 0:
	        	s = "CIRCLES";
	        	break;
	        case 1:
	        	s = "WHATS_HOT";
	        	break;
	        case 2:
	        case 3:
	        	s = "ALL";
	        	break;
	        case 4:
	        	s = "SQUARES";
	        	break;
        	default:
        		s = "ALL";
        		break;
        }
        
        getactivitiesrequest.streamParams.viewType = s;
        boolean flag;
        boolean flag1;
        if(mView != 3 && mView != 0 || mCircleId != null || mGaiaId != null)
            getactivitiesrequest.streamParams.sort = "LATEST";
        else
            getactivitiesrequest.streamParams.sort = "BEST";
        getactivitiesrequest.streamParams.focusGroupId = mCircleId;
        getactivitiesrequest.streamParams.productionStreamOid = mGaiaId;
        getactivitiesrequest.streamParams.squareStreamId = mSquareStreamId;
        getactivitiesrequest.continuesToken = null;
        getactivitiesrequest.streamParams.maxNumUpdates = Integer.valueOf(1);
        getactivitiesrequest.streamParams.collapserType = "MOBILE";
        getactivitiesrequest.streamParams.maxComments = Integer.valueOf(0);
        getactivitiesrequest.streamParams.maxNumImages = Integer.valueOf(0);
        getactivitiesrequest.streamParams.fieldRequestOptions = new FieldRequestOptions();
        getactivitiesrequest.streamParams.fieldRequestOptions.includeLegacyMediaData = Boolean.FALSE;
        getactivitiesrequest.streamParams.fieldRequestOptions.includeEmbedsData = Boolean.TRUE;
        getactivitiesrequest.streamParams.updateFilter = new UpdateFilter();
        getactivitiesrequest.streamParams.updateFilter.includeNamespace = EsPostsData.getStreamNamespaces(mFromWidget);
        if(mView == 0 && mCircleId == null && mGaiaId == null)
            flag = true;
        else
            flag = false;
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        getactivitiesrequest.skipPopularMixin = Boolean.valueOf(flag1);
        getactivitiesrequest.streamParams.updateMixinFilter = new UpdateMixinFilter();
        getactivitiesrequest.streamParams.updateMixinFilter.mixinType = EsPostsData.getMixinsWhitelist(flag);
        getactivitiesrequest.embedOptions = new ClientEmbedOptions();
        getactivitiesrequest.embedOptions.includeType = EsPostsData.getEmbedsWhitelist();
        getactivitiesrequest.isUserInitiated = Boolean.FALSE;
        return getactivitiesrequest;
    }

}
