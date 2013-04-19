/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class PhotosHomeGridLoader extends EsCursorLoader {

	private static final String CAMERA_PHOTO_PROJECTION[] = {
        "_id", "datetaken"
    };
    private static final Uri CAMERA_URI[];
    public static final String PROJECTION[] = {
        "_id", "photo_count", "notification_count", "timestamp", "type", "album_id", "owner_id", "stream_id", "title", "photo_id_1", 
        "url_1", "photo_id_2", "url_2", "photo_id_3", "url_3", "photo_id_4", "url_4", "photo_id_5", "url_5"
    };
    private static long sRowId;
    private final EsAccount mAccount;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private boolean mObserverRegistered;
    private final String mOwnerGaiaId;
    private final boolean mPhotosHome;
    private final boolean mShowLocalCameraAlbum;
    private String mUserName;

    static 
    {
        Uri auri[] = new Uri[4];
        auri[0] = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        auri[1] = MediaStoreUtils.PHONE_STORAGE_IMAGES_URI;
        auri[2] = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        auri[3] = MediaStoreUtils.PHONE_STORAGE_VIDEO_URI;
        CAMERA_URI = auri;
    }
    
    public PhotosHomeGridLoader(Context context, EsAccount esaccount, String s, String s1, boolean flag, boolean flag1)
    {
        super(context, null);
        mAccount = esaccount;
        mOwnerGaiaId = s;
        mUserName = s1;
        mPhotosHome = flag;
        mShowLocalCameraAlbum = flag1;
    }

    private void loadCameraAlbum(EsMatrixCursor esmatrixcursor) {
        String s = getContext().getResources().getString(R.string.photos_home_local_label);
        String s1 = null;
        ContentResolver contentresolver = getContext().getContentResolver();
        
        Cursor cursor = null;
        for(int i = 0; i < CAMERA_URI.length; i++) {
        	try {
        		cursor = null;
	        	cursor = contentresolver.query(CAMERA_URI[i], CAMERA_PHOTO_PROJECTION, null, null, "datetaken desc");
	        	if(null != cursor && cursor.moveToFirst()) {
	        		long l = cursor.getLong(0);
	        		s1 = ContentUris.withAppendedId(CAMERA_URI[i], l).toString();
	        		// TODO
	        		if(null != s1) {
	        			break;
	        		}
	        	}
        	} finally {
        		if(null != cursor) {
        			cursor.close();
        		}
        	}
        }
        
        if(s1 != null)
        {
            Long along[] = new Long[1];
            along[0] = Long.valueOf(0L);
            writeMatrix(esmatrixcursor, null, null, null, "camera_photos", null, null, null, s, along, new String[] {
                s1
            });
        }
    }

    private static String logDelta(long l)
    {
        long l1 = System.currentTimeMillis() - l;
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(l1 / 1000L);
        stringbuffer.append(".");
        stringbuffer.append(l1 % 1000L);
        stringbuffer.append(" sec");
        return stringbuffer.toString();
    }

    private void processAlbumCursor(Cursor cursor, EsMatrixCursor esmatrixcursor)
    {
        long l = System.currentTimeMillis();
        Object obj = null;
        int i = 0;
        Long long1 = null;
        Long long2 = null;
        String s = null;
        String s1 = null;
        String s2 = null;
        String as[] = new String[1];
        Long along[] = new Long[1];
        do
        {
            if(!cursor.moveToNext())
                break;
            String s3 = cursor.getString(2);
            if(s3 != null)
            {
                if(!TextUtils.equals(s3, ((CharSequence) (obj))))
                {
                    if(obj != null)
                        writeMatrix(esmatrixcursor, long1, null, long2, null, ((String) (obj)), s1, s2, s, along, as);
                    obj = s3;
                    i = 0;
                    if(cursor.isNull(1))
                        long1 = null;
                    else
                        long1 = Long.valueOf(cursor.getLong(1));
                    if(cursor.isNull(5))
                        long2 = null;
                    else
                        long2 = Long.valueOf(cursor.getLong(5));
                    if(cursor.isNull(6))
                        s = null;
                    else
                        s = cursor.getString(6);
                    if(cursor.isNull(3))
                        s1 = null;
                    else
                        s1 = cursor.getString(3);
                    if(cursor.isNull(4))
                        s2 = null;
                    else
                        s2 = cursor.getString(4);
                    as = new String[1];
                    along = new Long[1];
                }
                if(i <= 0)
                {
                    String s4;
                    Long long3;
                    if(cursor.isNull(8))
                        s4 = null;
                    else
                        s4 = cursor.getString(8);
                    if(cursor.isNull(7))
                        long3 = null;
                    else
                        long3 = Long.valueOf(cursor.getLong(7));
                    as[i] = s4;
                    along[i] = long3;
                    i++;
                }
            }
        } while(true);
        if(obj != null)
            writeMatrix(esmatrixcursor, long1, null, long2, null, ((String) (obj)), s1, s2, s, along, as);
        if(EsLog.isLoggable("PhotosHomeLoader", 3))
            Log.d("PhotosHomeLoader", (new StringBuilder("#processAlbumCursor; ")).append(logDelta(l)).toString());
    }

    private static void writeMatrix(EsMatrixCursor esmatrixcursor, Long long1, Long long2, Long long3, String s, String s1, String s2, String s3, 
            String s4, Long along[], String as[])
    {
        EsMatrixCursor.RowBuilder rowbuilder = esmatrixcursor.newRow();
        long l = sRowId;
        sRowId = 1L + l;
        rowbuilder.add(Long.valueOf(l)).add(long1).add(long2).add(long3).add(s).add(s1).add(s2).add(s3).add(s4);
        if(along != null)
        {
            for(int i = 0; i < along.length; i++)
                rowbuilder.add(along[i]).add(as[i]);

        }
    }
    
    public final Cursor esLoadInBackground() {
        // TODO
    	return null;
    }

    protected final void onAbandon()
    {
        if(mObserverRegistered)
        {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
            mObserverRegistered = false;
        }
    }

    protected final void onReset()
    {
        onAbandon();
    }

    protected final void onStartLoading()
    {
        ContentResolver contentresolver = getContext().getContentResolver();
        if(!mObserverRegistered)
        {
            if(mPhotosHome)
                contentresolver.registerContentObserver(EsProvider.PHOTO_HOME_URI, false, mObserver);
            contentresolver.registerContentObserver(Uri.withAppendedPath(EsProvider.PHOTO_OF_USER_ID_URI, mOwnerGaiaId), false, mObserver);
            contentresolver.registerContentObserver(Uri.withAppendedPath(EsProvider.ALBUM_VIEW_BY_OWNER_URI, mOwnerGaiaId), false, mObserver);
            mObserverRegistered = true;
        }
        forceLoad();
    }
}
