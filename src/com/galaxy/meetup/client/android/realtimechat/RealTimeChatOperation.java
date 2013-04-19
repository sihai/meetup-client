/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.Collection;

import android.content.Context;

import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class RealTimeChatOperation {

	protected final EsAccount mAccount;
    protected final Context mContext;
    protected final RealTimeChatOperationState mOperationState = new RealTimeChatOperationState(null);
    
	public RealTimeChatOperation(Context context, EsAccount esaccount)
    {
        mContext = context;
        mAccount = esaccount;
    }

    public void execute()
    {
    }

    public final Collection getResponses()
    {
        return mOperationState.getRequests();
    }

    public int getResultCode()
    {
        return 1;
    }

    public Object getResultValue()
    {
        return null;
    }

}
