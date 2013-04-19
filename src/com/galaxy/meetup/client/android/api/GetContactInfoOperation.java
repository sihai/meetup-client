/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataCircleMemberId;
import com.galaxy.meetup.server.client.domain.DataCirclePerson;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.LoadPeopleRequest;
import com.galaxy.meetup.server.client.domain.response.LoadCircleMembersResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetContactInfoOperation extends PlusiOperation {

	private final String mGaiaId;
    private DataCirclePerson mPerson;
    
	public GetContactInfoOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "loadpeople", null, operationlistener, LoadCircleMembersResponse.class);
        mGaiaId = s;
    }

    public final DataCirclePerson getPerson()
    {
        return mPerson;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        LoadCircleMembersResponse loadcirclemembersresponse = (LoadCircleMembersResponse)genericjson;
        if(loadcirclemembersresponse.circlePerson != null && loadcirclemembersresponse.circlePerson.size() > 0)
            mPerson = (DataCirclePerson)loadcirclemembersresponse.circlePerson.get(0);
    }

    protected final GenericJson populateRequest()
    {
        LoadPeopleRequest loadpeoplerequest = new LoadPeopleRequest();
        loadpeoplerequest.circleMemberId = new ArrayList();
        DataCircleMemberId datacirclememberid = new DataCircleMemberId();
        datacirclememberid.obfuscatedGaiaId = mGaiaId;
        loadpeoplerequest.circleMemberId.add(datacirclememberid);
        loadpeoplerequest.includeIsFollowing = Boolean.valueOf(false);
        loadpeoplerequest.includeMemberships = Boolean.valueOf(false);
        return loadpeoplerequest;
    }

}
