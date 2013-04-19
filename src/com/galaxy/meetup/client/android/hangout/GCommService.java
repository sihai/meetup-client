/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.service.Hangout;

/**
 * 
 * @author sihai
 *
 */
public class GCommService extends Service {

	private Runnable callTimeoutRunnable;
    private final EventHandler eventHandler = new EventHandler();
    private Handler handler;
    private final LocalBinder localBinder = new LocalBinder();
    private Intent notificationIntent;
    private MediaPlayer playbackMediaPlayer;
    
    public GCommService()
    {
        handler = new Handler(Looper.getMainLooper());
    }

    public final Intent getNotificationIntent()
    {
        return notificationIntent;
    }

    public IBinder onBind(Intent intent)
    {
        Intent intent1 = new Intent("android.intent.action.VIEW");
        intent1.setClass(this, GCommService.class);
        startService(intent1);
        return localBinder;
    }

    public void onCreate()
    {
        super.onCreate();
        Log.debug("GCommService.onCreate called");
        GCommApp.getInstance(this).registerForEvents(this, eventHandler, true);
        stopForeground(true);
    }

    public void onDestroy()
    {
        Log.debug("GCommService.onDestroy called");
        GCommApp.getInstance(this).unregisterForEvents(this, eventHandler, true);
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int j)
    {
        return 1;
    }

