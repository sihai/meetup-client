/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.ClientEmbedOptions;
import com.galaxy.meetup.server.client.domain.RenderContext;
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.domain.request.GetActivityRequest;
import com.galaxy.meetup.server.client.domain.response.GetActivityResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetActivityOperation extends PlusiOperation {

	private final String mActivityId;
    private final String mOwnerGaiaId;
    private String mResponseUpdateId;
    private final String mSquareId;
    
	public GetActivityOperation(Context context, EsAccount esaccount, String s, String s1, String s2, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getactivity", intent, operationlistener, GetActivityResponse.class);
        mActivityId = s;
        mOwnerGaiaId = s1;
        mSquareId = s2;
    }

    public final String getResponseUpdateId()
    {
        return mResponseUpdateId;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        GetActivityResponse getactivityresponse = (GetActivityResponse)response;
        ArrayList arraylist = new ArrayList(1);
        arraylist.add(getactivityresponse.update);
        EsPostsData.insertActivitiesAndOverwrite(mContext, mAccount, null, arraylist, "DEFAULT");
        Iterator iterator = arraylist.iterator();
        if(iterator.hasNext())
            mResponseUpdateId = ((Update)iterator.next()).updateId;
    }

    protected final Request populateRequest()
    {
        GetActivityRequest getactivityrequest = new GetActivityRequest();
        getactivityrequest.activityId = mActivityId;
        if(!TextUtils.isEmpty(mOwnerGaiaId))
            getactivityrequest.ownerId = mOwnerGaiaId;
        getactivityrequest.fetchReadState = Boolean.valueOf(true);
        getactivityrequest.embedOptions = new ClientEmbedOptions();
        getactivityrequest.embedOptions.includeType = EsPostsData.getEmbedsWhitelist();
        if(!TextUtils.isEmpty(mSquareId))
        {
            getactivityrequest.renderContext = new RenderContext();
            getactivityrequest.renderContext.location = "MOBILE_SQUARE_STREAM";
            getactivityrequest.renderContext.streamId = new ArrayList();
            getactivityrequest.renderContext.streamId.add(mSquareId);
        }
        return getactivityrequest;
    }

}
