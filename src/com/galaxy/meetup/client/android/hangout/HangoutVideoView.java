/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.Thermometer;
import com.galaxy.meetup.client.util.Utils;

/**
 * 
 * @author sihai
 *
 */
public class HangoutVideoView extends RelativeLayout {

	private final View mAudiodMutedStatusView;
    private final ImageView mAvatarView;
    private final ImageView mBackgoundLogo;
    private final View mBlockedView;
    private final View mCameraErrorView;
    private final Rect mDispSize = new Rect();
    private final Display mDisplay = ((WindowManager)getContext().getSystemService("window")).getDefaultDisplay();
    private final ImageButton mFlashToggleButton;
    private HangoutTile mHangoutTile;
    private LayoutMode mLayoutMode;
    private final View mPausedView;
    private final View mPinnedStatusView;
    private RelativeLayout mRootView;
    private final ImageView mSnapshotView;
    private View mVideoSurface;
    private final Thermometer mVolumeBar;
    
    public HangoutVideoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        LayoutInflater.from(context).inflate(R.layout.hangout_video_view, this, true);
        mAvatarView = (ImageView)findViewById(R.id.hangout_video_avatar);
        mBlockedView = findViewById(R.id.hangout_video_blocked);
        mPausedView = findViewById(R.id.hangout_video_paused);
        mCameraErrorView = findViewById(R.id.hangout_camera_error);
        mFlashToggleButton = (ImageButton)findViewById(R.id.hangout_toggle_flash_light_button);
        mBackgoundLogo = (ImageView)findViewById(R.id.hangout_background_logo);
        mSnapshotView = (ImageView)findViewById(R.id.hangout_video_snapshot);
        mRootView = (RelativeLayout)findViewById(R.id.hangout_video_view);
        mAudiodMutedStatusView = findViewById(R.id.hangout_audio_muted_status);
        mPinnedStatusView = findViewById(R.id.hangout_pinned_status);
        mVolumeBar = (Thermometer)findViewById(R.id.hangout_volume);
        mLayoutMode = LayoutMode.FIT;
        setLayoutParams(new android.widget.RelativeLayout.LayoutParams(-2, -2));
    }

    public final ImageView getAvatarView()
    {
        return mAvatarView;
    }

    public Bitmap getBitmap()
    {
        return null;
    }

    public MeetingMember getCurrentVideoSource()
    {
        return null;
    }

    public final ImageButton getFlashToggleButton()
    {
        return mFlashToggleButton;
    }

    public final ImageView getSnapshotView()
    {
        return mSnapshotView;
    }

    public final void hideAudioMutedStatus()
    {
        mAudiodMutedStatusView.setVisibility(8);
    }

    public final void hideAvatar()
    {
        mAvatarView.setVisibility(8);
    }

    public final void hideBlocked()
    {
        mBlockedView.setVisibility(8);
    }

    public final void hideLogo()
    {
        mBackgoundLogo.setVisibility(8);
    }

    public final void hidePaused()
    {
        mPausedView.setVisibility(8);
    }

    public final void hidePinnedStatus()
    {
        mPinnedStatusView.setVisibility(8);
    }

    public final void hideVideoSurface()
    {
        mVideoSurface.setVisibility(8);
    }

    public final void hideVolumeBar()
    {
        mVolumeBar.setVisibility(8);
    }

    public final boolean isAudioMuteStatusShowing()
    {
        boolean flag;
        if(mAudiodMutedStatusView.getVisibility() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isHangoutTileStarted()
    {
        boolean flag;
        if(mHangoutTile != null && mHangoutTile.isTileStarted())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public boolean isVideoShowing()
    {
        return false;
    }

    protected final void layoutVideo(int i, int j, int k, int l) {
    	
    	RectangleDimensions rectangledimensions = null;
    	if(LayoutMode.FILL == mLayoutMode) {
    		rectangledimensions = new RectangleDimensions(k, l);
    	} else if(LayoutMode.FIT == mLayoutMode) {
    		if(j == 0)
                rectangledimensions = new RectangleDimensions(k, l);
            else
                rectangledimensions = Utils.fitContentInContainer((double)i / (double)j, k, l);
    	} else {
    		Log.error((new StringBuilder("Unknown layout mode: ")).append(mLayoutMode).toString());
    		return;
    	}
    	
    	android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)mVideoSurface.getLayoutParams();
        layoutparams.width = rectangledimensions.width;
        layoutparams.height = rectangledimensions.height;
        mVideoSurface.setLayoutParams(layoutparams);
        Object aobj[] = new Object[8];
        aobj[0] = mLayoutMode.toString();
        aobj[1] = Integer.valueOf(i);
        aobj[2] = Integer.valueOf(j);
        aobj[3] = Integer.valueOf(k);
        aobj[4] = Integer.valueOf(l);
        aobj[5] = Integer.valueOf(rectangledimensions.width);
        aobj[6] = Integer.valueOf(rectangledimensions.height);
        aobj[7] = toString();
        Log.debug("HangoutVideo.layout: mode=%s  video=%d,%d  parent=%d,%d   new dimensions=%d,%d  self=%s", aobj);
    	
    }

    public void onMeasure(int i, int j)
    {
        int k = getPaddingLeft() + getPaddingRight();
        int l = getPaddingTop() + getPaddingBottom();
        int i1 = android.view.View.MeasureSpec.getSize(i) - k;
        int j1 = android.view.View.MeasureSpec.getSize(j) - l;
        if(i1 <= 0 || j1 <= 0)
        {
            mDisplay.getRectSize(mDispSize);
            if(i1 <= 0)
                i1 = mDispSize.width() - k;
            if(j1 <= 0)
                j1 = mDispSize.height() - l;
        }
        onMeasure(i1, j1);
        super.onMeasure(i, j);
    }

    public void setBackgroundViewColor(int i)
    {
        mRootView.setBackgroundColor(i);
    }

    public final void setHangoutTile(HangoutTile hangouttile)
    {
        mHangoutTile = hangouttile;
    }

    public final void setLayoutMode(LayoutMode layoutmode)
    {
        mLayoutMode = layoutmode;
        requestLayout();
    }

    public final void setVideoSurface(View view)
    {
        if(mVideoSurface != null)
            mRootView.removeView(mVideoSurface);
        mVideoSurface = view;
        int i;
        if(mRootView.getChildCount() > 0)
            i = 1;
        else
            i = 0;
        mRootView.addView(mVideoSurface, i);
        invalidate();
        requestLayout();
    }

    public void setVolume(int i)
    {
        if(i < 0)
            i = 0;
        if(i > 9)
            i = 9;
        mVolumeBar.setFillLevel((double)i / 9D);
    }

    public final void showAudioMutedStatus()
    {
        mAudiodMutedStatusView.setVisibility(0);
    }

    public final void showAvatar()
    {
        if(!mSnapshotView.isShown())
            mAvatarView.setVisibility(0);
    }

    public final void showBlocked()
    {
        mBlockedView.setVisibility(0);
    }

    public final void showCameraError()
    {
        mCameraErrorView.setVisibility(0);
    }

    public final void showPaused()
    {
        mPausedView.setVisibility(0);
    }

    public final void showPinnedStatus()
    {
        mPinnedStatusView.setVisibility(0);
    }

    public final void showVideoSurface()
    {
        mVideoSurface.setVisibility(0);
    }

    public final void showVolumeBar()
    {
        mVolumeBar.setVisibility(0);
    }
	
	public static enum LayoutMode {
		FILL,
		FIT;
	}
}