    public final void showHangoutNotification(Intent intent)
    {
        notificationIntent = intent;
        Log.info("GCommService.showHangoutNotification");
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, 0x8000000);
        Notification notification = new Notification();
        notification.icon = R.drawable.hangout_notification_icon;
        notification.flags = 2 | notification.flags;
        notification.flags = 0x20 | notification.flags;
        notification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.hangout_ongoing_notify_title), getResources().getString(R.string.hangout_ongoing_notify_text), pendingintent);
        startForeground(1, notification);
    }

    public final void startRingback()
    {
        stopRingback();
        Log.debug("GCommService.startRingback");
        try
        {
            playbackMediaPlayer = MediaPlayer.create(this, R.raw.hangout_ringback);
        }
        catch(android.content.res.Resources.NotFoundException notfoundexception)
        {
            Log.error("Error playing media: ", new Object[] {
                notfoundexception
            });
        }
        if(playbackMediaPlayer == null)
        {
            Log.error((new StringBuilder("Could not create MediaPlayer for ")).append(R.raw.hangout_ringback).toString());
        } else
        {
            playbackMediaPlayer.setLooping(true);
            playbackMediaPlayer.start();
        }
    }

    public final void stopRingback()
    {
        Log.debug("GCommService.stopRingback");
        if(playbackMediaPlayer != null)
        {
            playbackMediaPlayer.stop();
            playbackMediaPlayer = null;
        }
    }

    private final class EventHandler extends GCommEventHandler implements android.media.MediaPlayer.OnCompletionListener {

	    private void playSound(int i) {
	    	try {
		    	MediaPlayer mediaplayer = MediaPlayer.create(GCommService.this, i);
		    	if(mediaplayer == null)
		        {
		            Log.error((new StringBuilder("Could not create MediaPlayer for ")).append(i).toString());
		        } else
		        {
		            mediaplayer.setOnCompletionListener(this);
		            mediaplayer.start();
		        }
	    	} catch (android.content.res.Resources.NotFoundException notfoundexception) {
	    		Log.error("Error playing media: ", new Object[] {
	    	            notfoundexception
	    	        });
	    	}
	    }

	    public final void onCompletion(MediaPlayer mediaplayer)
	    {
	        mediaplayer.release();
	    }
	
	    public final void onError(GCommNativeWrapper.Error error)
	    {
	        super.onError(error);
	        if(callTimeoutRunnable != null)
	        {
	            handler.removeCallbacks(callTimeoutRunnable);
	            callTimeoutRunnable = null;
	        }
	        stopRingback();
	        stopForeground(true);
	    }

	    public final void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
	    {
	        super.onMediaBlock(meetingmember, meetingmember1, flag);
	        if(meetingmember1 != null && !meetingmember1.isSelf())
	            playSound(R.raw.hangout_alert);
	    }

	    public final void onMeetingEnterError(GCommNativeWrapper.MeetingEnterError meetingentererror)
	    {
	        super.onMeetingEnterError(meetingentererror);
	        if(callTimeoutRunnable != null)
	        {
	            handler.removeCallbacks(callTimeoutRunnable);
	            callTimeoutRunnable = null;
	        }
	        stopRingback();
	        stopForeground(true);
	    }

	    public final void onMeetingExited(boolean flag)
	    {
	        super.onMeetingExited(flag);
	        if(callTimeoutRunnable != null)
	        {
	            handler.removeCallbacks(callTimeoutRunnable);
	            callTimeoutRunnable = null;
	        }
	        stopRingback();
	        stopForeground(true);
	    }

    public final void onMeetingMediaStarted() {
        super.onMeetingMediaStarted();
        final GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(GCommService.this).getGCommNativeWrapper();
        Hangout.Info info = gcommnativewrapper.getHangoutInfo();
        if(null == info) {
        	Log.debug("Hangout info is null");
        	return;
        }
        
        if(info.getLaunchSource() == Hangout.LaunchSource.Ring && gcommnativewrapper.getMeetingMemberCount() == 1)
        {
            Log.debug("Leaving meeting since there are no participants");
            callTimeoutRunnable = new Runnable() {

                public final void run()
                {
                    if(gcommnativewrapper.getMeetingMemberCount() == 1)
                        GCommApp.getInstance(GCommService.this).exitMeeting();
                    callTimeoutRunnable = null;
                }
            };
            
            handler.postDelayed(callTimeoutRunnable, 3000L);
        }
    }

    public final void onMeetingMemberEntered(MeetingMember meetingmember)
    {
        super.onMeetingMemberEntered(meetingmember);
        if(meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED)
        {
            if(callTimeoutRunnable != null)
            {
                handler.removeCallbacks(callTimeoutRunnable);
                callTimeoutRunnable = null;
            }
            stopRingback();
        }
    }

    public final void onMeetingMemberExited(MeetingMember meetingmember)
    {
        super.onMeetingMemberExited(meetingmember);
        final GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(GCommService.this).getGCommNativeWrapper();
        Hangout.Info info = gcommnativewrapper.getHangoutInfo();
        if(info != null && (info.getLaunchSource() == Hangout.LaunchSource.Ring || info.getRingInvitees()) && gcommnativewrapper.getMeetingMemberCount() == 1)
        {
            if(callTimeoutRunnable != null)
            {
                handler.removeCallbacks(callTimeoutRunnable);
                callTimeoutRunnable = null;
            }
            callTimeoutRunnable = new Runnable() {

                public final void run()
                {
                    if(gcommnativewrapper.getMeetingMemberCount() == 1)
                        GCommApp.getInstance(GCommService.this).exitMeeting();
                    callTimeoutRunnable = null;
                }
            };
            handler.postDelayed(callTimeoutRunnable, 3000L);
        }
        playSound(R.raw.hangout_leave);
    }

    public final void onMeetingMemberPresenceConnectionStatusChanged(MeetingMember meetingmember)
    {
        super.onMeetingMemberPresenceConnectionStatusChanged(meetingmember);
        if(meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED)
        {
            if(callTimeoutRunnable != null)
            {
                handler.removeCallbacks(callTimeoutRunnable);
                callTimeoutRunnable = null;
            }
            stopRingback();
        }
        if(GCommApp.getInstance(GCommService.this).shouldShowToastForMember(meetingmember) && meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED)
            playSound(R.raw.hangout_join);
    }

    public final void onMucEntered(MeetingMember meetingmember)
    {
        super.onMucEntered(meetingmember);
        final GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(GCommService.this).getGCommNativeWrapper();
        final Hangout.Info hangoutInfo = gcommnativewrapper.getHangoutInfo();
        if(null == hangoutInfo) {
        	Log.debug("hangoutInfo is null?!?");
        	return;
        }
        
        if(hangoutInfo.getRingInvitees())
        {
            startRingback();
            if(callTimeoutRunnable != null)
            {
                handler.removeCallbacks(callTimeoutRunnable);
                callTimeoutRunnable = null;
            }
            callTimeoutRunnable = new Runnable() {

                public final void run()
                {
                    stopRingback();
                    GCommApp.sendObjectMessage(GCommService.this, 51, hangoutInfo);
                    callTimeoutRunnable = new Runnable() {

                        public final void run()
                        {
                            if(gcommnativewrapper.getMeetingMemberCount() == 1)
                                GCommApp.getInstance(GCommService.this).exitMeeting();
                            callTimeoutRunnable = null;
                        }
                    };
                    handler.postDelayed(callTimeoutRunnable, 3000L);
                }

            };
            
            handler.postDelayed(callTimeoutRunnable, 45000L);
        }
    }

    public final void onRemoteMute(MeetingMember meetingmember, MeetingMember meetingmember1)
    {
        super.onRemoteMute(meetingmember, meetingmember1);
        if(!meetingmember1.isSelf())
            playSound(R.raw.hangout_alert);
    }

    public final void onVCardResponse(MeetingMember meetingmember)
    {
        super.onVCardResponse(meetingmember);
        if(GCommApp.getInstance(GCommService.this).shouldShowToastForMember(meetingmember) && meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTED)
            playSound(R.raw.hangout_join);
    }

}

	public final class LocalBinder extends Binder {

		final GCommService getService() {
			return GCommService.this;
		}
	}
}
