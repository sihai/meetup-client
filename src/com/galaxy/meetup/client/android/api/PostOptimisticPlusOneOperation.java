/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.server.client.domain.DataPlusOne;

/**
 * 
 * @author sihai
 *
 */
public class PostOptimisticPlusOneOperation extends PlusOneOperation {

	public PostOptimisticPlusOneOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, boolean flag)
    {
        super(context, esaccount, intent, operationlistener, "TACO", s, flag);
    }

    protected final void onFailure()
    {
        Context context = mContext;
        EsAccount esaccount = mAccount;
        String s = mItemId;
        boolean flag;
        if(!mIsPlusOne)
            flag = true;
        else
            flag = false;
        EsPostsData.plusOnePost(context, esaccount, s, flag);
        if(mIsPlusOne)
            EsService.updateNotifications(mContext, mAccount);
    }

    protected final void onPopulateRequest()
    {
        EsPostsData.plusOnePost(mContext, mAccount, mItemId, mIsPlusOne);
        if(mIsPlusOne)
            EsService.updateNotifications(mContext, mAccount);
    }

    protected final void onSuccess(DataPlusOne dataplusone)
    {
        if(dataplusone != null)
            EsPostsData.updatePostPlusOneId(mContext, mAccount, mItemId, dataplusone.id);
    }
}
