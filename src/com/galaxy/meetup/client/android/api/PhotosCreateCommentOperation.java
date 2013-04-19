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
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.PhotosCreateCommentRequest;
import com.galaxy.meetup.server.client.domain.response.PhotosCreateCommentResponse;

/**
 * 
 * @author sihai
 *
 */
public class PhotosCreateCommentOperation extends PlusiOperation {

	private final String mAuthkey;
    private final String mComment;
    private final String mOwnerId;
    private final long mPhotoId;
    
    public PhotosCreateCommentOperation(Context context, EsAccount esaccount, long l, String s, String s1, String s2, 
            Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "photoscreatecomment", intent, operationlistener, PhotosCreateCommentResponse.class);
        mPhotoId = l;
        mOwnerId = s;
        mComment = s1;
        mAuthkey = s2;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        PhotosCreateCommentResponse photoscreatecommentresponse = (PhotosCreateCommentResponse)genericjson;
        onStartResultProcessing();
        EsPhotosDataApiary.updatePhotoCommentList(mContext, mAccount, Long.toString(mPhotoId), photoscreatecommentresponse.comment);
    }

    protected final GenericJson populateRequest()
    {
        PhotosCreateCommentRequest photoscreatecommentrequest = new PhotosCreateCommentRequest();
        photoscreatecommentrequest.obfuscatedOwnerId = mOwnerId;
        photoscreatecommentrequest.photoId = Long.valueOf(mPhotoId);
        photoscreatecommentrequest.comment = mComment;
        if(mAuthkey != null)
            photoscreatecommentrequest.authkey = mAuthkey;
        return photoscreatecommentrequest;
    }
}
