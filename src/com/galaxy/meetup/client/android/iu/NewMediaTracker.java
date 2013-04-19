/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.util.Iterator;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.ExifInterface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * @author sihai
 *
 */
public class NewMediaTracker {

	private static final Config ALL_CONFIGS[];
    private static final String EXIF_TAGS[] = {
        "FNumber", "DateTime", "ExposureTime", "Flash", "FocalLength", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", 
        "GPSLongitude", "GPSLongitudeRef", "GPSProcessingMethod", "GPSTimeStamp", "ImageLength", "ImageWidth", "ISOSpeedRatings", "Make", "Model", "Orientation"
    };
    private static final String MEDIA_RECORD_TABLE;
    private static final String PROJECTION_ID[] = {
        "_id"
    };
    private static final String PROJECTION_MAX_ID[] = {
        "MAX(_id)"
    };
    private static final String SELECT_MEDIA_NOT_UPLOADED_BY_ACCOUNT;
    private static final int UPLOAD_ACCOUNT_INDEX;
    private static final int UPLOAD_REASON_INDEX;
    private static NewMediaTracker sMediaTracker;
    private final Context mContext;
    private final SharedPreferences mPreferences;
    private boolean mStopProcessing;
    private final TrackRecord mTrackRecords[];
    private final UploadsDatabaseHelper mUploadsDbHelper;

    static 
    {
        UPLOAD_REASON_INDEX = MediaRecordEntry.SCHEMA.getColumnIndex("upload_reason");
        UPLOAD_ACCOUNT_INDEX = MediaRecordEntry.SCHEMA.getColumnIndex("upload_account");
        MEDIA_RECORD_TABLE = MediaRecordEntry.SCHEMA.getTableName();
        SELECT_MEDIA_NOT_UPLOADED_BY_ACCOUNT = (new StringBuilder("upload_account IS NULL AND media_id NOT IN ( SELECT media_id FROM ")).append(MEDIA_RECORD_TABLE).append(" WHERE upload_account").append(" = ? )").toString();
        Config aconfig[] = new Config[4];
        aconfig[0] = new Config("photo", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "external");
        aconfig[1] = new Config("photo", android.provider.MediaStore.Images.Media.getContentUri("phoneStorage"), "phoneStorage");
        aconfig[2] = new Config("video", android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "external");
        aconfig[3] = new Config("video", android.provider.MediaStore.Video.Media.getContentUri("phoneStorage"), "phoneStorage");
        ALL_CONFIGS = aconfig;
    }
    
	private NewMediaTracker(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mUploadsDbHelper = UploadsDatabaseHelper.getInstance(context);
        mTrackRecords = new TrackRecord[ALL_CONFIGS.length];
        loadStates();
    }
	
	public static synchronized void clearPreferences(Context context)
    {
        // TODO
    }
	
	private static void clearPreferencesInternal(
			SharedPreferences sharedpreferences) {
		android.content.SharedPreferences.Editor editor = sharedpreferences
				.edit();
		Iterator iterator = sharedpreferences.getAll().keySet().iterator();
		do {
			if (!iterator.hasNext())
				break;
			String s = (String) iterator.next();
			if (s.startsWith("media_scanner."))
				editor.remove(s);
		} while (true);
		editor.commit();
	}
	
	public static synchronized NewMediaTracker getInstance(Context context) {
        NewMediaTracker newmediatracker;
        if(sMediaTracker == null)
            sMediaTracker = new NewMediaTracker(context);
        newmediatracker = sMediaTracker;
        return newmediatracker;
    }
	
	private static long getNextMediaId(ContentResolver contentresolver, Config config, long l) {
		// TODO
		return 0L;
    }
	
