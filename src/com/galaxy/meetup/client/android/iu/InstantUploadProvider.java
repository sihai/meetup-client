/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.galaxy.meetup.client.android.service.PicasaQuotaChangedReceiver;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.picasa.sync.PhotoEntry;

/**
 * 
 * @author sihai
 *
 */
public class InstantUploadProvider extends ContentProvider {

	private static final Map MEDIA_RECORD_MAP;
    private static final String MEDIA_RECORD_TABLE;
    private static final String PHOTO_TABLE_NAME;
    private static final String PROJECTION_ID[] = {
        "_id"
    };
    private static final Object SETTINGS_LOCK = new Object();
    private static final Map SETTING_DEFAULTS;
    private static final Map SETTING_DEPRECATED;
    private static final Map UPLOAD_TASK_MAP;
    private static final String UPLOAD_TASK_TABLE;
    private static QuotaTask sQuotaTask;
    private String mAuthority;
    private final UriMatcher mUriMatcher = new UriMatcher(-1);

    static {
        UPLOAD_TASK_TABLE = UploadTaskEntry.SCHEMA.getTableName();
        MEDIA_RECORD_TABLE = MediaRecordEntry.SCHEMA.getTableName();
        PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
        UPLOAD_TASK_MAP = new HashMap();
        MEDIA_RECORD_MAP = new HashMap();
        UPLOAD_TASK_MAP.put("_id", "_id");
        UPLOAD_TASK_MAP.put("upload_account", "account");
        UPLOAD_TASK_MAP.put("album_id", "album_id");
        UPLOAD_TASK_MAP.put("bytes_total", "bytes_total");
        UPLOAD_TASK_MAP.put("bytes_uploaded", "bytes_uploaded");
        UPLOAD_TASK_MAP.put("media_url", "content_uri");
        UPLOAD_TASK_MAP.put("event_id", "event_id");
        UPLOAD_TASK_MAP.put("fingerprint", "fingerprint");
        UPLOAD_TASK_MAP.put("upload_state", "state");
        UPLOAD_TASK_MAP.put("media_record_id", "media_record_id");
        MEDIA_RECORD_MAP.put("_id", "_id");
        MEDIA_RECORD_MAP.put("upload_account", "upload_account");
        MEDIA_RECORD_MAP.put("album_id", "album_id");
        MEDIA_RECORD_MAP.put("bytes_total", "bytes_total");
        MEDIA_RECORD_MAP.put("bytes_uploaded", "bytes_uploaded");
        MEDIA_RECORD_MAP.put("media_url", "media_url");
        MEDIA_RECORD_MAP.put("event_id", "event_id");
        MEDIA_RECORD_MAP.put("fingerprint", "fingerprint");
        MEDIA_RECORD_MAP.put("upload_state", "upload_state");
        MEDIA_RECORD_MAP.put("media_id", "media_id");
        SETTING_DEFAULTS = new HashMap();
        SETTING_DEPRECATED = new HashMap();
        SETTING_DEFAULTS.put("auto_upload_account_name", null);
        SETTING_DEFAULTS.put("auto_upload_account_type", null);
        SETTING_DEFAULTS.put("auto_upload_enabled", "0");
        SETTING_DEFAULTS.put("sync_on_wifi_only", "1");
        SETTING_DEFAULTS.put("video_upload_wifi_only", "1");
        SETTING_DEFAULTS.put("sync_on_roaming", "0");
        SETTING_DEFAULTS.put("sync_on_battery", "1");
        SETTING_DEFAULTS.put("instant_share_eventid", null);
        SETTING_DEFAULTS.put("instant_share_starttime", "0");
        SETTING_DEFAULTS.put("instant_share_endtime", "0");
        SETTING_DEFAULTS.put("upload_full_resolution", "1");
        SETTING_DEFAULTS.put("instant_upload_state", Integer.toString(0));
        SETTING_DEFAULTS.put("instant_share_state", Integer.toString(0));
        SETTING_DEFAULTS.put("upload_all_state", Integer.toString(0));
        SETTING_DEFAULTS.put("manual_upload_state", Integer.toString(0));
        SETTING_DEFAULTS.put("quota_limit", Long.toString(-1L));
        SETTING_DEFAULTS.put("quota_used", Long.toString(-1L));
        SETTING_DEFAULTS.put("full_size_disabled", "1");
        SETTING_DEPRECATED.put("sync_photo_on_mobile", "0");
    }
    
    public InstantUploadProvider() {
    }

    private int cancelUploads(Uri uri, String s, String as[]) {
        // TODO
    	return 0;
    }

    static void disableInstantShare(Context context) {
        ContentResolver contentresolver = context.getContentResolver();
        synchronized(SETTINGS_LOCK) {
            android.provider.Settings.System.putString(contentresolver, "com.google.android.picasasync.instant_share_eventid", null);
        }
    }

    private Cursor queryPhotos(Uri uri, String as[]) {
    	// TODO
    	return null;
    }

    private Cursor querySettings(Uri uri, String as[]) {
        // TODO
    	return null;
    }

