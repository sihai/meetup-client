/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.io.IOException;
import java.util.HashSet;

import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.content.SyncStats;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.gallery3d.common.Utils;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.picasa.sync.PhotoEntry;
import com.galaxy.picasa.sync.PicasaDatabaseHelper;
import com.galaxy.picasa.sync.PicasaSyncHelper;
import com.galaxy.picasa.sync.SyncTask;

/**
 * 
 * @author sihai
 *
 */
public class UploadsManager {

	private static final Uri EXTERNAL_STORAGE_FSID_URI = Uri.parse("content://media/external/fs_id");
    private static final String MEDIA_RECORD_TABLE_NAME;
    private static final String PHOTO_TABLE_NAME;
    private static final String PROJECTION_DATA[] = {
        "_data"
    };
    private static final String PROJECTION_FINGERPRINT[] = {
        "fingerprint"
    };
    private static final String PROJECTION_QUOTA[] = {
        "quota_limit", "quota_used"
    };
    private static UploadsManager sInstance;
    private final AccountManager mAccountManager;
    private final Context mContext;
    private UploadTask mCurrent;
    private int mExternalStorageFsId;
    private final Handler mHandler;
    private volatile boolean mIsExternalStorageFsIdReady;
    private final PicasaDatabaseHelper mPicasaDbHelper;
    private final SharedPreferences mPreferences;
    private HashSet mProblematicAccounts;
    private long mResetDelay;
    private final UploadSettings mSettings;
    private final PicasaSyncHelper mSyncHelper;
    private HashSet mSyncedAccounts;
    private final NewMediaTracker mToddsMediaTracker;
    private String mUploadUrl;
    private final UploadsDatabaseHelper mUploadsDbHelper;

    static 
    {
        MEDIA_RECORD_TABLE_NAME = MediaRecordEntry.SCHEMA.getTableName();
        PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
    }
    
    
    private static final class MessageHandler extends Handler {

    	MessageHandler(Looper looper)
        {
            super(looper);
        }
    	
        public final void handleMessage(Message message) {
        	if(null == UploadsManager.sInstance) {
        		return;
        	}
        	
        	switch(message.what)
            {
            case 4: // '\004'
            default:
                throw new AssertionError((new StringBuilder("unknown message: ")).append(message.what).toString());

            case 6: // '\006'
                if(message.obj instanceof Cursor)
                    UploadsManager.sInstance.mSettings.reloadSettings((Cursor)message.obj);
                else
                    UploadsManager.sInstance.mSettings.reloadSettings(null);
                break;

            case 5: // '\005'
                UploadsManager.sInstance.cancelTaskInternal(((Long)message.obj).longValue());
                break;

            case 1: // '\001'
                UploadsManager.sInstance.sendUploadAllProgressBroadcast();
                break;

            case 2: // '\002'
                UploadsManager.sInstance.uploadExistingPhotosInternal((String)message.obj);
                break;

            case 3: // '\003'
                UploadsManager.sInstance.cancelUploadExistingPhotosInternal((String)message.obj);
                break;

            case 7: // '\007'
                UploadsManager.sInstance.onFsIdChangedInternal();
                break;

            case 8: // '\b'
                if(EsLog.isLoggable("iu.UploadsManager", 3))
                    Log.d("iu.UploadsManager", "Try to reset UploadsManager again!");
                UploadsManager.sInstance.reset();
                break;
            }
        }
        
    }
    
    private final class UploadTask extends SyncTask implements Uploader.UploadProgressListener {
    	
    	protected UploadTaskEntry mCurrentTask;
        private HashSet mFingerprintSet;
        private final boolean mIsPhoto;
        protected String mLogName;
        protected volatile boolean mRunning;
        private final String mStateSetting;
        protected PicasaSyncHelper.SyncContext mSyncContext;
        protected final int mUploadType;
        private final ContentValues mValues;
        
