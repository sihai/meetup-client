/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.galaxy.picasa.sync.PicasaFacade;

/**
 * 
 * @author sihai
 *
 */
public class GalleryUtils {

	private static final String ACCOUNT_PROJECTION[] = {
        "account"
    };
    private static final String PHOTOID_PROJECTION[] = {
        "picasa_id"
    };
    private static final String USERID_PROJECTION[] = {
        "user_id"
    };
    private static final String USER_ACCOUNT_PROJECTION[] = {
        "user_account"
    };
    
    public static String getAccountName(Context context, Uri uri) {
    	
    	try {
    		return getAccountNameNew(context, uri);
    	} catch (UnsupportedOperationException unsupportedoperationexception) {
    		return getAccountNameOld(context, uri);
    	}
    }

    private static String getAccountNameNew(Context context, Uri uri) throws UnsupportedOperationException {
    	Cursor cursor = null;
    	try {
    		String s = null;
    		cursor = context.getContentResolver().query(uri, USER_ACCOUNT_PROJECTION, null, null, null);
    		if(null != cursor && cursor.moveToNext()) {
	    		if(!cursor.isNull(0)){
	    			s = cursor.getString(0);
	    		}
    		}
    		
    		if(null == s) {
    			throw new UnsupportedOperationException("old version of Gallery");
    		}
    		return s;
    	} finally {
    		if(cursor != null)
                cursor.close();
    	}
    }

    private static String getAccountNameOld(Context context, Uri uri) {
        long l = getPhotoIdOld(uri);
        
        if(0L == l) {
        	return null;
        }
        Cursor cursor = null;
        ContentResolver contentresolver = context.getContentResolver();
        Uri uri1 = PicasaFacade.get(context).getPhotosUri();
        String as[] = USERID_PROJECTION;
        String as1[] = new String[1];
        as1[0] = Long.toString(l);
        
        try {
        	cursor = contentresolver.query(uri1, as, "_id = ?", as1, null);
        	if(null == cursor || !cursor.moveToFirst()) {
        		return null;
        	}
        	int i = cursor.getInt(0);
        	
        	Cursor cursor1 = null;
        	Uri uri2 = PicasaFacade.get(context).getUsersUri();
            String as2[] = ACCOUNT_PROJECTION;
            String as3[] = new String[1];
            as3[0] = Integer.toString(i);
            
            try {
            	cursor1 = contentresolver.query(uri2, as2, "_id = ?", as3, null);
            	if(null == cursor1 || !cursor1.moveToFirst()) {
            		return null;
            	}
            	return cursor1.getString(0);
            } finally {
            	if(null != cursor1) {
            		cursor1.close();
            	}
            }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public static long getPhotoId(Context context, Uri uri) {
    	
    	try {
    		return getPhotoIdNew(context, uri);
    	} catch (UnsupportedOperationException unsupportedoperationexception) {
    		return getPhotoIdOld(uri);
    	}
    }

    private static long getPhotoIdNew(Context context, Uri uri) throws UnsupportedOperationException {
        Cursor cursor = null;
        
        try {
        	Long id = null;
        	cursor = context.getContentResolver().query(uri, PHOTOID_PROJECTION, null, null, null);
        	if(cursor == null || !cursor.moveToNext()) {
        		id = null;
        	}
        	
        	if(cursor.isNull(0)) {
        		id = null;
        	}
        	id = cursor.getLong(0);
        	if(null == id) {
        		throw new UnsupportedOperationException("old version of Gallery");
        	}
        	return id;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    private static long getPhotoIdOld(Uri uri) {
        if(!isGalleryContentUri(uri)) 
        	return 0L; 
        List list = uri.getPathSegments();
        if(list == null || list.size() != 3)
        {
        	return 0L;
        }
        if(!"picasa".equals(list.get(0)) || !"item".equals(list.get(1)))
        {
        	return 0L;
        }
        
        try {
        	return Long.parseLong((String)list.get(2));
        } catch (NumberFormatException numberformatexception) {
        	if(EsLog.isLoggable("GalleryUtils", 4))
                Log.i("GalleryUtils", (new StringBuilder("Invalid photo ID; uri: ")).append(uri.toString()).toString());
        	return 0L;
        }
    }

    public static boolean isGalleryContentUri(Uri uri)
    {
        boolean flag;
        if(uri != null && "content".equals(uri.getScheme()) && "com.google.android.gallery3d.provider".equals(uri.getAuthority()))
            flag = true;
        else
            flag = false;
        return flag;
    }
}
