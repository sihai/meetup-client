/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.content.SyncStats;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Utils;
import com.galaxy.meetup.client.util.AccountsUtil;


/**
 * 
 * @author sihai
 *
 */
public class PicasaSyncHelper {

	private static final String ALBUM_PROJECTION_ID_DATE[] = {
        "_id", "date_updated"
    };
    private static final String ALBUM_TABLE_NAME;
    private static final String PHOTO_TABLE_NAME;
    private static final String PROJECTION_ID_ACCOUNT[] = {
        "_id", "account"
    };
    private static final String PROJECTION_ID_DATE_INDEX[] = {
        "_id", "date_updated", "display_index"
    };
    private static final String USER_TABLE_NAME;
    private static PicasaSyncHelper sInstance;
    private Context mContext;
    private PicasaDatabaseHelper mDbHelper;
    private SyncLockManager mLockManager;

    static 
    {
        PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
        ALBUM_TABLE_NAME = AlbumEntry.SCHEMA.getTableName();
        USER_TABLE_NAME = UserEntry.SCHEMA.getTableName();
    }
	
    private PicasaSyncHelper(Context context) {
        mLockManager = new SyncLockManager();
        mContext = context.getApplicationContext();
        mDbHelper = PicasaDatabaseHelper.get(mContext);
    }
    
	public static synchronized PicasaSyncHelper getInstance(Context context) {
        PicasaSyncHelper picasasynchelper;
        if(sInstance == null)
            sInstance = new PicasaSyncHelper(context);
        picasasynchelper = sInstance;
        return picasasynchelper;
    }
	
