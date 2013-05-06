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
import com.galaxy.meetup.server.client.domain.VolumeControlMap;
import com.galaxy.meetup.server.client.domain.response.GetVolumeControlsResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetVolumeControlsOperation extends PlusiOperation {

	private VolumeControlMap mMap;

	public GetVolumeControlsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getvolumecontrols", null, operationlistener, GetVolumeControlsResponse.class);
    }

    public final VolumeControlMap getVolumeControlMap()
    {
        return mMap;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        mMap = ((GetVolumeControlsResponse)response).map;
    }

    protected final Request populateRequest()
    {
    	return new Request();
    }
}
