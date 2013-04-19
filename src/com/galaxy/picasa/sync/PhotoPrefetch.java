/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.galaxy.picasa.store.MetricsUtils;

/**
 * 
 * @author sihai
 *
 */
public class PhotoPrefetch implements SyncTaskProvider {

	private final Context mContext;
    private final int mImageType;
    private final SharedPreferences mPrefs;
    
    public PhotoPrefetch(Context context, int i)
    {
        mContext = context;
        mImageType = i;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }
    
    static void onRequestSync(Context context)
    {
        compareAndSetCleanBit(PreferenceManager.getDefaultSharedPreferences(context), 0, 2);
    }
    
    private static synchronized boolean compareAndSetCleanBit(SharedPreferences sharedpreferences, int i, int j)
    {
        int k = sharedpreferences.getInt("picasasync.prefetch.clean-cache", 0);
        boolean flag = false;
        if(k == i) {
        	sharedpreferences.edit().putInt("picasasync.prefetch.clean-cache", j).commit();
            flag = true;
        }
        return flag;
    }
    
    public final void collectTasks(Collection collection)
    {
        PicasaSyncHelper picasasynchelper = PicasaSyncHelper.getInstance(mContext);
        android.database.sqlite.SQLiteDatabase sqlitedatabase = picasasynchelper.getWritableDatabase();
        switch(mImageType)
        {
        default:
            throw new AssertionError();

        case 2: // '\002'
            for(Iterator iterator2 = picasasynchelper.getUsers().iterator(); iterator2.hasNext();) {
            	UserEntry userentry2 = (UserEntry)iterator2.next();
            	if(SyncState.PREFETCH_FULL_IMAGE.isRequested(sqlitedatabase, userentry2.account))
                    collection.add(new PrefetchFullImage(userentry2.account));
            }
            break;
        case 1: // '\001'
            for(Iterator iterator1 = picasasynchelper.getUsers().iterator(); iterator1.hasNext();) {
            	UserEntry userentry1 = (UserEntry)iterator1.next();
            	if(!SyncState.PREFETCH_SCREEN_NAIL.isRequested(sqlitedatabase, userentry1.account)) {
            		continue;
            	} else {
            		collection.add(new PrefetchScreenNail(userentry1.account));
            		break;
            	}
            }
        case 3: // '\003'
            for(Iterator iterator = picasasynchelper.getUsers().iterator(); iterator.hasNext();) {
            	UserEntry userentry = (UserEntry)iterator.next();
            	if(!SyncState.PREFETCH_ALBUM_COVER.isRequested(sqlitedatabase, userentry.account)) {
            		continue;
            	} else {
            		collection.add(new PrefetchAlbumCover(userentry.account));
            		break;
            	}
            }
            break;
        }
    }

	@Override
	public void resetSyncStates() {
        PicasaSyncHelper picasasynchelper = PicasaSyncHelper.getInstance(mContext);
        android.database.sqlite.SQLiteDatabase sqlitedatabase = picasasynchelper.getWritableDatabase();
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        synchronized(PhotoPrefetch.class) {
	        if(sharedpreferences.getInt("picasasync.prefetch.clean-cache", 0) == 1)
	            sharedpreferences.edit().putInt("picasasync.prefetch.clean-cache", 2).commit();
        }
        
        SyncState syncstate = null;
        if(1 == mImageType) {
        	syncstate = SyncState.PREFETCH_SCREEN_NAIL;
        } else if(2 == mImageType) {
        	syncstate = SyncState.PREFETCH_FULL_IMAGE;
        } else if(3 == mImageType) {
        	syncstate = SyncState.PREFETCH_ALBUM_COVER;
        } else {
        	throw new AssertionError();
        }
        
        for(Iterator iterator = picasasynchelper.getUsers().iterator(); iterator.hasNext(); syncstate.resetSyncToDirty(sqlitedatabase, ((UserEntry)iterator.next()).account));
	}
	
	private final class PrefetchAlbumCover extends PrefetchScreenNail {

		public PrefetchAlbumCover(String s)
        {
            super(s, SyncState.PREFETCH_ALBUM_COVER);
        }
		
        public final void performSync(SyncResult syncresult) throws IOException
        {
            int i = MetricsUtils.begin("PrefetchAlbumCover");
            try {
            	performSyncCommon(syncresult);
            	MetricsUtils.endWithReport(i, "picasa.prefetch.thumbnail");
            } catch (Exception exception) {
            	MetricsUtils.endWithReport(i, "picasa.prefetch.thumbnail");
            }
        }

        protected final boolean performSyncInternal(UserEntry userentry, PrefetchHelper prefetchhelper) throws IOException {
            boolean flag = true;
            if(mContext.getExternalCacheDir() != null) {
            	byte byte0 = 2;
                prefetchhelper.syncAlbumCoversForUser(mSyncContext, userentry);
                if(mSyncContext.syncInterrupted())
                    flag = false;
            } else { 
            	Log.w("PhotoPrefetch", "no external storage, skip album cover prefetching");
            }
            return flag;
        }

    }
	
	private class PrefetchScreenNail extends SyncTask {
		
		protected boolean mSyncCancelled;
        protected PrefetchHelper.PrefetchContext mSyncContext;
        private SyncState mSyncState;

        public PrefetchScreenNail(String s)
        {
            this(s, SyncState.PREFETCH_SCREEN_NAIL);
        }

