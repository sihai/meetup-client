/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.graphics.Bitmap;
import android.util.Log;

import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class EsPhotosData {

	static final int FINGERPRINT_STREAM_PREFIX_LENGTH = 6;
	
	static void cleanupData(SQLiteDatabase sqlitedatabase, EsAccount esaccount)
    {
        long l = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM photo", null);
        long l1 = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM album", null);
        String s = esaccount.getGaiaId();
        sqlitedatabase.delete("photos_of_user", "photo_of_user_id!=?", new String[] {
            s
        });
        sqlitedatabase.delete("photos_in_stream", "photo_id IN ( SELECT photo_id FROM photos_by_stream_view WHERE owner_id!=? )", new String[] {
            s
        });
        sqlitedatabase.delete("photos_in_album", "photo_id IN ( SELECT photo_id FROM photos_by_album_view WHERE owner_id!=? )", new String[] {
            s
        });
        sqlitedatabase.delete("photos_in_event", "event_id NOT IN ( SELECT event_id FROM events )", null);
        sqlitedatabase.delete("photo", "photo_id NOT IN ( SELECT photo_id FROM photos_in_album) AND photo_id NOT IN ( SELECT photo_id FROM photos_in_event) AND photo_id NOT IN ( SELECT photo_id FROM photos_in_stream) AND photo_id NOT IN ( SELECT photo_id FROM photos_of_user) AND photo_id NOT IN ( SELECT cover_photo_id FROM album) AND album_id NOT IN ( SELECT album_id FROM activities) ", null);
        sqlitedatabase.delete("album", "owner_id != ? AND album_id NOT IN ( SELECT DISTINCT album_id FROM photo) ", new String[] {
            s
        });
        long l2 = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM photo", null);
        long l3 = DatabaseUtils.longForQuery(sqlitedatabase, "SELECT count(*) FROM album", null);
        if(EsLog.isLoggable("EsPhotosData", 3))
        {
            Log.d("EsPhotosData", (new StringBuilder("cleanupData removed dead photos; was: ")).append(l).append(", now: ").append(l2).toString());
            Log.d("EsPhotosData", (new StringBuilder("cleanupData removed dead albums; was: ")).append(l1).append(", now: ").append(l3).toString());
        }
    }

    static Map getAlbumEntityMap(SQLiteDatabase sqlitedatabase, String s)
    {
        Cursor cursor = null;
        Map map = new HashMap();
        try {
	        cursor = sqlitedatabase.query("album", new String[] {
	            "album_id", "entity_version"
	        }, "owner_id=? AND title IS NOT NULL", new String[] {
	            s
	        }, null, null, null);
	        while(cursor.moveToNext()) 
	        	map.put(cursor.getString(0), Long.valueOf(cursor.getLong(1)));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        return map;
    }

    static Long getAlbumRowId(SQLiteDatabase sqlitedatabase, String s) {
    	
    	Cursor cursor = null;
    	try {
	        cursor = sqlitedatabase.query("album", new String[] {
	            "_id"
	        }, "album_id=?", new String[] {
	            s
	        }, null, null, null);
       
	        if(cursor.moveToNext()) {
	        	return Long.valueOf(cursor.getLong(0));
	        } else {
	        	return null;
	        }
    	} finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    static Map getCurrentAlbumMap(SQLiteDatabase sqlitedatabase, String s, String s1)
    {
        Cursor cursor = null;
        Map map = new HashMap();
        try {
	        cursor = sqlitedatabase.query("photos_by_album_view", new String[] {
	            "photo_id", "entity_version"
	        }, "owner_id=? AND album_id=?", new String[] {
	            s1, s
	        }, null, null, null);
	        while(cursor.moveToNext()) 
	        	map.put(Long.valueOf(cursor.getLong(0)), Long.valueOf(cursor.getLong(1)));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        return map;
    }

    static Map getCurrentStreamMap(SQLiteDatabase sqlitedatabase, String s, String s1)
    {
        Cursor cursor = null;
        Map map = new HashMap();
        try {
	        cursor = sqlitedatabase.query("photos_by_stream_view", new String[] {
	            "photo_id", "entity_version"
	        }, "owner_id=? AND stream_id=?", new String[] {
	            s1, s
	        }, null, null, null);
	        while(cursor.moveToNext()) 
	        	map.put(Long.valueOf(cursor.getLong(0)), Long.valueOf(cursor.getLong(1)));
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        return map;
    }

    static String getDeltaTime(long l)
    {
        StringBuilder stringbuilder = new StringBuilder();
        long l1 = System.currentTimeMillis() - l;
        stringbuilder.append(l1 / 1000L).append(".").append(l1 % 1000L).append(" seconds");
        return stringbuilder.toString();
    }

    static Long getPhotoRowId(SQLiteDatabase sqlitedatabase, String s)
    {
    	Cursor cursor = null;
    	try {
	    	cursor = sqlitedatabase.query("photo", new String[] {
	            "_id"
	        }, "photo_id=?", new String[] {
	            s
	        }, null, null, null);
	    	
	    	if(cursor.moveToNext()) {
	    		return Long.valueOf(cursor.getLong(0));
	    	} else {
	    		return null;
	    	}
    	} finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    static long getPhotosHomeRowId(SQLiteDatabase sqlitedatabase, String s) {
    	
    	Cursor cursor = null;
    	
    	try {
	        cursor = sqlitedatabase.query("photo_home", new String[] {
	            "_id"
	        }, "type=?", new String[] {
	            s
	        }, null, null, null);
	        if(cursor.moveToNext()) {
	        	return cursor.getLong(0);
	        } else {
	        	return -1L;
	        }
    	} finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    static final byte[] hexToBytes(CharSequence charsequence)
    {
        byte abyte0[];
        if(charsequence == null || charsequence.length() == 0)
        {
            abyte0 = null;
        } else
        {
            abyte0 = new byte[(1 + charsequence.length()) / 2];
            abyte0[0] = 0;
            int i = charsequence.length() % 2;
            int j = 0;
            while(j < charsequence.length()) 
            {
                char c = charsequence.charAt(j);
                boolean flag;
                if(c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F')
                    flag = true;
                else
                    flag = false;
                if(!flag)
                    throw new IllegalArgumentException("string contains non-hex chars");
                if(i % 2 == 0)
                {
                    abyte0[i >> 1] = (byte)(hexValue(c) << 4);
                } else
                {
                    int k = i >> 1;
                    abyte0[k] = (byte)(abyte0[k] + (byte)hexValue(c));
                }
                i++;
                j++;
            }
        }
        return abyte0;
    }

    private static final int hexValue(char c)
    {
        int i;
        if(c >= '0' && c <= '9')
            i = c - 48;
        else
        if(c >= 'a' && c <= 'f')
            i = 10 + (c - 97);
        else
            i = 10 + (c - 65);
        return i;
    }

    public static Bitmap loadLocalBitmap(Context context, LocalImageRequest localimagerequest)
    {
    	try {
	    	android.net.Uri uri = localimagerequest.getUri();
	        int i = localimagerequest.getWidth();
	        int j = localimagerequest.getHeight();
	        android.content.ContentResolver contentresolver = context.getContentResolver();
	        String s = ImageUtils.getMimeType(contentresolver, uri);
	        Bitmap bitmap;
	        if(ImageUtils.isImageMimeType(s))
	        {
	        	return ImageUtils.createLocalBitmap(contentresolver, uri, Math.max(i, j));
	        }
	        if(ImageUtils.isVideoMimeType(s))
	        {
	            return MediaStoreUtils.getThumbnail(context, uri, i, j);
	        }
	        if(EsLog.isLoggable("EsPhotosData", 5))
	            Log.w("EsPhotosData", (new StringBuilder("LocalImageRequest#loadBytes: unknown mimeType=")).append(s).toString());
	        return null;
    	} catch (OutOfMemoryError outofmemoryerror) {
    		if(EsLog.isLoggable("EsPhotosData", 6))
                Log.e("EsPhotosData", "Could not load image", outofmemoryerror);
    	} catch (IOException ioexception) {
    		if(EsLog.isLoggable("EsPhotosData", 6))
                Log.e("EsPhotosData", "Could not load image", ioexception);
    	}
    	return null;
    }

    public static void syncPhotos(Context context, EsAccount esaccount, EsSyncAdapterService.SyncState syncstate)
    {
        EsPhotosDataApiary.syncTopLevel(context, esaccount, syncstate);
    }

    static void updateCommentCount(SQLiteDatabase sqlitedatabase, String s, int i) {
    	
    	if(0 == i) {
    		return;
    	}
    	
    	String as[] = {
                s
            };
        long l1;
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("SELECT comment_count").append(" FROM photo").append(" WHERE photo_id=?");
        l1 = DatabaseUtils.longForQuery(sqlitedatabase, stringbuilder.toString(), as);
        long l = l1;
        if(l >= 0L)
        {
            ContentValues contentvalues = new ContentValues();
            contentvalues.put("comment_count", Long.valueOf(Math.max(l + (long)i, 0L)));
            try {
            	sqlitedatabase.update("photo", contentvalues, "photo_id=?", as);
            } catch (SQLiteDoneException sqlitedoneexception) {
            	// TODO log
            }
        }
    }
}