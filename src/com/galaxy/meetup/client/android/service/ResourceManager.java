/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 
 * @author sihai
 *
 */
public class ResourceManager implements Callback {

	private final Context mContext;
    private LoaderThread mLoaderThread;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper(), this);
    
    public final Context getContext()
    {
        return mContext;
    }
    
    public void onFirstConsumerRegistered(Resource resource)
    {
    }

    public void onLastConsumerUnregistered(Resource resource)
    {
    }
    
	@Override
	public boolean handleMessage(Message message) {
		
		if(0 == message.what) {
			Resource resource3 = (Resource)message.obj;
	        resource3.mStatus = message.arg1;
	        resource3.notifyConsumers();
		} else if(1 == message.what) {
			ResourceData resourcedata = (ResourceData)message.obj;
	        Resource resource1 = resourcedata.resource;
	        resource1.mResource = resourcedata.data;
	        resource1.mStatus = message.arg1;
	        resource1.notifyConsumers();
		} else if(2 == message.what) {
			Resource resource2 = (Resource)message.obj;
	        resource2.mStatus = message.arg1;
	        resource2.mHttpStatusCode = message.arg2;
	        resource2.notifyConsumers();
		} else if(3 == message.what) {
			Resource resource = (Resource)message.obj;
	        resource.mResourceType = message.arg1;
	        resource.notifyConsumers();
		} else {
			
		}
		return true;
	}

	protected ResourceManager(Context context)
    {
        mContext = context;
    }

    protected final void deliverHttpError(Resource resource, int i, int j)
    {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(2, 6, j, resource));
    }

    protected final void deliverResourceContent(Resource resource, int i, Object obj)
    {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(1, i, 0, new ResourceData(resource, obj)));
    }

    protected final void deliverResourceStatus(Resource resource, int i)
    {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(0, i, 0, resource));
    }

    protected final void deliverResourceType(Resource resource, int i)
    {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(3, i, 0, resource));
    }
    
    protected final void loadResource(Resource resource)
    {
        if(mLoaderThread == null)
        {
            mLoaderThread = new LoaderThread();
            mLoaderThread.start();
        }
        mLoaderThread.loadResource(resource);
    }
    
	private static final class LoaderThread extends HandlerThread implements
			android.os.Handler.Callback {

		public final boolean handleMessage(Message message) {
			((Resource) message.obj).load();
			return true;
		}

		public final void loadResource(Resource resource) {
			if (mLoaderThreadHandler == null)
				mLoaderThreadHandler = new Handler(getLooper(), this);
			mLoaderThreadHandler.sendMessage(mLoaderThreadHandler
					.obtainMessage(0, resource));
		}

		public final void run() {
			android.os.Process.setThreadPriority(10);
			super.run();
		}

		private Handler mLoaderThreadHandler;

		public LoaderThread() {
			super("ImageLoader");
		}
	}

	private static final class ResourceData {

		Object data;
		Resource resource;

		public ResourceData(Resource resource1, Object obj) {
			resource = resource1;
			data = obj;
		}
	}
}
