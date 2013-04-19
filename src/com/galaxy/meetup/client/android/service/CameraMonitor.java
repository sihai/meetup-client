/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.iu.InstantUploadFacade;
import com.galaxy.meetup.client.android.iu.NewMediaTracker;
import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class CameraMonitor extends BroadcastReceiver {

	private static final Uri MEDIA_STORE_URIS[];
    private static final Intent sIntent = new Intent("com.google.android.apps.plus.NEW_PICTURE");
    private static ContentObserver sMediaObserver;

    static 
    {
        Uri auri[] = new Uri[6];
        auri[0] = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        auri[1] = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        auri[2] = MediaStoreUtils.PHONE_STORAGE_IMAGES_URI;
        auri[3] = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        auri[4] = android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;
        auri[5] = MediaStoreUtils.PHONE_STORAGE_VIDEO_URI;
        MEDIA_STORE_URIS = auri;
    }

    public CameraMonitor()
    {
    }

    public static void registerObservers(Context context)
    {
        if(sMediaObserver == null)
        {
            sMediaObserver = new MediaObserver(context, null);
            ContentResolver contentresolver = context.getContentResolver();
            Uri auri[] = MEDIA_STORE_URIS;
            int i = auri.length;
            int j = 0;
            while(j < i) 
            {
                contentresolver.registerContentObserver(auri[j], true, sMediaObserver);
                j++;
            }
        }
    }

    public void onReceive(final Context context, Intent intent)
    {
        final android.os.PowerManager.WakeLock wl = ((PowerManager)context.getSystemService("power")).newWakeLock(1, "Camera Monitor");
        wl.acquire();
        (new Thread(new Runnable() {

            public final void run() {
            	try {
	                NewMediaTracker.getInstance(context).processNewMedia();
	                EsAccount esaccount = EsAccountsData.getActiveAccount(context);
	                if(esaccount != null) {
	                	EsEventData.validateInstantShare(context, esaccount);
	                	if(InstantUpload.isEnabled(context) || InstantUpload.isInstantShareEnabled(context))
	                		InstantUploadFacade.requestUploadSync(context); 
	                }
            	} finally {
            		wl.release();
            	}
            }

        })).start();
    }
    
    static final class MediaObserver extends ContentObserver
    {

        public final void onChange(boolean flag)
        {
            PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0, CameraMonitor.sIntent, 0);
            ((AlarmManager)context.getSystemService("alarm")).set(0, 12000L + System.currentTimeMillis(), pendingintent);
        }

        private final Context context;

        public MediaObserver(Context context1, Handler handler)
        {
            super(null);
            context = context1;
        }
    }
}
