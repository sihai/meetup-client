/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.util.HashSet;
import java.util.Set;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.picasa.sync.PicasaSyncHelper;
import com.galaxy.picasa.sync.SyncTask;
import com.galaxy.picasa.sync.SyncTaskProvider;
import com.galaxy.picasa.sync.UserEntry;

/**
 * 
 * @author sihai
 *
 */
public class InstantUploadSyncManager {

	private static final String PROJECTION_ENABLE_ACCOUNT[] = {
        "auto_upload_enabled", "auto_upload_account_name", "instant_share_eventid", "instant_share_starttime", "instant_share_endtime"
    };
    private static final String UPLOAD_TASK_CLEANUP_DELETE_WHERE;
    private static InstantUploadSyncManager sInstance;
    private boolean mBackgroundData;
    private final Context mContext;
    private volatile SyncSession mCurrentSession;
    private boolean mHasWifiConnectivity;
    private final Set mInvalidAccounts = new HashSet();
    private boolean mIsPlugged;
    private boolean mIsRoaming;
    SyncTaskProvider mProvider;
    private final Handler mSyncHandler;
    private final PicasaSyncHelper mSyncHelper;

    static 
    {
        UPLOAD_TASK_CLEANUP_DELETE_WHERE = (new StringBuilder("media_record_id NOT IN ( SELECT _id FROM ")).append(MediaRecordEntry.SCHEMA.getTableName()).append(" WHERE upload_account").append(" == ? AND ").append("upload_state < ").append(200).append(" )").toString();
    }
    
    private InstantUploadSyncManager(Context context)
    {
        mHasWifiConnectivity = false;
        mIsRoaming = false;
        mIsPlugged = false;
        mBackgroundData = false;
        mContext = context.getApplicationContext();
        mSyncHelper = PicasaSyncHelper.getInstance(mContext);
        HandlerThread handlerthread = new HandlerThread("picasa-sync-manager", 10);
        handlerthread.start();
        mSyncHandler = new Handler(handlerthread.getLooper()) ;
        mSyncHandler.sendEmptyMessage(1);
        mSyncHandler.sendEmptyMessage(4);
        mSyncHandler.sendEmptyMessage(2);
        mSyncHandler.sendEmptyMessage(5);
        OnAccountsUpdateListener onaccountsupdatelistener = new OnAccountsUpdateListener() {

            public final void onAccountsUpdated(Account aaccount[])
            {
                if(EsLog.isLoggable("iu.SyncManager", 4))
                    Log.i("iu.SyncManager", "account change detect - update database");
                mSyncHandler.sendEmptyMessage(4);
            }
        };
        AccountManager.get(mContext).addOnAccountsUpdatedListener(onaccountsupdatelistener, null, false);
    }
    
	
	public static synchronized InstantUploadSyncManager getInstance(Context context) {
        InstantUploadSyncManager instantuploadsyncmanager;
        if(sInstance == null)
            sInstance = new InstantUploadSyncManager(context);
        instantuploadsyncmanager = sInstance;
        return instantuploadsyncmanager;
    }
	
	public final void onEnvironmentChanged()
    {
        mSyncHandler.sendEmptyMessage(2);
    }
	
	public final synchronized void onAccountActivated(String s) {
        if(mSyncHelper.findUser(s) == null) {
            SQLiteDatabase sqlitedatabase = mSyncHelper.getWritableDatabase();
            UserEntry userentry = new UserEntry(s);
            UserEntry.SCHEMA.insertOrReplace(sqlitedatabase, userentry);
        }
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
	
	public final synchronized void onAccountDeactivated(String s) {
        SQLiteDatabase sqlitedatabase = UploadsDatabaseHelper.getInstance(mContext).getWritableDatabase();
        String as[] = {
            s
        };
        sqlitedatabase.delete(MediaRecordEntry.SCHEMA.getTableName(), "upload_account == ?", as);
    }
	
	public final void updateTasks(long l)
    {
        mSyncHandler.sendEmptyMessageDelayed(3, l);
    }
	
	public static final class SyncSession {

		public final String account;
        SyncTask mCurrentTask;
        boolean mSyncCancelled;
        public final SyncResult result;

        public SyncSession(String s, SyncResult syncresult)
        {
            account = s;
            result = syncresult;
        }
        
        public final synchronized void cancelSync() {
            mSyncCancelled = true;
            if(mCurrentTask != null) {
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
