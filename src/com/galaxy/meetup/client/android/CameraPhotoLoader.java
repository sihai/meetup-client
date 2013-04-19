/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class CameraPhotoLoader extends CameraAlbumLoader {

	public CameraPhotoLoader(Context context)
    {
        super(context, null);
    }

    protected final Cursor buildMatrixCursor(Context context, Cursor acursor[], Uri auri[])
    {
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(PhotoPagerLoader.PhotoQuery.PROJECTION);
		do {
		 	int i = -1;
	        Cursor cursor = null;
	        long l = -1L;
	        int j = 0;
			try {
		        while(j < acursor.length) 
		        {
		            Cursor cursor1 = acursor[j];
		            if(cursor1 == null || cursor1.isAfterLast())
		                continue;
		            long l2;
		            if(cursor1.isNull(1))
		                l2 = 0L;
		            else
		                l2 = cursor1.getLong(1);
		            if(l2 > l)
		            {
		                l = l2;
		                i = j;
		            }
		            j++;
		        }
		        if(i == -1) {
		        	return esmatrixcursor;
		        }
		        cursor = acursor[i];
		        long l1 = cursor.getLong(0);
		        String s = cursor.getString(2);
		        Uri uri = ContentUris.withAppendedId(auri[i], l1);
		        byte abyte0[] = MediaStoreUtils.toVideoDataBytes(context, uri);
		        esmatrixcursor.newRow().add(Long.valueOf(l1)).add(Long.valueOf(0L)).add(uri.toString()).add(null).add(s).add(abyte0).add(null);
			} finally {
				if(null != cursor) {
					cursor.moveToNext();
				}
			}
		} while(true);			
    }
}
