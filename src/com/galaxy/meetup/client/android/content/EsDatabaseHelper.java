/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.File;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.RingtoneUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsDatabaseHelper extends SQLiteOpenHelper {

	private static final String MASTER_COLUMNS[] = {
        "name"
    };
    private static boolean sAlarmsInitialized;
    private static SparseArray<EsDatabaseHelper> sHelpers = new SparseArray<EsDatabaseHelper>();
    private static Object _s_lock_ = new Object();
    private static volatile long sLastDatabaseDeletionTimestamp;
    private final Context mContext;
    private volatile boolean mDeleted;
    private int mIndex;
    
	//===========================================================================
    //						Constructor
    //===========================================================================
	private EsDatabaseHelper(Context context, int i) {
        super(context, (new StringBuilder("es")).append(i).append(".db").toString(), null, 1221);
        mContext = context;
        mIndex = i;
    }
	
	private synchronized void doDeleteDatabase() {
		if(mDeleted) {
			return;
		} else {
			SQLiteDatabase sqlitedatabase = getWritableDatabase();
			// ×î¶à³¢ÊÔ3´Î
			for(int i = 0; i < 3; i++) {
				try {
					sqlitedatabase.beginTransaction();
			        mDeleted = true;
			        sLastDatabaseDeletionTimestamp = System.currentTimeMillis();
			        sqlitedatabase.endTransaction();
			        sqlitedatabase.close();
			        (new File(sqlitedatabase.getPath())).delete();
			        break;
				} catch (Throwable t) {
					Log.e("EsDatabaseHelper", "Cannot close database", t);
				}
			}
		}
    }

    private static void dropAllViews(SQLiteDatabase sqlitedatabase) {
        Cursor cursor = null;
        try {
        	String name = null;
        	cursor = sqlitedatabase.query("sqlite_master", MASTER_COLUMNS, "type='view'", null, null, null, null);
        	do {
                if(!cursor.moveToNext())
                    break;
                name = cursor.getString(0);
                sqlitedatabase.execSQL((new StringBuilder("DROP VIEW IF EXISTS ")).append(name).toString());
            } while(true);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
	
	public static EsDatabaseHelper getDatabaseHelper(Context context, int i) {
        if(context == null)
        	throw new NullPointerException("Context is null");
        
        if(i < 0)
        	throw new IllegalArgumentException((new StringBuilder("Invalid account index: ")).append(i).toString());
        
        EsDatabaseHelper esdatabasehelper;
        synchronized(_s_lock_) {
        	esdatabasehelper = sHelpers.get(i);
	        if(esdatabasehelper == null) {
	            esdatabasehelper = new EsDatabaseHelper(context, i);
	            sHelpers.put(i, esdatabasehelper);
	        }
	        if(!sAlarmsInitialized) {
	            EsService.scheduleUnconditionalSyncAlarm(context);
	            EsService.scheduleSyncAlarm(context);
	            sAlarmsInitialized = true;
	        }
        }
        return esdatabasehelper;
    }
	
	public static EsDatabaseHelper getDatabaseHelper(Context context, EsAccount esaccount) {
        return getDatabaseHelper(context, esaccount.getIndex());
    }
	
	static long getRowsCount(SQLiteDatabase sqlitedatabase, String s, String s1, String as[]) {
		Cursor cursor = null;
		try {
			cursor = sqlitedatabase.query(s, new String[] {"COUNT(*)"}, null, null, null, null, null);
			if(!cursor.moveToFirst()) 
				return 0L; 
			else 
				return  cursor.getLong(0);
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
    }
	
	public static boolean isDatabaseRecentlyDeleted() {
		
		if(0L == sLastDatabaseDeletionTimestamp) {
			return false;
		} else {
			if(System.currentTimeMillis() - sLastDatabaseDeletionTimestamp < 60000L) {
				return true;
			} else {
				return false;
			}
		}
    }

	private void rebuildTables(SQLiteDatabase sqlitedatabase) {
        rebuildTables(sqlitedatabase, EsAccountsData.getActiveAccount(mContext));
    }
	
	private static void upgradeViews(SQLiteDatabase sqlitedatabase) {
        if(EsLog.isLoggable("EsDatabaseHelper", 3))
            Log.d("EsDatabaseHelper", "Upgrade database views");
        String as[] = EsProvider.getViewNames();
        for(int i = 0; i < as.length; i++)
            sqlitedatabase.execSQL((new StringBuilder("DROP VIEW IF EXISTS ")).append(as[i]).toString());

        String as1[] = EsProvider.getViewSQLs();
        for(int j = 0; j < as1.length; j++)
            sqlitedatabase.execSQL(as1[j]);

    }

	public final void createNewDatabase() {
        mDeleted = false;
    }
	
	public final void deleteDatabase() {
        (new AsyncTask() {

            protected final Object doInBackground(Object aobj[]) {
                doDeleteDatabase();
                return null;
            }

        }).execute(new Void[] {null});
    }
	
	public final synchronized SQLiteDatabase getReadableDatabase() {
        if(mDeleted)
            throw new SQLiteException("Database deleted");
        return super.getReadableDatabase();
    }
	
	public final SQLiteDatabase getWritableDatabase() {
		if(mDeleted)
            throw new SQLiteException("Database deleted");
        return super.getWritableDatabase();
    }
	
	public final void rebuildTables(SQLiteDatabase sqlitedatabase, EsAccount esaccount) {
        Cursor cursor = null;
        try {
        	String name = null;
        	cursor = sqlitedatabase.query("sqlite_master", MASTER_COLUMNS, "type='table'", null, null, null, null);
        	do {
                if(!cursor.moveToNext())
                    break;
                name = cursor.getString(0);
                if(!name.startsWith("android_") && !name.startsWith("sqlite_"))
                    sqlitedatabase.execSQL((new StringBuilder("DROP TABLE IF EXISTS ")).append(name).toString());
            } while(true);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        dropAllViews(sqlitedatabase);
        onCreate(sqlitedatabase);
        if(esaccount != null) {
            String s = esaccount.getGaiaId();
            sqlitedatabase.execSQL((new StringBuilder("UPDATE account_status SET user_id='")).append(s).append("' WHERE ").append("user_id IS NULL").toString());
        }
        return;
    }
	
	@Override
	public void onCreate(SQLiteDatabase sqlitedatabase) {
		String as[] = EsProvider.getTableSQLs();
        for(int i = 0; i < as.length; i++)
            sqlitedatabase.execSQL(as[i]);

        String as1[] = EsProvider.getIndexSQLs();
        for(int j = 0; j < as1.length; j++)
            sqlitedatabase.execSQL(as1[j]);

        String as2[] = EsProvider.getViewSQLs();
        for(int k = 0; k < as2.length; k++)
            sqlitedatabase.execSQL(as2[k]);

        EsProvider.insertVirtualCircles(mContext, sqlitedatabase);
        RingtoneUtils.registerHangoutRingtoneIfNecessary(mContext);
	}
	
	@Override
	public final void onDowngrade(SQLiteDatabase sqlitedatabase, int i, int j) {
        rebuildTables(sqlitedatabase);
    }
	
	@Override
	public final void onOpen(SQLiteDatabase sqlitedatabase) {
        if(!sqlitedatabase.isReadOnly())
            sqlitedatabase.execSQL("PRAGMA foreign_keys=ON;");
    }
	
	@Override
	public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
		if(EsLog.isLoggable("EsDatabaseHelper", 3))
            Log.d("EsDatabaseHelper", (new StringBuilder("Upgrade database: ")).append(i).append(" --> ").append(j).toString());
		
		try {
			if(j >= i) {
				if(i < 756) {
					rebuildTables(sqlitedatabase);
			        EsAccountsData.onAccountUpgradeRequired(mContext, mIndex);
			        EsAccount esaccount3 = EsAccountsData.getActiveAccountUnsafe(mContext);
			        if(esaccount3 != null)
			            ContentResolver.requestSync(AccountsUtil.newAccount(esaccount3.getName()), "com.galaxy.meetup.client.android.content.EsProvider", new Bundle());
				} else if(i < 911) {
					RingtoneUtils.registerHangoutRingtoneIfNecessary(mContext);
			        i = 911;
			        rebuildTables(sqlitedatabase);
			        upgradeViews(sqlitedatabase);
			        EsAccount esaccount2 = EsAccountsData.getActiveAccountUnsafe(mContext);
			        if(esaccount2 != null)
			            ContentResolver.requestSync(AccountsUtil.newAccount(esaccount2.getName()), "com.galaxy.meetup.client.android.content.EsProvider", new Bundle());
				}
			} else {
				 rebuildTables(sqlitedatabase);
			     EsAccount esaccount4 = EsAccountsData.getActiveAccountUnsafe(mContext);
			     if(esaccount4 != null)
			            ContentResolver.requestSync(AccountsUtil.newAccount(esaccount4.getName()), "com.galaxy.meetup.client.android.content.EsProvider", new Bundle());
			}
		} catch (SQLiteException sqliteexception) {
			if(EsLog.isLoggable("EsDatabaseHelper", 6))
	            Log.e("EsDatabaseHelper", (new StringBuilder("Failed to upgrade database: ")).append(i).append(" --> ").append(j).toString(), sqliteexception);
	        rebuildTables(sqlitedatabase);
	        EsAccount esaccount1 = EsAccountsData.getActiveAccountUnsafe(mContext);
	        if(esaccount1 != null)
	            ContentResolver.requestSync(AccountsUtil.newAccount(esaccount1.getName()), "com.galaxy.meetup.client.android.content.EsProvider", new Bundle());
		} catch (Exception e) {
			EsAccount esaccount = EsAccountsData.getActiveAccountUnsafe(mContext);
	        if(esaccount != null)
	            ContentResolver.requestSync(AccountsUtil.newAccount(esaccount.getName()), "com.galaxy.meetup.client.android.content.EsProvider", new Bundle());
		}
	}
}
