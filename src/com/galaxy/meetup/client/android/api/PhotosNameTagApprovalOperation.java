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
import com.galaxy.meetup.server.client.domain.request.PhotosNameTagApprovalRequest;
import com.galaxy.meetup.server.client.domain.response.PhotosNameTagApprovalResponse;

/**
 * 
 * @author sihai
 *
 */
public class PhotosNameTagApprovalOperation extends PlusiOperation {

	private final boolean mApprove;
    private final String mOwnerId;
    private final long mPhotoId;
    private final long mShapeId;
    
	public PhotosNameTagApprovalOperation(Context context, EsAccount esaccount, long l, String s, long l1, 
            boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "photosnametagapproval", intent, operationlistener, PhotosNameTagApprovalResponse.class);
        mOwnerId = s;
        mPhotoId = l;
        mShapeId = l1;
        mApprove = flag;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        PhotosNameTagApprovalResponse photosnametagapprovalresponse = (PhotosNameTagApprovalResponse)genericjson;
        onStartResultProcessing();
        if(photosnametagapprovalresponse.success.booleanValue())
            EsPhotosDataApiary.updatePhotoShapeApproval(mContext, mAccount, mPhotoId, mShapeId, mApprove);
    }

    protected final GenericJson populateRequest()
    {
        PhotosNameTagApprovalRequest photosnametagapprovalrequest = new PhotosNameTagApprovalRequest();
        photosnametagapprovalrequest.obfuscatedOwnerId = mOwnerId;
        photosnametagapprovalrequest.photoId = Long.valueOf(mPhotoId);
        photosnametagapprovalrequest.shapeId = Long.valueOf(mShapeId);
        photosnametagapprovalrequest.approve = Boolean.valueOf(mApprove);
        return photosnametagapprovalrequest;
    }

}