        public PrefetchScreenNail(String s, SyncState syncstate)
        {
            super(s);
            mSyncCancelled = false;
            mSyncState = syncstate;
        }

        public final void cancelSync()
        {
            mSyncCancelled = true;
            if(mSyncContext != null)
                mSyncContext.stopSync();
        }

        public boolean isBackgroundSync()
        {
            return true;
        }

        public final boolean isSyncOnBattery()
        {
            return isSyncOnBattery(mContext);
        }

        public final boolean isSyncOnExternalStorageOnly()
        {
            return true;
        }

        public final boolean isSyncOnRoaming()
        {
            return isSyncOnRoaming(mContext);
        }

        public final boolean isSyncOnWifiOnly()
        {
            return isSyncPicasaOnWifiOnly(mContext);
        }

        public void performSync(SyncResult syncresult) throws IOException
        {
        	int i = MetricsUtils.begin("PrefetchScreenNail");
        	try {
        		performSyncCommon(syncresult);
        		MetricsUtils.endWithReport(i, "picasa.prefetch.screennail");
        	} catch (Exception exception) {
        		MetricsUtils.endWithReport(i, "picasa.prefetch.screennail");
        	}
        }

        protected final void performSyncCommon(SyncResult syncresult) throws IOException
        {
            // TODO
        }

        protected boolean performSyncInternal(UserEntry userentry, PrefetchHelper prefetchhelper) throws IOException
        {
            boolean flag = true;
            if(mContext.getExternalCacheDir() != null) {
            	byte byte0 = 2;
                prefetchhelper.syncScreenNailsForUser(mSyncContext, userentry);
                if(mSyncContext.syncInterrupted())
                    flag = false;
            } else { 
            	Log.w("PhotoPrefetch", "no external storage, skip screenail prefetching");
            }
            return flag;
        }
    }
	
	
	private final class PrefetchFullImage extends PrefetchScreenNail implements PrefetchHelper.PrefetchListener {

		private PrefetchHelper.CacheStats mCacheStats;

	    public PrefetchFullImage(String s)
	    {
	        super(s, SyncState.PREFETCH_FULL_IMAGE);
	    }
	    
	    public final boolean isBackgroundSync()
	    {
	        return false;
	    }

	    public final void onDownloadFinish()
	    {
	        PrefetchHelper.CacheStats cachestats = mCacheStats;
	        cachestats.pendingCount = -1 + cachestats.pendingCount;
	        int i = cachestats.totalCount - cachestats.pendingCount;
	        updateOngoingNotification(i, cachestats.totalCount);
	    }

	    public final void performSync(SyncResult syncresult) throws IOException
	    {
	        int i = MetricsUtils.begin("PrefetchFullImage");
	        try {
	        	performSyncCommon(syncresult);
	        	MetricsUtils.endWithReport(i, "picasa.prefetch.full_image");
	        } catch (Exception exception) {
	        	MetricsUtils.endWithReport(i, "picasa.prefetch.full_image");
	        }
	    }

	    protected final boolean performSyncInternal(UserEntry userentry, PrefetchHelper prefetchhelper) throws IOException {
	        mSyncContext.setCacheDownloadListener(this);
	        byte byte0 = 2;
	        mCacheStats = prefetchhelper.getCacheStatistics(2);
	        if(mCacheStats.pendingCount == 0) {
	        	return true;
	        }
	        
	        boolean flag = false;
	        try {
		        prefetchhelper.syncFullImagesForUser(mSyncContext, userentry);
		        ((NotificationManager)mContext.getSystemService("notification")).cancel(1);
		        if(mCacheStats.pendingCount == 0)
		            showPrefetchCompleteNotification(mCacheStats.totalCount);
		        Exception exception;
		        if(!mSyncContext.syncInterrupted())
		            flag = true;
		        else
		            flag = false;
		        return flag;
	        } catch (Exception e) {
	        	((NotificationManager)mContext.getSystemService("notification")).cancel(1);
	        	return false;
	        }
	    }
	}
	
	final void showPrefetchCompleteNotification(int i)
    {
        RemoteViews remoteviews = new RemoteViews(mContext.getPackageName(), R.layout.ps_cache_notification);
        remoteviews.setTextViewText(R.id.ps_status, mContext.getString(R.string.ps_cache_done));
        remoteviews.setProgressBar(R.id.ps_progress, i, i, false);
        Notification notification = new Notification();
        notification.icon = 0x1080082;
        notification.contentView = remoteviews;
        notification.when = System.currentTimeMillis();
        ((NotificationManager)mContext.getSystemService("notification")).notify(2, notification);
    }
	
	final void updateOngoingNotification(int i, int j)
    {
        Resources resources = mContext.getResources();
        int k = R.string.ps_cache_status;
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(i);
        aobj[1] = Integer.valueOf(j);
        String s = resources.getString(k, aobj);
        RemoteViews remoteviews = new RemoteViews(mContext.getPackageName(), R.layout.ps_cache_notification);
        remoteviews.setTextViewText(R.id.ps_status, s);
        remoteviews.setProgressBar(R.id.ps_progress, j, i, false);
        Notification notification = new Notification();
        notification.icon = 0x1080081;
        notification.contentView = remoteviews;
        notification.when = System.currentTimeMillis();
        notification.flags = 2 | notification.flags;
        ((NotificationManager)mContext.getSystemService("notification")).notify(1, notification);
    }
}
