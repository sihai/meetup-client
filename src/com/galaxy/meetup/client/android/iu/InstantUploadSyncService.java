package com.galaxy.meetup.client.android.iu;

import java.util.Iterator;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;

public class InstantUploadSyncService extends Service {

	private static InstantUploadSyncAdapter sSyncAdapter;
	
	public InstantUploadSyncService()
    {
    }

    public static void activateAccount(Context context, String s)
    {
        Account account = new Account(s, AccountsUtil.ACCOUNT_TYPE);
        ContentResolver.setIsSyncable(account, "com.galaxy.android.apps.meetup.iu.EsGoogleIuProvider", 1);
        ContentResolver.setSyncAutomatically(account, "com.galaxy.android.apps.meetup.iu.EsGoogleIuProvider", true);
        ContentResolver.requestSync(account, "com.galaxy.android.apps.meetup.iu.EsGoogleIuProvider", new Bundle());
        resetSyncStates(context, s);
        InstantUploadSyncManager.getInstance(context).onAccountActivated(s);
    }

    public static void deactivateAccount(Context context, String s)
    {
        Account account = new Account(s, AccountsUtil.ACCOUNT_TYPE);
        ContentResolver.setIsSyncable(account, "com.galaxy.meetup.client.android.iu.EsGalaxyIuProvider", 0);
        ContentResolver.cancelSync(account, "com.galaxy.meetup.client.android.iu.EsGalaxyIuProvider");
        InstantUploadSyncManager.getInstance(context).onAccountDeactivated(s);
    }

    private static synchronized InstantUploadSyncAdapter getSyncAdapter(Context context)
    {
        if(sSyncAdapter == null)
            sSyncAdapter = new InstantUploadSyncAdapter(context);
        InstantUploadSyncAdapter instantuploadsyncadapter = sSyncAdapter;
        return instantuploadsyncadapter;
    }

    private static void resetSyncStates(Context context, String s)
    {
        Iterator iterator = AccountsUtil.getAccounts(context).iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Account account = (Account)iterator.next();
            if(!TextUtils.equals(s, account.name))
                deactivateAccount(context, account.name);
        } while(true);
    }

    public final IBinder onBind(Intent intent)
    {
        return getSyncAdapter(this).getSyncAdapterBinder();
    }

	public static class CarryOverDummyReceiver extends BroadcastReceiver
    {

        public void onReceive(Context context, Intent intent)
        {
        }

        public CarryOverDummyReceiver()
        {
        }
    }
	
	private static final class InstantUploadSyncAdapter extends AbstractThreadedSyncAdapter {
		
		private final Context mContext;
        private InstantUploadSyncManager.SyncSession mSession;

        public InstantUploadSyncAdapter(Context context) {
            super(context, false);
            mContext = context;
        }
        
		private static void carryOverSyncAutomaticallyForAllAccounts(Context context) {
			// TODO
        }
		
		public final void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentproviderclient, SyncResult syncresult) {
			// TODO
        }
		
		public final synchronized void onSyncCanceled() {
            if(EsLog.isLoggable("InstantUploadSyncSvc", 3))
                Log.d("InstantUploadSyncSvc", "receive cancel request");
            super.onSyncCanceled();
            if(mSession != null)
                mSession.cancelSync();
        }
    }
}
