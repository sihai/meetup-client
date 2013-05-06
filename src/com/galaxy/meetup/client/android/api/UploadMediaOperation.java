/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.ApiaryHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.MeetupRequest;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.request.UploadMediaRequest;
import com.galaxy.meetup.server.client.domain.response.UploadMediaResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class UploadMediaOperation extends PlusiOperation {

	private static final Bundle QUERY_PARAMS;
    private final String mAlbumId;
    private final String mAlbumLabel;
    private final String mAlbumTitle;
    private final String mOwnerId;
    private final byte mPayloadData[];
    private UploadMediaResponse mResponse;
    private Integer mTopOffset;

    static 
    {
        Bundle bundle = new Bundle();
        bundle.putString("uploadType", "multipart");
        QUERY_PARAMS = bundle;
    }

    
    public UploadMediaOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, String s2, 
            String s3, byte abyte0[])
    {
        super(context, esaccount, "uploadmedia", QUERY_PARAMS, intent, operationlistener, new ApiaryHttpRequestConfiguration(context, esaccount, "oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native", null, "multipart/related; boundary=onetwothreefourfivesixseven"), UploadMediaResponse.class);
        mOwnerId = s;
        mAlbumId = s1;
        mAlbumTitle = s2;
        mAlbumLabel = s3;
        mTopOffset = null;
        mPayloadData = abyte0;
    }

    public UploadMediaOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, byte abyte0[])
    {
        this(context, esaccount, intent, operationlistener, s, s1, null, null, abyte0);
    }

    public UploadMediaOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, byte abyte0[], 
            Integer integer)
    {
        this(context, esaccount, intent, operationlistener, s, s1, null, null, abyte0);
        mTopOffset = integer;
    }

    protected final MeetupRequest createHttpEntity(Request request)
    {
        UploadMediaRequest uploadmediarequest = (UploadMediaRequest)request;
        return new MeetupRequest(mAccount, uploadmediarequest, mPayloadData);
    }

    public final UploadMediaResponse getUploadMediaResponse()
    {
        return mResponse;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        UploadMediaResponse uploadmediaresponse;
        uploadmediaresponse = (UploadMediaResponse)response;
        onStartResultProcessing();
        mResponse = uploadmediaresponse;
        if(!uploadmediaresponse.setProfilePhotoSucceeded.booleanValue()) {
        	if(!EsLog.isLoggable("HttpTransaction", 6)) {
        		// TODO ???
        		throw new UploadMediaException();
        	} else { 
        		if(!"profile".equals(mAlbumId)) {
        			if("scrapbook".equals(mAlbumId))
        	            Log.e("HttpTransaction", "Failed to upload and set cover photo");
        			throw new UploadMediaException();
        		} else { 
        			Log.e("HttpTransaction", "Failed to upload and set profile photo");
        			throw new UploadMediaException();
        		}
        	}
        }
    }

    protected final Request populateRequest()
    {
        UploadMediaRequest uploadmediarequest = new UploadMediaRequest();
        uploadmediarequest.ownerId = mOwnerId;
        uploadmediarequest.albumId = mAlbumId;
        uploadmediarequest.autoResize = Boolean.valueOf(true);
        if(mAlbumLabel != null)
            uploadmediarequest.albumLabel = mAlbumLabel;
        if(mAlbumTitle != null)
            uploadmediarequest.albumTitle = mAlbumTitle;
        if(mTopOffset != null)
            uploadmediarequest.offset = mTopOffset;
        return uploadmediarequest;
    }
    
    
    public static final class UploadMediaException extends ProtocolException
    {

        public UploadMediaException()
        {
        }
    }
}
