/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author sihai
 *
 */
public abstract class RemoteVideoView extends HangoutVideoView {
	
	static final boolean $assertionsDisabled;
	private IncomingContentType currentContent;
    private final EventHandler eventHandler = new EventHandler();
    protected int incomingVideoContainerHeight;
    protected int incomingVideoContainerWidth;
    protected int incomingVideoFrameHeight;
    protected int incomingVideoFrameWidth;
    private boolean isRegistered;
    protected MeetingMember mCurrentVideoSource;
    protected VideoChangeListener mListener;
    private final VideoTextureView mVideoSurface;
    protected int requestID;
    private boolean showingUnknownAvatar;
    
    static 
    {
        boolean flag;
        if(!RemoteVideoView.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
	public RemoteVideoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        incomingVideoFrameWidth = 10;
        incomingVideoFrameHeight = 20;
        currentContent = IncomingContentType.NONE;
        requestID = 0;
        mVideoSurface = new VideoTextureView(context, attributeset);
        setVideoSurface(mVideoSurface);
        setLayoutMode(HangoutVideoView.LayoutMode.FIT);
    }

	protected abstract void startVideo();
	
    public final Bitmap getBitmap()
    {
        return mVideoSurface.getBitmap();
    }

    public final MeetingMember getCurrentVideoSource()
    {
        return mCurrentVideoSource;
    }

    public final boolean isVideoShowing()
    {
        boolean flag;
        if(mVideoSurface != null && mVideoSurface.isDecoding())
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onMeasure$3b4dfe4b(int i, int j)
    {
        layoutVideo(incomingVideoFrameWidth, incomingVideoFrameHeight, i, j);
        android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams)mVideoSurface.getLayoutParams();
        if(incomingVideoContainerWidth != layoutparams.width || incomingVideoContainerHeight != layoutparams.height)
        {
            incomingVideoContainerWidth = layoutparams.width;
            incomingVideoContainerHeight = layoutparams.height;
            if(requestID != 0)
                GCommApp.getInstance(getContext()).getGCommNativeWrapper().setIncomingVideoParameters(requestID, incomingVideoContainerWidth, incomingVideoContainerHeight, GCommNativeWrapper.ScalingMode.AUTO_ZOOM, 15);
        }
    }

    public final void onPause()
    {
        GCommApp gcommapp = GCommApp.getInstance(getContext());
        if(isRegistered)
        {
            gcommapp.unregisterForEvents(getContext(), eventHandler, false);
            isRegistered = false;
        }
        mVideoSurface.onPause();
        if(requestID != 0)
        {
            gcommapp.getGCommNativeWrapper().stopIncomingVideo(requestID);
            requestID = 0;
        }
    }

    public final void onResume()
    {
        if(!isRegistered)
        {
            GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
            isRegistered = true;
        }
        setIncomingContent(IncomingContentType.VIDEO);
        startVideo();
    }

    public void setAlpha(float f)
    {
        if(mVideoSurface != null)
            mVideoSurface.setAlpha(f);
        else
            super.setAlpha(f);
    }

    protected final void setIncomingContent(MeetingMember meetingmember)
    {
        if(meetingmember.isMediaBlocked())
            setIncomingContent(IncomingContentType.BLOCKED);
        else
        if(meetingmember.isVideoPaused())
            setIncomingContent(IncomingContentType.VIDEO_PAUSED);
        else
            setIncomingContent(IncomingContentType.VIDEO);
    }

    protected final void setIncomingContent(IncomingContentType incomingcontenttype)
    {
        if(incomingcontenttype != currentContent)
        {
            ImageView imageview;
            if(incomingcontenttype == IncomingContentType.VIDEO)
                showVideoSurface();
            else
                hideVideoSurface();
            imageview = getSnapshotView();
            if(incomingcontenttype == IncomingContentType.AVATAR || incomingcontenttype == IncomingContentType.BLOCKED || incomingcontenttype == IncomingContentType.VIDEO_PAUSED)
            {
                Bitmap bitmap = mVideoSurface.getBitmap();
                if(bitmap != null)
                {
                    imageview.setImageBitmap(bitmap);
                    imageview.setVisibility(0);
                } else
                {
                    imageview.setVisibility(8);
                }
            } else
            {
                imageview.setVisibility(8);
            }
            if(incomingcontenttype == IncomingContentType.AVATAR || incomingcontenttype == IncomingContentType.BLOCKED || incomingcontenttype == IncomingContentType.VIDEO_PAUSED)
                showAvatar();
            else
                hideAvatar();
            if(incomingcontenttype == IncomingContentType.BLOCKED)
                showBlocked();
            else
                hideBlocked();
            if(incomingcontenttype == IncomingContentType.VIDEO_PAUSED)
                showPaused();
            else
                hidePaused();
            hideLogo();
            showingUnknownAvatar = false;
            currentContent = incomingcontenttype;
        }
    }

    public void setVideoChangeListener(VideoChangeListener videochangelistener)
    {
        mListener = videochangelistener;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	public static class CenterStageVideoView extends RemoteVideoView
    {
		public CenterStageVideoView(Context context, AttributeSet attributeset)
        {
            super(context, attributeset);
        }
		
        protected final void startVideo()
        {
            MeetingMember meetingmember = GCommApp.getInstance(getContext()).getSelectedVideoSource();
            if(meetingmember == null)
            {
                requestID = GCommApp.getInstance(getContext()).getGCommNativeWrapper().startIncomingVideoForSpeakerIndex(0, incomingVideoContainerWidth, incomingVideoContainerHeight, 15);
            } else
            {
                requestID = GCommApp.getInstance(getContext()).getGCommNativeWrapper().startIncomingVideoForUser(meetingmember.getMucJid(), incomingVideoContainerWidth, incomingVideoContainerHeight, 15);
                setIncomingContent(meetingmember);
            }
        }

        public final void updateVideoStreaming()
        {
            if(requestID != 0)
            {
                GCommApp gcommapp = GCommApp.getInstance(getContext());
                GCommNativeWrapper gcommnativewrapper = gcommapp.getGCommNativeWrapper();
                MeetingMember meetingmember = gcommapp.getSelectedVideoSource();
                if(mCurrentVideoSource != null && mCurrentVideoSource == meetingmember && (mCurrentVideoSource.isMediaBlocked() || mCurrentVideoSource.isVideoPaused()))
                {
                    IncomingContentType incomingcontenttype;
                    if(mCurrentVideoSource.isMediaBlocked())
                        incomingcontenttype = IncomingContentType.BLOCKED;
                    else
                        incomingcontenttype = IncomingContentType.VIDEO_PAUSED;
                    setIncomingContent(incomingcontenttype);
                } else
                if(meetingmember == null)
                {
                    if(mListener != null)
                        mListener.onVideoSourceAboutToChange();
                    setIncomingContent(IncomingContentType.VIDEO);
                    gcommnativewrapper.setIncomingVideoSourceToSpeakerIndex(requestID, 0);
                } else
                {
                    setIncomingContent(IncomingContentType.VIDEO);
                    gcommnativewrapper.setIncomingVideoSourceToUser(requestID, meetingmember.getMucJid());
                    if(mListener != null)
                        mListener.onVideoSourceAboutToChange();
                    onPause();
                    setIncomingContent(meetingmember);
                    onResume();
                }
            }
        }

    }

    private final class EventHandler extends GCommEventHandler
    {

        public final void onError(GCommNativeWrapper.Error error)
        {
            super.onError(error);
            if(error == GCommNativeWrapper.Error.AUDIO_VIDEO_SESSION)
                setIncomingContent(IncomingContentType.NONE);
        }

        public final void onIncomingVideoFrameDimensionsChanged(int i, int j, int k)
        {
            super.onIncomingVideoFrameDimensionsChanged(i, j, k);
            if(i == requestID && (j != incomingVideoFrameWidth || k != incomingVideoFrameHeight))
            {
                incomingVideoFrameWidth = j;
                incomingVideoFrameHeight = k;
                requestLayout();
            }
            return;
        }

        public final void onIncomingVideoFrameReceived(int i)
        {
            super.onIncomingVideoFrameReceived(i);
            if(i == requestID)
                mVideoSurface.requestRender();
        }

        public final void onIncomingVideoStarted(int i)
        {
            super.onIncomingVideoStarted(i);
            if(i == requestID && !GCommApp.getInstance(getContext()).isExitingHangout() && isHangoutTileStarted())
            {
                mVideoSurface.setRequestID(i);
                mVideoSurface.onResume();
            }
        }

        public final void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
        {
            super.onMediaBlock(meetingmember, meetingmember1, flag);
            if(meetingmember != null && meetingmember == mCurrentVideoSource)
                setIncomingContent(mCurrentVideoSource);
        }

        public final void onVCardResponse(MeetingMember meetingmember)
        {
            super.onVCardResponse(meetingmember);
            if(mCurrentVideoSource == meetingmember && currentContent == IncomingContentType.AVATAR && showingUnknownAvatar && meetingmember.getVCard() != null && meetingmember.getVCard().getAvatarData() != null)
            {
                Avatars.renderAvatar(getContext(), meetingmember, getAvatarView());
                showingUnknownAvatar = false;
            }
        }

        public final void onVideoPauseStateChanged(MeetingMember meetingmember, boolean flag)
        {
            super.onVideoPauseStateChanged(meetingmember, flag);
            if(meetingmember == mCurrentVideoSource)
                setIncomingContent(mCurrentVideoSource);
        }

        public final void onVideoSourceChanged(int i, MeetingMember meetingmember, boolean flag)
        {
            super.onVideoSourceChanged(i, meetingmember, flag);
            if(i != requestID) {
            	return;
            }
            if(meetingmember != null && meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED) {
            	if(meetingmember.isVideoPaused() || meetingmember.isMediaBlocked())
                    setIncomingContent(meetingmember);
                else
                if(flag)
                {
                    setIncomingContent(IncomingContentType.VIDEO);
                } else
                {
                    Avatars.renderAvatar(getContext(), meetingmember, getAvatarView());
                    setIncomingContent(IncomingContentType.AVATAR);
                    if(meetingmember.getVCard() == null || meetingmember.getVCard().getAvatarData() == null)
                        showingUnknownAvatar = true;
                }
            } else {
            	setIncomingContent(IncomingContentType.NONE);
            }
            
            if(mCurrentVideoSource != meetingmember)
            {
                if(mListener != null)
                    mListener.onVideoSourceAboutToChange();
                mCurrentVideoSource = meetingmember;
                onPause();
                onResume();
            }
            Object aobj[] = new Object[1];
            aobj[0] = currentContent.toString();
            Log.info("Now showing %s on video activity", aobj);
            
        }

    }

    private static enum IncomingContentType {
        NONE,
        VIDEO,
        AVATAR,
        BLOCKED,
        VIDEO_PAUSED;
    }

    public static class ParticipantVideoView extends RemoteVideoView {

    	private final MeetingMember member;

        public ParticipantVideoView(Context context, AttributeSet attributeset, MeetingMember meetingmember)
        {
            super(context, null);
            member = meetingmember;
        }
        
        public final MeetingMember getMember()
        {
            return member;
        }

        protected final void startVideo()
        {
            if(!$assertionsDisabled && mCurrentVideoSource != null && member != mCurrentVideoSource)
            {
                throw new AssertionError();
            } else
            {
                requestID = GCommApp.getInstance(getContext()).getGCommNativeWrapper().startIncomingVideoForUser(member.getMucJid(), incomingVideoContainerWidth, incomingVideoContainerHeight, 15);
                setIncomingContent(member);
                return;
            }
        }

    }

    public static interface VideoChangeListener
    {

        public abstract void onVideoSourceAboutToChange();
    }
}
