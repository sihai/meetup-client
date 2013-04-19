/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.galaxy.meetup.client.android.api.PhotosInAlbumOperation;
import com.galaxy.meetup.client.android.api.PhotosOfUserOperation;
import com.galaxy.meetup.client.android.api.UserPhotoAlbumsOperation;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.Comment;
import com.galaxy.meetup.server.client.domain.DataAlbum;
import com.galaxy.meetup.server.client.domain.DataComment;
import com.galaxy.meetup.server.client.domain.DataPhoto;
import com.galaxy.meetup.server.client.domain.DataPlusOne;
import com.galaxy.meetup.server.client.domain.DataRect32;
import com.galaxy.meetup.server.client.domain.DataShape;
import com.galaxy.meetup.server.client.domain.DataUser;
import com.galaxy.meetup.server.client.domain.DataVideo;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EsPhotosDataApiary extends EsPhotosData {

	private static final String PHOTO_COMMENT_ID_COLUMN[] = {
        "plusone_data"
    };
    private static String sPhotosFromPostsAlbumName;
    
    
    public static void deletePhotoComment(Context context, EsAccount esaccount, String s)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        long l = System.currentTimeMillis();
        String as[] = (new String[] {
            s
        });
        long l1 = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT photo_id FROM photo_comment WHERE comment_id = ?", as);
        try {
	        sqlitedatabase.beginTransaction();
	        int i = sqlitedatabase.delete("photo_comment", "comment_id = ?", as);
	        updateCommentCount(sqlitedatabase, Long.toString(l1), -i);
	        sqlitedatabase.setTransactionSuccessful();
	        if(EsLog.isLoggable("EsPhotosData", 4))
	            Log.i("EsPhotosData", (new StringBuilder("[DELETE_PHOTO_COMMENT], duration: ")).append(getDeltaTime(l)).toString());
	        Uri uri = ContentUris.withAppendedId(EsProvider.PHOTO_COMMENTS_BY_PHOTO_ID_URI, l1);
	        context.getContentResolver().notifyChange(uri, null);
	        context.getContentResolver().notifyChange(EsProvider.PHOTO_URI, null);
        } catch (SQLiteDoneException sqlitedoneexception) {
        	if(EsLog.isLoggable("EsPhotosData", 5))
                Log.w("EsPhotosData", (new StringBuilder("WARNING: could not find photo for the comment: ")).append(s).toString());
        } finally {
        	sqlitedatabase.endTransaction();
        	if(EsLog.isLoggable("EsPhotosData", 4))
                Log.i("EsPhotosData", (new StringBuilder("[DELETE_PHOTO_COMMENT], duration: ")).append(getDeltaTime(l)).toString());
        }
    }
    
    private static void deletePhotoPlusOneRow(SQLiteDatabase sqlitedatabase, String s)
    {
        sqlitedatabase.delete("photo_plusone", "photo_id=?", new String[] {
            s
        });
    }

    public static void deletePhotos(Context context, EsAccount esaccount, List list) {
    	
    	if(null == list || list.isEmpty()) {
    		return;
    	}
    	
    	SQLiteDatabase sqlitedatabase;
        long l;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        l = System.currentTimeMillis();
        HashMap hashmap;
        StringBuilder stringbuilder;
        String as[];
        try {
	        sqlitedatabase.beginTransaction();
	        hashmap = new HashMap();
	        stringbuilder = new StringBuilder();
	        as = new String[list.size()];
	        stringbuilder.append("photo_id IN(");
	        int i = -1 + list.size();
	        do
	        {
	            if(i < 0)
	                break;
	            String s1 = Long.toString(((Long)list.get(i)).longValue());
	            if(EsLog.isLoggable("EsPhotosData", 3))
	                Log.d("EsPhotosData", (new StringBuilder(">> deletePhoto photo id: ")).append(s1).toString());
	            String as2[] = {
	                s1
	            };
	            try
	            {
	                String s2 = DatabaseUtils.stringForQuery(sqlitedatabase, "SELECT album_id FROM photo WHERE photo_id = ?", as2);
	                Integer integer = (Integer)hashmap.get(s2);
	                if(integer == null)
	                    integer = Integer.valueOf(0);
	                hashmap.put(s2, Integer.valueOf(-1 + integer.intValue()));
	            }
	            catch(SQLiteDoneException sqlitedoneexception1)
	            {
	                if(EsLog.isLoggable("EsPhotosData", 5))
	                    Log.w("EsPhotosData", (new StringBuilder("Album not found for photo: ")).append(s1).toString());
	            }
	            stringbuilder.append("?,");
	            as[i] = s1;
	            i--;
	        } while(true);
	        
	        String s;
	        String as1[];
	        int j;
	        stringbuilder.setLength(-1 + stringbuilder.length());
	        stringbuilder.append(")");
	        ContentValues contentvalues = new ContentValues();
	        Iterator iterator = hashmap.keySet().iterator();
	        while(iterator.hasNext()) {
	        	s = (String)iterator.next();
	            as1 = (new String[] {
	                s
	            });
	            j = ((Integer)hashmap.get(s)).intValue();
	            try {
	            	contentvalues.put("photo_count", Long.valueOf(Math.max(0L, DatabaseUtils.longForQuery(sqlitedatabase, "SELECT photo_count FROM album WHERE photo_count NOT NULL AND album_id = ?", as1) + (long)j)));
	            	sqlitedatabase.update("album", contentvalues, "album_id = ?", as1);
	            } catch (SQLiteDoneException sqlitedoneexception) {
	            	if(EsLog.isLoggable("EsPhotosData", 4)) {
	            		Log.i("EsPhotosData", (new StringBuilder("Photo count not found; album id: ")).append(s).toString());
	            	}
	            }
	        }
	        
	        sqlitedatabase.delete("photo", stringbuilder.toString(), as);
	        sqlitedatabase.setTransactionSuccessful();
	        context.getContentResolver().notifyChange(EsProvider.PHOTO_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static void deletePhotosInTransaction(SQLiteDatabase sqlitedatabase, Map map)
    {
        if(map.size() > 0)
        {
            StringBuilder stringbuilder = new StringBuilder();
            ArrayList arraylist = new ArrayList(map.size());
            stringbuilder.append("photo_id IN(");
            Long long1;
            for(Iterator iterator = map.keySet().iterator(); iterator.hasNext(); arraylist.add(Long.toString(long1.longValue())))
            {
                long1 = (Long)iterator.next();
                stringbuilder.append("?,");
            }

            stringbuilder.setLength(-1 + stringbuilder.length());
            stringbuilder.append(")");
            sqlitedatabase.delete("photo", stringbuilder.toString(), (String[])arraylist.toArray(new String[0]));
        }
    }

    private static String getAlbumId(DataAlbum dataalbum)
    {
        String s;
        try
        {
            Long.parseLong(dataalbum.id);
            s = dataalbum.id;
        }
        catch(NumberFormatException numberformatexception)
        {
            s = (new StringBuilder()).append(dataalbum.id).append("_").append(dataalbum.owner.id).toString();
        }
        return s;
    }

    private static String getAlbumOutput(DataAlbum dataalbum, int i)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if(i > 0)
        {
            for(int j = 0; j < i; j++)
                stringbuilder.append(' ');

        }
        String s = stringbuilder.toString();
        stringbuilder.setLength(0);
        stringbuilder.append(s).append("ALBUM [id: ").append(getAlbumId(dataalbum)).append(", owner: ").append(dataalbum.owner.id).append(", count: ").append(dataalbum.photoCount);
        if(dataalbum.albumType != null)
            stringbuilder.append(",\n").append(s).append("       type: ").append(dataalbum.albumType);
        if(dataalbum.title != null)
            stringbuilder.append(",\n").append(s).append("       title: ").append(dataalbum.title);
        if(dataalbum.cover != null)
            stringbuilder.append("\n").append(getCoverPhotoOutput(dataalbum.cover, i + 2));
        stringbuilder.append("]");
        return stringbuilder.toString();
    }

    private static ContentValues getCommentContentValues(DataComment datacomment, String s)
    {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("photo_id", s);
        contentvalues.put("comment_id", datacomment.id);
        contentvalues.put("author_id", datacomment.user.id);
        contentvalues.put("content", datacomment.text);
        if(datacomment.timestamp != null)
            contentvalues.put("create_time", Long.valueOf((long)(1000D * Double.parseDouble(datacomment.timestamp))));
        if(datacomment.lastUpdateTimestamp != null)
            contentvalues.put("update_time", datacomment.lastUpdateTimestamp);
        if(datacomment.plusOne != null)
            contentvalues.put("plusone_data", datacomment.plusOne.toJsonString());
        else
            contentvalues.putNull("plusone_data");
        return contentvalues;
    }
    
    private static DataPlusOne getCommentPlusOneData(SQLiteDatabase sqlitedatabase, String s)
    {
    	Cursor cursor = null;
    	try {
	    	cursor = sqlitedatabase.query("photo_comment", PHOTO_COMMENT_ID_COLUMN, "comment_id=?", new String[] {
	            s
	        }, null, null, null);
	        if(!cursor.moveToFirst() || cursor.isNull(0)) {
	        	return null;
	        } else {
	        	return (DataPlusOne)JsonUtil.fromByteArray(cursor.getBlob(0), DataPlusOne.class);
	        }
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    }

    private static String getCoverPhotoOutput(DataPhoto dataphoto, int i)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if(i > 0)
        {
            for(int j = 0; j < i; j++)
                stringbuilder.append(' ');

        }
        String s = stringbuilder.toString();
        stringbuilder.setLength(0);
        long l;
        String s1;
        if(dataphoto.timestampSeconds == null)
            l = 0L;
        else
            l = (long)(1000D * dataphoto.timestampSeconds.doubleValue());
        stringbuilder.append(s).append("COVER PHOTO [id: ").append(dataphoto.id).append(", owner: ");
        if(dataphoto.owner == null)
            s1 = "N/A";
        else
            s1 = dataphoto.owner.id;
        stringbuilder.append(s1);
        if(l != 0L)
        {
            CharSequence charsequence = DateFormat.format("MMM dd, yyyy h:mmaa", new Date(l));
            stringbuilder.append(", date: ").append(charsequence);
        }
        stringbuilder.append("]");
        stringbuilder.append("\n");
        return stringbuilder.toString();
    }
    
    private static byte[] getFingerPrint(DataPhoto dataphoto)
    {
        if(dataphoto.streamId == null) 
        	return null;
        
        for(Iterator iterator = dataphoto.streamId.iterator(); iterator.hasNext();) {
        	String s = (String)iterator.next();
        	if(s.startsWith("cs_01_")) {
        		return hexToBytes(s.substring(FINGERPRINT_STREAM_PREFIX_LENGTH));
        	}
        }
        
        return null;
    }

    private static ContentValues getPhotoCommentPlusOneContentValues(DataPlusOne dataplusone)
    {
        ContentValues contentvalues = new ContentValues();
        if(dataplusone == null)
            contentvalues.putNull("plusone_data");
        else
            contentvalues.put("plusone_data", dataplusone.toJsonString());
        return contentvalues;
    }

    private static String getPhotoOutput(DataPhoto dataphoto, int i)
    {
        StringBuilder stringbuilder = new StringBuilder();
        String s = stringbuilder.toString();
        stringbuilder.setLength(0);
        DataPlusOne dataplusone = dataphoto.plusOne;
        DataVideo datavideo = dataphoto.video;
        double d;
        long l;
        StringBuilder stringbuilder1;
        String s1;
        StringBuilder stringbuilder2;
        String s2;
        StringBuilder stringbuilder3;
        StringBuilder stringbuilder4;
        boolean flag;
        StringBuilder stringbuilder5;
        int j;
        StringBuilder stringbuilder6;
        StringBuilder stringbuilder7;
        Object obj;
        StringBuilder stringbuilder8;
        StringBuilder stringbuilder9;
        Object obj1;
        if(dataphoto.timestampSeconds == null)
            d = 0.0D;
        else
            d = dataphoto.timestampSeconds.doubleValue();
        l = 1000L * (long)d;
        stringbuilder1 = stringbuilder.append(s).append("PHOTO [id: ").append(dataphoto.id).append(", owner: ");
        if(dataphoto.owner == null)
            s1 = "N/A";
        else
            s1 = dataphoto.owner.id;
        stringbuilder1.append(s1).append(", version: ").append(dataphoto.entityVersion);
        if(l != 0L)
        {
            CharSequence charsequence = DateFormat.format("MMM dd, yyyy h:mmaa", new Date(l));
            stringbuilder.append(", date: ").append(charsequence);
        }
        stringbuilder.append(", \n");
        stringbuilder2 = stringbuilder.append(s).append("      title: ");
        if(dataphoto.title == null)
            s2 = "N/A";
        else
            s2 = dataphoto.title;
        stringbuilder3 = stringbuilder2.append(s2).append(", ");
        stringbuilder4 = new StringBuilder("video? ");
        if(datavideo != null)
            flag = true;
        else
            flag = false;
        stringbuilder5 = stringbuilder3.append(stringbuilder4.append(flag).toString()).append(", comments: ");
        if(dataphoto.totalComments == null)
            j = 0;
        else
            j = dataphoto.totalComments.intValue();
        stringbuilder6 = stringbuilder5.append(j).append(", ");
        stringbuilder7 = new StringBuilder("+1s: ");
        if(dataplusone != null)
            obj = dataplusone.globalCount;
        else
            obj = "0";
        stringbuilder8 = stringbuilder6.append(stringbuilder7.append(obj).toString()).append(", ");
        stringbuilder9 = new StringBuilder("by me: ");
        if(dataplusone != null)
            obj1 = dataplusone.isPlusonedByViewer;
        else
            obj1 = "false";
        stringbuilder8.append(stringbuilder9.append(obj1).toString());
        if(dataphoto.streamId != null)
        {
            String s3;
            for(Iterator iterator1 = dataphoto.streamId.iterator(); iterator1.hasNext(); stringbuilder.append(", \n").append(s).append("      stream: ").append(s3))
                s3 = (String)iterator1.next();

        }
        if(dataphoto.album != null)
            stringbuilder.append("\n").append(getAlbumOutput(dataphoto.album, 2));
        if(dataphoto.shape != null)
        {
            DataShape datashape;
            for(Iterator iterator = dataphoto.shape.iterator(); iterator.hasNext(); stringbuilder.append("\n").append(getShapeOutput(datashape, 2)))
                datashape = (DataShape)iterator.next();

        }
        stringbuilder.append("]");
        stringbuilder.append("\n");
        stringbuilder.append("\n");
        return stringbuilder.toString();
    }
    
    private static ContentValues getShapeContentValues(DataShape datashape, String s, String s1)
    {
        ContentValues contentvalues;
        contentvalues = new ContentValues();
        if(datashape.relativeBounds != null)
            contentvalues.put("bounds", datashape.relativeBounds.toJsonString());
        contentvalues.put("creator_id", datashape.creator.id);
        contentvalues.put("photo_id", Long.valueOf(Long.parseLong(s)));
        contentvalues.put("shape_id", datashape.id);
        contentvalues.put("status", datashape.status);
        if(null != datashape.user) {
        	contentvalues.put("subject_id", datashape.user.id);
        } else { 
        	if(null != datashape.suggestion && !datashape.suggestion.isEmpty()) {
        		DataUser datauser;
        		for(Iterator iterator = datashape.suggestion.iterator(); iterator.hasNext();) {
        			datauser = (DataUser)iterator.next();
        			if(null != datauser.id && datauser.id.equals(s1)) {
        				contentvalues.put("subject_id", datauser.id);
        				break;
        			}
        		}
        	}
        }
        
        return contentvalues;

    }

    private static String getShapeOutput(DataShape datashape, int i)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if(i > 0)
        {
            for(int j = 0; j < i; j++)
                stringbuilder.append(' ');

        }
        String s = stringbuilder.toString();
        stringbuilder.setLength(0);
        DataRect32 datarect32 = datashape.bounds;
        StringBuilder stringbuilder1 = stringbuilder.append(s).append("SHAPE [(");
        Object aobj[] = new Object[4];
        aobj[0] = datarect32.upperLeft.x;
        aobj[1] = datarect32.upperLeft.y;
        aobj[2] = datarect32.lowerRight.x;
        aobj[3] = datarect32.lowerRight.y;
        StringBuilder stringbuilder2 = stringbuilder1.append(String.format("%d, %d, %d, %d", aobj)).append("), ").append("subjectId: ");
        String s1;
        Boolean boolean1;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        StringBuilder stringbuilder3;
        if(datashape.user == null)
            s1 = "N/A";
        else
            s1 = datashape.user.id;
        stringbuilder2.append(s1).append(", status: ").append(datashape.status);
        boolean1 = Boolean.valueOf(false);
        boolean2 = Boolean.valueOf(false);
        boolean3 = datashape.viewerCanEdit;
        boolean4 = datashape.viewerCanApprove;
        stringbuilder3 = new StringBuilder();
        String s2;
        String s6;
        if(boolean1 != null && boolean1.booleanValue())
        {
            if(stringbuilder3.length() == 0)
                s6 = "";
            else
                s6 = "|";
            stringbuilder3.append(s6).append("COMMENT");
        }
        if(boolean2 != null && boolean2.booleanValue())
        {
            String s5;
            if(stringbuilder3.length() == 0)
                s5 = "";
            else
                s5 = "|";
            stringbuilder3.append(s5).append("TAG");
        }
        if(boolean3 != null && boolean3.booleanValue())
        {
            String s4;
            if(stringbuilder3.length() == 0)
                s4 = "";
            else
                s4 = "|";
            stringbuilder3.append(s4).append("EDIT");
        }
        if(boolean4 != null && boolean4.booleanValue())
        {
            String s3;
            if(stringbuilder3.length() == 0)
                s3 = "";
            else
                s3 = "|";
            stringbuilder3.append(s3).append("APPROVE");
        }
        s2 = stringbuilder3.toString();
        if(!TextUtils.isEmpty(s2))
            stringbuilder.append(", \n").append(s).append("       state: ").append(s2);
        stringbuilder.append("]");
        return stringbuilder.toString();
    }
    
    private static void insertAlbumInTransaction(SQLiteDatabase sqlitedatabase, DataAlbum dataalbum, Long long1, List list)
    {
        Long long2 = dataalbum.entityVersion;
        if(null != long1 && long1.equals(long2)) {
        	if(EsLog.isLoggable("EsPhotosData", 3))
                Log.d("EsPhotosData", (new StringBuilder("Album not updated; id: ")).append(dataalbum.id).toString());
        	return;
        }
        
        Long long3 = insertOrUpdateAlbumRow(sqlitedatabase, dataalbum);
        if(long3 == null)
        {
            if(EsLog.isLoggable("EsPhotosData", 5))
                Log.w("EsPhotosData", "Could not insert album row");
        } else
        {
            boolean flag = "UPDATES_ALBUMS".equals(dataalbum.albumType);
            boolean flag1;
            if(!TextUtils.equals(dataalbum.id, getAlbumId(dataalbum)))
                flag1 = true;
            else
                flag1 = false;
            if(!flag || flag1)
            {
                DataPhoto dataphoto = dataalbum.cover;
                long l = long3.longValue();
                if(dataphoto != null && dataphoto.original != null)
                {
                    String as[] = new String[1];
                    as[0] = Long.toString(l);
                    sqlitedatabase.delete("album_cover", "album_key=?", as);
                    ContentValues contentvalues = new ContentValues();
                    contentvalues.put("album_key", Long.valueOf(l));
                    contentvalues.put("url", dataphoto.original.url);
                    contentvalues.put("width", dataphoto.original.width);
                    contentvalues.put("height", dataphoto.original.height);
                    contentvalues.put("size", dataphoto.fileSize);
                    sqlitedatabase.insertWithOnConflict("album_cover", null, contentvalues, 4);
                }
            }
            if(list != null)
                list.add(EsProvider.PHOTO_BY_ALBUM_URI.buildUpon().appendEncodedPath(getAlbumId(dataalbum)).build());
        }

    }

    private static void insertAlbumListInTransaction(SQLiteDatabase sqlitedatabase, List list, Map map, List list1, EsSyncAdapterService.SyncState syncstate)
    {
        int i = list.size();
        for(int j = 0; j < i; j++)
        {
            DataAlbum dataalbum = (DataAlbum)list.get(j);
            if(EsLog.isLoggable("EsPhotosData", 3))
                EsLog.writeToLog(3, "EsPhotosData", getAlbumOutput(dataalbum, 0));
            if(syncstate != null)
                syncstate.incrementCount();
            insertAlbumInTransaction(sqlitedatabase, dataalbum, (Long)map.remove(getAlbumId(dataalbum)), list1);
        }

    }
    
    public static void insertAlbumPhotos(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, DataAlbum dataalbum, List list, Boolean boolean1)
    {
        ArrayList arraylist;
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        long l = System.currentTimeMillis();
        String s = getAlbumId(dataalbum);
        arraylist = new ArrayList();
        Map hashmap = getCurrentAlbumMap(sqlitedatabase, s, dataalbum.owner.id);
        if(EsLog.isLoggable("EsPhotosData", 3))
            EsLog.writeToLog(3, "EsPhotosData", getAlbumOutput(dataalbum, 0));
        Cursor cursor = null;
        try {
        	Long long1 = null;
	        sqlitedatabase.beginTransaction();
	        if(syncstate != null)
	            syncstate.incrementCount();
	        String as[] = {
	            "entity_version"
	        };
	        String as1[] = new String[1];
	        as1[0] = dataalbum.id;
	        cursor = sqlitedatabase.query("album", as, "album_id=?", as1, null, null, null);
	        if(cursor.moveToFirst()) {
	        	long1 = Long.valueOf(cursor.getLong(0));
	        }
	        
	        insertAlbumInTransaction(sqlitedatabase, dataalbum, long1, arraylist);
	        ContentValues contentvalues = new ContentValues();
	        contentvalues.put("album_id", s);
	        insertPhotosInTransaction(sqlitedatabase, list, boolean1, dataalbum, hashmap, "photos_in_album", contentvalues, arraylist, syncstate);
	        deletePhotosInTransaction(sqlitedatabase, hashmap);
	        sqlitedatabase.setTransactionSuccessful();
	        
	        ContentResolver contentresolver = context.getContentResolver();
	        for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); contentresolver.notifyChange((Uri)iterator.next(), null));
        } finally {
        	sqlitedatabase.endTransaction();
        	if(null != cursor) {
        		cursor.close();
        	}
        	if(EsLog.isLoggable("EsPhotosData", 4))
	        {
	            StringBuilder stringbuilder1 = (new StringBuilder("[INSERT_ALBUM_PHOTOS], album ID: ")).append(s).append(", num photos: ");
	            StringBuilder stringbuilder;
	            int j;
	            if(list != null)
	                j = list.size();
	            else
	                j = 0;
	            Log.i("EsPhotosData", stringbuilder1.append(j).append(", duration: ").append(getDeltaTime(l)).toString());
	        }
        }
    }

    public static void insertAlbums(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, String s, List list, List list1)
    {
    	SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        long l = System.currentTimeMillis();
        Map hashmap = getAlbumEntityMap(sqlitedatabase, s);
        ArrayList arraylist = new ArrayList();
        int i = 0;
        ArrayList arraylist1;
        StringBuilder stringbuilder;
        
        try {
	        sqlitedatabase.beginTransaction();
	        i = 0;
	        if(list != null)
	        {
	            i = 0 + list.size();
	            insertAlbumListInTransaction(sqlitedatabase, list, hashmap, arraylist, syncstate);
	        }
	        if(list1 != null)
	        {
	            i += list1.size();
	            insertAlbumListInTransaction(sqlitedatabase, list1, hashmap, arraylist, syncstate);
	        }
	        int j = hashmap.size();
	        arraylist1 = new ArrayList(j);
        	stringbuilder = new StringBuilder();
        	stringbuilder.append("album_type == 'ALL_OTHERS' AND album_id IN(");
	        if(j > 0) {
	        	String s2;
		        for(Iterator iterator = hashmap.keySet().iterator(); iterator.hasNext(); arraylist1.add(s2))
		        {
		            s2 = (String)iterator.next();
		            stringbuilder.append("?,");
		        }
	        }
	        
	        stringbuilder.setLength(-1 + stringbuilder.length());
	        stringbuilder.append(")");
	        sqlitedatabase.delete("album", stringbuilder.toString(), (String[])arraylist1.toArray(new String[0]));
	        sqlitedatabase.setTransactionSuccessful();
	        if(EsLog.isLoggable("EsPhotosData", 4))
	            Log.i("EsPhotosData", (new StringBuilder("[INSERT_ALBUM_LIST], num albums: ")).append(i).append(", duration: ").append(getDeltaTime(l)).toString());
	        Uri uri2;
	        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); context.getContentResolver().notifyChange(uri2, null))
	            uri2 = (Uri)iterator1.next();

	        Uri uri1;
	        for(Iterator iterator2 = hashmap.keySet().iterator(); iterator2.hasNext(); context.getContentResolver().notifyChange(uri1, null))
	        {
	            String s1 = (String)iterator2.next();
	            uri1 = Uri.withAppendedPath(EsProvider.PHOTO_BY_ALBUM_URI, s1);
	        }

	        if(i > 0 || hashmap.size() > 0)
	        {
	            Uri uri = Uri.withAppendedPath(EsProvider.ALBUM_VIEW_BY_OWNER_URI, s);
	            context.getContentResolver().notifyChange(uri, null);
	        }
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
    
    public static void insertEventPhotoInTransaction(SQLiteDatabase sqlitedatabase, DataPhoto dataphoto, String s, Map map, List list)
    {
        long l = System.currentTimeMillis();
        ContentValues contentvalues;
        String as[];
        if(EsLog.isLoggable("EsPhotosData", 3))
            EsLog.writeToLog(3, "EsPhotosData", getPhotoOutput(dataphoto, 0));
        if(insertPhotoInTransaction(sqlitedatabase, dataphoto, null, true, map, null, list, null) == null && EsLog.isLoggable("EsPhotosData", 5))
            Log.w("EsPhotosData", (new StringBuilder("Could not insert row for event photo; id: ")).append(dataphoto.id).toString());
        contentvalues = new ContentValues();
        contentvalues.put("event_id", s);
        contentvalues.put("photo_id", Long.valueOf(Long.parseLong(dataphoto.id)));
        as = new String[2];
        as[0] = s;
        as[1] = dataphoto.id;
        if(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM photos_in_event WHERE event_id=? AND photo_id=?", as) != 0L) { 
        	sqlitedatabase.update("photos_in_event", contentvalues, "event_id=? AND photo_id=?", as);
        } else { 
        	sqlitedatabase.insert("photos_in_event", null, contentvalues);
        }
        
        if(EsLog.isLoggable("EsPhotosData", 4))
            Log.i("EsPhotosData", (new StringBuilder("[INSERT_EVENT_PHOTO], event: ")).append(s).append(", duration: ").append(getDeltaTime(l)).toString());
    }

    private static Long insertOrUpdateAlbumRow(SQLiteDatabase sqlitedatabase, DataAlbum dataalbum)
    {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("album_id", getAlbumId(dataalbum));
        contentvalues.put("owner_id", dataalbum.owner.id);
        if(!TextUtils.isEmpty(dataalbum.title))
            contentvalues.put("title", dataalbum.title);
        if(dataalbum.timestampSeconds != null)
            contentvalues.put("timestamp", Long.valueOf((long)(1000D * Double.parseDouble(dataalbum.timestampSeconds))));
        contentvalues.put("album_type", dataalbum.albumType);
        if(dataalbum.entityVersion != null)
            contentvalues.put("entity_version", dataalbum.entityVersion);
        String s;
        Long long1;
        if(!"ALL_OTHERS".equals(dataalbum.albumType))
        {
            if("UPDATES_ALBUMS".equals(dataalbum.albumType))
            {
                contentvalues.put("stream_id", "posts");
                contentvalues.put("sort_order", Integer.valueOf(40));
                if(!TextUtils.isEmpty(sPhotosFromPostsAlbumName))
                    contentvalues.put("title", sPhotosFromPostsAlbumName);
            } else
            if("BUNCH_ALBUMS".equals(dataalbum.albumType))
            {
                contentvalues.put("stream_id", "messenger");
                contentvalues.put("sort_order", Integer.valueOf(50));
            } else
            if("PROFILE_PHOTOS".equals(dataalbum.albumType))
            {
                contentvalues.put("stream_id", "profile");
                contentvalues.put("sort_order", Integer.valueOf(60));
            } else
            {
                contentvalues.putNull("stream_id");
            }
            contentvalues.putNull("photo_count");
        } else
        {
            contentvalues.put("sort_order", Integer.valueOf(100));
            contentvalues.putNull("stream_id");
            if(dataalbum.photoCount != null)
                contentvalues.put("photo_count", dataalbum.photoCount);
        }
        if(dataalbum.cover != null && dataalbum.cover.id != null)
            contentvalues.put("cover_photo_id", Long.valueOf(Long.parseLong(dataalbum.cover.id)));
        s = getAlbumId(dataalbum);
        long1 = getAlbumRowId(sqlitedatabase, s);
        if(long1 == null)
        {
            long1 = Long.valueOf(sqlitedatabase.insertWithOnConflict("album", null, contentvalues, 4));
            if(long1.longValue() == -1L)
                long1 = null;
        } else
        if(sqlitedatabase.update("album", contentvalues, "album_id=?", new String[] {s}) == 0)
            long1 = null;
        return long1;
    }

    private static boolean insertOrUpdatePhotoCommentRow(SQLiteDatabase sqlitedatabase, String s, ContentValues contentvalues)
    {
        boolean flag = true;
        String as[] = new String[]{s};
        if(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM photo_comment WHERE comment_id=?", as) != 0L) {
            if(sqlitedatabase.update("photo_comment", contentvalues, "comment_id=?", as) == 0)
                flag = false; 
        } else {
        	if(sqlitedatabase.insertWithOnConflict("photo_comment", null, contentvalues, 4) == -1L)
                flag = false;
        }
        return flag;
    }

    private static boolean insertOrUpdatePhotoPlusOneRow(SQLiteDatabase sqlitedatabase, DataPlusOne dataplusone, String s)
    {
        ContentValues contentvalues;
        boolean flag1;
        contentvalues = new ContentValues();
        boolean flag;
        int i;
        int k;
        if(dataplusone.isPlusonedByViewer == null)
            flag = false;
        else
            flag = dataplusone.isPlusonedByViewer.booleanValue();
        if(dataplusone.globalCount == null)
            i = 0;
        else
            i = dataplusone.globalCount.intValue();
        contentvalues.put("plusone_data", dataplusone.toJsonString());
        contentvalues.put("plusone_by_me", Boolean.valueOf(flag));
        contentvalues.put("plusone_count", Integer.valueOf(i));
        contentvalues.put("plusone_id", dataplusone.id);
        contentvalues.put("photo_id", s);
        if(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM photo_plusone WHERE photo_id=?", new String[] {s}) != 0L) {
        	int j = sqlitedatabase.update("photo_plusone", contentvalues, "photo_id=?", new String[] {
                    s
                });
                flag1 = false;
                if(j != 0)
                    flag1 = true; 
        } else { 
             flag1 = false;
             if(sqlitedatabase.insertWithOnConflict("photo_plusone", null, contentvalues, 4) != -1L)
                 flag1 = true;
        }
        return flag1;
    }

    public static void insertPhoto(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, DataPhoto dataphoto, Boolean boolean1)
    {
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        long l = System.currentTimeMillis();
        ArrayList arraylist = new ArrayList();
        try {
        	
        	sqlitedatabase.beginTransaction();
            if(EsLog.isLoggable("EsPhotosData", 3))
                EsLog.writeToLog(3, "EsPhotosData", getPhotoOutput(dataphoto, 0));
            HashSet hashset = new HashSet();
            if(insertPhotoInTransaction(sqlitedatabase, dataphoto, boolean1, true, null, hashset, arraylist, esaccount.getGaiaId()) == null && EsLog.isLoggable("EsPhotosData", 5))
                Log.w("EsPhotosData", (new StringBuilder("Could not insert row for photo of me; id: ")).append(dataphoto.id).toString());
            EsPeopleData.replaceUsersInTransaction(sqlitedatabase, new ArrayList(hashset));
            sqlitedatabase.setTransactionSuccessful();
            if(EsLog.isLoggable("EsPhotosData", 4))
                Log.i("EsPhotosData", (new StringBuilder("[INSERT_PHOTO], photo ID: ")).append(dataphoto.id).append(", duration: ").append(getDeltaTime(l)).toString());
            Uri uri;
            for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); context.getContentResolver().notifyChange(uri, null))
                uri = (Uri)iterator.next();
            context.getContentResolver().notifyChange(EsProvider.PHOTO_URI, null);
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    private static Long insertPhotoInTransaction(SQLiteDatabase sqlitedatabase, DataPhoto dataphoto, Boolean boolean1, boolean flag, Map map, Set set, List list, String s)
    {
    	if(null == dataphoto.album) {
    		if(EsLog.isLoggable("EsPhotosData", 5))
                Log.w("EsPhotosData", (new StringBuilder("Cannot add photo that has no album; photo id: ")).append(dataphoto.id).toString());
    		return null;
    	}
    	
    	Long long4 = null;
    	HashMap hashmap;
        Cursor cursor = null;
        DataAlbum dataalbum = dataphoto.album;
        String s1 = getAlbumId(dataalbum);
        Long long1;
        Long long2;
        if(map != null)
            long1 = (Long)map.get(s1);
        else
            long1 = null;
        if(long1 == null)
            try
            {
                String as6[] = new String[1];
                as6[0] = dataalbum.id;
                long1 = Long.valueOf(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT entity_version FROM album WHERE album_id = ?", as6));
                if(map != null)
                    map.put(s1, long1);
            }
            catch(SQLiteDoneException sqlitedoneexception)
            {
                long1 = null;
                if(map != null)
                {
                    map.put(s1, Long.valueOf(-1L));
                    long1 = null;
                }
            }
        long2 = dataalbum.entityVersion;
        if(long1 == null || long2 != null && !long1.equals(long2))
        {
            if(insertOrUpdateAlbumRow(sqlitedatabase, dataalbum) == null)
            {
                if(EsLog.isLoggable("EsPhotosData", 5))
                    Log.w("EsPhotosData", (new StringBuilder("Could not insert album row; album id: ")).append(s1).toString());
                return null;
            }
            if(map != null)
                map.put(s1, long2);
        }
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("photo_id", Long.valueOf(Long.parseLong(dataphoto.id)));
        contentvalues.put("plus_one_key", Integer.valueOf(0));
        contentvalues.put("album_id", s1);
        contentvalues.put("url", dataphoto.original.url);
        contentvalues.put("title", dataphoto.title);
        if(!TextUtils.isEmpty(dataphoto.caption))
            contentvalues.put("description", dataphoto.caption);
        Boolean boolean2 = dataphoto.viewerCanComment;
        Boolean boolean3 = dataphoto.viewerCanTag;
        Boolean boolean4 = Boolean.valueOf(false);
        Boolean boolean5 = Boolean.valueOf(false);
        byte byte0;
        int i;
        byte byte1;
        int j;
        byte byte2;
        int k;
        byte byte3;
        Long long3;
        byte abyte0[];
        String as1[];
        String as2[];
        if(boolean2 != null && boolean2.booleanValue())
            byte0 = 2;
        else
            byte0 = 0;
        i = byte0 | 0;
        if(boolean3 != null && boolean3.booleanValue())
            byte1 = 4;
        else
            byte1 = 0;
        j = i | byte1;
        if(boolean4 != null && boolean4.booleanValue())
            byte2 = 8;
        else
            byte2 = 0;
        k = j | byte2;
        if(boolean5 != null && boolean5.booleanValue())
            byte3 = 16;
        else
            byte3 = 0;
        contentvalues.put("action_state", Integer.valueOf(byte3 | k));
        if(dataphoto.totalComments != null)
        {
            int k1;
            if(dataphoto.comment == null)
                k1 = 0;
            else
                k1 = dataphoto.comment.size();
            if(k1 != 0)
            {
                contentvalues.put("comment_count", Integer.valueOf(k1));
                if(EsLog.isLoggable("EsPhotosData", 5) && k1 != dataphoto.totalComments.intValue())
                    Log.w("EsPhotosData", (new StringBuilder("WARN: comment mismatch; total: ")).append(dataphoto.totalComments).append(", actual: ").append(k1).toString());
            } else
            {
                contentvalues.put("comment_count", dataphoto.totalComments);
            }
        }
        if(dataphoto.owner != null)
            contentvalues.put("owner_id", dataphoto.owner.id);
        if(dataphoto.timestampSeconds != null && dataphoto.timestampSeconds.doubleValue() > 0.0D)
        {
            long3 = Long.valueOf(dataphoto.timestampSeconds.longValue());
        } else
        {
            Double double1 = dataphoto.uploadTimestampSeconds;
            long3 = null;
            if(double1 != null)
            {
                double l = dataphoto.uploadTimestampSeconds.doubleValue() - 0.0D;
                if(l > 0)
                    long3 = Long.valueOf(dataphoto.uploadTimestampSeconds.longValue());
            }
        }
        if(long3 != null)
            contentvalues.put("timestamp", Long.valueOf(1000L * long3.longValue()));
        contentvalues.put("entity_version", dataphoto.entityVersion);
        abyte0 = getFingerPrint(dataphoto);
        if(abyte0 != null)
            contentvalues.put("fingerprint", abyte0);
        if(dataphoto.video != null)
            contentvalues.put("video_data", dataphoto.video.toJsonString());
        if(dataphoto.isPanorama != null && dataphoto.isPanorama.booleanValue())
            contentvalues.put("is_panorama", Integer.valueOf(1));
        if(dataphoto.uploadStatus != null)
            contentvalues.put("upload_status", dataphoto.uploadStatus);
        else
            contentvalues.put("upload_status", "ORIGINAL");
        if(boolean1 != null)
            contentvalues.put("downloadable", boolean1);
        if(dataphoto.original != null)
        {
            if(dataphoto.original.width != null)
                contentvalues.put("width", dataphoto.original.width);
            if(dataphoto.original.height != null)
                contentvalues.put("height", dataphoto.original.height);
        }
        long4 = getPhotoRowId(sqlitedatabase, dataphoto.id);
        if(long4 == null)
        {
            long4 = Long.valueOf(sqlitedatabase.insertWithOnConflict("photo", null, contentvalues, 4));
            if(long4.longValue() == -1L)
                long4 = null;
        } else
        {
            String as[] = new String[1];
            as[0] = dataphoto.id;
            if(sqlitedatabase.update("photo", contentvalues, "photo_id=?", as) == 0)
                long4 = null;
        }
        if(set != null)
            set.add(dataphoto.owner);
        if(dataphoto.plusOne != null)
            insertOrUpdatePhotoPlusOneRow(sqlitedatabase, dataphoto.plusOne, dataphoto.id);
        else
            deletePhotoPlusOneRow(sqlitedatabase, dataphoto.id);
        hashmap = new HashMap();
        as1 = (new String[] {
            "comment_id", "update_time"
        });
        as2 = new String[1];
        as2[0] = dataphoto.id;
        cursor = sqlitedatabase.query("photo_comment", as1, "photo_id=?", as2, null, null, null, null);
        while(cursor.moveToNext()) 
            hashmap.put(cursor.getString(0), Long.valueOf(cursor.getLong(1)));
        
        List list1 = dataphoto.comment;
        boolean flag1 = false;
        if(list1 != null)
        {
            Iterator iterator1 = dataphoto.comment.iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                DataComment datacomment = (DataComment)iterator1.next();
                Long long5 = (Long)hashmap.remove(datacomment.id);
                ContentValues contentvalues2 = getCommentContentValues(datacomment, dataphoto.id);
                if(long5 == null || !long5.equals(datacomment.lastUpdateTimestamp))
                {
                    if(set != null)
                        set.add(datacomment.user);
                    insertOrUpdatePhotoCommentRow(sqlitedatabase, datacomment.id, contentvalues2);
                    flag1 = true;
                }
            } while(true);
        }
        if(hashmap.size() > 0)
        {
            ArrayList arraylist = new ArrayList(hashmap.size());
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("comment_id IN(");
            String s2;
            for(Iterator iterator = hashmap.keySet().iterator(); iterator.hasNext(); arraylist.add(s2))
            {
                s2 = (String)iterator.next();
                stringbuilder.append("?,");
            }

            stringbuilder.setLength(-1 + stringbuilder.length());
            stringbuilder.append(")");
            sqlitedatabase.delete("photo_comment", stringbuilder.toString(), (String[])arraylist.toArray(new String[0]));
        }
        if(flag1 || hashmap.size() > 0)
        {
            Uri uri = Uri.withAppendedPath(EsProvider.PHOTO_COMMENTS_BY_PHOTO_ID_URI, dataphoto.id);
            if(list != null)
                list.add(uri);
        }
        String as3[] = new String[1];
        as3[0] = dataphoto.id;
        sqlitedatabase.delete("photo_shape", "photo_id=?", as3);
        if(dataphoto.shape != null)
        {
            int i1 = dataphoto.shape.size();
            int j1 = 0;
            while(j1 < i1) 
            {
                DataShape datashape = (DataShape)dataphoto.shape.get(j1);
                DataUser datauser = datashape.user;
                boolean flag2;
                ContentValues contentvalues1;
                if(datauser != null && datauser.displayName != null && datauser.id != null && !"0".equals(datauser.id) || datashape.suggestion != null)
                    flag2 = true;
                else
                    flag2 = false;
                if(!flag2)
                    continue;
                if(set != null && datauser != null)
                    set.add(datauser);
                contentvalues1 = getShapeContentValues(datashape, dataphoto.id, s);
                if(contentvalues1 != null)
                {
                    String as4[] = new String[1];
                    as4[0] = datashape.id;
                    if(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM photo_shape WHERE shape_id=?", as4) == 0L)
                    {
                        if(sqlitedatabase.insertWithOnConflict("photo_shape", null, contentvalues1, 4) == -1L);
                    } else
                    {
                        String as5[] = new String[1];
                        as5[0] = datashape.id;
                        if(sqlitedatabase.update("photo_shape", contentvalues1, "shape_id=?", as5) == 0);
                    }
                }
                j1++;
            }
            Uri uri2 = Uri.withAppendedPath(EsProvider.PHOTO_SHAPES_BY_PHOTO_ID_URI, dataphoto.id);
            if(list != null)
                list.add(uri2);
        }
        if(long4 != null)
        {
            Uri uri1 = Uri.withAppendedPath(EsProvider.PHOTO_BY_PHOTO_ID_URI, dataphoto.id);
            if(list != null)
                list.add(uri1);
        }
        
        return long4;
    }

    private static void insertPhotosInTransaction(SQLiteDatabase sqlitedatabase, List list, Boolean boolean1, DataAlbum dataalbum, Map map, String s, ContentValues contentvalues, List list1, 
            EsSyncAdapterService.SyncState syncstate)
    {
        // TODO
    }

    public static void insertStreamPhotos(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, String s, String s1, List list, boolean flag)
    {
        // TODO
    }

    public static void insertUserPhotos(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate, List list, List list1, String s)
    {
        SQLiteDatabase sqlitedatabase;
        long l;
        int i;
        int j;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        l = System.currentTimeMillis();
        ArrayList arraylist = new ArrayList();
        ContentValues contentvalues;
        Iterator iterator;
        Uri uri1;
        if(list == null)
            i = 0;
        else
            i = list.size();
        if(list1 == null)
            j = 0;
        else
            j = list1.size();
        
        try {
	        sqlitedatabase.beginTransaction();
	        sqlitedatabase.delete("photos_of_user", "photo_of_user_id=?", new String[] {
	            s
	        });
	        if(EsLog.isLoggable("EsPhotosData", 3))
	            Log.d("EsPhotosData", ">>>>> approved photos");
	        contentvalues = new ContentValues();
	        contentvalues.put("photo_of_user_id", s);
	        insertPhotosInTransaction(sqlitedatabase, list, null, null, null, "photos_of_user", contentvalues, arraylist, syncstate);
	        if(EsLog.isLoggable("EsPhotosData", 3))
	            Log.d("EsPhotosData", ">>>>> unapproved photos");
	        insertPhotosInTransaction(sqlitedatabase, list1, null, null, null, "photos_of_user", contentvalues, arraylist, syncstate);
	        sqlitedatabase.setTransactionSuccessful();
	        if(EsLog.isLoggable("EsPhotosData", 4))
	            Log.i("EsPhotosData", (new StringBuilder("[INSERT_USER_PHOTOS], userId: ")).append(s).append(", approved: ").append(i).append(", unapproved: ").append(j).append(", duration: ").append(getDeltaTime(l)).toString());
	        for(iterator = arraylist.iterator(); iterator.hasNext(); context.getContentResolver().notifyChange(uri1, null))
	            uri1 = (Uri)iterator.next();
	        if(i > 0 || j > 0)
	        {
	            Uri uri = Uri.withAppendedPath(EsProvider.PHOTO_OF_USER_ID_URI, s);
	            context.getContentResolver().notifyChange(uri, null);
	        }
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }

    public static void setPhotosFromPostsAlbumName(String s)
    {
        sPhotosFromPostsAlbumName = s;
    }

    static boolean syncTopLevel(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        boolean flag;
        if(syncstate.isCanceled())
        {
            flag = false;
        } else
        {
            if(EsLog.isLoggable("EsPhotosData", 3))
                Log.d("EsPhotosData", "    #syncTopLevel(); start");
            String s = esaccount.getGaiaId();
            syncstate.onStart("Photos:TopLevel");
            flag = true;
            PhotosOfUserOperation photosofuseroperation = new PhotosOfUserOperation(context, esaccount, syncstate, s, false, null, null);
            photosofuseroperation.start();
            if(photosofuseroperation.hasError())
            {
                Log.w("EsPhotosData", (new StringBuilder("    #syncTopLevel(); failed user photo; code: ")).append(photosofuseroperation.getErrorCode()).append(", reason: ").append(photosofuseroperation.getReasonPhrase()).toString());
                flag = false;
            }
            UserPhotoAlbumsOperation userphotoalbumsoperation = new UserPhotoAlbumsOperation(context, esaccount, syncstate, s, null, null);
            userphotoalbumsoperation.start();
            if(userphotoalbumsoperation.hasError())
            {
                Log.w("EsPhotosData", (new StringBuilder("    #syncTopLevel(); failed photo albums; code: ")).append(userphotoalbumsoperation.getErrorCode()).append(", reason: ").append(userphotoalbumsoperation.getReasonPhrase()).toString());
                flag = false;
            }
            PhotosInAlbumOperation photosinalbumoperation = new PhotosInAlbumOperation(context, esaccount, syncstate, "camerasync", s, false, null, null);
            photosinalbumoperation.start();
            if(photosinalbumoperation.hasError())
            {
                Log.w("EsPhotosData", (new StringBuilder("    #syncTopLevel(); failed camera photos; code: ")).append(photosinalbumoperation.getErrorCode()).append(", reason: ").append(photosinalbumoperation.getReasonPhrase()).toString());
                flag = false;
            }
            PhotosInAlbumOperation photosinalbumoperation1 = new PhotosInAlbumOperation(context, esaccount, syncstate, "posts", s, false, null, null);
            photosinalbumoperation1.start();
            if(photosinalbumoperation1.hasError())
            {
                Log.w("EsPhotosData", (new StringBuilder("    #syncTopLevel(); failed post photos; code: ")).append(photosinalbumoperation1.getErrorCode()).append(", reason: ").append(photosinalbumoperation1.getReasonPhrase()).toString());
                flag = false;
            }
            if(flag && EsLog.isLoggable("EsPhotosData", 3))
                Log.d("EsPhotosData", "    #syncTopLevel(); completed");
            syncstate.onFinish();
        }
        return flag;
    }

    public static void updateInstantUploadCover(Context context, EsAccount esaccount, DataPhoto dataphoto)
    {
        SQLiteDatabase sqlitedatabase;
        long l;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        l = System.currentTimeMillis();
        ContentValues contentvalues;
        long l1;
        sqlitedatabase.beginTransaction();
        contentvalues = new ContentValues(6);
        contentvalues.put("type", "from_my_phone");
        contentvalues.put("sort_order", Integer.valueOf(30));
        contentvalues.putNull("photo_count");
        l1 = getPhotosHomeRowId(sqlitedatabase, "from_my_phone");
        if(l1 == -1L) {
        	long l2 = sqlitedatabase.insertWithOnConflict("photo_home", null, contentvalues, 4);
            l1 = l2;
        } else { 
        	sqlitedatabase.update("photo_home", contentvalues, "type=?", new String[] {
                    "from_my_phone"
                });
        }
        
        String as[] = new String[1];
        as[0] = Long.toString(l1);
        sqlitedatabase.delete("photo_home_cover", "photo_home_key=?", as);
        if(dataphoto != null)
        {
            contentvalues.clear();
            contentvalues.put("photo_home_key", Long.valueOf(l1));
            if(!TextUtils.isEmpty(dataphoto.id))
                contentvalues.put("photo_id", dataphoto.id);
            contentvalues.put("url", dataphoto.original.url);
            contentvalues.put("width", dataphoto.original.width);
            contentvalues.put("height", dataphoto.original.height);
            contentvalues.put("size", dataphoto.fileSize);
            sqlitedatabase.insertWithOnConflict("photo_home_cover", null, contentvalues, 4);
        }
        sqlitedatabase.setTransactionSuccessful();
        sqlitedatabase.endTransaction();
        if(EsLog.isLoggable("EsPhotosData", 4))
            Log.i("EsPhotosData", (new StringBuilder("[INSERT_COVER_INSTANT_UPLOAD], duration: ")).append(getDeltaTime(l)).toString());
        return;
    }

    public static void updatePhotoComment(Context context, EsAccount esaccount, Comment comment)
    {
        SQLiteDatabase sqlitedatabase;
        long l;
        String as[];
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        l = System.currentTimeMillis();
        as = new String[1];
        as[0] = comment.commentId;
        String s = Long.toString(DatabaseUtils.longForQuery(sqlitedatabase, "SELECT photo_id FROM photo_comment WHERE comment_id = ?", as));
        if(EsLog.isLoggable("EsPhotosData", 3))
        {
            StringBuilder stringbuilder = new StringBuilder();
            String s1 = stringbuilder.toString();
            stringbuilder.setLength(0);
            stringbuilder.append(s1).append("COMMENT [id: ").append(comment.commentId).append(", content: ").append(comment.text);
            stringbuilder.append("]");
            EsLog.writeToLog(3, "EsPhotosData", stringbuilder.toString());
        }
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("photo_id", s);
        contentvalues.put("comment_id", comment.commentId);
        contentvalues.put("author_id", comment.obfuscatedId);
        contentvalues.put("content", comment.text);
        if(comment.timestamp != null)
            contentvalues.put("create_time", comment.timestamp);
        if(comment.updatedTimestampUsec != null)
            contentvalues.put("update_time", comment.updatedTimestampUsec);
        if(comment.plusone != null)
            contentvalues.put("plusone_data", comment.plusone.toJsonString());
        insertOrUpdatePhotoCommentRow(sqlitedatabase, comment.commentId, contentvalues);
        if(EsLog.isLoggable("EsPhotosData", 4))
            Log.i("EsPhotosData", (new StringBuilder("[UPDATE_PHOTO_COMMENTS], photo ID: ")).append(s).append(", comment ID: ").append(comment.commentId).append(", duration: ").append(getDeltaTime(l)).toString());
        Uri uri = Uri.withAppendedPath(EsProvider.PHOTO_COMMENTS_BY_PHOTO_ID_URI, s);
        context.getContentResolver().notifyChange(uri, null);
        context.getContentResolver().notifyChange(EsProvider.PHOTO_URI, null);
    }

    public static void updatePhotoCommentList(Context context, EsAccount esaccount, String s, List list)
    {
        ArrayList arraylist;
        int i;
        SQLiteDatabase sqlitedatabase;
        long l;
        arraylist = new ArrayList();
        int j;
        DataComment datacomment;
        ContentValues contentvalues;
        StringBuilder stringbuilder;
        String s1;
        Uri uri;
        if(list == null)
            i = 0;
        else
            i = list.size();
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        l = System.currentTimeMillis();
        try {
	        sqlitedatabase.beginTransaction();
	        for(j = 0; j < i; j++) {
	        	datacomment = (DataComment)list.get(j);
	            contentvalues = getCommentContentValues(datacomment, s);
	            if(EsLog.isLoggable("EsPhotosData", 3))
	            {
	                stringbuilder = new StringBuilder();
	                s1 = stringbuilder.toString();
	                stringbuilder.setLength(0);
	                stringbuilder.append(s1).append("COMMENT [id: ").append(datacomment.id).append(", content: ").append(datacomment.text);
	                stringbuilder.append("]");
	                EsLog.writeToLog(3, "EsPhotosData", stringbuilder.toString());
	            }
	            if(insertOrUpdatePhotoCommentRow(sqlitedatabase, datacomment.id, contentvalues))
	                updateCommentCount(sqlitedatabase, s, 1);
	            uri = Uri.withAppendedPath(EsProvider.PHOTO_COMMENTS_BY_PHOTO_ID_URI, s);
	            context.getContentResolver().notifyChange(uri, null);
	            context.getContentResolver().notifyChange(EsProvider.PHOTO_URI, null);
	        }
	        sqlitedatabase.setTransactionSuccessful();
	        int k = arraylist.size();
	        for(int i1 = 0; i1 < k; i1++)
	            context.getContentResolver().notifyChange((Uri)arraylist.get(i1), null);
        } finally {
        	sqlitedatabase.endTransaction();
        	if(EsLog.isLoggable("EsPhotosData", 4))
	            Log.i("EsPhotosData", (new StringBuilder("[INSERT_PHOTO_COMMENTS], photo ID: ")).append(s).append(", num comments: ").append(i).append(", duration: ").append(getDeltaTime(l)).toString());
        }
    }

    public static void updatePhotoPlusOne(Context context, EsAccount esaccount, String s, DataPlusOne dataplusone)
    {
        if(EsLog.isLoggable("EsPhotosData", 3))
            Log.d("EsPhotosData", (new StringBuilder(">> updatePlusOne; photo id: ")).append(s).toString());
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        Uri uri;
        if(dataplusone != null)
            insertOrUpdatePhotoPlusOneRow(sqlitedatabase, dataplusone, s);
        else
            deletePhotoPlusOneRow(sqlitedatabase, s);
        uri = Uri.withAppendedPath(EsProvider.PHOTO_BY_PHOTO_ID_URI, s);
        context.getContentResolver().notifyChange(uri, null);
    }
    
    public static boolean updatePhotoCommentPlusOne(Context context, EsAccount esaccount, String s, String s1, DataPlusOne dataplusone, boolean flag)
    {
        // TODO
    	return false;
    }

    public static boolean updatePhotoCommentPlusOne(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        if(EsLog.isLoggable("EsPhotosData", 3))
            Log.d("EsPhotosData", (new StringBuilder(">>>>> updatePhotoCommentPlusOne photo id: ")).append(s).append(", commentId: ").append(s1).append(" ").append(flag).toString());
        return updatePhotoCommentPlusOne(context, esaccount, s, s1, updatePlusOne(getCommentPlusOneData(EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase(), s1), flag), false);
    }

    public static void updatePhotoPlusOne(Context context, EsAccount esaccount, String s, boolean flag)
    {
        SQLiteDatabase sqlitedatabase;
        Cursor cursor = null;
        boolean flag1;
        DataPlusOne dataplusone;
        DataPlusOne dataplusone1;
        Uri uri;
        boolean flag2;
        String s1;
        if(EsLog.isLoggable("EsPhotosData", 3))
        {
            StringBuilder stringbuilder = (new StringBuilder(">> updatePlusOne; photo id: ")).append(s);
            if(flag)
                s1 = "";
            else
                s1 = " (un)";
            Log.d("EsPhotosData", stringbuilder.append(s1).append(" +1'd").toString());
        }
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        
        try {
	        cursor = sqlitedatabase.query("photo_plusone", new String[] {
	            "plusone_data"
	        }, "photo_id=?", new String[] {
	            s
	        }, null, null, null);
	        flag1 = cursor.moveToFirst();
	        dataplusone = null;
	        if(flag1)
	        {
	            flag2 = cursor.isNull(0);
	            dataplusone = null;
	            if(!flag2)
	                dataplusone = (DataPlusOne)JsonUtil.fromByteArray(cursor.getBlob(0), DataPlusOne.class);
	        }
	        dataplusone1 = updatePlusOne(dataplusone, flag);
	        if(null != dataplusone1) {
	        	insertOrUpdatePhotoPlusOneRow(sqlitedatabase, dataplusone1, s);
	        } else {
	        	deletePhotoPlusOneRow(sqlitedatabase, s);
	        }
	        uri = Uri.withAppendedPath(EsProvider.PHOTO_BY_PHOTO_ID_URI, s);
	        context.getContentResolver().notifyChange(uri, null);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static void updatePhotoPlusOne$95d6774(Context context, EsAccount esaccount, String s, DataPlusOne dataplusone)
    {
        if(EsLog.isLoggable("EsPhotosData", 3))
            Log.d("EsPhotosData", (new StringBuilder(">> updatePlusOne; photo id: ")).append(s).toString());
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        Uri uri;
        if(dataplusone != null)
            insertOrUpdatePhotoPlusOneRow(sqlitedatabase, dataplusone, s);
        else
            deletePhotoPlusOneRow(sqlitedatabase, s);
        uri = Uri.withAppendedPath(EsProvider.PHOTO_BY_PHOTO_ID_URI, s);
        context.getContentResolver().notifyChange(uri, null);
    }

    public static void updatePhotoShapeApproval(Context context, EsAccount esaccount, long l, long l1, boolean flag)
    {
       // TODO
    }

    public static void updatePhotosOfYouCover(Context context, EsAccount esaccount, DataPhoto dataphoto)
    {
        SQLiteDatabase sqlitedatabase;
        long l;
        sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        l = System.currentTimeMillis();
        ContentValues contentvalues;
        long l1;
        sqlitedatabase.beginTransaction();
        contentvalues = new ContentValues(6);
        contentvalues.put("type", "photos_of_me");
        contentvalues.put("sort_order", Integer.valueOf(20));
        contentvalues.putNull("photo_count");
        l1 = getPhotosHomeRowId(sqlitedatabase, "photos_of_me");
        if(l1 == -1L) {
        	long l2 = sqlitedatabase.insertWithOnConflict("photo_home", null, contentvalues, 4);
            l1 = l2;
        } else {
        	sqlitedatabase.update("photo_home", contentvalues, "type=?", new String[] {
                    "photos_of_me"
                });
        }
        
        String as[] = new String[1];
        as[0] = Long.toString(l1);
        sqlitedatabase.delete("photo_home_cover", "photo_home_key=?", as);
        if(dataphoto != null)
        {
            contentvalues.clear();
            contentvalues.put("photo_home_key", Long.valueOf(l1));
            if(!TextUtils.isEmpty(dataphoto.id))
                contentvalues.put("photo_id", dataphoto.id);
            contentvalues.put("url", dataphoto.original.url);
            contentvalues.put("width", dataphoto.original.width);
            contentvalues.put("height", dataphoto.original.height);
            contentvalues.put("size", dataphoto.fileSize);
            sqlitedatabase.insertWithOnConflict("photo_home_cover", null, contentvalues, 4);
        }
        sqlitedatabase.setTransactionSuccessful();
        sqlitedatabase.endTransaction();
        if(EsLog.isLoggable("EsPhotosData", 4))
            Log.i("EsPhotosData", (new StringBuilder("[INSERT_COVER_PHOTOS_OF_YOU], duration: ")).append(getDeltaTime(l)).toString());
        return;
    }

    private static DataPlusOne updatePlusOne(DataPlusOne dataplusone, boolean flag)
    {
    	DataPlusOne dataplusone1;
        if(dataplusone == null) {
            dataplusone1 = null;
            if(flag)
            {
                dataplusone1 = new DataPlusOne();
                dataplusone1.isPlusonedByViewer = Boolean.valueOf(true);
                dataplusone1.globalCount = Integer.valueOf(1);
            }
            return dataplusone1;
        } else {
        	if(flag) {
        		dataplusone.isPlusonedByViewer = Boolean.valueOf(true);
                dataplusone.globalCount = Integer.valueOf(1 + dataplusone.globalCount.intValue());
        	} else {
        		dataplusone.isPlusonedByViewer = Boolean.valueOf(false);
                if(dataplusone.globalCount.intValue() > 0)
                {
                    dataplusone.globalCount = Integer.valueOf(-1 + dataplusone.globalCount.intValue());
                }
        	}
        	
        	dataplusone.id = null;
            dataplusone1 = dataplusone;
            return dataplusone1;
        }
    }
}
