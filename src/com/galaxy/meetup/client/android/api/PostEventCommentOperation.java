/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.Comment;
import com.galaxy.meetup.server.client.domain.request.PostCommentRequest;
import com.galaxy.meetup.server.client.domain.response.PostCommentResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class PostEventCommentOperation extends PlusiOperation {

	private static final Random sRandom = new Random();
    private final String mActivityId;
    private final String mContent;
    private final String mEventId;
    
	public PostEventCommentOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, String s2)
    {
        super(context, esaccount, "postcomment", intent, operationlistener, PostCommentResponse.class);
        mActivityId = s;
        mContent = s2;
        mEventId = s1;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        PostCommentResponse postcommentresponse = (PostCommentResponse)response;
        if(postcommentresponse != null)
        {
            Comment comment = postcommentresponse.comment;
            if(comment != null && mActivityId.equals(comment.updateId))
            {
                EsEventData.EventActivity eventactivity = new EsEventData.EventActivity();
                eventactivity.activityType = 5;
                eventactivity.data = comment.text;
                eventactivity.ownerName = comment.authorName;
                eventactivity.ownerGaiaId = comment.obfuscatedId;
                eventactivity.timestamp = comment.timestamp.longValue();
                EsEventData.EventComment eventcomment = new EsEventData.EventComment();
                eventcomment.commentId = comment.commentId;
                eventcomment.text = comment.text;
                if(comment.isOwnedByViewer != null)
                    eventcomment.ownedByViewer = comment.isOwnedByViewer.booleanValue();
                if(comment.plusone != null && comment.plusone.globalCount != null)
                    eventcomment.totalPlusOnes = comment.plusone.globalCount.intValue();
                if(eventactivity.ownerGaiaId != null && !TextUtils.isEmpty(eventcomment.text))
                {
                    eventactivity.data = eventcomment.toJsonString();
                    ArrayList arraylist = new ArrayList();
                    arraylist.add(eventactivity);
                    EsEventData.insertEventActivities(mContext, mAccount, mEventId, null, arraylist, true);
                }
            }
        }
    }

    protected final Request populateRequest()
    {
        PostCommentRequest postcommentrequest = new PostCommentRequest();
        long l = System.currentTimeMillis();
        postcommentrequest.clientId = (new StringBuilder()).append(mAccount.getGaiaId()).append(l).append(sRandom.nextInt()).toString();
        postcommentrequest.activityId = mActivityId;
        postcommentrequest.creationTimeMs = Long.valueOf(l);
        postcommentrequest.commentText = mContent;
        return postcommentrequest;
    }

}
