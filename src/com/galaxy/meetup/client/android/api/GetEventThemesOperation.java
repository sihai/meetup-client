/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.response.GetEventThemesResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetEventThemesOperation extends PlusiOperation {

	public GetEventThemesOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "geteventthemes", intent, operationlistener, GetEventThemesResponse.class);
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetEventThemesResponse geteventthemesresponse = (GetEventThemesResponse)genericjson;
        if(geteventthemesresponse.themes != null)
            EsEventData.insertEventThemes(mContext, mAccount, geteventthemesresponse.themes);
    }

    protected final GenericJson populateRequest()
    {
    	return new GenericJson();
    }

}
