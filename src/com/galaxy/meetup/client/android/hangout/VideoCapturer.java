/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.WindowManager;

import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class VideoCapturer implements android.view.SurfaceHolder.Callback {

	static final boolean $assertionsDisabled;
    private volatile Camera camera;
    private Cameras.CameraProperties cameraProperties;
    private Cameras.CameraType cameraType;
    private final Context context;
    private volatile int deviceRotation;
    private boolean flashLightEnabled;
    private volatile int frameRotationBeforeDisplaying;
    private volatile int frameRotationBeforeSending;
    private final SurfaceHolder holder;
    private final Host host;
    protected volatile boolean isCapturing;
    protected boolean isSurfaceReady;
    private final GCommNativeWrapper nativeWrapper;
    private volatile int previewFrameHeight;
    private volatile int previewFrameWidth;
    protected boolean startCapturingWhenSurfaceReady;
    private boolean supportsFlashLight;
    protected SurfaceTexture surfaceTexture;
    private final WindowManager windowManager;

    static 
    {
        boolean flag;
        if(!VideoCapturer.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
	
    public VideoCapturer(Context context1, GCommNativeWrapper gcommnativewrapper, SurfaceHolder surfaceholder, Host host1)
    {
        context = context1;
        nativeWrapper = gcommnativewrapper;
        holder = surfaceholder;
        holder.setType(3);
        holder.addCallback(this);
        host = host1;
        windowManager = (WindowManager)context1.getSystemService("window");
        deviceRotation = -1;
    }

    protected VideoCapturer(Context context1, GCommNativeWrapper gcommnativewrapper, Host host1)
    {
        context = context1;
        nativeWrapper = gcommnativewrapper;
        holder = null;
        host = host1;
        windowManager = (WindowManager)context1.getSystemService("window");
        deviceRotation = -1;
    }

    private void configureCamera() {
    	
    	try {
	        Log.debug("*** configureCamera");
	        if(!isSurfaceReady || surfaceTexture == null) {
	        	if(holder == null) { 
	        		Log.error("No surface for camera preview.");
	        	} else { 
	        		camera.setPreviewDisplay(holder);
	        	}
	        } else { 
	        	camera.setPreviewTexture(surfaceTexture);
	        }
    	} catch (IOException e) {
    		Log.error("setPreviewDisplay failed.  Ignoring, but video capture is disabled");
    		camera.release();
    		camera = null;
    		return;
    	}
        
        android.hardware.Camera.Size size;
        int i;
        int j;
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        size = getBestMatchPreviewSize(cameraProperties, parameters);
        parameters.setPreviewFormat(17);
        parameters.setPreviewSize(size.width, size.height);
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(size.width);
        aobj[1] = Integer.valueOf(size.height);
        Log.info("Setting camera preview size to %d x %d", aobj);
        supportsFlashLight = false;
        List list = parameters.getSupportedFlashModes();
        if(list != null)
            supportsFlashLight = list.contains("torch");
        Log.info((new StringBuilder("Camera flash light in torch mode supports: ")).append(supportsFlashLight).toString());
        if(supportsFlashLight && flashLightEnabled)
        {
            parameters.setFlashMode("torch");
            Log.debug("Turning on flash light in torch mode");
        }
        camera.setParameters(parameters);
        long l = ImageFormat.getBitsPerPixel(17);
        long l1 = l * (long)(size.width * size.height);
        i = (int)((7L + l1) / 8L);
        Object aobj1[] = new Object[3];
        aobj1[0] = Long.valueOf(l);
        aobj1[1] = Long.valueOf(l1);
        aobj1[2] = Integer.valueOf(i);
        Log.info("BitsPerPixel: %d BitsPerFrame: %d BufferSize: %d", aobj1);
        camera.setPreviewCallbackWithBuffer(null);
        for(j = 0; j < 5; j++) {
        	camera.addCallbackBuffer(new byte[i + 1024]);
        }
        
        camera.setPreviewCallbackWithBuffer(new android.hardware.Camera.PreviewCallback() {

            public final void onPreviewFrame(byte abyte0[], Camera camera1)
            {
                synchronized(VideoCapturer.this)
                {
                    if(isCapturing)
                    {
                        if(VideoCapturer.access$000(VideoCapturer.this))
                        {
                            camera1.stopPreview();
                            configureCamera();
                            camera1.startPreview();
                        }
                        nativeWrapper.provideOutgoingVideoFrame(abyte0, System.nanoTime(), frameRotationBeforeSending);
                        camera.addCallbackBuffer(abyte0);
                    }
                }
            }
        });
        
        int k = 0;
        deviceRotation = windowManager.getDefaultDisplay().getRotation();
        if(0 == deviceRotation) {
        	k = 0;
        } else if(1 == deviceRotation) {
        	k = 90;
        } else if(2 == deviceRotation) {
        	k = 180;
        } else if(3 == deviceRotation) {
        	k = 270;
        } else {
        	throw new IllegalArgumentException("Invalid value of deviceOrientation");
        }
        
        Object aobj2[];
        Object aobj3[];
        int j1;
        int k1;
        boolean flag;
        int i1 = Compatibility.getCameraOrientation(cameraProperties);
        if(cameraProperties.isFrontFacing())
        {
            if(!Property.HANGOUT_CAMERA_MIRRORED.get().toUpperCase().equals("FALSE"))
                flag = true;
            else
                flag = false;
            if(flag)
                frameRotationBeforeSending = (360 - (k + i1) % 360) % 360;
            else
                frameRotationBeforeSending = (i1 + k) % 360;
            frameRotationBeforeDisplaying = (360 - (k + i1) % 360) % 360;
        } else
        {
            frameRotationBeforeSending = (360 + (i1 - k)) % 360;
            frameRotationBeforeDisplaying = (360 + (i1 - k)) % 360;
        }
        aobj2 = new Object[2];
        aobj2[0] = Integer.valueOf(k);
        aobj2[1] = Integer.valueOf(i1);
        Log.info("Device orientation is %d; camera orientation is %d", aobj2);
        aobj3 = new Object[2];
        aobj3[0] = Integer.valueOf(frameRotationBeforeDisplaying);
        aobj3[1] = Integer.valueOf(frameRotationBeforeSending);
        Log.info("frameRotationBeforeDisplaying is %d; frameRotationBeforeSending is %d", aobj3);
        camera.setDisplayOrientation(frameRotationBeforeDisplaying);
        j1 = previewFrameWidth;
        k1 = previewFrameHeight;
        
        if(0 == frameRotationBeforeDisplaying || 180 == frameRotationBeforeDisplaying) {
        	previewFrameWidth = size.width;
            previewFrameHeight = size.height;
        } else if(90 == frameRotationBeforeDisplaying || 270 ==frameRotationBeforeDisplaying) {
        	previewFrameWidth = size.height;
            previewFrameHeight = size.width;
        } else {
        	// TODO
        }
        
        if(previewFrameWidth != j1 || previewFrameHeight != k1)
            GCommApp.sendObjectMessage(context, 204, new RectangleDimensions(previewFrameWidth, previewFrameHeight));
        
    }

    private static android.hardware.Camera.Size getBestMatchPreviewSize(Cameras.CameraProperties cameraproperties, android.hardware.Camera.Parameters parameters)
    {
        List list = Compatibility.getSupportedPreviewSizes(parameters, cameraproperties);
        android.hardware.Camera.Size size = null;
        if(list != null)
        {
            Iterator iterator = list.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                android.hardware.Camera.Size size1 = (android.hardware.Camera.Size)iterator.next();
                long l = getMisMatchArea(size1);
                Object aobj[] = new Object[4];
                aobj[0] = Integer.valueOf(size1.width);
                aobj[1] = Integer.valueOf(size1.height);
                int i;
                if(parameters != null)
                    i = parameters.getPreviewFrameRate();
                else
                    i = 0;
                aobj[2] = Integer.valueOf(i);
                aobj[3] = Long.valueOf(l);
                Log.info("Supported camera preview size: %d x %d x %d. mismatch area=%d", aobj);
                if(size == null)
                    size = size1;
                else
                if(l < getMisMatchArea(size))
                    size = size1;
            } while(true);
        }
        return size;
    }

    private static long getMisMatchArea(android.hardware.Camera.Size size)
    {
        long l;
        if(size.width <= 480 && size.height <= 360)
            l = (long)(480 - size.width) * (long)size.height + 480L * (long)(360 - size.height);
        else
        if(size.width > 480 && size.height > 360)
            l = 360L * (long)(-480 + size.width) + (long)(-360 + size.height) * (long)size.width;
        else
        if(size.width > 480)
        {
            if(!$assertionsDisabled && size.height > 360)
                throw new AssertionError();
            l = (long)(-480 + size.width) * (long)size.height + 480L * (long)(360 - size.height);
        } else
        {
            if(!$assertionsDisabled && (size.width > 480 || size.height <= 360))
                throw new AssertionError();
            l = 360L * (long)(480 - size.width) + (long)(-360 + size.height) * (long)size.width;
        }
        return l;
    }

    public static android.hardware.Camera.Size getSizeOfCapturedFrames(Cameras.CameraType cameratype) {
    	
    	try {
	        Cameras.CameraResult cameraresult = Cameras.open(cameratype);
	        android.hardware.Camera.Size size;
	        Camera camera1 = cameraresult.getCamera();
	        size = getBestMatchPreviewSize(cameraresult.getProperties(), camera1.getParameters());
	        if(size != null)
	        {
	            Object aobj[] = new Object[2];
	            aobj[0] = Integer.valueOf(size.width);
	            aobj[1] = Integer.valueOf(size.height);
	            Log.info("Size of captured frames %d x %d", aobj);
	        }
	        camera1.release();
	        return size;
    	} catch (RuntimeException runtimeexception) {
    		Log.error((new StringBuilder("Error opening camera: ")).append(runtimeexception).toString());
    		return null;
    	}
    }

    public final boolean flashLightEnabled()
    {
        return flashLightEnabled;
    }

    public final boolean isCapturing()
    {
        return isCapturing;
    }

    public final void start(Cameras.CameraType cameratype)
    {
        cameraType = cameratype;
        startCapturingWhenSurfaceReady = true;
        if(isSurfaceReady)
            startCapturing();
    }

    protected final synchronized boolean startCapturing() {
    	
        if(isCapturing) {
        	return true;
        }
        try {
	        Cameras.CameraResult cameraresult = Cameras.open(cameraType);
	        camera = cameraresult.getCamera();
	        cameraProperties = cameraresult.getProperties();
	        configureCamera();
	        camera.startPreview();
	        isCapturing = true;
	        Host host2 = host;
	        host2.onCapturingStateChanged();
	        return true;
        } catch (RuntimeException runtimeexception) {
        	if(camera != null)
            {
                camera.release();
                camera = null;
            }
            isCapturing = false;
            Host host1 = host;
            host1.onCapturingStateChanged();
            host.onCameraOpenError(runtimeexception);
            return false;
        }
        
    }

    public final void stop()
    {
        stopCapturing();
        startCapturingWhenSurfaceReady = false;
    }

    protected final synchronized void stopCapturing()
    {
        Log.debug("*** stopCapturing");
        if(isCapturing)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
            isCapturing = false;
            Host host1 = host;
            host1.onCapturingStateChanged();
            previewFrameWidth = 0;
            previewFrameHeight = 0;
        }
    }

    public final boolean supportsFlashLight()
    {
        return supportsFlashLight;
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k)
    {
        isSurfaceReady = true;
        if(startCapturingWhenSurfaceReady && !isCapturing)
            startCapturing();
    }

    public void surfaceCreated(SurfaceHolder surfaceholder)
    {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder)
    {
        isSurfaceReady = false;
        stopCapturing();
    }

    public final void toggleFlashLightEnabled()
    {
        if(supportsFlashLight)
        {
            boolean flag;
            android.hardware.Camera.Parameters parameters;
            if(!flashLightEnabled)
                flag = true;
            else
                flag = false;
            flashLightEnabled = flag;
            parameters = camera.getParameters();
            if(flashLightEnabled)
            {
                parameters.setFlashMode("torch");
                Log.debug("Turning on flash light in torch mode");
            } else
            {
                parameters.setFlashMode("off");
                Log.debug("Turning off flash light in torch mode");
            }
            camera.setParameters(parameters);
        }
    }
    
    static boolean access$000(VideoCapturer videocapturer)
    {
        boolean flag;
        if(videocapturer.windowManager.getDefaultDisplay().getRotation() != videocapturer.deviceRotation)
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    //===================================================================================
    //							Inner class
    //===================================================================================
	static interface Host
    {

        public abstract void onCameraOpenError(RuntimeException runtimeexception);

        public abstract void onCapturingStateChanged();
    }

    public static final class TextureViewVideoCapturer extends VideoCapturer
        implements android.view.TextureView.SurfaceTextureListener
    {

        public final void onSurfaceTextureAvailable(SurfaceTexture surfacetexture, int i, int j)
        {
            surfaceTexture = surfacetexture;
            isSurfaceReady = true;
            if(startCapturingWhenSurfaceReady && !isCapturing)
                startCapturing();
        }

        public final boolean onSurfaceTextureDestroyed(SurfaceTexture surfacetexture)
        {
            isSurfaceReady = false;
            stopCapturing();
            surfaceTexture = null;
            return true;
        }

        public final void onSurfaceTextureSizeChanged(SurfaceTexture surfacetexture, int i, int j)
        {
        }

        public final void onSurfaceTextureUpdated(SurfaceTexture surfacetexture)
        {
        }

        protected final TextureView textureView;

        public TextureViewVideoCapturer(Context context1, GCommNativeWrapper gcommnativewrapper, TextureView textureview, Host host1)
        {
            super(context1, gcommnativewrapper, host1);
            textureView = textureview;
            textureView.setSurfaceTextureListener(this);
        }
    }
}