	private static long getOptionalLong(ContentResolver contentresolver, Uri uri, String as[], long l) {
        Cursor cursor = null;
        
        try {
        	cursor = contentresolver.query(uri, as, null, null, null);
        	if(cursor.moveToNext()) {
        		return cursor.getLong(0);
        	}
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        return 0L;
    }
	
	private static String getOptionalString(ContentResolver contentresolver, Uri uri, String as[]) {
		 Cursor cursor = null;
	        
	        try {
	        	cursor = contentresolver.query(uri, as, null, null, null);
	        	if(cursor.moveToNext()) {
	        		return cursor.getString(0);
	        	}
	        } finally {
	        	if(null != cursor) {
	        		cursor.close();
	        	}
	        }
	        return null;
    }
	
	private static boolean hasExif(ExifInterface exifinterface) {
		// TODO
		return false;
    }
	
	private static boolean hasGoogleUploadExif(ContentResolver contentresolver, Uri uri) {
		// TODO
		return false;
    }
	
	private static boolean isStorageAvailable(ContentResolver contentresolver, Config config) {
		// TODO
		return false;
    }
	
	private void loadStates()
    {
        for(int i = -1 + mTrackRecords.length; i >= 0; i--)
        {
            Config config = ALL_CONFIGS[i];
            TrackRecord trackrecord = new TrackRecord(config);
            trackrecord.mLastMediaId = mPreferences.getLong(config.mKeyLastMediaId, 0L);
            mTrackRecords[i] = trackrecord;
        }

    }
	
	private boolean performSanityChecks(ContentResolver contentresolver, UploadSettings uploadsettings, Uri uri, boolean flag)
    {
		// TODO
				return false;
    }
	
	private synchronized void resetPreferencesInternal()
    {
        clearPreferencesInternal(mPreferences);
        loadStates();
        mStopProcessing = false;
    }

    private void saveStates()
    {
        android.content.SharedPreferences.Editor editor = mPreferences.edit();
        TrackRecord atrackrecord[] = mTrackRecords;
        int i = atrackrecord.length;
        for(int j = 0; j < i; j++)
        {
            TrackRecord trackrecord = atrackrecord[j];
            editor.putLong(trackrecord.mConfig.mKeyLastMediaId, trackrecord.mLastMediaId);
        }

        editor.commit();
    }

    final void cancelUpload(String s, int i)
    {
        Cursor cursor = null;
        SQLiteDatabase sqlitedatabase = mUploadsDbHelper.getReadableDatabase();
        String s1 = MEDIA_RECORD_TABLE;
        String as[] = MediaRecordEntry.SCHEMA.getProjection();
        String as1[] = new String[2];
        as1[0] = s;
        as1[1] = Integer.toString(40);
        try {
	        cursor = sqlitedatabase.query(s1, as, "upload_account = ? AND upload_reason = ? AND upload_state < 200", as1, null, null, null);
	        while(cursor.moveToNext()) 
	        {
	            MediaRecordEntry mediarecordentry = MediaRecordEntry.fromCursor(cursor);
	            MediaRecordEntry.SCHEMA.deleteWithId(mUploadsDbHelper.getWritableDatabase(), mediarecordentry.id);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    /*final UploadTaskEntry getNextUpload(HashSet hashset, PicasaSyncHelper.SyncContext synccontext)
    {
    	// TODO
    	return null;
    }*/
    
    final int getUploadProgress(String s, int i)
    {
    	// TODO
    	return 0;
    }
    
    final int getUploadTotal(int i) {
    	Cursor cursor = null;
    	try {
    		cursor = mUploadsDbHelper.getReadableDatabase().query(true, MEDIA_RECORD_TABLE, new String[] {
    				"COUNT(*)"
    		}, "upload_account IS NULL", null, null, null, null, null);
    		if(cursor.moveToNext()) {
    			return cursor.getInt(0);
    		}
    	} finally {
    		if(null != cursor) {
        		cursor.close();
        	}
    	}
    	return 0;
    }
    
    final void onUploadComplete(UploadTaskEntry uploadtaskentry)
    {
        MediaRecordEntry mediarecordentry = MediaRecordEntry.fromId(mUploadsDbHelper.getReadableDatabase(), uploadtaskentry.getMediaRecordId());
        if(mediarecordentry == null)
        {
            Log.w("iu.UploadsManager", (new StringBuilder("Could not get media record for task: ")).append(uploadtaskentry).toString());
        } else
        {
            mediarecordentry.setState(300);
            MediaRecordEntry.SCHEMA.insertOrReplace(mUploadsDbHelper.getWritableDatabase(), mediarecordentry);
        }
    }

    public final synchronized int processNewMedia()
    {
    	// TODO
    	return 0;
    }
    
    final void startUpload(String s, int i)
    {
        SQLiteDatabase sqlitedatabase;
        Cursor cursor = null;
        sqlitedatabase = mUploadsDbHelper.getWritableDatabase();
        
        try {
	        cursor = mUploadsDbHelper.getReadableDatabase().query(true, MEDIA_RECORD_TABLE, MediaRecordEntry.SCHEMA.getProjection(), SELECT_MEDIA_NOT_UPLOADED_BY_ACCOUNT, new String[] {
	            s
	        }, null, null, null, null);
	        while(cursor.moveToNext()) 
	        {
	            MediaRecordEntry mediarecordentry1 = MediaRecordEntry.fromCursor(cursor);
	            mediarecordentry1.id = 0L;
	            mediarecordentry1.setUploadAccount(s);
	            mediarecordentry1.setUploadReason(40);
	            mediarecordentry1.setState(100);
	            MediaRecordEntry.SCHEMA.insertOrReplace(sqlitedatabase, mediarecordentry1);
	        }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public final String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("NewMediaTracker:");
        TrackRecord atrackrecord[] = mTrackRecords;
        int i = atrackrecord.length;
        for(int j = 0; j < i; j++)
        {
            TrackRecord trackrecord = atrackrecord[j];
            stringbuilder.append(";").append(trackrecord.toString());
        }

        return stringbuilder.toString();
    }
	
	private static final class Config
    {

        public final String toString()
        {
            return (new StringBuilder()).append(mMediaType).append("-").append(mStorage).toString();
        }

        public final String mKeyLastMediaId;
        public final Uri mMediaStoreUri;
        public final String mMediaType;
        public final String mStorage;

        public Config(String s, Uri uri, String s1)
        {
            mMediaType = s;
            mStorage = s1;
            mMediaStoreUri = uri;
            mKeyLastMediaId = (new StringBuilder("media_scanner.")).append(s1).append(".").append(s).append(".last_media_id").toString();
        }
    }

    private static final class TrackRecord
    {

        public final String toString()
        {
            return (new StringBuilder()).append(mConfig).append(",").append(mLastMediaId).toString();
        }

        final Config mConfig;
        long mLastMediaId;

        TrackRecord(Config config)
        {
            mConfig = config;
        }
    }
}

