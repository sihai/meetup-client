/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;


import com.galaxy.meetup.client.android.service.Hangout;

/**
 * 
 * @author sihai
 *
 */
public abstract class GCommEventHandler {

	GCommEventHandler()
    {
    }

    public void onAudioMuteStateChanged(MeetingMember meetingmember, boolean flag)
    {
    }

    public void onCallgrokLogUploadCompleted()
    {
    }

    public void onCameraPreviewFrameDimensionsChanged(int i, int j)
    {
    }

    public void onCameraSwitchRequested()
    {
    }

    public void onCurrentSpeakerChanged(MeetingMember meetingmember)
    {
    }

    public void onError(GCommNativeWrapper.Error error)
    {
    }

    public void onHangoutCreated(Hangout.Info info)
    {
        Log.debug((new StringBuilder()).append(this).append(" onHangoutCreated:").append(info).toString());
    }

    public void onHangoutWaitTimeout(Hangout.Info info)
    {
        Log.debug((new StringBuilder()).append(this).append(" onHangoutWaitTimeout:").append(info).toString());
    }

    public void onIncomingVideoFrameDimensionsChanged(int i, int j, int k)
    {
    }

    public void onIncomingVideoFrameReceived(int i)
    {
    }

    public void onIncomingVideoStarted(int i)
    {
    }

    public void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
    {
    }

    public void onMeetingEnterError(GCommNativeWrapper.MeetingEnterError meetingentererror)
    {
    }

    public void onMeetingExited(boolean flag)
    {
    }

    public void onMeetingMediaStarted()
    {
    }

    public void onMeetingMemberEntered(MeetingMember meetingmember)
    {
        Log.debug((new StringBuilder()).append(this).append(" onMeetingMemberEntered Id:").append(meetingmember.getMucJid()).append(" currentStatus: ").append(meetingmember.getCurrentStatus()).append(" prevStatus: ").append(meetingmember.getPreviousStatus()).toString());
    }

    public void onMeetingMemberExited(MeetingMember meetingmember)
    {
        Log.debug((new StringBuilder()).append(this).append(" onMeetingMemberExited Id:").append(meetingmember.getMucJid()).append(" currentStatus: ").append(meetingmember.getCurrentStatus()).append(" prevStatus: ").append(meetingmember.getPreviousStatus()).toString());
    }

    public void onMeetingMemberPresenceConnectionStatusChanged(MeetingMember meetingmember)
    {
        Log.debug((new StringBuilder()).append(this).append(" onMeetingMemberPresenceConnectionStatusChanged Id:").append(meetingmember.getMucJid()).append(" currentStatus: ").append(meetingmember.getCurrentStatus()).append(" prevStatus: ").append(meetingmember.getPreviousStatus()).toString());
    }

    public void onMucEntered(MeetingMember meetingmember)
    {
    }

    public void onOutgoingVideoStarted()
    {
    }

    public void onRemoteMute(MeetingMember meetingmember, MeetingMember meetingmember1)
    {
    }

    public void onSignedIn(String s)
    {
    }

    public void onSignedOut()
    {
    }

    public void onSigninTimeOutError()
    {
    }

    public void onVCardResponse(MeetingMember meetingmember)
    {
        Log.debug((new StringBuilder()).append(this).append(" onVCardResponse Id:").append(meetingmember.getMucJid()).append(" currentStatus: ").append(meetingmember.getCurrentStatus()).append(" prevStatus: ").append(meetingmember.getPreviousStatus()).toString());
    }

    public void onVideoMuteChanged(boolean flag)
    {
    }

    public void onVideoMuteToggleRequested()
    {
    }

    public void onVideoPauseStateChanged(MeetingMember meetingmember, boolean flag)
    {
    }

    public void onVideoSourceChanged(int i, MeetingMember meetingmember, boolean flag)
    {
    }

    public void onVolumeChanged(MeetingMember meetingmember, int i)
    {
    }
}
