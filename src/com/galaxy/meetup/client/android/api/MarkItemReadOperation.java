/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.request.MarkItemReadRequest;
import com.galaxy.meetup.server.client.domain.response.MarkItemReadResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class MarkItemReadOperation extends PlusiOperation {

	private final boolean mIsNotificationType;
    private final List mItemIds;
    
    public MarkItemReadOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, List list, boolean flag)
    {
        super(context, esaccount, "markitemread", intent, operationlistener, MarkItemReadResponse.class);
        mItemIds = list;
        mIsNotificationType = flag;
    }

    public final List getItemIds()
    {
        return mItemIds;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        if(!mIsNotificationType)
            EsPostsData.markActivitiesAsRead(mContext, mAccount, mItemIds);
    }

    public final boolean isNotificationType()
    {
        return mIsNotificationType;
    }

    protected final Request populateRequest()
    {
        MarkItemReadRequest markitemreadrequest = new MarkItemReadRequest();
        if(mIsNotificationType)
            markitemreadrequest.networkType = "4";
        else
            markitemreadrequest.networkType = "3";
        markitemreadrequest.itemIds = mItemIds;
        return markitemreadrequest;
    }
}