	public final UserEntry findUser(String s)
    {
		Cursor cursor = null;
		
		try {
	        cursor = mDbHelper.getReadableDatabase().query(USER_TABLE_NAME, UserEntry.SCHEMA.getProjection(), "account=?", new String[] {
	            s
	        }, null, null, null);
	        if(null != cursor && cursor.moveToNext()) {
	        	return (UserEntry)UserEntry.SCHEMA.cursorToObject(cursor, new UserEntry());
	        }
	        return null;
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
    }
	
	public final SQLiteDatabase getWritableDatabase()
    {
        return mDbHelper.getWritableDatabase();
    }
	
	public final ArrayList getUsers()
    {
        Cursor cursor = null;
        ArrayList arraylist = new ArrayList();
        try {
        	cursor = mDbHelper.getReadableDatabase().query(USER_TABLE_NAME, UserEntry.SCHEMA.getProjection(), null, null, null, null, null);
        	while(cursor.moveToNext()) 
        		arraylist.add(UserEntry.SCHEMA.cursorToObject(cursor, new UserEntry()));
        	return arraylist;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
	
	public final void syncAccounts(String s)
    {
        HashMap hashmap = new HashMap();
        Cursor cursor = null;
        Log.d("gp.PicasaSync", "sync account database");
        SQLiteDatabase sqlitedatabase = mDbHelper.getWritableDatabase();
        try {
	        cursor = sqlitedatabase.query(USER_TABLE_NAME, PROJECTION_ID_ACCOUNT, null, null, null, null, null);
	        while(cursor.moveToNext()) 
	        {
	            String s1 = cursor.getString(0);
	            hashmap.put(cursor.getString(1), s1);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        Account aaccount[] = AccountManager.get(mContext).getAccountsByType(AccountsUtil.ACCOUNT_TYPE);
        Log.d("gp.PicasaSync", (new StringBuilder("accounts in DB=")).append(hashmap.size()).toString());
        if(aaccount != null)
        {
            boolean flag = PicasaFacade.get(mContext).isMaster();
            int i = aaccount.length;
            int j = 0;
            while(j < i) 
            {
                Account account = aaccount[j];
                boolean flag1;
                boolean flag2;
                if(hashmap.remove(account.name) != null)
                    flag1 = true;
                else
                    flag1 = false;
                if(ContentResolver.getIsSyncable(account, s) > 0)
                    flag2 = true;
                else
                    flag2 = false;
                if(flag && !flag1 && flag2)
                {
                    Log.d("gp.PicasaSync", (new StringBuilder("add account to DB:")).append(Utils.maskDebugInfo(account)).toString());
                    UserEntry.SCHEMA.insertOrReplace(mDbHelper.getWritableDatabase(), new UserEntry(account.name));
                }
                j++;
            }
        }
        if(!hashmap.isEmpty())
        {
            java.util.Map.Entry entry;
            for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext(); deleteUser(sqlitedatabase, (String)entry.getValue()))
            {
                entry = (java.util.Map.Entry)iterator.next();
                Log.d("gp.PicasaSync", (new StringBuilder("remove account:")).append(Utils.maskDebugInfo(entry.getKey())).toString());
            }

            notifyAlbumsChange();
            notifyPhotosChange();
            PicasaSyncManager.get(mContext).requestPrefetchSync();
        }
        return;
    }
	
	private void notifyAlbumsChange()
    {
        mContext.getContentResolver().notifyChange(PicasaFacade.get(mContext).getAlbumsUri(), null, false);
    }

    private void notifyPhotosChange()
    {
        mContext.getContentResolver().notifyChange(PicasaFacade.get(mContext).getPhotosUri(), null, false);
    }
	
	private static void deleteUser(SQLiteDatabase sqlitedatabase, String s)
    {
		Cursor cursor = null;
        String as[];
        try {
	        sqlitedatabase.beginTransaction();
	        as = (new String[] {
	            s
	        });
	        cursor = sqlitedatabase.query(ALBUM_TABLE_NAME, Entry.ID_PROJECTION, "user_id=?", as, null, null, null);
	        String as1[] = new String[1];
	        for(; cursor.moveToNext(); sqlitedatabase.delete(PHOTO_TABLE_NAME, "album_id=?", as1))
	            as1[0] = cursor.getString(0);
	        sqlitedatabase.delete(ALBUM_TABLE_NAME, "user_id=?", as);
	        sqlitedatabase.delete(USER_TABLE_NAME, "_id=?", as);
	        sqlitedatabase.setTransactionSuccessful();
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        	sqlitedatabase.endTransaction();
        }
    }

	public final class SyncContext
    {
		public PicasaApi api;
        private Account mAccount;
        private String mAuthToken;
        private volatile boolean mStopSync;
        private Thread mThread;
        public SyncResult result;

        public SyncContext(SyncResult syncresult, Thread thread)
        {
            super();
            result = (SyncResult)Utils.checkNotNull(syncresult);
            api = new PicasaApi(mContext.getContentResolver());
            mThread = thread;
        }
        
        public final void refreshAuthToken()
        {
            AccountManager accountmanager = AccountManager.get(mContext);
            if(mAuthToken != null)
                accountmanager.invalidateAuthToken(AccountsUtil.ACCOUNT_TYPE, mAuthToken);
            mAuthToken = null;
            try
            {
                mAuthToken = accountmanager.blockingGetAuthToken(mAccount, "lh2", true);
                api.setAuthToken(mAuthToken);
            }
            catch(Exception exception)
            {
                Log.w("gp.PicasaSync", "getAuthToken fail", exception);
            }
            if(mAuthToken == null)
            {
                Log.w("gp.PicasaSync", (new StringBuilder("cannot get auth token: ")).append(Utils.maskDebugInfo(mAccount.name)).toString());
                SyncStats syncstats = result.stats;
                syncstats.numAuthExceptions = 1L + syncstats.numAuthExceptions;
            }
        }

        public final boolean setAccount(String s)
        {
            if(mAccount == null || !mAccount.name.equals(s))
            {
                mAccount = new Account(s, AccountsUtil.ACCOUNT_TYPE);
                mAuthToken = null;
                refreshAuthToken();
            }
            boolean flag;
            if(mAuthToken != null)
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final void stopSync()
        {
            mStopSync = true;
            if(mThread != null)
                mThread.interrupt();
        }

        public final boolean syncInterrupted()
        {
            return mStopSync;
        }
    }
}
