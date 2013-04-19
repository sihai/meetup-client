/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class PicasaDatabaseHelper extends SQLiteOpenHelper {

	private static final String ALBUM_ENTRY_PROJECTION_LIST;
    private static final String ALBUM_ENTRY_QUERY;
    private static final String ALBUM_TABLE;
    private static final String USER_ACCOUNT_QUERY;
    private static final String USER_ID_QUERY;
    private static final String USER_TABLE;
    public static PicasaDatabaseHelper sInstance;
    private Context mContext;

    static {
        USER_TABLE = UserEntry.SCHEMA.getTableName();
        ALBUM_TABLE = AlbumEntry.SCHEMA.getTableName();
        USER_ID_QUERY = (new StringBuilder("select _id from ")).append(USER_TABLE).append(" where account").append("='%s' LIMIT 1").toString();
        ALBUM_ENTRY_PROJECTION_LIST = TextUtils.join(",", AlbumEntry.SCHEMA.getProjection());
        ALBUM_ENTRY_QUERY = (new StringBuilder("select ")).append(ALBUM_ENTRY_PROJECTION_LIST).append(" from ").append(ALBUM_TABLE).append(" where _id=(%s) LIMIT 1").toString();
        USER_ACCOUNT_QUERY = (new StringBuilder("select account")).append(" from ").append(USER_TABLE).append(" where _id=%s LIMIT 1").toString();
    }
    
    private PicasaDatabaseHelper(Context context) {
        super(context.getApplicationContext(), "picasa.db", null, 107);
        mContext = context.getApplicationContext();
    }

    public static synchronized PicasaDatabaseHelper get(Context context) {
        PicasaDatabaseHelper picasadatabasehelper;
        if(sInstance == null)
            sInstance = new PicasaDatabaseHelper(context);
        picasadatabasehelper = sInstance;
        return picasadatabasehelper;
    }
    
    final AlbumEntry getAlbumEntry(String s) {
        
        String s1 = String.format(ALBUM_ENTRY_QUERY, new Object[] {
            s
        });
        Cursor cursor = null;
        try {
        	cursor = getReadableDatabase().rawQuery(s1, null);
        	AlbumEntry albumentry = null;
	        if(cursor == null || !cursor.moveToNext()) {
	        	return null;
	        }
	        return (AlbumEntry)AlbumEntry.SCHEMA.cursorToObject(cursor, new AlbumEntry());
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    public final synchronized SQLiteDatabase getReadableDatabase() {
    	
    	try {
    		 return super.getReadableDatabase();
    	} catch (Throwable t) {
    		mContext.deleteDatabase("picasa.db");
    		return super.getReadableDatabase();
    	}
    }
    
    final String getUserAccount(long l) {
        
        String query = String.format(USER_ACCOUNT_QUERY, new Object[]{String.valueOf(l)});
        Cursor cursor = null;
        try {
        	cursor = getReadableDatabase().rawQuery(query, null);
        	if(null == cursor || !cursor.moveToNext()) {
        		return null;
        	}
        	return cursor.getString(0);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
	public final synchronized SQLiteDatabase getWritableDatabase() {

		try {
			return super.getWritableDatabase();
		} catch (Throwable t) {
			mContext.deleteDatabase("picasa.db");
			return super.getWritableDatabase();
		}
	}
	
	public final void onCreate(SQLiteDatabase sqlitedatabase) {
		PhotoEntry.SCHEMA.createTables(sqlitedatabase);
		AlbumEntry.SCHEMA.createTables(sqlitedatabase);
		UserEntry.SCHEMA.createTables(sqlitedatabase);
		PicasaSyncManager.get(mContext).requestAccountSync();
	}

	public final void onDowngrade(SQLiteDatabase sqlitedatabase, int i, int j) {
		onUpgrade(sqlitedatabase, i, j);
	}

	public final void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
		PhotoEntry.SCHEMA.dropTables(sqlitedatabase);
		AlbumEntry.SCHEMA.dropTables(sqlitedatabase);
		UserEntry.SCHEMA.dropTables(sqlitedatabase);
		onCreate(sqlitedatabase);
		PicasaSyncManager.get(mContext).requestMetadataSync(true);
	}
}
