/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.ui.view.GLTextureView;

/**
 * 
 * @author sihai
 *
 */
public class VideoTextureView extends GLTextureView {

	private final GCommNativeWrapper mGcommNativeWrapper;
    private volatile boolean mIsDecoding;
    private final Renderer mRenderer;
    private volatile int mRequestID;
    
    public VideoTextureView(Context context)
    {
        this(context, null);
    }

    public VideoTextureView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mRequestID = 0;
        mGcommNativeWrapper = GCommApp.getInstance(getContext()).getGCommNativeWrapper();
        setEGLContextClientVersion(2);
        mRenderer = new Renderer();
        setRenderer(mRenderer);
        setRenderMode(0);
        onPause();
    }

    public final boolean isDecoding()
    {
        return mIsDecoding;
    }

    public final void setRequestID(int i)
    {
        if(mRequestID != i)
        {
            mRequestID = i;
            mRenderer.reinitialize();
        }
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class Renderer implements GLTextureView.Renderer {

		private volatile int mAttempt;
	    private volatile boolean mEnabled;
	    private volatile boolean mInitializeRendererPending;
	    private int mPendingHeight;
	    private int mPendingWidth;
	    private boolean mSurfaceSizePending;
	    
	    private synchronized boolean handleFailure()
	    {
	        boolean flag;
	        mAttempt = 1 + mAttempt;
	        int i = mAttempt;
	        flag = false;
	        if(i >= 30)
	        {
	            mIsDecoding = false;
	            mEnabled = false;
	            mInitializeRendererPending = false;
	            mSurfaceSizePending = false;
	            flag = true;
	        }
	        if(flag)
	            Log.debug((new StringBuilder("Configuring native video renderer failed after ")).append(mAttempt).append(" attempts").toString());
	        return flag;
	    }

	    private void initializeRenderer() {
	    	
            if(!mInitializeRendererPending) {
            	if(!mEnabled || !mSurfaceSizePending) {
            		return; 
            	} else { 
            		if(mGcommNativeWrapper.setIncomingVideoRendererSurfaceSize(mRequestID, mPendingWidth, mPendingHeight)) {
            			synchronized(this) {
            				mSurfaceSizePending = false;
            			}
            		} else {
            			
            		}
            	}
            } else { 
            	if(!mGcommNativeWrapper.initializeIncomingVideoRenderer(mRequestID)) {
            		if(!handleFailure()) {
            			if(!mEnabled || !mSurfaceSizePending) {
            				return;
            			} else { 
            				if(mGcommNativeWrapper.setIncomingVideoRendererSurfaceSize(mRequestID, mPendingWidth, mPendingHeight)) {
            					synchronized(this) {
                    				mSurfaceSizePending = false;
                    			}
                    		} else {
                    			if(handleFailure())
                                    Log.debug("setIncomingVideoRendererSurfaceSize failed. Rendering disabled");
                    		}
            			}
            		} else { 
            			Log.debug("initializeIncomingVideoRenderer failed. Rendering disabled");
            			return;
            		}
            	} else { 
            		synchronized(this) {
            			mEnabled = true;
                        mInitializeRendererPending = false;
            		}
            		if(!mEnabled || !mSurfaceSizePending) {
            			return; 
            		} else { 
            			if(mGcommNativeWrapper.setIncomingVideoRendererSurfaceSize(mRequestID, mPendingWidth, mPendingHeight)) {
            				synchronized(this) {
                				mSurfaceSizePending = false;
                			}
                		} else {
                			if(handleFailure())
                                Log.debug("setIncomingVideoRendererSurfaceSize failed. Rendering disabled");
                		}
            		}
            	}
            }
	    }

	    @Override
	    public final void onDrawFrame(GL10 gl)
	    {
	        initializeRenderer();
	        if(!mEnabled)
	        	return; 
	        else {
	        	mGcommNativeWrapper.renderIncomingVideoFrame(mRequestID);
	        	synchronized(this) {
	        		mIsDecoding = true;
	        	}
	        }
	    }

	    @Override
	    public final void onSurfaceChanged(GL10 gl, int width, int height) {
	    	synchronized(this) {
		        mSurfaceSizePending = true;
		        mPendingHeight = height;
		        mPendingWidth = width;
		        mAttempt = 0;
	    	}
	        initializeRenderer();
	    }

	    @Override
	    public final void onSurfaceCreated(GL10 gl, EGLConfig config)
	    {
	        initializeRenderer();
	    }

	    public final void reinitialize() {
	    	synchronized(this) {
	    		mInitializeRendererPending = true;
		        mAttempt = 0;
		        mIsDecoding = false;
	    	}
	    }

	}
}
