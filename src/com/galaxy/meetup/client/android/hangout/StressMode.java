/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.content.Context;
import android.os.Handler;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class StressMode {

	private static StressMode stressMode;
    private final Context context;
    private Runnable disconnectRunnable;
    private final EventHandler eventHandler = new EventHandler();
    private Runnable exitMeetingRunnable;
    private final GCommApp gcommApp;
    private final Handler handler = new Handler();
    private final Hangout.Info hangoutInfo;
    private Runnable launchGreenRoomRunnable;
    private Runnable meetingEnterRunnable;
    
    private StressMode(Context context1, GCommApp gcommapp, Hangout.Info info)
    {
        launchGreenRoomRunnable = new Runnable() {

            public final void run()
            {
                Object aobj[] = new Object[1];
                aobj[0] = Boolean.valueOf(StressMode.isEnabled());
                Log.debug("StressMode: launchGreenRoom: %s", aobj);
                if(StressMode.isEnabled())
                {
                    android.content.Intent intent = Intents.getHangoutActivityIntent(context, gcommApp.getAccount(), hangoutInfo, false, null);
                    context.startActivity(intent);
                }
            }
        };
        meetingEnterRunnable = new Runnable() {

            public final void run()
            {
                Log.debug("StressMode: enterHangout");
                gcommApp.enterHangout(hangoutInfo, true, null, false);
            }
        };
        exitMeetingRunnable = new Runnable() {

            public final void run()
            {
                Log.debug("StressMode: disconnect");
                gcommApp.exitMeeting();
            }
        };
        disconnectRunnable = new Runnable() {

            public final void run()
            {
                Log.debug("StressMode: disconnect");
                gcommApp.disconnect();
                handler.postDelayed(launchGreenRoomRunnable, 2000L);
            }

        };
        context = context1;
        gcommApp = gcommapp;
        hangoutInfo = info;
        gcommapp.registerForEvents(context1, eventHandler, false);
    }

    static void initialize(Context context1, GCommApp gcommapp, Hangout.Info info)
    {
        if(isEnabled() && stressMode == null)
            stressMode = new StressMode(context1, gcommapp, info);
    }

    static boolean isEnabled()
    {
        return Property.HANGOUT_STRESS_MODE.get().toUpperCase().equals("TRUE");
    }
    
    private void removeCallbacks()
    {
        handler.removeCallbacks(launchGreenRoomRunnable);
        handler.removeCallbacks(meetingEnterRunnable);
        handler.removeCallbacks(disconnectRunnable);
        return;
    }
    
    final class EventHandler extends GCommEventHandler
    {

        public final void onError(GCommNativeWrapper.Error error)
        {
            super.onError(error);
            removeCallbacks();
            handler.postDelayed(launchGreenRoomRunnable, 0L);
        }

        public final void onMeetingEnterError(GCommNativeWrapper.MeetingEnterError meetingentererror)
        {
            super.onMeetingEnterError(meetingentererror);
            handler.postDelayed(meetingEnterRunnable, 0L);
        }

        public final void onMeetingExited(boolean flag)
        {
            super.onMeetingExited(flag);
            removeCallbacks();
            handler.postDelayed(launchGreenRoomRunnable, 0L);
        }

        public final void onMeetingMediaStarted()
        {
            super.onMeetingMediaStarted();
            handler.postDelayed(exitMeetingRunnable, 15000L);
        }

        public final void onSignedIn(String s)
        {
            super.onSignedIn(s);
            handler.postDelayed(meetingEnterRunnable, 0L);
        }

        public final void onSignedOut()
        {
            super.onSignedOut();
            removeCallbacks();
            handler.postDelayed(launchGreenRoomRunnable, 0L);
        }

        public final void onSigninTimeOutError()
        {
            super.onSigninTimeOutError();
            handler.postDelayed(launchGreenRoomRunnable, 0L);
        }

    }
}
