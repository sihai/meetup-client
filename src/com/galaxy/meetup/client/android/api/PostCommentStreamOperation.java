/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
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
public class PostCommentStreamOperation extends PlusiOperation {

	private static final Random sRandom = new Random();
    private final String mActivityId;
    private final String mContent;
    
	public PostCommentStreamOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1)
    {
        super(context, esaccount, "postcomment", intent, operationlistener, PostCommentResponse.class);
        mActivityId = s;
        mContent = s1;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        PostCommentResponse postcommentresponse = (PostCommentResponse)response;
        if(postcommentresponse != null)
        {
            Comment comment = postcommentresponse.comment;
            if(comment != null && mActivityId.equals(comment.updateId))
                EsPostsData.insertComment(mContext, mAccount, comment.updateId, comment);
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
