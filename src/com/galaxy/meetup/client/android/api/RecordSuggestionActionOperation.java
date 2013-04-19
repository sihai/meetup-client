/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataCircleMemberId;
import com.galaxy.meetup.server.client.domain.DataSuggestedEntityId;
import com.galaxy.meetup.server.client.domain.DataSuggestionAction;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.RecordSuggestionRequest;
import com.galaxy.meetup.server.client.domain.response.RecordSuggestionResponse;

/**
 * 
 * @author sihai
 *
 */
public class RecordSuggestionActionOperation extends PlusiOperation {

	private final String mActionType;
    private final List mPersonIds;
    private final List mSuggestionIds;
    private final String mSuggestionsUi;
    
    public RecordSuggestionActionOperation(Context context, EsAccount esaccount, String s, List list, List list1, String s1, Intent intent, 
            HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "recordsuggestion", null, null, RecordSuggestionResponse.class);
        mSuggestionsUi = s;
        mPersonIds = list;
        mSuggestionIds = list1;
        mActionType = s1;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
    }

    protected final GenericJson populateRequest()
    {
        RecordSuggestionRequest recordsuggestionrequest = new RecordSuggestionRequest();
        recordsuggestionrequest.suggestionAction = new DataSuggestionAction();
        recordsuggestionrequest.suggestionAction.accepted = Boolean.valueOf(false);
        recordsuggestionrequest.suggestionAction.actionType = mActionType;
        recordsuggestionrequest.suggestionAction.suggestionUi = mSuggestionsUi;
        recordsuggestionrequest.suggestionAction.suggestedEntityId = new ArrayList();
        recordsuggestionrequest.suggestionAction.suggestionId = new ArrayList();
        if(mPersonIds != null && mSuggestionIds != null && mPersonIds.size() == mSuggestionIds.size())
        {
            int i = 0;
            while(i < mPersonIds.size()) 
            {
                String s = (String)mPersonIds.get(i);
                String s1 = (String)mSuggestionIds.get(i);
                DataSuggestedEntityId datasuggestedentityid = new DataSuggestedEntityId();
                DataCircleMemberId datacirclememberid = new DataCircleMemberId();
                String s2 = EsPeopleData.extractGaiaId(s);
                datasuggestedentityid.suggestionId = s1;
                if(s2 != null)
                {
                    datasuggestedentityid.obfuscatedGaiaId = s2;
                    datacirclememberid.obfuscatedGaiaId = s2;
                    recordsuggestionrequest.suggestionAction.suggestedEntityId.add(datasuggestedentityid);
                    recordsuggestionrequest.suggestionAction.suggestionId.add(datacirclememberid);
                } else
                if(s.startsWith("e:"))
                {
                    datasuggestedentityid.email = s.substring(2);
                    datacirclememberid.email = s.substring(2);
                    recordsuggestionrequest.suggestionAction.suggestedEntityId.add(datasuggestedentityid);
                    recordsuggestionrequest.suggestionAction.suggestionId.add(datacirclememberid);
                }
                i++;
            }
        }
        return recordsuggestionrequest;
    }
}
