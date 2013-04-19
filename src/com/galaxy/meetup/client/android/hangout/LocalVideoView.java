/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.hangout.VideoCapturer.Host;

/**
 * 
 * @author sihai
 *
 */
public class LocalVideoView extends HangoutVideoView implements Host {

	private boolean audioVideoFailed;
    private final EventHandler eventHandler = new EventHandler();
    private boolean isRegistered;
    private int numPendingStartOutgoingVideoRequests;
    private Cameras.CameraType selectedCameraType;
    private int selfFrameHeight;
    private int selfFrameWidth;
    private final TextureView textureView;
    private final ImageButton toggleFlashButton = getFlashToggleButton();
    private final VideoCapturer videoCapturer;
    
    public LocalVideoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        selfFrameWidth = 320;
        selfFrameHeight = 240;
        textureView = new TextureView(context, attributeset);
        setVideoSurface(textureView);
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)textureView.getLayoutParams();
        layoutparams.width = -2;
        layoutparams.height = -2;
        textureView.setLayoutParams(layoutparams);
        setLayoutMode(HangoutVideoView.LayoutMode.FIT);
        videoCapturer = new VideoCapturer.TextureViewVideoCapturer(context, GCommApp.getInstance(context).getGCommNativeWrapper(), textureView, this);
        toggleFlashButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                videoCapturer.toggleFlashLightEnabled();
                updateFlashLightButtonState();
            }

        });
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
        if(!videoCapturer.supportsFlashLight() || !videoCapturer.isCapturing())
        {
            toggleFlashButton.setVisibility(8);
        } else
        {
            toggleFlashButton.setVisibility(0);
            if(videoCapturer.flashLightEnabled())
                toggleFlashButton.setImageResource(R.drawable.ic_flash_off_holo_light);
            else
                toggleFlashButton.setImageResource(R.drawable.ic_flash_on_holo_light);
        }
    }

    public final boolean isVideoShowing()
    {
        return videoCapturer.isCapturing();
    }

    public final void onCameraOpenError(RuntimeException runtimeexception)
    {
        Log.warn("Video capturer failed to start");
        Log.warn(android.util.Log.getStackTraceString(runtimeexception));
        hideVideoSurface();
        hideLogo();
        showCameraError();
    }

    public final void onCapturingStateChanged()
    {
        updateFlashLightButtonState();
    }

    public final void onMeasure(int i, int j)
    {
        layoutVideo(selfFrameWidth, selfFrameHeight, i, j);
    }

    public final void onPause()
    {
        if(isRegistered)
        {
            Context context = getContext();
            GCommApp.getInstance(context).unregisterForEvents(context, eventHandler, false);
            isRegistered = false;
        }
        GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(getContext()).getGCommNativeWrapper();
        if(gcommnativewrapper.isOutgoingVideoStarted())
            gcommnativewrapper.stopOutgoingVideo();
        videoCapturer.stop();
    }

    public final void onResume()
    {
        if(!isRegistered)
        {
            Context context = getContext();
            GCommApp.getInstance(context).registerForEvents(context, eventHandler, false);
            isRegistered = true;
        }
        if(!GCommApp.getInstance(getContext()).isOutgoingVideoMute())
            if(GCommApp.getInstance(getContext()).getGCommNativeWrapper().isOutgoingVideoStarted() || !GCommApp.getInstance(getContext()).isInAHangoutWithMedia())
            {
                Cameras.CameraType cameratype = GCommApp.getInstance(getContext()).getLastUsedCameraType();
                hideLogo();
                showVideoSurface();
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
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class EventHandler extends GCommEventHandler
    {

        public final void onCameraPreviewFrameDimensionsChanged(int i, int j)
        {
            super.onCameraPreviewFrameDimensionsChanged(i, j);
            selfFrameWidth = i;
            selfFrameHeight = j;
            layoutVideo(selfFrameWidth, selfFrameHeight, getWidth(), getHeight());
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
            int i = numPendingStartOutgoingVideoRequests;
            numPendingStartOutgoingVideoRequests -= 1;
            if(isHangoutTileStarted() && numPendingStartOutgoingVideoRequests == 0 && !GCommApp.getInstance(getContext()).isOutgoingVideoMute())
            {
                hideLogo();
                showVideoSurface();
                videoCapturer.start(selectedCameraType);
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
