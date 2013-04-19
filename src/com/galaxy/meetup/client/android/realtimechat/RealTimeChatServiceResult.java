/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

/**
 * 
 * @author sihai
 *
 */
public class RealTimeChatServiceResult {

	private final Client.BunchServerResponse mCommand;
    private final int mErrorCode;
    private int mRequestId;

	RealTimeChatServiceResult()
    {
        this(0, 1, null);
    }

    RealTimeChatServiceResult(int i, int j, Client.BunchServerResponse bunchserverresponse)
    {
        mRequestId = i;
        mErrorCode = j;
        mCommand = bunchserverresponse;
    }

    public final Client.BunchServerResponse getCommand()
    {
        return mCommand;
    }

    public final int getErrorCode()
    {
        return mErrorCode;
    }

    public final int getRequestId()
    {
        return mRequestId;
    }

}