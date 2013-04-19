/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

import com.galaxy.meetup.client.android.AuthData;
import com.galaxy.meetup.client.android.ClientVersion;
import com.galaxy.meetup.client.android.EsApplication;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.hangout.crash.CrashReport;
import com.galaxy.meetup.client.android.hangout.crash.CrashTriggerActivity;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.Utils;

/**
 * 
 * @author sihai
 *
 */
public class GCommApp implements OnAudioFocusChangeListener, Hangout.ApplicationEventListener {

	static final boolean $assertionsDisabled;
    private static boolean crashReported;
    private static GCommApp gcommApp;
    private EsAccount account;
    private final GCommEventHandler appEventHandler = new AppEventHandler();
    private boolean audioFocus;
    private AudioManager audioManager;
    private final ConnectivityChangeListener connectivityChangeListener = new ConnectivityChangeListener();
    private ConnectivityManager connectivityManager;
    private android.os.PowerManager.WakeLock cpuWakeLock;
    private int currentNetworkSubtype;
    private int currentNetworkType;
    private EsFragmentActivity currentStartedHangoutActivity;
    private List eventHandlers;
    private boolean exitMeetingCleanupDone;
    private GCommNativeWrapper gcommNativeWrapper;
    private volatile GCommService gcommService;
    private final ServiceConnection gcommServiceConnection = new ServiceConnection() {

        public final void onServiceConnected(ComponentName componentname, IBinder ibinder)
        {
            Log.debug((new StringBuilder("onServiceConnected called. Service: ")).append(ibinder.getClass().getName()).toString());
            if(ibinder instanceof GCommService.LocalBinder)
                gcommService = ((GCommService.LocalBinder)ibinder).getService();
        }

        public final void onServiceDisconnected(ComponentName componentname)
        {
            Log.debug("onServiceDisconnected called");
            gcommService = null;
        }

    };
    
    private Set greenRoomParticipantIds;
    private Handler handler;
    private Hangout.Info hangoutInfo;
    private int hangoutSigninRefCount;
    private long hangoutStartTime;
    private HeadsetBroadcastReceiver headsetBroadcastReceiver;
    private int incomingAudioLevelBeforeAudioFocusLoss;
    private boolean isBound;
    private boolean isExitingHangout;
    private Cameras.CameraType lastUsedCameraType;
    private int mSavedAudioMode;
    private boolean muteMicOnAudioFocusGain;
    private boolean outgoingVideoMute;
    private final EsApplication plusOneApplication;
    private android.os.PowerManager.WakeLock screenWakeLock;
    BroadcastReceiver screenoffBroadcastListener;
    private MeetingMember selectedVideoSource;
    private GCommEventHandler serviceEventHandler;
    private SigninTask signinTask;
    private android.net.wifi.WifiManager.WifiLock wifiLock;

