/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.server.client.domain.DataVideo;
import com.galaxy.meetup.server.client.domain.DataVideoStream;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class MediaStoreUtils {

	private static final Pattern PAT_RESOLUTION = Pattern.compile("(\\d+)[xX](\\d+)");
    public static final Uri PHONE_STORAGE_IMAGES_URI = android.provider.MediaStore.Images.Media.getContentUri("phoneStorage");
    public static final Uri PHONE_STORAGE_VIDEO_URI = android.provider.MediaStore.Video.Media.getContentUri("phoneStorage");
    
    public static final String MEDIA_ID_PROJECTION[] = {
        "_id"
    };
    
	public static boolean deleteLocalFileAndMediaStore(ContentResolver contentresolver, Uri uri) {
        boolean flag = true;
        String s = getFilePath(contentresolver, uri);
        if(contentresolver.delete(uri, null, null) > 0)
            flag = false;
        if(flag && s != null) {
            File file = new File(s);
            if(file.exists())
                flag = file.delete();
        }
        return flag;
    }
	
	public static String getFilePath(ContentResolver contentresolver, Uri uri) {
		Cursor cursor = null;
		try {
			cursor = contentresolver.query(uri, new String[] {"_data"}, null, null, null);
			if(null == cursor) {
				if(EsLog.isLoggable("MediaStoreUtils", 5)) {
					 Log.w("MediaStoreUtils", (new StringBuilder("getFilePath: query returned null cursor for uri=")).append(uri).toString());
				}
				return null;
			}
			if(!cursor.moveToFirst()) {
				if(EsLog.isLoggable("MediaStoreUtils", 5))
		            Log.w("MediaStoreUtils", (new StringBuilder("getFilePath: query returned empty cursor for uri=")).append(uri).toString());
				return null;
			}
			String s = cursor.getString(0);
			if(TextUtils.isEmpty(s)) {
				Log.w("MediaStoreUtils", (new StringBuilder("getFilePath: MediaColumns.DATA was empty for uri=")).append(uri).toString());
				return null;
			}
			return s;
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
    }
	
	public static long getMediaId(ContentResolver contentresolver, Uri uri) {
		
        Cursor cursor = null;
        
        try {
        	cursor = contentresolver.query(uri, MEDIA_ID_PROJECTION, null, null, null);
        	if(null == cursor || !cursor.moveToFirst()) {
        		return -1L;
        	}
        	return cursor.getLong(0);
        } finally {
        	if(null != cursor) {
				cursor.close();
			}
        }
    }
	
	public static Bitmap getThumbnail(Context context, Uri uri, int i) {
        int j = ImageUtils.getMaxThumbnailDimension(context, 1);
        return getThumbnailHelper(context, uri, j, j, 1);
    }

    public static Bitmap getThumbnail(Context context, Uri uri, int i, int j) {
        byte byte0 = 3;
        int k = ImageUtils.getMaxThumbnailDimension(context, byte0);
        if(i > k || j > k)
            byte0 = 1;
        return getThumbnailHelper(context, uri, i, j, byte0);
    }
    
    private static Bitmap getThumbnailHelper(Context context, Uri uri, int i, int j, int k) {
    	
    	if(null == uri) {
    		return null;
    	}
    	if(!isExternalMediaStoreUri(uri)) {
    		return null;
    	}
    	ContentResolver contentresolver = context.getContentResolver();
    	long id = ContentUris.parseId(uri);
        String type = ImageUtils.getMimeType(contentresolver, uri);
        if(ImageUtils.isImageMimeType(type)) {
        	Bitmap bitmap = android.provider.MediaStore.Images.Thumbnails.getThumbnail(contentresolver, id, k, null);
        	if(bitmap != null)
            {
                bitmap = ImageUtils.rotateBitmap(contentresolver, uri, bitmap);
                if(bitmap.getWidth() != i || bitmap.getHeight() != j)
                {
                    Bitmap bitmap1 = ImageUtils.resizeAndCropBitmap(bitmap, i, j);
                    if(bitmap1 != bitmap)
                    {
                        bitmap.recycle();
                        bitmap = bitmap1;
                    }
                }
            }
        	
        	return bitmap;
        } else if(ImageUtils.isVideoMimeType(type)) {
        	return android.provider.MediaStore.Video.Thumbnails.getThumbnail(contentresolver, id, k, null);
        } else {
        	if(EsLog.isLoggable("MediaStoreUtils", 5))
            {
                Log.w("MediaStoreUtils", (new StringBuilder("getThumbnail: unrecognized mimeType=")).append(type).append(", uri=").append(uri).toString());
            }
        	return null;
        }
    }
    
    public static boolean isExternalMediaStoreUri(Uri uri) {
    	if(isMediaStoreUri(uri)) {
    		return false;
    	}
    	String s = uri.getPath();
        String s1 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath();
        String s2 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI.getPath();
        return s.startsWith(s1) || s.startsWith(s2);
    }
    
    public static boolean isMediaStoreUri(Uri uri) {
        boolean flag;
        if(uri != null && "content".equals(uri.getScheme()) && "media".equals(uri.getAuthority()))
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    public static DataVideo toVideoData(Context context, Uri uri)
    {
        ContentResolver contentresolver = context.getContentResolver();
        if(!ImageUtils.isVideoMimeType(ImageUtils.getMimeType(contentresolver, uri))) {
        	return  null;
        }
        
        DataVideoStream datavideostream = new DataVideoStream();
        datavideostream.url = uri.toString();
        datavideostream.formatId = Integer.valueOf(0);
        Cursor cursor = null;
        try {
        	cursor = contentresolver.query(uri, VideoQuery.PROJECTION, null, null, null);
        	if(null == cursor || !cursor.moveToFirst()) {
        		return null;
        	}
        	long l = cursor.getLong(1);
            String s = cursor.getString(2);
            if(null == s) {
            	return null;
            }
            Matcher matcher = PAT_RESOLUTION.matcher(s);
            if(!matcher.find()) {
            	return  null;
            }
            datavideostream.width = Integer.parseInt(matcher.group(1));
            datavideostream.height = Integer.parseInt(matcher.group(2));
            List list = new ArrayList(1);
            list.add(datavideostream);
            DataVideo datavideo = new DataVideo();
            datavideo.status = "FINAL";
            datavideo.durationMillis = Long.valueOf(l);
            datavideo.stream = list;
            return datavideo;
        } finally {
        	if(null != cursor) {
				cursor.close();
			}
        }
    }

    
    public static byte[] toVideoDataBytes(Context context, Uri uri) {
        DataVideo datavideo = toVideoData(context, uri);
        byte abyte0[];
        if(datavideo == null)
            abyte0 = null;
        else
            abyte0 = JsonUtil.toByteArray(datavideo);
        return abyte0;
    }
	
	//===========================================================================
    //						Inner class
    //===========================================================================
	private static interface VideoQuery {

        public static final String PROJECTION[] = {
            "_id", "duration", "resolution"
        };

    }
}
