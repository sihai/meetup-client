/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.util.HashMap;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.gallery3d.util.ThreadPool;

/**
 * 
 * @author sihai
 *
 */
public class ImmediateSync {

	private static ImmediateSync sInstance;
    private final HashMap mCompleteTaskMap = new HashMap();
    private final Context mContext;
    private final HashMap mPendingTaskMap = new HashMap();
    private final ThreadPool mThreadPool = new ThreadPool();
    
    private ImmediateSync(Context context)
    {
        mContext = context;
    }

    private synchronized void completeTask(Task task)
    {
        String s;
        s = task.taskId;
        if(mPendingTaskMap.remove(s) == task) {
        	mCompleteTaskMap.put(s, task);
            Uri uri = PicasaFacade.get(mContext).getSyncRequestUri().buildUpon().appendPath(s).build();
            mContext.getContentResolver().notifyChange(uri, null, false);
            if(task.syncResultCode != 0)
                Log.d("ImmediateSync", (new StringBuilder("sync ")).append(s).append(" incomplete ").append(task.syncResultCode).toString());
        } else {
        	Log.d("ImmediateSync", (new StringBuilder("new task added, ignored old:")).append(s).toString());
        }
    }

    public static synchronized ImmediateSync get(Context context)
    {
        ImmediateSync immediatesync = null;
        if(sInstance == null)
            sInstance = new ImmediateSync(context);
        immediatesync = sInstance;
        return immediatesync;
    }

    private void requestSyncAlbumList(final String final_s, String as[])
    {
       // TODO
    }

    public final synchronized boolean cancelTask(String s)
    {
        boolean flag = true;
        Log.d("ImmediateSync", (new StringBuilder("cancel sync ")).append(s).toString());
        Task task = (Task)mPendingTaskMap.get(s);
        if(task == null || task.refCount <= 0) {
        	flag = false;
        } else {
        	int i = -1 + task.refCount;
            task.refCount = i;
            if(i == 0)
            {
                task.syncResultCode = 1;
                if(task.syncContext != null)
                    task.syncContext.stopSync();
            }
        }
        return flag;
    }

    public final synchronized int getResult(String s)
    {
    	int i;
        Task task;
        task = (Task)mCompleteTaskMap.get(s);
        if(task == null)
            task = (Task)mPendingTaskMap.get(s);
        if(task != null) {
        	i = task.syncResultCode;
        } else { 
        	i = 3;
        }
        
        return i;
    }

    public final String requestSyncAlbum(String s)
    {
        // TODO
    	return null;
    }

    public final synchronized String requestSyncAlbumListForAccount(String s)
    {
        String s1;
        s1 = String.valueOf(s.hashCode());
        Task task = (Task)mPendingTaskMap.get(s1);
        if(task == null || !task.addRequester()) {
        	mCompleteTaskMap.remove(s1);
            requestSyncAlbumList(s1, new String[] {
                s
            });
        } else {
        	Log.d("ImmediateSync", (new StringBuilder("task already exists:")).append(s1).toString());
        }
        
        return s1;
    }

    public final synchronized String requestSyncAlbumListForAllAccounts()
    {
        String s;
        Task task = (Task)mPendingTaskMap.get("all");
        if(task == null || !task.addRequester()) {
        	mCompleteTaskMap.remove("all");
            requestSyncAlbumList("all", null);
            s = "all";
        } else {
        	Log.d("ImmediateSync", "task already exists:all");
            s = "all";
        }
        return s;
    }
    
    private abstract class Task implements com.android.gallery3d.util.ThreadPool.Job {
    	
    	public int refCount;
        public PicasaSyncHelper.SyncContext syncContext;
        public int syncResultCode;
        public final String taskId;

        Task(String s)
        {
            super();
            syncResultCode = -1;
            refCount = 1;
            taskId = s;
        }

	    private Void run()
	    {
	    	try {
		        int i = doSync();
		        synchronized(ImmediateSync.this)
		        {
		            if(syncResultCode == -1)
		                syncResultCode = i;
		        }
		        return null;
	    	} finally {
	    		completeTask(this);
	    	}
	    }
	
	    final boolean addRequester()
	    {
	        boolean flag;
	        if(syncResultCode == -1 || syncResultCode == 0)
	        {
	            refCount = 1 + refCount;
	            flag = true;
	        } else
	        {
	            flag = false;
	        }
	        return flag;
	    }
	
	    protected abstract int doSync();
	
	    public final Object run(com.android.gallery3d.util.ThreadPool.JobContext jobcontext)
	    {
	        return run();
	    }
	
	    protected final boolean syncInterrupted()
	    {
	        boolean flag = true;
	        synchronized(ImmediateSync.this)
	        {
	            if(syncResultCode != 1)
	                flag = false;
	        }
	        return flag;
	    }
	
	    
	}
}
