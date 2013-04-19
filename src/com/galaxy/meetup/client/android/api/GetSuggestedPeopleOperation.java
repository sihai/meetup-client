/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.ApiaryBatchOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;

/**
 * 
 * @author sihai
 *
 */
public class GetSuggestedPeopleOperation extends ApiaryBatchOperation {

	public GetSuggestedPeopleOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, esaccount, null, null);
        add(new FindMorePeopleOperation(context, esaccount, null, null));
        add(new GetCelebritySuggestionsOperation(context, esaccount, null, null));
    }
}
