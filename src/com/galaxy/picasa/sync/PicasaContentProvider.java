/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.android.gallery3d.common.Utils;
import com.galaxy.picasa.store.MetricsUtils;
import com.galaxy.picasa.store.PicasaStoreFacade;

/**
 * 
 * @author sihai
 *
 */
public class PicasaContentProvider extends ContentProvider {

	private static final String ALBUM_TABLE_NAME;
    private static final String ALBUM_TYPE_WHERE;
    private static final String PHOTO_TABLE_NAME;
    private static String PROJECTION_CONTENT_URL[] = {
        "content_url"
    };
    private static String PROJECTION_SCREENNAIL_URL[] = {
        "screennail_url"
    };
    private static String PROJECTION_THUMBNAIL_URL[] = {
        "thumbnail_url"
    };
    private static final HashMap SETTING_DEFAULTS;
    private static final HashMap SETTING_DEPRECATED;
    private static final String USER_TABLE_NAME;
    private String mAuthority;
    private PicasaDatabaseHelper mDbHelper;
    private PicasaStoreFacade mPicasaStoreFacade;
    private final UriMatcher mUriMatcher = new UriMatcher(-1);

    static 
    {
        USER_TABLE_NAME = UserEntry.SCHEMA.getTableName();
        ALBUM_TABLE_NAME = AlbumEntry.SCHEMA.getTableName();
        PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
        ALBUM_TYPE_WHERE = (new StringBuilder("_id in (SELECT album_id FROM ")).append(PHOTO_TABLE_NAME).append(" WHERE content_type").append(" LIKE ?)").toString();
        SETTING_DEFAULTS = new HashMap();
        SETTING_DEPRECATED = new HashMap();
        SETTING_DEFAULTS.put("sync_picasa_on_wifi_only", "1");
        SETTING_DEFAULTS.put("sync_on_roaming", "0");
        SETTING_DEFAULTS.put("sync_on_battery", "1");
        SETTING_DEPRECATED.put("sync_photo_on_mobile", "0");
        SETTING_DEPRECATED.put("auto_upload_enabled", "0");
        SETTING_DEPRECATED.put("auto_upload_account_name", null);
        SETTING_DEPRECATED.put("auto_upload_account_type", null);
        SETTING_DEPRECATED.put("sync_on_wifi_only", "1");
        SETTING_DEPRECATED.put("video_upload_wifi_only", "1");
    }
    
    public PicasaContentProvider()
    {
        mPicasaStoreFacade = null;
    }

    private static long getItemIdFromUri(Uri uri)
    {
    	try {
    		return Long.parseLong((String)uri.getPathSegments().get(1));
    	} catch (NumberFormatException numberformatexception) {
    		Log.w("gp.PicasaContentProvider", (new StringBuilder("cannot get id from: ")).append(uri).toString());
    		return -1L;
    	}
    }
    
    private static long getLastSegmentAsLong(Uri uri, long l)
    {
        long l1 = -1L;
        List list = uri.getPathSegments();
        if(list.size() != 0) {
        	String s = (String)list.get(-1 + list.size());
        	try {
        		l1 = Long.parseLong(s);
        	} catch (NumberFormatException numberformatexception) {
        		Log.w("gp.PicasaContentProvider", (new StringBuilder("pasre fail:")).append(uri).toString(), numberformatexception);
        	}
        } else {
        	Log.w("gp.PicasaContentProvider", (new StringBuilder("parse fail: ")).append(uri).toString());
        }
        return l1;
    }

    private String lookupAlbumCoverUrl(long l)
    {
        String s = null;
        Cursor cursor = null;
        SQLiteDatabase sqlitedatabase = PicasaDatabaseHelper.get(getContext()).getReadableDatabase();
        String s1 = ALBUM_TABLE_NAME;
        String as[] = PROJECTION_THUMBNAIL_URL;
        String as1[] = new String[1];
        as1[0] = String.valueOf(l);
        try {
        	cursor = sqlitedatabase.query(s1, as, "_id=?", as1, null, null, null);
        	if(null != cursor && cursor.moveToNext() && !cursor.isNull(0)) {
        		s = cursor.getString(0);
        	}
        } finally {
        	Utils.closeSilently(cursor);
        }
        
        return s;
    }

    private String lookupContentUrl(long l, String s)
    {
        String s1 = null;
        Cursor cursor = null;
        if(s == null)
            s = "full";
        Context context = getContext();
        String as[];
        SQLiteDatabase sqlitedatabase;
        String s2;
        String as1[];
        boolean flag;
        if("full".equals(s))
            as = PROJECTION_CONTENT_URL;
        else
            as = PROJECTION_SCREENNAIL_URL;
        sqlitedatabase = PicasaDatabaseHelper.get(context).getReadableDatabase();
        s2 = PHOTO_TABLE_NAME;
        as1 = new String[1];
        as1[0] = String.valueOf(l);
        
        try {
        	cursor = sqlitedatabase.query(s2, as, "_id=?", as1, null, null, null);
        	if(null != cursor && cursor.moveToNext() && !cursor.isNull(0)) {
        		s1 = cursor.getString(0);
        	}
        } finally {
        	Utils.closeSilently(cursor);
        }
        return s1;
    }

