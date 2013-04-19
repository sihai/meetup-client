/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * 
 * @author sihai
 *
 */
public class PackagesMediaMonitor extends BroadcastReceiver {

	public PackagesMediaMonitor()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        intent.setClass(context, PackagesMediaMonitor.AsyncService.class);
        context.startService(intent);
    }
    
    public static class AsyncService extends IntentService
    {

        protected void onHandleIntent(Intent intent)
        {
            // TODO
        }

        public void onStart(Intent intent, int i)
        {
            if(mServiceLock == null)
                mServiceLock = ((PowerManager)getSystemService("power")).newWakeLock(1, "AsyncService");
            mServiceLock.acquire();
            super.onStart(intent, i);
        }

        private android.os.PowerManager.WakeLock mServiceLock;

        public AsyncService()
        {
            super("GPlusPackageMediaMonitor");
            setIntentRedelivery(true);
        }
    }
}
