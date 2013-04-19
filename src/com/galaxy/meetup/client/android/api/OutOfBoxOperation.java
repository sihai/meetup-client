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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.MobileOutOfBoxRequest;
import com.galaxy.meetup.server.client.domain.response.MobileOutOfBoxResponse;

/**
 * 
 * @author sihai
 *
 */
public class OutOfBoxOperation extends PlusiOperation {

	private MobileOutOfBoxRequest mRequest;
    private MobileOutOfBoxResponse mResponse;
    
    public OutOfBoxOperation(Context context, EsAccount esaccount, MobileOutOfBoxRequest mobileoutofboxrequest, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "mobileoutofboxflow", null, null, MobileOutOfBoxResponse.class);
        mRequest = mobileoutofboxrequest;
    }

    public final MobileOutOfBoxResponse getResponse()
    {
        return mResponse;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        mResponse = (MobileOutOfBoxResponse)genericjson;
    }

    protected final GenericJson populateRequest()
    {
        MobileOutOfBoxRequest mobileoutofboxrequest = new MobileOutOfBoxRequest();
        mobileoutofboxrequest.clientType = "NATIVE_ANDROID";
        String s;
        if(mRequest.upgradeOrigin != null)
            s = mRequest.upgradeOrigin;
        else
            s = "DEFAULT";
        mobileoutofboxrequest.upgradeOrigin = s;
        mobileoutofboxrequest.action = mRequest.action;
        mobileoutofboxrequest.input = mRequest.input;
        return mobileoutofboxrequest;
    }
}
