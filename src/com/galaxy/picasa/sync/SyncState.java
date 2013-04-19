/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author sihai
 *
 */
public class SyncState {

	public static final SyncState METADATA = new SyncState(0);
    public static final SyncState METADATA_MANUAL = new SyncState(1);
    public static final SyncState PREFETCH_ALBUM_COVER = new SyncState(4);
    public static final SyncState PREFETCH_FULL_IMAGE = new SyncState(3);
    public static final SyncState PREFETCH_SCREEN_NAIL = new SyncState(2);
    private static final String STATE_PROJECTION[] = {
        "sync_states"
    };
    private static final String USER_TABLE_NAME;
    private static final ContentValues sValues = new ContentValues();
    private static final String sWhereArgs[] = new String[1];
    private final int mOffset;

    static {
        USER_TABLE_NAME = UserEntry.SCHEMA.getTableName();
    }
    
    private SyncState(int i) {
        mOffset = i * 2;
    }
    
    private synchronized boolean compareAndSet(SQLiteDatabase sqlitedatabase, String s, int i, int j) {
        int k = getStates(sqlitedatabase, s);
        if(k == -1 || (3 & k >> mOffset) != i) {
        	return false;
        }
        
        writeStates(sqlitedatabase, s, k, j);
        return true;
    }

    private static int getStates(SQLiteDatabase sqlitedatabase, String s) {
        Cursor cursor = null;
        sWhereArgs[0] = s;
        try {
        	cursor = sqlitedatabase.query(USER_TABLE_NAME, STATE_PROJECTION, "account=?", sWhereArgs, null, null, null, "1");
        	if(cursor.moveToNext()) {
        		return cursor.getInt(0);
        	}
        	return -1;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private void writeStates(SQLiteDatabase sqlitedatabase, String s, int i, int j) {
        sWhereArgs[0] = s;
        int k = i & (-1 ^ 3 << mOffset) | j << mOffset;
        sValues.put("sync_states", Integer.valueOf(k));
        try {
	        sqlitedatabase.beginTransaction();
	        sqlitedatabase.update(USER_TABLE_NAME, sValues, "account=?", sWhereArgs);
	        sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public final int getState(SQLiteDatabase sqlitedatabase, String s) {
        return 3 & getStates(sqlitedatabase, s) >> mOffset;
    }

    public final synchronized boolean isRequested(SQLiteDatabase sqlitedatabase, String s) {
        int i = getStates(sqlitedatabase, s);
        return !(i == -1 || (3 & i >> mOffset) != 2);
    }

    public final void onSyncFinish(SQLiteDatabase sqlitedatabase, String s) {
        compareAndSet(sqlitedatabase, s, 1, 0);
    }

    public final synchronized boolean onSyncRequested(SQLiteDatabase sqlitedatabase, String s) {
        int i = getStates(sqlitedatabase, s);
        if(i == -1 || (3 & i >> mOffset) == 2) {
        	return false;
        }
      
        writeStates(sqlitedatabase, s, i, 2);
        return true;
    }

    public final boolean onSyncStart(SQLiteDatabase sqlitedatabase, String s) {
        return compareAndSet(sqlitedatabase, s, 2, 1);
    }

    public final void resetSyncToDirty(SQLiteDatabase sqlitedatabase, String s) {
        compareAndSet(sqlitedatabase, s, 1, 2);
    }
}
