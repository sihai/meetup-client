/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 
 * @author sihai
 *
 */
public class VideoView extends GLSurfaceView {
	
	private final GCommNativeWrapper gcommNativeWrapper;
    private volatile boolean reinitializeRenderer;
    private volatile int requestID;
    
    public VideoView(Context context)
    {
        this(context, null);
    }

    public VideoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        requestID = 0;
        gcommNativeWrapper = GCommApp.getInstance(getContext()).getGCommNativeWrapper();
        setEGLContextClientVersion(2);
        setRenderer(new Renderer());
        setRenderMode(0);
        onPause();
    }

    public void setRequestID(int i)
    {
        if(i != requestID)
        {
            requestID = i;
            reinitializeRenderer = true;
        }
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private final class Renderer implements android.opengl.GLSurfaceView.Renderer {

    	private boolean disabled;
    	
	    public final void onDrawFrame(GL10 gl10)
	    {
	        if(reinitializeRenderer)
	        {
	            boolean flag;
	            if(!gcommNativeWrapper.initializeIncomingVideoRenderer(requestID))
	                flag = true;
	            else
	                flag = false;
	            disabled = flag;
	            if(disabled)
	                Log.debug("initializeIncomingVideoRenderer failed. Rendering disabled");
	            reinitializeRenderer = false;
	        }
	        if(!disabled)
	            gcommNativeWrapper.renderIncomingVideoFrame(requestID);
	    }

	    public final void onSurfaceChanged(GL10 gl10, int i, int j)
	    {
	        boolean flag;
	        flag = true;
	        if(reinitializeRenderer)
	        {
	            boolean flag1;
	            if(!gcommNativeWrapper.initializeIncomingVideoRenderer(requestID))
	                flag1 = flag;
	            else
	                flag1 = false;
	            disabled = flag1;
	            if(disabled)
	                Log.debug("initializeIncomingVideoRenderer failed. Rendering disabled");
	            reinitializeRenderer = false;
	        }
	        if(!disabled) {
	        	if(gcommNativeWrapper.setIncomingVideoRendererSurfaceSize(requestID, i, j))
		            flag = false;
		        disabled = flag;
		        if(disabled)
		            Log.debug("setIncomingVideoRendererSurfaceSize failed. Rendering disabled");
	        }
	    }

	    public final void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig)
	    {
	        boolean flag;
	        if(!gcommNativeWrapper.initializeIncomingVideoRenderer(requestID))
	            flag = true;
	        else
	            flag = false;
	        disabled = flag;
	        if(disabled)
	            Log.debug("initializeIncomingVideoRenderer failed. Rendering disabled");
	    }
    }
}
