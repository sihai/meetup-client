/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsDatabaseHelper;
import com.galaxy.meetup.client.android.content.EsDeepLinkInstallsData;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsPhotosDataApiary;
import com.galaxy.meetup.client.android.hangout.HangoutNotifications;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.iu.NewMediaTracker;
import com.galaxy.meetup.client.android.service.PicasaNetworkReceiver;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.EventDateUtils;
import com.galaxy.meetup.client.util.LinksRenderUtils;
import com.galaxy.meetup.client.util.PlusBarUtils;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.picasa.store.PicasaStoreFacade;

/**
 * 
 * @author sihai
 *
 */
public class EsApplication extends Application implements
		UncaughtExceptionHandler {

	public static int sMemoryClass;
    private Handler mHandler;
    private Thread.UncaughtExceptionHandler sSystemUncaughtExceptionHandler;
    
	//===========================================================================
    //						Constructor
    //===========================================================================
	public EsApplication()
    {
    }

    public void onCreate() {
        super.onCreate();
        
        sSystemUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        final Context applicationContext = getApplicationContext();
        // FIXME 4 Test
        EsAccountsData.deactivateAccount(applicationContext, "sihai", true);
        EsAccountsData.loadExperiments(applicationContext);
        
        (new AsyncTask() {

            protected final Object doInBackground(Object aobj[]) {
                InstantUpload.startMonitoring(applicationContext);
                HangoutNotifications.registerHangoutSounds(applicationContext);
                TextPaintUtils.init(applicationContext);
                EsAccount esaccount = EsAccountsData.getActiveAccount(applicationContext);
                //EsEventData.validateInstantShare(applicationContext, esaccount);
                EsDeepLinkInstallsData.removeStaleEntries(applicationContext, esaccount);
                InstantUpload.showFirstTimeFullSizeNotification(applicationContext, esaccount);
                NewMediaTracker.getInstance(applicationContext).processNewMedia();
                return null;
            }
        }).execute((Object[])null);
        ActivityManager activitymanager = (ActivityManager)applicationContext.getSystemService("activity");
        if(Build.VERSION.SDK_INT >= 11)
            sMemoryClass = activitymanager.getLargeMemoryClass();
        else
            sMemoryClass = activitymanager.getMemoryClass();
        PlusBarUtils.init(applicationContext);
        LinksRenderUtils.init(applicationContext);
        EsPhotosDataApiary.setPhotosFromPostsAlbumName(applicationContext.getString(R.string.photo_view_default_title));
        com.galaxy.picasa.sync.R.init(R.class);
        PicasaStoreFacade.setNetworkReceiver(PicasaNetworkReceiver.class);
        InstantUploadFacade.setNetworkReceiver(PicasaNetworkReceiver.class);
    }
    
    public void uncaughtException(final Thread thread, final Throwable ex)
    {
        boolean flag;
        if(getMainLooper().getThread() != thread)
            flag = true;
        else
            flag = false;
        
        if(flag) {
            if(EsLog.isLoggable("EsApplication", 6))
                Log.e("EsApplication", (new StringBuilder("Uncaught exception in background thread ")).append(thread).toString(), ex);
            if(EsDatabaseHelper.isDatabaseRecentlyDeleted()) {
                if(EsLog.isLoggable("EsApplication", 6))
                    Log.e("EsApplication", "An account has just been deactivated, which put background threads at a risk of failure. Letting this thread live.", ex);
            } else {
                if(mHandler == null)
                    mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {

                    public final void run() {
                        sSystemUncaughtExceptionHandler.uncaughtException(thread, ex);
                    }
                });
            }
        } else {
            sSystemUncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
