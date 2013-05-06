/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPhotosDataApiary;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.server.client.domain.DataAlbum;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.RequestsPhotoOptions;
import com.galaxy.meetup.server.client.domain.request.PhotosInAlbumRequest;
import com.galaxy.meetup.server.client.domain.response.PhotosInAlbumResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class PhotosInAlbumOperation extends PlusiOperation {

	private final String mAuthkey;
    private final String mCollectionId;
    private final boolean mCoverOnly;
    private boolean mIsAlbum;
    private final String mOwnerId;
    private final EsSyncAdapterService.SyncState mSyncState;
    
    public PhotosInAlbumOperation(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, String s, String s1, boolean flag, Intent intent, 
            HttpOperation.OperationListener operationlistener)
    {
        this(context, esaccount, syncstate, s, s1, flag, null, null, null);
    }

    private PhotosInAlbumOperation(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, String s, String s1, boolean flag, Intent intent, 
            HttpOperation.OperationListener operationlistener, String s2)
    {
        super(context, esaccount, "photosinalbum", intent, operationlistener, PhotosInAlbumResponse.class);
        mCollectionId = s;
        mOwnerId = s1;
        mCoverOnly = flag;
        mSyncState = syncstate;
        mAuthkey = s2;
        //Long.parseLong(mCollectionId);
        mIsAlbum = true;
    }

    public PhotosInAlbumOperation(Context context, EsAccount esaccount, String s, String s1, Intent intent, HttpOperation.OperationListener operationlistener, String s2)
    {
        this(context, esaccount, null, s, s1, false, intent, operationlistener, s2);
    }

    protected final void handleResponse(Response response) throws IOException
    {
        List list;
        DataAlbum dataalbum;
        PhotosInAlbumResponse photosinalbumresponse = (PhotosInAlbumResponse)response;
        onStartResultProcessing();
        list = photosinalbumresponse.photo;
        dataalbum = photosinalbumresponse.album;
        Boolean boolean1 = photosinalbumresponse.isDownloadable;
        if(TextUtils.equals(mCollectionId, "camerasync"))
        {
            DataPhoto dataphoto;
            if(list != null && list.size() > 0)
                dataphoto = (DataPhoto)list.get(0);
            else
                dataphoto = null;
            EsPhotosDataApiary.updateInstantUploadCover(mContext, mAccount, dataphoto);
        }
        if(mCoverOnly || dataalbum == null) 
        	return;
        
        if(!mIsAlbum) {
        	if("CAMERA_SYNC".equals(dataalbum.albumType))
                EsPhotosDataApiary.insertStreamPhotos(mContext, mAccount, mSyncState, "camerasync", mOwnerId, list, false);
            else
            if("UPDATES_ALBUMS".equals(dataalbum.albumType))
                EsPhotosDataApiary.insertStreamPhotos(mContext, mAccount, mSyncState, "posts", mOwnerId, list, false);
            else
            if("PROFILE_PHOTOS".equals(dataalbum.albumType))
                EsPhotosDataApiary.insertStreamPhotos(mContext, mAccount, mSyncState, "profile", mOwnerId, list, false);
            else
            if("BUNCH_ALBUMS".equals(dataalbum.albumType))
                EsPhotosDataApiary.insertStreamPhotos(mContext, mAccount, mSyncState, "messenger", mOwnerId, list, false);
        } else { 
        	EsPhotosDataApiary.insertAlbumPhotos(mContext, mAccount, mSyncState, dataalbum, list, boolean1);
        }
        
    }

    protected final Request populateRequest()
    {
        PhotosInAlbumRequest photosinalbumrequest = new PhotosInAlbumRequest();
        RequestsPhotoOptions requestsphotooptions = new RequestsPhotoOptions();
        requestsphotooptions.returnAlbumInfo = Boolean.valueOf(true);
        requestsphotooptions.returnComments = Boolean.valueOf(false);
        requestsphotooptions.returnDownloadability = Boolean.valueOf(true);
        boolean flag = TextUtils.equals(mOwnerId, mAccount.getGaiaId());
        boolean flag1 = false;
        if(!flag)
            flag1 = true;
        requestsphotooptions.returnOwnerInfo = Boolean.valueOf(flag1);
        requestsphotooptions.returnPhotos = Boolean.valueOf(true);
        requestsphotooptions.returnPlusOnes = Boolean.valueOf(true);
        requestsphotooptions.returnShapes = Boolean.valueOf(true);
        requestsphotooptions.returnVideoUrls = Boolean.valueOf(true);
        photosinalbumrequest.collectionId = mCollectionId;
        photosinalbumrequest.ownerId = mOwnerId;
        if(mAuthkey != null)
            photosinalbumrequest.authkey = mAuthkey;
        photosinalbumrequest.photoOptions = requestsphotooptions;
        if(mCoverOnly)
            photosinalbumrequest.maxResults = Integer.valueOf(1);
        return photosinalbumrequest;
    }
}
