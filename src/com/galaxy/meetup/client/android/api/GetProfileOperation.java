/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.request.GetSimpleProfileRequest;
import com.galaxy.meetup.server.client.domain.response.GetSimpleProfileResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetProfileOperation extends PlusiOperation {

	private final boolean mInsertInDatabase;
    private final String mOwnerId;
    private SimpleProfile mProfile;
    
	public GetProfileOperation(Context context, EsAccount esaccount, String s, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getsimpleprofile", null, null, GetSimpleProfileResponse.class);
        mOwnerId = s;
        mInsertInDatabase = flag;
    }

    public final SimpleProfile getProfile()
    {
        return mProfile;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        mProfile = ((GetSimpleProfileResponse)response).profile;
        if(mInsertInDatabase)
            EsPeopleData.insertProfile(mContext, mAccount, mOwnerId, mProfile);
    }

    protected final Request populateRequest()
    {
        GetSimpleProfileRequest getsimpleprofilerequest = new GetSimpleProfileRequest();
        getsimpleprofilerequest.ownerId = mOwnerId;
        getsimpleprofilerequest.useCachedDataForCircles = Boolean.valueOf(false);
        getsimpleprofilerequest.includeAclData = Boolean.valueOf(mAccount.isMyGaiaId(mOwnerId));
        return getsimpleprofilerequest;
    }

}
