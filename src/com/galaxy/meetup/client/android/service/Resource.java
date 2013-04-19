/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.view.View;

import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public abstract class Resource {

	private ResourceConsumer mConsumer;
    private List mConsumerList;
    protected boolean mDebugLogEnabled;
    protected volatile int mHttpStatusCode;
    protected final ResourceIdentifier mId;
    protected final ResourceManager mManager;
    protected volatile Object mResource;
    protected volatile int mResourceType;
    protected volatile int mStatus;
    private Object mTag;
    
	public Resource(ResourceManager resourcemanager, ResourceIdentifier resourceidentifier)
    {
        mManager = resourcemanager;
        mId = resourceidentifier;
        mDebugLogEnabled = EsLog.isLoggable("EsResource", 3);
    }
	
	public abstract void deliverData(byte abyte0[], boolean flag);
	
	public abstract void load();

    public final void deliverDownloadError(int i)
    {
        if(mStatus != 2 && mStatus != 3)
        {
            if(mDebugLogEnabled)
                logDebug((new StringBuilder("Request no longer needed, not delivering status change: ")).append(mId).append(", current status: ").append(statusToString(mStatus)).append(", ignored new status: ").append(statusToString(i)).toString());
        } else
        {
            if(mDebugLogEnabled)
                logDebug((new StringBuilder("Delivering error code to consumers: ")).append(mId).append(" status: ").append(statusToString(i)).toString());
            mManager.deliverResourceStatus(this, i);
        }
    }

    public final void deliverHttpError(int i)
    {
        if(i == 404)
            mManager.deliverResourceStatus(this, 4);
        else
            mManager.deliverHttpError(this, 6, i);
    }

    public final void deliverResource(Object obj)
    {
        mManager.deliverResourceContent(this, 1, obj);
    }

    public final void deliverResourceType(int i)
    {
        if(mDebugLogEnabled)
            logDebug((new StringBuilder("Delivering resource type to consumers: ")).append(mId).append(" resource type: 2").toString());
        mManager.deliverResourceType(this, 2);
    }

    public File getCacheFileName()
    {
        return null;
    }

    public final ResourceIdentifier getIdentifier()
    {
        return mId;
    }

    public final Object getResource()
    {
        return mResource;
    }

    public final int getResourceType()
    {
        return mResourceType;
    }

    public final int getStatus()
    {
        return mStatus;
    }

    public final String getStatusAsString()
    {
        return statusToString(mStatus);
    }

    public final boolean isDebugLogEnabled()
    {
        return mDebugLogEnabled;
    }

    public final void logDebug(String s)
    {
        if(mDebugLogEnabled)
            if(EsLog.isLoggable("EsResource", 3))
                Log.d("EsResource", s);
            else
                Log.i("EsResource", s);
    }

    public final void notifyConsumers()
    {
        if(mConsumerList != null)
        {
            int i = mConsumerList.size();
            for(int j = 0; j < i; j++)
            {
                ResourceConsumerHolder resourceconsumerholder = (ResourceConsumerHolder)mConsumerList.get(j);
                ResourceConsumer resourceconsumer1 = resourceconsumerholder.consumer;
                Object _tmp = resourceconsumerholder.tag;
                resourceconsumer1.onResourceStatusChange(this);
            }

        } else
        if(mConsumer != null)
        {
            ResourceConsumer resourceconsumer = mConsumer;
            Object _tmp1 = mTag;
            resourceconsumer.onResourceStatusChange(this);
        }
    }

    public void recycle()
    {
        mStatus = 0;
        mResource = null;
    }

    public final void register(ResourceConsumer resourceconsumer)
    {
    	boolean flag = true;
        if(mConsumer != resourceconsumer || (mTag != null || false) && (mTag == null || !mTag.equals(null))) {
        	flag = false;
        	if(null != mConsumerList) {
        		int size = mConsumerList.size();
        		for(int i = 0; i < size; i++) {
        			if(((ResourceConsumerHolder)mConsumerList.get(i)).matches(resourceconsumer, null)) {
        				flag = true;
        			}
        			break;
        		}
        	}
        }
        
        if(!flag)
        {
            boolean flag1;
            if(mConsumerList != null)
            {
                flag1 = mConsumerList.isEmpty();
                mConsumerList.add(new ResourceConsumerHolder(resourceconsumer, null));
            } else
            if(mConsumer != null)
            {
                mConsumerList = new ArrayList();
                mConsumerList.add(new ResourceConsumerHolder(mConsumer, mTag));
                mConsumer = null;
                mTag = null;
                mConsumerList.add(new ResourceConsumerHolder(resourceconsumer, null));
                flag1 = false;
            } else
            {
                mConsumer = resourceconsumer;
                mTag = null;
                flag1 = true;
            }
            if(flag1)
                mManager.onFirstConsumerRegistered(this);
            resourceconsumer.onResourceStatusChange(this);
        }
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(getClass().getSimpleName()).append('@').append(System.identityHashCode(this)).append("\n  ID: ").append(mId).append("\n  Status: ").append(statusToString(mStatus));
        stringbuilder.append("\n  Consumers:");
        if(mConsumerList != null)
        {
            int i = mConsumerList.size();
            for(int j = 0; j < i; j++)
            {
                ResourceConsumerHolder resourceconsumerholder = (ResourceConsumerHolder)mConsumerList.get(j);
                stringbuilder.append("\n   ");
                appendConsumer(stringbuilder, resourceconsumerholder.consumer, resourceconsumerholder.tag);
            }

        } else
        if(mConsumer != null)
        {
            stringbuilder.append("\n   ");
            appendConsumer(stringbuilder, mConsumer, mTag);
        } else
        {
            stringbuilder.append("\n   none");
        }
        return stringbuilder.toString();
    }

    public final void unregister(ResourceConsumer resourceconsumer)
    {
        if(mConsumer != resourceconsumer || mTag != null && (mTag == null || !mTag.equals(null))) {
            if(null != mConsumerList) {
            	for(Iterator iterator = mConsumerList.iterator(); iterator.hasNext();) {
            		if(((ResourceConsumerHolder)iterator.next()).matches(resourceconsumer, null)) {
            			iterator.remove();
            		}
            	}
            	
            	if(mConsumerList.isEmpty())
                    mManager.onLastConsumerUnregistered(this);
            }
        } else { 
        	 mConsumer = null;
             mTag = null;
             mManager.onLastConsumerUnregistered(this);
        }
    }

    private static void appendConsumer(StringBuilder stringbuilder, ResourceConsumer resourceconsumer, Object obj)
    {
        stringbuilder.append(resourceconsumer);
        if(resourceconsumer instanceof View)
            stringbuilder.append(" context: ").append(((View)resourceconsumer).getContext());
        if(obj != null)
            stringbuilder.append(" tag: ").append(obj);
    }
    
    private static String statusToString(int i) {
    	String s = null;
    	switch(i) {
	    	case 0:
	    		s = "canceled";
	    		break;
	    	case 1:
	    		s = "downloading";
	    		break;
	    	case 2:
	    		s = "loading";
	    		break;
	    	case 3:
	    		s = "not found";
	    		break;
	    	case 4:
	    		s = "out of memory";
	    		break;
	    	case 5:
	    		s = "packed";
	    		break;
	    	case 6:
	    		s = "permanent error";
	    		break;
	    	case 7:
	    		s = "ready";
	    		break;
	    	case 8:
	    		s = "transient error";
	    		break;
	    	case 9:
	    		s = "undefined";
	    		break;
    		default:
    			s = String.valueOf(i);
    			break;
    	}
    	return s;
    }
    
	public static abstract class ResourceIdentifier {

		public ResourceIdentifier() {
		}
	}
	
	private static final class ResourceConsumerHolder {

		ResourceConsumer consumer;
		Object tag;

		public ResourceConsumerHolder(ResourceConsumer resourceconsumer,
				Object obj) {
			consumer = resourceconsumer;
			tag = obj;
		}

		public final boolean matches(ResourceConsumer resourceconsumer,
				Object obj) {
			if (consumer != resourceconsumer) {
				return false;
			}

			boolean flag = false;
			if (tag == null) {
				flag = false;
				if (obj == null)
					flag = true;
			} else {
				flag = tag.equals(obj);
			}
			return flag;
		}
	}
}
