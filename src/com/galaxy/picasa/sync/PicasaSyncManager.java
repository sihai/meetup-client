/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


/**
 * 
 * @author sihai
 * 
 */
public class PicasaSyncManager {

	private static PicasaSyncManager sInstance;
    private boolean mBackgroundData;
    private final Context mContext;
    private volatile SyncSession mCurrentSession;
    private final PicasaFacade mFacade;
    private boolean mHasWifiConnectivity;
    private final Set mInvalidAccounts = new HashSet();
    private boolean mIsPlugged;
    private boolean mIsRoaming;
    private final List mProviders = new ArrayList();
    private final Handler mSyncHandler;
    private final PicasaSyncHelper mSyncHelper;
    private List mSyncRequests;
	
	private PicasaSyncManager(Context context) {
        mHasWifiConnectivity = false;
        mIsRoaming = false;
        mIsPlugged = false;
        mBackgroundData = false;
        mSyncRequests = new ArrayList();
        mContext = context.getApplicationContext();
        mFacade = PicasaFacade.get(mContext);
        mSyncHelper = PicasaSyncHelper.getInstance(mContext);
        HandlerThread handlerthread = new HandlerThread("picasa-sync-manager", 10);
        handlerthread.start();
        mSyncHandler = new Handler(handlerthread.getLooper()) {

            public final void handleMessage(Message message) {
            	
            	switch( message.what) {
            	case 1:
            		access$200();
            		break;
            	case 2:
            		access$300();
            		break;
            	case 3:
            		updateTasksInternal();
            		break;
            	case 4:
            		mSyncHelper.syncAccounts(mFacade.getAuthority());
            		break;
            	case 5:
            		access$400((Boolean)message.obj);
            		break;
            	case 6:
            		access$100();
            		break;
            		default:
            			throw new AssertionError((new StringBuilder("unknown message: ")).append(message.what).toString());
            	}
            }
        };
        mSyncHandler.sendEmptyMessage(6);
        mSyncHandler.sendEmptyMessage(4);
        mSyncHandler.sendEmptyMessage(2);
        mSyncHandler.sendEmptyMessage(5);
        OnAccountsUpdateListener onaccountsupdatelistener = new OnAccountsUpdateListener() {

            public final void onAccountsUpdated(Account aaccount[])
            {
                Log.i("gp.PicasaSyncManager", "account change detect - update database");
                mSyncHandler.sendEmptyMessage(4);
            }

        };
        AccountManager.get(mContext).addOnAccountsUpdatedListener(onaccountsupdatelistener, null, false);
    }
	
	public static synchronized PicasaSyncManager get(Context context) {
        PicasaSyncManager picasasyncmanager;
        if(sInstance == null)
            sInstance = new PicasaSyncManager(context);
        picasasyncmanager = sInstance;
        return picasasyncmanager;
    }
	
	public final void onBatteryStateChanged(boolean flag)
    {
        Handler handler = mSyncHandler;
        Boolean boolean1;
        Message message;
        if(flag)
            boolean1 = Boolean.TRUE;
        else
            boolean1 = Boolean.FALSE;
        message = Message.obtain(handler, 5, boolean1);
        mSyncHandler.sendMessage(message);
    }
	
	public final void onEnvironmentChanged()
    {
        mSyncHandler.sendEmptyMessage(2);
    }
	
	public final void updateTasks(long l) {
		mSyncHandler.sendEmptyMessageDelayed(3, l);
	}
	
	public final void resetSyncStates() {
		synchronized (mInvalidAccounts) {
			mInvalidAccounts.clear();
		}
		synchronized (this) {
			for (Iterator iterator = mProviders.iterator(); iterator.hasNext(); ((SyncTaskProvider) iterator
					.next()).resetSyncStates())
				;
		}
	}
	
	public final void requestPrefetchSync() {
		PhotoPrefetch.onRequestSync(mContext);
		requestSync(null, SyncState.PREFETCH_FULL_IMAGE);
		requestSync(null, SyncState.PREFETCH_SCREEN_NAIL);
		requestSync(null, SyncState.PREFETCH_ALBUM_COVER);
	}
	
