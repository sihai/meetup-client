/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class EsEmotiShareData {

	public static final String EMOTISHARE_PROJECTION[] = {
        "_id", "type", "data", "generation"
    };
    private static final Object sEmotiShareSyncLock = new Object();
    
    public static void cleanupData(SQLiteDatabase sqlitedatabase)
    {
        int i = sqlitedatabase.delete("emotishare_data", null, null);
        EsLog.writeToLog(3, "EsEmotiShareData", (new StringBuilder("cleanupData deleted EmotiShares: ")).append(i).toString());
    }

    private static boolean doSync(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate) {
    	
    	if(syncstate.isCanceled()) {
    		return false;
    	}
    	
        syncstate.onStart("EmotiShare");
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        cleanupData(sqlitedatabase);
	        sqlitedatabase.setTransactionSuccessful();
	        Resources resources = context.getResources();
	        int ai[] = resources.getIntArray(R.array.emotishare_id);
	        int ai1[] = resources.getIntArray(R.array.emotishare_release_generation);
	        String as[] = resources.getStringArray(R.array.emotishare_name);
	        String as1[] = resources.getStringArray(R.array.emotishare_type);
	        String as2[] = resources.getStringArray(R.array.emotishare_category);
	        String as3[] = resources.getStringArray(R.array.emotishare_share_text);
	        String as4[] = resources.getStringArray(R.array.emotishare_description);
	        String as5[] = resources.getStringArray(R.array.emotishare_icon_uri);
	        String as6[] = resources.getStringArray(R.array.emotishare_image_uri);
	        long l = System.currentTimeMillis();
	        long al[] = new long[3];
	        al[0] = Long.valueOf(Property.EMOTISHARE_GEN1_DATE.get()).longValue();
	        al[1] = Long.valueOf(Property.EMOTISHARE_GEN2_DATE.get()).longValue();
	        al[2] = Long.valueOf(Property.EMOTISHARE_GEN3_DATE.get()).longValue();
	        ArrayList arraylist = new ArrayList();
	        for(int i = 0; i < ai.length; i++)
	        {
	            ArrayList arraylist1 = new ArrayList();
	            arraylist1.add(as2[i]);
	            DbEmbedEmotishare dbembedemotishare = new DbEmbedEmotishare(as1[i], as[i], as6[i], as4[i]);
	            DbEmotishareMetadata dbemotisharemetadata = new DbEmotishareMetadata(ai[i], arraylist1, as3[i], as5[i], dbembedemotishare, ai1[i]);
	            int j = -1 + ai1[i];
	            if(j >= 0 && j < al.length && l >= al[j])
	                arraylist.add(dbemotisharemetadata);
	        }
	        syncstate.onFinish(insertEmotiShares(context, esaccount, arraylist));
	        return true;
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static boolean ensureSynced(Context context, EsAccount esaccount)
    {
        EsSyncAdapterService.SyncState syncstate = new EsSyncAdapterService.SyncState();
        syncstate.onSyncStart("Exp sync");
        boolean flag = syncAll(context, esaccount, syncstate, null, false);
        syncstate.onSyncFinish();
        return flag;
    }

    private static int insertEmotiShares(Context context, EsAccount esaccount, List list)
    {
        int i;
        long l;
        SQLiteDatabase sqlitedatabase;
        i = 0;
        l = System.currentTimeMillis();
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        DbEmotishareMetadata dbemotisharemetadata;
	        for(Iterator iterator = list.iterator(); iterator.hasNext(); EsLog.writeToLog(3, "EsEmotiShareData", (new StringBuilder("Insert: ")).append(dbemotisharemetadata).toString()))
	        {
	            dbemotisharemetadata = (DbEmotishareMetadata)iterator.next();
	            ContentValues contentvalues = toContentValues(dbemotisharemetadata);
	            if(contentvalues != null)
	            {
	                sqlitedatabase.insert("emotishare_data", null, contentvalues);
	                i++;
	            }
	        }
	        sqlitedatabase.setTransactionSuccessful();
	        saveSyncTimestamp(context, esaccount, l);
	        if(i != 0)
	            context.getContentResolver().notifyChange(EsProvider.EMOTISHARE_URI, null);
	        return i;
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static long querySyncTimestamp(Context context, EsAccount esaccount)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        try { 
        	return DatabaseUtils.longForQuery(sqlitedatabase, "SELECT last_emotishare_sync_time  FROM account_status", null);
        } catch (SQLiteDoneException sqlitedoneexception) {
        	// TODO log
        	return -1L;
        }
    }

    private static void saveSyncTimestamp(Context context, EsAccount esaccount, long l)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("last_emotishare_sync_time", Long.valueOf(l));
        sqlitedatabase.update("account_status", contentvalues, null, null);
        context.getContentResolver().notifyChange(EsProvider.ACCOUNT_STATUS_URI, null);
    }

    public static boolean syncAll(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, HttpOperation.OperationListener operationlistener, boolean flag)
    {
    	synchronized(sEmotiShareSyncLock) {
    		boolean flag1 = false;
    		if(!flag) {
		        long l = querySyncTimestamp(context, esaccount);
		        long l1 = System.currentTimeMillis() - l;
		        if(l1 > 0L && l1 < 60000L) {
		        	flag1 = true;
		        }
		        
		        flag1 = doSync(context, esaccount, syncstate);
		        if(flag1)
		            saveSyncTimestamp(context, esaccount, System.currentTimeMillis());
    		}
    		return flag1;
    	}
    }

    private static ContentValues toContentValues(DbEmotishareMetadata dbemotisharemetadata)
    {
        if(null == dbemotisharemetadata) {
        	return null;
        }
        
        DbEmbedEmotishare dbembedemotishare = dbemotisharemetadata.getEmbed();
        ContentValues contentvalues = null;
        if(dbembedemotishare == null) {
        	return null;
        }
        String s = dbembedemotishare.getType();
        if(TextUtils.isEmpty(s)) {
        	return null;
        }
        try {
	        byte abyte0[] = DbEmotishareMetadata.serialize(dbemotisharemetadata);
	        if(null == abyte0) {
	        	return null;
	        }
	        contentvalues = new ContentValues();
	        contentvalues.put("type", s);
	        contentvalues.put("data", abyte0);
	        contentvalues.put("generation", Integer.valueOf(dbemotisharemetadata.getGeneration()));
	        return contentvalues;
        } catch (IOException e) {
        	// TODO log
        	return null;
        }
    }
}