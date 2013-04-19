/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.DownloadImageOperationNoCache;
import com.galaxy.meetup.client.android.api.GetActivitiesOperation;
import com.galaxy.meetup.client.android.api.GetNearbyActivitiesOperation;
import com.galaxy.meetup.client.android.api.LocationQuery;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.Comment;
import com.galaxy.meetup.server.client.domain.Update;

/**
 * 
 * @author sihai
 *
 */
public class EsPostsData {

	private static final String ACTIVITY_TIMESTAMP_AND_STATUS_COLUMNS[] = {
        "activity_id", "modified", "data_state"
    };
    private static List sEmbedsWhitelist;
    private static boolean sInitialized;
    private static int sLargePlayerSize;
    private static Integer sMaxContentLength;
    private static float sMaxPortraitAspectRatio;
    private static float sMinLandscapeAspectRatio;
    private static ArrayList sMixinsWhitelist;
    private static ArrayList sMixinsWithPopularWhitelist;
    private static ArrayList sShareboxWhitelist;
    private static ArrayList sStreamNamespaces;
    private static boolean sSyncEnabled = true;
    private static final Object sSyncLock = new Object();
    private static ArrayList sWidgetStreamNamespaces;
    
    public static String buildActivitiesStreamKey(String s, String s1, DbLocation dblocation, boolean flag, int i)
    {
        return buildStreamKey(s, s1, dblocation, flag, null, null, i);
    }

    public static String buildSquareStreamKey(String s, String s1, boolean flag)
    {
        return buildStreamKey(null, null, null, false, s, s1, 0);
    }

