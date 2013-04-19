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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.ModifyCirclePropertiesRequest;
import com.galaxy.meetup.server.client.domain.response.ModifyCirclePropertiesResponse;

/**
 * 
 * @author sihai
 *
 */
public class ModifyCirclePropertiesOperation extends PlusiOperation {

	private final String mCircleId;
    private final String mCircleName;
    private final boolean mJustFollowing;
    
	public ModifyCirclePropertiesOperation(Context context, EsAccount esaccount, String s, String s1, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "modifycircleproperties", intent, operationlistener, ModifyCirclePropertiesResponse.class);
        mCircleId = s;
        mCircleName = s1;
        mJustFollowing = flag;
    }

    protected final void handleResponse(GenericJson genericjson)
        throws IOException
    {
        EsPeopleData.modifyCircleProperties(mContext, mAccount, mCircleId, mCircleName, mJustFollowing);
    }

    protected final GenericJson populateRequest()
    {
        ModifyCirclePropertiesRequest modifycirclepropertiesrequest = new ModifyCirclePropertiesRequest();
        if(mCircleId.startsWith("f."))
            modifycirclepropertiesrequest.circleId = mCircleId.substring(2);
        else
            modifycirclepropertiesrequest.circleId = mCircleId;
        modifycirclepropertiesrequest.name = mCircleName;
        modifycirclepropertiesrequest.justFollowing = Boolean.valueOf(mJustFollowing);
        return modifycirclepropertiesrequest;
    }

}
