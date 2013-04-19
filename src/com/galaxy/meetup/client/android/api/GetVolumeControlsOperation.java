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

    protected final void handleResponse(GenericJson genericjson)
        throws IOException
    {
        mMap = ((GetVolumeControlsResponse)genericjson).map;
    }

    protected final GenericJson populateRequest()
    {
    	return new GenericJson();
    }
}
