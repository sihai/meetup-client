/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * 
 * @author sihai
 *
 */
public class LowStorageMonitor extends BroadcastReceiver {

	public LowStorageMonitor()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        if("android.intent.action.DEVICE_STORAGE_LOW".equals(intent.getAction()))
            (new CleanupTask()).execute(new Context[] {
                context
            });
    }
    
    private static final class CleanupTask extends AsyncTask {

        @Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		CleanupTask()
        {
        }

    }
}
