/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.EmbedClientItem;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.RequestsPostActivityRequestAttribution;
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.domain.request.PostActivityRequest;
import com.galaxy.meetup.server.client.domain.response.GetActivitiesResponse;

/**
 * 
 * @author sihai
 *
 */
public class CreateEventOperation extends PlusiOperation {

	private static final List EVENT_EMBED_TYPES = Collections.unmodifiableList(Arrays.asList(new String[] {
        "PLUS_EVENT", "EVENT", "THING"
    }));
    private final AudienceData mAudience;
    private final String mExternalId;
    private final PlusEvent mPlusEvent;
	    
	public CreateEventOperation(Context context, EsAccount esaccount, PlusEvent plusevent, AudienceData audiencedata, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "postactivity", intent, operationlistener, GetActivitiesResponse.class);
        mPlusEvent = plusevent;
        mAudience = audiencedata;
        mExternalId = s;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetActivitiesResponse getactivitiesresponse = (GetActivitiesResponse)genericjson;
        if(getactivitiesresponse == null || getactivitiesresponse.stream == null || getactivitiesresponse.stream.update == null) 
        	return;
        
        int i;
        int j;
        i = getactivitiesresponse.stream.update.size();
        for(j = 0; j < i; j++) {
        	Update update = (Update)getactivitiesresponse.stream.update.get(j);
            if(null != update && null != update.embed && null != update.embed.plusEvent) {
        		EsEventData.insertEvent(mContext, mAccount, update.updateId, update.embed.plusEvent, update);
        		return;
            }
        }
    }

    @Override
    protected final GenericJson populateRequest()
    {
        PostActivityRequest postactivityrequest = new PostActivityRequest();
        postactivityrequest.attribution = new RequestsPostActivityRequestAttribution();
        postactivityrequest.attribution.androidAppName = "Mobile";
        postactivityrequest.externalId = mExternalId;
        postactivityrequest.sharingRoster = EsPeopleData.convertAudienceToSharingRoster(mAudience);
        postactivityrequest.updateText = mPlusEvent.getDescription();
        postactivityrequest.embed = new EmbedClientItem();
        postactivityrequest.embed.type = EVENT_EMBED_TYPES;
        postactivityrequest.embed.plusEvent = mPlusEvent;
        return postactivityrequest;
    }

}
