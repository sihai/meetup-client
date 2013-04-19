/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.analytics.AnalyticsInfo;
import com.galaxy.meetup.client.android.analytics.EsAnalytics;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.iu.InstantUploadSyncService;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;

/**
 * @author sihai
 */
public class EsSyncAdapterService extends Service {

	private static SyncState sCurrentSyncState;
	private static SyncAdapterImpl sSyncAdapter = null;
	private static final Object sSyncAdapterLock = new Object();
	private static Map sSyncStates = new HashMap();

	public EsSyncAdapterService() {
	}

	public void onCreate() {
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null)
				sSyncAdapter = new SyncAdapterImpl(getApplicationContext());
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return sSyncAdapter.getSyncAdapterBinder();
	}

	public static void activateAccount(Context context, String s) {
		Account account = AccountsUtil.newAccount(s);
		ContentResolver.setIsSyncable(account, "com.galaxy.meetup.client.android.content.EsProvider", 1);
		ContentResolver.setSyncAutomatically(account, "com.galaxy.meetup.client.android.content.EsProvider", true);
		ContentResolver.requestSync(account, "com.galaxy.meetup.client.android.content.EsProvider", new Bundle());
		resetSyncStates(context, s);
	}

	public static void deactivateAccount(String s) {
		Account account = AccountsUtil.newAccount(s);
		ContentResolver.setIsSyncable(account, "com.galaxy.meetup.client.android.content.EsProvider", 0);
		ContentResolver.cancelSync(account, "com.galaxy.meetup.client.android.content.EsProvider");
		SyncState syncstate = (SyncState) sSyncStates.get(s);
		if (syncstate != null)
			syncstate.cancel();
	}

	public static SyncState getAccountSyncState(String s) {
		SyncState syncstate;
		synchronized (sSyncStates) {
			syncstate = (SyncState) sSyncStates.get(s);
			if (syncstate == null) {
				syncstate = new SyncState();
				sSyncStates.put(s, syncstate);
			}
		}
		return syncstate;
	}

	private static void resetSyncStates(Context context, String s) {
		Iterator iterator = AccountsUtil.getAccounts(context).iterator();
		do {
			if (!iterator.hasNext())
				break;
			Account account = (Account) iterator.next();
			if (!TextUtils.equals(s, account.name))
				deactivateAccount(account.name);
		} while (true);
	}

	// ===========================================================================
	// Inner class
	// ===========================================================================

	public static final class SyncOperationState {

		public int count;
		public long duration;
		public HttpTransactionMetrics metrics;
		public String operation;
		public int subCount;

		public SyncOperationState() {
		}
	}

	public static class SyncState {

		private boolean mCanceled;
		private int mCurrentCount;
		private HttpTransactionMetrics mCurrentMetrics;
		private String mCurrentOperation;
		private long mCurrentOperationStart;
		private int mCurrentSubCount;
		private boolean mFullSync;
		private final ArrayList<SyncOperationState> mOperations = new ArrayList<SyncOperationState>();
		private final LinkedBlockingQueue<Bundle> mRequestQueue = new LinkedBlockingQueue<Bundle>();
		private long mStartTimestamp;
		private String mSyncName;

		public SyncState() {
		}

		public final synchronized void onStart(String operation) {
			mCurrentOperation = operation;
			mCurrentOperationStart = System.currentTimeMillis();
			mCurrentCount = 0;
			mCurrentSubCount = 0;
			mCurrentMetrics = new HttpTransactionMetrics();
		}

		public final synchronized void onSyncStart(String syncName) {
			if (EsLog.isLoggable("EsSyncAdapterService", 4))
				Log.i("EsSyncAdapterService", (new StringBuilder()).append(syncName).append(" started.").toString());
			mSyncName = syncName;
			mCanceled = false;
			mStartTimestamp = System.currentTimeMillis();
			mOperations.clear();
		}

		public final synchronized void onFinish() {
			onFinish(mCurrentCount, mCurrentSubCount);
		}

		public final synchronized void onFinish(int i) {
			onFinish(i, 0);
		}
        
        private synchronized void onFinish(int count, int subCount) {
            SyncOperationState syncoperationstate = new SyncOperationState();
            syncoperationstate.operation = mCurrentOperation;
            syncoperationstate.duration = System.currentTimeMillis() - mCurrentOperationStart;
            syncoperationstate.count = count;
            syncoperationstate.subCount = subCount;
            syncoperationstate.metrics = mCurrentMetrics;
            mCurrentMetrics = null;
            mOperations.add(syncoperationstate);
        }
        
        public final synchronized void onSyncFinish()
        {
        	logSyncStats(this);
        }
        
		public final synchronized void cancel() {
			mCanceled = true;
		}

		public final synchronized boolean isCanceled() {
			return mCanceled;
		}

		public final HttpTransactionMetrics getHttpTransactionMetrics() {
			return mCurrentMetrics;
		}

		public final synchronized void incrementCount() {
			mCurrentCount += 1;
		}

		public final synchronized void incrementCount(int count) {
			mCurrentCount += count;
		}

		public final synchronized void incrementSubCount() {
			mCurrentSubCount += 1;
		}

		public final Bundle pollAccountSyncRequest() {
			return mRequestQueue.poll();
		}

		public final synchronized boolean requestAccountSync(Bundle bundle) {
			boolean flag;
			flag = mRequestQueue.isEmpty();
			if (bundle == null)
				bundle = new Bundle();
			mRequestQueue.offer(bundle);
			return flag;
		}

		public final void setFullSync(boolean flag) {
			mFullSync = flag;
		}

		private static synchronized void logSyncStats(SyncState syncstate) {
			// TODO
		}
	}

	private class SyncListener implements HttpOperation.OperationListener {

		private final SyncResult mSyncResult;

		public final void onOperationComplete(HttpOperation httpoperation) {

			int code = httpoperation.getErrorCode();
			Exception exception = httpoperation.getException();
			if (EsLog.isLoggable("EsSyncAdapterService", 3))
				Log.d("EsSyncAdapterService", (new StringBuilder("Sync operation complete: ")).append(code).append('/')
						.append(httpoperation.getReasonPhrase()).append('/').append(exception).toString());

			if (null == exception) {
				if (code == 401) {
					mSyncResult.stats.numAuthExceptions += 1;
				} else if (httpoperation.hasError()) {
					mSyncResult.stats.numIoExceptions += 1L;
				}
				return;
			} else if (exception instanceof AuthenticatorException) {
				mSyncResult.stats.numAuthExceptions += 1;
			} else {
				mSyncResult.stats.numIoExceptions += 1;
			}
		}

		public SyncListener(SyncResult syncresult) {
			mSyncResult = syncresult;
		}
	}

	private static final class SyncAdapterImpl extends AbstractThreadedSyncAdapter {

		private final Context context;

		public SyncAdapterImpl(Context context) {
			super(context, false);
			this.context = context;
		}

		private boolean isSubscribed(Account account, String s, String s1) {

			String as[] = new String[5];
			as[0] = "com.galaxy.meetup.client.android.content.EsProvider";
			as[1] = s;
			as[2] = account.name;
			as[3] = account.type;
			as[4] = s1;
			Cursor cursor = null;
			try {
				cursor = getContext().getContentResolver().query(SubscribedFeeds.Feeds.CONTENT_URI,
						new String[] { "_id" },
						"authority = ? AND feed = ? AND _sync_account = ? AND _sync_account_type = ? AND service = ?",
						as, null);
				if (null == cursor) {
					return false;
				}
				return cursor.moveToFirst();
			} finally {
				if (null != cursor) {
					cursor.close();
				}
			}
		}

		private void subscribe(Account account, String s, String s1) {
			if (EsLog.isLoggable("EsSyncAdapterService", 3))
				Log.d("EsSyncAdapterService", "  --> Subscribe all feeds");
			ContentResolver contentresolver = getContext().getContentResolver();
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("feed", s);
			contentvalues.put("_sync_account", account.name);
			contentvalues.put("_sync_account_type", account.type);
			contentvalues.put("authority", "com.galaxy.meetup.client.android.content.EsProvider");
			contentvalues.put("service", s1);
			contentresolver.insert(SubscribedFeeds.Feeds.CONTENT_URI, contentvalues);
		}

		private void updateSubscribedFeeds(Account account) {
			boolean flag;
			if (ContentResolver.getMasterSyncAutomatically()
					&& ContentResolver.getSyncAutomatically(account,
							"com.galaxy.meetup.client.android.content.EsProvider"))
				flag = true;
			else
				flag = false;
			if (flag) {
				boolean flag1 = true;
				if (!isSubscribed(account,
						"https://m.google.com/app/feed/notifications?authority=com.google.plus.notifications",
						"webupdates")) {
					subscribe(account,
							"https://m.google.com/app/feed/notifications?authority=com.google.plus.notifications",
							"webupdates");
					flag1 = false;
				}
				if (!isSubscribed(account, "com.google.plus.events", "events")) {
					subscribe(account, "com.google.plus.events", "events");
					flag1 = false;
				}
				if (flag1) {
					if (EsLog.isLoggable("EsSyncAdapterService", 3))
						Log.d("EsSyncAdapterService", "  --> Already subscribed");
					String as1[] = new String[3];
					as1[0] = "com.google.plus.notifications";
					as1[1] = "https://m.google.com/app/feed/notifications?authority=com.google.plus.notifications";
					as1[2] = account.type;
					getContext().getContentResolver().delete(SubscribedFeeds.Feeds.CONTENT_URI,
							"authority = ? AND feed = ? AND _sync_account_type = ?", as1);
				}
			} else {
				if (EsLog.isLoggable("EsSyncAdapterService", 3))
					Log.d("EsSyncAdapterService", "  --> Unsubscribe all feeds");
				ContentResolver contentresolver = getContext().getContentResolver();
				StringBuilder stringbuilder = new StringBuilder();
				stringbuilder.append("_sync_account=?");
				stringbuilder.append(" AND _sync_account_type=?");
				stringbuilder.append(" AND authority=?");
				android.net.Uri uri = SubscribedFeeds.Feeds.CONTENT_URI;
				String s = stringbuilder.toString();
				String as[] = new String[3];
				as[0] = account.name;
				as[1] = account.type;
				as[2] = "com.galaxy.meetup.client.android.content.EsProvider";
				contentresolver.delete(uri, s, as);
			}
		}

		public final void onPerformSync(Account account, final Bundle bundle, String s, ContentProviderClient contentproviderclient, SyncResult syncresult)
        {

            boolean flag;
            EsAccount theAccount = EsAccountsData.getActiveAccount(context);
            boolean flag1;

            Account account2;
            
            if(theAccount != null && account.name.equals(theAccount.getName()))
                flag = true;
            else
                flag = false;
            
            if(bundle != null && bundle.getBoolean("initialize", false))
                flag1 = true;
            else
                flag1 = false;
            if(flag1) {
            	account2 = AccountsUtil.newAccount(account.name);
                int k;
                if(flag)
                    k = 1;
                else
                    k = 0;
                ContentResolver.setIsSyncable(account2, "com.galaxy.meetup.client.android.content.EsProvider", k);
                updateSubscribedFeeds(account);
            }
            
            String s1;
            Context context1 = context;
            SharedPreferences sharedpreferences = context1.getSharedPreferences("sync", 0);
            if(!sharedpreferences.getBoolean("adapters_reset", false))
            {
                Account aaccount[] = AccountManager.get(context1).getAccountsByType(AccountsUtil.ACCOUNT_TYPE);
                if(aaccount != null)
                {
                    int i = aaccount.length;
                    int j = 0;
                    while(j < i) 
                    {
                        Account account1 = aaccount[j];
                        boolean flag3;
                        if(ContentResolver.getIsSyncable(account1, "com.galaxy.meetup.client.android.content.EsProvider") == 1)
                            flag3 = true;
                        else
                            flag3 = false;
                        if(!flag3)
                            InstantUploadSyncService.deactivateAccount(context1, account1.name);
                        j++;
                    }
                }
                sharedpreferences.edit().putBoolean("adapters_reset", true).commit();
            }
            updateSubscribedFeeds(account);
            if(!flag)
            {
                EsAccount esaccount = EsAccountsData.getActiveAccountUnsafe(context);
                if(esaccount == null || !EsAccountsData.isAccountUpgradeRequired(context, esaccount))
                    return;
                boolean flag2;
                try
                {
                    EsAccountsData.upgradeAccount(context, esaccount);
                }
                catch(Exception exception1)
                {
                    Log.e("EsSyncAdapterService", "Failed to upgrade account", exception1);
                    return;
                }
                theAccount = EsAccountsData.getActiveAccount(context);
                if(theAccount != null && account.name.equals(theAccount.getName()))
                    flag2 = true;
                else
                    flag2 = false;
                if(!flag2)
                	return;
            }
            if(null != bundle && bundle.containsKey("feed")) {
            	s1 = bundle.getString("feed");
                if(EsLog.isLoggable("EsSyncAdapterService", 3))
                    Log.d("EsSyncAdapterService", (new StringBuilder("  --> Sync specific feed: ")).append(s1).toString());
                bundle.putBoolean("sync_from_tickle", true);
                if(!"https://m.google.com/app/feed/notifications?authority=com.google.plus.notifications".equals(s1)) {
                	if("com.google.plus.events".equals(s1)) {
                		bundle.putInt("sync_what", 2);
                    	EsAnalytics.postRecordEvent(context, theAccount, new AnalyticsInfo(OzViews.NOTIFICATIONS_SYSTEM), OzActions.TICKLE_EVENT_RECEIVED, null);
                	} else {
                		Log.e("EsSyncAdapterService", (new StringBuilder("Unexpected feed: ")).append(s1).toString());
                		return;
                	}
                } else {
                	bundle.putInt("sync_what", 1);
                    EsAnalytics.postRecordEvent(context, theAccount, new AnalyticsInfo(OzViews.NOTIFICATIONS_SYSTEM), OzActions.TICKLE_NOTIFICATION_RECEIVED, null);
                }
            }
            
            EsSyncAdapterService.sCurrentSyncState = EsSyncAdapterService.getAccountSyncState(account.name);
            EsSyncAdapterService.access$100(context, theAccount, bundle, EsSyncAdapterService.sCurrentSyncState, syncresult);
            final EsAccount account3 = theAccount;
            if(!EsSyncAdapterService.sCurrentSyncState.isCanceled())
                (new Handler(Looper.getMainLooper())).post(new Runnable() {

                    public final void run()
                    {
                        Context context = SyncAdapterImpl.this.context;
                        String s;
                        if(bundle != null)
                            s = bundle.getString("sync_tag");
                        else
                            s = null;
                        EsService.syncComplete(context, account3, s);
                    }

                });
            EsSyncAdapterService.sCurrentSyncState = null;
        }

		public final void onSyncCanceled() {
			if (EsSyncAdapterService.sCurrentSyncState != null)
				EsSyncAdapterService.sCurrentSyncState.cancel();
		}
	}

	
	static void access$100(Context context, EsAccount esaccount, Bundle bundle, SyncState syncstate, SyncResult syncresult)
    {
        if(!syncstate.requestAccountSync(bundle)) {
        	return;
        }
        // TODO
    }

}