	public final void requestAccountSync() {
		mSyncHandler.sendEmptyMessage(4);
	}

	public final void requestMetadataSync(boolean flag) {
		SyncState syncstate;
		if (flag)
			syncstate = SyncState.METADATA_MANUAL;
		else
			syncstate = SyncState.METADATA;
		requestSync(null, syncstate);
	}
	
	private synchronized void requestSync(String s, SyncState syncstate)
    {
        if(mSyncRequests.size() == 0)
            mSyncHandler.sendEmptyMessage(1);
        mSyncRequests.add(new SyncRequest(null, syncstate));
    }
	
	private void access$200() {
		List requestList = null;
		synchronized (this) {
			requestList = mSyncRequests;
			mSyncRequests = new ArrayList();
		}
		boolean flag = false;
		SQLiteDatabase sqlitedatabase = mSyncHelper.getWritableDatabase();
		ArrayList arraylist1 = null;
		for (Iterator iterator = requestList.iterator(); iterator.hasNext();) {
			SyncRequest syncrequest = (SyncRequest) iterator.next();
			Exception exception;
			if (syncrequest.account == null) {
				if (arraylist1 == null)
					arraylist1 = mSyncHelper.getUsers();
				Iterator iterator1 = arraylist1.iterator();
				while (iterator1.hasNext()) {
					UserEntry userentry = (UserEntry) iterator1.next();
					flag |= syncrequest.state.onSyncRequested(sqlitedatabase,
							userentry.account);
				}
			} else {
				flag |= syncrequest.state.onSyncRequested(sqlitedatabase,
						syncrequest.account);
			}
		}

		if (flag)
			updateTasks(1000L);
		return;
	}
	
	private void updateTasksInternal()
    {
        // TODO
    }
	
	private SyncTask nextSyncTaskInternal(String s)
    {
		SyncTask synctask = null;
        int j = mProviders.size();
        for(int i = 0; i < j; i++) {
        	SyncTaskProvider synctaskprovider = (SyncTaskProvider)mProviders.get(i);
        	ArrayList arraylist = new ArrayList();
            synctaskprovider.collectTasks(arraylist);
            Iterator iterator = arraylist.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                SyncTask synctask1 = (SyncTask)iterator.next();
                synctask1.mPriority = i;
                if(acceptSyncTask(synctask1) && (synctask == null || synctask1.syncAccount.equals(s)))
                     synctask = synctask1;
            } while(true);
        }
        
