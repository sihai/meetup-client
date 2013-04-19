/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public class RealTimeChatOperationState {

	private int mClientVersion;
    private boolean mClientVersionChanged;
    private final Long mCurrentConversationRowId;
    private final List mRequests = new LinkedList();
    private boolean mShouldTriggerNotification;
    
	public RealTimeChatOperationState(Long long1)
    {
        mCurrentConversationRowId = long1;
        mShouldTriggerNotification = false;
        mClientVersionChanged = false;
        mClientVersion = 0;
    }

    public final void addRequest(Client.BunchClientRequest bunchclientrequest)
    {
        mRequests.add(bunchclientrequest);
    }

    public final boolean getClientVersionChanged()
    {
        return mClientVersionChanged;
    }

    public final Long getCurrentConversationRowId()
    {
        return mCurrentConversationRowId;
    }

    public final List getRequests()
    {
        return mRequests;
    }

    public final void setClientVersion(int i)
    {
        mClientVersion = i;
        mClientVersionChanged = true;
    }

    public final void setShouldTriggerNotifications()
    {
        mShouldTriggerNotification = true;
    }

    public final boolean shouldTriggerNotifications()
    {
        return mShouldTriggerNotification;
    }

}
