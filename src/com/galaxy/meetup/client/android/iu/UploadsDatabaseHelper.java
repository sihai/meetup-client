/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author sihai
 *
 */
public class UploadsDatabaseHelper extends SQLiteOpenHelper {

	private static UploadsDatabaseHelper sInstance;
    private Context mContext;
    
    private UploadsDatabaseHelper(Context context) {
        super(context.getApplicationContext(), "iu.upload.db", null, 7);
        mContext = context;
    }

    public static synchronized UploadsDatabaseHelper getInstance(Context context) {
        UploadsDatabaseHelper uploadsdatabasehelper;
        if(sInstance == null)
            sInstance = new UploadsDatabaseHelper(context);
        uploadsdatabasehelper = sInstance;
        return uploadsdatabasehelper;
    }

    public final synchronized SQLiteDatabase getReadableDatabase() {
    	
    	try {
    		return super.getReadableDatabase();
    	} catch (Throwable throwable) {
    		mContext.deleteDatabase("iu.upload.db");
    		return super.getReadableDatabase();
    	}
    }

    public final synchronized SQLiteDatabase getWritableDatabase() {
    	try {
    		return super.getWritableDatabase();
    	} catch (Throwable throwable) {
    		mContext.deleteDatabase("iu.upload.db");
    		return super.getWritableDatabase();
    	}
    }

    public final void onCreate(SQLiteDatabase sqlitedatabase) {
        UploadTaskEntry.SCHEMA.createTables(sqlitedatabase);
        MediaRecordEntry.SCHEMA.createTables(sqlitedatabase);
    }

    public final void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
        if(j == 4)
            InstantUploadProvider.disableInstantShare(mContext);
        UploadTaskEntry.SCHEMA.dropTables(sqlitedatabase);
        MediaRecordEntry.SCHEMA.dropTables(sqlitedatabase);
        if(i < 6)
            try
            {
                sqlitedatabase.execSQL("DROP TABLE media_map");
                sqlitedatabase.execSQL("DROP TABLE upload_records");
            }
            catch(SQLiteException sqliteexception) { }
        NewMediaTracker.clearPreferences(mContext);
        onCreate(sqlitedatabase);
    }

    public final void reset() {
        SQLiteDatabase sqlitedatabase = getWritableDatabase();
        sqlitedatabase.delete(UploadTaskEntry.SCHEMA.getTableName(), null, null);
        sqlitedatabase.delete(MediaRecordEntry.SCHEMA.getTableName(), null, null);
        NewMediaTracker.clearPreferences(mContext);
    }

}
