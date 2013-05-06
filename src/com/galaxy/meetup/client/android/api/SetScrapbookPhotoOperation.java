/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.SetScrapbookCoverPhotoRequest;
import com.galaxy.meetup.server.client.domain.response.SetScrapbookCoverPhotoResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SetScrapbookPhotoOperation extends PlusiOperation {

	private boolean mIsGalleryPhoto;
    private String mPhotoId;
    private int mTopOffset;
    
	public static final class SetScrapbookPhotoException extends ProtocolException
    {

        public SetScrapbookPhotoException()
        {
        }
    }


    public SetScrapbookPhotoOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, int i, boolean flag)
    {
        super(context, esaccount, "setscrapbookcoverphoto", intent, operationlistener, SetScrapbookCoverPhotoResponse.class);
        mPhotoId = s;
        mTopOffset = i;
        mIsGalleryPhoto = flag;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        if(!((SetScrapbookCoverPhotoResponse)response).success.booleanValue())
            throw new SetScrapbookPhotoException();
        else
            return;
    }

    protected final Request populateRequest()
    {
        SetScrapbookCoverPhotoRequest setscrapbookcoverphotorequest = new SetScrapbookCoverPhotoRequest();
        setscrapbookcoverphotorequest.ownerId = getAccount().getGaiaId();
        setscrapbookcoverphotorequest.photoId = mPhotoId;
        setscrapbookcoverphotorequest.offset = Integer.valueOf(mTopOffset);
        setscrapbookcoverphotorequest.galleryPhoto = Boolean.valueOf(mIsGalleryPhoto);
        return setscrapbookcoverphotorequest;
    }

}
