/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsDatabaseHelper;

/**
 * 
 * @author sihai
 *
 */
public class SearchUtils {

	public static String getContinuationToken(Context context, EsAccount esaccount, String s)
    {
        String s1 = null;
        if(s == null)
        	return s1;
        
        Cursor cursor = null;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        String as[] = {
            "continuation_token"
        };
        String as1[] = new String[1];
        as1[0] = getSearchKey(s);
        try {
        	cursor = sqlitedatabase.query("search", as, "search_key=?", as1, null, null, null, null);
        	if(!cursor.moveToFirst()) {
        		return null;
        	}
        	return cursor.getString(0);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
    }

    public static String getSearchKey(String s)
    {
        return (new StringBuilder("com.google.android.apps.plus.search_key-")).append(s).toString();
    }

    public static void insertSearchResults(Context context, EsAccount esaccount, String s, String s1)
    {
    	if(null == s) {
    		return;
    	}
    	
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
    	
    	try {
    		sqlitedatabase.beginTransaction();
            ContentValues contentvalues = new ContentValues(2);
            contentvalues.put("search_key", getSearchKey(s));
            contentvalues.put("continuation_token", s1);
            sqlitedatabase.insertWithOnConflict("search", null, contentvalues, 5);
            sqlitedatabase.setTransactionSuccessful();
    	} finally {
    		sqlitedatabase.endTransaction();
    	}
    }
}
