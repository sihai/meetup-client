/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.http.HttpOperation;

/**
 * 
 * @author sihai
 *
 */
public class ProfileOptimisticPlusOneOperation extends PlusOneOperation {

	public ProfileOptimisticPlusOneOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, boolean flag)
    {
        super(context, esaccount, intent, operationlistener, "ENTITY", s, flag);
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
        EsPeopleData.changePlusOneData(context, esaccount, s, flag);
    }

    protected final void onPopulateRequest()
    {
        EsPeopleData.changePlusOneData(mContext, mAccount, mItemId, mIsPlusOne);
    }
}