        return synctask;
    }
	
	private boolean acceptSyncTask(SyncTask synctask)
    {
        // TODO
		return false;
    }
	
	synchronized void access$100()
    {
        mProviders.add(new MetadataSync(mContext, true));
        mProviders.add(new MetadataSync(mContext, false));
        mProviders.add(new PhotoPrefetch(mContext, 2));
        mProviders.add(new PhotoPrefetch(mContext, 3));
        mProviders.add(new PhotoPrefetch(mContext, 1));
    }
	
	private void access$300()
    {
        boolean flag = true;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag6;
        mSyncHandler.removeMessages(2);
        ConnectivityManager connectivitymanager = (ConnectivityManager)mContext.getSystemService("connectivity");
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        Log.d("gp.PicasaSyncManager", (new StringBuilder("active network: ")).append(networkinfo).toString());
        if(networkinfo == null) 
        	flag1 = false; 
        else 
        	switch(networkinfo.getType()) {
        	case 0:
        		flag6 = false;
        		if(!flag6) {
       			 	flag1 = false;
       		 	} else {
       			 flag1 = flag;
       		 	}
        		break;
        	case 1:
        		flag6 = flag;
	       		 if(!flag6) {
	       			 flag1 = false;
	       		 } else {
	       			 flag1 = flag;
	       		 }
        	case 2:
        		flag6 = false;
        		if(!flag6) {
       			 	flag1 = false;
       		 	} else {
       			 flag1 = flag;
       		 	}
        		break;
        	case 3:
        		flag6 = false;
        		if(!flag6) {
       			 	flag1 = false;
       		 	} else {
       			 flag1 = flag;
       		 	}
        		break;
        	case 4:
        		flag6 = false;
        		if(!flag6) {
       			 	flag1 = false;
       		 	} else {
       			 flag1 = flag;
       		 	}
        		break;
        	case 5:
        		flag6 = false;
        		if(!flag6) {
       			 	flag1 = false;
       		 	} else {
       			 flag1 = flag;
       		 	}
        		break;
        	case 6:
        		flag6 = false;
        		if(!flag6) {
       			 	flag1 = false;
       		 	} else {
       			 flag1 = flag;
       		 	}
        		break;
        	default:
        		flag6 = flag;
        		 if(!flag6) {
        			 flag1 = false;
        		 } else {
        			 flag1 = flag;
        		 }
        		break;
        	}
        if(flag1 != mHasWifiConnectivity)
        {
            mHasWifiConnectivity = flag1;
            flag2 = flag;
        } else
        {
            flag2 = false;
        }
        flag3 = false;
        if(networkinfo != null)
        {
            boolean flag5 = networkinfo.isRoaming();
            flag3 = false;
            if(flag5)
                flag3 = flag;
        }
        if(flag3 != mIsRoaming)
        {
        	mIsRoaming = flag3;
            flag2 = flag;
        }
        flag4 = connectivitymanager.getBackgroundDataSetting();
        Log.d("gp.PicasaSyncManager", (new StringBuilder("background data: ")).append(flag4).toString());
        if(mBackgroundData != flag4)
            mBackgroundData = flag4;
        else
            flag = flag2;
        if(flag)
            updateTasksInternal();
        return;
    }
	
	private void access$400(Boolean boolean1) {
		boolean flag;
		flag = true;
		mSyncHandler.removeMessages(5);
		if (null == boolean1) {
			Intent intent;
			IntentFilter intentfilter = new IntentFilter(
					"android.intent.action.BATTERY_CHANGED");
			intent = mContext.registerReceiver(null, intentfilter);
			if (null == intent) {
				Log.w("gp.PicasaSyncManager", "there is no battery info yet");
				return;
			}
			int i = intent.getIntExtra("plugged", -1);
			if (i != 1 && i != 2)
				flag = false;
			boolean1 = Boolean.valueOf(flag);
		}
		Log.d("gp.PicasaSyncManager", (new StringBuilder("battery info: "))
				.append(boolean1).toString());
		if (mIsPlugged != boolean1.booleanValue()) {
			mIsPlugged = boolean1.booleanValue();
			updateTasksInternal();
		}
	}
	
	private class SyncRequest {

		public String account;
		public SyncState state;

		public SyncRequest(String s, SyncState syncstate) {
			account = s;
			state = syncstate;
		}
	}

	private final class GetNextSyncTask implements Callable {

		private final SyncSession mSession;

		public GetNextSyncTask(SyncSession syncsession) {
			super();
			mSession = syncsession;
		}

		public Void call() {
			mSyncHandler.removeMessages(3);
			SyncTask synctask = nextSyncTaskInternal(mSession.account);
			synchronized (mSession) {
				if (mSession.mSyncCancelled)
					return null;
				mSession.mCurrentTask = synctask;
			}
			return null;
		}
	}

	public static final class SyncSession {

		public final String account;
		SyncTask mCurrentTask;
		boolean mSyncCancelled;
		public final SyncResult result;

		public SyncSession(String s, SyncResult syncresult) {
			account = s;
			result = syncresult;
		}

		public final synchronized void cancelSync() {
			mSyncCancelled = true;
			if (mCurrentTask != null) {
				mCurrentTask.cancelSync();
				mCurrentTask = null;
			}
		}

		public final synchronized boolean isSyncCancelled() {
			boolean flag = mSyncCancelled;
			return flag;
		}
	}
}
