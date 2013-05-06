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
import com.galaxy.meetup.server.client.domain.request.PhotosPlusOneRequest;
import com.galaxy.meetup.server.client.domain.response.PhotosPlusOneResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class PhotosPlusOneOperation extends PlusiOperation {

	private final long mAlbumId;
    private final boolean mIsPlusOne;
    private final String mOwnerId;
    private final String mPhotoId;
    
    public PhotosPlusOneOperation(Context context, EsAccount esaccount, long l, String s, long l1, 
            boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "photosplusone", intent, operationlistener, PhotosPlusOneResponse.class);
        mPhotoId = Long.toString(l);
        mOwnerId = s;
        mAlbumId = l1;
        mIsPlusOne = flag;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        PhotosPlusOneResponse photosplusoneresponse;
        photosplusoneresponse = (PhotosPlusOneResponse)response;
        onStartResultProcessing();
        if(photosplusoneresponse.success.booleanValue()) {
        	if(photosplusoneresponse.plusOne != null)
            {
                Context context = mContext;
                EsAccount esaccount = mAccount;
                String s = mPhotoId;
                //mOwnerId;
                EsPhotosDataApiary.updatePhotoPlusOne(context, esaccount, s, photosplusoneresponse.plusOne);
            }
        } else { 
        	Context context1 = mContext;
            EsAccount esaccount1 = mAccount;
            String s1 = mPhotoId;
            //mOwnerId;
            boolean flag;
            if(!mIsPlusOne)
                flag = true;
            else
                flag = false;
            EsPhotosDataApiary.updatePhotoPlusOne(context1, esaccount1, s1, flag);
        }
    }

    protected final Request populateRequest()
    {
        PhotosPlusOneRequest photosplusonerequest = new PhotosPlusOneRequest();
        photosplusonerequest.ownerId = mOwnerId;
        photosplusonerequest.photoId = mPhotoId;
        photosplusonerequest.albumId = Long.valueOf(mAlbumId);
        photosplusonerequest.isPlusOne = Boolean.valueOf(mIsPlusOne);
        photosplusonerequest.returnPlusOneResult = Boolean.valueOf(true);
        Context context = mContext;
        EsAccount esaccount = mAccount;
        String s = mPhotoId;
        String _tmp = mOwnerId;
        EsPhotosDataApiary.updatePhotoPlusOne(context, esaccount, s, mIsPlusOne);
        return photosplusonerequest;
    }
}
