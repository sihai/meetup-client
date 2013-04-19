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
import com.galaxy.meetup.server.client.domain.MobilePreference;
import com.galaxy.meetup.server.client.domain.request.SetMobileSettingsRequest;
import com.galaxy.meetup.server.client.domain.response.SetMobileSettingsResponse;

/**
 * 
 * @author sihai
 *
 */
public class SetSettingsOperation extends PlusiOperation {

	final long mWarmWelcomeTimestamp;
	
	public SetSettingsOperation(Context context, EsAccount esaccount, long l, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "setmobilesettings", null, null, SetMobileSettingsResponse.class);
        mWarmWelcomeTimestamp = l;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
    }

    protected final GenericJson populateRequest()
    {
        SetMobileSettingsRequest setmobilesettingsrequest = new SetMobileSettingsRequest();
        MobilePreference mobilepreference = new MobilePreference();
        mobilepreference.wwMainFlowAckTimestampMsec = Long.valueOf(mWarmWelcomeTimestamp);
        setmobilesettingsrequest.preference = mobilepreference;
        return setmobilesettingsrequest;
    }
}
