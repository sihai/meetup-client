/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.RelativeLayoutWithLayoutNotifications;
import com.galaxy.meetup.client.util.Utils;

/**
 * 
 * @author sihai
 *
 */
public class SelfVideoView extends RelativeLayoutWithLayoutNotifications implements VideoCapturer.Host {

	private boolean audioVideoFailed;
    private View cameraErrorView;
    private boolean disableFlashLightSupport;
    private final EventHandler eventHandler = new EventHandler();
    private int extraBottomOffset;
    private ViewGroup insetViewGroup;
    private LayoutMode layoutMode;
    private HangoutTile mHangoutTile;
    private float mVerticalGravity;
    private int numPendingStartOutgoingVideoRequests;
    private Cameras.CameraType selectedCameraType;
    private int selfFrameHeight;
    private int selfFrameWidth;
    private SurfaceView surfaceView;
    private ImageButton toggleFlashLightButton;
    private VideoCapturer videoCapturer;
    
    
    public SelfVideoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        selfFrameWidth = 320;
        selfFrameHeight = 240;
        layoutMode = LayoutMode.FIT;
        disableFlashLightSupport = false;
        mVerticalGravity = 0.0F;
        LayoutInflater.from(context).inflate(R.layout.hangout_self_video_view, this, true);
        insetViewGroup = (ViewGroup)findViewById(R.id.inset);
        surfaceView = (SurfaceView)insetViewGroup.findViewById(R.id.surface_view);
        cameraErrorView = insetViewGroup.findViewById(R.id.self_video_error);
        toggleFlashLightButton = (ImageButton)insetViewGroup.findViewById(R.id.hangout_toggle_flash_light_button);
        toggleFlashLightButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                videoCapturer.toggleFlashLightEnabled();
                updateFlashLightButtonState();
            }
        });
        videoCapturer = new VideoCapturer(context, GCommApp.getInstance(context).getGCommNativeWrapper(), surfaceView.getHolder(), this);
    }

    private void restartOutgoingVideo(Cameras.CameraType cameratype)
    {
        if(!audioVideoFailed)
        {
            videoCapturer.stop();
            if(GCommApp.getInstance(getContext()).isInAHangoutWithMedia())
            {
                GCommApp.getInstance(getContext()).getGCommNativeWrapper().stopOutgoingVideo();
                GCommApp.getInstance(getContext()).setLastUsedCameraType(cameratype);
                android.hardware.Camera.Size size = VideoCapturer.getSizeOfCapturedFrames(cameratype);
                if(size == null)
                {
                    onCameraOpenError(null);
                } else
                {
                    Log.info("Starting outgoing video");
                    GCommApp.getInstance(getContext()).getGCommNativeWrapper().startOutgoingVideo(size.width, size.height);
                    selectedCameraType = cameratype;
                    numPendingStartOutgoingVideoRequests = 1 + numPendingStartOutgoingVideoRequests;
                }
            } else
            {
                videoCapturer.start(cameratype);
            }
        }
    }

    private void updateFlashLightButtonState()
    {
        if(disableFlashLightSupport || !videoCapturer.supportsFlashLight() || !videoCapturer.isCapturing())
        {
            toggleFlashLightButton.setVisibility(8);
        } else
        {
            toggleFlashLightButton.setVisibility(0);
            if(videoCapturer.flashLightEnabled())
                toggleFlashLightButton.setImageResource(R.drawable.ic_flash_off_holo_light);
            else
                toggleFlashLightButton.setImageResource(R.drawable.ic_flash_on_holo_light);
        }
    }

    public final void layout(int i, int j)
    {
        RectangleDimensions rectangledimensions;
        int k;
        int l;
        Object aobj[];
        android.widget.RelativeLayout.LayoutParams layoutparams;
        if(layoutMode == LayoutMode.INSET)
        {
            int k1 = Math.max(i, j);
            
            int l1;
            int i2;
            if(selfFrameWidth > selfFrameHeight)
            {
                i2 = (int)(0.20000000000000001D * (double)k1);
                l1 = (int)(((double)i2 * (double)selfFrameHeight) / (double)selfFrameWidth);
            } else
            {
                l1 = (int)(0.20000000000000001D * (double)k1);
                i2 = (int)(((double)l1 * (double)selfFrameWidth) / (double)selfFrameHeight);
            }
            rectangledimensions = new RectangleDimensions(i2, l1);
            l = (int)(0.02D * (double)k1);
            k = l + extraBottomOffset;
        } else
        if(selfFrameHeight == 0)
        {
            rectangledimensions = new RectangleDimensions(100, 100);
            k = 0;
            l = 0;
        } else
        {
            int i1 = selfFrameWidth;
            int j1 = selfFrameHeight;
            rectangledimensions = Utils.fitContentInContainer((double)i1 / (double)j1, i, j);
            k = 0;
            l = 0;
        }
        layoutparams = new android.widget.RelativeLayout.LayoutParams(rectangledimensions.width, rectangledimensions.height);
        layoutparams.setMargins(0, 0, l, k);
        if(layoutMode == LayoutMode.INSET)
        {
            layoutparams.addRule(12);
            layoutparams.addRule(11);
        } else
        {
            layoutparams.addRule(13);
        }
        insetViewGroup.setLayoutParams(layoutparams);
        aobj = new Object[6];
        aobj[0] = Integer.valueOf(selfFrameWidth);
        aobj[1] = Integer.valueOf(selfFrameHeight);
        aobj[2] = Integer.valueOf(i);
        aobj[3] = Integer.valueOf(j);
        aobj[4] = Integer.valueOf(rectangledimensions.width);
        aobj[5] = Integer.valueOf(rectangledimensions.height);
        Log.debug("SelfView.layout: frame=%d,%d root=%d,%d self=%d,%d", aobj);
        setPadding(0, (int)((float)j * mVerticalGravity), 0, 0);
    }

    public final void onCameraOpenError(RuntimeException runtimeexception)
    {
        Log.warn("Video capturer failed to start");
        Log.warn(android.util.Log.getStackTraceString(runtimeexception));
        surfaceView.setVisibility(8);
        cameraErrorView.setVisibility(0);
    }

    public final void onCapturingStateChanged()
    {
        updateFlashLightButtonState();
    }

    public final void onMeasure(int i, int j)
    {
        layout(i, j);
    }

    public final void onPause()
    {
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), eventHandler, false);
        if(GCommApp.getInstance(getContext()).getGCommNativeWrapper().isOutgoingVideoStarted())
            GCommApp.getInstance(getContext()).getGCommNativeWrapper().stopOutgoingVideo();
        videoCapturer.stop();
    }

    public final void onResume()
    {
        GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
        startCapturing();
    }

    public void setExtraBottomOffset(int i)
    {
        extraBottomOffset = i;
        surfaceView.requestLayout();
    }

    public void setHangoutTile(HangoutTile hangouttile)
    {
        mHangoutTile = hangouttile;
    }

    public void setLayoutMode(LayoutMode layoutmode)
    {
        layoutMode = layoutmode;
    }

    public void setVerticalGravity(float f)
    {
        mVerticalGravity = f;
    }

    public void setVisibleViewOnTouchListener(android.view.View.OnTouchListener ontouchlistener)
    {
        if(layoutMode == LayoutMode.FIT)
        {
            setOnTouchListener(ontouchlistener);
            surfaceView.setOnTouchListener(null);
        } else
        {
            setOnTouchListener(null);
            surfaceView.setOnTouchListener(ontouchlistener);
        }
    }

    public final void startCapturing()
    {
        if(!GCommApp.getInstance(getContext()).isOutgoingVideoMute())
            if(GCommApp.getInstance(getContext()).getGCommNativeWrapper().isOutgoingVideoStarted() || !GCommApp.getInstance(getContext()).isInAHangoutWithMedia())
            {
                Cameras.CameraType cameratype = GCommApp.getInstance(getContext()).getLastUsedCameraType();
                surfaceView.setVisibility(0);
                videoCapturer.start(cameratype);
            } else
            if(Cameras.isAnyCameraAvailable())
            {
                Cameras.CameraType cameratype1 = GCommApp.getInstance(getContext()).getLastUsedCameraType();
                if(cameratype1 == null)
                    if(Cameras.isFrontFacingCameraAvailable())
                        cameratype1 = Cameras.CameraType.FrontFacing;
                    else
                        cameratype1 = Cameras.CameraType.RearFacing;
                restartOutgoingVideo(cameratype1);
            } else
            {
                Log.info("Not starting outgoing video because device is not capable.");
            }
    }

    public final void turnOffFlashLightSupport()
    {
        disableFlashLightSupport = true;
        toggleFlashLightButton.setVisibility(8);
    }
    
    static int access$210(SelfVideoView selfvideoview)
    {
        int i = selfvideoview.numPendingStartOutgoingVideoRequests;
        selfvideoview.numPendingStartOutgoingVideoRequests = i - 1;
        return i;
    }
    
    //============================================================================
    //							Inner class
    //============================================================================
    
    public static enum LayoutMode {
    	INSET,
    	FIT;
    }
    
    private final class EventHandler extends GCommEventHandler
    {

        public final void onCameraPreviewFrameDimensionsChanged(int i, int j)
        {
            super.onCameraPreviewFrameDimensionsChanged(i, j);
            selfFrameWidth = i;
            selfFrameHeight = j;
            layout(getWidth(), getHeight());
        }

        public final void onCameraSwitchRequested()
        {
            Cameras.CameraType cameratype = GCommApp.getInstance(getContext()).getLastUsedCameraType();
            Cameras.CameraType cameratype1;
            if(cameratype == null || cameratype == Cameras.CameraType.FrontFacing)
                cameratype1 = Cameras.CameraType.RearFacing;
            else
                cameratype1 = Cameras.CameraType.FrontFacing;
            GCommApp.getInstance(getContext()).setLastUsedCameraType(cameratype1);
            if(!GCommApp.getInstance(getContext()).isOutgoingVideoMute())
                restartOutgoingVideo(cameratype1);
        }

        public final void onError(GCommNativeWrapper.Error error)
        {
            super.onError(error);
            if(error == GCommNativeWrapper.Error.AUDIO_VIDEO_SESSION)
            {
                videoCapturer.stop();
                audioVideoFailed = true;
            }
        }

        public final void onOutgoingVideoStarted()
        {
        	super.onOutgoingVideoStarted();
            Log.info("Outgoing video started");
            SelfVideoView.access$210(SelfVideoView.this);
            if ((SelfVideoView.this.mHangoutTile != null) && (SelfVideoView.this.mHangoutTile.isTileStarted()) && (SelfVideoView.this.numPendingStartOutgoingVideoRequests == 0) && (!GCommApp.getInstance(SelfVideoView.this.getContext()).isOutgoingVideoMute()))
            {
              SelfVideoView.this.surfaceView.setVisibility(0);
              SelfVideoView.this.videoCapturer.start(SelfVideoView.this.selectedCameraType);
            }
        }

        public final void onVideoMuteToggleRequested()
        {
            boolean flag = true;
            boolean flag1 = GCommApp.getInstance(getContext()).isOutgoingVideoMute();
            GCommApp gcommapp;
            boolean flag2;
            Context context;
            if(flag1)
            {
                restartOutgoingVideo(GCommApp.getInstance(getContext()).getLastUsedCameraType());
            } else
            {
                videoCapturer.stop();
                GCommApp.getInstance(getContext()).getGCommNativeWrapper().stopOutgoingVideo();
            }
            gcommapp = GCommApp.getInstance(getContext());
            if(!flag1)
                flag2 = flag;
            else
                flag2 = false;
            gcommapp.setOutgoingVideoMute(flag2);
            context = getContext();
            if(flag1)
                flag = false;
            GCommApp.sendObjectMessage(context, 203, Boolean.valueOf(flag));
        }
    }
}
