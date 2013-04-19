/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.util.Collection;
import java.util.Iterator;

import android.content.Context;
import android.content.SyncResult;

/**
 * 
 * @author sihai
 *
 */
public class MetadataSync implements SyncTaskProvider {

	private final Context mContext;
    private final boolean mIsManual;
    
	public MetadataSync(Context context, boolean flag)
    {
        mContext = context;
        mIsManual = flag;
    }

    public final void collectTasks(Collection collection)
    {
        PicasaSyncHelper picasasynchelper = PicasaSyncHelper.getInstance(mContext);
        android.database.sqlite.SQLiteDatabase sqlitedatabase = picasasynchelper.getWritableDatabase();
        SyncState syncstate;
        if(mIsManual)
            syncstate = SyncState.METADATA_MANUAL;
        else
            syncstate = SyncState.METADATA;
        Iterator iterator = picasasynchelper.getUsers().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            UserEntry userentry = (UserEntry)iterator.next();
            if(syncstate.isRequested(sqlitedatabase, userentry.account))
            {
                String s = userentry.account;
                boolean _tmp = mIsManual;
                collection.add(new MetadataSyncTask(s));
            }
        } while(true);
    }

    public final void resetSyncStates()
    {
        PicasaSyncHelper picasasynchelper = PicasaSyncHelper.getInstance(mContext);
        android.database.sqlite.SQLiteDatabase sqlitedatabase = picasasynchelper.getWritableDatabase();
        SyncState syncstate;
        Iterator iterator;
        if(mIsManual)
            syncstate = SyncState.METADATA_MANUAL;
        else
            syncstate = SyncState.METADATA;
        for(iterator = picasasynchelper.getUsers().iterator(); iterator.hasNext(); syncstate.resetSyncToDirty(sqlitedatabase, ((UserEntry)iterator.next()).account));
    }
    
    
    private final class MetadataSyncTask extends SyncTask {

        public final synchronized void cancelSync()
        {
            mSyncCancelled = true;
            if(mSyncContext != null)
                mSyncContext.stopSync();
        }

        public final boolean isBackgroundSync()
        {
            boolean flag;
            if(!mIsManual)
                flag = true;
            else
                flag = false;
            return flag;
        }

        public final boolean isSyncOnBattery()
        {
            return isSyncOnBattery(mContext);
        }

        public final boolean isSyncOnWifiOnly()
        {
            boolean flag;
            if(mIsManual)
                flag = false;
            else
                flag = isSyncPicasaOnWifiOnly(mContext);
            return flag;
        }

        public final void performSync(SyncResult syncresult)
        {
            // TODO
        }

        private boolean mSyncCancelled;
        private PicasaSyncHelper.SyncContext mSyncContext;

        public MetadataSyncTask(String s)
        {
            super(s);
            mSyncCancelled = false;
        }
    }
}
