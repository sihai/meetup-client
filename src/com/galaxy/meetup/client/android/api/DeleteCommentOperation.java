/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPhotosDataApiary;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.DeleteCommentRequest;
import com.galaxy.meetup.server.client.domain.response.DeleteCommentResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class DeleteCommentOperation extends PlusiOperation {

	private final String mCommentId;
	
	public DeleteCommentOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s)
    {
        super(context, esaccount, "deletecomment", intent, operationlistener, DeleteCommentResponse.class);
        mCommentId = s;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        EsPostsData.deleteComment(mContext, mAccount, mCommentId);
        EsPhotosDataApiary.deletePhotoComment(mContext, mAccount, mCommentId);
    }

    protected final Request populateRequest()
    {
    	DeleteCommentRequest response = new DeleteCommentRequest();
        response.commentId = mCommentId;
        return response;
    }

}