    private Cursor queryUploads(String s, Map map, String as[], String s1, String as1[], String s2, String s3) {
        SQLiteDatabase sqlitedatabase = UploadsDatabaseHelper.getInstance(getContext()).getReadableDatabase();
        SQLiteQueryBuilder sqlitequerybuilder = new SQLiteQueryBuilder();
        sqlitequerybuilder.setTables(s);
        sqlitequerybuilder.setProjectionMap(map);
        return sqlitequerybuilder.query(sqlitedatabase, as, s1, as1, null, null, s2, s3);
    }

    static void updateQuotaSettings(Context context, String s, GDataUploader.GDataQuota gdataquota) {
        ContentValues contentvalues = new ContentValues(3);
        if(gdataquota.quotaLimit != -1L)
            contentvalues.put("quota_limit", Long.toString(gdataquota.quotaLimit));
        if(gdataquota.quotaUsed != -1L)
            contentvalues.put("quota_used", Long.toString(gdataquota.quotaUsed));
        String s1;
        if(gdataquota.disableFullRes)
            s1 = "1";
        else
            s1 = "0";
        contentvalues.put("full_size_disabled", s1);
        if(EsLog.isLoggable("iu.IUProvider", 4))
            Log.i("iu.IUProvider", (new StringBuilder("Update quota settings; ")).append(gdataquota.toString()).toString());
        if(updateSettings(context, s, contentvalues))
        {
            Intent intent = new Intent(context, PicasaQuotaChangedReceiver.class);
            intent.setAction("com.google.android.apps.plus.iu.QUOTA_CHANGED");
            intent.putExtra("quota_limit", (int)gdataquota.quotaLimit);
            intent.putExtra("quota_used", (int)gdataquota.quotaUsed);
            intent.putExtra("full_size_disabled", gdataquota.disableFullRes);
            context.sendBroadcast(intent);
        }
    }

    private static boolean updateSettings(Context context, String s, ContentValues contentvalues) {
        // TODO
    	return false;
    }

    public void attachInfo(Context context, ProviderInfo providerinfo) {
        super.attachInfo(context, providerinfo);
        mAuthority = providerinfo.authority;
        mUriMatcher.addURI(mAuthority, "uploads", 5);
        mUriMatcher.addURI(mAuthority, "upload_all", 9);
        mUriMatcher.addURI(mAuthority, "iu", 17);
        mUriMatcher.addURI(mAuthority, "settings", 11);
        mUriMatcher.addURI(mAuthority, "photos", 18);
    }

    public int delete(Uri uri, String s, String as[]) {
        int code = mUriMatcher.match(uri);
        int i;
        if(5 == code) {
        	i = cancelUploads(uri, s, as);
        } else if(9 == code) {
        	String s1 = uri.getQueryParameter("account");
            if(s1 != null)
                UploadsManager.getInstance(getContext()).cancelUploadExistingPhotos(s1);
            i = 0;
        } else if(11 == code) {
        	ContentValues contentvalues = new ContentValues();
            java.util.Map.Entry entry;
            for(Iterator iterator = SETTING_DEFAULTS.entrySet().iterator(); iterator.hasNext(); contentvalues.put((String)entry.getKey(), (String)entry.getValue()))
                entry = (java.util.Map.Entry)iterator.next();

            if(updateSettings(getContext(), null, contentvalues))
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
    	int code = mUriMatcher.match(uri);
    	if(5 == code) {
    		s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.iu.upload";
    	} else if(9 == code) {
    		s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.iu.upload_all";
    	} else if(17 == code) {
    		s = "vnd.android.cursor.dir/vnd.google.android.apps.plus.iu.iu";
    	} else if(18 == code) {
    		s = "vnd.android.cursor.item/vnd.google.android.apps.plus.iu.photos_content_uri";
    	} else {
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
        return true;
    }

    public Cursor query(Uri uri, String as[], String s, String as1[], String s1)
    {
        // TODO
    	return null;
    }

    public int update(Uri uri, ContentValues contentvalues, String s, String as[]) {
        String s1;
        switch(mUriMatcher.match(uri)) {
        default:
            throw new IllegalArgumentException((new StringBuilder("unsupported uri:")).append(uri).toString());

        case 11: // '\013'
            s1 = uri.getQueryParameter("account");
            break;
        }
        int i;
        if(updateSettings(getContext(), s1, contentvalues))
            i = 1;
        else
            i = 0;
        return i;
    }

    static final class QuotaTask extends AsyncTask {

        private Void doInBackground() {
            GDataUploader gdatauploader = new GDataUploader(mContext);
            GDataUploader.GDataQuota gdataquota = gdatauploader.getQuota(mAccount);
            try {
            	if(gdataquota != null)
            		InstantUploadProvider.updateQuotaSettings(mContext, mAccount, gdataquota);
            
            	synchronized(InstantUploadProvider.SETTINGS_LOCK) {
            		if(InstantUploadProvider.sQuotaTask == this)
            			InstantUploadProvider.sQuotaTask = null;
            	}
            	return null;
            } finally {
            	gdatauploader.close();
            }
        }

        protected final Object doInBackground(Object aobj[])
        {
            return doInBackground();
        }

        final String getAccount()
        {
            return mAccount;
        }

        private final String mAccount;
        private final Context mContext;

        QuotaTask(Context context, String s)
        {
            mContext = context;
            mAccount = s;
        }
    }
}
