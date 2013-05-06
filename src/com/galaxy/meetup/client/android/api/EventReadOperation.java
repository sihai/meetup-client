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
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.Comment;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.EmbedsPerson;
import com.galaxy.meetup.server.client.domain.EventFrame;
import com.galaxy.meetup.server.client.domain.EventSelector;
import com.galaxy.meetup.server.client.domain.Invitee;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.domain.ReadOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsCommentsOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsFramesOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsPhotosOptions;
import com.galaxy.meetup.server.client.domain.ReadOptionsUpdateOptions;
import com.galaxy.meetup.server.client.domain.ReadResponsePhotosData;
import com.galaxy.meetup.server.client.domain.Update;
import com.galaxy.meetup.server.client.domain.request.EventReadRequest;
import com.galaxy.meetup.server.client.domain.response.EventLeafResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class EventReadOperation extends PlusiOperation {

	private static final String EVENT_PROJECTION[] = {
        "polling_token", "resume_token"
    };
    private String mAuthKey;
    private final String mEventId;
    private final boolean mFetchNewer;
    private final String mInvitationToken;
    private boolean mPermissionErrorEncountered;
    private String mPollingToken;
    private final boolean mResolveTokens;
    private String mResumeToken;
    
    public EventReadOperation(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, String s4, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, esaccount, "eventread", null, null, EventLeafResponse.class);
        mPermissionErrorEncountered = false;
        if(TextUtils.isEmpty(s))
        {
            throw new IllegalArgumentException("Event ID must not be empty");
        } else
        {
            mEventId = s;
            mPollingToken = s1;
            mResumeToken = s2;
            mAuthKey = s3;
            mInvitationToken = s4;
            mFetchNewer = flag;
            mResolveTokens = false;
            return;
        }
    }

    public EventReadOperation(Context context, EsAccount esaccount, String eventId, String s1, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, esaccount, "eventread", null, null, EventLeafResponse.class);
        mPermissionErrorEncountered = false;
        if(TextUtils.isEmpty(eventId))
        {
            throw new IllegalArgumentException("Event ID must not be empty");
        } else
        {
            mEventId = eventId;
            mPollingToken = null;
            mResumeToken = null;
            mAuthKey = s1;
            mInvitationToken = null;
            mFetchNewer = flag;
            mResolveTokens = true;
            return;
        }
    }

    protected final void handleResponse(Response response) throws IOException {
        Update update;
        PlusEvent plusevent;
        String s;
        long l;
        ArrayList arraylist;
        EventLeafResponse eventleafresponse = (EventLeafResponse)response;
        update = eventleafresponse.update;
        plusevent = eventleafresponse.plusEvent;
        s = eventleafresponse.activityId;
        l = EsEventData.getDisplayTime(mContext, mAccount, plusevent);
        arraylist = new ArrayList();
        if(TextUtils.equals(eventleafresponse.status, "INSUFFICIENT_PERMISSION")) {
        	mPermissionErrorEncountered = true;
        	return;
        }
        
        String s1;
        String s2;
        boolean flag;
        boolean flag1;
        boolean flag2;
        ArrayList arraylist1;
        if(TextUtils.isEmpty(eventleafresponse.resumeToken))
        {
            if(mFetchNewer)
                s1 = mResumeToken;
            else
                s1 = null;
        } else
        {
            s1 = eventleafresponse.resumeToken;
        }
        if(mResumeToken == null || mFetchNewer)
            s2 = eventleafresponse.pollingToken;
        else
            s2 = mPollingToken;
        if(mPollingToken != null || mResumeToken != null)
            flag = true;
        else
            flag = false;
        if(!mFetchNewer)
            flag1 = true;
        else
            flag1 = false;
        if(mFetchNewer && mPollingToken != null && !TextUtils.isEmpty(eventleafresponse.resumeToken))
        {
            EsEventData.deleteEvent(mContext, mAccount, plusevent.getId());
            flag2 = true;
        } else
        {
            flag2 = flag1;
        }
        arraylist1 = new ArrayList();
        if(eventleafresponse.photosData != null)
        {
            for(Iterator iterator3 = eventleafresponse.photosData.iterator(); iterator3.hasNext();)
            {
                ReadResponsePhotosData readresponsephotosdata = (ReadResponsePhotosData)iterator3.next();
                if(readresponsephotosdata.photos != null)
                {
                    Iterator iterator4 = readresponsephotosdata.photos.iterator();
                    while(iterator4.hasNext()) 
                    {
                        DataPhoto dataphoto = (DataPhoto)iterator4.next();
                        EsEventData.EventActivity eventactivity2 = new EsEventData.EventActivity();
                        eventactivity2.activityType = 100;
                        if(dataphoto.uploadTimestampSeconds != null)
                            eventactivity2.timestamp = (long)(1000D * dataphoto.uploadTimestampSeconds.doubleValue());
                        if(dataphoto.owner != null)
                        {
                            eventactivity2.ownerGaiaId = dataphoto.owner.id;
                            eventactivity2.ownerName = dataphoto.owner.displayName;
                            EmbedsPerson embedsperson2 = new EmbedsPerson();
                            embedsperson2.setName(eventactivity2.ownerName);
                            embedsperson2.setOwnerObfuscatedId(eventactivity2.ownerGaiaId);
                            embedsperson2.setImageUrl(dataphoto.owner.profilePhotoUrl);
                            arraylist.add(embedsperson2);
                        }
                        if(eventactivity2.ownerGaiaId != null && dataphoto.original != null && dataphoto.original.url != null)
                        {
                            if(flag2)
                                l = eventactivity2.timestamp;
                            eventactivity2.data =dataphoto.toJsonString();
                            arraylist1.add(eventactivity2);
                        }
                    }
                }
            }

        }
        if(eventleafresponse.comments != null)
        {
            Iterator iterator2 = eventleafresponse.comments.iterator();
            do
            {
                if(!iterator2.hasNext())
                    break;
                Comment comment = (Comment)iterator2.next();
                EsEventData.EventActivity eventactivity1 = new EsEventData.EventActivity();
                eventactivity1.activityType = 5;
                if(comment.timestamp != null)
                    eventactivity1.timestamp = comment.timestamp.longValue();
                eventactivity1.ownerGaiaId = comment.obfuscatedId;
                eventactivity1.ownerName = comment.authorName;
                EmbedsPerson embedsperson1 = new EmbedsPerson();
                embedsperson1.setName(eventactivity1.ownerName);
                embedsperson1.setOwnerObfuscatedId(eventactivity1.ownerGaiaId);
                embedsperson1.setImageUrl(comment.authorPhotoUrl);
                arraylist.add(embedsperson1);
                EsEventData.EventComment eventcomment = new EsEventData.EventComment();
                eventcomment.commentId = comment.commentId;
                eventcomment.text = comment.text;
                if(comment.isOwnedByViewer != null)
                    eventcomment.ownedByViewer = comment.isOwnedByViewer.booleanValue();
                if(comment.plusone != null && comment.plusone.globalCount != null)
                    eventcomment.totalPlusOnes = comment.plusone.globalCount.intValue();
                if(eventactivity1.ownerGaiaId != null && !TextUtils.isEmpty(eventcomment.text))
                {
                    eventactivity1.data = eventcomment.toJsonString();
                    arraylist1.add(eventactivity1);
                }
            } while(true);
        }
        if(eventleafresponse.frames != null)
        {
            Iterator iterator = eventleafresponse.frames.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                EventFrame eventframe = (EventFrame)iterator.next();
                EsEventData.EventActivity eventactivity = new EsEventData.EventActivity();
                EsEventData.EventCoalescedFrame eventcoalescedframe;
                if("INVITED".equals(eventframe.verbType))
                    eventactivity.activityType = 2;
                else
                if("RSVP_NO".equals(eventframe.verbType))
                    eventactivity.activityType = 3;
                else
                if("RSVP_YES".equals(eventframe.verbType))
                {
                    eventactivity.activityType = 4;
                } else
                {
                    if(!"CHECKIN".equals(eventframe.verbType))
                        continue;
                    eventactivity.activityType = 1;
                }
                if(eventframe.lastTimeMillis != null)
                    eventactivity.timestamp = eventframe.lastTimeMillis.longValue();
                eventcoalescedframe = new EsEventData.EventCoalescedFrame();
                eventcoalescedframe.people = new ArrayList();
                if(eventframe.invitee != null)
                {
                    Iterator iterator1 = eventframe.invitee.iterator();
                    do
                    {
                        if(!iterator1.hasNext())
                            break;
                        Invitee invitee = (Invitee)iterator1.next();
                        EmbedsPerson embedsperson = invitee.getInvitee();
                        if(embedsperson != null && embedsperson.getOwnerObfuscatedId() != null)
                        {
                            EsEventData.EventPerson eventperson = new EsEventData.EventPerson();
                            eventperson.gaiaId = embedsperson.getOwnerObfuscatedId();
                            eventperson.name = embedsperson.getName();
                            eventcoalescedframe.people.add(eventperson);
                            eventperson.numAdditionalGuests = invitee.getNumAdditionalGuests().intValue();
                            arraylist.add(embedsperson);
                        }
                    } while(true);
                }
                if(!eventcoalescedframe.people.isEmpty())
                {
                    eventactivity.data = eventcoalescedframe.toJsonString();
                    arraylist1.add(eventactivity);
                }
            } while(true);
        }
        EsEventData.updateEventActivities(mContext, mAccount, s, plusevent, update, s2, s1, arraylist1, flag, l, arraylist);
    }

    public final void onHttpOperationComplete(int i, String s, Exception exception)
    {
        if(i != 404) {
        	 if(i >= 400 && TextUtils.equals(mEventId, InstantUpload.getInstantShareEventId(mContext)))
             {
                 if(EsLog.isLoggable("HttpTransaction", 4))
                     Log.i("HttpTransaction", (new StringBuilder("[EVENT_READ] received error: ")).append(i).append("; disable IS").toString());
                 EsEventData.disableInstantShare(mContext);
             }
        } else { 
        	EsEventData.deleteEvent(mContext, mAccount, mEventId);
        }
    }

    protected final Request populateRequest()
    {
        EventReadRequest eventreadrequest = new EventReadRequest();
        Cursor cursor = null;
        if(mResolveTokens) {
        	try {
	        	cursor = EsEventData.getEvent(mContext, mAccount, mEventId, EVENT_PROJECTION);
		        if(cursor.moveToFirst())
		        {
		            mPollingToken = cursor.getString(0);
		            mResumeToken = cursor.getString(1);
		        }
        	} finally {
        		if(null != cursor) {
        			cursor.close();
        		}
        	}
        }
        ReadOptionsUpdateOptions readoptionsupdateoptions = new ReadOptionsUpdateOptions();
        readoptionsupdateoptions.includeActivityId = Boolean.valueOf(true);
        readoptionsupdateoptions.includeUpdate = Boolean.valueOf(true);
        ReadOptionsFramesOptions readoptionsframesoptions = new ReadOptionsFramesOptions();
        readoptionsframesoptions.maxFrames = Integer.valueOf(1000);
        ReadOptionsCommentsOptions readoptionscommentsoptions = new ReadOptionsCommentsOptions();
        readoptionscommentsoptions.maxComments = Integer.valueOf(500);
        ArrayList arraylist = new ArrayList();
        ReadOptionsPhotosOptions readoptionsphotosoptions = new ReadOptionsPhotosOptions();
        readoptionsphotosoptions.maxPhotos = Integer.valueOf(50);
        arraylist.add(readoptionsphotosoptions);
        EventSelector eventselector = new EventSelector();
        eventselector.eventId = mEventId;
        eventselector.authKey = mAuthKey;
        ArrayList arraylist1 = new ArrayList();
        ReadOptions readoptions = new ReadOptions();
        readoptions.photosOptions = arraylist;
        readoptions.framesOptions = readoptionsframesoptions;
        readoptions.commentsOptions = readoptionscommentsoptions;
        readoptions.eventUpdateOptions = readoptionsupdateoptions;
        readoptions.responseFormat = "LIST";
        readoptions.includePlusEvent = Boolean.valueOf(true);
        readoptions.resolvePersons = Boolean.valueOf(true);
        arraylist1.add(readoptions);
        eventreadrequest.readOptions = arraylist1;
        eventreadrequest.eventSelector = eventselector;
        eventreadrequest.invitationToken = mInvitationToken;
        Exception exception;
        if(mFetchNewer || mResumeToken == null)
            eventreadrequest.pollingToken = mPollingToken;
        else
            eventreadrequest.resumeToken = mResumeToken;
        return eventreadrequest;
    }

    public final void setErrorInfo(int i, String s, Exception exception)
    {
        if(mPermissionErrorEncountered)
            super.setErrorInfo(403, "INSUFFICIENT_PERMISSION", null);
        else
            super.setErrorInfo(i, s, exception);
    }
}
