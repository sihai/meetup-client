/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author sihai
 *
 */
public class BatteryReceiver extends BroadcastReceiver {

	public BatteryReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        intent.setComponent(new ComponentName(context, BatteryReceiver.AsyncService.class));
        context.startService(intent);
    }
    
    public static class AsyncService extends IntentService
    {

        protected void onHandleIntent(Intent intent)
        {
            boolean flag = "android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction());
            InstantUploadSyncManager.getInstance(this).onBatteryStateChanged(flag);
        }

        public AsyncService()
        {
            super("InstantUploadSyncBatteryReceiverAsync");
        }
    }
}
