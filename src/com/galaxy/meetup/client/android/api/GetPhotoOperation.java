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
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.RequestsPhotoOptions;
import com.galaxy.meetup.server.client.domain.request.GetPhotoRequest;
import com.galaxy.meetup.server.client.domain.response.GetPhotoResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetPhotoOperation extends PlusiOperation {

	private DataPhoto mDataPhoto;
    private final long mPhotoId;
    private final String mUserId;
    
	public GetPhotoOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, long l, String s)
    {
        super(context, esaccount, "getphoto", intent, operationlistener, GetPhotoResponse.class);
        mPhotoId = l;
        mUserId = s;
    }

    public final DataPhoto getDataPhoto()
    {
        return mDataPhoto;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        GetPhotoResponse getphotoresponse = (GetPhotoResponse)response;
        onStartResultProcessing();
        mDataPhoto = getphotoresponse.photo;
        EsPhotosDataApiary.insertPhoto(mContext, mAccount, null, getphotoresponse.photo, getphotoresponse.isDownloadable);
    }

    protected final Request populateRequest()
    {
        GetPhotoRequest getphotorequest = new GetPhotoRequest();
        getphotorequest.ownerId = mUserId;
        RequestsPhotoOptions requestsphotooptions = new RequestsPhotoOptions();
        requestsphotooptions.returnAlbumInfo = Boolean.valueOf(true);
        requestsphotooptions.returnComments = Boolean.valueOf(true);
        requestsphotooptions.returnDownloadability = Boolean.valueOf(true);
        requestsphotooptions.returnOwnerInfo = Boolean.valueOf(false);
        requestsphotooptions.returnPhotos = Boolean.valueOf(true);
        requestsphotooptions.returnPlusOnes = Boolean.valueOf(true);
        requestsphotooptions.returnShapes = Boolean.valueOf(true);
        requestsphotooptions.returnVideoUrls = Boolean.valueOf(true);
        getphotorequest.photoOptions = requestsphotooptions;
        getphotorequest.photoId = Long.toString(mPhotoId);
        return getphotorequest;
    }

}
