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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.DeleteCommentRequest;
import com.galaxy.meetup.server.client.domain.response.DeleteCommentResponse;

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

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        EsPostsData.deleteComment(mContext, mAccount, mCommentId);
        EsPhotosDataApiary.deletePhotoComment(mContext, mAccount, mCommentId);
    }

    protected final GenericJson populateRequest()
    {
    	DeleteCommentRequest genericjson = new DeleteCommentRequest();
        genericjson.commentId = mCommentId;
        return genericjson;
    }

}
