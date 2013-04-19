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
import com.galaxy.meetup.server.client.domain.request.PhotosNameTagSuggestionApprovalRequest;
import com.galaxy.meetup.server.client.domain.response.PhotosNameTagSuggestionApprovalResponse;

/**
 * 
 * @author sihai
 *
 */
public class PhotosTagSuggestionApprovalOperation extends PlusiOperation {
	
	private final boolean mApprove;
    private final String mOwnerId;
    private final String mPhotoId;
    private final String mShapeId;
    private final String mTaggeeId;
    
	public PhotosTagSuggestionApprovalOperation(Context context, EsAccount esaccount, String s, boolean flag, String s1, String s2, String s3, 
            Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "photosnametagsuggestionapproval", intent, operationlistener, PhotosNameTagSuggestionApprovalResponse.class);
        mOwnerId = s;
        mApprove = flag;
        mPhotoId = s1;
        mShapeId = s2;
        mTaggeeId = s3;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        PhotosNameTagSuggestionApprovalResponse photosnametagsuggestionapprovalresponse = (PhotosNameTagSuggestionApprovalResponse)genericjson;
        onStartResultProcessing();
        if(photosnametagsuggestionapprovalresponse.success.booleanValue())
            EsPhotosDataApiary.updatePhotoShapeApproval(mContext, mAccount, Long.valueOf(mPhotoId).longValue(), Long.valueOf(mShapeId).longValue(), mApprove);
    }

    protected final GenericJson populateRequest()
    {
        PhotosNameTagSuggestionApprovalRequest photosnametagsuggestionapprovalrequest = new PhotosNameTagSuggestionApprovalRequest();
        photosnametagsuggestionapprovalrequest.ownerId = mOwnerId;
        photosnametagsuggestionapprovalrequest.approve = Boolean.valueOf(mApprove);
        photosnametagsuggestionapprovalrequest.photoId = mPhotoId;
        photosnametagsuggestionapprovalrequest.shapeId = mShapeId;
        photosnametagsuggestionapprovalrequest.taggeeId = mTaggeeId;
        return photosnametagsuggestionapprovalrequest;
    }

}
