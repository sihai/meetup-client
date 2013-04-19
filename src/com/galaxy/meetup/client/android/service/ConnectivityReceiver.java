/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.iu.InstantUploadSyncManager;

/**
 * 
 * @author sihai
 *
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	public ConnectivityReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        context.startService(new Intent(context, ConnectivityReceiver.AsyncService.class));
    }
    
	public static class AsyncService extends IntentService
    {

        protected void onHandleIntent(Intent intent)
        {
            ImageResourceManager.getInstance(this).onEnvironmentChanged();
            InstantUploadSyncManager.getInstance(this).onEnvironmentChanged();
        }

        public AsyncService()
        {
            super("InstantUploadSyncConnectivityAsync");
        }
    }

}
