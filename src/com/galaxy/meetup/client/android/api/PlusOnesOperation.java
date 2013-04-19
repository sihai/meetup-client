/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.ApiaryApiInfo;
import com.galaxy.meetup.client.android.network.ApiaryOperation;
import com.galaxy.meetup.client.android.network.PlatformHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.MeetupRequest;
import com.galaxy.meetup.client.util.PlatformContractUtils;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.Plusones;

/**
 * 
 * @author sihai
 *
 */
public class PlusOnesOperation extends ApiaryOperation {

	private Plusones mPlusones;
	
	public PlusOnesOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, ApiaryApiInfo apiaryapiinfo, String s)
    {
        super(context, esaccount, (new android.net.Uri.Builder()).scheme("https").authority(Property.POS_FRONTEND_URL.get()).path(Property.POS_FRONTEND_PATH.get()).appendPath("plusones").appendPath(s).appendQueryParameter("nolog", "true").appendQueryParameter("max_people", "10").appendQueryParameter("source", "native:android_app").appendQueryParameter("container", PlatformContractUtils.getContainerUrl(apiaryapiinfo)).build().toString(), null, null, new PlatformHttpRequestConfiguration(context, esaccount, "Manage your +1's", Property.POS_BACKEND_URL.get(), apiaryapiinfo), "GET", Plusones.class);
    }

	public final MeetupRequest createPostData()
    {
        return null;
    }

    public final Plusones getPlusones()
    {
        return mPlusones;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        mPlusones = (Plusones)genericjson;
    }

    protected final GenericJson populateRequest()
    {
        return new GenericJson();
    }

}
