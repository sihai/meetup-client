/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.SimpleProfile;
import com.galaxy.meetup.server.client.domain.request.MutateProfileRequest;
import com.galaxy.meetup.server.client.domain.response.MutateProfileResponse;

/**
 * 
 * @author sihai
 *
 */
public class MutateProfileOperation extends PlusiOperation {

	private static Map sErrorCodeMap;
    private SimpleProfile mProfileUpdates;
    
    static {
    	sErrorCodeMap = new HashMap(16);
    	sErrorCodeMap.put("UNKNOWN", Integer.valueOf(0));
        sErrorCodeMap.put("DATA_TOO_LARGE", Integer.valueOf(1));
        sErrorCodeMap.put("INVALID_DATE", Integer.valueOf(2));
        sErrorCodeMap.put("NAME_VIOLATION", Integer.valueOf(3));
        sErrorCodeMap.put("INVALID_PUBLIC_USERNAME", Integer.valueOf(4));
        sErrorCodeMap.put("NAME_CHANGE_THROTTLED", Integer.valueOf(5));
        sErrorCodeMap.put("INVALID_CHAR", Integer.valueOf(6));
        sErrorCodeMap.put("INCLUDES_NICKNAME", Integer.valueOf(7));
        sErrorCodeMap.put("HARD_NAME_VIOLATION", Integer.valueOf(8));
        sErrorCodeMap.put("HARD_INVALID_CHAR", Integer.valueOf(9));
        sErrorCodeMap.put("HARD_INCLUDES_NICKNAME", Integer.valueOf(10));
        sErrorCodeMap.put("HARD_INVALID_NICKNAME", Integer.valueOf(11));
        sErrorCodeMap.put("TAGLINE_HARD_INVALID_CHAR", Integer.valueOf(12));
        sErrorCodeMap.put("INVALID_NICKNAME", Integer.valueOf(13));
        sErrorCodeMap.put("INVALID_WEBSITE", Integer.valueOf(14));
        sErrorCodeMap.put("INVALID_BIRTHDAY", Integer.valueOf(15));
    }
    
	public static final class MutateProfileException extends ProtocolException
    {

        public MutateProfileException(MutateProfileResponse mutateprofileresponse)
        {
            super(PrimitiveUtils.safeInt((Integer)sErrorCodeMap.get(mutateprofileresponse.errorCode)), mutateprofileresponse.errorMessage);
        }
    }


    public MutateProfileOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, SimpleProfile simpleprofile)
    {
        super(context, esaccount, "mutateprofile", intent, operationlistener, MutateProfileResponse.class);
        mProfileUpdates = simpleprofile;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        MutateProfileResponse mutateprofileresponse = (MutateProfileResponse)genericjson;
        SimpleProfile simpleprofile = mutateprofileresponse.updatedProfile;
        if(simpleprofile == null)
        {
            throw new MutateProfileException(mutateprofileresponse);
        } else
        {
            EsPeopleData.insertProfile(mContext, mAccount, simpleprofile.obfuscatedGaiaId, simpleprofile);
            return;
        }
    }

    protected final GenericJson populateRequest()
    {
    	MutateProfileRequest genericjson = new MutateProfileRequest();
        genericjson.profile = mProfileUpdates;
        return genericjson;
    }
}
