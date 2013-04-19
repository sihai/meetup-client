/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.galaxy.meetup.client.android.c2dm.C2DMReceiver;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.ServiceThread;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class RealTimeChatService extends Service {

	private static boolean sConversationsLoaded = false;
    private static Long sCurrentConversationRowId = null;
    private static Integer sLastRequestId = Integer.valueOf(0);
    private static final List sListeners = new ArrayList();
    private static final PendingRequestList sPendingRequests = new PendingRequestList();
    private static final Map sResults = new ResultsLinkedHashMap();
    private static android.os.PowerManager.WakeLock sWakeLock;
    private BunchClient mBunchClient;
    private final ConnectRunnable mConnectRunnable = new ConnectRunnable();
    private int mConnectionRequestCount;
    private Handler mHandler;
    private long mLastConnectAttemptTime;
    private long mLastConnectRequestTimestamp;
    private long mLastMessageTime;
    private long mLastResponseTime;
    private PendingIntent mLongTermConnect;
    private boolean mNeedsSync;
    private final Runnable mPingRunnable = new Runnable() {

        public final void run()
        {
            Intent intent = new Intent(RealTimeChatService.this, RealTimeChatService.class);
            intent.putExtra("op", 221);
            startService(intent);
        }
    };
    private int mReconnectCount;
    private long mReconnectDelay;
    private ServiceThread mServiceThread;
    private final Runnable mStopRunnable = new Runnable() {

        public final void run()
        {
            RealTimeChatService.initWakeLock(RealTimeChatService.this);
            if(RealTimeChatService.sWakeLock.isHeld())
            {
                if(EsLog.isLoggable("RealTimeChatService", 3))
                    Log.d("RealTimeChatService", "release wake lock");
                RealTimeChatService.sWakeLock.release();
            }
            if(RealTimeChatService.sPendingRequests.isEmpty())
            {
                if(EsLog.isLoggable("RealTimeChatService", 3))
                    Log.d("RealTimeChatService", "Stop runnable: Stopping service");
                stopSelf();
            } else
            {
                if(EsLog.isLoggable("RealTimeChatService", 3))
                    Log.d("RealTimeChatService", "Stop runnable: Not stopping, things to do");
                RealTimeChatService.sPendingRequests.dump();
            }
        }

    };
    private final Runnable mTimeoutRunnable = new Runnable() {

        public final void run()
        {
            long l = SystemClock.elapsedRealtime() - 15000L;
            Integer integer;
            RealTimeChatServiceResult realtimechatserviceresult;
            for(Iterator iterator = RealTimeChatService.sPendingRequests.getOutdatedRequestIds(l).iterator(); iterator.hasNext(); RealTimeChatService.sResults.put(integer, realtimechatserviceresult))
            {
                integer = (Integer)iterator.next();
                if(EsLog.isLoggable("RealTimeChatService", 3))
                    Log.d("RealTimeChatService", (new StringBuilder(" request ")).append(integer).append(" timed out").toString());
                for(Iterator iterator1 = RealTimeChatService.sListeners.iterator(); iterator1.hasNext(); ((RealTimeChatServiceListener)iterator1.next()).onResponseTimeout(integer.intValue()));
                realtimechatserviceresult = new RealTimeChatServiceResult(integer.intValue(), 3, null);
            }

            RealTimeChatService.sPendingRequests.trim(l);
            RealTimeChatService.sPendingRequests.dump();
        }
    };
    
    private static void initWakeLock(Context context)
    {
        if(sWakeLock == null)
            sWakeLock = ((PowerManager)context.getSystemService("power")).newWakeLock(1, "realtimechat");
    }
    
    public static void initiateConnection(Context context, EsAccount esaccount)
    {
        if(EsLog.isLoggable("RealTimeChatService", 4))
            Log.i("RealTimeChatService", "initiateConnection");
        initWakeLock(context);
        if(!sWakeLock.isHeld())
        {
            if(EsLog.isLoggable("RealTimeChatService", 3))
                Log.d("RealTimeChatService", "acquiring wake lock");
            sWakeLock.acquire();
        }
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 111);
        intent.putExtra("account", esaccount);
        context.startService(intent);
    }
    
    public static void logout(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 116);
        intent.putExtra("account", esaccount);
        context.startService(intent);
    }

	public static boolean getConversationsLoaded()
    {
        return sConversationsLoaded;
    }
	
	static String getOrRequestC2dmId(Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("realtimechat", 0);
        String s = sharedpreferences.getString("c2dm_registration_id", null);
        long l = sharedpreferences.getLong("c2dm_registration_time", 0L);
        String s1 = sharedpreferences.getString("c2dm_registration_build_version", null);
        String s2;
        android.content.SharedPreferences.Editor editor;
        try
        {
            s2 = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            if(EsLog.isLoggable("RealTimeChatService", 6))
                Log.e("RealTimeChatService", "Can't find package information for current package, continuing anyway", namenotfoundexception);
            s2 = s1;
        }
        if(s1 != null && s1.equals(s2)) {
        	if(System.currentTimeMillis() - l > 0x2932e00L)
            {
                if(EsLog.isLoggable("RealTimeChatService", 3))
                    Log.d("RealTimeChatService", "refreshing registration for expiration");
                android.content.SharedPreferences.Editor editor1 = sharedpreferences.edit();
                if(s != null)
                {
                    Log.d("RealTimeChatService", "saving registration");
                    editor1.putString("sticky_c2dm_registration_id", s);
                }
                editor1.commit();
                s = null;
            } 
        } else { 
        	if(EsLog.isLoggable("RealTimeChatService", 3))
                Log.d("RealTimeChatService", "refreshing registration for update");
            editor = sharedpreferences.edit();
            if(s != null)
            {
                Log.d("RealTimeChatService", "saving registration");
                editor.putString("sticky_c2dm_registration_id", s);
            }
            editor.commit();
            s = null;
        }
        
        if(s == null)
            C2DMReceiver.requestC2DMRegistrationId(context);
        return s;
    }
	
	public static String getStickyC2dmId(Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("realtimechat", 0);
        String s = sharedpreferences.getString("c2dm_registration_id", null);
        if(s != null)
        {
            android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("sticky_c2dm_registration_id", s);
            editor.commit();
        } else
        {
            s = sharedpreferences.getString("sticky_c2dm_registration_id", null);
        }
        return s;
    }
	
	public static void registerListener(RealTimeChatServiceListener realtimechatservicelistener)
    {
        sListeners.add(realtimechatservicelistener);
    }
	
	public static void unregisterListener(RealTimeChatServiceListener realtimechatservicelistener)
    {
        sListeners.remove(realtimechatservicelistener);
    }
	
	public static int checkMessageSent(Context context, EsAccount esaccount, long l, int i)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 226);
        intent.putExtra("account", esaccount);
        intent.putExtra("message_row_id", l);
        intent.putExtra("flags", i);
        return startCommand(context, intent);
    }
	
	public static int inviteParticipants(Context context, EsAccount esaccount, long l, AudienceData audiencedata)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 332);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("audience", audiencedata);
        return startCommand(context, intent);
    }
	
	public static void resetNotifications(Context context, EsAccount esaccount)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 340);
        intent.putExtra("account", esaccount);
        context.startService(intent);
    }
	
	public static int removeMessage(Context context, EsAccount esaccount, long l)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 336);
        intent.putExtra("account", esaccount);
        intent.putExtra("message_row_id", l);
        return startCommand(context, intent);
    }
	
	public static int requestMoreEvents(Context context, EsAccount esaccount, long l)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 342);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        return startCommand(context, intent);
    }
	
	public static int leaveConversation(Context context, EsAccount esaccount, long l)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 333);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        return startCommand(context, intent);
    }
	
	public static int sendTileEvent(Context context, EsAccount esaccount, String s, String s1, int i, String s2, HashMap hashmap)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 351);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_id", s);
        intent.putExtra("tile_type", s1);
        intent.putExtra("tile_event_version", 0);
        intent.putExtra("tile_event_type", s2);
        intent.putExtra("tile_event_data", hashmap);
        return startCommand(context, intent);
    }
	
	public static int sendMessage(Context context, EsAccount esaccount, long l, String s, String s1)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 331);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("message_text", s);
        intent.putExtra("uri", s1);
        return startCommand(context, intent);
    }
	
	public static int sendTypingRequest(Context context, EsAccount esaccount, long l, Client.Typing.Type type)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 349);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("typing_status", type.getNumber());
        return startCommand(context, intent);
    }
	
	public static int sendLocalPhoto(Context context, EsAccount esaccount, long l, String s)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 345);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("uri", s);
        return startCommand(context, intent);
    }
	
	public static int createConversation(Context context, EsAccount esaccount, AudienceData audiencedata, String s)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 330);
        intent.putExtra("account", esaccount);
        intent.putExtra("audience", audiencedata);
        intent.putExtra("message_text", s);
        return startCommand(context, intent);
    }
	
	public static int setConversationMuted(Context context, EsAccount esaccount, long l, boolean flag)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 338);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("conversation_muted", flag);
        return startCommand(context, intent);
    }
	
	public static int setConversationName(Context context, EsAccount esaccount, long l, String s)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 337);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("conversation_name", s);
        return startCommand(context, intent);
    }
	
	public static void allowDisconnect(Context context, EsAccount esaccount)
    {
        if(EsLog.isLoggable("RealTimeChatService", 4))
            Log.i("RealTimeChatService", "allowDisconnect");
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 112);
        intent.putExtra("account", esaccount);
        context.startService(intent);
    }
	
	public static void connectAndStayConnected(Context context, EsAccount esaccount)
    {
        if(EsLog.isLoggable("RealTimeChatService", 4))
            Log.i("RealTimeChatService", "connectAndStayConnected");
        initWakeLock(context);
        if(!sWakeLock.isHeld())
        {
            if(EsLog.isLoggable("RealTimeChatService", 3))
                Log.d("RealTimeChatService", "acquiring wake lock");
            sWakeLock.acquire();
        }
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 110);
        intent.putExtra("account", esaccount);
        context.startService(intent);
    }
	
	public static synchronized void setCurrentConversationRowId(Long long1)
    {
        if(EsLog.isLoggable("RealTimeChatService", 3))
            Log.d("RealTimeChatService", (new StringBuilder("setCurrentConversationRowId ")).append(long1).toString());
        sCurrentConversationRowId = long1;
    }
	
	public static int markConversationRead(Context context, EsAccount esaccount, long l)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 335);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        return startCommand(context, intent);
    }
	
	public static int markConversationNotificationsSeen(Context context, EsAccount esaccount, long l)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 350);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        return startCommand(context, intent);
    }
	
	public static boolean isRequestPending(int i)
    {
        return sPendingRequests.requestPending(Integer.valueOf(i));
    }
	
	public static int retrySendMessage(Context context, EsAccount esaccount, long l)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 344);
        intent.putExtra("account", esaccount);
        intent.putExtra("message_row_id", l);
        return startCommand(context, intent);
    }
	
	public static RealTimeChatServiceResult removeResult(int i)
    {
        return (RealTimeChatServiceResult)sResults.remove(Integer.valueOf(i));
    }
	
	public static int sendPresenceRequest(Context context, EsAccount esaccount, long l, boolean flag, boolean flag1)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 348);
        intent.putExtra("account", esaccount);
        intent.putExtra("conversation_row_id", l);
        intent.putExtra("is_present", flag);
        intent.putExtra("reciprocate", flag1);
        return startCommand(context, intent);
    }
	
	public static int requestSuggestedParticipants(Context context, EsAccount esaccount, AudienceData audiencedata, Client.SuggestionsRequest.SuggestionsType suggestionstype)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 352);
        intent.putExtra("account", esaccount);
        intent.putExtra("audience", audiencedata);
        intent.putExtra("type", suggestionstype.getNumber());
        return startCommand(context, intent);
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private static int startCommand(Context context, Intent intent) {
        Integer integer = sLastRequestId;
        sLastRequestId = Integer.valueOf(1 + sLastRequestId.intValue());
        int i = integer.intValue();
        intent.putExtra("rid", i);
        context.startService(intent);
        if(EsLog.isLoggable("RealTimeChatService", 3))
            Log.d("RealTimeChatService", (new StringBuilder("start command request ")).append(i).append(" opCode ").append(intent.getIntExtra("op", 0)).toString());
        sPendingRequests.addRequest(Integer.valueOf(i));
        return i;
    }
	
	public static void connectIfLoggedIn(Context context, String s, String s1, String s2)
    {
        if(EsLog.isLoggable("RealTimeChatService", 4))
            Log.i("RealTimeChatService", "connectIfLoggedIn");
        initWakeLock(context);
        if(!sWakeLock.isHeld())
        {
            if(EsLog.isLoggable("RealTimeChatService", 3))
                Log.d("RealTimeChatService", "acquiring wake lock");
            sWakeLock.acquire();
        }
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 115);
        intent.putExtra("account_id", s);
        intent.putExtra("conversation_id", s1);
        intent.putExtra("message_timestamp", s2);
        context.startService(intent);
    }
	
	public static void handleC2DMRegistrationError(Context context, String s)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 117);
        intent.putExtra("error", s);
        context.startService(intent);
    }
	
	public static void handleC2DMRegistration(Context context, String s)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 113);
        intent.putExtra("registration", s);
        context.startService(intent);
    }
	
	public static void handleC2DMUnregistration(Context context)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 114);
        context.startService(intent);
    }
	
	public static boolean debuggable()
    {
        return EsLog.ENABLE_DOGFOOD_FEATURES;
    }
	
	public static int setAcl(Context context, EsAccount esaccount, int i)
    {
        Intent intent = new Intent(context, RealTimeChatService.class);
        intent.putExtra("op", 343);
        intent.putExtra("account", esaccount);
        intent.putExtra("acl", i);
        return startCommand(context, intent);
    }

	//======================================================================================
	//							Inner class
	//======================================================================================
	
	private final class ConnectRunnable implements Runnable {

		public EsAccount mAccount;
		
	    ConnectRunnable()
	    {
	        super();
	    }
	    
	    public final void run()
	    {
	        if(EsLog.isLoggable("RealTimeChatService", 3))
	            Log.d("RealTimeChatService", "running connect runnable");
	        Intent intent = new Intent(RealTimeChatService.this, RealTimeChatService.class);
	        intent.putExtra("op", 220);
	        intent.putExtra("account", mAccount);
	        startService(intent);
	    }
	
	}
	
	private static final class PendingRequestList {

		private final LinkedList mRequestList = new LinkedList();
        private final HashMap mRequestTimestamps = new HashMap();

        PendingRequestList()
        {
        }
        
        public final void addRequest(Object obj)
        {
            long l = SystemClock.elapsedRealtime();
            if(EsLog.isLoggable("RealTimeChatService", 3))
                Log.d("RealTimeChatService", (new StringBuilder("adding request ")).append(obj).append(" time ").append(l).toString());
            mRequestList.addLast(obj);
            mRequestTimestamps.put(obj, Long.valueOf(l));
        }

        public final void dump()
        {
            if(EsLog.isLoggable("RealTimeChatService", 2))
            {
                StringBuilder stringbuilder = new StringBuilder();
                Object obj;
                Long long1;
                for(Iterator iterator = mRequestList.iterator(); iterator.hasNext(); stringbuilder.append("[").append(obj).append(",").append(long1).append("] "))
                {
                    obj = iterator.next();
                    long1 = (Long)mRequestTimestamps.get(obj);
                }

                Log.v("RealTimeChatService", (new StringBuilder("pendingRequests ")).append(stringbuilder.toString()).toString());
            }
        }

        public final List getOutdatedRequestIds(long l)
        {
            LinkedList linkedlist = new LinkedList();
            Iterator iterator = mRequestList.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                Object obj = iterator.next();
                Long long1 = (Long)mRequestTimestamps.get(obj);
                if(long1 == null || long1.longValue() >= l)
                    break;
                linkedlist.add(obj);
            } while(true);
            return linkedlist;
        }

        public final boolean isEmpty()
        {
            return mRequestList.isEmpty();
        }

        public final void removeRequest(Object obj)
        {
            mRequestTimestamps.remove(obj);
        }

        public final boolean requestPending(Object obj)
        {
            boolean flag;
            if((Long)mRequestTimestamps.get(obj) != null)
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final void trim(long l)
        {
            for(boolean flag = false; !flag && !mRequestList.isEmpty();)
            {
                Object obj = mRequestList.getFirst();
                Long long1 = (Long)mRequestTimestamps.get(obj);
                if(long1 == null || long1.longValue() < l)
                {
                    mRequestTimestamps.remove(obj);
                    mRequestList.removeFirst();
                } else
                {
                    flag = true;
                }
            }

        }
    }
	
	private static final class ResultsLinkedHashMap extends LinkedHashMap {

        /**
		 * 
		 */
		private static final long serialVersionUID = -6565939257603795696L;

		ResultsLinkedHashMap()
        {
        }
        
        protected final boolean removeEldestEntry(java.util.Map.Entry entry)
        {
            boolean flag;
            if(size() > 32)
                flag = true;
            else
                flag = false;
            return flag;
        }

    }
	
}
