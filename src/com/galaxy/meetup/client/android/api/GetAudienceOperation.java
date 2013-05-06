/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsDatabaseHelper;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.Person;
import com.galaxy.meetup.server.client.domain.request.GetAudienceRequest;
import com.galaxy.meetup.server.client.domain.response.GetAudienceResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class GetAudienceOperation extends PlusiOperation {

	private final String mActivityId;
    private AudienceData mAudienceData;
    
    public GetAudienceOperation(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getaudience", intent, operationlistener, GetAudienceResponse.class);
        mActivityId = s;
    }

    public final AudienceData getAudience()
    {
        return mAudienceData;
    }

    protected final void handleResponse(Response response) throws IOException {
    	
        int i;
        int j;
        GetAudienceResponse getaudienceresponse = (GetAudienceResponse)response;
        Person person;
        if(getaudienceresponse.gaiaAudienceCount == null)
            i = 0;
        else
            i = getaudienceresponse.gaiaAudienceCount.intValue();
        if(getaudienceresponse.nonGaiaAudienceCount == null)
            j = 0;
        else
            j = getaudienceresponse.nonGaiaAudienceCount.intValue();
        ArrayList arraylist = new ArrayList();
        if(null != getaudienceresponse.person) {
        	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(mContext, mAccount).getWritableDatabase();
        	try {
	            sqlitedatabase.beginTransaction();
	            for(Iterator iterator = getaudienceresponse.person.iterator(); iterator.hasNext(); EsPeopleData.replaceUserInTransaction(sqlitedatabase, person.obfuscatedId, person.userName, person.photoUrl))
	            {
	                person = (Person)iterator.next();
	                arraylist.add(new PersonData(person.obfuscatedId, person.userName, null, EsAvatarData.compressAvatarUrl(person.photoUrl)));
	            }
	            sqlitedatabase.setTransactionSuccessful();
        	} finally {
        		 sqlitedatabase.endTransaction();
        	}
        }
           
        mAudienceData = new AudienceData(arraylist, null, i + j);
        return;
    }

    protected final Request populateRequest()
    {
        GetAudienceRequest getaudiencerequest = new GetAudienceRequest();
        getaudiencerequest.updateId = mActivityId;
        getaudiencerequest.returnFullProfile = Boolean.valueOf(true);
        return getaudiencerequest;
    }
}
