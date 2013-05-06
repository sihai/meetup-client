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
import com.galaxy.meetup.server.client.domain.request.LoadPeopleRequest;
import com.galaxy.meetup.server.client.domain.response.LoadCircleMembersResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class ResolveVanityIdOperation extends PlusiOperation {

	private DataCirclePerson mPerson;
    private final String mVanityId;
    
    public ResolveVanityIdOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "loadpeople", null, null, LoadCircleMembersResponse.class);
        mVanityId = s;
    }

    public final DataCirclePerson getPerson()
    {
        return mPerson;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        LoadCircleMembersResponse loadcirclemembersresponse = (LoadCircleMembersResponse)response;
        if(loadcirclemembersresponse.circlePerson != null && loadcirclemembersresponse.circlePerson.size() > 0)
            mPerson = (DataCirclePerson)loadcirclemembersresponse.circlePerson.get(0);
    }

    protected final Request populateRequest()
    {
        LoadPeopleRequest loadpeoplerequest = new LoadPeopleRequest();
        loadpeoplerequest.circleMemberId = new ArrayList();
        DataCircleMemberId datacirclememberid = new DataCircleMemberId();
        datacirclememberid.url = (new StringBuilder("https://plus.google.com/")).append(mVanityId).toString();
        loadpeoplerequest.circleMemberId.add(datacirclememberid);
        loadpeoplerequest.includeIsFollowing = Boolean.valueOf(false);
        loadpeoplerequest.includeMemberships = Boolean.valueOf(false);
        return loadpeoplerequest;
    }
}
