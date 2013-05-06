/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.PeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.MuteUserRequest;
import com.galaxy.meetup.server.client.domain.response.MuteUserResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class MuteUserOperation extends PlusiOperation {

	private static Factory sFactory = new Factory();
    private final PeopleData mDb;
    private String mGaiaId;
    private boolean mIsMute;
    
    public MuteUserOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, PeopleData peopledata)
    {
        super(context, esaccount, "muteuser", intent, operationlistener, MuteUserResponse.class);
        mDb = peopledata;
    }

    public static Factory getFactory()
    {
        return sFactory;
    }

    protected final void handleResponse(Response response)
        throws IOException
    {
        MuteUserResponse muteuserresponse = (MuteUserResponse)response;
        mDb.setMuteState(mGaiaId, muteuserresponse.isMuted.booleanValue());
    }

    protected final Request populateRequest()
    {
        MuteUserRequest muteuserrequest = new MuteUserRequest();
        muteuserrequest.obfuscatedGaiaId = mGaiaId;
        muteuserrequest.shouldMute = Boolean.valueOf(mIsMute);
        return muteuserrequest;
    }

    public final void startThreaded(String s, boolean flag)
    {
        mGaiaId = s;
        mIsMute = flag;
        startThreaded();
    }
    
    public static final class Factory
    {

        public static MuteUserOperation build(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, PeopleData peopledata)
        {
            return new MuteUserOperation(context, esaccount, intent, operationlistener, peopledata);
        }

    }

}
