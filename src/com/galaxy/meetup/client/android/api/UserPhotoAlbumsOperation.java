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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.UserPhotoAlbumsRequest;
import com.galaxy.meetup.server.client.domain.response.UserPhotoAlbumsResponse;

/**
 * 
 * @author sihai
 *
 */
public class UserPhotoAlbumsOperation extends PlusiOperation {

	private final String mOwnerId;
    private final EsSyncAdapterService.SyncState mSyncState;
    
    public UserPhotoAlbumsOperation(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "userphotoalbums", intent, operationlistener, UserPhotoAlbumsResponse.class);
        mSyncState = syncstate;
        mOwnerId = s;
    }

    public UserPhotoAlbumsOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        this(context, esaccount, null, s, intent, operationlistener);
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        UserPhotoAlbumsResponse userphotoalbumsresponse = (UserPhotoAlbumsResponse)genericjson;
        onStartResultProcessing();
        String s;
        if(mOwnerId == null)
            s = mAccount.getGaiaId();
        else
            s = mOwnerId;
        EsPhotosDataApiary.insertAlbums(mContext, mAccount, mSyncState, s, userphotoalbumsresponse.aggregateAlbum, userphotoalbumsresponse.nonAggregateAlbum);
    }

    protected final GenericJson populateRequest()
    {
        UserPhotoAlbumsRequest userphotoalbumsrequest = new UserPhotoAlbumsRequest();
        boolean flag;
        String s;
        if(mOwnerId == null || !TextUtils.equals(mOwnerId, mAccount.getGaiaId()))
            flag = true;
        else
            flag = false;
        userphotoalbumsrequest.sharedAlbumsOnly = Boolean.valueOf(flag);
        if(mOwnerId == null)
            s = mAccount.getGaiaId();
        else
            s = mOwnerId;
        userphotoalbumsrequest.ownerId = s;
        userphotoalbumsrequest.maxPreviewCount = Integer.valueOf(1);
        userphotoalbumsrequest.maxResults = Integer.valueOf(200);
        return userphotoalbumsrequest;
    }
}
