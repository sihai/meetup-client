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
import com.galaxy.meetup.server.client.domain.DataPlusOne;
import com.galaxy.meetup.server.client.domain.request.PlusOneRequest;
import com.galaxy.meetup.server.client.domain.response.PlusOneResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class PlusOneOperation extends PlusiOperation {

	protected final boolean mIsPlusOne;
    protected final String mItemId;
    protected final String mItemType;
    
	public PlusOneOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, boolean flag)
    {
        super(context, esaccount, "plusone", intent, operationlistener, PlusOneResponse.class);
        mItemType = s;
        mItemId = s1;
        mIsPlusOne = flag;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        PlusOneResponse plusoneresponse = (PlusOneResponse)response;
        if(plusoneresponse.success.booleanValue())
            onSuccess(plusoneresponse.plusOne);
        else
            onFailure();
    }

    protected void onFailure()
    {
    }

    public final void onHttpOperationComplete(int i, String s, Exception exception)
    {
        if(i != 200 || exception != null)
            onFailure();
        super.onHttpOperationComplete(i, s, exception);
    }

    protected void onPopulateRequest()
    {
    }

    protected void onSuccess(DataPlusOne dataplusone)
    {
    }

    protected final Request populateRequest()
    {
        PlusOneRequest plusonerequest = new PlusOneRequest();
        onPopulateRequest();
        plusonerequest.type = mItemType;
        plusonerequest.itemId = mItemId;
        plusonerequest.isPlusone = Boolean.valueOf(mIsPlusOne);
        return plusonerequest;
    }

}