        protected UploadTask(String s, int i, boolean flag)
        {
        	super(s);
            int flag1 = 1;
            mRunning = true;
            mValues = new ContentValues(1);
            mUploadType = i;
            mIsPhoto = flag;
            int j = i << 1;
            if(flag)
                flag1 = 0;
            mPriority = flag1 | j;
            switch(i) {
	            case 10:
	            	mStateSetting = "manual_upload_state";
	                mLogName = "Manual";
	            	break;
	            case 20:
	            	mStateSetting = "instant_share_state";
	                mLogName = "InstantShare";
	            	break;
	            case 30:
	            	mStateSetting = "instant_upload_state";
	                mLogName = "InstantUpload";
	            	break;
	            case 40:
	            	 mStateSetting = "upload_all_state";
	                 mLogName = "UploadAll";
	            	break;
            	default:
            		throw new IllegalArgumentException((new StringBuilder("unknown upload type: ")).append(i).toString());
            }
        }
        
    	private UploadTaskEntry getNextUpload() throws IOException {
    		// TODO
    		return null;
        }
    	
    	private boolean isOutOfQuota(UploadTaskEntry uploadtaskentry) {
    		// TODO
    		return false;
    	}
    	
    	private boolean isUploadedBefore(UploadTaskEntry uploadtaskentry) {
    		// TODO
    		return false;
        }
    	
    	private boolean onIncompleteUpload(UploadTaskEntry uploadtaskentry, boolean flag) {
    		// TODO
    		return false;
    	}
    	
    	private void onTaskDone(UploadTaskEntry uploadtaskentry) {
    		// TODO
        }
    	
    	private void performSyncInternal(SyncResult syncresult) throws IOException {
    		// TODO
    	}
    	
    	private void skipTask(UploadTaskEntry uploadtaskentry, SyncStats syncstats, Throwable throwable)
        {
            setState(uploadtaskentry, 11, throwable);
            syncstats.numSkippedEntries = 1L + syncstats.numSkippedEntries;
            removeTaskFromDb(uploadtaskentry.id);
            MediaRecordEntry mediarecordentry = MediaRecordEntry.fromId(mUploadsDbHelper.getReadableDatabase(), uploadtaskentry.getMediaRecordId());
            if(mediarecordentry != null)
            {
                mediarecordentry.setState(400, 38);
                MediaRecordEntry.SCHEMA.insertOrReplace(mUploadsDbHelper.getWritableDatabase(), mediarecordentry);
            }
            onTaskDone(uploadtaskentry);
        }
    	
    	private boolean syncCameraSyncStream(SyncResult syncresult, String s) {
    		// TODO
    		return false;
        }
    	
    	public final boolean cancelIfCurrentTaskMatches(long l) {
    		// TODO
    		return false;
        }
    	
    	public final void cancelSync()
        {
            synchronized(UploadsManager.this)
            {
                mRunning = false;
                stopCurrentTask(6);
                if(mSyncContext != null)
                    mSyncContext.stopSync();
                if(EsLog.isLoggable("iu.UploadsManager", 4))
                    Log.i("iu.UploadsManager", (new StringBuilder("--- CANCEL sync ")).append(mLogName).toString());
            }
        }