    static 
    {
        boolean flag;
        if(!GCommApp.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
	
	
	private GCommApp(EsApplication esapplication)
    {
        screenoffBroadcastListener = new BroadcastReceiver() {

            public final void onReceive(Context context, Intent intent1)
            {
                if(intent1.getAction().equals("android.intent.action.SCREEN_OFF") && isInAHangout())
                {
                    exitMeetingAndDisconnect();
                    GCommApp.sendEmptyMessage(context, 54);
                }
            }
        };
        
        mSavedAudioMode = -2;
        eventHandlers = new ArrayList();
        currentNetworkType = -1;
        currentNetworkSubtype = -1;
        hangoutStartTime = -1L;
        handler = new Handler(Looper.getMainLooper());
        Log.debug("Constructing GCommApp");
        plusOneApplication = esapplication;
        resetSelfMediaState();
        Utils.initialize(esapplication);
        String s = esapplication.getFilesDir().getAbsolutePath();
        boolean flag = Property.NATIVE_HANGOUT_LOG.getBoolean();
        String s1 = Property.NATIVE_WRAPPER_HANGOUT_LOG_LEVEL.get();
        GCommNativeWrapper.initialize(esapplication, s, "Google_Plus_Android", Utils.getVersion(), flag, (new StringBuilder()).append(ClientVersion.from(esapplication)).toString(), s1);
        (new AsyncTask() {

            protected final Object doInBackground(Object aobj[])
            {
                return GCommApp.getCaptureSessionType();
            }

            protected final void onPostExecute(Object obj)
            {
                int i = ((GCommNativeWrapper.DeviceCaptureType)obj).ordinal();
                Log.debug((new StringBuilder("Setting device capture type: ")).append(i).toString());
                GCommNativeWrapper.nativeStaticSetDeviceCaptureType(i);
            }
        }).execute(new Void[0]);
        
        if(!isBound)
        {
            Intent intent = new Intent(plusOneApplication, GCommService.class);
            isBound = plusOneApplication.bindService(intent, gcommServiceConnection, 1);
        }
        if(!isBound)
        {
            Log.error("Unable to bind to GCommService");
        } else
        {
            audioManager = (AudioManager)esapplication.getSystemService("audio");
            connectivityManager = (ConnectivityManager)esapplication.getSystemService("connectivity");
            gcommNativeWrapper = new GCommNativeWrapper(esapplication.getApplicationContext());
            PowerManager powermanager = (PowerManager)esapplication.getSystemService("power");
            cpuWakeLock = powermanager.newWakeLock(1, "gcomm");
            screenWakeLock = powermanager.newWakeLock(10, "gcomm");
            wifiLock = ((WifiManager)esapplication.getSystemService("wifi")).createWifiLock(1, "gcomm");
            hangoutSigninRefCount = 0;
            gcommApp = this;
            esapplication.registerReceiver(screenoffBroadcastListener, new IntentFilter("android.intent.action.SCREEN_OFF"));
        }
    }
	
	public static void deactivateAccount(Context context, EsAccount esaccount)
    {
        if(gcommApp != null && gcommApp.isInAHangout())
        {
            gcommApp.exitMeetingAndDisconnect();
            gcommApp.dispatchMessage(54, null);
        }
        HangoutRingingActivity.deactivateAccount(context, esaccount);
    }

    private void exitMeetingAndDisconnect()
    {
        Log.debug("GCommApp.exitMeetingAndDisconnect");
        gcommNativeWrapper.stopOutgoingVideo();
        exitMeeting();
        disconnect();
    }

    private ArrayList getAllEventHandlers()
    {
        ArrayList arraylist = new ArrayList(2 + eventHandlers.size());
        if(appEventHandler != null)
            arraylist.add(appEventHandler);
        if(serviceEventHandler != null)
            arraylist.add(serviceEventHandler);
        arraylist.addAll(eventHandlers);
        return arraylist;
    }

    private static GCommNativeWrapper.DeviceCaptureType getCaptureSessionType()
    {
        String s = "";
        float f;
        int i;
        String as[];
        int j;
        try
        {
            InputStream inputstream = (new ProcessBuilder(new String[] {
                "/system/bin/cat", "/proc/cpuinfo"
            })).start().getInputStream();
            for(byte abyte0[] = new byte[1024]; inputstream.read(abyte0) != -1;)
                s = (new StringBuilder()).append(s).append(new String(abyte0)).toString();

            inputstream.close();
        }
        catch(IOException ioexception)
        {
            Log.debug(ioexception.toString());
        }
        f = 0.0F;
        i = 0;
        as = s.split("\n");
        j = as.length;
        for(int k = 0; k < j; k++)
        {
            String s1 = as[k];
            if(s1.matches("BogoMIPS.*"))
            {
                f += Float.parseFloat(s1.replaceAll("[^.0-9]", ""));
                i++;
            }
        }

        GCommNativeWrapper.DeviceCaptureType devicecapturetype;
        if(f > 10F && f < 200F || f > 1000F || i > 1)
            devicecapturetype = GCommNativeWrapper.DeviceCaptureType.MEDIUM_RESOLUTION;
        else
            devicecapturetype = GCommNativeWrapper.DeviceCaptureType.LOW_RESOLUTION;
        return devicecapturetype;
    }

    public static GCommApp getInstance(Context context)
    {
        if(gcommApp == null)
            gcommApp = new GCommApp((EsApplication)context.getApplicationContext());
        return gcommApp;
    }

    public static boolean isDebuggable(Context context)
    {
        boolean flag;
        if((2 & context.getApplicationInfo().flags) != 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static boolean isInstantiated()
    {
        boolean flag;
        if(gcommApp != null)
            flag = true;
        else
            flag = false;
        return flag;
    }
    
    private static void reportCrash(String s, boolean flag) {
        Thread.setDefaultUncaughtExceptionHandler(null);
	        if(!crashReported && gcommApp != null) {
	        crashReported = true;
	        Intent intent = new Intent(gcommApp.plusOneApplication, CrashTriggerActivity.class);
	        intent.addFlags(0x10000000);
	        if(s != null)
	            intent.putExtra("com.google.android.apps.plus.hangout.java_crash_signature", s);
	        gcommApp.plusOneApplication.startActivity(intent);
	        if(gcommApp.currentStartedHangoutActivity != null)
	            gcommApp.currentStartedHangoutActivity.finish();
	        GCommApp gcommapp = gcommApp;
	        if(gcommapp.isBound) {
	            gcommapp.plusOneApplication.unbindService(gcommapp.gcommServiceConnection);
	            gcommapp.isBound = false;
	        }
        }
    }

    static void reportJavaCrashFromNativeCode(Throwable throwable)
    {
        Log.error(android.util.Log.getStackTraceString(throwable));
        reportCrash(CrashReport.computeJavaCrashSignature(throwable), false);
    }

    static void reportNativeCrash()
    {
        reportCrash(null, false);
    }

    private void resetSelfMediaState() {
        outgoingVideoMute = false;
        if(!Cameras.isFrontFacingCameraAvailable()) {
        	if(Cameras.isRearFacingCameraAvailable())
                lastUsedCameraType = Cameras.CameraType.RearFacing; 
        } else { 
        	lastUsedCameraType = Cameras.CameraType.FrontFacing;
        }
    }

    public static void sendEmptyMessage(Context context, int i)
    {
        sendObjectMessage(context, i, null);
    }

    public static void sendObjectMessage(Context context, int i, Object obj) {
    	final int messageId = i;
        final GCommApp gcommapp = getInstance(context);
        final Object o = obj;
        gcommapp.handler.post(new Runnable() {

            public final void run()
            {
                gcommApp.dispatchMessage(messageId, o);
            }
        });
    }

    private void setupAudio()
    {
        audioFocus = true;
        setAudioMute(muteMicOnAudioFocusGain);
        gcommNativeWrapper.setIncomingAudioVolume(incomingAudioLevelBeforeAudioFocusLoss);
        if(headsetBroadcastReceiver != null && !headsetBroadcastReceiver.isHeadsetPluggedIn())
            audioManager.setSpeakerphoneOn(true);
    }

    private void updateWakeLockState(boolean flag) {
        // TODO
    }

    public final void createHangout(boolean flag)
    {
        Log.debug("GCommApp.createHangout");
        updateWakeLockState(true);
        gcommNativeWrapper.createHangout(flag);
    }

    public final void disconnect()
    {
        Log.debug((new StringBuilder("GCommApp.disconnect: ")).append(gcommNativeWrapper).toString());
        if(wifiLock.isHeld())
        {
            wifiLock.release();
            Log.info("Released wifi lock");
        }
        if(currentNetworkType != -1)
        {
            currentNetworkType = -1;
            currentNetworkSubtype = -1;
            plusOneApplication.unregisterReceiver(connectivityChangeListener);
        }
        gcommNativeWrapper.signoutAndDisconnect();
        resetSelfMediaState();
    }

    public final void dispatchMessage(int i, Object obj) {
    	// TODO
    	
    }

    public final void enterHangout(Hangout.Info info, boolean flag, List list, boolean flag1)
    {
        /*Log.debug("GCommApp.enterHangout: %s", new Object[] {
            info
        });
        updateWakeLockState(true);
        selectedVideoSource = null;
        gcommNativeWrapper.enterMeeting(info, flag, flag1);
        if(list == null)
        {
            greenRoomParticipantIds = null;
        } else
        {
            Iterator iterator;
            if(greenRoomParticipantIds == null)
                greenRoomParticipantIds = new HashSet();
            else
                greenRoomParticipantIds.clear();
            iterator = list.iterator();
            while(iterator.hasNext()) 
            {
                Data.Participant participant = (Data.Participant)iterator.next();
                greenRoomParticipantIds.add(participant.getParticipantId());
            }
        }
        isExitingHangout = false;
        hangoutInfo = info;*/
    }

    public final void exitMeeting()
    {
        Log.debug("GCommApp.exitMeeting");
        exitMeetingCleanup();
        if(!$assertionsDisabled && gcommNativeWrapper == null)
        {
            throw new AssertionError();
        } else
        {
            gcommNativeWrapper.exitMeeting();
            return;
        }
    }

    public final void exitMeetingCleanup()
    {
        isExitingHangout = true;
        updateWakeLockState(false);
        if(!$assertionsDisabled && !isExitingHangout)
            throw new AssertionError();
        int i;
        android.content.SharedPreferences.Editor editor;
        if(audioFocus)
            i = gcommNativeWrapper.getIncomingAudioVolume();
        else
            i = incomingAudioLevelBeforeAudioFocusLoss;
        editor = plusOneApplication.getSharedPreferences(getClass().getName(), 0).edit();
        editor.putInt("INCOMING_AUDIO_VOLUME", i);
        editor.commit();
        onAudioFocusChange(-1);
        if(android.os.Build.VERSION.SDK_INT < 14 && mSavedAudioMode != -2)
            audioManager.setMode(mSavedAudioMode);
        else
            audioManager.setMode(0);
        mSavedAudioMode = -2;
        audioManager.abandonAudioFocus(this);
        audioFocus = false;
        if(headsetBroadcastReceiver != null)
        {
            plusOneApplication.unregisterReceiver(headsetBroadcastReceiver);
            headsetBroadcastReceiver = null;
        }
        hangoutStartTime = -1L;
        exitMeetingCleanupDone = true;
    }

    public final EsAccount getAccount()
    {
        return account;
    }

    public final EsApplication getApp()
    {
        return plusOneApplication;
    }

    public final GCommNativeWrapper getGCommNativeWrapper()
    {
        return gcommNativeWrapper;
    }

    public final GCommService getGCommService()
    {
        return gcommService;
    }

    public final Cameras.CameraType getLastUsedCameraType()
    {
        return lastUsedCameraType;
    }

    public final MeetingMember getSelectedVideoSource()
    {
        return selectedVideoSource;
    }

    public final boolean hasAudioFocus()
    {
        return audioFocus;
    }

    public final boolean isAudioMute()
    {
        return gcommNativeWrapper.isAudioMute();
    }

    public final boolean isExitingHangout()
    {
        return isExitingHangout;
    }

    public final boolean isInAHangout() {
        GCommNativeWrapper gcommnativewrapper = gcommNativeWrapper;
        if(null == gcommnativewrapper) {
        	return false;
        }
        GCommNativeWrapper.GCommAppState gcommappstate = gcommNativeWrapper.getCurrentState();
        if(gcommappstate != GCommNativeWrapper.GCommAppState.IN_MEETING_WITHOUT_MEDIA)
        {
            GCommNativeWrapper.GCommAppState gcommappstate1 = GCommNativeWrapper.GCommAppState.IN_MEETING_WITH_MEDIA;
            if(gcommappstate != gcommappstate1)
                return false;
        }
        return true;
    }

    public final boolean isInAHangoutWithMedia() {
        boolean flag;
        if(gcommNativeWrapper != null && gcommNativeWrapper.getCurrentState() == GCommNativeWrapper.GCommAppState.IN_MEETING_WITH_MEDIA)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isInHangout(Hangout.Info info) {
        boolean flag;
        if(!isInAHangout())
            flag = false;
        else
            flag = gcommNativeWrapper.isInHangout(info);
        return flag;
    }

    public final boolean isOutgoingVideoMute() {
        return outgoingVideoMute;
    }

    public void onAudioFocusChange(int i) {
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(i);
        aobj[1] = Boolean.valueOf(isInAHangoutWithMedia());
        Log.debug("onAudioFocusChange: %d (meeting=%s)", aobj);
        if(!isInAHangoutWithMedia()) {
        	return;
        }
        
        switch(i)
        {
        case -3: 
            incomingAudioLevelBeforeAudioFocusLoss = gcommNativeWrapper.getIncomingAudioVolume();
            int j = 0 + (0 + incomingAudioLevelBeforeAudioFocusLoss) / 5;
            gcommNativeWrapper.setIncomingAudioVolume(j);
            muteMicOnAudioFocusGain = gcommNativeWrapper.isAudioMute();
            setAudioMute(true);
            break;

        case 1: // '\001'
            setupAudio();
            Object aobj2[] = new Object[2];
            aobj2[0] = Boolean.valueOf(audioManager.isSpeakerphoneOn());
            aobj2[1] = Integer.valueOf(gcommNativeWrapper.getIncomingAudioVolume());
            Log.info("AUDIOFOCUS_GAIN: speakerphone=%s volume=%d", aobj2);
            break;

        case -2: 
        case -1: 
            audioFocus = false;
            if(!isExitingHangout)
            {
                incomingAudioLevelBeforeAudioFocusLoss = gcommNativeWrapper.getIncomingAudioVolume();
                gcommNativeWrapper.setIncomingAudioVolume(0);
                muteMicOnAudioFocusGain = gcommNativeWrapper.isAudioMute();
                setAudioMute(true);
            }
            audioManager.setSpeakerphoneOn(false);
            Object aobj1[] = new Object[1];
            aobj1[0] = Boolean.valueOf(audioManager.isSpeakerphoneOn());
            Log.info("AUDIOFOCUS_LOSS: speakerphone=%s", aobj1);
            break;
        }
    }

    final void raiseNetworkError()
    {
        sendObjectMessage(plusOneApplication, -1, GCommNativeWrapper.Error.NETWORK);
    }

    public final void registerForEvents(Context context, GCommEventHandler gcommeventhandler, boolean flag)
    {
        Log.info("Registering for events: %s", new Object[] {
            gcommeventhandler
        });
        if(!$assertionsDisabled && !Utils.isOnMainThread(context))
            throw new AssertionError();
        if(!$assertionsDisabled && gcommeventhandler == null)
            throw new AssertionError();
        if(flag)
        {
            if(!$assertionsDisabled && serviceEventHandler != null)
                throw new AssertionError();
            serviceEventHandler = gcommeventhandler;
        } else
        {
            eventHandlers.add(gcommeventhandler);
        }
    }

    public final void setAudioMute(boolean flag)
    {
        gcommNativeWrapper.setAudioMute(flag);
    }

    public final void setLastUsedCameraType(Cameras.CameraType cameratype)
    {
        if(!$assertionsDisabled && cameratype == null)
        {
            throw new AssertionError();
        } else
        {
            lastUsedCameraType = cameratype;
            return;
        }
    }

    public final void setOutgoingVideoMute(boolean flag)
    {
        outgoingVideoMute = flag;
    }

    public final void setSelectedVideoSource(MeetingMember meetingmember)
    {
        selectedVideoSource = meetingmember;
    }

    public final boolean shouldShowToastForMember(MeetingMember meetingmember) {
        boolean flag = meetingmember.isSelf();
        if(flag) {
        	return false;
        }

        long l = (new Date()).getTime();
        boolean flag2;
        boolean flag3;
        if(hangoutStartTime != -1L && l - hangoutStartTime > 5000L)
            flag2 = true;
        else
            flag2 = false;
        if(greenRoomParticipantIds != null && !greenRoomParticipantIds.contains(meetingmember.getId()))
            flag3 = true;
        else
            flag3 = false;
        if(!flag3)
        {
            if(!flag2)
                return false;
        }
        return true;
        
    }

    public final void signinUser(EsAccount esaccount) {
        int i = 1;
        Log.info((new StringBuilder("GCommApp.signinUser: signinTask=")).append(signinTask).toString());
        if(signinTask != null) {
        	return; 
        } 
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
        if(networkinfo != null) {
        	currentNetworkType = networkinfo.getType();
            currentNetworkSubtype = networkinfo.getSubtype();
            Object aobj[] = new Object[2];
            aobj[0] = Integer.valueOf(currentNetworkType);
            aobj[i] = Integer.valueOf(currentNetworkSubtype);
            Log.info("Current network type: %d subtype: %d", aobj);
            plusOneApplication.registerReceiver(connectivityChangeListener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            if(currentNetworkType == i && !wifiLock.isHeld())
            {
                wifiLock.acquire();
                Log.info("Acquired wifi lock");
            }
        } else {
        	Log.info("startUsingNetwork: info is null");
            raiseNetworkError();
            i = 0;
        }
        if(i != 0)
        {
            account = esaccount;
            signinTask = new SigninTask(plusOneApplication.getApplicationContext());
            signinTask.execute(new Void[0]);
        }

    }

    public final void startingHangoutActivity(EsFragmentActivity esfragmentactivity)
    {
        hangoutSigninRefCount = 1 + hangoutSigninRefCount;
        currentStartedHangoutActivity = esfragmentactivity;
        Log.debug((new StringBuilder("Starting HangoutActivity: ")).append(hangoutSigninRefCount).toString());
    }

    public final void stoppingHangoutActivity() {
        if(!$assertionsDisabled && hangoutSigninRefCount <= 0)
            throw new AssertionError();
        hangoutSigninRefCount = -1 + hangoutSigninRefCount;
        currentStartedHangoutActivity = null;
        Log.debug((new StringBuilder("Stopping HangoutActivity: ")).append(hangoutSigninRefCount).toString());
       
        if(null != gcommNativeWrapper) {
        	 if(isInAHangoutWithMedia())
             {
                 if(gcommNativeWrapper.isOutgoingVideoStarted())
                     gcommNativeWrapper.stopOutgoingVideo();
             } else
             {
                 disconnect();
             }
        }
    }

    public final void unregisterForEvents(Context context, GCommEventHandler gcommeventhandler, boolean flag)
    {
        Log.info("Unregistering for events: %s", new Object[] {
            gcommeventhandler
        });
        if(!$assertionsDisabled && !Utils.isOnMainThread(context))
            throw new AssertionError();
        if(!$assertionsDisabled && gcommeventhandler == null)
            throw new AssertionError();
        if(flag)
        {
            if(!$assertionsDisabled && gcommeventhandler != serviceEventHandler)
                throw new AssertionError();
            serviceEventHandler = null;
        } else
        {
            if(!$assertionsDisabled && !eventHandlers.contains(gcommeventhandler))
                throw new AssertionError();
            eventHandlers.remove(gcommeventhandler);
        }
    }
    
    static boolean access$200(NetworkInfo networkinfo)
    {
        boolean flag;
        if(networkinfo.getState() != android.net.NetworkInfo.State.DISCONNECTING && networkinfo.getState() != android.net.NetworkInfo.State.DISCONNECTED && networkinfo.getState() != android.net.NetworkInfo.State.SUSPENDED)
            flag = true;
        else
            flag = false;
        return flag;
    }

	
	//===================================================================================
	//								Inner class
	//===================================================================================
	
	private final class AppEventHandler extends GCommEventHandler {

        public final void onError(GCommNativeWrapper.Error error)
        {
            if(hangoutInfo != null)
            {
                ExitHistory.recordErrorExit(plusOneApplication, hangoutInfo, error, false);
                hangoutInfo = null;
            }
            exitMeetingAndDisconnect();
        }

        public final void onMeetingEnterError(GCommNativeWrapper.MeetingEnterError meetingentererror)
        {
        }

        public final void onMeetingExited(boolean flag)
        {
            if(!exitMeetingCleanupDone)
                exitMeetingCleanup();
            isExitingHangout = false;
            if(hangoutInfo != null)
            {
                ExitHistory.recordNormalExit(plusOneApplication, hangoutInfo, false);
                hangoutInfo = null;
            }
        }

        public final void onMeetingMediaStarted()
        {
            exitMeetingCleanupDone = false;
            if(!$assertionsDisabled && !GCommApp.this.wifiLock.isHeld())
            {
                throw new AssertionError();
            } else
            {
                headsetBroadcastReceiver = new HeadsetBroadcastReceiver();
                boolean flag = true;
                plusOneApplication.registerReceiver(headsetBroadcastReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
                hangoutStartTime = System.currentTimeMillis();
                return;
            }
        }

        public final void onSigninTimeOutError()
        {
            exitMeetingAndDisconnect();
        }

    }
	
	private final class ConnectivityChangeListener extends BroadcastReceiver
    {

        public final void onReceive(Context context, Intent intent)
        {
            NetworkInfo networkinfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
            Object aobj[] = new Object[2];
            aobj[0] = networkinfo.getTypeName();
            aobj[1] = networkinfo.getState();
            Log.info("Connectivity change: network %s in state %s", aobj);
            if(intent.getBooleanExtra("noConnectivity", false))
                Log.info("No connectivity");
            NetworkInfo networkinfo1 = (NetworkInfo)intent.getParcelableExtra("otherNetwork");
            if(networkinfo1 != null)
            {
                Object aobj1[] = new Object[3];
                aobj1[0] = networkinfo1.getTypeName();
                aobj1[1] = networkinfo1.getSubtypeName();
                aobj1[2] = networkinfo1.getState();
                Log.info("Other network is %s in state %s", aobj1);
            }
            if(networkinfo.getType() == currentNetworkType && networkinfo.getSubtype() == currentNetworkSubtype && !GCommApp.access$200(networkinfo))
                raiseNetworkError();
        }
    }

    private final class HeadsetBroadcastReceiver extends BroadcastReceiver {

    	private boolean headsetPluggedIn = false;
    	
        final boolean isHeadsetPluggedIn()
        {
            return headsetPluggedIn;
        }

        public final void onReceive(Context context, Intent intent) {
            boolean flag = true;
            if(!"android.intent.action.HEADSET_PLUG".equals(intent.getAction())) 
            	return;
            
            boolean flag1;
            if(intent.getIntExtra("state", 0) != 0)
                flag1 = flag;
            else
                flag1 = false;
            headsetPluggedIn = flag1;
            if(isInAHangoutWithMedia() && audioFocus)
            {
                AudioManager audiomanager = audioManager;
                if(headsetPluggedIn)
                    flag = false;
                audiomanager.setSpeakerphoneOn(flag);
            }
        }

    }

    private final class SigninTask extends AsyncTask {

        private final Context context;

        SigninTask(Context context1)
        {
            super();
            context = context1;
        }
        
        private String doInBackground(Void avoid[])
        {
        	try {
	            if(!$assertionsDisabled && avoid.length != 0)
	                throw new AssertionError();
	            return AuthData.getAuthToken(context, account.getName(), "webupdates");
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}

        }

        protected final Object doInBackground(Object aobj[])
        {
            return doInBackground((Void[])aobj);
        }

        protected final void onPostExecute(Object obj)
        {
            String s = (String)obj;
            signinTask = null;
            gcommNativeWrapper.signoutAndDisconnect();
            if(!isCancelled())
                if(s == null)
                {
                    Log.info("Got null auth token. Raising authenticatioin error message.");
                    GCommApp.sendObjectMessage(context, -1, GCommNativeWrapper.Error.AUTHENTICATION);
                    account = null;
                } else
                {
                    gcommNativeWrapper.connectAndSignin(account, s);
                }
        }

    }
    
    public static enum WakeLockType {
    	NONE,
    	CPU,
    	SCREEN;
    }
}
