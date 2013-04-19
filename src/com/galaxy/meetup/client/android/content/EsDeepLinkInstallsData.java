/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class EsDeepLinkInstallsData {

	private static final String PROJECTION[] = {
        "package_name", "name", "source_name", "embed_deep_link", "launch_source"
    };
	
	static void cleanupData(SQLiteDatabase sqlitedatabase)
    {
        int i = sqlitedatabase.delete("deep_link_installs", null, null);
        if(EsLog.isLoggable("DeepLinking", 3))
            Log.d("DeepLinking", (new StringBuilder("cleanupData deleted deep link installs: ")).append(i).toString());
    }
	
	public static DeepLinkInstall getByPackageName(Context context, EsAccount esaccount, String s)
    {
		Cursor cursor = null;
		try {
	        cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase().query("deep_link_installs_view", PROJECTION, "package_name=?", new String[] {
	            s
	        }, null, null, null, null);
	        if(null == cursor) {
	        	boolean flag = EsLog.isLoggable("DeepLinking", 5);
	            if(flag)
	            {
	                Log.w("DeepLinking", (new StringBuilder("no deep link install data found for ")).append(s).toString());
	            }
	            return null; 
	        } else {
	        	if(cursor.moveToFirst()) {
	        		return DeepLinkInstall.newInstance(cursor);
	        	}
	        }
	        return null;
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
    }
	
	public static void insert(Context context, EsAccount esaccount, String s, String s1, String s2, String s3)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
        contentvalues.put("package_name", s);
        contentvalues.put("launch_source", s3);
        contentvalues.put("activity_id", s1);
        contentvalues.put("author_id", s2);
        if(sqlitedatabase.replace("deep_link_installs", null, contentvalues) <= 0L && EsLog.isLoggable("DeepLinking", 5))
            Log.w("DeepLinking", (new StringBuilder("failed to add deep link install data for ")).append(s).toString());
    }

    public static void removeByPackageName(Context context, EsAccount esaccount, String s)
    {
        SQLiteDatabase sqlitedatabase;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        if(sqlitedatabase.delete("deep_link_installs", "package_name=?", new String[] {s}) <= 0 && EsLog.isLoggable("DeepLinking", 5))
	            Log.w("DeepLinking", (new StringBuilder("failed to delete deep link install data for ")).append(s).toString());
	        sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static void removeStaleEntries(Context context, EsAccount esaccount) {
    	
    	if(null == esaccount) {
    		return;
    	}
    	
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
    	try {
    		sqlitedatabase.beginTransaction();
    		long l = System.currentTimeMillis() - 0x36ee80L;
    		String as[] = new String[1];
    		as[0] = Long.toString(l);
    		int i = sqlitedatabase.delete("deep_link_installs", "timestamp<?", as);
    		if(i > 0 && EsLog.isLoggable("DeepLinking", 3))
    			Log.d("DeepLinking", (new StringBuilder()).append(i).append(" stale deep link install row(s) deleted").toString());
    		sqlitedatabase.setTransactionSuccessful();
    	} finally {
        	sqlitedatabase.endTransaction();
        }
    }
	
	public static final class DeepLinkInstall {

		public final String authorName;
        public final String creationSource;
        public final String data;
        public final String launchSource;
        public final String packageName;


        private DeepLinkInstall(String s, String s1, String s2, String s3, String s4)
        {
            authorName = s;
            creationSource = s1;
            packageName = s2;
            launchSource = s3;
            data = s4;
        }
        
        public final String toString()
        {
            Object aobj[] = new Object[5];
            aobj[0] = authorName;
            aobj[1] = creationSource;
            aobj[2] = packageName;
            aobj[3] = launchSource;
            aobj[4] = data;
            return String.format("DeepLinkInstall: authorName=%s, appName=%s, packageName=%s, launchSource=%s, data=%s", aobj);
        }
        
        static DeepLinkInstall newInstance(Cursor cursor)
        {
            String s = "";
            byte abyte0[] = cursor.getBlob(cursor.getColumnIndex("embed_deep_link"));
            if(abyte0 != null)
                s = DbEmbedDeepLink.deserialize(abyte0).getDeepLinkId();
            return new DeepLinkInstall(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("source_name")), cursor.getString(cursor.getColumnIndex("package_name")), cursor.getString(cursor.getColumnIndex("launch_source")), s);
        }

    }
}
