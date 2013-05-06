/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.ApiaryActivity;
import com.galaxy.meetup.client.android.network.ApiaryActivityFactory;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.network.PlatformHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.server.client.domain.ClientEmbedOptions;
import com.galaxy.meetup.server.client.domain.request.LinkPreviewRequest;
import com.galaxy.meetup.server.client.domain.response.LinkPreviewResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class LinkPreviewOperation extends PlusiOperation {

	private ApiaryActivity mActivity;
    private final CallToActionData mCallToAction;
    private final String mSourceUrl;
    
	public LinkPreviewOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, CallToActionData calltoactiondata, ApiaryApiInfo apiaryapiinfo)
    {
        super(context, esaccount, "linkpreview", null, null, new PlatformHttpRequestConfiguration(context, esaccount, "oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native", Property.PLUS_BACKEND_URL.get(), apiaryapiinfo), LinkPreviewResponse.class);
        mSourceUrl = s;
        mCallToAction = calltoactiondata;
    }

    public final ApiaryActivity getActivity()
    {
        return mActivity;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        LinkPreviewResponse linkpreviewresponse = (LinkPreviewResponse)response;
        if(linkpreviewresponse != null)
            mActivity = ApiaryActivityFactory.getApiaryActivity(linkpreviewresponse);
    }

    protected final Request populateRequest()
    {
        LinkPreviewRequest linkpreviewrequest = new LinkPreviewRequest();
        linkpreviewrequest.content = mSourceUrl;
        if(mCallToAction != null)
        {
            linkpreviewrequest.isInteractivePost = Boolean.valueOf(true);
            linkpreviewrequest.callToActionLabel = mCallToAction.mLabel;
            linkpreviewrequest.callToActionUrl = mCallToAction.mUrl;
            linkpreviewrequest.callToActionDeepLinkId = mCallToAction.mDeepLinkId;
        }
        linkpreviewrequest.useBlackboxPreviewData = Boolean.valueOf(true);
        linkpreviewrequest.fallbackToUrl = Boolean.valueOf(true);
        linkpreviewrequest.embedOptions = new ClientEmbedOptions();
        linkpreviewrequest.embedOptions.includeType = EsPostsData.getShareboxEmbedsWhitelist();
        return linkpreviewrequest;
    }

}
