/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class BlockingC2DMClient {

	private final CountDownLatch mEvent = new CountDownLatch(1);
    private final RealTimeChatServiceListener mRealTimeChatListener = new OnC2dmReceivedListener();
    private String mRegistrationToken;
    private ServiceResult mServiceResult;
    private final long mTimeoutMilliseconds = 30000L;
    private boolean mUsed;
    
    public BlockingC2DMClient(long l)
    {
    }

    public final void blockingGetC2dmToken(Context context) throws Exception
    {
        if(mUsed)
            throw new IllegalStateException("This class is single-use.");
        try {
	        mUsed = true;
	        RealTimeChatService.registerListener(mRealTimeChatListener);
	        mRegistrationToken = RealTimeChatService.getOrRequestC2dmId(context);
	        if(null != mRegistrationToken) {
	        	mServiceResult = new ServiceResult();
	        } else {
	        	if(!mEvent.await(mTimeoutMilliseconds, TimeUnit.MILLISECONDS))
	            {
	                if(EsLog.isLoggable("BlockingC2DMClient", 6))
	                    Log.e("BlockingC2DMClient", "Waiting for C2DM registration timed out.");
	                mServiceResult = new ServiceResult(-2, "Waiting for C2DM registration timed out.", null);
	            }
	            if(mServiceResult == null)
	            {
	                if(EsLog.isLoggable("BlockingC2DMClient", 5))
	                    Log.w("BlockingC2DMClient", "Result was not set by service.");
	                mServiceResult = new ServiceResult(0, "Result was not set by service.", null);
	            }
	        }
	        
	        RealTimeChatService.unregisterListener(mRealTimeChatListener);
        } catch (InterruptedException interruptedexception) {
        	if(EsLog.isLoggable("BlockingC2DMClient", 6))
                Log.e("BlockingC2DMClient", "Waiting for C2DM registration interrupted.", interruptedexception);
            mServiceResult = new ServiceResult(-1, "Waiting for C2DM registration interrupted.", interruptedexception);
            RealTimeChatService.unregisterListener(mRealTimeChatListener);
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
        	RealTimeChatService.unregisterListener(mRealTimeChatListener);
        	throw exception;
        }
    }

    public final boolean hasError()
    {
        boolean flag;
        if(mRegistrationToken == null || mServiceResult == null || mServiceResult.hasError())
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    private final class OnC2dmReceivedListener extends RealTimeChatServiceListener
    {

        final void onC2dmRegistration(ServiceResult serviceresult, String s)
        {
            mServiceResult = serviceresult;
            mRegistrationToken = s;
            mEvent.countDown();
        }
    }
}
