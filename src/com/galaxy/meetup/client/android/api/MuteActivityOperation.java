/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.MuteActivityRequest;
import com.galaxy.meetup.server.client.domain.response.MuteActivityResponse;

/**
 * 
 * @author sihai
 *
 */
public class MuteActivityOperation extends PlusiOperation {

	private final String mActivityId;
    private final boolean mIsMuted;
    
	public MuteActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, boolean flag)
    {
        super(context, esaccount, "muteactivity", intent, operationlistener, MuteActivityResponse.class);
        mActivityId = s;
        mIsMuted = flag;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        EsPostsData.muteActivity(mContext, mAccount, mActivityId, mIsMuted);
    }

    protected final GenericJson populateRequest()
    {
        MuteActivityRequest muteactivityrequest = new MuteActivityRequest();
        muteactivityrequest.activityId = mActivityId;
        muteactivityrequest.muteState = Boolean.valueOf(mIsMuted);
        return muteactivityrequest;
    }

}
