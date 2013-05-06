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
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.RequestsPhotoOptions;
import com.galaxy.meetup.server.client.domain.request.PhotosOfUserRequest;
import com.galaxy.meetup.server.client.domain.response.PhotosOfUserResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class PhotosOfUserOperation extends PlusiOperation {

	private final boolean mCoverOnly;
    private final EsSyncAdapterService.SyncState mSyncState;
    private final String mUserId;
    
	public PhotosOfUserOperation(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, String s, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "photosofuser", intent, operationlistener, PhotosOfUserResponse.class);
        mSyncState = syncstate;
        mUserId = s;
        mCoverOnly = flag;
    }

    public PhotosOfUserOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        this(context, esaccount, null, s, false, intent, operationlistener);
    }

    protected final void handleResponse(Response response) throws IOException
    {
        PhotosOfUserResponse photosofuserresponse = (PhotosOfUserResponse)response;
        onStartResultProcessing();
        if(TextUtils.equals(mUserId, mAccount.getGaiaId()))
        {
            DataPhoto dataphoto;
            if(photosofuserresponse.approvedPhoto != null && photosofuserresponse.approvedPhoto.size() > 0)
                dataphoto = (DataPhoto)photosofuserresponse.approvedPhoto.get(0);
            else
            if(photosofuserresponse.unapprovedPhoto != null && photosofuserresponse.unapprovedPhoto.size() > 0)
                dataphoto = (DataPhoto)photosofuserresponse.unapprovedPhoto.get(0);
            else
                dataphoto = null;
            EsPhotosDataApiary.updatePhotosOfYouCover(mContext, mAccount, dataphoto);
        }
        if(!mCoverOnly)
            EsPhotosDataApiary.insertUserPhotos(mContext, mAccount, mSyncState, photosofuserresponse.approvedPhoto, photosofuserresponse.unapprovedPhoto, mUserId);
    }

    protected final Request populateRequest()
    {
        PhotosOfUserRequest photosofuserrequest = new PhotosOfUserRequest();
        photosofuserrequest.ownerId = mUserId;
        RequestsPhotoOptions requestsphotooptions = new RequestsPhotoOptions();
        requestsphotooptions.returnAlbumInfo = Boolean.valueOf(true);
        requestsphotooptions.returnComments = Boolean.valueOf(false);
        requestsphotooptions.returnDownloadability = Boolean.valueOf(true);
        requestsphotooptions.returnOwnerInfo = Boolean.valueOf(false);
        requestsphotooptions.returnPhotos = Boolean.valueOf(true);
        requestsphotooptions.returnPlusOnes = Boolean.valueOf(true);
        requestsphotooptions.returnShapes = Boolean.valueOf(true);
        requestsphotooptions.returnVideoUrls = Boolean.valueOf(true);
        photosofuserrequest.photoOptions = requestsphotooptions;
        if(mCoverOnly)
            photosofuserrequest.maxResults = Integer.valueOf(1);
        return photosofuserrequest;
    }

}