    private synchronized Cursor querySettings(String as[])
    {
        // TODO
    	return null;
    }

    private boolean resetSettings()
    {
        ContentValues contentvalues = new ContentValues();
        java.util.Map.Entry entry;
        for(Iterator iterator = SETTING_DEFAULTS.entrySet().iterator(); iterator.hasNext(); contentvalues.put((String)entry.getKey(), (String)entry.getValue()))
            entry = (java.util.Map.Entry)iterator.next();

        return updateSettings(contentvalues);
    }

    private boolean updateSettings(ContentValues contentvalues)
    {
        // TODO
    	return false;
    }

    public void attachInfo(Context context, ProviderInfo providerinfo)
    {
        super.attachInfo(context, providerinfo);
        mAuthority = providerinfo.authority;
        mUriMatcher.addURI(mAuthority, "photos", 1);
        mUriMatcher.addURI(mAuthority, "albums", 3);
        mUriMatcher.addURI(mAuthority, "posts", 15);
        mUriMatcher.addURI(mAuthority, "posts_album", 16);
        mUriMatcher.addURI(mAuthority, "users", 12);
        mUriMatcher.addURI(mAuthority, "photos/#", 2);
        mUriMatcher.addURI(mAuthority, "albums/#", 4);
        mUriMatcher.addURI(mAuthority, "users/#", 13);
        mUriMatcher.addURI(mAuthority, "settings", 9);
        mUriMatcher.addURI(mAuthority, "sync_request", 10);
        mUriMatcher.addURI(mAuthority, "sync_request/*", 11);
        mUriMatcher.addURI(mAuthority, "albumcovers/#", 14);
    }

    public int delete(Uri uri, String s, String as[])
    {
    	int i;
        int value = mUriMatcher.match(uri);
        if(9 == value) {
        	if(resetSettings())
                i = 1;
            else
                i = 0;
        } else if(10 == value) {
        	throw new IllegalArgumentException((new StringBuilder("unsupported uri:")).append(uri).toString());
        } else if(11 == value) {
            List list = uri.getPathSegments();
            if(list.size() != 2)
                throw new IllegalArgumentException("Invalid URI: expect /sync_request/<task_ID>");
            String s1 = (String)list.get(1);
            if(ImmediateSync.get(getContext()).cancelTask(s1))
                i = 1;
            else
                i = 0;
        } else {
        	throw new IllegalArgumentException((new StringBuilder("unsupported uri:")).append(uri).toString());
        }
        
        return i;
    }

    public String getType(Uri uri)
    {
    	String s = null;
        int value = mUriMatcher.match(uri);
        switch(value) {
        case 1:
        	s = "vnd.android.cursor.dir/vnd.google.android.picasasync.item";
        	break;
        case 2:
        	s = "vnd.android.cursor.item/vnd.google.android.picasasync.item";
        	break;
        case 3:
        	s = "vnd.android.cursor.dir/vnd.google.android.picasasync.album";
        	break;
        case 4:
        	s = "vnd.android.cursor.item/vnd.google.android.picasasync.album";
        	break;
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        	throw new IllegalArgumentException((new StringBuilder("Invalid URI: ")).append(uri).toString());
        case 12:
        	s = "vnd.android.cursor.dir/vnd.google.android.picasasync.user";
        	break;
        case 13:
        	s = "vnd.android.cursor.item/vnd.google.android.picasasync.user";
        	break;
        case 14:
        	s = "vnd.android.cursor.item/vnd.google.android.picasasync.album_cover";
        	break;
        case 15:
        	s = "vnd.android.cursor.dir/vnd.google.android.picasasync.post";
        	break;
        case 16:
        	s = "vnd.android.cursor.dir/vnd.google.android.picasasync.post_album";
        	break;
        default:
        	throw new IllegalArgumentException((new StringBuilder("Invalid URI: ")).append(uri).toString());
        }
        return s;
    }

    public Uri insert(Uri uri, ContentValues contentvalues)
    {
        // TODO
    	return null;
    }

    public boolean onCreate()
    {
        mDbHelper = PicasaDatabaseHelper.get(getContext());
        return true;
    }

    public ParcelFileDescriptor openFile(Uri uri, String s) throws FileNotFoundException
    {
        // TODO
    	return null;
    }

    public Cursor query(Uri uri, String as[], String s, String as1[], String s1)
    {
        // TODO
    	return null;
    }

    public int update(Uri uri, ContentValues contentvalues, String s, String as[])
    {
    	int i;
        int value = mUriMatcher.match(uri);
        if(4 == value) {
            Integer integer = contentvalues.getAsInteger("cache_flag");
            i = 0;
            if(integer != null)
            {
                long l = getLastSegmentAsLong(uri, -1L);
                if(l != -1L)
                    PrefetchHelper.get(getContext()).setAlbumCachingFlag(l, integer.intValue());
                i = 1;
            }
        } else if(9 == value) {
        	boolean flag = updateSettings(contentvalues);
            i = 0;
            if(flag)
                i = 1;
        } else {
        	throw new IllegalArgumentException((new StringBuilder("unsupported uri:")).append(uri).toString());
        }
        
        return i;
    }
}
