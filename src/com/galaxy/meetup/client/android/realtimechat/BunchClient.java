/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import com.galaxy.meetup.client.android.Version;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.android.xmpp.GoogleTalkClient;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class BunchClient extends GoogleTalkClient {

	private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Version.ClientVersion mClientVersion;
    private boolean mConnected;
    private android.os.Handler.Callback mHandlerCallback;
    private BunchClientListener mListener;
    private final PendingRequestList mPendingRequestList = new PendingRequestList();
    private final Collection mQueuedCommands = new LinkedList();
    
    public BunchClient(EsAccount esaccount, Context context, String s, String s1, BunchClientListener bunchclientlistener)
    {
        super(esaccount, context, s, s1, "bunch");
        mHandlerCallback = new android.os.Handler.Callback() {

            public final boolean handleMessage(Message message)
            {
            	if(100 == message.what) {
            		checkResponseReceived((Client.BunchClientRequest)message.obj);
            	} else if(101 == message.what) {
            		BunchClient.access$000(BunchClient.this, (PendingRequest)message.obj);
            	}
            	
            	return true;
            }
        };
        mConnected = false;
        mListener = bunchclientlistener;
    }
    
	@Override
	protected void onConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDisconnected(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessageReceived(byte[] abyte0) {
		// TODO Auto-generated method stub
		
	}
	
	public final void checkResponseReceived(Client.BunchClientRequest bunchclientrequest) {
		
	}

	private static String getRequestTypeName(Client.BunchClientRequest bunchclientrequest)
    {
        String s;
        if(bunchclientrequest.hasChatMessageRequest())
            s = "ChatMessageRequest";
        else
        if(bunchclientrequest.hasConversationJoinRequest())
            s = "ConversationJoinRequest";
        else
        if(bunchclientrequest.hasConversationListRequest())
            s = "ConversationListRequest";
        else
        if(bunchclientrequest.hasConversationPreferenceRequest())
            s = "ConversationPreferenceRequest";
        else
        if(bunchclientrequest.hasConversationRenameRequest())
            s = "ConversationRenameRequest";
        else
        if(bunchclientrequest.hasConversationRequest())
            s = "ConversationRequest";
        else
        if(bunchclientrequest.hasConversationSearchRequest())
            s = "ConversationSearchRequest";
        else
        if(bunchclientrequest.hasDeviceRegistrationRequest())
            s = "DeviceRegistrationRequest";
        else
        if(bunchclientrequest.hasEventSearchRequest())
            s = "EventSearchRequest";
        else
        if(bunchclientrequest.hasEventStreamRequest())
            s = "EventStreamRequest";
        else
        if(bunchclientrequest.hasGlobalConversationPreferencesRequest())
            s = "GlobalConversationPreferencesRequest";
        else
        if(bunchclientrequest.hasHangoutInviteFinishRequest())
            s = "HangoutInviteFinishRequest";
        else
        if(bunchclientrequest.hasHangoutInviteKeepAliveRequest())
            s = "HangoutInviteKeepAliveRequest";
        else
        if(bunchclientrequest.hasHangoutInviteReplyRequest())
            s = "HangoutInviteReplyRequest";
        else
        if(bunchclientrequest.hasHangoutRingFinishRequest())
            s = "HangoutRingFinishRequest";
        else
        if(bunchclientrequest.hasInviteRequest())
            s = "InviteRequest";
        else
        if(bunchclientrequest.hasLeaveConversationRequest())
            s = "LeaveConversationRequest";
        else
        if(bunchclientrequest.hasPingRequest())
            s = "PingRequest";
        else
        if(bunchclientrequest.hasPresenceRequest())
            s = "PresenceRequest";
        else
        if(bunchclientrequest.hasReceiptRequest())
            s = "ReceiptRequest";
        else
        if(bunchclientrequest.hasReplyToInviteRequest())
            s = "ReplyToInviteRequest";
        else
        if(bunchclientrequest.hasSetAclsRequest())
            s = "SetAclsRequest";
        else
        if(bunchclientrequest.hasSuggestionsRequest())
            s = "SuggestionsRequest";
        else
        if(bunchclientrequest.hasTileEventRequest())
            s = "TileEventRequest";
        else
        if(bunchclientrequest.hasTypingRequest())
            s = "TypingRequest";
        else
        if(bunchclientrequest.hasUserCreationRequest())
            s = "UserCreationRequest";
        else
        if(bunchclientrequest.hasUserInfoRequest())
            s = "UserInfoRequest";
        else
            s = "Unknown";
        return s;
    }
	
	private static boolean expectResponse(Client.BunchClientRequest bunchclientrequest)
    {
        boolean flag;
        if(bunchclientrequest.hasReceiptRequest())
            flag = false;
        else
            flag = true;
        return flag;
    }
	
	private Client.BatchCommand.Builder createBatchCommandBuilderWithClientVersion()
    {
        return Client.BatchCommand.newBuilder().setClientVersionMessage(mClientVersion);
    }
	
	private static boolean retryOnTimeout(Client.BunchClientRequest bunchclientrequest)
    {
        boolean flag;
        if(bunchclientrequest.hasUserCreationRequest() || bunchclientrequest.hasConversationListRequest() || bunchclientrequest.hasEventStreamRequest())
            flag = true;
        else
            flag = false;
        return flag;
    }
	
	private boolean shouldEnqueueIfDisconnected(Client.BunchClientRequest bunchclientrequest)
    {
		// TODO
		return false;
    }
	
	static void access$000(BunchClient bunchclient, PendingRequest pendingrequest)
    {
        if(EsLog.isLoggable("BunchClient", 3))
            Log.d("BunchClient", (new StringBuilder("retrySendRequest ")).append(pendingrequest.mRequestId).toString());
        Client.BunchClientRequest bunchclientrequest = pendingrequest.mRequest;
        synchronized(bunchclient) {
        	if(!expectResponse(bunchclientrequest)) {
        		if(EsLog.isLoggable("BunchClient", 4)) {
        			Log.i("BunchClient", (new StringBuilder("Sending command ")).append(getRequestTypeName(bunchclientrequest)).append(" [").append(bunchclientrequest.getRequestClientId()).append("] not expecting response").toString());
        		}
        	} else { 
        		if(EsLog.isLoggable("BunchClient", 4))
                    Log.i("BunchClient", (new StringBuilder("Sending command ")).append(getRequestTypeName(bunchclientrequest)).append(" [").append(bunchclientrequest.getRequestClientId()).append("] expecting response").toString());
                pendingrequest.mTimestamp = SystemClock.elapsedRealtime();
                pendingrequest.mMetrics = new HttpTransactionMetrics();
                pendingrequest.mMetrics.onBeginTransaction((new StringBuilder("RealTimeChat:")).append(getRequestTypeName(bunchclientrequest)).toString());
                bunchclient.mPendingRequestList.addRequest(bunchclientrequest.getRequestClientId(), pendingrequest);
                if(retryOnTimeout(bunchclientrequest) && bunchclient.mBackgroundHandler != null)
                {
                    long l = 15000L << pendingrequest.mRetryCount;
                    android.os.Message message = bunchclient.mBackgroundHandler.obtainMessage(100, bunchclientrequest);
                    bunchclient.mBackgroundHandler.sendMessageDelayed(message, l);
                    if(EsLog.isLoggable("BunchClient", 5))
                        Log.w("BunchClient", (new StringBuilder("Bunch request timeout ")).append(pendingrequest.mRequest.getRequestClientId()).append(" checking in ").append(l).toString());
                }
        	}
        }
        
        boolean flag = bunchclient.mConnected;
        boolean flag1 = false;
        if(flag)
            flag1 = bunchclient.sendMessage(bunchclient.createBatchCommandBuilderWithClientVersion().addRequest(bunchclientrequest).build().toByteArray());
        Exception exception;
        if(!flag1)
        {
            if(bunchclient.shouldEnqueueIfDisconnected(bunchclientrequest))
            {
                if(EsLog.isLoggable("BunchClient", 3))
                    Log.d("BunchClient", "queueing");
                bunchclient.mQueuedCommands.add(new Pair(Long.valueOf(SystemClock.elapsedRealtime()), bunchclientrequest));
            }
        } else
        if(EsLog.isLoggable("BunchClient", 3))
            Log.d("BunchClient", "sent");
    }
	
	private class PendingRequest {
		
        HttpTransactionMetrics mMetrics;
        public Client.BunchClientRequest mRequest;
        public int mRequestId;
        public int mRetryCount;
        public long mTimestamp;

        PendingRequest(int i, Client.BunchClientRequest bunchclientrequest, long l, int j)
        {
            super();
            mRequestId = i;
            mRequest = bunchclientrequest;
            mTimestamp = l;
            mRetryCount = j;
        }
    }

    private class PendingRequestList implements Iterable {

    	private final HashMap mRequestData = new HashMap();
        private final LinkedList mRequestList = new LinkedList();

        PendingRequestList()
        {
        }
        
        public final void addRequest(String s, PendingRequest pendingrequest)
        {
            mRequestList.addLast(s);
            mRequestData.put(s, pendingrequest);
        }

        public final void clear()
        {
            mRequestList.clear();
            mRequestData.clear();
        }

        public final void dump()
        {
            if(EsLog.isLoggable("BunchClient", 3))
            {
                Log.d("BunchClient", "mRequestList");
                String s1;
                for(Iterator iterator1 = mRequestList.iterator(); iterator1.hasNext(); Log.d("BunchClient", (new StringBuilder("  requestId ")).append(s1).toString()))
                    s1 = (String)iterator1.next();

                Log.d("BunchClient", "mRequestData");
                String s;
                PendingRequest pendingrequest;
                for(Iterator iterator2 = mRequestData.keySet().iterator(); iterator2.hasNext(); Log.d("BunchClient", (new StringBuilder("  requestId ")).append(s).append(" ").append(BunchClient.getRequestTypeName(pendingrequest.mRequest)).toString()))
                {
                    s = (String)iterator2.next();
                    pendingrequest = (PendingRequest)mRequestData.get(s);
                }

            }
        }

        public final PendingRequest getData(String s)
        {
            return (PendingRequest)mRequestData.get(s);
        }

        public final List getRequestIds()
        {
            LinkedList linkedlist = new LinkedList();
            for(Iterator iterator1 = mRequestList.iterator(); iterator1.hasNext(); linkedlist.add((String)iterator1.next()));
            return linkedlist;
        }

        public final boolean isEmpty()
        {
            return mRequestData.isEmpty();
        }

        public final Iterator iterator()
        {
            return mRequestData.values().iterator();
        }

        public final void removeRequest(String s)
        {
            mRequestData.remove(s);
        }

        public final List trimOutdatedRequestIds(long l)
        {
            boolean flag = false;
            LinkedList linkedlist = new LinkedList();
            while(!flag && !mRequestList.isEmpty()) 
            {
                String s = (String)mRequestList.getFirst();
                PendingRequest pendingrequest = (PendingRequest)mRequestData.get(s);
                if(pendingrequest != null)
                {
                    Long long1 = Long.valueOf(pendingrequest.mTimestamp);
                    if(long1 == null || long1.longValue() < l)
                    {
                        linkedlist.add(pendingrequest);
                        mRequestData.remove(s);
                        mRequestList.removeFirst();
                    } else
                    {
                        flag = true;
                    }
                } else
                {
                    mRequestList.removeFirst();
                }
            }
            return linkedlist;
        }
    }

    public static interface BunchClientListener
    {

        public abstract void onConnected(BunchClient bunchclient);

        public abstract void onDisconnected(BunchClient bunchclient, int i);

        public abstract void onPingReceived(BunchClient bunchclient);

        public abstract void onResultsReceived(BunchClient bunchclient, List list);
    }

    private final class ResponseFailedException extends Exception
    {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = -3794790249531577515L;
		
		Data.ResponseStatus mStatus;

        ResponseFailedException(Data.ResponseStatus responsestatus)
        {
            super();
            mStatus = responsestatus;
        }
    	
        public final String toString()
        {
            return (new StringBuilder()).append(super.toString()).append(" ").append(mStatus).toString();
        }

    }

    private final class TimedOutException extends Exception
    {

        /**
		 * 
		 */
		private static final long serialVersionUID = -2408097733815066444L;

		TimedOutException()
        {
            super();
        }
    }
}
