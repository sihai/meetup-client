/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.JsonReader;
import android.util.Log;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.DefaultHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation.OperationListener;
import com.galaxy.meetup.client.android.service.ServiceThread.IntentProcessor;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class SkyjamPlaybackService extends Service implements OnBufferingUpdateListener,
		OnCompletionListener, OnErrorListener, OnPreparedListener,
		OperationListener, IntentProcessor {

	private static EsAccount sAccount;
    private static String sActivityId;
    private static int sCurrentTime;
    private static List sListeners = new ArrayList();
    private static String sMusicUrl;
    private static String sSongName;
    private static String sStatus;
    private static int sTotalPlayableTime;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private NotificationManager mNotificationManager;
    private ServiceThread mServiceThread;
    private final Runnable mUpdateTimeRunnable = new Runnable() {

        public final void run()
        {
            if(mMediaPlayer != null && mMediaPlayer.isPlaying() && mMediaPlayer.getCurrentPosition() < SkyjamPlaybackService.sTotalPlayableTime)
            {
                SkyjamPlaybackService.sCurrentTime = mMediaPlayer.getCurrentPosition();
                SkyjamPlaybackService skyjamplaybackservice = SkyjamPlaybackService.this;
                int i = R.string.skyjam_status_playing;
                Object aobj[] = new Object[2];
                aobj[0] = getTimeString(SkyjamPlaybackService.sCurrentTime);
                aobj[1] = getTimeString(SkyjamPlaybackService.sTotalPlayableTime);
                SkyjamPlaybackService.sStatus = skyjamplaybackservice.getString(i, aobj);
            } else
            {
                SkyjamPlaybackService.sStatus = getString(R.string.skyjam_status_stopped);
            }
            dispatchStatusUpdate();
            mHandler.postDelayed(this, 1000L);
        }

    };
    
    public SkyjamPlaybackService()
    {
    }

    private void dispatchStatusUpdate()
    {
        final String musicUrl = sMusicUrl;
        final boolean playing;
        final String status;
        if(musicUrl != null && mMediaPlayer != null)
            playing = true;
        else
            playing = false;
        status = sStatus;
        mHandler.post(new Runnable() {

            public final void run()
            {
                for(Iterator iterator = SkyjamPlaybackService.sListeners.iterator(); iterator.hasNext(); ((SkyjamPlaybackListener)iterator.next()).onPlaybackStatusUpdate(musicUrl, playing, status));
            }
        });
    }

    public static String getPlaybackStatus(Context context, String s)
    {
        String s1;
        if(sMusicUrl != null && sMusicUrl.equals(s) && sStatus != null)
            s1 = sStatus;
        else
            s1 = context.getString(R.string.skyjam_status_stopped);
        return s1;
    }

    private String getTimeString(int i)
    {
        int j = i / 1000;
        int k = j / 60;
        int l = j % 60;
        int i1 = R.string.skyjam_time_formatting;
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(k);
        aobj[1] = Integer.valueOf(l);
        return getString(i1, aobj);
    }

    public static boolean isPlaying(String s)
    {
        boolean flag;
        if(sMusicUrl != null && sMusicUrl.equals(s))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static void logOut(Context context)
    {
        if(sMusicUrl != null)
        {
            Intent intent = new Intent(context, SkyjamPlaybackService.class);
            intent.setAction("com.google.android.apps.plus.service.SkyjamPlaybackService.STOP");
            intent.putExtra("music_account", sAccount);
            intent.putExtra("music_url", sMusicUrl);
            intent.putExtra("song", sSongName);
            intent.putExtra("activity_id", sActivityId);
            context.startService(intent);
        }
    }

    public static void registerListener(SkyjamPlaybackListener skyjamplaybacklistener)
    {
        sListeners.add(skyjamplaybacklistener);
    }

    private void stop()
    {
        mHandler.removeCallbacks(mUpdateTimeRunnable);
        if(mMediaPlayer != null)
        {
            if(EsLog.isLoggable("SkyjamPlaybackService", 3))
                Log.d("SkyjamPlaybackService", "stop");
            if(mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        sStatus = getString(R.string.skyjam_status_stopped);
        dispatchStatusUpdate();
        sMusicUrl = null;
        sSongName = null;
        sAccount = null;
        sActivityId = null;
        sCurrentTime = 0;
        sTotalPlayableTime = 0;
        mNotificationManager.cancel(27312);
    }

    public static void unregisterListener(SkyjamPlaybackListener skyjamplaybacklistener)
    {
        sListeners.remove(skyjamplaybacklistener);
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onBufferingUpdate(MediaPlayer mediaplayer, int i)
    {
        if(EsLog.isLoggable("SkyjamPlaybackService", 3))
            Log.d("SkyjamPlaybackService", (new StringBuilder("buffering: ")).append(i).append("%").toString());
    }

    public void onCompletion(MediaPlayer mediaplayer)
    {
        if(EsLog.isLoggable("SkyjamPlaybackService", 3))
            Log.d("SkyjamPlaybackService", "completion");
        stop();
        stopSelf();
    }

    public void onCreate()
    {
        super.onCreate();
        mNotificationManager = (NotificationManager)getSystemService("notification");
        sStatus = getString(R.string.skyjam_status_stopped);
        mHandler = new Handler(Looper.getMainLooper());
        mServiceThread = new ServiceThread(mHandler, "SkyjamServiceThread", this);
        mServiceThread.start();
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(mServiceThread != null)
        {
            mServiceThread.quit();
            mServiceThread = null;
        }
    }

    public boolean onError(MediaPlayer mediaplayer, int i, int j)
    {
        if(EsLog.isLoggable("SkyjamPlaybackService", 3))
            Log.d("SkyjamPlaybackService", (new StringBuilder("error: what=")).append(i).append(", extra=").append(j).toString());
        stop();
        stopSelf();
        return true;
    }

    public final void onOperationComplete(HttpOperation httpoperation)
    {
        int i;
        String s;
        String s1;
        if(httpoperation.hasError() || mMediaPlayer == null) {
        	return;
        }
        i = 0;
        s = null;
        s1 = null;
        String s2 = httpoperation.getOutputStream().toString();
        if(EsLog.isLoggable("SkyjamPlaybackService", 3))
            Log.d("SkyjamPlaybackService", (new StringBuilder("Received server response: ")).append(s2).toString());
        
        try {
	        JsonReader jsonreader = new JsonReader(new StringReader(s2));
	        jsonreader.beginObject();
	        while(jsonreader.hasNext()) 
	        {
	            String s3 = jsonreader.nextName();
	            if(s3.equals("durationMillis"))
	                i = jsonreader.nextInt();
	            else
	            if(s3.equals("playType"))
	                s = jsonreader.nextString().toLowerCase();
	            else
	            if(s3.equals("url"))
	                s1 = jsonreader.nextString();
	            else
	                jsonreader.skipValue();
	        }
	        jsonreader.endObject();
	        sTotalPlayableTime = i;
	        if(s != null && !s.equals("full") && s.endsWith("sp")) {
	        	sTotalPlayableTime = 1000 * Integer.parseInt(s.substring(0, -2 + s.length()));
	        }
	        if(EsLog.isLoggable("SkyjamPlaybackService", 3))
	            Log.d("SkyjamPlaybackService", (new StringBuilder("Total playable time set to ")).append(sTotalPlayableTime).append(" ms").toString());
	        try
	        {
	            if(EsLog.isLoggable("SkyjamPlaybackService", 3))
	                Log.d("SkyjamPlaybackService", "play");
	            mMediaPlayer.setAudioStreamType(3);
	            mMediaPlayer.setLooping(false);
	            mMediaPlayer.setDataSource(s1);
	            mMediaPlayer.prepareAsync();
	            sStatus = getString(R.string.skyjam_status_buffering);
	            dispatchStatusUpdate();
	        }
	        catch(IOException ioexception1) { }
	        stop();
        } catch (IOException e) {
        	// TODO
        }
    }

    public void onPrepared(MediaPlayer mediaplayer)
    {
        if(EsLog.isLoggable("SkyjamPlaybackService", 3))
            Log.d("SkyjamPlaybackService", "prepared");
        if(mediaplayer == mMediaPlayer)
        {
            mMediaPlayer.start();
            int i = R.string.skyjam_status_playing;
            Object aobj[] = new Object[2];
            aobj[0] = getTimeString(sCurrentTime);
            aobj[1] = getTimeString(sTotalPlayableTime);
            sStatus = getString(i, aobj);
            dispatchStatusUpdate();
            mHandler.postDelayed(mUpdateTimeRunnable, 1000L);
            int j = R.string.skyjam_notification_playing_song;
            Object aobj1[] = new Object[1];
            aobj1[0] = sSongName;
            String s = getString(j, aobj1);
            String s1 = getString(R.string.skyjam_notification_playing_song_title);
            int k = R.string.skyjam_notification_playing_song_subtitle;
            Object aobj2[] = new Object[1];
            aobj2[0] = sSongName;
            String s2 = getString(k, aobj2);
            Notification notification = new Notification(R.mipmap.icon, s, System.currentTimeMillis());
            notification.setLatestEventInfo(this, s1, s2, PendingIntent.getActivity(this, 0, Intents.getPostCommentsActivityIntent(this, sAccount, sActivityId), 0x8000000));
            notification.flags = 2 | notification.flags;
            mNotificationManager.notify(27312, notification);
        }
    }

    public final void onServiceThreadEnd()
    {
        stop();
    }

    public int onStartCommand(Intent intent, int i, int j)
    {
        if(intent != null)
            mServiceThread.put(intent);
        return 1;
    }

    public final void processIntent(Intent intent)
    {
        String s = intent.getAction();
        
        if(null == s) {
        	return;
        }
       
        if(s.equals("com.google.android.apps.plus.service.SkyjamPlaybackService.PLAY"))
        {
            String s2 = intent.getStringExtra("music_url");
            if(s2 != null && !s2.equals(sMusicUrl))
            {
                if(sMusicUrl != null)
                    stop();
                sAccount = (EsAccount)intent.getParcelableExtra("music_account");
                sMusicUrl = intent.getStringExtra("music_url");
                sSongName = intent.getStringExtra("song");
                sActivityId = intent.getStringExtra("activity_id");
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                sStatus = getString(R.string.skyjam_status_connecting);
                dispatchStatusUpdate();
                (new HttpOperation(this, "GET", sMusicUrl, new DefaultHttpRequestConfiguration(this, sAccount, "sj"), sAccount, new ByteArrayOutputStream(2048), null, this)).startThreaded();
            }
        } else
        if(s.equals("com.google.android.apps.plus.service.SkyjamPlaybackService.STOP"))
        {
            String s1 = intent.getStringExtra("music_url");
            if(s1 != null && s1.equals(sMusicUrl))
            {
                stop();
                stopSelf();
            }
        }
        
    }
    
    
    public static interface SkyjamPlaybackListener {

        void onPlaybackStatusUpdate(String s, boolean flag, String s1);
    }
}
