/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataSuggestedPerson;
import com.galaxy.meetup.server.client.domain.request.FindMorePeopleRequest;
import com.galaxy.meetup.server.client.domain.response.FindMorePeopleResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class FindMorePeopleOperation extends PlusiOperation {

	public FindMorePeopleOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, esaccount, "findmorepeople", null, null, FindMorePeopleResponse.class);
    }

    protected final void handleResponse(Response response) throws IOException {
        FindMorePeopleResponse findmorepeopleresponse = (FindMorePeopleResponse)response;
        List arraylist = new ArrayList();
        if(findmorepeopleresponse.suggestion != null)
            arraylist.addAll(findmorepeopleresponse.suggestion);
        Iterator iterator = arraylist.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            DataSuggestedPerson datasuggestedperson = (DataSuggestedPerson)iterator.next();
            if(datasuggestedperson.member == null || datasuggestedperson.member.memberProperties == null || datasuggestedperson.member.memberProperties.esUser == null || !datasuggestedperson.member.memberProperties.esUser.booleanValue())
                iterator.remove();
            else
            if(datasuggestedperson.score == null || datasuggestedperson.score.doubleValue() < 0.025000000000000001D)
                iterator.remove();
        } while(true);
        EsPeopleData.insertSuggestedPeople(mContext, getAccount(), arraylist);
    }

    protected final Request populateRequest() {
    	FindMorePeopleRequest response = new FindMorePeopleRequest();
        response.maxSuggestions = Integer.valueOf(80);
        return response;
    }

}
