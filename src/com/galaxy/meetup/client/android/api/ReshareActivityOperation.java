/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.domain.request.PostActivityRequest;
import com.galaxy.meetup.server.client.domain.response.PostActivityResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class ReshareActivityOperation extends PlusiOperation {
	
	private final AudienceData mAudience;
    private final String mContent;
    private final String mReshareId;
    
	public ReshareActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, AudienceData audiencedata)
    {
        super(context, esaccount, "postactivity", intent, operationlistener, PostActivityResponse.class);
        mReshareId = s;
        mContent = s1;
        mAudience = audiencedata;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        PostActivityResponse postactivityresponse = (PostActivityResponse)response;
        if(postactivityresponse == null) { 
        	return;
        }
        
        List list = postactivityresponse.stream.update;
        if(list != null && list.size() > 0) {
        	for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                Update update = (Update)iterator.next();
                if(!mReshareId.equals(update.sharedFromItemId))
                    continue; /* Loop/switch isn't completed */
            }

            String s = EsPostsData.buildActivitiesStreamKey(null, null, null, false, 0);
            EsPostsData.insertActivitiesAndOverwrite(mContext, mAccount, s, list, "DEFAULT");
        }
    }

    protected final Request populateRequest()
    {
        PostActivityRequest postactivityrequest = new PostActivityRequest();
        postactivityrequest.resharedUpdateId = mReshareId;
        postactivityrequest.updateText = mContent;
        postactivityrequest.sharingRoster = EsPeopleData.convertAudienceToSharingRoster(mAudience);
        return postactivityrequest;
    }

}
