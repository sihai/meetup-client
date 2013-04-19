/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.EventSelector;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.ReadOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsCommentsOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsFramesOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsPhotosOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsUpdateOptions;
import com.galaxy.meetup.server.client.domain.request.EventReadRequest;
import com.galaxy.meetup.server.client.domain.response.EventLeafResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetEventOperation extends PlusiOperation {

	private final String mAuthKey;
    private final String mEventId;
    
    public GetEventOperation(Context context, EsAccount esaccount, String s, String s1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "eventread", null, null, EventLeafResponse.class);
        if(TextUtils.isEmpty(s))
        {
            throw new IllegalArgumentException("Event ID must not be empty");
        } else
        {
            mEventId = s;
            mAuthKey = s1;
            return;
        }
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException {
        Cursor cursor = null;
        EventLeafResponse eventleafresponse = (EventLeafResponse)genericjson;
        String s = eventleafresponse.activityId;
        PlusEvent plusevent = eventleafresponse.plusEvent;
        long l = 0L;
        String s1 = null;
        String s2 = null;
        try {
        	cursor = EsEventData.getEvent(mContext, mAccount, plusevent.getId(), EventQuery.PROJECTION);
        	if(cursor.moveToFirst()) {
        		s1 = cursor.getString(0);
                s2 = cursor.getString(1);
                l = cursor.getLong(2);
        	}
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        EsEventData.updateEventActivities(mContext, mAccount, s, plusevent, eventleafresponse.update, s1, s2, null, true, l, null);
        
    }

    public final void onHttpOperationComplete(int i, String s, Exception exception)
    {
        if(i == 404){
        	EsEventData.deleteEvent(mContext, mAccount, mEventId);
        	return;
        }
        
        if(i >= 400 && TextUtils.equals(mEventId, InstantUpload.getInstantShareEventId(mContext)))
        {
            if(EsLog.isLoggable("HttpTransaction", 4))
                Log.i("HttpTransaction", (new StringBuilder("[GET_EVENT] received error: ")).append(i).append("; disable IS").toString());
            EsEventData.disableInstantShare(mContext);
        }
    }

    protected final GenericJson populateRequest()
    {
        EventReadRequest eventreadrequest = new EventReadRequest();
        ReadOptionsFramesOptions readoptionsframesoptions = new ReadOptionsFramesOptions();
        readoptionsframesoptions.maxFrames = Integer.valueOf(0);
        ReadOptionsCommentsOptions readoptionscommentsoptions = new ReadOptionsCommentsOptions();
        readoptionscommentsoptions.maxComments = Integer.valueOf(0);
        ArrayList arraylist = new ArrayList();
        ReadOptionsPhotosOptions readoptionsphotosoptions = new ReadOptionsPhotosOptions();
        readoptionsphotosoptions.maxPhotos = Integer.valueOf(0);
        arraylist.add(readoptionsphotosoptions);
        ReadOptionsUpdateOptions readoptionsupdateoptions = new ReadOptionsUpdateOptions();
        readoptionsupdateoptions.includeActivityId = Boolean.valueOf(true);
        readoptionsupdateoptions.includeUpdate = Boolean.valueOf(true);
        EventSelector eventselector = new EventSelector();
        eventselector.eventId = mEventId;
        eventselector.authKey = mAuthKey;
        ArrayList arraylist1 = new ArrayList();
        ReadOptions readoptions = new ReadOptions();
        readoptions.photosOptions = arraylist;
        readoptions.framesOptions = readoptionsframesoptions;
        readoptions.commentsOptions = readoptionscommentsoptions;
        readoptions.responseFormat = "LIST";
        readoptions.includePlusEvent = Boolean.valueOf(true);
        readoptions.resolvePersons = Boolean.valueOf(true);
        readoptions.eventUpdateOptions = readoptionsupdateoptions;
        arraylist1.add(readoptions);
        eventreadrequest.readOptions = arraylist1;
        eventreadrequest.eventSelector = eventselector;
        return eventreadrequest;
    }
	
	public static interface EventQuery
    {

        public static final String PROJECTION[] = {
            "polling_token", "resume_token", "display_time"
        };

    }
}
