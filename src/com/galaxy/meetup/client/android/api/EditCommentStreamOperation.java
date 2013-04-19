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
import com.galaxy.meetup.client.android.content.EsPhotosDataApiary;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.Comment;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.EditCommentRequest;
import com.galaxy.meetup.server.client.domain.response.EditCommentResponse;

/**
 * 
 * @author sihai
 *
 */
public class EditCommentStreamOperation extends PlusiOperation {

	private final String mActivityId;
    private final String mCommentId;
    private final String mContent;
    
	public EditCommentStreamOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, String s2)
    {
        super(context, esaccount, "editcomment", intent, operationlistener, EditCommentResponse.class);
        mActivityId = s;
        mCommentId = s1;
        mContent = s2;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        EditCommentResponse editcommentresponse = (EditCommentResponse)genericjson;
        if(editcommentresponse != null)
        {
            Comment comment = editcommentresponse.comment;
            if(comment != null && TextUtils.equals(mActivityId, comment.updateId) && TextUtils.equals(mCommentId, comment.commentId))
            {
                EsPostsData.updateComment(mContext, mAccount, comment.updateId, comment);
                EsPhotosDataApiary.updatePhotoComment(mContext, mAccount, comment);
            }
        }
    }

    protected final GenericJson populateRequest()
    {
        EditCommentRequest editcommentrequest = new EditCommentRequest();
        editcommentrequest.activityId = mActivityId;
        editcommentrequest.commentId = mCommentId;
        editcommentrequest.commentText = mContent;
        return editcommentrequest;
    }

}