        public final boolean isBackgroundSync()
        {
            boolean flag;
            if(mUploadType != 10)
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final boolean isSyncOnBattery()
        {
            boolean flag;
            if(mUploadType == 10 || mUploadType == 20 || mSettings.getSyncOnBattery())
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final boolean isSyncOnRoaming()
        {
            boolean flag;
            if(mUploadType == 10 || mUploadType == 20 || mSettings.getSyncOnRoaming())
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final boolean isSyncOnWifiOnly()
        {
        	// TODO
        	return false;
        }
        
        public final void onFileChanged(UploadTaskEntry uploadtaskentry)
        {
            FingerprintHelper.get(mContext).invalidate(uploadtaskentry.getContentUri().toString());
        }

        public final void onProgress(UploadTaskEntry uploadtaskentry)
        {
            synchronized(UploadsManager.this)
            {
                if(mRunning)
                {
                    if(EsLog.isLoggable("iu.UploadsManager", 2))
                        Log.v("iu.UploadsManager", (new StringBuilder("  progress: ")).append(uploadtaskentry).toString());
                    updateTaskStateAndProgressInDb(uploadtaskentry);
                    if(mUploadType == 10)
                    {
                        notifyManualUploadDbChanges();
                        android.content.ComponentName componentname = uploadtaskentry.getComponentName();
                    }
                }
            }
        }

        public final void onRejected(int i)
        {
            if(EsLog.isLoggable("iu.UploadsManager", 4))
                Log.i("iu.UploadsManager", (new StringBuilder("REJECT ")).append(mLogName).append(" due to ").append(InstantUploadFacade.stateToString(i)).toString());
            onStateChanged(i);
            if(mUploadType == 40)
                requestUploadAllProgressBroadcast();
        }

        protected final void onStateChanged(int i)
        {
            mValues.clear();
            mValues.put(mStateSetting, Integer.valueOf(i));
            mContext.getContentResolver().update(InstantUploadFacade.SETTINGS_URI, mValues, null, null);
            if(mUploadType == 40)
                requestUploadAllProgressBroadcast();
        }

        public final void performSync(SyncResult syncresult)
            throws IOException
        {
        	// TODO
        }
        
        protected final void stopCurrentTask(int i)
        {
            UploadTaskEntry uploadtaskentry = mCurrentTask;
            if(EsLog.isLoggable("iu.UploadsManager", 3))
                Log.d("iu.UploadsManager", (new StringBuilder("stopCurrentTask: ")).append(uploadtaskentry).toString());
            if(uploadtaskentry != null)
                synchronized(UploadsManager.this)
                {
                    if(uploadtaskentry.isCancellable())
                    {
                        setState(uploadtaskentry, i);
                        notify();
                    }
                }
        }
    }
    
    private final class UploadTaskProvider implements SyncTaskProvider {

	    public final SyncTask getNextTask(String s)
	    {
	        // TODO
	    	return null;
	    }

	    public final void onSyncStart()
	    {
	        if(EsLog.isLoggable("iu.UploadsManager", 3))
	            Log.d("iu.UploadsManager", "onSyncStart");
	        mProblematicAccounts.clear();
	        mSyncedAccounts.clear();
	    }
    }
    
    private UploadsManager(Context context) {
        mProblematicAccounts = new HashSet();
        mSyncedAccounts = new HashSet();
        mIsExternalStorageFsIdReady = false;
        mResetDelay = 15000L;
        mContext = context;
        mAccountManager = AccountManager.get(context);
        mUploadsDbHelper = UploadsDatabaseHelper.getInstance(context);
        mPicasaDbHelper = PicasaDatabaseHelper.get(context);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSyncHelper = PicasaSyncHelper.getInstance(context);
        mSettings = UploadSettings.getInstance(context);
        mToddsMediaTracker = NewMediaTracker.getInstance(context);
        HandlerThread handlerthread = new HandlerThread("picasa-uploads-manager", 10);
        handlerthread.start();
        mHandler = new MessageHandler(handlerthread.getLooper());
        String s = mSettings.getSyncAccount();
        if(s != null && mToddsMediaTracker.getUploadProgress(s, 40) == 0)
            requestUploadAllProgressBroadcast();
        mIsExternalStorageFsIdReady = mPreferences.contains("external_storage_fsid");
        if(mIsExternalStorageFsIdReady)
            mExternalStorageFsId = mPreferences.getInt("external_storage_fsid", -1);
        String s1 = mPreferences.getString("system_release", null);
        boolean flag;
        if(!android.os.Build.VERSION.RELEASE.equals(s1))
        {
            mPreferences.edit().putString("system_release", android.os.Build.VERSION.RELEASE).commit();
            if(EsLog.isLoggable("iu.UploadsManager", 4))
                Log.i("iu.UploadsManager", (new StringBuilder("System upgrade from ")).append(s1).append(" to ").append(android.os.Build.VERSION.RELEASE).toString());
            flag = true;
        } else
        {
            flag = false;
        }
        if(flag)
            reset();
        context.getContentResolver().registerContentObserver(EXTERNAL_STORAGE_FSID_URI, false, new ContentObserver(mHandler) {

            public final void onChange(boolean flag1)
            {
                onFsIdChangedInternal();
            }

        });
        Message.obtain(mHandler, 7).sendToTarget();
    }
    
    private synchronized void cancelTaskInternal(long l)
    {
        if(mCurrent == null || !mCurrent.cancelIfCurrentTaskMatches(l))
        {
            UploadTaskEntry uploadtaskentry = UploadTaskEntry.fromDb(mUploadsDbHelper.getWritableDatabase(), l);
            if(uploadtaskentry != null)
            {
                removeTaskFromDb(l);
                setState(uploadtaskentry, 8);
                notifyManualUploadDbChanges();
                if(EsLog.isLoggable("iu.UploadsManager", 4))
                    Log.i("iu.UploadsManager", (new StringBuilder("--- CANCEL task: ")).append(uploadtaskentry).toString());
            }
        }
    }

    private synchronized void cancelUploadExistingPhotosInternal(String s) {
        if(EsLog.isLoggable("iu.UploadsManager", 4))
            Log.i("iu.UploadsManager", "--- CANCEL upload all");
        mToddsMediaTracker.cancelUpload(s, 40);
        if(mCurrent != null && mCurrent.mUploadType == 40)
            mCurrent.stopCurrentTask(7);
    }

    private synchronized MediaRecordEntry doUpload(UploadTaskEntry uploadtaskentry, Uploader.UploadProgressListener uploadprogresslistener, SyncResult syncresult)
    {
        // TODO
    	return null;
    }

    private static String getFilePath(Uri uri, ContentResolver contentresolver)
    {
    	// TODO
    	return null;
    }

    private static int getFsId(Context context)
    {
       // TODO
    	return 0;
    }

    public static synchronized UploadsManager getInstance(Context context)
    {
        UploadsManager uploadsmanager;
        if(sInstance == null)
            sInstance = new UploadsManager(context);
        uploadsmanager = sInstance;
        return uploadsmanager;
    }

    private static boolean isExternalStorageMounted()
    {
        String s = Environment.getExternalStorageState();
        boolean flag;
        if(s.equals("mounted") || s.equals("mounted_ro"))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void notifyManualUploadDbChanges()
    {
        mContext.getContentResolver().notifyChange(InstantUploadFacade.UPLOADS_URI, null, false);
    }

    private synchronized void onFsIdChangedInternal()
    {
    	// TODO
    }

    private boolean removeTaskFromDb(long l)
    {
        return UploadTaskEntry.SCHEMA.deleteWithId(mUploadsDbHelper.getWritableDatabase(), l);
    }

    private void requestUploadAllProgressBroadcast()
    {
        Message.obtain(mHandler, 1).sendToTarget();
    }

    private synchronized void reset()
    {
        // TODO
    }

    private void sendUploadAllProgressBroadcast()
    {
        // TODO
    }

    private synchronized void setCurrentUploadTask(UploadTask uploadtask)
    {
        mCurrent = uploadtask;
    }

    private void setMediaRecordStateAndProgress(long l, int i, Throwable throwable, boolean flag, long l1, 
            String s)
    {
    	// TODO
    }

    private void updateTaskStateAndProgressInDb(UploadTaskEntry uploadtaskentry)
    {
        // TODO
    }

    private void uploadExistingPhotosInternal(String s)
    {
        // TODO
    }

    private boolean writeToPhotoTable(UploadTaskEntry uploadtaskentry, MediaRecordEntry mediarecordentry)
    {
        // TODO
    	return false;
    }

    public final long addManualUpload(ContentValues contentvalues)
    {
        SQLiteDatabase sqlitedatabase = mUploadsDbHelper.getWritableDatabase();
        MediaRecordEntry mediarecordentry = MediaRecordEntry.createNew(contentvalues);
        mediarecordentry.setUploadReason(10);
        mediarecordentry.setState(100);
        long l = MediaRecordEntry.SCHEMA.insertOrReplace(sqlitedatabase, mediarecordentry);
        if(EsLog.isLoggable("iu.UploadsManager", 4))
            Log.i("iu.UploadsManager", (new StringBuilder("+++ ADD record; manual upload: ")).append(mediarecordentry).toString());
        InstantUploadSyncManager.getInstance(mContext).updateTasks(500L);
        return l;
    }

    public final void cancelTask(long l)
    {
        Message.obtain(mHandler, 5, Long.valueOf(l)).sendToTarget();
    }

    public final void cancelUploadExistingPhotos(String s)
    {
        Message.obtain(mHandler, 3, s).sendToTarget();
    }

    public final Cursor getInstantUploadStatus()
    {
        String s = mSettings.getSyncAccount();
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(new String[] {
            "iu_pending_count"
        });
        int i = mToddsMediaTracker.getUploadProgress(s, 30);
        esmatrixcursor.newRow().add(Integer.valueOf(i));
        if(EsLog.isLoggable("iu.UploadsManager", 3))
            Log.d("iu.UploadsManager", (new StringBuilder("get iu pending count for ")).append(Utils.maskDebugInfo(s)).append(":").append(i).toString());
        return esmatrixcursor;
    }

    public final Cursor getUploadAllStatus(String s)
    {
        boolean flag = true;
        String as[] = new String[4];
        as[0] = "upload_all_account";
        as[1] = "upload_all_progress";
        as[2] = "upload_all_count";
        as[3] = "upload_all_state";
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(as);
        if(s != null)
        {
            int i = mToddsMediaTracker.getUploadTotal(40);
            int j = i - mToddsMediaTracker.getUploadProgress(s, 40);
            if(EsLog.isLoggable("iu.UploadsManager", 3))
            {
                StringBuilder stringbuilder = (new StringBuilder("get upload-all status for ")).append(Utils.maskDebugInfo(s)).append(" allDone? ");
                if(i != j)
                    flag = false;
                Log.d("iu.UploadsManager", stringbuilder.append(flag).append(" current:").append(j).append(" total:").append(i).append(" state=0").toString());
            }
            esmatrixcursor.newRow().add(s).add(Integer.valueOf(j)).add(Integer.valueOf(i)).add(Integer.valueOf(0));
        } else
        {
            esmatrixcursor.newRow().add(null).add(null).add(null).add(Integer.valueOf(0));
        }
        return esmatrixcursor;
    }

    public final SyncTaskProvider getUploadTaskProvider() {
        return new UploadTaskProvider();
    }

    public final void reloadSystemSettings() {
        Cursor cursor = mSettings.getSystemSettingsCursor();
        Message.obtain(mHandler, 6, cursor).sendToTarget();
    }

    final HashSet retrieveAllFingerprints(String s) {
        // TODO
    	return null;
    }

    final synchronized void setState(UploadTaskEntry uploadtaskentry, int i) {
        uploadtaskentry.setState(i);
        setMediaRecordStateAndProgress(uploadtaskentry.getMediaRecordId(), i, null, false, 0L, null);
    }

    final synchronized void setState(UploadTaskEntry uploadtaskentry, int i, Throwable throwable) {
        uploadtaskentry.setState(i, throwable);
        setMediaRecordStateAndProgress(uploadtaskentry.getMediaRecordId(), i, throwable, false, 0L, null);
    }

    public final void uploadExistingPhotos(String s)
    {
        Message.obtain(mHandler, 2, s).sendToTarget();
    }
}
