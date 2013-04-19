/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.RelativeLayoutWithLayoutNotifications;

/**
 * 
 * @author sihai
 *
 */
public abstract class IncomingVideoView extends RelativeLayoutWithLayoutNotifications {

	static final boolean $assertionsDisabled = IncomingVideoView.class.desiredAssertionStatus();
	
	private final ImageView avatarView;
    private final View blockedView;
    private IncomingContentType currentContent;
    protected MeetingMember currentVideoSource;
    private final EventHandler eventHandler = new EventHandler();
    protected int incomingVideoHeight;
    protected int incomingVideoWidth;
    private HangoutTile mHangoutTile;
    protected int requestID;
    private boolean showingUnknownAvatar;
    private final View videoPausedView;
    protected final VideoView videoView;
    
    protected abstract void startVideo();
    
    
    public IncomingVideoView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        currentContent = IncomingContentType.NONE;
        requestID = 0;
        LayoutInflater.from(context).inflate(R.layout.hangout_incoming_video_view, this, true);
        videoView = (VideoView)findViewById(R.id.video_view);
        avatarView = (ImageView)findViewById(R.id.video_avatar);
        blockedView = findViewById(R.id.blocked);
        videoPausedView = findViewById(R.id.video_paused);
    }

    final int getRequestId()
    {
        return requestID;
    }

    public final void onMeasure(int i, int j)
    {
        incomingVideoWidth = i;
        incomingVideoHeight = j;
        if(requestID != 0)
            GCommApp.getInstance(getContext()).getGCommNativeWrapper().setIncomingVideoParameters(requestID, incomingVideoWidth, incomingVideoHeight, GCommNativeWrapper.ScalingMode.AUTO_ZOOM, 15);
    }

    public final void onPause()
    {
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), eventHandler, false);
        videoView.onPause();
        if(requestID != 0)
        {
            GCommApp.getInstance(getContext()).getGCommNativeWrapper().stopIncomingVideo(requestID);
            requestID = 0;
        }
    }

    public final void onResume()
    {
        GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
        setIncomingContent(IncomingContentType.VIDEO);
        startVideo();
    }

    public void setHangoutTile(HangoutTile hangouttile)
    {
        mHangoutTile = hangouttile;
    }

    protected final void setIncomingContent(IncomingContentType incomingcontenttype)
    {
        videoView.setVisibility(8);
        avatarView.setVisibility(8);
        blockedView.setVisibility(8);
        videoPausedView.setVisibility(8);
        showingUnknownAvatar = false;
        
       
        switch(incomingcontenttype) {
        case NONE:
        	break;
        case VIDEO:
        	videoView.setVisibility(0);
        	break;
        case AVATAR:
        	avatarView.setVisibility(0);
        	break;
        case BLOCKED:
        	blockedView.setVisibility(0);
        	break;
        case VIDEO_PAUSED:
        	videoPausedView.setVisibility(0);
        	break;
        default:
        	break;
        }
        
        currentContent = incomingcontenttype;
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
    
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private static enum IncomingContentType {
    	NONE,
    	VIDEO,
    	AVATAR,
    	BLOCKED,
    	VIDEO_PAUSED;
    }
    
    public static class MainVideoView extends IncomingVideoView
    {
    	public MainVideoView(Context context, AttributeSet attributeset)
        {
            super(context, attributeset);
        }
    	
        protected final void startVideo()
        {
            MeetingMember meetingmember = GCommApp.getInstance(getContext()).getSelectedVideoSource();
            if(meetingmember == null)
            {
                requestID = GCommApp.getInstance(getContext()).getGCommNativeWrapper().startIncomingVideoForSpeakerIndex(0, incomingVideoWidth, incomingVideoHeight, 15);
            } else
            {
                requestID = GCommApp.getInstance(getContext()).getGCommNativeWrapper().startIncomingVideoForUser(meetingmember.getMucJid(), incomingVideoWidth, incomingVideoHeight, 15);
                setIncomingContent(meetingmember);
            }
        }

        public final void updateVideoStreaming()
        {
            if(requestID != 0)
            {
                MeetingMember meetingmember = GCommApp.getInstance(getContext()).getSelectedVideoSource();
                if(meetingmember == null)
                {
                    GCommApp.getInstance(getContext()).getGCommNativeWrapper().setIncomingVideoSourceToSpeakerIndex(requestID, 0);
                } else
                {
                    GCommApp.getInstance(getContext()).getGCommNativeWrapper().setIncomingVideoSourceToUser(requestID, meetingmember.getMucJid());
                    setIncomingContent(meetingmember);
                }
            }
        }
    }

    public static class ParticipantVideoView extends IncomingVideoView
    {
    	private final MeetingMember member;

        public ParticipantVideoView(Context context, AttributeSet attributeset, MeetingMember meetingmember)
        {
            super(context, null);
            member = meetingmember;
            videoView.setZOrderMediaOverlay(true);
        }
        
        public final MeetingMember getMember()
        {
            return member;
        }

        protected final void startVideo()
        {
            if(!$assertionsDisabled && currentVideoSource != null && member != currentVideoSource)
            {
                throw new AssertionError();
            } else
            {
                requestID = GCommApp.getInstance(getContext()).getGCommNativeWrapper().startIncomingVideoForUser(member.getMucJid(), incomingVideoWidth, incomingVideoHeight, 15);
                setIncomingContent(member);
                return;
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

        public final void onIncomingVideoFrameReceived(int i)
        {
            super.onIncomingVideoFrameReceived(i);
            if(i == requestID)
                videoView.requestRender();
        }

        public final void onIncomingVideoStarted(int i)
        {
            super.onIncomingVideoStarted(i);
            if(i == requestID && !GCommApp.getInstance(getContext()).isExitingHangout() && mHangoutTile.isTileStarted())
            {
                videoView.setRequestID(i);
                videoView.onResume();
            }
        }

        public final void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
        {
            super.onMediaBlock(meetingmember, meetingmember1, flag);
            if(meetingmember != null && meetingmember == currentVideoSource)
                setIncomingContent(currentVideoSource);
        }

        public final void onVCardResponse(MeetingMember meetingmember)
        {
            super.onVCardResponse(meetingmember);
            if(currentVideoSource == meetingmember && currentContent == IncomingContentType.AVATAR && showingUnknownAvatar && meetingmember.getVCard() != null && meetingmember.getVCard().getAvatarData() != null)
            {
                Avatars.renderAvatar(getContext(), meetingmember, avatarView);
                showingUnknownAvatar = false;
            }
        }

        public final void onVideoPauseStateChanged(MeetingMember meetingmember, boolean flag)
        {
            super.onVideoPauseStateChanged(meetingmember, flag);
            if(meetingmember == currentVideoSource)
                setIncomingContent(currentVideoSource);
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
                    Avatars.renderAvatar(getContext(), meetingmember, avatarView);
                    setIncomingContent(IncomingContentType.AVATAR);
                    if(meetingmember.getVCard() == null || meetingmember.getVCard().getAvatarData() == null)
                        showingUnknownAvatar = true;
                }
            } else {
            	setIncomingContent(IncomingContentType.NONE);
            }
            
            currentVideoSource = meetingmember;
            Object aobj[] = new Object[1];
            aobj[0] = currentContent.toString();
            Log.info("Now showing %s on video activity", aobj);
        }

    }
}