    private static String buildStreamKey(String s, String s1, DbLocation dblocation, boolean flag, String s2, String s3, int i)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if(!TextUtils.isEmpty(s1) && s1.startsWith("f."))
            s1 = s1.substring(2);
        stringbuilder.append(s1);
        stringbuilder.append('|');
        stringbuilder.append(s);
        stringbuilder.append('|');
        int j;
        if(dblocation != null)
        {
            if(dblocation.hasCoordinates())
            {
                stringbuilder.append(dblocation.getLatitudeE7());
                stringbuilder.append(',');
                stringbuilder.append(dblocation.getLongitudeE7());
                stringbuilder.append(',');
                stringbuilder.append((int)dblocation.getPrecisionMeters());
            }
        } else
        {
            stringbuilder.append("null");
        }
        stringbuilder.append('|');
        if(flag)
            j = 1;
        else
            j = 0;
        stringbuilder.append(j);
        stringbuilder.append('|');
        stringbuilder.append(s2);
        stringbuilder.append('|');
        stringbuilder.append(s3);
        stringbuilder.append('|');
        stringbuilder.append(i);
        return stringbuilder.toString();
    }

    static void cleanupData(SQLiteDatabase sqlitedatabase)
    {
        if(sSyncEnabled)
        {
            String s = buildActivitiesStreamKey(null, null, null, false, 0);
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("stream_key NOT IN(");
            stringbuilder.append(DatabaseUtils.sqlEscapeString(s));
            stringbuilder.append(')');
            int i = sqlitedatabase.delete("activity_streams", stringbuilder.toString(), null);
            if(EsLog.isLoggable("EsPostsData", 3))
                Log.d("EsPostsData", (new StringBuilder("deleteNonEssentialStreams deleted streams: ")).append(i).toString());
            int j = sqlitedatabase.delete("activities", "activity_id NOT IN (SELECT activity_id FROM activity_streams)", null);
            if(EsLog.isLoggable("EsPostsData", 3))
                Log.d("EsPostsData", (new StringBuilder("cleanupData deleted unreferenced activities: ")).append(j).toString());
            if(getAvailableStorage() < 0xf42400L)
            {
                int k = sqlitedatabase.delete("activities", "activity_id IN (SELECT activity_id FROM activity_streams WHERE sort_index > 50)", null);
                if(EsLog.isLoggable("EsPostsData", 3))
                    Log.d("EsPostsData", (new StringBuilder("cleanupData deleted \"all circles\" activities: ")).append(k).toString());
            }
            deleteUnusedLocations(sqlitedatabase);
        }
    }
    
    private static void createCommentValues(Comment comment, String s, ContentValues contentvalues) {
        contentvalues.clear();
        String s1 = comment.commentId;
        contentvalues.put("activity_id", s);
        contentvalues.put("comment_id", s1);
        contentvalues.put("author_id", comment.obfuscatedId);
        contentvalues.put("content", comment.text);
        contentvalues.put("created", comment.timestamp);
        byte abyte0[] = null;
        if(comment.plusone == null) 
        	abyte0 = null;
        else 
        	try {
        		abyte0 = DbPlusOneData.serialize(comment.plusone);
        	}  catch (IOException e) {
        		contentvalues.putNull("plus_one_data");
        	}
        	
        contentvalues.put("plus_one_data", abyte0);
    }
    
    public static void deleteActivity(Context context, EsAccount esaccount, String s) {
        SQLiteDatabase sqlitedatabase;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>>> deleteActivity id: ")).append(s).toString());
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
	    try {
	        sqlitedatabase.beginTransaction();
	        List list = getActivityStreams(sqlitedatabase, s);
	        String as[] = {
	            s
	        };
	        sqlitedatabase.delete("activity_streams", "activity_id=?", as);
	        sqlitedatabase.delete("activities", "activity_id=?", as);
	        sqlitedatabase.setTransactionSuccessful();
	        sqlitedatabase.endTransaction();
	        ContentResolver contentresolver = context.getContentResolver();
	        for(Iterator iterator = list.iterator(); iterator.hasNext(); contentresolver.notifyChange(EsProvider.buildStreamUri(esaccount, (String)iterator.next()), null));
	    } finally {
	    	sqlitedatabase.endTransaction();
	    }
    }

    public static void deleteActivityStream(Context context, EsAccount esaccount, String s) {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("stream_key IN(");
        stringbuilder.append(DatabaseUtils.sqlEscapeString(s));
        stringbuilder.append(')');
        int i = sqlitedatabase.delete("activity_streams", stringbuilder.toString(), null);
        Uri uri = EsProvider.buildStreamUri(esaccount, s);
        context.getContentResolver().notifyChange(uri, null);
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder("deleteActivityStream deleted streams: ")).append(i).toString());
    }

    public static void deleteComment(Context context, EsAccount esaccount, String s)
    {
        SQLiteDatabase sqlitedatabase;
        String as[];
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        as = (new String[] {
            s
        });
        String s1 = DatabaseUtils.stringForQuery(sqlitedatabase, "SELECT activity_id FROM activity_comments WHERE comment_id = ?", as);
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>> deleteComment: ")).append(s).append(" for activity: ").append(s1).toString());
        sqlitedatabase.beginTransaction();
        StringBuffer stringbuffer = new StringBuffer(256);
        stringbuffer.append("comment_id IN(");
        stringbuffer.append(DatabaseUtils.sqlEscapeString(s));
        stringbuffer.append(')');
        try {
	        sqlitedatabase.delete("activity_comments", stringbuffer.toString(), null);
	        updateTotalCommentCountInTransaction(sqlitedatabase, s1, -1);
	        sqlitedatabase.setTransactionSuccessful();
	        sqlitedatabase.endTransaction();
	        if(s1 != null)
	            notifyActivityChange(sqlitedatabase, context, esaccount, s1);
        } catch (SQLiteDoneException e) {
        	 if(EsLog.isLoggable("EsPostsData", 5))
                 Log.w("EsPostsData", (new StringBuilder("WARNING: could not find photo for the comment: ")).append(s).toString());
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    private static void deleteUnusedLocations(SQLiteDatabase sqlitedatabase) {
    	Cursor cursor = null;
    	try {
    		cursor = sqlitedatabase.query("location_queries", new String[] {
    				"_id"
    		}, null, null, null, null, "_id DESC");
    		if(null == cursor || cursor.getCount() <= 1) {
    			return;
    		}
    		cursor.moveToFirst();
    		cursor.moveToNext();
    		StringBuilder stringbuilder = new StringBuilder();
    	    stringbuilder.append("_id IN(");
    	    stringbuilder.append(cursor.getLong(0));
    	    while(cursor.moveToNext()) {
    	    	stringbuilder.append(',');
    	    	stringbuilder.append(cursor.getLong(0));
    	    }
    	    stringbuilder.append(')');
    	    sqlitedatabase.delete("location_queries", stringbuilder.toString(), null);
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    }

    public static ServiceResult doActivityStreamSync(Context context, EsAccount esaccount, int i, String s, String s1, String s2, boolean flag, String s3, 
            int j, HttpOperation.OperationListener operationlistener, EsSyncAdapterService.SyncState syncstate)
        throws Exception
    {
        if(EsLog.isLoggable("EsPostsData", 3))
        {
            String s4 = buildActivitiesStreamKey(s1, s, null, false, i);
            Log.d("EsPostsData", (new StringBuilder("doActivityStreamSync starting sync stream: ")).append(s4).append(", count: ").append(j).toString());
        }
        GetActivitiesOperation getactivitiesoperation = new GetActivitiesOperation(context, esaccount, i, s, s1, s2, flag, s3, j, syncstate, null, operationlistener);
        getactivitiesoperation.start();
        if(getactivitiesoperation.getException() != null)
            throw getactivitiesoperation.getException();
        if(getactivitiesoperation.hasError())
            throw new IOException((new StringBuilder("Error: ")).append(getactivitiesoperation.getErrorCode()).append(" [").append(getactivitiesoperation.getReasonPhrase()).append("]").toString());
        else
            return new ServiceResult(getactivitiesoperation);
    }

    public static ServiceResult doNearbyActivitiesSync(Context context, EsAccount esaccount, DbLocation dblocation, String s, int i, HttpOperation.OperationListener operationlistener, EsSyncAdapterService.SyncState syncstate)
        throws Exception
    {
        if(EsLog.isLoggable("EsPostsData", 3))
        {
            String s1 = buildActivitiesStreamKey(null, null, dblocation, false, 2);
            Log.d("EsPostsData", (new StringBuilder("doNearbyActivitiesSync starting sync stream: ")).append(s1).append(", count: ").append(i).toString());
        }
        GetNearbyActivitiesOperation getnearbyactivitiesoperation = new GetNearbyActivitiesOperation(context, esaccount, dblocation, s, i, syncstate, null, null);
        getnearbyactivitiesoperation.start();
        if(getnearbyactivitiesoperation.getException() != null)
            throw getnearbyactivitiesoperation.getException();
        if(getnearbyactivitiesoperation.hasError())
            throw new IOException((new StringBuilder("Error: ")).append(getnearbyactivitiesoperation.getErrorCode()).append(" [").append(getnearbyactivitiesoperation.getReasonPhrase()).append("]").toString());
        else
            return new ServiceResult(getnearbyactivitiesoperation);
    }

    public static Bitmap getActivityImageData(Context context, EsAccount esaccount, String s)
    {
    	Cursor cursor = null;
    	
    	try {
	        cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("activities", new String[] {
	            "embed_media"
	        }, "activity_id=?", new String[] {
	            s
	        }, null, null, null);
	        
	        if(null == cursor || !cursor.moveToFirst() || cursor.isNull(0)) {
	        	return null;
	        }
	        
	        byte abyte0[] = cursor.getBlob(0);
	        DbEmbedMedia dbembedmedia = DbEmbedMedia.deserialize(abyte0);
	        if(dbembedmedia == null || TextUtils.isEmpty(dbembedmedia.getImageUrl())) {
	        	return null;
	        }
	        Resources resources = context.getResources();
	        int i = (int)resources.getDimension(R.dimen.notification_bigpicture_width);
	        int j = (int)resources.getDimension(R.dimen.notification_bigpicture_width);
	        DownloadImageOperationNoCache downloadimageoperationnocache = new DownloadImageOperationNoCache(context, esaccount, (new MediaImageRequest(dbembedmedia.getImageUrl(), 3, i, j, true)).getDownloadUrl(), null, null);
	        downloadimageoperationnocache.start();
	        if(downloadimageoperationnocache.getBitmap() == null) {
	        	return null;
	        }
	        return downloadimageoperationnocache.getBitmap();
	        
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    }
    
    public static void insertLocations(Context context, EsAccount esaccount, LocationQuery locationquery, DbLocation dblocation, DbLocation dblocation1, List arraylist) throws IOException {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        String s = locationquery.getKey();
        sqlitedatabase.delete("location_queries", "key=?", new String[] {
            s
        });
        ArrayList arraylist1 = new ArrayList();
        if(dblocation != null)
            arraylist1.add(dblocation);
        if(dblocation1 != null)
            arraylist1.add(dblocation1);
        arraylist1.addAll(arraylist);
        if(arraylist1.size() <= 0) {
        	return;
        }
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("key", s);
        long l = sqlitedatabase.insertOrThrow("location_queries", "key", contentvalues);
        if(l < 0L)
            return;
        try {
            sqlitedatabase.beginTransaction();
            int i = arraylist1.size();
            for(int j = 0; j < i; j++)
            {
                DbLocation dblocation2 = (DbLocation)arraylist1.get(j);
                contentvalues.clear();
                contentvalues.put("qrid", Long.valueOf(l));
                contentvalues.put("name", dblocation2.getLocationName());
                contentvalues.put("location", DbLocation.serialize(dblocation2));
                sqlitedatabase.insertOrThrow("locations", "qrid", contentvalues);
            }

            sqlitedatabase.setTransactionSuccessful();
            
            Uri uri = EsProvider.buildLocationQueryUri(esaccount, locationquery.getKey());
            context.getContentResolver().notifyChange(uri, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static long getActivityLastEditedTime(Update update)
    {
        return Math.max(PrimitiveUtils.safeLong(update.updatedTimestampUsec) / 1000L, update.timestamp.longValue());
    }

    private static long getActivityLastModifiedTime(Update update)
    {
        long l = getActivityLastEditedTime(update);
        if(update.comment != null)
        {
            for(Iterator iterator = update.comment.iterator(); iterator.hasNext();)
            {
                Comment comment = (Comment)iterator.next();
                l = Math.max(l, Math.max(PrimitiveUtils.safeLong(comment.updatedTimestampUsec), comment.timestamp.longValue()));
            }

        }
        if(update.plusone != null)
            l = Math.max(l, (long)PrimitiveUtils.safeDouble(update.plusone.timeModifiedMs));
        return l;
    }

    private static HashMap getActivityStatuses(SQLiteDatabase sqlitedatabase, List list)
    {
        Cursor cursor = null;
        StringBuilder stringbuilder = new StringBuilder();
        ArrayList arraylist = new ArrayList();
        stringbuilder.append("activity_id IN (");
        Update update;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(update.updateId))
        {
            update = (Update)iterator.next();
            stringbuilder.append("?,");
        }

        stringbuilder.setLength(-1 + stringbuilder.length());
        stringbuilder.append(")");
        HashMap hashmap = new HashMap();
        
        try {
	        cursor = sqlitedatabase.query("activities", ACTIVITY_TIMESTAMP_AND_STATUS_COLUMNS, stringbuilder.toString(), (String[])arraylist.toArray(new String[0]), null, null, null);
	        while(cursor.moveToNext()) 
	        {
	            String s = cursor.getString(0);
	            ActivityStatus activitystatus = new ActivityStatus();
	            activitystatus.timestamp = cursor.getLong(1);
	            activitystatus.dataStatus = cursor.getInt(2);
	            hashmap.put(s, activitystatus);
	        }
	        return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static List getActivityStreams(SQLiteDatabase sqlitedatabase, String s)
    {
        Cursor cursor = null;
        ArrayList arraylist = new ArrayList();
        try {
	        cursor = sqlitedatabase.query(true, "activity_streams", ActivityStreamKeyQuery.PROJECTION, "activity_id=?", new String[] {
	            s
	        }, null, null, null, null);
	        if(null != cursor) {
	        	for(; cursor.moveToNext(); arraylist.add(cursor.getString(0)));
	        }
	        return arraylist;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static long getAvailableStorage()
    {
        long l;
        try
        {
            StatFs statfs = new StatFs(Environment.getDataDirectory().getPath());
            l = (long)statfs.getAvailableBlocks() * (long)statfs.getBlockSize();
            if(EsLog.isLoggable("EsPostsData", 3))
                Log.d("EsPostsData", (new StringBuilder("getAvailableStorage: ")).append(l).toString());
        }
        catch(Exception exception)
        {
            Log.e("EsPostsData", "getAvailableStorage", exception);
            l = -1L;
        }
        return l;
    }

    private static DbPlusOneData getCommentPlusOneData(SQLiteDatabase sqlitedatabase, String s)
    {
        Cursor cursor = null;
        try {
	        cursor = sqlitedatabase.query("activity_comments", new String[] {
	            "plus_one_data"
	        }, "comment_id=?", new String[] {
	            s
	        }, null, null, null);
	        
	        if(null == cursor || !cursor.moveToFirst() || cursor.isNull(0)) {
	        	return null;
	        }
	        
	        return DbPlusOneData.deserialize(cursor.getBlob(0));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static int getDefaultText(long l)
    {
        int i;
        if((4096L & l) != 0L)
            i = R.string.card_auto_text_event;
        else
        if((16384L & l) != 0L)
            i = R.string.card_auto_text_skyjam;
        else
        if((64L & l) != 0L)
            i = R.string.card_auto_text_album;
        else
        if((128L & l) != 0L)
            i = R.string.card_auto_text_video;
        else
        if((32772L & l) != 0L)
            i = R.string.card_auto_text_link;
        else
        if((32L & l) != 0L)
            i = R.string.card_auto_text_image;
        else
        if((0x400000L & l) != 0L)
            i = R.string.card_auto_text_emotishare;
        else
        if((8L & l) != 0L)
            i = R.string.card_auto_text_location;
        else
        if((0x10000L & l) != 0L)
            i = R.string.card_auto_text_review;
        else
            i = 0;
        return i;
    }

    public static List getEmbedsWhitelist()
    {
        if(sEmbedsWhitelist == null)
        {
            ArrayList arraylist = new ArrayList();
            sEmbedsWhitelist = arraylist;
            arraylist.add("SQUARE");
            sEmbedsWhitelist.add("SQUARE_INVITE");
            sEmbedsWhitelist.add("APP_INVITE");
            sEmbedsWhitelist.add("WEB_PAGE");
            sEmbedsWhitelist.add("PLUS_PHOTO");
            sEmbedsWhitelist.add("PLUS_PHOTO_ALBUM");
            sEmbedsWhitelist.add("VIDEO_OBJECT");
            sEmbedsWhitelist.add("CHECKIN");
            sEmbedsWhitelist.add("PLACE_REVIEW");
            sEmbedsWhitelist.add("PLUS_PHOTOS_ADDED_TO_COLLECTION");
            sEmbedsWhitelist.add("PLUS_EVENT");
            sEmbedsWhitelist.add("PLAY_MUSIC_TRACK");
            sEmbedsWhitelist.add("PLAY_MUSIC_ALBUM");
            sEmbedsWhitelist.add("HANGOUT_CONSUMER");
            sEmbedsWhitelist.add("EMOTISHARE");
            sEmbedsWhitelist.add("THING");
        }
        return sEmbedsWhitelist;
    }

    public static ArrayList getMixinsWhitelist(boolean flag)
    {
        ArrayList arraylist1;
        if(flag)
        {
            if(sMixinsWithPopularWhitelist == null)
            {
                ArrayList arraylist2 = new ArrayList();
                sMixinsWithPopularWhitelist = arraylist2;
                arraylist2.add("POPULAR_RECOMMENDATIONS");
                sMixinsWithPopularWhitelist.add("SQUARES");
                sMixinsWithPopularWhitelist.add("BIRTHDAYS");
            }
            arraylist1 = sMixinsWithPopularWhitelist;
        } else
        {
            if(sMixinsWhitelist == null)
            {
                ArrayList arraylist = new ArrayList();
                sMixinsWhitelist = arraylist;
                arraylist.add("SQUARES");
                sMixinsWhitelist.add("BIRTHDAYS");
            }
            arraylist1 = sMixinsWhitelist;
        }
        return arraylist1;
    }

    private static int getMostRecentSortIndex(SQLiteDatabase sqlitedatabase, String s)
    {
    	Cursor cursor = null;
    	
    	try {
	    	cursor = sqlitedatabase.query("activity_streams", new String[] {
	            "sort_index"
	        }, "stream_key=?", new String[] {
	            s
	        }, null, null, "sort_index ASC", "1");
	    	
	    	if(null == cursor || !cursor.moveToFirst()) {
	    		return 0;
	    	}
	    	
	    	return cursor.getInt(0);
    	} finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static DbPlusOneData getPostPlusOneData(SQLiteDatabase sqlitedatabase, String s)
    {
        Cursor cursor = null;
        DbPlusOneData dbplusonedata;
        try {
	        cursor = sqlitedatabase.query("activities", new String[] {
	            "plus_one_data"
	        }, "activity_id=?", new String[] {
	            s
	        }, null, null, null);
	        
	        if(null == cursor || !cursor.moveToFirst()) {
	        	return null;
	        }
	        if(cursor.isNull(0)) {
	        	return new DbPlusOneData();
	        }
	        return DbPlusOneData.deserialize(cursor.getBlob(0));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static ArrayList getShareboxEmbedsWhitelist()
    {
        if(sShareboxWhitelist == null)
        {
            ArrayList arraylist = new ArrayList();
            sShareboxWhitelist = arraylist;
            arraylist.add("APP_INVITE");
            sShareboxWhitelist.add("WEB_PAGE");
            sShareboxWhitelist.add("VIDEO_OBJECT");
            sShareboxWhitelist.add("PLAY_MUSIC_TRACK");
            sShareboxWhitelist.add("PLAY_MUSIC_ALBUM");
            sShareboxWhitelist.add("THING");
        }
        return sShareboxWhitelist;
    }

    public static ArrayList getStreamNamespaces(boolean flag)
    {
        ArrayList arraylist1;
        if(flag)
        {
            if(sWidgetStreamNamespaces == null)
            {
                ArrayList arraylist2 = new ArrayList();
                sWidgetStreamNamespaces = arraylist2;
                arraylist2.add("STREAM");
                sWidgetStreamNamespaces.add("PHOTO");
                sWidgetStreamNamespaces.add("BIRTHDAY");
            }
            arraylist1 = sWidgetStreamNamespaces;
        } else
        {
            if(sStreamNamespaces == null)
            {
                ArrayList arraylist = new ArrayList();
                sStreamNamespaces = arraylist;
                arraylist.add("STREAM");
                sStreamNamespaces.add("EVENT");
                sStreamNamespaces.add("SEARCH");
                sStreamNamespaces.add("PLUSONE");
                sStreamNamespaces.add("PHOTO");
                sStreamNamespaces.add("A2A");
                sStreamNamespaces.add("BIRTHDAY");
                sStreamNamespaces.add("PHOTOS_ADDED_TO_EVENT");
            }
            arraylist1 = sStreamNamespaces;
        }
        return arraylist1;
    }

    public static boolean hasStreamChanged(Context context, EsAccount esaccount, String s, List list)
    {
        String s1;
        SQLiteDatabase sqlitedatabase;
        Cursor cursor = null;
        String s2;
        if(list == null || list.size() == 0)
        {
            s1 = null;
        } else
        {
            s1 = ((Update)list.get(0)).updateId;
            if(EsLog.isLoggable("EsPostsData", 3))
                Log.d("EsPostsData", (new StringBuilder("hasStreamChanged received: ")).append(list.size()).toString());
        }
        
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        cursor = sqlitedatabase.query("activity_streams", new String[] {
	            "activity_id"
	        }, "stream_key=?", new String[] {
	            s
	        }, null, null, "sort_index ASC", "1");
	        if(null == cursor) {
	        	s2 = null;
	        } else {
	        	s2 = cursor.getString(0);
	        }
	        sqlitedatabase.setTransactionSuccessful();
	        if(EsLog.isLoggable("EsPostsData", 3))
	            Log.d("EsPostsData", (new StringBuilder("hasStreamChanged: ")).append(s).append(" ,server activity id: ").append(s1).append(" ,local activity id: ").append(s2).toString());
	        
	        boolean flag;
	        String s3;
	        if(s2 == null)
	        {
	            if(s1 == null)
	                flag = false;
	            else
	                flag = true;
	        } else
	        if(s1 == null)
	            flag = false;
	        else
	        if(!s1.equals(s2))
	            flag = true;
	        else
	            flag = false;
	        return flag;
        } finally {
        	sqlitedatabase.endTransaction();
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static void insertActivitiesAndOverwrite(Context context, EsAccount esaccount, String s, List list, String s1)
        throws IOException
    {
        SQLiteDatabase sqlitedatabase;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
        	sqlitedatabase.beginTransaction();
        	insertActivitiesInTransaction(context, esaccount, sqlitedatabase, list, s1, true);
        	if(s != null)
        		insertActivitiesIntoStreamInTransaction$400325ad(sqlitedatabase, s, list);
        	sqlitedatabase.setTransactionSuccessful();
        	if(s != null)
            {
                Uri uri = EsProvider.buildStreamUri(esaccount, s);
                context.getContentResolver().notifyChange(uri, null);
            } else
            {
                Iterator iterator = list.iterator();
                while(iterator.hasNext()) 
                    notifyActivityChange(sqlitedatabase, context, esaccount, ((Update)iterator.next()).updateId);
            }
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static void insertActivitiesInTransaction(Context context, EsAccount esaccount, SQLiteDatabase sqlitedatabase, List list, String s, boolean flag)
        throws IOException
    {
        // TODO
    }

    private static void insertActivitiesIntoStreamInTransaction$400325ad(SQLiteDatabase sqlitedatabase, String s, List list)
    {
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder("insertActivitiesAndOverwrite in stream: ")).append(s).append(" ").append(list.size()).toString());
        ContentValues contentvalues = new ContentValues(4);
        int i = getMostRecentSortIndex(sqlitedatabase, s) - list.size();
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            Update update = (Update)iterator.next();
            contentvalues.put("stream_key", s);
            contentvalues.put("activity_id", update.updateId);
            contentvalues.put("sort_index", Integer.valueOf(i));
            contentvalues.put("last_activity", Integer.valueOf(0));
            sqlitedatabase.insertWithOnConflict("activity_streams", "activity_id", contentvalues, 4);
            i++;
        }

    }

    public static void insertComment(Context context, EsAccount esaccount, String s, Comment comment) {
        SQLiteDatabase sqlitedatabase;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>> insertComment: ")).append(comment.commentId).append(" for activity: ").append(s).toString());
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
        	sqlitedatabase.beginTransaction();
        	ContentValues contentvalues = new ContentValues();
        	createCommentValues(comment, s, contentvalues);
        	sqlitedatabase.insertWithOnConflict("activity_comments", "activity_id", contentvalues, 5);
        	updateTotalCommentCountInTransaction(sqlitedatabase, s, 1);
	        EsPeopleData.replaceUserInTransaction(sqlitedatabase, comment.obfuscatedId, comment.authorName, comment.authorPhotoUrl);
	        sqlitedatabase.setTransactionSuccessful();
	        notifyActivityChange(sqlitedatabase, context, esaccount, s);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static void insertLocations(Context context, EsAccount esaccount, LocationQuery locationquery, DbLocation dblocation, DbLocation dblocation1, ArrayList arraylist)
        throws IOException
    {
        long l;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        String s = locationquery.getKey();
        sqlitedatabase.delete("location_queries", "key=?", new String[] {
            s
        });
        ArrayList arraylist1 = new ArrayList();
        if(dblocation != null)
            arraylist1.add(dblocation);
        if(dblocation1 != null)
            arraylist1.add(dblocation1);
        arraylist1.addAll(arraylist);
        if(arraylist1.size() <= 0) {
        	return;
        }
        
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("key", s);
        l = sqlitedatabase.insertOrThrow("location_queries", "key", contentvalues);
        if(l < 0L) {
        	return;
        }
        
        try {
	        sqlitedatabase.beginTransaction();
	        int i = arraylist1.size();
	        for(int j = 0; j < i; j++)
	        {
	            DbLocation dblocation2 = (DbLocation)arraylist1.get(j);
	            contentvalues.clear();
	            contentvalues.put("qrid", Long.valueOf(l));
	            contentvalues.put("name", dblocation2.getLocationName());
	            contentvalues.put("location", DbLocation.serialize(dblocation2));
	            sqlitedatabase.insertOrThrow("locations", "qrid", contentvalues);
	        }
	
	        sqlitedatabase.setTransactionSuccessful();
	        Uri uri = EsProvider.buildLocationQueryUri(esaccount, locationquery.getKey());
	        context.getContentResolver().notifyChange(uri, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static void insertMultiStreamActivities(Context context, EsAccount esaccount, List list, List list1, String s)
        throws IOException
    {
        SQLiteDatabase sqlitedatabase;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
        	sqlitedatabase.beginTransaction();
        	insertActivitiesInTransaction(context, esaccount, sqlitedatabase, list1, s, false);
        	for(Iterator iterator = list.iterator(); iterator.hasNext(); insertActivitiesIntoStreamInTransaction$400325ad(sqlitedatabase, (String)iterator.next(), list1));
        	sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static boolean isActivityPlusOnedByViewer(Context context, EsAccount esaccount, String s) {
    	Cursor cursor = null;
    	try {
	        cursor = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getReadableDatabase().query("activities", new String[] {
	            "plus_one_data"
	        }, "activity_id=?", new String[] {
	            s
	        }, null, null, null);
	        if(!cursor.moveToFirst() || cursor.isNull(0)) {
	        	return false;
	        }
	        DbPlusOneData dbplusonedata = DbPlusOneData.deserialize(cursor.getBlob(0));
	        if(null == dbplusonedata) {
	        	return false;
	        }
	        return dbplusonedata.isPlusOnedByMe();
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    }

    public static void markActivitiesAsRead(Context context, EsAccount esaccount, List list) {
        SQLiteDatabase sqlitedatabase;
        if(EsLog.isLoggable("EsPostsData", 3)) {
            Log.d("EsPostsData", ">>>>> markActivitiesAsRead activity ids:");
            String s1;
            for(Iterator iterator1 = list.iterator(); iterator1.hasNext(); Log.d("EsPostsData", (new StringBuilder("\t")).append(s1).toString()))
                s1 = (String)iterator1.next();

        }
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        Iterator iterator;
	        ContentValues contentvalues = new ContentValues(1);
	        contentvalues.put("has_read", Integer.valueOf(1));
	        StringBuilder stringbuffer = new StringBuilder(256);
	        stringbuffer.append("activity_id IN(");
	        iterator = list.iterator();
	        if(iterator.hasNext()) {
	        	stringbuffer.append(DatabaseUtils.sqlEscapeString((String)iterator.next()));
	        }
			while(iterator.hasNext()) {
				stringbuffer.append(DatabaseUtils.sqlEscapeString(","));
				stringbuffer.append(DatabaseUtils.sqlEscapeString((String)iterator.next()));
			}
			stringbuffer.append(DatabaseUtils.sqlEscapeString(")"));
	        sqlitedatabase.update("activities", contentvalues, stringbuffer.toString(), null);
	        sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static void muteActivity(Context context, EsAccount esaccount, String s, boolean flag)
    {
        int i;
        SQLiteDatabase sqlitedatabase;
        i = 1;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>>> muteActivity id: ")).append(s).toString());
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        ContentValues contentvalues = new ContentValues(1);
	        ContentResolver contentresolver;
	        if(!flag)
	            i = 0;
	        contentvalues.put("has_muted", Integer.valueOf(i));
	        sqlitedatabase.update("activities", contentvalues, "activity_id=?", new String[] {
	            s
	        });
	        sqlitedatabase.setTransactionSuccessful();
	        sqlitedatabase.endTransaction();
	        List list = getActivityStreams(sqlitedatabase, s);
	        contentresolver = context.getContentResolver();
	        for(Iterator iterator = list.iterator(); iterator.hasNext(); contentresolver.notifyChange(EsProvider.buildStreamUri(esaccount, (String)iterator.next()), null));
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static void notifyActivityChange(SQLiteDatabase sqlitedatabase, Context context, EsAccount esaccount, String s)
    {
        ContentResolver contentresolver = context.getContentResolver();
        android.net.Uri.Builder builder = EsProvider.ACTIVITY_VIEW_BY_ACTIVITY_ID_URI.buildUpon();
        builder.appendPath(s);
        contentresolver.notifyChange(builder.build(), null);
        for(Iterator iterator = getActivityStreams(sqlitedatabase, s).iterator(); iterator.hasNext(); contentresolver.notifyChange(EsProvider.buildStreamUri(esaccount, (String)iterator.next()), null));
    }

    public static DbPlusOneData plusOneComment(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>>> plusOneComment activity id: ")).append(s).append(", commentId: ").append(s1).append(" ").append(flag).toString());
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        DbPlusOneData dbplusonedata = getCommentPlusOneData(sqlitedatabase, s1);
        if(dbplusonedata == null)
        {
            dbplusonedata = null;
        } else
        {
            dbplusonedata.updatePlusOnedByMe(flag);
            replaceCommentPlusOneData(sqlitedatabase, s1, dbplusonedata);
            notifyActivityChange(sqlitedatabase, context, esaccount, s);
        }
        return dbplusonedata;
    }

    public static DbPlusOneData plusOnePost(Context context, EsAccount esaccount, String s, boolean flag)
    {
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>>> plusOnePost activity id: ")).append(s).append(" ").append(flag).toString());
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        DbPlusOneData dbplusonedata = getPostPlusOneData(sqlitedatabase, s);
        if(dbplusonedata == null)
        {
            dbplusonedata = null;
        } else
        {
            dbplusonedata.updatePlusOnedByMe(flag);
            replacePostPlusOneData(sqlitedatabase, s, dbplusonedata);
            notifyActivityChange(sqlitedatabase, context, esaccount, s);
        }
        return dbplusonedata;
    }

    private static void replaceCommentPlusOneData(SQLiteDatabase sqlitedatabase, String s, DbPlusOneData dbplusonedata) {
    	try {
    		byte abyte0[] = DbPlusOneData.serialize(dbplusonedata);
    		ContentValues contentvalues = new ContentValues(1);
    		contentvalues.put("plus_one_data", abyte0);
    		sqlitedatabase.update("activity_comments", contentvalues, "comment_id=?", new String[] {
    				s
    		});
    	} catch (IOException e) {
    		Log.e("EsPostsData", (new StringBuilder("Could not serialize DbPlusOneData ")).append(e).toString());
    	}
    }

    private static void replacePostPlusOneData(SQLiteDatabase sqlitedatabase, String s, DbPlusOneData dbplusonedata) {
    	try {
	        byte abyte0[] = DbPlusOneData.serialize(dbplusonedata);
	        ContentValues contentvalues = new ContentValues(1);
	        contentvalues.put("plus_one_data", abyte0);
	        sqlitedatabase.update("activities", contentvalues, "activity_id=?", new String[] {
	            s
	        });
    	} catch (IOException e) {
    		 Log.e("EsPostsData", (new StringBuilder("Could not serialize DbPlusOneData ")).append(e).toString());
    	}
    }

    public static void setSyncEnabled(boolean flag)
    {
        sSyncEnabled = flag;
    }

    public static void syncActivities(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, HttpOperation.OperationListener operationlistener)
        throws Exception
    {
    	synchronized(sSyncLock) {
    		
    	}
        // TODO
    }

    public static void updateComment(Context context, EsAccount esaccount, String s, Comment comment) {
        SQLiteDatabase sqlitedatabase;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>> editComment: ")).append(comment.commentId).append(" for activity: ").append(s).toString());
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        ContentValues contentvalues = new ContentValues();
	        createCommentValues(comment, s, contentvalues);
	        StringBuffer stringbuffer = new StringBuffer(256);
	        stringbuffer.append("comment_id IN(");
	        stringbuffer.append(DatabaseUtils.sqlEscapeString(comment.commentId));
	        stringbuffer.append(')');
	        sqlitedatabase.update("activity_comments", contentvalues, stringbuffer.toString(), null);
	        EsPeopleData.replaceUserInTransaction(sqlitedatabase, comment.obfuscatedId, comment.authorName, comment.authorPhotoUrl);
	        sqlitedatabase.setTransactionSuccessful();
	        notifyActivityChange(sqlitedatabase, context, esaccount, s);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static void updateCommentPlusOneId(Context context, EsAccount esaccount, String s, String s1, String s2)
    {
        SQLiteDatabase sqlitedatabase;
        DbPlusOneData dbplusonedata;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>>> updateCommentPlusOneId activity id: ")).append(s).append(", comment id: ").append(s1).toString());
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        dbplusonedata = getCommentPlusOneData(sqlitedatabase, s1);
        if(dbplusonedata != null && !TextUtils.equals(dbplusonedata.getId(), s2))
        {
            dbplusonedata.setId(s2);
            replaceCommentPlusOneData(sqlitedatabase, s1, dbplusonedata);
        }
        return;
    }

    private static void updateCommentsInTransaction(SQLiteDatabase sqlitedatabase, String s, List list, boolean flag)
    {
        if(!flag)
            sqlitedatabase.delete("activity_comments", "activity_id=?", new String[] {
                s
            });
        if(!list.isEmpty())
        {
            ContentValues contentvalues = new ContentValues();
            Iterator iterator = list.iterator();
            while(iterator.hasNext()) 
            {
                Comment comment = (Comment)iterator.next();
                if(EsLog.isLoggable("EsPostsData", 3))
                    Log.d("EsPostsData", (new StringBuilder("    >>>>> insertComments comment id: ")).append(comment.commentId).append(", author id: ").append(comment.obfuscatedId).append(", content: ").append(comment.text).append(", created: ").append(comment.timestamp).toString());
                if(PrimitiveUtils.safeBoolean(comment.isSpam) && !PrimitiveUtils.safeBoolean(comment.isOwnedByViewer))
                {
                    if(EsLog.isLoggable("EsPostsData", 3))
                        Log.d("EsPostsData", "    >>>>> skipping! isSpam=true");
                } else
                {
                    createCommentValues(comment, s, contentvalues);
                    sqlitedatabase.insertWithOnConflict("activity_comments", "activity_id", contentvalues, 5);
                    EsPeopleData.replaceUserInTransaction(sqlitedatabase, comment.obfuscatedId, comment.authorName, comment.authorPhotoUrl);
                }
            }
        }
    }

    private static void updateMediaInTransaction(SQLiteDatabase sqlitedatabase, String s, HashSet hashset, boolean flag) {
        // TODO
    }

    public static void updatePostPlusOneId(Context context, EsAccount esaccount, String s, String s1)
    {
        SQLiteDatabase sqlitedatabase;
        DbPlusOneData dbplusonedata;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder(">>>>> update post plusone id: ")).append(s).append(" ").append(s1).toString());
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        dbplusonedata = getPostPlusOneData(sqlitedatabase, s);
        if(dbplusonedata != null && !TextUtils.equals(dbplusonedata.getId(), s1))
        {
            dbplusonedata.setId(s1);
            replacePostPlusOneData(sqlitedatabase, s, dbplusonedata);
        }
        return;
    }

    public static void updateStreamActivities(Context context, EsAccount esaccount, String s, List list, String s1, String s2, String s3, EsSyncAdapterService.SyncState syncstate)
        throws IOException
    {
        int i;
        SQLiteDatabase sqlitedatabase;
        if(list == null)
            list = new ArrayList();
        i = list.size();
        if(TextUtils.equals(s2, s3))
            s3 = null;
        if(EsLog.isLoggable("EsPostsData", 3))
            Log.d("EsPostsData", (new StringBuilder("updateStreamActivities: ")).append(s).append(" received activities: ").append(i).append(" ,new token: ").append(s3).append(" ,old token: ").append(s2).toString());
        if(syncstate != null)
            syncstate.incrementCount(i);
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
	        sqlitedatabase.beginTransaction();
	        int j;
	        ContentValues contentvalues;
	        int k;
	        if(TextUtils.isEmpty(s2))
	        {
	            j = 0;
	            sqlitedatabase.delete("activity_streams", "stream_key=?", new String[] {
	                s
	            });
	        } else
	        {
	            j = (int)DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM activity_streams WHERE stream_key=?", new String[] {
	                s
	            });
	        }
	        contentvalues = new ContentValues(5);
	        k = -1 + (j + i);
	        for(Iterator iterator = list.iterator(); iterator.hasNext();)
	        {
	            Update update = (Update)iterator.next();
	            contentvalues.put("stream_key", s);
	            contentvalues.put("activity_id", update.updateId);
	            contentvalues.put("sort_index", Integer.valueOf(j));
	            sqlitedatabase.insertWithOnConflict("activity_streams", "activity_id", contentvalues, 5);
	            j++;
	        }
	
	        contentvalues.clear();
	        contentvalues.put("token", s3);
	        sqlitedatabase.update("activity_streams", contentvalues, "stream_key=? AND sort_index=0", new String[] {
	            s
	        });
	        if(TextUtils.isEmpty(s3))
	        {
	            contentvalues.clear();
	            contentvalues.put("last_activity", Integer.valueOf(1));
	            String as[] = new String[2];
	            as[0] = s;
	            as[1] = String.valueOf(k);
	            sqlitedatabase.update("activity_streams", contentvalues, "stream_key=? AND sort_index=?", as);
	        }
	        if(i > 0)
	        {
	            if(EsLog.isLoggable("EsPostsData", 3))
	                Log.d("EsPostsData", (new StringBuilder("updateStreamActivities: ")).append(s).append(" inserting activities:").append(list.size()).toString());
	            insertActivitiesInTransaction(context, esaccount, sqlitedatabase, list, s1, false);
	        }
	        sqlitedatabase.setTransactionSuccessful();
	        Uri uri = EsProvider.buildStreamUri(esaccount, s);
	        context.getContentResolver().notifyChange(uri, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static void updateTotalCommentCountInTransaction(SQLiteDatabase sqlitedatabase, String s, int i)
    {
        ContentValues contentvalues = new ContentValues();
        Cursor cursor = null;
        
        try {
	        cursor = sqlitedatabase.query("activities", new String[] {
	            "total_comment_count"
	        }, "activity_id=?", new String[] {
	            s
	        }, null, null, null);
	        int j = 0;
	        if(cursor != null)
	        {
	            boolean flag = cursor.moveToFirst();
	            j = 0;
	            if(flag)
	                j = cursor.getInt(0);
	        }
	        contentvalues.put("total_comment_count", Integer.valueOf(j + i));
	        sqlitedatabase.update("activities", contentvalues, "activity_id=?", new String[] {
	            s
	        });
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
    
    private static final class ActivityStatus
    {

        int dataStatus;
        long timestamp;

        ActivityStatus()
        {
        }
    }

    private static interface ActivityStreamKeyQuery
    {

        public static final String PROJECTION[] = {
            "stream_key"
        };

    }
}
